package com.example.sstubot.initial;

import com.example.sstubot.database.model.Direction;
import com.example.sstubot.database.model.Exam;
import jakarta.persistence.*;

import java.util.LinkedList;
import java.util.List;

@Entity
@Table
public class MetaInfoAboutUserIntoDirection
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;
    final static int USER_CODE_ID = 1;
    final static int AMOUNT_SCORE_ID = 2;
    int countExams;
    int AMOUNT_SCORE_FOR_INDIVIDUAL_ACHIEVEMENTS;
    int DOCUMENT_TYPE_ID;//Оригинал / Копия
    int AGREEMENT_ID = DOCUMENT_TYPE_ID + 1;
    int CONDITION_ID; //Состояние - подано/отозвано/зачислен
    int CHAMPION_ID;
    @OneToOne(optional = false)
    @JoinColumn(name = "direction_id", unique = true)
    protected Direction direction;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "meta_id")
    @OrderColumn
    List<Exam> examList = new LinkedList<>();
    protected MetaInfoAboutUserIntoDirection(){};
    public MetaInfoAboutUserIntoDirection(Direction direction,int countExams, List<Exam> exams)
    {
        this.countExams = countExams;
        this.AMOUNT_SCORE_FOR_INDIVIDUAL_ACHIEVEMENTS = countExams + AMOUNT_SCORE_ID + 1;
        this.DOCUMENT_TYPE_ID = AMOUNT_SCORE_FOR_INDIVIDUAL_ACHIEVEMENTS + 1;
        this.AGREEMENT_ID = DOCUMENT_TYPE_ID + 1;
        this.CONDITION_ID = AGREEMENT_ID + 2;
        this.CHAMPION_ID = CONDITION_ID + 2;
        this.direction = direction;
        direction.setMetaInfo(this);
        this.examList = exams;
    }
    public List<Exam> getExamList() {
        return examList;
    }

    public void setExamList(List<Exam> examList) {
        this.examList = examList;
    }
}
