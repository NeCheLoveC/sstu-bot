package com.example.sstubot.initial;

import com.example.sstubot.database.model.Claim;
import com.example.sstubot.database.model.Direction;
import com.example.sstubot.database.model.User;
import com.example.sstubot.database.model.urils.ClaimType;
import com.example.sstubot.database.model.urils.ContainerQuotaClaims;
import com.example.sstubot.database.service.ClaimService;
import com.example.sstubot.database.service.DirectionService;
import com.example.sstubot.database.service.InstituteService;
import com.example.sstubot.database.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Component
public class LoadEntities {
    protected DirectionLoad directionLoad;
    protected LoadManager loadManager;
    protected InstituteService instituteService;
    protected DirectionService directionService;
    protected UserService userSerivce;
    @Autowired
    protected ClaimService claimService;

    @Autowired
    public LoadEntities(DirectionLoad directionLoad, LoadManager loadManager, InstituteService instituteService, DirectionService directionService, UserService userSerivce) {
        this.directionLoad = directionLoad;
        this.loadManager = loadManager;
        this.instituteService = instituteService;
        this.directionService = directionService;
        this.userSerivce = userSerivce;
    }

    @Transactional
    public void load() throws IOException
    {
        if(instituteService.countInstance() == 0)
            directionLoad.load(); // Загрузка институтов с направлениями
        List<Direction> directionList = directionService.getAllDirection();
        Map<String, Direction> directionHashMap = getMapDirectionsFromList(directionList);
        Map<String, User> userMap = this.loadManager.loadClaims(directionHashMap,directionList);
        Collection<User> userCollection = sortClaimsIntoUsers(userMap.values());
        List<Direction> directions = directionService.getAllDirection();

        for(Direction d : directions)
        {
            d.initContainers();
        }

        enrollmentUserIntoDirectionsFirstStage(userCollection);
        enrollmentUserIntoDirectionsSecondStage(userCollection);
        enrollmentUserWithoutOriginalDoc(userCollection);

        for(Direction d : directions)
        {
            d.initWinClaimPositionAndMinSocre();
        }

        for(User u : userCollection)
        {
            userSerivce.save(u);
        }
    }

    private void enrollmentUserWithoutOriginalDoc(Collection<User> users)
    {
        users = users.stream().filter((user) -> !user.isOriginalDocuments()).toList();

        for(User user : users)
        {
            for(Claim claim : user.getSortedClaims())
            {
                claim.enrollWithoutOriginalDoc();
            }
        }
    }


    private Map<String, Direction> getMapDirectionsFromList(List<Direction> directions)
    {
        Map<String, Direction> directionMap = new HashMap<>();
        for(Direction d : directions)
        {
            directionMap.put(d.getUrlToListOfClaims(),d);
            directionMap.put(d.getUrlToListOfClaimsCommerce(),d);
        }
        return directionMap;
    }
    @Transactional
    protected void clearLastTry(UserService userService)
    {
        userService.clearAll();
    }

