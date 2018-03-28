package seniorproject.attendancetrackingsystem.utils;

public class Department {
  private int departmentId;
  private String departmentName;
  private String abbreviation;

  /** Creates Department Object. */

  public Department(int departmentId, String abbreviation, String departmentName) {
    this.departmentId = departmentId;
    this.departmentName = departmentName;
    this.abbreviation = abbreviation;
  }

  public int getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(int departmentId) {
    this.departmentId = departmentId;
  }

  public String getDepartmentName() {
    return departmentName;
  }

  public void setDepartmentName(String departmentName) {
    this.departmentName = departmentName;
  }

  public String getAbbreviation() {
    return abbreviation;
  }

  public void setAbbreviation(String abbreviation) {
    this.abbreviation = abbreviation;
  }
}
