package com.example.sstubot.database.model.urils;

import com.example.sstubot.database.model.Claim;
import jakarta.annotation.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class ContainerCommerce
{
    protected List<Claim> claims = new LinkedList<>();

    protected ClaimType claimType;
    //protected int minScoreForAdd = 0;
    public ContainerCommerce()
    {
        this.claimType = ClaimType.COMMERCE_GENERAL_LIST;
    }




    //null - если при добавлении заявки не было вытеснено другого абитуриента
    @Nullable
    public Claim addClaimIntoContainer(Claim claim)
    {
        if(!claim.getClaimType().equals(claimType))
            throw new RuntimeException("Данный контейнер не подходит для данного типы заявки");
        if(canAddClaim(claim))
        {
            return addClaim(claim);
        }
        else
        {
            return null;
        }
    }

    //Перед вставкой обязательно проверка на canAddClaim(Claim)
    private Claim addClaim(Claim claim)
    {
        Claim removedClaim = null;
        ListIterator<Claim> iterator = claims.listIterator();
        int i = 0;
        while (iterator.hasNext())
        {
            Claim c = iterator.next();
            int compareResult = claim.compareTo(c);
            if(compareResult > 0)
            {
                removedClaim = c;
                break;
            }
            i++;
        }
        claims.add(i,claim);
        claim.getUser().setWinClaim(claim,claims.indexOf(claim));
        //this.minScoreForAdd = claims.get(claims.size() - 1).getSummaryOfScore();
        return removedClaim;
    }
    private boolean canAddClaim(Claim claim)
    {
        return true;
    }


    public void removeClaimFromList(Claim claim) {
        if(claim == null)
            throw new NullPointerException("Claim не может быть равен null");
        if(!claim.getClaimType().equals(claimType))
            throw new RuntimeException("Данной заявки нет в списке");
        claims.remove(claim);
    }
}
