package com.gyapak.userinformation.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Professor extends Person implements Serializable {
    private String professorId;
    private String professorOfficeNum;
    private String professorDepartment;
    private String professorDesignation;

    public Professor() {}

    public Professor(String professorId, String professorOfficeNum, String professorDepartment, String professorDesignation) {
        this.professorId = professorId;
        this.professorOfficeNum = professorOfficeNum;
        this.professorDepartment = professorDepartment;
        this.professorDesignation = professorDesignation;
    }

    public Professor(String personFirstName, String personMiddleName, String personLastName, Date personDOB, String personContactNumber, String personEmail, String personGender, String personUserName, String professorId, String professorOfficeNum, String professorDepartment, String professorDesignation) {
        super(personFirstName, personMiddleName, personLastName, personDOB, personContactNumber, personEmail, personGender, personUserName, null);
        this.professorId = professorId;
        this.professorOfficeNum = professorOfficeNum;
        this.professorDepartment = professorDepartment;
        this.professorDesignation = professorDesignation;
    }

    public Professor(String personFirstName, String personMiddleName, String personLastName, Date personDOB, String personContactNumber, String personEmail, String personGender, String personUserName, String personPassword, String professorId, String professorOfficeNum, String professorDepartment, String professorDesignation) {
        super(personFirstName, personMiddleName, personLastName, personDOB, personContactNumber, personEmail, personGender, personUserName, personPassword);
        this.professorId = professorId;
        this.professorOfficeNum = professorOfficeNum;
        this.professorDepartment = professorDepartment;
        this.professorDesignation = professorDesignation;
    }

    public String getProfessorId() {
        return professorId;
    }

    public void setProfessorId(String professorId) {
        this.professorId = professorId;
    }

    public String getProfessorOfficeNum() {
        return professorOfficeNum;
    }

    public void setProfessorOfficeNum(String professorOfficeNum) {
        this.professorOfficeNum = professorOfficeNum;
    }

    public String getProfessorDepartment() {
        return professorDepartment;
    }

    public void setProfessorDepartment(String professorDepartment) {
        this.professorDepartment = professorDepartment;
    }

    public String getProfessorDesignation() {
        return professorDesignation;
    }

    public void setProfessorDesignation(String professorDesignation) {
        this.professorDesignation = professorDesignation;
    }
}
