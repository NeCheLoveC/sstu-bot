package com.example.sstubot.database.model;

import jakarta.persistence.*;

@Entity
@Table(indexes = {@Index(unique = true, columnList = "")}, name = "'user'")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;

    @Column(name = "unique_code")
    protected String uniqueCode;

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
}
