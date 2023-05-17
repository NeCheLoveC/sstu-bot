package com.example.sstubot.initial;

import com.example.sstubot.database.model.Claim;
import com.example.sstubot.database.model.Direction;
import com.example.sstubot.database.model.User;
import com.example.sstubot.database.service.DirectionService;
import com.example.sstubot.database.service.InstituteService;
import com.example.sstubot.database.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class LoadEntities {
    protected InstitutesLoad institutesLoad;
    protected DirectionLoad directionLoad;
    protected LoadManager loadManager;
    protected InstituteService instituteService;
    protected DirectionService directionService;
    protected UserService userSerivce;

    @Autowired
    public LoadEntities(@Qualifier("institutesLoad") InstitutesLoad institutesLoad, DirectionLoad directionLoad, LoadManager loadManager, InstituteService instituteService, DirectionService directionService, UserService userSerivce) {
        this.institutesLoad = institutesLoad;
        this.directionLoad = directionLoad;
        this.loadManager = loadManager;
        this.instituteService = instituteService;
        this.directionService = directionService;
        this.userSerivce = userSerivce;
        System.out.println("test");
    }


    public void load()
    {
        //HashMap<String, Institute> instituteHashMap =  institutesLoad.load();
        if(instituteService.countInstance() == 0)
            directionLoad.load();
        Map<String, User> userMap = this.loadManager.loadClaims();
        Collection<User> userCollection = sortClaimsIntoUsers(userMap.values());
        //Зачисление в Direction



        //Map<String, User> users = loadManager.loadClaims(directionList);
        System.out.println("test");
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
    protected void enrollmentUserIntoDirections(Collection<User> users)
    {
        int count = 0;
        List<User> userList = new LinkedList<>(users.stream().filter(x -> x.isOriginalDocuments()).toList());
        Set<Direction> directionSet = directionLoad
        //Нужно получить всех пользователей с оригиналами!
        for(User user : userList)
        {

        }
        Set<Direction> directions = new HashSet<>();


    }

    public boolean tryPushUser(User user)
    {
        for(Claim claim : user.getClaims())
        {

        }
    }
}
