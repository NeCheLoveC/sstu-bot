package com.example.sstubot.database.model;

import com.example.sstubot.database.model.urils.ClaimType;
import jakarta.persistence.*;
import org.hibernate.Hibernate;

import java.util.*;

@Entity
@Table(name = "claim")
public class Claim implements Comparable<Claim>
{
    @EmbeddedId
    protected PrimaryKey id = new PrimaryKey();
    @ManyToOne
    @JoinColumn(name = "user_id")
    @MapsId("userId")
    protected User user;
    @ManyToOne
    @JoinColumn(name = "direction_id")
    @MapsId("directionId")
    protected Direction direction;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "claim_type", insertable = false,updatable = false)
    protected ClaimType claimType;
    @Column(name = "countScore_for_individual_achievements")
    protected int countScoreForIndividualAchievements = 0;

    @Column(name = "champion")
    protected boolean champion = false;
    @Column(name = "absence")
    protected boolean absence = false;
    @OneToMany(mappedBy = "claim", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    protected List<Score> scoreList = new LinkedList<>();
    @Column(name = "summary_of_score")
    protected int summaryOfScore = 0;
    @Column(name = "is_win")
    protected boolean isWin = false;
    protected Claim(){}
    public Claim(User user, Direction direction, ClaimType claimType) {
        //if(user == null || direction == null )
            //throw new RuntimeException("При создании Claim были переданы user = null или department = null");
        //if(user.getId() == null || direction.getId() == null)
            //throw new RuntimeException("Для создания заявки user и department уже должны быть ХРАНИМЫМИ");
        this.id.userId = user.getId();
        this.id.directionId = direction.getId();
        this.id.claimType = claimType;

        this.user = user;;
        this.direction = direction;
        this.claimType = claimType;

        user.addClaim(this);
        direction.addClaim(this);
    }
    public static Claim createNewClaim(User user, Direction direction, ClaimType claimType)
    {
        Claim claim = new Claim(user,direction,claimType);
        return claim;
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

    public boolean isAbsence() {
        return absence;
    }

    public void setAbsence(boolean absence) {
        this.absence = absence;
    }

    public int getSummaryOfScore() {
        return summaryOfScore;
    }

    public void setSummaryOfScore(int summaryOfScore) {
        this.summaryOfScore = summaryOfScore;
    }

    public boolean isWin() {
        return isWin;
    }

    public void setWin(boolean win) {
        isWin = win;
    }

    @Override
    public int compareTo(Claim o) {
        if(!o.direction.equals(this.direction))
            throw new RuntimeException("Claim могут быть сранимы только одинаковых типов, онс ссылаются на разные Direction"
                    + "\n" + this.direction.urlToListOfClaims
                    + "\n" + o.direction.urlToListOfClaims
                    );
        /*
        if(o.isBudget() != this.isBudget())
            throw new RuntimeException("Claim не могу быть сравнимы. Один находится на бюджетной, а второй - на коммерсечской основе");
         */
        if(!this.getClaimType().equals(o.getClaimType()))
            throw new RuntimeException("Claim могут быть сранимы только одинаковых типов");
        if(this.isChampion() != o.isChampion())
        {
            if(isChampion())
                return 1;
            else
                return -1;
        }
        return this.getSummaryOfScore() - o.getSummaryOfScore();
    }
}
