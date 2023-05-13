package com.example.sstubot.initial;

import com.example.sstubot.database.model.Direction;
import com.example.sstubot.database.model.Institute;
import com.example.sstubot.database.model.User;
import com.example.sstubot.database.service.InstituteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LoadEntities {
    protected InstitutesLoad institutesLoad;
    protected DirectionLoad directionLoad;
    protected LoadManager loadManager;
    protected InstituteService instituteService;

    @Autowired
    public LoadEntities(@Qualifier("institutesLoad") InstitutesLoad institutesLoad, DirectionLoad directionLoad, LoadManager loadManager, InstituteService instituteService) {
        this.institutesLoad = institutesLoad;
        this.directionLoad = directionLoad;
        this.loadManager = loadManager;
        this.instituteService = instituteService;
        System.out.println("test");
    }


    public void load()
    {
        //HashMap<String, Institute> instituteHashMap =  institutesLoad.load();
        if(instituteService.countInstance() == 0)
            directionLoad.load();

        //Map<String, User> users = loadManager.loadClaims(directionList);
        System.out.println("test");
    }
}
