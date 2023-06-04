package com.example.sstubot.database.model.urils;

import com.example.sstubot.database.model.Claim;

public interface ClaimContainer
{
    public boolean canAddClaim(Claim claim);
    public Claim addClaimIntoContainer(Claim claim);

    public void removeClaimFromList(Claim claim);
}
