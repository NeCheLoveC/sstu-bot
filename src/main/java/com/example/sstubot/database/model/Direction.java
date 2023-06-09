package com.example.sstubot.database.model;

import com.example.sstubot.database.model.urils.*;
import com.example.sstubot.initial.MetaInfoAboutUserIntoDirection;
import jakarta.persistence.*;
import org.hibernate.Hibernate;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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

    @OneToOne(mappedBy = "direction", optional = true, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    protected MetaInfoAboutUserIntoDirection metaInfo;
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
    @Column(name = "ignore_direction")
    protected boolean ignoreDirection = false;
    @Column(name = "url_to_list_of_claims_commerce",unique = true)
    protected String urlToListOfClaimsCommerce;
    // TODO: 05.05.2023 Вот тут не уверен насчет hibernate
    //@OneToMany
    //@JoinColumn(name = "direction_id")
    //List<Exam> exams = new LinkedList<>();
    /*
    @Enumerated(EnumType.STRING)
    @Column(name = "direction_type_payment")
    protected DirectionType directionTypePayment;
     */
    @Column(nullable = true)
    protected Integer minScoreGeneralList = null;
    @Column(nullable = true)
    protected Integer minScoreTargetList = null;
    @Column(nullable = true)
    protected Integer minScoreUnusualList = null;
    @Column(nullable = true)
    protected Integer minScoreSpecialList = null;
    @OneToMany(mappedBy = "direction")
    private List<Claim> allClaims = new LinkedList<>();
    @Transient
    private List<Claim> newClaims = new LinkedList<>();

    public void refreshGeneralList()
    {
        budgetGeneralListClaims.refreshClaims();
    }
    @Transient
    private GeneralListContainer budgetGeneralListClaims;
    @Transient
    private ContainerQuotaClaims budgetSpecialQuotaClaims; //= new ContainerQuotaClaims(amountSpecialQuota, ClaimType.BUDGET_SPECIAL_QUOTA);
    @Transient
    private ContainerQuotaClaims budgetTargetQuotaClaims;// = new ContainerQuotaClaims(amountTargetQuota, ClaimType.BUDGET_TARGET_QUOTA);
    @Transient
    private ContainerQuotaClaims budgetUnusualQuotaClaims;// = new ContainerQuotaClaims(amountUnusualQuota, ClaimType.BUDGET_UNUSUAL_QUOTA);
    @Transient
    private ContainerCommerce commerceGeneralListClaims;// = new ContainerCommerce();

    public Direction()
    {
    }

    public Direction(String name, Institute institute, EducationType educationType,MetaInfoAboutUserIntoDirection metaInfo)
    {
        this();
        this.name = name;
        this.institute = institute;
        this.educationType = educationType;
        this.metaInfo = metaInfo;
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

    /*
    public List<Exam> getExams() {
        return exams;
    }
     */

    /*
    public void setExams(List<Exam> exams) {
        this.exams = exams;
    }
     */
    public void addClaim(Claim claim)
    {
        this.allClaims.add(claim);
    }

    public EducationType getEducationType() {
        return educationType;
    }

    public void setEducationType(EducationType educationType) {
        this.educationType = educationType;
    }

    public boolean isIgnoreDirection() {
        return ignoreDirection;
    }

    public void setIgnoreDirection(boolean ignoreDirection) {
        this.ignoreDirection = ignoreDirection;
    }

    /*
    public DirectionType getDirectionTypePayment() {
        return directionTypePayment;
    }

    public void setDirectionTypePayment(DirectionType directionTypePayment) {
        this.directionTypePayment = directionTypePayment;
    }

     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Direction direction = (Direction) o;
        return getId() != null && Objects.equals(getId(), direction.getId());
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    public void addClaimIntoNewClaims(Claim claim)
    {
        newClaims.add(claim);
    }

    public boolean canAddIntoTarget(Claim claim)
    {
        return budgetTargetQuotaClaims.canAddClaim(claim);
    }

    public boolean canAddIntoSpecial(Claim claim)
    {
        return budgetSpecialQuotaClaims.canAddClaim(claim);
    }

    public boolean canAddIntoUnusual(Claim claim)
    {
        return budgetUnusualQuotaClaims.canAddClaim(claim);
    }

    public boolean canAddIntoGeneralList(Claim claim)
    {
        return budgetGeneralListClaims.canAddClaim(claim);
    }

    public boolean canAddIntoCommerce(Claim claim)
    {
        return true;
    }

    public Claim addClaimIntoList(Claim claim)
    {
        switch (claim.getClaimType())
        {
            case BUDGET_SPECIAL_QUOTA:
                return budgetSpecialQuotaClaims.addClaimIntoContainer(claim);
            case BUDGET_TARGET_QUOTA:
                return budgetTargetQuotaClaims.addClaimIntoContainer(claim);
            case BUDGET_UNUSUAL_QUOTA:
                return budgetUnusualQuotaClaims.addClaimIntoContainer(claim);
            case BUDGET_GENERAL_LIST:
                return budgetGeneralListClaims.addClaimIntoContainer(claim);
            case COMMERCE_GENERAL_LIST:
                return commerceGeneralListClaims.addClaimIntoContainer(claim);
            default:
                throw new RuntimeException("Не распознан тип заявки... (INTO switch/case");
        }
    }

    public GeneralListContainer getBudgetGeneralListClaims() {
        return budgetGeneralListClaims;
    }

    public void setBudgetGeneralListClaims(GeneralListContainer budgetGeneralListClaims) {
        this.budgetGeneralListClaims = budgetGeneralListClaims;
    }

    public int amountReservedByQuot()
    {
        return budgetTargetQuotaClaims.currentSize();
    }

    public void initContainers()
    {
        this.budgetUnusualQuotaClaims = new ContainerQuotaClaims(amountUnusualQuota, ClaimType.BUDGET_UNUSUAL_QUOTA);
        this.budgetSpecialQuotaClaims = new ContainerQuotaClaims(amountSpecialQuota, ClaimType.BUDGET_SPECIAL_QUOTA);
        this.budgetTargetQuotaClaims = new ContainerQuotaClaims(amountTargetQuota, ClaimType.BUDGET_TARGET_QUOTA);
        this.commerceGeneralListClaims = new ContainerCommerce();
        this.budgetGeneralListClaims = new GeneralListContainer(() -> {return this.budgetTargetQuotaClaims.currentSize() + this.budgetSpecialQuotaClaims.currentSize() + this.budgetUnusualQuotaClaims.currentSize();}, amountMainBudgetIntoPlan);
    }

    public void deleteClaim(Claim claim)
    {
        switch (claim.getClaimType())
        {
            case BUDGET_SPECIAL_QUOTA:
                budgetSpecialQuotaClaims.removeClaimFromList(claim);
                break;
            case BUDGET_TARGET_QUOTA:
                budgetTargetQuotaClaims.removeClaimFromList(claim);
                break;
            case BUDGET_UNUSUAL_QUOTA:
                budgetUnusualQuotaClaims.removeClaimFromList(claim);
                break;
            case BUDGET_GENERAL_LIST:
                budgetGeneralListClaims.removeClaimFromList(claim);
                break;
            case COMMERCE_GENERAL_LIST:
                commerceGeneralListClaims.removeClaimFromList(claim);
                break;
            default:
                throw new RuntimeException("Не распознан тип заявки... (INTO switch/case");
        }
    }

    public void initWinClaimPositionAndMinSocre()
    {
        budgetTargetQuotaClaims.initWinClaimPosition();
        budgetSpecialQuotaClaims.initWinClaimPosition();
        budgetUnusualQuotaClaims.initWinClaimPosition();
        budgetGeneralListClaims.initWinClaimPosition();

        if(amountMainBudgetIntoPlan != 0)
            this.minScoreGeneralList = budgetGeneralListClaims.getMinScoreForAdd();
        if(amountSpecialQuota != 0)
            this.minScoreSpecialList = budgetSpecialQuotaClaims.getMinScoreForAdd();
        if(amountTargetQuota != 0)
            this.minScoreTargetList = budgetTargetQuotaClaims.getMinScoreForAdd();
        if(amountUnusualQuota != 0)
            this.minScoreUnusualList = budgetUnusualQuotaClaims.getMinScoreForAdd();
    }

    public void enrollClaimWithoutOrigDoc(Claim claim)
    {
        if(claim.getDirection() != this)
            throw new RuntimeException("ошибка в enrollClaimWithoutDoc");
        if(claim.claimWithOriginalDoc())
            throw new RuntimeException("ошибка в enrollClaimWithoutDoc");

        switch (claim.getClaimType())
        {
            case BUDGET_SPECIAL_QUOTA:
                budgetSpecialQuotaClaims.removeClaimFromList(claim);
                break;
            case BUDGET_TARGET_QUOTA:
                budgetTargetQuotaClaims.removeClaimFromList(claim);
                break;
            case BUDGET_UNUSUAL_QUOTA:
                budgetUnusualQuotaClaims.removeClaimFromList(claim);
                break;
            case BUDGET_GENERAL_LIST:
                budgetGeneralListClaims.removeClaimFromList(claim);
                break;
            case COMMERCE_GENERAL_LIST:
                commerceGeneralListClaims.removeClaimFromList(claim);
                break;
            default:
                throw new RuntimeException("Не распознан тип заявки... (INTO switch/case");
        }

    }
}
