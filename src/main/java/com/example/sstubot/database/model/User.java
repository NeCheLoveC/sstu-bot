package com.example.sstubot.database.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(indexes = {@Index(unique = true, columnList = "")}, name = "'user'")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;

    @Column(name = "unique_code")
    protected String uniqueCode;

    @OneToMany
    protected Collection<Claim> claims = new ArrayList();

    public User(){};

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public Long getId() {
        return id;
    }

    public void addClaim(Claim claim)
    {
        this.claims.add(claim);
    }

    public void addClaim(Collection<Claim> claim)
    {
        for(Claim obj : claim)
            this.claims.add(obj);
    }
}
