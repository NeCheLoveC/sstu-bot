package com.example.sstubot.database.model.urils;

import com.example.sstubot.database.model.Claim;
import jakarta.annotation.Nullable;

import java.util.*;

public class ContainerClaims
{
    protected List<Claim> claims = new LinkedList<>();
    protected int  maxSize;
    //protected int currentSize;
    protected ClaimType claimType;
    protected int minScoreForAdd = 0;
    public ContainerClaims(int maxSize, ClaimType claimType)
    {
        this.maxSize = maxSize;
        this.claimType = claimType;
    }

    //true - если добавить в список можно
    //false - если добавить нельзя т.к не хватает проходных балов или мест
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
        claim.getUser().setWinClaim(claim);
        refreshMinScore();
        return removedClaim;
    }

    private void refreshMinScore()
    {
        if(!claims.isEmpty())
        {
            this.minScoreForAdd = this.claims.get(this.claims.size() - 1).getSummaryOfScore();
        }
    }

    public boolean canAddClaim(Claim claim)
    {
        if(!claim.getClaimType().equals(claimType))
            throw new RuntimeException("Данный контейнер не подходит для данного типы заявки");
        if(maxSize == 0)
            return false;
        if(maxSize > claims.size())
            return true;
        //Если самый "слабый" элемент меньше чем текущий
        Claim lastElement = getLastElementIfExist();
        int resultOfCompared = claim.compareTo(lastElement);
        if(resultOfCompared > 0)
            return true;
        else if(resultOfCompared < 0)
            return false;
        else
        {
            //Не очень корректно
            System.out.println("У пользоватлей равенство баллов " + claim.getUser().getUniqueCode() + " и " + lastElement.getUser().getUniqueCode());
            return false;
        }

    }

    private Claim getLastElementIfExist()
    {
        if(!claims.isEmpty())
        {
            return claims.get(claims.size() - 1);
        }
        return null;
    }
}
