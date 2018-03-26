package seniorproject.attendancetrackingsystem.utils;


import java.util.ArrayList;


public class Course {
    public int courseID;
    public String courseName, courseCode;
    public ArrayList<Integer> sections;

    Course(String courseName, String courseCode) {
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.sections = new ArrayList<>();
    }
}
