package com.example.sstubot.database.model;

import com.example.sstubot.database.model.urils.EducationType;
import com.example.sstubot.initial.MetaInfoAboutUserIntoDirection;
import jakarta.persistence.*;

import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "Direction", indexes = {
        @Index(name = "INDEX_URL_TO_BUDGET", columnList = "url_to_list_of_claims_budget", unique = true),
        @Index(name = "INDEX_URL_TO_COMMERCE", columnList = "url_to_list_of_claims_commerce", unique = true)
})
public class Direction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;

    @Column(name = "name")
    protected String name;
    @Column(name = "edu_type")
    @Enumerated(EnumType.STRING)
    protected EducationType educationType;//очка, заочка, очно-заочное

    @ManyToOne
    @JoinColumn(name = "institute_id")
    protected Institute institute;

    @Embedded
    private MetaInfoAboutUserIntoDirection metaInfo;

    //Особая квота
    @Column(name = "unusual_quota")
    protected int amountUnusualQuota = 0;

    //Основные места - план (бюджет)
    @Column(name = "amount_budget")
    protected int amountBudget = 0;

    //Спец квота
    @Column(name = "amount_special_quota")
    protected int amountSpecialQuota = 0;

    //Целевое обучение
    @Column(name = "amount_target_quota")
    protected int amountTargetQuota = 0;
    //Бюджетных мест согласно плану
    @Column(name = "amount_main_budget_plan")
    protected int amountMainBudgetIntoPlan = 0;

    @Column(name = "abbreviation")
    protected String abbreviation;

    @Column(name = "url_to_list_of_claims_budget", unique = true)
    protected String urlToListOfClaims;

    @Column(name = "url_to_list_of_claims_commerce",unique = true)
    protected String urlToListOfClaimsCommerce;
    // TODO: 05.05.2023 Вот тут не уверен насчет hibernate
    @OneToMany
    @JoinColumn(name = "direction_id")
    List<Exam> exams = new LinkedList<>();
    /*
    @Enumerated(EnumType.STRING)
    @Column(name = "direction_type_payment")
    protected DirectionType directionTypePayment;
     */
    @Transient
    private List<Claim> budgetGeneralListClaims = new LinkedList<Claim>();
    @Transient
    private List<Claim> budgetSpecialQuotaClaims = new LinkedList<Claim>();
    @Transient
    private List<Claim> budgetTargetQuotaClaims = new LinkedList<Claim>();
    @Transient
    private List<Claim> budgetUnusualQuotaClaims = new LinkedList<Claim>();
    @Transient
    private List<Claim> commerceGeneralListClaims = new LinkedList<>();
    @Transient
    protected int reservedUnusualQuota = 0;
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
        institute.addDirection(this);
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

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public int getAmountUnusualQuota() {
        return amountUnusualQuota;
    }

    public void setAmountUnusualQuota(int unusualQuota) {
        this.amountUnusualQuota = unusualQuota;
    }

    public int getReservedUnusualQuota() {
        return reservedUnusualQuota;
    }

    public void setReservedUnusualQuota(int reservedUnusualQuota) {
        this.reservedUnusualQuota = reservedUnusualQuota;
    }

    public String getUrlToListOfClaims() {
        return urlToListOfClaims;
    }

    public void setUrlToListOfClaims(String urlToListOfClaims) {
        this.urlToListOfClaims = urlToListOfClaims;
    }

    public String getUrlToListOfClaimsCommerce() {
        return urlToListOfClaimsCommerce;
    }

    public void setUrlToListOfClaimsCommerce(String urlToListOfClaimsCommerce) {
        this.urlToListOfClaimsCommerce = urlToListOfClaimsCommerce;
    }

    public MetaInfoAboutUserIntoDirection getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(MetaInfoAboutUserIntoDirection metaInfo) {
        this.metaInfo = metaInfo;
    }

    public List<Exam> getExams() {
        return exams;
    }

    public void setExams(List<Exam> exams) {
        this.exams = exams;
    }

    /*
    public DirectionType getDirectionTypePayment() {
        return directionTypePayment;
    }

    public void setDirectionTypePayment(DirectionType directionTypePayment) {
        this.directionTypePayment = directionTypePayment;
    }

     */
}
