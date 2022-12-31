package com.example.sstubot.database.model;

import jakarta.persistence.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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

    @Column(name = "countScore_for_individual_achievements")
    protected int countScoreForIndividualAchievements = 0;

    // TODO: 25.12.2022 ПЕРЕИМЕНОВАТЬ 
    @Column(name = "champion")
    protected boolean champion = false;

    @OneToMany(mappedBy = "claim")
    protected Set<Score> scoreList = new HashSet<>();

    protected Claim(){}

    public Claim(User user, Direction direction) {
        this.id.user = user;
        this.id.direction = direction;

        this.userId = user.id;;
        this.directionId = direction.getId();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDirectionId() {
        return directionId;
    }

    public void setDirectionId(Long directionId) {
        this.directionId = directionId;
    }

    public Set<Score> getScoreList() {
        return scoreList;
    }

    public void setScoreList(Set<Score> scoreList) {
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
}
