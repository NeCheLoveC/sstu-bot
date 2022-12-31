package com.example.sstubot.database.model;

import jakarta.persistence.*;

@Entity
@Table
public class Score
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;

    @Column(name = "score")
    protected Integer score = null;

    @ManyToOne
    @JoinColumns(value = {@JoinColumn(name = "user_id",referencedColumnName = "user_id"), @JoinColumn(name = "direction_id", referencedColumnName = "direction_id")})
    protected Claim claim;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    protected Exam exam;

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
}
