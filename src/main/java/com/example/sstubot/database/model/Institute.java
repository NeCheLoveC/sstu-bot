package com.example.sstubot.database.model;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table
public class Institute
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;

    @Column(name = "name", unique = true)
    protected String name;

    @OneToMany(mappedBy = "institute")
    protected Set<Direction> directions = new HashSet<>();

    public Institute(){}

    public Institute(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public Set<Direction> getDirections() {
        return directions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean addDirection(Direction direction)
    {
        return directions.add(direction);
    }
}
