package seniorproject.attendancetrackingsystem.utils;

public class TakenCourses {
  private int course_id;
  private int section;

  public TakenCourses(int course_id, int section) {
    this.course_id = course_id;
    this.section = section;
  }

  public int getCourse_id() {
    return course_id;
  }

  public void setCourse_id(int course_id) {
    this.course_id = course_id;
  }

  public int getSection() {
    return section;
  }

  public void setSection(int section) {
    this.section = section;
  }
}
