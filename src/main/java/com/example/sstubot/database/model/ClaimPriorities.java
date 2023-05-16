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
    @ManyToOne
    @JoinColumn(name = "user_id")
    protected User user;
    protected boolean isBudget;
    protected ClaimPriorities(){}

    public ClaimPriorities(Direction direction,User user, boolean isBudget) {
        if(direction == null || user == null)
            throw new RuntimeException("Конструктор имеет null аргументы (ClaimPriorities)");
        this.direction = direction;
        this.isBudget = isBudget;
        this.user = user;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
