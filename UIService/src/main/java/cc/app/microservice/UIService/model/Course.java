package cc.app.microservice.UIService.model;

import java.io.Serializable;
import java.time.Year;

public class Course implements Serializable {
    private String courseId;
    private String courseCode;
    private String courseName;
    private Year courseYear;
    private String courseSemester;
    private String courseType;
    private Professor professor;

    public Course() {}

    public Course(String courseId, String courseCode, String courseName, Year courseYear, String courseSemester, String courseType, Professor professor) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.courseYear = courseYear;
        this.courseSemester = courseSemester;
        this.courseType = courseType;
        this.professor = professor;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Year getCourseYear() {
        return courseYear;
    }

    public void setCourseYear(Year courseYear) {
        this.courseYear = courseYear;
    }

    public String getCourseSemester() {
        return courseSemester;
    }

    public void setCourseSemester(String courseSemester) {
        this.courseSemester = courseSemester;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", courseCode='" + courseCode + '\'' +
                ", courseName='" + courseName + '\'' +
                ", courseYear=" + courseYear +
                ", courseSemester='" + courseSemester + '\'' +
                ", courseType='" + courseType + '\'' +
                ", professor=" + professor +
                '}';
    }
}
