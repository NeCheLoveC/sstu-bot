package com.example.sstubot.database.model;

import jakarta.persistence.*;

@Entity
@Table
public class ClaimPriorities
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;
    @ManyToOne
    @JoinColumn(name = "direction_id")
    protected Direction direction;
    @Column(name = "is_budget")
    protected boolean isBudget;
    protected ClaimPriorities(){}

    public ClaimPriorities(Direction direction, boolean isBudget) {
        this.direction = direction;
        this.isBudget = isBudget;
    }

    public Long getId() {
        return id;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
