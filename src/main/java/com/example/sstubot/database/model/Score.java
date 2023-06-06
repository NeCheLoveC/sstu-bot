package com.example.sstubot.database.model;

import jakarta.persistence.*;

@Entity
@Table
public class Score implements Comparable<Score>
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;

    @Column(name = "score")
    protected Integer score = null;

    @ManyToOne
    @JoinColumns(value = {
                    @JoinColumn(name = "user_id",referencedColumnName = "user_id"),
                    @JoinColumn(name = "direction_id", referencedColumnName = "direction_id"),
                    @JoinColumn(name = "claim_type", referencedColumnName = "claim_type")
            })
    protected Claim claim;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "exam_id")
    protected Exam exam;
    @Column(name = "absence")
    protected boolean absence = false;//Неявка

    public Score() {}

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public Claim getClaim() {
        return claim;
    }

    public void setClaim(Claim claim) {
        this.claim = claim;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public boolean isAbsence() {
        return absence;
    }

    public void setAbsence(boolean absence) {
        this.absence = absence;
        claim.setAbsence(absence);
    }

    @Override
    public int compareTo(Score o)
    {
        if(!exam.name.equals(o.getExam().name))
            throw new RuntimeException("Ошибка: попытка сравнения двух разных экзаменов");
        return this.score - o.score;
    }
}
