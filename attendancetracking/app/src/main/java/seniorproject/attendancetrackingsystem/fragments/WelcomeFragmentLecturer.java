package seniorproject.attendancetrackingsystem.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.DatabaseManager;
import seniorproject.attendancetrackingsystem.helpers.JsonHelper;
import seniorproject.attendancetrackingsystem.helpers.SessionManager;
import seniorproject.attendancetrackingsystem.utils.Schedule;

/* A simple {@link Fragment} subclass. */
public class WelcomeFragmentLecturer extends Fragment {

  private static String IMG_PREF = "http://attendancesystem.xyz/attendancetracking/";
  private String token = "not_initialized";
  private Handler handler;
  private boolean updated = false;
  private boolean noCourseForToday = false;
  private Schedule schedule = null;
  private ListView listView;
  private ArrayList<String> items;
  private ArrayAdapter<String> adapter;
  private Timer timer;
  private ArrayList<Schedule.CourseInfo> currentCourses = new ArrayList<>();
  private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
  private Switch secureSwitch;
  private SessionManager session;

  public WelcomeFragmentLecturer() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_welcome_lecturer, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    handler = new Handler(Looper.getMainLooper());
    timer = new Timer();

    session = new SessionManager(getActivity().getApplicationContext());
    HashMap<String, String> userInfo = session.getUserDetails();
    ImageView avatar = getActivity().findViewById(R.id.avatar);

    if (userInfo.get(SessionManager.KEY_USER_IMG).isEmpty()
        || userInfo.get(SessionManager.KEY_USER_IMG) == null) {
      avatar.setImageResource(R.drawable.unknown_trainer);
    } else {
      Picasso.with(getActivity())
          .load(IMG_PREF + userInfo.get(SessionManager.KEY_USER_IMG))
          .fit()
          .centerCrop()
          .into(avatar);
    }
    TextView nameSurnameField = getActivity().findViewById(R.id.w_user_name);
    TextView description = getActivity().findViewById(R.id.w_user_mail);
    secureSwitch = getActivity().findViewById(R.id.secure_switch);
    listView = getActivity().findViewById(R.id.notification_list);
    items = new ArrayList<>();
    adapter =
        new ArrayAdapter<>(
            getActivity().getApplicationContext(), R.layout.notification_item, items);
    String nameText =
        userInfo.get(SessionManager.KEY_USER_NAME)
            + " "
            + userInfo.get(SessionManager.KEY_USER_SURNAME).toUpperCase();
    String mailText = userInfo.get(SessionManager.KEY_USER_MAIL);
    nameSurnameField.setText(nameText);
    description.setText(mailText);

