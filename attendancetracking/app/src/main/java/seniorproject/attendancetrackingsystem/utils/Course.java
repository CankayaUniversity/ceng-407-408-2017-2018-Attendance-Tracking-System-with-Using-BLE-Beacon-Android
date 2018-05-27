package seniorproject.attendancetrackingsystem.utils;

public class Course {
  private final int courseId;
  private final String courseName;
  private final String courseCode;
  private final int sectionNumber;
  private final int departmentId;

  /** Creates Course object. */
  public Course(
      int courseId, String courseName, String courseCode, int sectionNumber, int departmentId) {
    this.courseId = courseId;
    this.courseName = courseName;
    this.courseCode = courseCode;
    this.sectionNumber = sectionNumber;
    this.departmentId = departmentId;
  }

  public String getCourseName() {
    return courseName;
  }

}
