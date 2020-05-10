package cc.app.microservice.UIService.model;

import java.time.Year;
import java.util.Date;

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

    @Override
    public String toString() {
        return "Student{" +
                "studentEnrollNum='" + studentEnrollNum + '\'' +
                ", studentAdmitYear=" + studentAdmitYear +
                "} " + super.toString();
    }

    public boolean valid() {
        return this.getPersonUserName() != null && this.getPersonPassword() != null &&
                this.getPersonFirstName() != null && this.getPersonEmail() != null &&
                this.getPersonDOB() != null && this.getPersonContactNumber() != null &&
                this.studentAdmitYear !=null && this.studentEnrollNum != null;
    }
}
