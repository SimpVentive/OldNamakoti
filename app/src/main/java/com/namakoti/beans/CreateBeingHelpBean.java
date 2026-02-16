package com.namakoti.beans;

import java.io.Serializable;

/**
 * Created by anusha on 12/5/2017.
 */

public class CreateBeingHelpBean implements Serializable{

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getCauseId() {
        return causeId;
    }

    public void setCauseId(String causeId) {
        this.causeId = causeId;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getGodId() {
        return godId;
    }

    public void setGodId(String godId) {
        this.godId = godId;
    }

    public String getNamam() {
        return namam;
    }

    public void setNamam(String namam) {
        this.namam = namam;
    }

    public String getNoOfChants() {
        return noOfChants;
    }

    public void setNoOfChants(String noOfChants) {
        this.noOfChants = noOfChants;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPujaType() {
        return pujaType;
    }

    public void setPujaType(String pujaType) {
        this.pujaType = pujaType;
    }

    public String getNoOfPeopleHelp() {
        return noOfPeopleHelp;
    }

    public void setNoOfPeopleHelp(String noOfPeopleHelp) {
        this.noOfPeopleHelp = noOfPeopleHelp;
    }

    public String getAddParticipantsType() {
        return addParticipantsType;
    }

    public void setAddParticipantsType(String addParticipantsType) {
        this.addParticipantsType = addParticipantsType;
    }
    public String getSelfChantNo() {
        return selfChantNo;
    }

    public void setSelfChantNo(String selfChantNo) {
        this.selfChantNo = selfChantNo;
    }

    public String getPeopleChantNo() {
        return peopleChantNo;
    }

    public void setPeopleChantNo(String peopleChantNo) {
        this.peopleChantNo = peopleChantNo;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    private String requestId;
    private String causeId;
    private String languageId;
    private String godId;
    private String namam;
    private String noOfChants;
    private String selfChantNo;
    private String noOfPeopleHelp;
    private String peopleChantNo;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String pujaType;
    private String addParticipantsType;

}
