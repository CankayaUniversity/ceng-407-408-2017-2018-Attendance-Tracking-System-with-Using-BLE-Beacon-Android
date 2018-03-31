package seniorproject.attendancetrackingsystem.utils;

import java.util.ArrayList;

public class Student extends Actor {
  private int studentNumber;
  private String bluetoothMAC;
  private ArrayList<Course> takenCourses;

  public Student() {

  }

  public Student(int id, int studentNumber, String name, String surname, String bluetoothMAC, String
          mail) {
    this.studentNumber = studentNumber;
    this.bluetoothMAC = bluetoothMAC;
    setId(id);
    setMail(mail);
    setName(name);
    setSurname(surname);
  }

  public String getBluetoothMAC() {
    return this.bluetoothMAC;
  }

  public void setBluetoothMAC(String bluetoothMAC) {
    this.bluetoothMAC = bluetoothMAC;
  }

  public int getStudentNumber() {
    return this.studentNumber;
  }

  public void setStudentNumber(int studentNumber) {
    this.studentNumber = studentNumber;
  }

  public void initiateServices() {
    // Initiate Bluetooth Service
  }

  public void courseAssignment(int courseId, int section) {
    // Course Assignment
  }

  public void changePassword() {
    // Change Password
  }
}
