package com.example.sstubot.database.service;

import com.example.sstubot.database.model.Score;
import com.example.sstubot.database.repositories.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ScoreService
{
    protected ScoreRepository scoreRepository;

    @Autowired
    public ScoreService(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    public Score save(Score score)
    {
        return scoreRepository.save(score);
    }
}
