package com.example.sstubot.database.model;

import com.example.sstubot.database.model.urils.ClaimType;
import jakarta.persistence.*;
import org.hibernate.Hibernate;

import java.util.*;

@Entity
@Table(name = "Claim")
public class Claim {
    @Id
    @EmbeddedId
    protected PrimaryKey id = new PrimaryKey();
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    protected User user;
    @ManyToOne
    @JoinColumn(name = "direction_id", insertable = false, updatable = false)
    protected Direction direction;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "claim_type", insertable = false, updatable = false)
    protected ClaimType claimType;
    @Column(name = "countScore_for_individual_achievements")
    protected int countScoreForIndividualAchievements = 0;
    // TODO: 25.12.2022 ПЕРЕИМЕНОВАТЬ 
    @Column(name = "champion")
    protected boolean champion = false;

    @OneToMany(mappedBy = "claim")
    protected List<Score> scoreList = new LinkedList<>();

    protected Claim(){}

    public Claim(User user, Direction direction, ClaimType claimType) {
        this.id.userId = user.getId();
        this.id.directionId = direction.getId();
        this.id.claimType = claimType;

        this.user = user;;
        this.direction = direction;
        this.claimType = claimType;
    }

    public User getUser() {
        return user;
    }

    public Direction getDirection() {
        return direction;
    }

    public List<Score> getScoreList() {
        return scoreList;
    }

    public void setScoreList(List<Score> scoreList) {
        this.scoreList = scoreList;
    }

    public PrimaryKey getId() {
        return id;
    }

    public void addScore(Score score)
    {
        this.scoreList.add(score);
    }

    public void addScore(Collection<Score> score)
    {
        this.scoreList.addAll(score);
    }

    public int getCountScoreForIndividualAchievements() {
        return countScoreForIndividualAchievements;
    }

    public void setCountScoreForIndividualAchievements(int countScoreForIndividualAchievements) {
        this.countScoreForIndividualAchievements = countScoreForIndividualAchievements;
    }

    public boolean isChampion() {
        return champion;
    }

    public void setChampion(boolean champion) {
        this.champion = champion;
    }

    public ClaimType getClaimType() {
        return claimType;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setClaimType(ClaimType claimType) {
        this.claimType = claimType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Claim claim = (Claim) o;
        return getId() != null && Objects.equals(getId(), claim.getId())
                && getId() != null && Objects.equals(getId(), claim.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean isBudget()
    {
        return claimType == ClaimType.COMMERCE_GENERAL_LIST ? false : true;
    }
}
