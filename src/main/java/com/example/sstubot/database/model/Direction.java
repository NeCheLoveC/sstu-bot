package com.example.sstubot.database.model;

import jakarta.persistence.*;

@Entity
@Table
public class Direction
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;

    @Column(name = "name")
    protected String name;

    @ManyToOne
    @JoinColumn(name = "institute_id")
    protected Institute institute;

    @Column(name = "amount_budget")
    protected int amountBudget = 0;

    @Column(name = "amount_special_quota")
    protected int amountSpecialQuota = 0;

    @Column(name = "amount_target_quota")
    protected int amountTargetQuota = 0;

    @Column(name = "amount_main_budget_plan")
    protected int amountMainBudgetIntoPlan = 0;

    @Transient
    protected int reservedSpecialQuota = 0;

    @Transient
    protected int reservedTargetQuota = 0;

    @Transient
    protected int amountBudgetFinal = 0;

    public Direction(){}

    public Direction(String name, Institute institute)
    {
        this.name = name;
        this.institute = institute;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Institute getInstitute() {
        return institute;
    }

    public void setInstitute(Institute institute) {
        this.institute = institute;
    }

    public int getAmountBudget() {
        return amountBudget;
    }

    public void setAmountBudget(int amountBudget) {
        this.amountBudget = amountBudget;
    }

    public int getAmountSpecialQuota() {
        return amountSpecialQuota;
    }

    public void setAmountSpecialQuota(int amountSpecialQuota) {
        this.amountSpecialQuota = amountSpecialQuota;
    }

    public int getAmountTargetQuota() {
        return amountTargetQuota;
    }

    public void setAmountTargetQuota(int amountTargetQuota) {
        this.amountTargetQuota = amountTargetQuota;
    }

    public int getAmountMainBudgetIntoPlan() {
        return amountMainBudgetIntoPlan;
    }

    public void setAmountMainBudgetIntoPlan(int amountMainBudgetIntoPlan) {
        this.amountMainBudgetIntoPlan = amountMainBudgetIntoPlan;
    }

    public int getReservedSpecialQuota() {
        return reservedSpecialQuota;
    }

    public void setReservedSpecialQuota(int reservedSpecialQuota) {
        this.reservedSpecialQuota = reservedSpecialQuota;
    }

    public int getReservedTargetQuota() {
        return reservedTargetQuota;
    }

    public void setReservedTargetQuota(int reservedTargetQuota) {
        this.reservedTargetQuota = reservedTargetQuota;
    }

    public int getAmountBudgetFinal() {
        return amountBudgetFinal;
    }

    public void setAmountBudgetFinal(int amountBudgetFinal) {
        this.amountBudgetFinal = amountBudgetFinal;
    }

    public Long getId() {
        return id;
    }
}
