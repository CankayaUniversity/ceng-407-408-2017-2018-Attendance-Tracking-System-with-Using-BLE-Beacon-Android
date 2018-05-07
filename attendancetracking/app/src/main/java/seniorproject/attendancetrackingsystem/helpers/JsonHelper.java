package seniorproject.attendancetrackingsystem.helpers;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import seniorproject.attendancetrackingsystem.utils.Actor;
import seniorproject.attendancetrackingsystem.utils.Course;
import seniorproject.attendancetrackingsystem.utils.Department;
import seniorproject.attendancetrackingsystem.utils.GivenCourses;
import seniorproject.attendancetrackingsystem.utils.Lecturer;
import seniorproject.attendancetrackingsystem.utils.Schedule;
import seniorproject.attendancetrackingsystem.utils.Student;
import seniorproject.attendancetrackingsystem.utils.TakenCourses;

public class JsonHelper {
  private static JsonHelper mInstance;
  private static Context context;

  private JsonHelper(Context context) {
    JsonHelper.context = context;
  }

  /** Synchronize the JsonHelper object to make it common for whole activity. */
  public static synchronized JsonHelper getmInstance(Context context) {
    if (mInstance == null) {
      mInstance = new JsonHelper(context);
    }
    return mInstance;
  }

  public ArrayList<Department> parseDepartmentList(String jsonString) {
    ArrayList<Department> arrayList = new ArrayList<>();
    try {
      JSONArray jsonArray = new JSONArray(jsonString);
      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        Department tempObject =
            new Department(
                jsonObject.getInt("department_id"),
                jsonObject.getString("abbreviation"),
                jsonObject.getString("department_name"));
        arrayList.add(tempObject);
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return arrayList;
  }

  public ArrayList<Course> parseCourseList(String jsonString) {
    ArrayList<Course> arrayList = new ArrayList<>();
    try {
      JSONArray jsonArray = new JSONArray(jsonString);
      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        Course tempObject =
            new Course(
                jsonObject.getInt("course_id"),
                jsonObject.getString("course_name"),
                jsonObject.getString("course_code"),
                jsonObject.getInt("section_number"),
                jsonObject.getInt("department_id"));
        arrayList.add(tempObject);
      }

    } catch (JSONException e) {
      e.printStackTrace();
    }
    return arrayList;
  }

  public Actor parseUser(String jsonString) {
    Actor actor = null;
    try {
      JSONObject jsonObject = new JSONObject(jsonString);
      if (jsonObject.getString("user_type").equals("student")) {
        actor =
            new Student(
                jsonObject.getInt("student_id"),
                jsonObject.getInt("student_number"),
                jsonObject.getString("name"),
                jsonObject.getString("surname"),
                jsonObject.getString("bluetooth_mac"),
                jsonObject.getString("mail_address"));
      } else if (jsonObject.getString("user_type").equals("lecturer")) {
        actor =
            new Lecturer(
                jsonObject.getInt("lecturer_id"),
                jsonObject.getString("name"),
                jsonObject.getString("surname"),
                jsonObject.getString("mail_address"),
                jsonObject.getInt("department_id"));
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return actor;
  }

  public ArrayList<TakenCourses> parseTakenCourses(String jsonString) {
    ArrayList<TakenCourses> arrayList = new ArrayList<>();
    try {
      JSONArray jsonArray = new JSONArray(jsonString);
      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        TakenCourses tempTakenCourse =
            new TakenCourses(jsonObject.getInt("course_id"), jsonObject.getInt("section"));
        arrayList.add(tempTakenCourse);
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return arrayList;
  }

public ArrayList<GivenCourses> parseGivenCourses(String jsonString){
    ArrayList<GivenCourses> arrayList = new ArrayList<>();
    try{
      JSONArray jsonArray = new JSONArray(jsonString);
      for(int i = 0; i < jsonArray.length(); i++){
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        GivenCourses tempGivenCourse = new GivenCourses(jsonObject.getInt("course_id"));
        arrayList.add(tempGivenCourse);
      }
    }catch (JSONException e){
      e.printStackTrace();
    }
    return arrayList;
}
  public Schedule parseSchedule(String jsonString) {
    Schedule schedule = new Schedule();
    try {
      JSONArray jsonArray = new JSONArray(jsonString);
      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        schedule.add(jsonObject.getInt("course_id"),jsonObject.getInt("section"),
                jsonObject.getString("week_day"), jsonObject.getString("hour"),
                jsonObject.getString("beacon_mac"),jsonObject.getString("course_code"),
                jsonObject.getInt("classroom_id"));
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return schedule;
  }
}
