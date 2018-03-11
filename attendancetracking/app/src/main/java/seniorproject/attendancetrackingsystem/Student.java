package seniorproject.attendancetrackingsystem;


import java.util.ArrayList;

public class Student extends Actor {
    private int phoneNumber;
    private ArrayList<Course> takenCourses;

    public void setPhoneNumber(int phoneNumber){ this.phoneNumber = phoneNumber; }
    public int getPhoneNumber(){ return this.phoneNumber; }
    public void initiateServices(){
        //Initiate Bluetooth Service
    }
    public void courseAssignment(int courseID, int section){
        //Course Assignment
    }
    public void changePassword(){
        //Change Password
    }
}