    @Transactional
    protected Collection<User> sortClaimsIntoUsers(Collection<User> users)
    {
        for(User user : users)
        {
            user.sortClaim();
        }
        return users;
    }
    @Transactional
    protected void enrollUser(Collection<User> users)
    {
        enrollmentUserIntoDirectionsFirstStage(users);
    }
    protected void enrollmentUserIntoDirectionsFirstStage(Collection<User> users)
    {

        Collection<User> allUsers = users;
        //List<User> userListWithOriginal = new LinkedList<>(users.stream().filter(x -> x.isOriginalDocuments()).toList());
        //Set<Direction> directionSet = directionLoad
        //Нужно получить всех пользователей с оригиналами!
        while(true)
        {
            // TODO: 18.05.2023 Нужно проходиться по юзерам и обновлять список
            ListIterator<User> iter = users.stream().toList().listIterator();
            int count = 0;
            while(iter.hasNext())
            {
                User user = iter.next();
                if(!user.isOriginalDocuments())
                    continue;
                Claim currentWinClaimUser = user.getWinClaim();
                for(Claim claim : user.getSortedClaims())
                {
                    if(claim.equals(currentWinClaimUser))
                        break;
                    //Только квоты на этом плане + БВИ
                    if(!validateClaimTypeForFirstStage(claim))
                    {
                        continue;
                    }
                    //Claim delClaim = null;
                    boolean canAdd = false;
                    Direction direction = claim.getDirection();
                    switch (claim.getClaimType())
                    {
                        case BUDGET_SPECIAL_QUOTA:
                            if(canAdd = direction.canAddIntoSpecial(claim))
                                claim = direction.addClaimIntoList(claim);
                            break;
                        case BUDGET_TARGET_QUOTA:
                            if(canAdd = direction.canAddIntoTarget(claim))
                                claim = direction.addClaimIntoList(claim);
                            break;
                        case BUDGET_UNUSUAL_QUOTA:
                            if(canAdd = direction.canAddIntoUnusual(claim))
                                claim = direction.addClaimIntoList(claim);
                            break;
                        case BUDGET_GENERAL_LIST:
                            if(canAdd = direction.canAddIntoGeneralList(claim))
                                claim = direction.addClaimIntoList(claim);
                            break;
                        default:
                            throw new RuntimeException("Не распознан тип заявки... (INTO switch/case");
                    }
                    if(canAdd)
                    {
                        direction.refreshGeneralList();
                        count++;
                    }

                }
            }
            if(count == 0)
            {
                System.out.println("Юзеров было добавлено : 0");
                break;
            }
            else
            {
                System.out.println("Юзеров было добавлено : " + count);
                count = 0;
            }
        }
        //Set<Direction> directions = new HashSet<>();
    }

    protected void enrollmentUserIntoDirectionsSecondStage(Collection<User> users)
    {
        int count = 0;
        Collection<User> allUsers = users;
        List<User> userListWithOriginal = new LinkedList<>(users.stream().filter(x -> x.getWinClaim() == null).toList());
        //Set<Direction> directionSet = directionLoad
        //Нужно получить всех пользователей с оригиналами!
        while(true)
        {
            // TODO: 18.05.2023 Нужно проходиться по юзерам и обновлять список
            ListIterator<User> iter = userListWithOriginal.listIterator();
            while(iter.hasNext())
            {
                User user = iter.next();
                if(!user.isOriginalDocuments())
                    continue;
                Claim currentWinClaimUser = user.getWinClaim();
                for(Claim claim : user.getSortedClaims())
                {
                    if(claim.equals(currentWinClaimUser))
                        break;
                    //Только квоты на этом плане + БВИ
                    if(!validateClaimTypeForSecondStage(claim))
                    {
                        continue;
                    }
                    Claim delClaim = null;
                    boolean canAdd = false;
                    Direction direction = claim.getDirection();
                    //if(direction.)
                    switch (claim.getClaimType())
                    {
                        case BUDGET_GENERAL_LIST:
                            if(canAdd = direction.canAddIntoGeneralList(claim))
                                claim = direction.addClaimIntoList(claim);
                            break;
                            /*
                        case COMMERCE_GENERAL_LIST:
                            if(canAdd = direction.canAddIntoCommerce(claim))
                                claim = direction.addClaimIntoList(claim);
                            break;
                             */
                        default:
                            throw new RuntimeException("Не распознан тип заявки... (INTO switch/case");
                    }
                    if(canAdd)
                        count++;
                    /*
                    if(claim != null)
                        iter.add(claim.getUser());
                     */
                }
            }
            if(count == 0)
            {
                System.out.println("Юзеров было доавлено : 0");
                break;
            }
            else
            {
                System.out.println("Юзеров было доавлено : " + count);
                count = 0;
            }
        }
        //Set<Direction> directions = new HashSet<>();
    }

    private boolean validateClaimTypeForFirstStage(Claim claim)
    {
        if(claim.isAbsence())
            return false;
        if(claim.getClaimType() == ClaimType.COMMERCE_GENERAL_LIST)
            return false;
        if(claim.getClaimType() == ClaimType.BUDGET_GENERAL_LIST && claim.isChampion())
            return true;
        else if(claim.getClaimType() == ClaimType.BUDGET_GENERAL_LIST && !claim.isChampion())
            return false;
        return true;
    }

    private boolean validateClaimTypeForSecondStage(Claim claim)
    {
        if(claim.isAbsence())
            return false;
        if(claim.getClaimType() == ClaimType.BUDGET_GENERAL_LIST && !claim.isChampion())
            return true;
        return false;
    }
}
