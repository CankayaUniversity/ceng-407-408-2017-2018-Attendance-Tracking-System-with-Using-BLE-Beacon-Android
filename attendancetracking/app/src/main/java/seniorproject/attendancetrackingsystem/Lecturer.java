package seniorproject.attendancetrackingsystem;


import java.util.ArrayList;

public class Lecturer extends Actor {
    private String department;
    private ArrayList<Course>  givenCourses;

    public void setDepartment(String department) { this.department = department; }
    public String getDepartment() { return this.department; }
    public void takeAttendance(){
        //Take Attendance
    }
    public void courseAssignment(){
        //Course Assignment
    }
    public void showReport(int type){
        //Show Report
    }
}
