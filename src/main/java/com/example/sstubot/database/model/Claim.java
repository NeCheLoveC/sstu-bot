package com.example.sstubot.database.model;

import jakarta.persistence.*;

@Entity
@Table
public class Claim
{
    @Id
    @EmbeddedId
    protected PrimaryKey id = new PrimaryKey();

    @Column(name = "user_id", insertable = false, updatable = false)
    protected Long userId;

    @Column(name = "direction_id", insertable = false, updatable = false)
    protected Long directionId;

    protected Claim(){}

    public Claim(User user, Direction direction) {
        this.id.user = user;
        this.id.direction = direction;

        this.userId = user.id;;
        this.directionId = direction.getId();
    }
}
