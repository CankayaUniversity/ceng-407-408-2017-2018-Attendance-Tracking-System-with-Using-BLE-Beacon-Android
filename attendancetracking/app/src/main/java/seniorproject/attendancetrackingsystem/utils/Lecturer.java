package seniorproject.attendancetrackingsystem.utils;

import java.util.ArrayList;

public class Lecturer extends Actor {
  private int department;
  private ArrayList<Course> givenCourses;

  public Lecturer() {
  }

  public Lecturer(int id, String name, String surname, String mail, int department) {
    this.department = department;
    setId(id);
    setName(name);
    setSurname(surname);
    setMail(mail);
  }

  public int getDepartment() {
    return this.department;
  }

  public void setDepartment(int department) {
    this.department = department;
  }

  public void takeAttendance() {
    // Take Attendance
  }

  public void courseAssignment() {
    // Course Assignment
  }

  public void showReport(int type) {
    // Show Report
  }
}
