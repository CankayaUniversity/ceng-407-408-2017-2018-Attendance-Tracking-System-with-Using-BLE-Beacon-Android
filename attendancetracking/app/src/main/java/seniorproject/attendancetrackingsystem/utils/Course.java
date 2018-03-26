package seniorproject.attendancetrackingsystem.utils;


import java.util.ArrayList;


public class Course {
    private int courseID;
    private String courseName, courseCode;
    private int sectionNumber;
    private int departmentID;

    public Course(int courseID, String courseName, String courseCode, int sectionNumber, int departmentID) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.sectionNumber = sectionNumber;
        this.departmentID = departmentID;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public int getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(int departmentID) {
        this.departmentID = departmentID;
    }

    public int getSectionNumber() {
        return sectionNumber;
    }

    public void setSectionNumber(int sectionNumber) {
        this.sectionNumber = sectionNumber;
    }
}
