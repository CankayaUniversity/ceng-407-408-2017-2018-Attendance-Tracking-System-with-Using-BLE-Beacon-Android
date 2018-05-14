package seniorproject.attendancetrackingsystem.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.Timer;
import java.util.TimerTask;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.DatabaseManager;
import seniorproject.attendancetrackingsystem.helpers.SessionManager;

/* A simple {@link Fragment} subclass. */
public class ReportFragmentLecturer extends Fragment {
  private ListView listView;
  private ArrayList<Integer> classrooms = new ArrayList<>();
  private Handler handler;
  private ArrayList<StudentRow> studentList = new ArrayList<>();
  private StudentAdapter adapter;
  private Timer timer;
  private ArrayList<Given_Lectures_Row> givenLectures = new ArrayList<>();
  private ArrayList<String> courses = new ArrayList<>();
  private ArrayAdapter<String> course_adapter;
  private Spinner course_spinner;
  private TextView courseTxt;
  private FrameLayout calendar_hoder;
  private ArrayList<CalendarColumn> calendarColumns = new ArrayList<>();
  private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);

  public ReportFragmentLecturer() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_report_lecturer, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    listView = view.findViewById(R.id.studentlist);
    course_spinner = view.findViewById(R.id.lecturelist);
    courseTxt = view.findViewById(R.id.course_select);
    calendar_hoder = view.findViewById(R.id.cal_container);
    course_adapter =
        new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.spinner_item2, courses);
    course_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    handler = new Handler(Looper.getMainLooper());
    timer = new Timer();
    Bundle args = getArguments();
    if (args != null) {
      classrooms = args.getIntegerArrayList("classrooms");

      adapter = new StudentAdapter(getActivity(), R.layout.liststudent, studentList);
      Parcelable state = listView.onSaveInstanceState();
      listView.setAdapter(adapter);
      listView.onRestoreInstanceState(state);
      changeVisiblity(course_spinner, false);
      changeVisiblity(courseTxt, false);
      changeVisiblity(calendar_hoder, false);
      changeVisiblity(listView, true);
      studentListListener();
    } else {
      changeVisiblity(listView, false);
      changeVisiblity(course_spinner, true);
      changeVisiblity(courseTxt, true);
      fillCourseList();
    }

    course_spinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            fillCalendar(givenLectures.get(position));
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {
            changeVisiblity(calendar_hoder, false);
          }
        });
  }

  private void changeVisiblity(final View v, final boolean state) {
    handler.post(
        new Runnable() {
          @Override
          public void run() {
            if (state) v.setVisibility(View.VISIBLE);
            else v.setVisibility(View.INVISIBLE);
          }
        });
  }

  private void getCalendar() {
    changeVisiblity(calendar_hoder, true);
    final CaldroidFragment caldroidFragment = new CaldroidFragment();
    Bundle args = new Bundle();
    args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
    args.putBoolean(CaldroidFragment.ENABLE_CLICK_ON_DISABLED_DATES, true);
    ColorDrawable blue =
        new ColorDrawable(getResources().getColor(R.color.caldroid_holo_blue_light));
    try {
      for (CalendarColumn x : calendarColumns) {
        caldroidFragment.setBackgroundDrawableForDate(blue, simpleDateFormat.parse(x.date));
      }
    } catch (ParseException e) {
      // do nothing
    }
    final CaldroidListener listener =
        new CaldroidListener() {

          @Override
          public void onSelectDate(Date date, View view) {
            try {
              for (CalendarColumn x : calendarColumns) {
                Date d = simpleDateFormat.parse(x.date);
                if (d.compareTo(date) == 0) {
                  buildAndShowAlert(date);
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

  private void fillCalendar(final Given_Lectures_Row lecture) {
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
                    toastWithHandler(jsonObject.getString("message"));
                    return;
                  }
                } catch (JSONException e) {
                  // do nothing
                }
                try {
                  JSONArray jsonArray = new JSONArray(response);
                  calendarColumns.clear();
                  for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    CalendarColumn column =
                        new CalendarColumn(
                            jsonObject.getInt("classroom_id"),
                            jsonObject.getString("course_code"),
                            jsonObject.getString("date"),
                            jsonObject.getString("hour"));
                    calendarColumns.add(column);
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
            params.put("course_id", String.valueOf(lecture.course_id));
            params.put("section", String.valueOf(lecture.section));
            params.put("operation", "classrooms");
            return params;
          }
        };
    try {
      DatabaseManager.getmInstance(getActivity().getApplicationContext()).execute(request);
    } catch (NullPointerException e) {
      // do nothing
    }
  }

  private void showClassroom(int classroom_id) {
    Bundle args = new Bundle();
    ArrayList<Integer> classes = new ArrayList<>();
    classes.add(classroom_id);
    args.putIntegerArrayList("classrooms", classes);
    ReportFragmentLecturer f = new ReportFragmentLecturer();
    BottomNavigationView mainNav = getActivity().findViewById(R.id.main_nav);
    mainNav.setSelectedItemId(R.id.nav_report);
    f.setArguments(args);
    getFragmentManager().beginTransaction().replace(R.id.main_frame, f).commit();
  }

  private void cancelClassroom(int classroom_id) {
    // TODO cancel classroom
  }

  private void buildAndShowAlert(final Date date) {
    handler.post(
        new Runnable() {
          @Override
          public void run() {
            final AlertDialog alertDialog =
                new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT).create();
            LinearLayout linearLayout = new LinearLayout(getActivity());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            try {
              for (final CalendarColumn x : calendarColumns) {
                Date d = simpleDateFormat.parse(x.date);
                if (d.compareTo(date) == 0) {
                  alertDialog.setTitle(x.course_code + " " + simpleDateFormat.format(date));
                  LinearLayout horizan = new LinearLayout(getActivity());
                  horizan.setPadding(0, 5, 0, 5);
                  horizan.setGravity(Gravity.CENTER);
                  horizan.setOrientation(LinearLayout.HORIZONTAL);

                  TextView text = new TextView(getActivity());
                  text.setTextColor(Color.BLACK);
                  text.setText(x.hour);
                  text.setTextSize(16);
                  text.setPadding(0, 0, 40, 0);
                  text.setTextColor(getResources().getColor(R.color.caldroid_holo_blue_dark));
                  horizan.addView(text);

                  Button btn = new Button(getActivity());
                  btn.setText("Show");
                  btn.setBackgroundColor(getResources().getColor(R.color.caldroid_holo_blue_dark));
                  btn.setOnClickListener(
                      new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                          showClassroom(x.classroom_id);
                          alertDialog.dismiss();
                        }
                      });
                  horizan.addView(btn);

                  btn = new Button(getActivity());
                  btn.setText("Cancel");
                  btn.setBackgroundColor(getResources().getColor(R.color.caldroid_light_red));
                  btn.setOnClickListener(
                      new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                          cancelClassroom(x.classroom_id);
                          alertDialog.dismiss();
                        }
                      });
                  horizan.addView(btn);

                  linearLayout.addView(horizan);
                }
              }
            } catch (ParseException e) {
              // do nothing
            }

            alertDialog.setView(linearLayout);
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
          }
        });
  }

  private void studentListListener() {
    timer.scheduleAtFixedRate(
        new TimerTask() {
          @Override
          public void run() {
            for (int i = 0; i < classrooms.size(); i++) {
              getStudentList(classrooms.get(i));
            }
          }
        },
        0,
        10000); // 2 minutes
  }

  private void toastWithHandler(final String text) {
    handler.post(
        new Runnable() {
          @Override
          public void run() {
            Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_SHORT).show();
          }
        });
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    timer.cancel();
  }

  private void getStudentList(final int classroom_id) {
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
                    toastWithHandler(jsonObject.getString("message"));
                    return;
                  }
                } catch (JSONException e) {
                  // do nothing
                }
                try {
                  JSONArray jsonArray = new JSONArray(response);
                  studentList.clear();
                  for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    StudentRow studentRow =
                        new StudentRow(
                            jsonObject.getString("name") + " " + jsonObject.getString("surname"),
                            jsonObject.getInt("student_number"),
                            jsonObject.getInt("status"),
                            jsonObject.getInt("time"));
                    studentList.add(studentRow);
                  }
                  Parcelable state = listView.onSaveInstanceState();
                  listView.setAdapter(adapter);
                  listView.onRestoreInstanceState(state);
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
            params.put("classroom_id", String.valueOf(classroom_id));
            params.put("operation", "attendance-list");
            return params;
          }
        };
    try {
      DatabaseManager.getmInstance(getActivity().getApplicationContext()).execute(request);
    } catch (NullPointerException e) {
      // Do nothing
    }
  }

  private void fillCourseList() {
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
                    toastWithHandler(jsonObject.getString("message"));
                    return;
                  }
                } catch (JSONException e) {
                  // do nothing
                }

                try {
                  JSONArray jsonArray = new JSONArray(response);
                  givenLectures.clear();
                  courses.clear();
                  for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Given_Lectures_Row temp =
                        new Given_Lectures_Row(
                            jsonObject.getInt("course_id"),
                            jsonObject.getString("course_code"),
                            jsonObject.getInt("section"));
                    givenLectures.add(temp);
                    String course = temp.course_code + " - " + temp.section;
                    courses.add(course);
                  }
                  Parcelable state = course_spinner.onSaveInstanceState();
                  course_spinner.setAdapter(course_adapter);
                  course_spinner.onRestoreInstanceState(state);
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
            params.put("operation", "given-lectures-seperate-sections");
            return params;
          }
        };
    try {
      DatabaseManager.getmInstance(getActivity().getApplicationContext()).execute(request);
    } catch (NullPointerException e) {
      // do nothing
    }
  }

  class Given_Lectures_Row {
    int course_id;
    String course_code;
    int section;

    public Given_Lectures_Row(int course_id, String course_code, int section) {
      this.course_code = course_code;
      this.course_id = course_id;
      this.section = section;
    }
  }

  class CalendarColumn {
    String date;
    String hour;
    int classroom_id;
    String course_code;

    CalendarColumn(int classroom_id, String course_code, String date, String hour) {
      this.classroom_id = classroom_id;
      this.course_code = course_code;
      this.date = date;
      this.hour = hour;
    }
  }

  public class StudentRow {
    public String name;
    public int number;
    public int state;
    public int time;

    public StudentRow(String name, int number, int state, int time) {
      this.name = name;
      this.number = number;
      this.state = state;
      this.time = time;
    }

    public StudentRow() {
      super();
    }
  }

  public class StudentAdapter extends ArrayAdapter<StudentRow> {
    Context context;
    int layoutResourseId;
    ArrayList<StudentRow> data;

    private StudentAdapter(Context context, int layoutResourseId, ArrayList<StudentRow> data) {
      super(context, layoutResourseId, data);
      this.layoutResourseId = layoutResourseId;
      this.context = context;
      this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
      View row = convertView;
      StudentHolder holder = null;

      if (row == null) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourseId, parent, false);

        holder = new StudentHolder();
        holder.txtName = row.findViewById(R.id.studentName);
        holder.txtNumber = row.findViewById(R.id.studentNo);
        holder.txtLineNum = row.findViewById(R.id.lineNum);


        row.setTag(holder);
      } else {
        holder = (StudentHolder) row.getTag();
      }

      StudentRow student = data.get(position);

      //String info = student.name + " [" + student.time / 60000 + " m]";
      holder.txtName.setText(student.name);
      holder.txtNumber.setText(String.valueOf(student.number));
      holder.txtLineNum.setText(String.valueOf(position+1));
      if (student.state == 0) row.setBackgroundColor(getResources().getColor(R.color.stateRed));
      else if (student.state == 1)
        row.setBackgroundColor(getResources().getColor(R.color.stateYellow));
      else if (student.state == 2 || student.state == 3)
        row.setBackgroundColor(getResources().getColor(R.color.stateGreen));

      return row;
    }

    class StudentHolder {
      TextView txtNumber;
      TextView txtName;
      TextView txtLineNum;
    }
  }
}
