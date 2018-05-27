package seniorproject.attendancetrackingsystem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.instacart.library.truetime.TrueTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.DatabaseManager;
import seniorproject.attendancetrackingsystem.helpers.JsonHelper;
import seniorproject.attendancetrackingsystem.helpers.SessionManager;
import seniorproject.attendancetrackingsystem.utils.Schedule;

public class SelectCurrentCourse extends AppCompatActivity {
  private SessionManager sessionManager;
  private Schedule schedule;
  private ArrayList<Schedule.CourseInfo> currentCourses = new ArrayList<>();
  private ArrayList<String> courses = new ArrayList<>();
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
private Handler handler;
private ListView listView;
private ArrayAdapter<String> adapter;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_select_current_course);
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3"));
    handler = new Handler(getMainLooper());
    listView = findViewById(R.id.course_list);
    adapter = new ArrayAdapter<>(
            getApplicationContext(), R.layout.spinner_item, courses);

    sessionManager = new SessionManager(getApplicationContext());
    updateSchedule();
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        sessionManager.setSelectedCourse(currentCourses.get(position));
        sessionManager.setIsCourseSelected(true);
        startActivity(new Intent(SelectCurrentCourse.this, StudentActivity.class));
      }
    });
  }

  private void updateSchedule() {
    StringRequest request =
        new StringRequest(
            Request.Method.POST,
            DatabaseManager.GetOperations,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {


                  schedule = JsonHelper.getInstance(getApplicationContext()).parseSchedule(response);
               try{
                 getCurrentCourses(dateFormat.parse(dateFormat.format(TrueTime.now())));
               }catch (ParseException e){
                 e.printStackTrace();
               }
                addCourses();
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {}
            }) {
          @Override
          protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<>();
            params.put("user_id", sessionManager.getUserDetails().get(SessionManager.KEY_USER_ID));
            params.put("operation", "schedule");
            return params;
          }
        };
    DatabaseManager.getInstance(getApplicationContext()).execute(request);
  }

  private void getCurrentCourses(Date currentTime) {
    currentCourses.clear();
    if (schedule == null) return;
    for (Schedule.CourseInfo x : schedule.getCourses()) {
      String start = x.getHour();
      String end = x.getEnd_hour();
      try {
        if (currentTime.compareTo(dateFormat.parse(start)) >= 0
                && currentTime.compareTo(dateFormat.parse(end)) < 0) {
          currentCourses.add(x);
        }
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
  }

  private void addCourses(){
    courses.clear();
    for(Schedule.CourseInfo x : currentCourses){
      courses.add(x.getCourse_code() + " - " + x.getSection());
    }
    setAdapter();
  }

  private void setAdapter(){
    handler.post(new Runnable() {
      @Override
      public void run() {
        listView.setAdapter(adapter);
      }
    });
  }
}
