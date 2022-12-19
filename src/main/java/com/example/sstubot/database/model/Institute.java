package com.example.sstubot.database.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table
public class Institute
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;

    @Column(name = "name")
    protected String name;

    @OneToMany(mappedBy = "institute", cascade = CascadeType.PERSIST)
    protected Collection<Direction> directions = new ArrayList<>();

    public Institute(){}

    public Long getId() {
        return id;
    }

    public Collection<Direction> getDirections() {
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
