package seniorproject.attendancetrackingsystem.utils;

import java.util.ArrayList;

public class Lecturer extends Actor {
  private String department;
  private ArrayList<Course> givenCourses;

  public String getDepartment() {
    return this.department;
  }

  public void setDepartment(String department) {
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
