package com.example.sstubot.database.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

import java.io.Serializable;

@Embeddable
public class PrimaryKey implements Serializable
{
    @ManyToOne
    @JoinColumn(name = "user_id")
    protected User user;

    @ManyToOne
    @JoinColumn(name = "direction_id")
    protected Direction direction;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    protected PrimaryKey(){}

    public PrimaryKey(User user, Direction direction) {
        this.user = user;
        this.direction = direction;
    }
}
