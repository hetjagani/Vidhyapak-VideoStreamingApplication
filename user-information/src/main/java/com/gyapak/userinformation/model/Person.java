package com.gyapak.userinformation.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Person implements Serializable {
    private String personFirstName;
    private String personMiddleName;
    private String personLastName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date personDOB;

    private String personContactNumber;
    private String personEmail;
    private String personGender;
    private String personUserName;
    private String personPassword;

    public Person() {}

    public Person(String personFirstName, String personMiddleName, String personLastName, Date personDOB, String personContactNumber, String personEmail, String personGender, String personUserName) {
        this.personFirstName = personFirstName;
        this.personMiddleName = personMiddleName;
        this.personLastName = personLastName;
        this.personDOB = personDOB;
        this.personContactNumber = personContactNumber;
        this.personEmail = personEmail;
        this.personGender = personGender;
        this.personUserName = personUserName;
    }

    public Person(String personFirstName, String personMiddleName, String personLastName, Date personDOB, String personContactNumber, String personEmail, String personGender, String personUserName, String personPassword) {
        this.personFirstName = personFirstName;
        this.personMiddleName = personMiddleName;
        this.personLastName = personLastName;
        this.personDOB = personDOB;
        this.personContactNumber = personContactNumber;
        this.personEmail = personEmail;
        this.personGender = personGender;
        this.personUserName = personUserName;
        this.personPassword = personPassword;
    }

    @Override
    public String toString() {
        return "Person{" +
                "personFirstName='" + personFirstName + '\'' +
                ", personMiddleName='" + personMiddleName + '\'' +
                ", personLastName='" + personLastName + '\'' +
                ", personDOB=" + personDOB +
                ", personContactNumber='" + personContactNumber + '\'' +
                ", personEmail='" + personEmail + '\'' +
                ", personGender='" + personGender + '\'' +
                ", personUserName='" + personUserName + '\'' +
                '}';
    }

    public String getPersonFirstName() {
        return personFirstName;
    }

    public void setPersonFirstName(String personFirstName) {
        this.personFirstName = personFirstName;
    }

    public String getPersonMiddleName() {
        return personMiddleName;
    }

    public void setPersonMiddleName(String personMiddleName) {
        this.personMiddleName = personMiddleName;
    }

    public String getPersonLastName() {
        return personLastName;
    }

    public void setPersonLastName(String personLastName) {
        this.personLastName = personLastName;
    }

    public Date getPersonDOB() {
        return personDOB;
    }

    public void setPersonDOB(Date personDOB) {
        this.personDOB = personDOB;
    }

    public String getPersonContactNumber() {
        return personContactNumber;
    }

    public void setPersonContactNumber(String personContactNumber) {
        this.personContactNumber = personContactNumber;
    }

    public String getPersonEmail() {
        return personEmail;
    }

    public void setPersonEmail(String personEmail) {
        this.personEmail = personEmail;
    }

    public String getPersonGender() {
        return personGender;
    }

    public void setPersonGender(String personGender) {
        this.personGender = personGender;
    }

    public String getPersonUserName() {
        return personUserName;
    }

    public void setPersonUserName(String personUserName) {
        this.personUserName = personUserName;
    }

    public String getPersonPassword() {
        return personPassword;
    }

    public void setPersonPassword(String personPassword) {
        this.personPassword = personPassword;
    }
}
