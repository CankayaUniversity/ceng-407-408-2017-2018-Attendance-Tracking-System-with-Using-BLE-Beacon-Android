package seniorproject.attendancetrackingsystem.utils;

import java.util.ArrayList;

public class Lecturer extends Actor {
  private int department;
  private ArrayList<GivenCourses> givenCourses;

  public Lecturer() {}

  public Lecturer(
      int id, String name, String surname, String mail, int department, String imageURI) {
    this.department = department;
    setId(id);
    setName(name);
    setSurname(surname);
    setMail(mail);
    setImage(imageURI);
  }

  public int getDepartment() {
    return this.department;
  }

  public void setDepartment(int department) {
    this.department = department;
  }

  public ArrayList<GivenCourses> getGivenCourses() {
    return givenCourses;
  }

  public void setGivenCourses(ArrayList<GivenCourses> givenCourses) {
    this.givenCourses = givenCourses;
  }
}
