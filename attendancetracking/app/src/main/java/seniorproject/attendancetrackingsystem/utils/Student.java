package seniorproject.attendancetrackingsystem.utils;

import java.util.ArrayList;

public class Student extends Actor {
  private int studentNumber;
  private ArrayList<TakenCourses> takenCourses;

  public Student() {}

  public Student(
      int id, int studentNumber, String name, String surname, String mail, String imageURI) {
    this.studentNumber = studentNumber;
    setId(id);
    setMail(mail);
    setName(name);
    setSurname(surname);
    takenCourses = null;
    setImage(imageURI);
  }

  public int getStudentNumber() {
    return this.studentNumber;
  }

  public void setStudentNumber(int studentNumber) {
    this.studentNumber = studentNumber;
  }

  public ArrayList<TakenCourses> getTakenCourses() {
    return this.takenCourses;
  }

  public void setTakenCourses(ArrayList<TakenCourses> takenCourses) {
    this.takenCourses = takenCourses;
  }

  public void changePassword() {
    // Change Password
  }
}
