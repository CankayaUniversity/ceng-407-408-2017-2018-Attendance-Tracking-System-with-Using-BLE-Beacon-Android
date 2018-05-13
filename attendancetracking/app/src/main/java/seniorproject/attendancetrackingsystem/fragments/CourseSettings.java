package seniorproject.attendancetrackingsystem.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.DatabaseManager;
import seniorproject.attendancetrackingsystem.helpers.SessionManager;

public class CourseSettings extends Fragment {

  private static final String NEARLY_ATTENDED = "Nearly Attended ";
  private static final String FULLY_ATTENDED = "Fully Attended ";
  private Spinner course_spinner;
  private ArrayList<String> courses;
  private ArrayAdapter<String> adapter;
  private ArrayList<Given_Lecture_Row> given_lectures = new ArrayList<>();
  private Handler handler;
  private LinearLayout seek_layout;
  private double nearly = 0;
  private double fully = 70;
  private SeekBar middle_seek;
  private SeekBar attended_seek;
  private TextView near_text;
  private TextView full_text;
  private int pre_middle;
  private int pre_full;
  private int current_course_id;

  public CourseSettings() {}

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.course_settings, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    handler = new Handler(Looper.getMainLooper());
    near_text = view.findViewById(R.id.near_text);
    full_text = view.findViewById(R.id.full_text);
    seek_layout = view.findViewById(R.id.setting_layout);
    setLayoutVisibility(false);
    middle_seek = view.findViewById(R.id.nearly);
    middle_seek.setMax(100);
    attended_seek = view.findViewById(R.id.attended);
    attended_seek.setMax(100);
    course_spinner = view.findViewById(R.id.lecturelist);
    Button save_button = view.findViewById(R.id.save_btn);
    save_button.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (current_course_id != 0) save();
          }
        });
    courses = new ArrayList<>();
    adapter =
        new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.spinner_item, courses);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    get_course_list();

    middle_seek.setOnSeekBarChangeListener(
        new SeekBar.OnSeekBarChangeListener() {
          @Override
          public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int full = attended_seek.getProgress();
            set_preconditions(progress, full);
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {}

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    attended_seek.setOnSeekBarChangeListener(
        new SeekBar.OnSeekBarChangeListener() {
          @Override
          public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int middle = middle_seek.getProgress();
            set_preconditions(middle, progress);
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {}

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    course_spinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            current_course_id = given_lectures.get(position).course_id;
            get_preconditions(current_course_id);
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {
            setLayoutVisibility(false);
          }
        });
  }

  private void setLayoutVisibility(final boolean state) {
    handler.post(
        new Runnable() {
          @Override
          public void run() {
            if (state) seek_layout.setVisibility(View.VISIBLE);
            else {
              seek_layout.setVisibility(View.INVISIBLE);
              current_course_id = 0;
            }
          }
        });
  }

  private void toastWithHandle(final String text) {
    handler.post(
        new Runnable() {
          @Override
          public void run() {
            Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_SHORT).show();
          }
        });
  }

  private void save() {
    final int near = middle_seek.getProgress();
    final int full = attended_seek.getProgress();

    StringRequest request =
        new StringRequest(
            Request.Method.POST,
            DatabaseManager.SetOperations,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
                try {
                  JSONObject jsonObject = new JSONObject(response);
                  boolean result = jsonObject.getBoolean("success");
                  if (result) {
                    toastWithHandle("Course settings has been updated");
                  } else {
                    toastWithHandle(jsonObject.getString("message"));
                  }
                } catch (JSONException e) {
                  e.printStackTrace();
                }
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {}
            }) {
          @Override
          protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<>();
            params.put("operation", "preconditions");
            params.put("course_id", String.valueOf(current_course_id));
            params.put("middle", String.valueOf(near));
            params.put("attended", String.valueOf(full));

            return params;
          }
        };

    DatabaseManager.getmInstance(getActivity().getApplicationContext()).execute(request);
  }

  private void set_preconditions(double near, double full) {
    if (near > full) {
      return;
    }
    if (full * 0.8 < near) {
      near = Math.floor(full * 0.8);
    }
    middle_seek.setProgress((int) near);
    attended_seek.setProgress((int) full);
    String n = NEARLY_ATTENDED + ": " + String.valueOf(near) + "%";
    String f = FULLY_ATTENDED + ": " + String.valueOf(full) + "%";
    near_text.setText(n);
    full_text.setText(f);
    pre_full = (int) full;
    pre_middle = (int) near;
  }

  private void get_preconditions(final int course_id) {
    StringRequest request =
        new StringRequest(
            Request.Method.POST,
            DatabaseManager.GetOperations,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
                try {
                  JSONObject jsonObject = new JSONObject(response);
                  boolean result = jsonObject.getBoolean("success");
                  if (result) {
                    nearly = jsonObject.getDouble("middle");
                    fully = jsonObject.getDouble("attended");
                    set_preconditions(nearly, fully);
                  }
                } catch (JSONException e) {
                  e.printStackTrace();
                }
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {}
            }) {
          @Override
          protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<>();
            params.put("course_id", String.valueOf(course_id));
            params.put("operation", "preconditions");
            return params;
          }
        };

    DatabaseManager.getmInstance(getActivity().getApplicationContext()).execute(request);
  }

  private void get_course_list() {
    StringRequest request =
        new StringRequest(
            Request.Method.POST,
            DatabaseManager.GetOperations,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
                given_lectures.clear();
                try {
                  JSONObject jsonObject = new JSONObject(response);
                  boolean result = jsonObject.getBoolean("success");
                  if (!result) {
                    toastWithHandle(jsonObject.getString("message"));
                    setLayoutVisibility(false);
                  }
                } catch (JSONException e) {
                  // do nothing
                }
                try {
                  JSONArray jsonArray = new JSONArray(response);
                  for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Given_Lecture_Row given_lecture_row =
                        new Given_Lecture_Row(
                            jsonObject.getInt("course_id"), jsonObject.getString("course_code"));
                    given_lectures.add(given_lecture_row);
                  }
                  courses.clear();
                  for (Given_Lecture_Row x : given_lectures) {
                    courses.add(x.course_code);
                  }
                  course_spinner.setAdapter(adapter);
                  setLayoutVisibility(true);
                } catch (JSONException e) {
                  e.printStackTrace();
                  setLayoutVisibility(false);
                }
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {}
            }) {
          @Override
          protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<>();
            params.put(
                "user_id",
                new SessionManager(getActivity().getApplicationContext())
                    .getUserDetails()
                    .get(SessionManager.KEY_USER_ID));
            params.put("operation", "given-lectures");
            return params;
          }
        };
    DatabaseManager.getmInstance(getActivity().getApplicationContext()).execute(request);
  }

  class Given_Lecture_Row {
    int course_id;
    String course_code;

    Given_Lecture_Row(int course_id, String course_code) {
      this.course_code = course_code;
      this.course_id = course_id;
    }
  }
}
