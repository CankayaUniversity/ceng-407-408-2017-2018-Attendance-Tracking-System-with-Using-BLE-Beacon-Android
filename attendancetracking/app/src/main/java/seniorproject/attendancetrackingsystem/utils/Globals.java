package seniorproject.attendancetrackingsystem.utils;

import android.app.Application;

import java.util.ArrayList;

public class Globals extends Application {
  private ArrayList<Department> departments;
  private ArrayList<Course> courses;
  private Actor loggedUser;

  public ArrayList<Department> getDepartments() {
    return departments;
  }

  public void setDepartments(ArrayList<Department> departments) {
    this.departments = departments;
  }

  public ArrayList<Course> getCourses() {
    return courses;
  }

  public void setCourses(ArrayList<Course> courses) {
    this.courses = courses;
  }

  public Actor getLoggedUser() {
    return loggedUser;
  }

  public void setLoggedUser(Actor loggedUser) {
    this.loggedUser = loggedUser;
  }
}
