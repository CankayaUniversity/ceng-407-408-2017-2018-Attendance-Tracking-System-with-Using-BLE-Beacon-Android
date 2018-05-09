package seniorproject.attendancetrackingsystem.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    SessionManager session = new SessionManager(getActivity().getApplicationContext());
    HashMap<String, String> userInfo = session.getUserDetails();
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
            try {
              current = dateFormat.parse(dateFormat.format(new Date()));
              start = dateFormat.parse("00:00");
              stop = dateFormat.parse("23:59");
            } catch (ParseException e) {
              e.printStackTrace();
            }
            if (current.after(start) && current.before(stop)) {
              if (!updated) {
                Log.d("update:", "Gathering courses of the today");
                handler.post(new Runnable() {
                  @Override
                  public void run() {
                    updateSchedule();
                  }
                });
              }
            }
            if (updated) setItems(current);
            if (currentCourses.isEmpty()) secureModeSwitchVisibility(false);
            else if (token.equals("not_initialized")) secureModeSwitchVisibility(true);
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
    while (digits.size()!=5){
      do{
        for(int i = 0; i < 10; i++) number = r.nextInt(123);
      }while(!checkNumber(number));
      digits.add ((char)number);
    }
    while(!digits.isEmpty()) token = String.format("%s%s-", token, digits.pollFirst());
    token = token.substring(0, token.length()-1); //ignoring last '-' character
    setToken(token);
    showAlertDialog(token);
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
                "Once you activate the secure mode, you cannot be able to start regular mode for this course until the course finishes. " +
                        "\n\nAre you sure to activate secure mode for this course?");
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
            alertDialog.setMessage("Token: " + token);
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
                    params.put(
                        "user_id",
                        new SessionManager(getActivity().getBaseContext())
                            .getUserDetails()
                            .get(SessionManager.KEY_USER_ID));
                    params.put("operation", "lecturer-schedule");
                    return params;
                  }
                };
            try{
              DatabaseManager.getmInstance(getActivity().getApplicationContext()).execute(request);
            }catch (NullPointerException e){
              //do nothing
            }

          }
        });
  }

  private void currentCourse(Date currentTime) {
    currentCourses.clear();
    for (Schedule.CourseInfo x : schedule.getCourses()) {
      String start = x.getHour();
      String end = x.getEnd_hour();
      try {
        if (currentTime.after(dateFormat.parse(start))
            && currentTime.before(dateFormat.parse(end))) {
          currentCourses.add(x);
        }
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
  }

  private void setItems(Date currentTime) {
    currentCourse(currentTime);
    if (currentCourses.size() > 1) {
      items.clear();
      String course_code = currentCourses.get(0).getCourse_code();
      ArrayList<Integer> sections = new ArrayList<>();
      for (Schedule.CourseInfo x : currentCourses) {
        if (course_code.equals(x.getCourse_code())) {
          sections.add(x.getSection());
        }
      }
      String info = "Current Course: " + course_code;
      for (int x : sections) info = info + " - " + String.valueOf(x);
      items.add(info);
    } else if (currentCourses.size() == 1) {
      items.clear();
      String info =
          "Current Course: "
              + currentCourses.get(0).getCourse_code()
              + " - "
              + currentCourses.get(0).getSection();
      items.add(info);
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
              }
            });
        secureSwitch.setChecked(false);
        token = "not_initialized";
      }
      items.add(info);
    }

    handler.post(
        new Runnable() {
          @Override
          public void run() {
            listView.setAdapter(adapter);
          }
        });
  }
}
