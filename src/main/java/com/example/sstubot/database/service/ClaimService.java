package com.example.sstubot.database.service;

import com.example.sstubot.database.model.Claim;
import com.example.sstubot.database.repositories.ClaimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClaimService {
    protected ClaimRepository claimRepository;

    @Autowired
    public ClaimService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    public Claim save(Claim claim)
    {
        return claimRepository.save(claim);
    }
}
