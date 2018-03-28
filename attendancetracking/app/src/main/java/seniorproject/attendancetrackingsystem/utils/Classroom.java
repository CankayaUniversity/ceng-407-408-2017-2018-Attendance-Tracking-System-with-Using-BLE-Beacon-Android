package seniorproject.attendancetrackingsystem.utils;

import java.util.ArrayList;

public class Classroom {
  private int courseId;
  private int section;
  private String name;
  private String code;
  private ArrayList<Student> attendedStudents;
  private Lecturer lecturer;

  public String getCourseName() {
    return this.name;
  }

  public String getCourseCode() {
    return this.code;
  }

  public int getSection() {
    return this.section;
  }

  public void addStudent(Student studentInfo) {
    attendedStudents.add(studentInfo);
  }

  public ArrayList<Student> getStudentList() {
    return this.attendedStudents;
  }
}
