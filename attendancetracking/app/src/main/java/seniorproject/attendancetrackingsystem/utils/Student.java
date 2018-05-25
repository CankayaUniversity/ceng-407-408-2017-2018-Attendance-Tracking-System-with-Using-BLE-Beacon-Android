package seniorproject.attendancetrackingsystem.utils;

import java.util.ArrayList;

public class Student extends Actor {
  private final int studentNumber;
  private ArrayList<TakenCourses> takenCourses;

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

  public void setTakenCourses(ArrayList<TakenCourses> takenCourses) {
    this.takenCourses = takenCourses;
  }
}
