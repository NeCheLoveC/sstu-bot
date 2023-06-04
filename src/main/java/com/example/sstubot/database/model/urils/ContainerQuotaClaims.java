package com.example.sstubot.database.model.urils;

import com.example.sstubot.database.model.Claim;
import com.example.sstubot.database.model.User;
import jakarta.annotation.Nullable;

import java.util.*;

public class ContainerQuotaClaims implements ClaimContainer
{
    protected List<Claim> claims = new LinkedList<>();
    protected int  maxSize;
    //protected int currentSize;
    protected ClaimType claimType;
    protected int minScoreForAdd = 0;
    public ContainerQuotaClaims(int maxSize, ClaimType claimType)
    {
        if(claimType == ClaimType.BUDGET_GENERAL_LIST || claimType == ClaimType.COMMERCE_GENERAL_LIST)
            throw new RuntimeException("Нельзя добавить в этот контейнер списков...");
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
        /*
        1 - При успешном добавлени пользователь есть несколько вариантов:
            - пользователь добавлен т.к есть СВОБОДНЫЕ места, никого не вытеснили -> метод вернет null
            - свободных мест нет, НО у пользователя больше баллов чем у последнего в списке -> метод вернет последнего в спике т.к. он будет вытолкнут из этой очереди
            - если нет заявок - пользователь добавляется в очередь - return null
         */
        Claim removedClaim = null;
        if((maxSize > currentSize()) && (currentSize() == 0))
        {
            claims.add(claim);
            User user = claim.getUser();
            user.setWinClaim(claim);
        }
        else if(maxSize > currentSize())
        {
            ListIterator<Claim> iterator = claims.listIterator();
            int i = 0;
            Claim c;
            boolean isAdded = false;
            while (iterator.hasNext())
            {
                c = iterator.next();
                int compareResult = claim.compareTo(c);
                if(compareResult > 0)
                {
                    isAdded = true;
                    break;
                }
                i++;
            }
            //Добавить в конец
            if(!isAdded)
            {
                claims.add(claim);
            }
            else
            {
                claims.add(i,claim);
            }
            User user = claim.getUser();
            user.setWinClaim(claim);
        }
        else
        {
            ListIterator<Claim> iterator = claims.listIterator();
            int i = 0;
            Claim c;
            while (iterator.hasNext())
            {
                c = iterator.next();
                int compareResult = claim.compareTo(c);
                if(compareResult > 0)
                {
                    removedClaim = getLastElementIfExist();
                    break;
                }
                i++;
            }
            claims.add(i,claim);
            User user = claim.getUser();
            user.setWinClaim(claim);
            claims.remove(removedClaim);
            removedClaim.getUser().setWinClaim(null);
        }
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

    public int currentSize()
    {
        return claims.size();
    }

    @Override
    public void removeClaimFromList(Claim claim) {
        if(claim == null)
            throw new NullPointerException("Claim не может быть равен null");
        if(!claim.getClaimType().equals(claimType))
            throw new RuntimeException("Данной заявки нет в списке");
        claims.remove(claim);
        refreshMinScore();
    }
}
