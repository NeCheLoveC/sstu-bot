package com.example.sstubot.database.model.urils;

import com.example.sstubot.database.model.Claim;
import com.example.sstubot.database.model.User;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class GeneralListContainer implements ClaimContainer
{
    private ClaimType claimType = ClaimType.BUDGET_GENERAL_LIST;
    private List<Claim> claims = new LinkedList<>();
    private GetReservedQuots reservedQuots;
    private int minScoreForAdd = 0;
    private int maxSize = 0;
    public GeneralListContainer(GetReservedQuots reservedQuots, int maxSize)
    {
        this.reservedQuots = reservedQuots;
        this.maxSize = maxSize;
    }

    @Override
    public boolean canAddClaim(Claim claim)
    {
        // TODO: 04.06.2023 ВОТ тут нужно исправить
        if(!claim.getClaimType().equals(claimType))
            throw new RuntimeException("Данный контейнер не подходит для данного типы заявки");
        if(getRealCurrentMaxSize() == 0)
            return false;
        if(getRealCurrentMaxSize() > claims.size())
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

    @Override
    public Claim addClaimIntoContainer(Claim claim) {
        return addClaim(claim);
    }

    private Claim addClaim(Claim claim)
    {
        /*
        1 - При успешном добавлени пользователь есть несколько вариантов:
            - пользователь добавлен т.к есть СВОБОДНЫЕ места, никого не вытеснили -> метод вернет null
            - свободных мест нет, НО у пользователя больше баллов чем у последнего в списке -> метод вернет последнего в спике т.к. он будет вытолкнут из этой очереди
            - если нет заявок - пользователь добавляется в очередь - return null
         */
        Claim removedClaim = null;
        if((getRealCurrentMaxSize() > currentSize()) && (currentSize() == 0))
        {
            claims.add(claim);
            User user = claim.getUser();
            user.setWinClaim(claim,claims.indexOf(claim));
        }
        else if(getRealCurrentMaxSize() > currentSize())
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
            if(!isAdded)
            {
                claims.add(claim);
            }
            else
            {
                claims.add(i,claim);
            }
            User user = claim.getUser();
            user.setWinClaim(claim,claims.indexOf(claim));
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
            user.setWinClaim(claim,claims.indexOf(claim));
            claims.remove(removedClaim);
            removedClaim.getUser().setWinClaim(null,0);
        }
        refreshMinScore();
        return removedClaim;
    }

    private int currentSize()
    {
        return claims.size();
    }

    private void refreshMinScore()
    {
        if(!claims.isEmpty())
        {
            this.minScoreForAdd = this.claims.get(this.claims.size() - 1).getSummaryOfScore();
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

    public void refreshClaims()
    {
        while(getRealCurrentMaxSize() < currentSize())
        {
            //Нужно исключить все "лишние" заявки
            if(!claims.isEmpty())
            {
                claims.get(claims.size() - 1).getUser().setWinClaim(null,0);
                claims.remove(claims.size() - 1);
            }
        }
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

    private int getRealCurrentMaxSize()
    {
        return (maxSize - this.reservedQuots.getReserved());
    }

    public void initWinClaimPosition()
    {
        Iterator<Claim> iter = claims.iterator();
        for(int i = 0; iter.hasNext();i++)
        {
            iter.next().setPositionIntoWinList(i);
        }
    }

    public int getMinScoreForAdd() {
        return minScoreForAdd;
    }
}
