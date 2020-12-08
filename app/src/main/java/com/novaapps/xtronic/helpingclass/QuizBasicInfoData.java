package com.novaapps.xtronic.helpingclass;

import android.content.Context;

import java.io.Serializable;


public class QuizBasicInfoData implements Serializable {

    //Variables
    private String _CreatorName ;
    private String _CreatorUid;
    private String _TopicName ;
    private Long _Duration ;
    private String _DateCreated ;
    private String _IdentifierType ;
    private Boolean _IsQuestionBasedTimer ;

    public QuizBasicInfoData() {
    }


    public void InsertData(String _CN , String _TN , Long _D , Boolean TimerType){
        _CreatorName = _CN ;
        _TopicName = _TN ;
        _Duration = _D ;
        _IsQuestionBasedTimer = TimerType ;
    }

    public String get_IdentifierType() {
        return _IdentifierType;
    }

    public String get_CreatorUid() {
        return _CreatorUid;
    }

    public void set_CreatorUid(String creatorUid) {
        _CreatorUid = creatorUid;
    }

    public void set_IdentifierType(String identifierType) {
        _IdentifierType = identifierType;
    }

    public void set_DateCreated(String dateCreated) {
        _DateCreated = dateCreated;
    }

    public String get_CreatorName() {
        return _CreatorName;
    }

    public String get_TopicName() {
        return _TopicName;
    }

    public String get_DateCreated() {
        return _DateCreated;
    }

    public void set_CreatorName(String _CreatorName) {
        this._CreatorName = _CreatorName;
    }

    public void set_TopicName(String _TopicName) {
        this._TopicName = _TopicName;
    }

    public void set_Duration(Long _Duration) {
        this._Duration = _Duration;
    }

    public Long get_Duration() {
        return _Duration;
    }

    public Boolean get_IsQuestionBasedTimer() {
        return _IsQuestionBasedTimer;
    }

    public void set_IsQuestionBasedTimer(Boolean _IsQuestionBasedTimer) {
        this._IsQuestionBasedTimer = _IsQuestionBasedTimer;
    }
}
