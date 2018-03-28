package seniorproject.attendancetrackingsystem.utils;

public class Course {
  private int courseId;
  private String courseName;
  private String courseCode;
  private int sectionNumber;
  private int departmentId;

  /** Creates Course object. */
  public Course(
      int courseId, String courseName, String courseCode, int sectionNumber, int departmentId) {
    this.courseId = courseId;
    this.courseName = courseName;
    this.courseCode = courseCode;
    this.sectionNumber = sectionNumber;
    this.departmentId = departmentId;
  }

  public int getCourseId() {
    return courseId;
  }

  public void setCourseId(int courseId) {
    this.courseId = courseId;
  }

  public String getCourseName() {
    return courseName;
  }

  public void setCourseName(String courseName) {
    this.courseName = courseName;
  }

  public String getCourseCode() {
    return courseCode;
  }

  public void setCourseCode(String courseCode) {
    this.courseCode = courseCode;
  }

  public int getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(int departmentId) {
    this.departmentId = departmentId;
  }

  public int getSectionNumber() {
    return sectionNumber;
  }

  public void setSectionNumber(int sectionNumber) {
    this.sectionNumber = sectionNumber;
  }
}
