package com.example.sstubot.initial;

import com.example.sstubot.database.model.Direction;
import com.example.sstubot.database.model.Institute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Component
public class LoadEntities {
    InstitutesLoad institutesLoad;
    DirectionLoad directionLoad;
    UserLoad userLoad;

    @Autowired
    public LoadEntities(InstitutesLoad institutesLoad, DirectionLoad directionLoad, UserLoad userLoad) {
        this.institutesLoad = institutesLoad;
        this.directionLoad = directionLoad;
        this.userLoad = userLoad;
    }

    @Transactional
    public void load()
    {
        HashMap<String, Institute> instituteHashMap =  institutesLoad.load();
        List<Direction> directionList = directionLoad.load(instituteHashMap);
        for(Direction direction : directionList)
        {
            userLoad.loadBudget(direction.getUrlToListOfClaims(),direction);
        }
    }
}
