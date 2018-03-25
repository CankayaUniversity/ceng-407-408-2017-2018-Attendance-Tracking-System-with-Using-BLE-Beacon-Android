package seniorproject.attendancetrackingsystem;


public class Department {
    private int departmentID;
    private String departmentName, abbreviation;

    public Department(int departmentID, String abbreviation, String departmentName) {
        this.departmentID = departmentID;
        this.departmentName = departmentName;
        this.abbreviation = abbreviation;
    }

    public int getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(int departmentID) {
        this.departmentID = departmentID;
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
