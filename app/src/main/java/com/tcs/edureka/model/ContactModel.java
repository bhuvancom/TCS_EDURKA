package com.tcs.edureka.model;

public class ContactModel {
    String name, emailId, personalNo, ofcNo;

    public ContactModel() {
    }

    public ContactModel(String name, String emailId, String personalNo, String ofcNo) {
        this.name = name;
        this.emailId = emailId;
        this.personalNo = personalNo;
        this.ofcNo = ofcNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPersonalNo() {
        return personalNo;
    }

    public void setPersonalNo(String personalNo) {
        this.personalNo = personalNo;
    }

    public String getOfcNo() {
        return ofcNo;
    }

    public void setOfcNo(String ofcNo) {
        this.ofcNo = ofcNo;
    }
}
