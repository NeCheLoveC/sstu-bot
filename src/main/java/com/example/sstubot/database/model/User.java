package com.example.sstubot.database.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(indexes = {@Index(unique = true, columnList = "unique_code")}, name = "'user'")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;
    @Column(name = "unique_code")
    protected String uniqueCode;
    @Column(name = "original_documents")
    protected boolean originalDocuments = false;
    @OneToMany(mappedBy = "user")
    protected ArrayList<Claim> claims = new ArrayList();
    @Transient
    protected List<ClaimPriorities> priorities = new LinkedList<>();

    public User(){};

    public ArrayList<Claim> getClaims() {
        return claims;
    }

    public void setClaims(ArrayList<Claim> claims) {
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
    /*
    public void addClaim(Collection<Claim> claim)
    {
        for(Claim obj : claim)
            this.claims.add(obj);
    }
     */
}
