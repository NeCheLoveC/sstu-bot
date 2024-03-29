package com.example.sstubot.database.model;

import com.example.sstubot.database.model.urils.ClaimType;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(indexes = {@Index(unique = true, columnList = "unique_code")}, name = "\"user\"")
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
    @OrderColumn(name = "claim_position")
    protected List<ClaimPriorities> priorities = new LinkedList<>();

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "win_claim_user_id", referencedColumnName = "user_id"),
            @JoinColumn(name = "win_claim_department_id", referencedColumnName = "direction_id"),
            @JoinColumn(name = "win_claim_type", referencedColumnName = "claim_type")
    })
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
    private void deleteWinClaim()
    {
        if(this.winClaim != null)
        {
            this.winClaim.setWin(false);
            this.winClaim.positionIntoWinList = -1;
            this.winClaim.getDirection().deleteClaim(this.winClaim);
        }
        this.winClaim = null;
    }

    public void setWinClaim(Claim winClaim, int positionId) {
        deleteWinClaim();
        this.winClaim = winClaim;
        if(winClaim != null)
        {
            if(winClaim.isWin)
                throw new RuntimeException("Данное заявление уже выигрышное...");
            this.winClaim.setWin(true);
        }
    }

    public void sortClaim()
    {
        List<ClaimPriorities> budgetPriorities = getClaimPrioritiesIsBudget();

        List<Claim> firstStage = getClaimsForPriorityStage(budgetPriorities);
        List<Claim> secondStage = getClaimsForSecondStage(budgetPriorities);
        firstStage.addAll(secondStage);
        List<Claim> actualClaimsOrder = firstStage;

        this.sortedClaims = actualClaimsOrder;

        for(int i = 0;i < sortedClaims.size();i++)
        {
            claims.get(i).setPriorityIntoUserList(i);
        }
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
                if(similar(cp,c) && c.id.claimType.equals(ClaimType.BUDGET_TARGET_QUOTA))
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
                if(similar(cp,c) && c.id.claimType.equals(ClaimType.BUDGET_GENERAL_LIST) && c.isChampion())
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
                if(similar(cp,c) && c.id.claimType.equals(ClaimType.BUDGET_SPECIAL_QUOTA))
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
                if(similar(cp,c) && c.id.claimType.equals(ClaimType.BUDGET_UNUSUAL_QUOTA))
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
}
