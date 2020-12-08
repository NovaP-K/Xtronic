package com.novaapps.xtronic.helpingclass;

import java.io.Serializable;

public class QuizQuestions implements Serializable {

    private String Question ;
    private String ANS ;
    private int Qno;
    private String Option1 = "";
    private String Option3 = "";
    private String Option2 = "";
    private String Option4 = "";

    public QuizQuestions() {
    }

    public String getQuestion() {
        return Question;
    }

    public int getQno() {
        return Qno;
    }

    public void setQno(int qno) {
        Qno = qno;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getANS() {
        return ANS;
    }

    public void setANS(String ANS) {
        this.ANS = ANS;
    }

    public String getOption1() {
        return Option1;
    }

    public void setOption1(String option1) {
        Option1 = option1;
    }

    public String getOption3() {
        return Option3;
    }

    public void setOption3(String option3) {
        Option3 = option3;
    }

    public String getOption2() {
        return Option2;
    }

    public void setOption2(String option2) {
        Option2 = option2;
    }

    public String getOption4() {
        return Option4;
    }

    public void setOption4(String option4) {
        Option4 = option4;
    }
}
