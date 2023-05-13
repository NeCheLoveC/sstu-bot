package com.example.sstubot.database.model;

import jakarta.persistence.*;

public class ClaimPriorities
{
    protected Direction direction;
    protected boolean isBudget;
    protected ClaimPriorities(){}

    public ClaimPriorities(Direction direction, boolean isBudget) {
        this.direction = direction;
        this.isBudget = isBudget;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
