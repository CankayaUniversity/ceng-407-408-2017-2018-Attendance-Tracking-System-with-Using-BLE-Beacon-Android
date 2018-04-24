package seniorproject.attendancetrackingsystem.utils;

public class Admin extends Actor {
  private String username;

  public void deleteStudent(int studentId) {}

  public void deleteLecturer(int lecturerId, String validation) {}

  public void deleteAdmin(int adminId) {}

  public void addCourseAssignment(int studentId, int courseId) {}

  public void updateCourseAssignment(int studentId, int courseId) {}

  public void deleteCourseAssignment(int studentId, int courseId) {}

  public void createAdmin() {}

  public Student searchStudent(String name) {

    return null;
  }

  public Lecturer searchLecturer(String name) {

    return null;
  }

  public String getUsername() {
    return this.username;
  }
}
