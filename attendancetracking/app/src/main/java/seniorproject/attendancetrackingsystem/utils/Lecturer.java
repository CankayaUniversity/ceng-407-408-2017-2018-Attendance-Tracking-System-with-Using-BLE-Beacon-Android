package seniorproject.attendancetrackingsystem.utils;

import java.util.ArrayList;

public class Lecturer extends Actor {
  private final int department;
  private ArrayList<GivenCourses> givenCourses;

  public Lecturer(
      int id, String name, String surname, String mail, int department, String imageURI) {
    this.department = department;
    setId(id);
    setName(name);
    setSurname(surname);
    setMail(mail);
    setImage(imageURI);
  }

  public void setGivenCourses(ArrayList<GivenCourses> givenCourses) {
    this.givenCourses = givenCourses;
  }
}
