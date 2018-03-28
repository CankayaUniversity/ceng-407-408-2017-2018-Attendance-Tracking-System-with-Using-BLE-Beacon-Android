package seniorproject.attendancetrackingsystem.helpers;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import seniorproject.attendancetrackingsystem.utils.Course;
import seniorproject.attendancetrackingsystem.utils.Department;

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
}
