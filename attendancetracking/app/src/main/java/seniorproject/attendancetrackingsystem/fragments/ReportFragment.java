package seniorproject.attendancetrackingsystem.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.DatabaseManager;
import seniorproject.attendancetrackingsystem.helpers.SessionManager;

/* A simple {@link Fragment} subclass. */
public class ReportFragment extends Fragment {
  private final SimpleDateFormat simpleDateFormat =
      new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
  private Handler handler;
  private ArrayList<Taken_Lecture_Row> takenLectures = new ArrayList<>();
  private ArrayList<String> courses = new ArrayList<>();
  private Spinner course_spinner;
  private ArrayAdapter<String> course_adapter;
  private FrameLayout calendar_holder;
  private ArrayList<CalendarColumn> columnList = new ArrayList<>();

  public ReportFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View root = inflater.inflate(R.layout.fragment_report, container, false);
    return root;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    handler = new Handler(Looper.getMainLooper());
    course_spinner = view.findViewById(R.id.lecturelist);
    course_adapter =
        new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.spinner_item2, courses);
    course_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    calendar_holder = view.findViewById(R.id.cal_container);
    course_spinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            getAttendanceInformation(takenLectures.get(position));
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {
            setVisibility(false);
          }
        });
    getCourses();
  }

  private void setVisibility(final boolean state) {
    handler.post(
        new Runnable() {
          @Override
          public void run() {
            if (state) calendar_holder.setVisibility(View.VISIBLE);
            else calendar_holder.setVisibility(View.INVISIBLE);
          }
        });
  }

  private void toastWithHandle(final String message) {
    handler.post(
        new Runnable() {
          @Override
          public void run() {
            Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT)
                .show();
          }
        });
  }

  private int isExistsInDisctinctList(ArrayList<CalendarColumn> arr, CalendarColumn x) {
    int index = -1;
    for (CalendarColumn y : arr) {
      index++;
      if (y.date.equals(x.date)) return index;
    }
    return -1;
  }

  private void addClone(ArrayList<CalendarColumn> list, CalendarColumn x) {
    CalendarColumn temp = new CalendarColumn(x.date, x.hour, x.status, x.course_code);
    list.add(temp);
  }

  private void getCalendar() {
    setVisibility(true);
    final CaldroidFragment caldroidFragment = new CaldroidFragment();
    Bundle args = new Bundle();
    args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
    args.putBoolean(CaldroidFragment.ENABLE_CLICK_ON_DISABLED_DATES, true);
    ColorDrawable green = new ColorDrawable(getResources().getColor(R.color.stateGreen));
    ColorDrawable red = new ColorDrawable(getResources().getColor(R.color.stateRed));
    ColorDrawable yellow = new ColorDrawable(getResources().getColor(R.color.stateYellow));

    ArrayList<CalendarColumn> distinctList = new ArrayList<>();

    for (CalendarColumn x : columnList) {
      int index = isExistsInDisctinctList(distinctList, x);
      if (index == -1) addClone(distinctList, x);
      else {
        if (distinctList.get(index).status != x.status) {
          if (distinctList.get(index).status >= 1 || x.status >= 1)
            distinctList.get(index).status = 1;
        }
      }
    }

    for (CalendarColumn x : distinctList) {
      try {
        if (x.status == 0)
          caldroidFragment.setBackgroundDrawableForDate(red, (simpleDateFormat).parse(x.date));
        else if (x.status == 1)
          caldroidFragment.setBackgroundDrawableForDate(yellow, simpleDateFormat.parse(x.date));
        else if (x.status == 2)
          caldroidFragment.setBackgroundDrawableForDate(green, simpleDateFormat.parse(x.date));
      } catch (ParseException e) {
        // do nothing
      }
    }
    final CaldroidListener listener =
        new CaldroidListener() {

          @Override
          public void onSelectDate(Date date, View view) {
            try {
              for (CalendarColumn x : columnList) {
                Date d = simpleDateFormat.parse(x.date);
                if (d.compareTo(date) == 0) {
                  buildAndShowAlert(date, x);
                  return;
                }
              }
            } catch (ParseException e) {
              // do nothing
            }
          }
        };
    caldroidFragment.setCaldroidListener(listener);
    caldroidFragment.setArguments(args);
    getActivity()
        .getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.cal_container, caldroidFragment)
        .commit();
  }

  private void buildAndShowAlert(final Date date, final CalendarColumn course) {
    handler.post(
        new Runnable() {
          @Override
          public void run() {
            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT).create();
            String title = course.course_code + " Information";
            alertDialog.setTitle(title);
            StringBuilder message = new StringBuilder();
            try {
              for (CalendarColumn x : columnList) {
                Date d = simpleDateFormat.parse(x.date);
                if (d.compareTo(date) == 0) {
                  String info = x.date + " " + x.hour + " ";
                  if (x.status == 2) info = info + "Attended";
                  else if (x.status == 1) info = info + "Nearly Attended";
                  else if (x.status == 0) info = info + "Absent";
                  message.append(info).append("\n");
                }
              }
              alertDialog.setMessage(message.toString());
              alertDialog.setButton(
                  DialogInterface.BUTTON_NEGATIVE,
                  "Close",
                  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      alertDialog.dismiss();
                    }
                  });
              alertDialog.setCanceledOnTouchOutside(false);
              alertDialog.show();
            } catch (ParseException e) {
              // do nothing
            }
          }
        });
  }

  private void getCourses() {
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
                  if (!result) {
                    toastWithHandle(jsonObject.getString("message"));
                    setVisibility(false);
                  }
                } catch (JSONException e) {
                  // do nothing
                }

                try {
                  JSONArray jsonArray = new JSONArray(response);
                  takenLectures.clear();
                  for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Taken_Lecture_Row taken_lecture_row =
                        new Taken_Lecture_Row(
                            jsonObject.getInt("course_id"),
                            jsonObject.getString("course_code"),
                            jsonObject.getInt("section"));
                    takenLectures.add(taken_lecture_row);
                    courses.clear();
                    for (Taken_Lecture_Row x : takenLectures) {
                      courses.add(x.course_code);
                    }
                    Parcelable state = course_spinner.onSaveInstanceState();
                    course_spinner.setAdapter(course_adapter);
                    course_spinner.onRestoreInstanceState(state);
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
            params.put(
                "user_id",
                new SessionManager(getActivity().getApplicationContext())
                    .getUserDetails()
                    .get(SessionManager.KEY_USER_ID));
            params.put("operation", "taken-lectures");
            return params;
          }
        };
    try {
      DatabaseManager.getmInstance(getActivity().getApplicationContext()).execute(request);
    } catch (NullPointerException e) {
      // do nothing
    }
  }

  private void getAttendanceInformation(final Taken_Lecture_Row takenLectureRow) {
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
                  if (!result) {
                    toastWithHandle(jsonObject.getString("message"));
                  }
                } catch (JSONException e) {
                  // do nothing
                }
                try {
                  JSONArray jsonArray = new JSONArray(response);
                  columnList.clear();
                  for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    CalendarColumn column =
                        new CalendarColumn(
                            jsonObject.getString("date"),
                            jsonObject.getString("hour"),
                            jsonObject.getInt("status"),
                            jsonObject.getString("course_code"));
                    columnList.add(column);
                  }
                  getCalendar();
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
            params.put("operation", "attendance-info-calendar");
            params.put("course_id", String.valueOf(takenLectureRow.course_id));
            params.put("section", String.valueOf(takenLectureRow.section));
            params.put(
                "user_id",
                new SessionManager(getActivity().getApplicationContext())
                    .getUserDetails()
                    .get(SessionManager.KEY_USER_ID));
            return params;
          }
        };
    try {
      DatabaseManager.getmInstance(getActivity().getApplicationContext()).execute(request);
    } catch (NullPointerException e) {
      // do nothing
    }
  }

  class Taken_Lecture_Row {
    int course_id;
    String course_code;
    int section;

    Taken_Lecture_Row(int course_id, String course_code, int section) {
      this.course_code = course_code;
      this.course_id = course_id;
      this.section = section;
    }
  }

  class CalendarColumn {
    String date;
    String hour;
    int status;
    String course_code;

    CalendarColumn(String date, String hour, int status, String course_code) {
      this.date = date;
      this.hour = hour;
      if (status == 3) status = 2;
      this.status = status;
      this.course_code = course_code;
    }
  }
}
