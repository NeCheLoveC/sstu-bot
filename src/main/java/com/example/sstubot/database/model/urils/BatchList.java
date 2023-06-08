package com.example.sstubot.database.model.urils;

import com.example.sstubot.database.model.Claim;
import com.example.sstubot.database.model.Direction;
import com.example.sstubot.database.model.User;
import jakarta.persistence.Transient;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class BatchList
{
    private GeneralListContainer budgetGeneralListClaims;

    private ContainerQuotaClaims budgetSpecialQuotaClaims; //= new ContainerQuotaClaims(amountSpecialQuota, ClaimType.BUDGET_SPECIAL_QUOTA);

    private ContainerQuotaClaims budgetTargetQuotaClaims;// = new ContainerQuotaClaims(amountTargetQuota, ClaimType.BUDGET_TARGET_QUOTA);

    private ContainerQuotaClaims budgetUnusualQuotaClaims;// = new ContainerQuotaClaims(amountUnusualQuota, ClaimType.BUDGET_UNUSUAL_QUOTA);

    private ContainerCommerce commerceGeneralListClaims;

    public BatchList(int amountUnusualQuota,int amountSpecialQuota,int amountTargetQuota,int amountMainBudgetIntoPlan)
    {
        this.budgetUnusualQuotaClaims = new ContainerQuotaClaims(amountUnusualQuota, ClaimType.BUDGET_UNUSUAL_QUOTA);
        this.budgetSpecialQuotaClaims = new ContainerQuotaClaims(amountSpecialQuota, ClaimType.BUDGET_SPECIAL_QUOTA);
        this.budgetTargetQuotaClaims = new ContainerQuotaClaims(amountTargetQuota, ClaimType.BUDGET_TARGET_QUOTA);
        this.commerceGeneralListClaims = new ContainerCommerce();
        this.budgetGeneralListClaims = new GeneralListContainer(() -> {return this.budgetTargetQuotaClaims.currentSize() + this.budgetSpecialQuotaClaims.currentSize() + this.budgetUnusualQuotaClaims.currentSize();}, amountMainBudgetIntoPlan);
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
