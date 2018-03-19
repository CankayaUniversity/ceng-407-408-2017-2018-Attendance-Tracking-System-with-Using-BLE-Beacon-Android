package seniorproject.attendancetrackingsystem;


import java.util.ArrayList;

public class Student extends Actor {
    private int studentNumber;
    private String phoneNumber;
    private ArrayList<Course> takenCourses;

    Student() {
        studentNumber = 0;
        phoneNumber = "";
        setMail("");
        setName("");
        setSurname("");
        setPassword("");
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setStudentNumber(int studentNumber) {
        this.studentNumber = studentNumber;
    }

    public int getStudentNumber() {
        return this.studentNumber;
    }

    public void initiateServices() {
        //Initiate Bluetooth Service
    }

    public void courseAssignment(int courseID, int section) {
        //Course Assignment
    }

    public void changePassword() {
        //Change Password
    }
}
