package seniorproject.attendancetrackingsystem.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
  private ScrollView scroll_report;
  private LinearLayout generalReport;
  private FrameLayout calendar_hoder;
  private boolean secure_list = false;
  private Dialog popup;

  private TextView totalstudent;
  private TextView attendedstudent;
  private TextView nearlystudent;
  private TextView absentstudent;

  private ArrayList<CalendarColumn> calendarColumns = new ArrayList<>();
  private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
  private int lastSelectedSection;

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

    totalstudent = view.findViewById(R.id.totalstudent);
    absentstudent = view.findViewById(R.id.absentstudent);
    attendedstudent = view.findViewById(R.id.attendedstudent);
    nearlystudent = view.findViewById(R.id.nearlystudent);

    listView = view.findViewById(R.id.studentlist);
    course_spinner = view.findViewById(R.id.lecturelist);
    calendar_hoder = view.findViewById(R.id.cal_container);
    generalReport = view.findViewById(R.id.general_report);
    scroll_report = view.findViewById(R.id.scroll_report);
    course_adapter =
        new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.spinner_item2, courses);
    course_adapter.setDropDownViewResource(R.layout.spinner_item2);
    handler = new Handler(Looper.getMainLooper());
    timer = new Timer();
    Bundle args = getArguments();
    if (args != null) {
      classrooms = args.getIntegerArrayList("classrooms");
      adapter = new StudentAdapter(getActivity(), R.layout.liststudent, studentList);
      Parcelable state = listView.onSaveInstanceState();
      listView.setAdapter(adapter);
      listView.onRestoreInstanceState(state);
      changeVisibility(listView, true);
      changeVisibility(scroll_report, false);
      changeVisibility(generalReport, false);
      studentListListener();
    } else {
      changeVisibility(listView, false);
      changeVisibility(scroll_report, true);
      fillCourseList();
    }

    course_spinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            lastSelectedSection = givenLectures.get(position).section;
            fillCalendar(givenLectures.get(position));
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {
            changeVisibility(calendar_hoder, false);
          }
        });
    listView.setOnItemClickListener(
        new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            popup = new Dialog(getActivity());
            popup.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            if (secure_list) popup.setContentView(R.layout.securepopup);
            else popup.setContentView(R.layout.popup);
            // getting UI elements
            TextView student_number = popup.findViewById(R.id.student_number);
            TextView student_name = popup.findViewById(R.id.student_name);
            TextView close = popup.findViewById(R.id.txtclose);
            TextView attended = popup.findViewById(R.id.attendedPercent);
            TextView nearly = popup.findViewById(R.id.nearlyPercent);
            TextView absent = popup.findViewById(R.id.absentPercent);
            ImageView avatar = popup.findViewById(R.id.avatar);
            String URL = "http://attendancesystem.xyz/attendancetracking/";
            final Button mark = popup.findViewById(R.id.mark);
            // getting student info
            final StudentRow student = studentList.get(position);
            // Calculating total lecture hour
            double totalLecture = student.absent + student.nearly + student.attended;
            // Calculating percentages
            try {
              double attendedPercent = (double) student.attended / totalLecture;
              double absentPercent = (double) student.absent / totalLecture;
              double nearlyPercent = (double) student.nearly / totalLecture;
              attendedPercent = attendedPercent * 100;
              absentPercent = absentPercent * 100;
              nearlyPercent = nearlyPercent * 100;
              // Formatting double numbers

              String at = createPercent(attendedPercent);
              String ab = createPercent(absentPercent);
              String nr = createPercent(nearlyPercent);
              // setting percentages
              attended.setText(at);
              nearly.setText(nr);
              absent.setText(ab);
            } catch (ArithmeticException e) {
              attended.setText("0%");
              nearly.setText("0%");
              absent.setText("0%");
            }
            // if student is already attended, make invisible mark as attended button
            if (student.state == 2) mark.setVisibility(View.INVISIBLE);
            else mark.setVisibility(View.VISIBLE);
            mark.setOnClickListener(
                new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                    markAsAttended(student.classroom_id, student.student_id);
                    popup.dismiss();
                  }
                });
            if (student.img == null || student.img.isEmpty()) {
              Picasso.with(popup.getContext())
                  .load(R.drawable.unknown_trainer)
                  .fit()
                  .centerCrop()
                  .into(avatar);
            } else {
              Picasso.with(popup.getContext())
                  .load(URL + student.img)
                  .fit()
                  .centerCrop()
                  .placeholder(R.drawable.unknown_trainer)
                  .into(avatar);
            }
            if (secure_list) {
              ImageView secure_image = popup.findViewById(R.id.secure_image);
              if (student.secure_img == null || student.secure_img.isEmpty()) {
                Picasso.with(popup.getContext())
                    .load(R.drawable.unknown_trainer)
                    .fit()
                    .into(secure_image);
              } else {
                Picasso.with(popup.getContext())
                    .load(URL + student.secure_img)
                    .fit()
                    .placeholder(R.drawable.unknown_trainer)
                    .into(secure_image);
              }
            } else {
              TextView time = popup.findViewById(R.id.time);
              String info =
                  "This student attended approximately " + student.time / 60000 + " minute(s)";
              time.setText(info);
            }
            student_number.setText(String.valueOf(student.number));
            student_name.setText(student.name);
            close.setOnClickListener(
                new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                    popup.dismiss();
                  }
                });
            popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popup.show();
          }
        });
  }

  private String createPercent(double number) {
    NumberFormat formatter = new DecimalFormat("#0.0");
    String result;
    String parts[] = formatter.format(number).split("[.,]");
    if (parts[1].equals("0")) result = String.valueOf((int) number) + "%";
    else {
      result = formatter.format(number) + "%";
      result = result.replace(',', '.');
    }
    return result;
  }

  private void markAsAttended(final int classroom_id, final int student_id) {
    final StringRequest request =
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
                    toastWithHandler("Student has been marked as attended");
                    getStudentList();
                  } else {
                    toastWithHandler(jsonObject.getString("message"));
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
            params.put("operation", "mark-as-attended");
            params.put("student_id", String.valueOf(student_id));
            params.put("classroom_id", String.valueOf(classroom_id));
            return params;
          }
        };
    try {
      DatabaseManager.getmInstance(getActivity().getApplicationContext()).execute(request);
    } catch (NullPointerException e) {
      // do nothing
    }
  }

  private void changeVisibility(final View v, final boolean state) {
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
    changeVisibility(generalReport, true);
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

  private void setInfo(final int done, final int taken, final double average) {
    handler.post(
        new Runnable() {
          @Override
          public void run() {
            TextView tLecture = getActivity().findViewById(R.id.total_lecture);
            TextView tStudent = getActivity().findViewById(R.id.total_student);
            TextView averInfo = getActivity().findViewById(R.id.attendance_percentage);
            String info = "Total Lecture: " + String.valueOf(done);
            tLecture.setText(info);
            info = "Registered Students: " + String.valueOf(taken);
            tStudent.setText(info);
            NumberFormat formatter = new DecimalFormat("#0.00");
            info = "Participation: " + formatter.format(average) + "%";
            averInfo.setText(info);
          }
        });
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
                    getCalendar();
                    return;
                  }
                } catch (JSONException e) {
                  // do nothing
                }
                try {
                  JSONObject json = new JSONObject(response);
                  JSONArray jsonArray = json.getJSONArray("lectures");
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

                  JSONObject jsonObject = json.getJSONObject("info");
                  setInfo(
                      jsonObject.getInt("done"),
                      jsonObject.getInt("taken"),
                      jsonObject.getDouble("average"));

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

  private void cancelClassroom(final int classroom_id) {
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
                    toastWithHandler("The lecture cancelled");
                    fillCalendar(givenLectures.get(course_spinner.getSelectedItemPosition()));
                  } else {
                    toastWithHandler(jsonObject.getString("message"));
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
            params.put("operation", "cancel-classroom");
            params.put("classroom_id", String.valueOf(classroom_id));
            return params;
          }
        };
    try {
      DatabaseManager.getmInstance(getActivity().getApplicationContext()).execute(request);
    } catch (NullPointerException e) {
      // do nothing
    }
  }

  private void showCancelAlert(final int classroom_id, String course, int section, String hour) {
    final AlertDialog alertDialog =
        new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT).create();
    alertDialog.setTitle("Warning!");
    String message =
        "Are you sure to cancel this lecture?\nLecture info: "
            + course
            + " - "
            + section
            + "  "
            + hour;
    alertDialog.setMessage(message);
    alertDialog.setButton(
        DialogInterface.BUTTON_NEGATIVE,
        "Dismiss",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            alertDialog.dismiss();
          }
        });
    alertDialog.setButton(
        DialogInterface.BUTTON_POSITIVE,
        "Cancel Lecture",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            cancelClassroom(classroom_id);
            alertDialog.dismiss();
          }
        });
    alertDialog.show();
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
                          showCancelAlert(
                              x.classroom_id, x.course_code, lastSelectedSection, x.hour);
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
            getStudentList();
          }
        },
        0,
        60000); // every one minute
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

  private void getStudentList() {
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
                  JSONArray json = new JSONArray(response);
                  studentList.clear();
                  for(int j = 0; j < json.length(); j++){
                    JSONObject mainJson = json.getJSONObject(j);
                    JSONObject classroom_info = mainJson.getJSONObject("classroom_info");
                    secure_list = classroom_info.getString("type").equals("secure");
                    JSONArray student_info = mainJson.getJSONArray("student_info");
                    // JSONObject course_info = mainJson.getJSONObject("course_info");
                    for (int i = 0; i < student_info.length(); i++) {
                      JSONObject jsonObject = student_info.getJSONObject(i);
                      StudentRow studentRow =
                              new StudentRow(
                                      jsonObject.getInt("student_id"),
                                      classroom_info.getInt("classroom_id"),
                                      jsonObject.getString("name") + " " + jsonObject.getString("surname"),
                                      jsonObject.getInt("student_number"),
                                      jsonObject.getInt("status"),
                                      jsonObject.getInt("time"),
                                      jsonObject.getInt("attended"),
                                      jsonObject.getInt("nearly"),
                                      jsonObject.getInt("absent"),
                                      jsonObject.getString("img"),
                                      jsonObject.getString("secure_img"));
                      studentList.add(studentRow);
                    }
                  }

                  // JSONObject course_info = json.getJSONObject("course_info");

                  Collections.sort(studentList, new Comparator<StudentRow>() {
                    @Override
                    public int compare(StudentRow o1, StudentRow o2) {
                      if(o2.state == o1.state){
                        return o1.number - o2.number;
                      }else
                      {
                        return o2.state - o1.state;
                      }
                    }
                  });
                  int attended = 0;
                  int absent = 0;
                  int nearly = 0;
                  for (StudentRow x : studentList) {
                    if (x.state == 0) {
                      absent++;
                    } else if (x.state == 1) {
                      nearly++;
                    } else {
                      attended++;
                    }
                  }
                  totalstudent.setText(" Total Student: " + studentList.size());
                  attendedstudent.setText(" Attended: " + attended);
                  absentstudent.setText(" Absent: " + absent);
                  nearlystudent.setText(" Nearly: " + nearly);

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
            for(int i = 0 ; i < classrooms.size(); i++){
              params.put("classroom_id["+i+"]", String.valueOf(classrooms.get(i)));
            }
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
    String name;
    int number;
    int state;
    int time;
    int attended;
    int nearly;
    int absent;
    int student_id;
    int classroom_id;
    String img;
    String secure_img;

    StudentRow(
        int student_id,
        int classroom_id,
        String name,
        int number,
        int state,
        int time,
        int attended,
        int nearly,
        int absent,
        String img,
        String secure_img) {
      this.name = name;
      this.number = number;
      this.state = state;
      this.time = time;
      this.attended = attended;
      this.nearly = nearly;
      this.absent = absent;
      this.student_id = student_id;
      this.classroom_id = classroom_id;
      this.img = img;
      this.secure_img = secure_img;
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
        holder.student_pic = row.findViewById(R.id.studentPic);

        row.setTag(holder);
      } else {
        holder = (StudentHolder) row.getTag();
      }

      StudentRow student = data.get(position);
      String url = "http://attendancesystem.xyz/attendancetracking/";
      if (student.img == null || student.img.isEmpty()) {
        Picasso.with(getActivity())
            .load(R.drawable.unknown_trainer)
            .fit()
            .centerCrop()
            .into(holder.student_pic);
      } else {
        Picasso.with(getActivity())
            .load(url + student.img)
            .fit()
            .centerCrop()
            .placeholder(R.drawable.unknown_trainer)
            .into(holder.student_pic);
      }
      holder.txtName.setText(student.name);
      holder.txtNumber.setText(String.valueOf(student.number));
      holder.txtLineNum.setText(String.valueOf(position + 1));
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
      ImageView student_pic;
    }
  }
}
