package com.example.sstubot.database.model;

import com.example.sstubot.database.model.urils.ClaimType;
import jakarta.persistence.*;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PrimaryKey implements Serializable
{
    @Column(name = "user_ida")
    protected Long userId;
    @Column(name = "direction_ida")
    protected Long directionId;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "claim_type")
    public ClaimType claimType;
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDirectionId() {
        return directionId;
    }

    public void setDirection(Long directionId) {
        this.directionId = directionId;
    }
    protected PrimaryKey(){}

    public PrimaryKey(Long userId, Long directionId, ClaimType claimType) {
        this.userId = userId;
        this.directionId = directionId;
        this.claimType = claimType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PrimaryKey that = (PrimaryKey) o;
        return getUserId() != null && Objects.equals(getUserId(), that.getUserId())
                && getDirectionId() != null && Objects.equals(getDirectionId(), that.getDirectionId())
                && claimType != null && Objects.equals(claimType, that.claimType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, directionId, claimType);
    }
}
