package com.gyapak.userinformation.model;

import java.time.Year;
import java.util.Date;
import java.util.List;

public class Student extends Person {
    private String studentEnrollNum;
    private Year studentAdmitYear;

    public Student() {}

    public Student(String studentEnrollNum, Year studentAdmitYear) {
        this.studentEnrollNum = studentEnrollNum;
        this.studentAdmitYear = studentAdmitYear;
    }

    public Student(String personFirstName, String personMiddleName, String personLastName, Date personDOB, String personContactNumber, String personEmail, String personGender, String personUserName, String studentEnrollNum, Year studentAdmitYear) {
        super(personFirstName, personMiddleName, personLastName, personDOB, personContactNumber, personEmail, personGender, personUserName, null);
        this.studentEnrollNum = studentEnrollNum;
        this.studentAdmitYear = studentAdmitYear;
    }

    public Student(String personFirstName, String personMiddleName, String personLastName, Date personDOB, String personContactNumber, String personEmail, String personGender, String personUserName, String personPassword, String studentEnrollNum, Year studentAdmitYear) {
        super(personFirstName, personMiddleName, personLastName, personDOB, personContactNumber, personEmail, personGender, personUserName, personPassword);
        this.studentEnrollNum = studentEnrollNum;
        this.studentAdmitYear = studentAdmitYear;
    }

    public String getStudentEnrollNum() {
        return studentEnrollNum;
    }

    public void setStudentEnrollNum(String studentEnrollNum) {
        this.studentEnrollNum = studentEnrollNum;
    }

    public Year getStudentAdmitYear() {
        return studentAdmitYear;
    }

    public void setStudentAdmitYear(Year studentAdmitYear) {
        this.studentAdmitYear = studentAdmitYear;
    }
}
