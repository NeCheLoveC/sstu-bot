package com.example.sstubot.database.model;

import jakarta.persistence.*;

@Entity
@Table
public class Direction
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;

    @Column(name = "name")
    protected String name;

    @ManyToOne
    @JoinColumn(name = "institute_id")
    protected Institute institute;

    public Direction(){}

    public Direction(String name, Institute institute)
    {
        this.name = name;
        this.institute = institute;
    }
}
