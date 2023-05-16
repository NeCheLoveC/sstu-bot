package com.example.sstubot.database.service;

import com.example.sstubot.database.model.Direction;
import com.example.sstubot.database.model.User;
import com.example.sstubot.database.repositories.DirectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DirectionService
{
    protected DirectionRepository directionRepository;

    @Autowired
    public DirectionService(DirectionRepository directionRepository) {
        this.directionRepository = directionRepository;
    }

    public Direction save(Direction direction)
    {
        return directionRepository.save(direction);
    }

    public Direction getDirectionByBudgetUrl(String urlBudget)
    {
        return directionRepository.getDirectionByUrlToListOfClaims(urlBudget);
    }
    public Direction getDirectionByCommerceUrl(String url)
    {
        return directionRepository.getDirectionByUrlToListOfClaimsCommerce(url);
    }
    public List<Direction> getAllDirection()
    {
        return this.directionRepository.findAll();
    }
    /*
    public List<Direction> getAllDirectionWithClaims()
    {
        return this.directionRepository.getDirWithClaims();
    }
     */
}
