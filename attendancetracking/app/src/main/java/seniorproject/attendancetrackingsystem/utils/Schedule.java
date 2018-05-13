package seniorproject.attendancetrackingsystem.utils;

import java.io.Serializable;
import java.util.ArrayList;

public class Schedule implements Serializable {
  private ArrayList<CourseInfo> courses;

  public Schedule() {
    courses = new ArrayList<>();
  }

  public ArrayList<CourseInfo> getCourses() {
    return courses;
  }

  public void setCourses(ArrayList<CourseInfo> courses) {
    this.courses = courses;
  }

  public void add(
      int course_id,
      int section,
      String week_day,
      String hour,
      String beacon_mac,
      String course_code,
      int classroom_id) {
    CourseInfo newCourse =
        new CourseInfo(course_id, section, week_day, hour, beacon_mac, course_code, classroom_id);
    courses.add(newCourse);
  }

  public void add(CourseInfo courseInfo) {
    courses.add(courseInfo);
  }

  public class CourseInfo implements Serializable {
    private int course_id;
    private int section;
    private String week_day;
    private String hour;
    private String beacon_mac;
    private String course_code;
    private String end_hour;
    private int classroom_id;

    public CourseInfo(
        int course_id,
        int section,
        String week_day,
        String hour,
        String beacon_mac,
        String course_code,
        int classroom_id) {
      this.course_id = course_id;
      this.section = section;
      this.week_day = week_day;
      this.hour = hour;
      this.beacon_mac = beacon_mac;
      this.course_code = course_code;
      this.end_hour = this.hour.substring(0, 2);
      this.end_hour = String.valueOf(Integer.parseInt(this.end_hour) + 1) + ":10";
      this.classroom_id = classroom_id;
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

    public String getWeek_day() {
      return week_day;
    }

    public void setWeek_day(String week_day) {
      this.week_day = week_day;
    }

    public String getHour() {
      return hour;
    }

    public void setHour(String hour) {
      this.hour = hour;
    }

    public String getBeacon_mac() {
      return beacon_mac;
    }

    public void setBeacon_mac(String beacon_mac) {
      this.beacon_mac = beacon_mac;
    }

    public String getCourse_code() {
      return course_code;
    }

    public void setCourse_code(String course_code) {
      this.course_code = course_code;
    }

    public String getEnd_hour() {
      return end_hour;
    }

    public int getClassroom_id() {
      return classroom_id;
    }

    public void setClassroom_id(int classroom_id) {
      this.classroom_id = classroom_id;
    }
  }
}