    secureSwitch.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (secureSwitch.isChecked()) {
              infirmUser();
            }
          }
        });

    timer.scheduleAtFixedRate(
        new TimerTask() {
          Date start = null;
          Date stop = null;
          Date current = null;

          @Override
          public void run() {
              Calendar cal = Calendar.getInstance();
              if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                      || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                  items.clear();
                  items.add("Weekend");
                  return;
              }
            try {
              current = dateFormat.parse(dateFormat.format(new Date()));
              start = dateFormat.parse("09:20");
              stop = dateFormat.parse("17:10");
            } catch (ParseException e) {
              e.printStackTrace();
            }
            if (current.compareTo(start) >= 0 && current.compareTo(stop) < 0) {
              if (!noCourseForToday) {
                if (!updated) {
                  handler.post(
                      new Runnable() {
                        @Override
                        public void run() {
                          updateSchedule();
                        }
                      });
                }
              }
            } else {
              updated = false;
              noCourseForToday = false;
              items.clear();
              items.add("End of the day");
              session.turnOffSecure();
              handler.post(
                  new Runnable() {
                    @Override
                    public void run() {
                      Parcelable state = listView.onSaveInstanceState();
                      listView.setAdapter(adapter);
                      listView.onRestoreInstanceState(state);
                      currentCourses.clear();
                    }
                  });
            }
            if (updated || noCourseForToday) {
              setItems(current);
              if (currentCourses.isEmpty()) {
                secureModeSwitchVisibility(false);
                session.turnOffSecure();
              }
            }

            if (currentCourses.isEmpty()) {
              secureModeSwitchVisibility(false);
            } else if (!session.isSecureMode()) secureModeSwitchVisibility(true);
            else secureModeSwitchVisibility(false);
          }
        },
        0,
        2000);
  }

  private void setToken(final String token) {
    for (final Schedule.CourseInfo x : currentCourses) {
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
                    if (!result) {
                      Toast.makeText(
                              getActivity().getApplicationContext(),
                              "An error has been occured",
                              Toast.LENGTH_LONG)
                          .show();
                    }
                  } catch (JSONException e) {
                    e.printStackTrace();
                  }
                }
              },
              new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                  Toast.makeText(
                          getActivity().getApplicationContext(),
                          "An error has been occured",
                          Toast.LENGTH_LONG)
                      .show();
                }
              }) {
            @Override
            protected Map<String, String> getParams() {
              Map<String, String> params = new HashMap<>();
              params.put("classroom_id", String.valueOf(x.getClassroom_id()));
              params.put("token_value", token);
              // current time + 5minutes
              params.put("operation", "token");
              return params;
            }
          };

      DatabaseManager.getmInstance(getActivity().getApplicationContext()).execute(request);
    }
  }

  private boolean checkNumber(int number) {
    return number > 47 && number < 58 || number > 64 && number < 91 || number > 96 && number < 123;
  }

  private void generateToken() {
    Toast.makeText(
            getActivity().getApplicationContext(), "Secure mode is activated", Toast.LENGTH_SHORT)
        .show();

    Random r = new Random(System.currentTimeMillis());
    int number = 0;
    token = "";
    LinkedList<Character> digits = new LinkedList<>();
    while (digits.size() != 5) {
      do {
        for (int i = 0; i < 10; i++) number = r.nextInt(123);
      } while (!checkNumber(number));
      digits.add((char) number);
    }
    StringBuilder out = new StringBuilder();

    while (!digits.isEmpty()) token = String.format("%s%s", token, digits.pollFirst());
    for (int i = 0; i < token.length(); i++) {
      out.append(token.charAt(i)).append("-");
    }
    out = new StringBuilder(out.substring(0, out.length() - 1)); // ignoring last '-' character
    setToken(token);
    session.turnOnSecure(token);
    showAlertDialog(out.toString());
  }

  private void infirmUser() {
    handler.post(
        new Runnable() {
          @Override
          public void run() {
            final AlertDialog alertDialog =
                new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT).create();
            alertDialog.setTitle("Warning");
            alertDialog.setMessage(
                "Once you activate the secure mode, you cannot be able to start regular mode for this course until the course finishes. "
                    + "\n\nAre you sure to activate secure mode for this course?");
            alertDialog.setButton(
                Dialog.BUTTON_NEGATIVE,
                "Cancel",
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                  }
                });
            alertDialog.setButton(
                Dialog.BUTTON_POSITIVE,
                "Continue",
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    alertDialog.hide();
                    generateToken();
                  }
                });
            alertDialog.show();
          }
        });
  }

  private void showAlertDialog(final String token) {
    handler.post(
        new Runnable() {
          @Override
          public void run() {
            final AlertDialog alertDialog =
                new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT).create();
            alertDialog.setTitle("Secure Mode");
            alertDialog.setMessage("Token: " + session.getToken());
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setButton(
                Dialog.BUTTON_NEUTRAL,
                "Close",
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                  }
                });
            alertDialog.show();
            secureModeSwitchVisibility(false);
          }
        });
  }

  private void secureModeSwitchVisibility(final boolean state) {
    handler.post(
        new Runnable() {
          @Override
          public void run() {
            if (state) secureSwitch.setVisibility(View.VISIBLE);
            else secureSwitch.setVisibility(View.INVISIBLE);
          }
        });
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    timer.cancel();
  }

  private void updateSchedule() {
    handler.post(
        new Runnable() {
          @Override
          public void run() {
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
                            noCourseForToday = true;
                          }
                        } catch (JSONException e) {
                          // DO NOTHING
                        }
                        if (!noCourseForToday) {
                          schedule =
                              JsonHelper.getmInstance(getActivity().getApplicationContext())
                                  .parseSchedule(response);
                          if (schedule.getCourses().size() > 0) updated = true;
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
                    params.put("user_id", session.getUserDetails().get(SessionManager.KEY_USER_ID));
                    params.put("operation", "lecturer-schedule");
                    return params;
                  }
                };
            try {
              DatabaseManager.getmInstance(getActivity().getApplicationContext()).execute(request);
            } catch (NullPointerException e) {
              // do nothing
            }
          }
        });
  }

  private void setOnclick(boolean allow) {
    if (allow) {
      listView.setOnItemClickListener(
          new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              if (position == 0) {
                Bundle args = new Bundle();
                ArrayList<Integer> courses = new ArrayList<>();
                for (Schedule.CourseInfo x : currentCourses) {
                  courses.add(x.getClassroom_id());
                }
                args.putIntegerArrayList("classrooms", courses);
                ReportFragmentLecturer f = new ReportFragmentLecturer();
                BottomNavigationView mainNav = getActivity().findViewById(R.id.main_nav);
                mainNav.setSelectedItemId(R.id.nav_report);
                f.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.main_frame, f).commit();
              } else if (position == 1) {
                if (session.isSecureMode()) showAlertDialog(session.getToken());
              }
            }
          });
    } else {
      listView.setOnItemClickListener(
          new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              // do nothing
            }
          });
    }
  }

  private void currentCourse(Date currentTime) {
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

  private void setItems(Date currentTime) {
    currentCourse(currentTime);
    if (noCourseForToday) {
      items.clear();
      String info = "There is no course for today";
      items.add(info);
      setOnclick(false);
      handler.post(
          new Runnable() {
            @Override
            public void run() {
              Parcelable state = listView.onSaveInstanceState();
              listView.setAdapter(adapter);
              listView.onRestoreInstanceState(state);
            }
          });
      return;
    }
    if (currentCourses.size() > 1) {
      items.clear();
      String course_code = currentCourses.get(0).getCourse_code();
      ArrayList<Integer> sections = new ArrayList<>();
      for (Schedule.CourseInfo x : currentCourses) {
        if (course_code.equals(x.getCourse_code())) {
          sections.add(x.getSection());
        }
      }
      StringBuilder info = new StringBuilder("Current Course: " + course_code);
      for (int x : sections) info.append(" - ").append(String.valueOf(x));
      items.add(info.toString());
      if (session.isSecureMode()) items.add("Click here to display Secure Token");
      setOnclick(true);
    } else if (currentCourses.size() == 1) {
      items.clear();
      String info =
          "Current Course: "
              + currentCourses.get(0).getCourse_code()
              + " - "
              + currentCourses.get(0).getSection();
      items.add(info);
      if (session.isSecureMode()) items.add("Click here to display Secure Token");
      setOnclick(true);
    } else {
      items.clear();
      String info = "There is not active course for now";
      if (secureSwitch.isChecked()) {
        handler.post(
            new Runnable() {
              @Override
              public void run() {
                Toast.makeText(
                        getActivity().getApplicationContext(),
                        "Secure is " + "deactivated.",
                        Toast.LENGTH_SHORT)
                    .show();
                session.turnOffSecure();
              }
            });
        secureSwitch.setChecked(false);
        token = "not_initialized";
      }
      setOnclick(false);
      items.add(info);
    }

    handler.post(
        new Runnable() {
          @Override
          public void run() {
            Parcelable state = listView.onSaveInstanceState();
            listView.setAdapter(adapter);
            listView.onRestoreInstanceState(state);
          }
        });
  }
}
