package com.example.sstubot.initial;

import jakarta.persistence.Embeddable;

@Embeddable
public class MetaInfoAboutUserIntoDirection
{
    final static int USER_CODE_ID = 1;
    final static int AMOUNT_SCORE_ID = 2;
    int countExams;
    int AMOUNT_SCORE_FOR_INDIVIDUAL_ACHIEVEMENTS;
    int DOCUMENT_TYPE_ID;
    int AGREEMENT_ID = DOCUMENT_TYPE_ID + 1;
    int CONDITION_ID; //Состояние - подано/отозвано/зачислен
    int CHAMPION_ID;
    protected MetaInfoAboutUserIntoDirection(){};
    public MetaInfoAboutUserIntoDirection(int countExams)
    {
        this.countExams = countExams;
        this.AMOUNT_SCORE_FOR_INDIVIDUAL_ACHIEVEMENTS = countExams + AMOUNT_SCORE_ID + 1;
        this.DOCUMENT_TYPE_ID = AMOUNT_SCORE_FOR_INDIVIDUAL_ACHIEVEMENTS + 1;
        this.AGREEMENT_ID = DOCUMENT_TYPE_ID + 1;
        this.CONDITION_ID = AGREEMENT_ID + 2;
        this.CHAMPION_ID = CONDITION_ID + 2;
    }
}
