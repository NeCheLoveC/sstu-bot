package com.example.sstubot.database.model;

import com.example.sstubot.database.model.urils.ClaimType;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(indexes = {@Index(unique = true, columnList = "unique_code")}, name = "userrrr")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;
    @Column(name = "unique_code", unique = true)
    protected String uniqueCode;
    @Column(name = "original_documents")
    protected boolean originalDocuments = false;
    @OneToMany(mappedBy = "user",cascade = {CascadeType.PERSIST})
    protected List<Claim> claims = new ArrayList();
    @Transient
    protected List<Claim> sortedClaims = new LinkedList<>();

    @OneToMany(cascade = {CascadeType.REMOVE,CascadeType.PERSIST})
    @JoinColumn(name = "user_id")
    @OrderColumn(name = "claim_position", nullable = false)
    protected List<ClaimPriorities> priorities = new LinkedList<>();

    /*
    @JoinTable(
            name = "user_win_claim",
            joinColumns =
            @JoinColumn(name = "user_id"),
            inverseJoinColumns = {
                    @JoinColumn(name = "claim_user_id", referencedColumnName = "user_id"),
                    @JoinColumn(name = "direction_id", referencedColumnName = "direction_id"),
                    @JoinColumn(name = "claim_type", referencedColumnName = "claim_type"),
            }
    )
     */
    // TODO: 31.05.2023 РЕАЛИЗОВАТЬ ссылку на составной первичный ключ

    @Transient
    protected Claim winClaim;


    public User(){};

    public List<Claim> getClaims() {
        return claims;
    }

    public void setClaims(List<Claim> claims) {
        this.claims = claims;
    }

    public List<ClaimPriorities> getPriorities() {
        return priorities;
    }

    public void setPriorities(List<ClaimPriorities> priorities) {
        this.priorities = priorities;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public Long getId() {
        return id;
    }

    public void addPriorities(ClaimPriorities priorities)
    {
        this.priorities.add(priorities);
        priorities.setUser(this);
    }

    public void addClaim(Claim claim)
    {
        this.claims.add(claim);
    }

    public boolean isOriginalDocuments() {
        return originalDocuments;
    }

    public void setOriginalDocuments(boolean originalDocuments) {
        this.originalDocuments = originalDocuments;
    }

    public Claim getWinClaim() {
        return winClaim;
    }

    public void setWinClaim(Claim winClaim) {
        /*
        if(winClaim == null)
        {
            if(this.winClaim != null)
                this.winClaim.setWin(false);
            this.winClaim = null;
        }
        else
        {
            if(!winClaim.getUser().equals(this))
                throw new RuntimeException("Попытка добавления winClaim пользователю, не владеющего данной заявкой");
            if(this.winClaim != null)
                this.winClaim.setWin(false);
            this.winClaim = winClaim;
            this.winClaim.setWin(true);
        }
         */
        if(this.winClaim != null)
        {
            this.winClaim.setWin(false);
            this.winClaim.getDirection().deleteClaim(this.winClaim);
        }
        this.winClaim = winClaim;
        if(this.winClaim != null)
            this.winClaim.setWin(true);
        /*
        if(this.winClaim == null)
        {
            if(winClaim != null)
            {
                winClaim.setWin(true);
            }
            this.winClaim = winClaim;
        }
        else
        {
            this.winClaim.setWin(false);
            winClaim.getDirection().deleteClaim(this.winClaim);
            if(winClaim != null)
            {
                winClaim.setWin(true);
            }
            this.winClaim = winClaim;
        }
         */
    }

    public void sortClaim()
    {
        List<ClaimPriorities> budgetPriorities = getClaimPrioritiesIsBudget();

        List<Claim> firstStage = getClaimsForPriorityStage(budgetPriorities);
        List<Claim> secondStage = getClaimsForSecondStage(budgetPriorities);
        firstStage.addAll(secondStage);
        List<Claim> actualClaimsOrder = firstStage;

        this.sortedClaims = actualClaimsOrder;

    }
    private List<Claim> getClaimsForSecondStage(List<ClaimPriorities> newClaimPriorities)
    {
        List<Claim> claimList = new LinkedList<>();

        for(ClaimPriorities cp : newClaimPriorities)
        {
            for(Claim c : claims)
            {
                if(similar(cp,c) && c.getClaimType().equals(ClaimType.BUDGET_GENERAL_LIST) && !c.isChampion())
                    claimList.add(c);
            }
        }
        return claimList;
    }

    private List<Claim> getClaimsForPriorityStage(List<ClaimPriorities> newPriority)
    {
        List<Claim> claimsIntoPriorityStage = new LinkedList<>();
        //Целевые
        for(ClaimPriorities cp : newPriority)
        {
            for(Claim c : claims)
            {
                if(similar(cp,c) && c.claimType.equals(ClaimType.BUDGET_TARGET_QUOTA))
                {
                    claimsIntoPriorityStage.add(c);
                }
            }
        }

        //Общий конкурс ОЛИМПИАДНИК(БВИ)
        for(ClaimPriorities cp : newPriority)
        {
            for(Claim c : claims)
            {
                if(similar(cp,c) && c.claimType.equals(ClaimType.BUDGET_GENERAL_LIST) && c.isChampion())
                {
                    claimsIntoPriorityStage.add(c);
                }
            }
        }

        //ОТДЕЛЬНАЯ КВОТА ("СПЕЦИАЛЬНАЯ")
        for(ClaimPriorities cp : newPriority)
        {
            for(Claim c : claims)
            {
                if(similar(cp,c) && c.claimType.equals(ClaimType.BUDGET_SPECIAL_QUOTA))
                {
                    claimsIntoPriorityStage.add(c);
                }
            }
        }

        //ОСОБАЯ КВОТА
        for(ClaimPriorities cp : newPriority)
        {
            for(Claim c : claims)
            {
                if(similar(cp,c) && c.claimType.equals(ClaimType.BUDGET_UNUSUAL_QUOTA))
                {
                    claimsIntoPriorityStage.add(c);
                }
            }
        }

        return claimsIntoPriorityStage;
    }


    private List<ClaimPriorities> getClaimPrioritiesIsBudget()
    {
        List<ClaimPriorities> claimPriorities = new LinkedList<>();

        for(ClaimPriorities cp : priorities)
        {
            if(cp.isBudget)
                claimPriorities.add(cp);
        }
        return claimPriorities;
    }


    private static boolean similar(ClaimPriorities prior, Claim claim)
    {
        if(!prior.direction.equals(claim.direction))
            return false;
        if(!(prior.isBudget == claim.isBudget()))
            return false;
        return true;
    }



    public List<Claim> getSortedClaims() {
        return sortedClaims;
    }
    /*
    public void addClaim(Collection<Claim> claim)
    {
        for(Claim obj : claim)
            this.claims.add(obj);
    }
     */
}
