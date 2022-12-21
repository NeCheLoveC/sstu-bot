package com.example.sstubot.initial;

import com.example.sstubot.database.model.Institute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Component
public class LoadEntities {
    InstitutesLoad institutesLoad;
    DirectionLoad directionLoad;

    @Autowired
    public LoadEntities(InstitutesLoad institutesLoad, DirectionLoad directionLoad) {
        this.institutesLoad = institutesLoad;
        this.directionLoad = directionLoad;
    }

    @Transactional
    public void load()
    {
        HashMap<String, Institute> instituteHashMap =  institutesLoad.load();

        directionLoad.load(instituteHashMap);
    }
}
