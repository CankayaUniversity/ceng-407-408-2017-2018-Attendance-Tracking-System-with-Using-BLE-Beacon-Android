package seniorproject.attendancetrackingsystem.utils;

public class Department {
  private final int departmentId;
  private final String departmentName;
  private final String abbreviation;

  /** Creates Department Object. */

  public Department(int departmentId, String abbreviation, String departmentName) {
    this.departmentId = departmentId;
    this.departmentName = departmentName;
    this.abbreviation = abbreviation;
  }

  public int getDepartmentId() {
    return departmentId;
  }

  public String getDepartmentName() {
    return departmentName;
  }

}
