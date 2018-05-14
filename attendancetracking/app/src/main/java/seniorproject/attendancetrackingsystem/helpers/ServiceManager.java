package seniorproject.attendancetrackingsystem.helpers;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import br.com.goncalves.pugnotification.notification.PugNotification;
import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.activities.MainActivity;
import seniorproject.attendancetrackingsystem.utils.RegularMode;
import seniorproject.attendancetrackingsystem.utils.Schedule;

public class ServiceManager extends Service {
  private static final String UPDATE = "09:00";
  private static final String START_REGULAR = "00:20";
  private static final String STOP_REGULAR = "23:59";
  private boolean updatedForToday = false;
  private boolean noCourseForToday = false;
  private Schedule schedule = null;
  private Timer timer;
  private Handler handler;
  private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
  private boolean connected = false;
  private Schedule.CourseInfo currentCourse = null;
  private boolean allowNotification = true;
  private boolean secure = false;
  private boolean expired = false;

  private Date currentDate = null;
  private Date regularStart = null;
  private Date regularEnd = null;
  private Date updateDate = null;
  private Date breakTime = null;

  @Override
  public void onCreate() {
    super.onCreate();

    final BluetoothChecker bluetoothChecker = new BluetoothChecker();
    handler = new Handler(getMainLooper());
    timer = new Timer();
    timer.scheduleAtFixedRate(
        new TimerTask() {

          @Override
          public void run() {
            try {
              currentDate = dateFormat.parse(dateFormat.format(new Date()));
              regularStart = dateFormat.parse(START_REGULAR);
              regularEnd = dateFormat.parse(STOP_REGULAR);
              updateDate = dateFormat.parse(UPDATE);
              if (currentDate.after(updateDate) && currentDate.before(regularStart)) {
                // Log.i("ACTION", "UPDATE");
                updateSchedule();
              } else if (currentDate.after(regularStart) && currentDate.before(regularEnd)) {
                //  Log.i("ACTION", "START REGULAR MODE");
                if (!noCourseForToday) {
                  if (updatedForToday) {
                    if (!isServiceIsRunning(RegularMode.class)) {
                      currentCourse = currentCourse(currentDate);
                      if (currentCourse != null) {
                        broadcastCourseInfo(currentCourse);
                        breakTime = dateFormat.parse(currentCourse.getEnd_hour());
                        // CHECK BLUETOOTH
                        if (!BluetoothAdapter.getDefaultAdapter().isEnabled())
                          bluetoothChecker.start();
                        try {
                          bluetoothChecker.join();
                          // IF SERVICE IS NOT RUNNING START REGULAR
                          if (!isServiceIsRunning(RegularMode.class))
                            startRegularMode(currentCourse);
                        } catch (InterruptedException e) {
                          e.printStackTrace();
                        }
                      } else {
                        // IF THERE IS NOT ACTIVE COURSE
                        broadcastCourseInfo("null");
                      }
                    } else {
                      // BREAK TIME RUNS ONCE
                      if (breakTime != null && currentDate.after(breakTime)) {
                        BluetoothAdapter.getDefaultAdapter().disable();
                        stopRegularMode();
                        allowNotification = true;
                        secure = false;
                      } else if (currentCourse != null && !secure) {
                        // REGULAR MODE LECTURE
                        broadcastCourseInfo(currentCourse);
                      } else if (currentCourse != null && secure) {
                        // SECURE MODE LECTURE
                        broacastCourseInfo(currentCourse, secure, expired);
                      }
                    }

                  } else {
                    // IF NOT UPDATED FOR TODAY
                    updateSchedule();
                  }
                } else {
                  // IF THERE IS NOT ANY COURSE FOR TODAY
                  broadcastCourseInfo("no_course_for_today");
                }
              } else if (currentDate.after(regularEnd)) {
                // Log.i("ACTION", "STOP REGULAR MODE");
                // bluetoothChecker.interrupt();
                broadcastRegularModeInfo(false);
                if (isServiceIsRunning(RegularMode.class)) stopRegularMode();
                updatedForToday = false;
                noCourseForToday = false;
                secure = false;
                if (!new SessionManager(getBaseContext()).dailyNotificationState())
                  new SessionManager(getBaseContext()).changeDailyNotificatonState(true);
              }
            } catch (ParseException e) {
              e.printStackTrace();
            }
            if (!isServiceIsRunning(RegularMode.class)) {
              runCollector();
            }
          }
        },
        0,
        1000);
    Timer listeners = new Timer();
    listeners.scheduleAtFixedRate(
        new TimerTask() {
          @Override
          public void run() {
            if (currentDate != null
                && regularStart != null
                && regularEnd != null
                && currentDate.after(regularStart)
                && currentDate.before(regularEnd)) {
              connectionChecker();
              if (connected && currentCourse != null) tokenListener();
            }
          }
        },
        0,
        30000);
  }

  private void startRegularMode(Schedule.CourseInfo course) {
    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    Intent intent = new Intent(getBaseContext(), RegularMode.class);
    intent.putExtra("search", course.getBeacon_mac());
    intent.putExtra("course-info", course);
    startService(intent);
  }

  private void tokenListener() {
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
                    expired = jsonObject.getBoolean("experied");
                    secure = true;
                    broacastCourseInfo(currentCourse, secure, expired);
                    if (allowNotification) {
                      simpleNotification(
                          "Secure Mode",
                          "Secure mode is running " + "for " + currentCourse.getCourse_code(),
                          MainActivity.class);
                      allowNotification = false;
                    }
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
            params.put("operation", "get-token-status");
            params.put("classroom_id", String.valueOf(currentCourse.getClassroom_id()));
            return params;
          }
        };
    DatabaseManager.getmInstance(getBaseContext()).execute(request);
  }

  private void stopRegularMode() {

    stopService(new Intent(getBaseContext(), RegularMode.class));
    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  private boolean isServiceIsRunning(Class<?> serviceClass) {
    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningServiceInfo service :
        Objects.requireNonNull(manager).getRunningServices(Integer.MAX_VALUE)) {
      if (serviceClass.getName().equals(service.service.getClassName())) return true;
    }
    return false;
  }

  private void simpleNotification(String title, String text, Class<?> activity) {
    PugNotification.with(getBaseContext())
        .load()
        .title(title)
        .message(text)
        .smallIcon(R.drawable.pugnotification_ic_launcher)
        .largeIcon(R.drawable.pugnotification_ic_launcher)
        .click(activity)
        .flags(Notification.DEFAULT_ALL)
        .simple()
        .build();
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
                          // Do Nothing
                        }
                        if (!noCourseForToday) {
                          schedule =
                              JsonHelper.getmInstance(getBaseContext()).parseSchedule(response);
                          if (schedule.getCourses().size() > 0) {
                            updatedForToday = true;
                            if (new SessionManager(getBaseContext()).dailyNotificationState()) {
                              simpleNotification(
                                  "Update", "Your daily schedule is updated", MainActivity.class);
                              new SessionManager(getBaseContext())
                                  .changeDailyNotificatonState(false);
                            }
                          }
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
                        new SessionManager(getBaseContext())
                            .getUserDetails()
                            .get(SessionManager.KEY_USER_ID));
                    params.put("operation", "schedule");
                    return params;
                  }
                };
            DatabaseManager.getmInstance(getApplicationContext()).execute(request);
          }
        });
  }

  private void broadcastCourseInfo(Schedule.CourseInfo courseInfo) {
    Intent intent = new Intent();
    intent.setAction(RegularMode.ACTION);
    intent.putExtra("course_code", courseInfo.getCourse_code());
    intent.putExtra("classroom_id", courseInfo.getClassroom_id());
    sendBroadcast(intent);
  }

  private void broacastCourseInfo(Schedule.CourseInfo courseInfo, boolean secure, boolean expired) {
    Intent intent = new Intent();
    intent.setAction(RegularMode.ACTION);
    intent.putExtra("course_code", courseInfo.getCourse_code());
    intent.putExtra("classroom_id", courseInfo.getClassroom_id());
    intent.putExtra("secure", secure);
    intent.putExtra("expired", expired);
    sendBroadcast(intent);
  }

  private void broadcastCourseInfo(String courseInfo) {
    Intent intent = new Intent();
    intent.setAction(RegularMode.ACTION);
    intent.putExtra("course_code", courseInfo);
    sendBroadcast(intent);
  }

  private void broadcastRegularModeInfo(boolean status) {
    Intent intent = new Intent();
    intent.setAction("RegularModeStatus");
    intent.putExtra("status", status);
    sendBroadcast(intent);
  }

  private void runCollector() {
    File root = new File(Environment.getExternalStorageDirectory(), "AttendanceTracking");
    if (!root.exists()) return; // no need to push something to database
    File[] list = root.listFiles();
    if (list == null) return;
    if (list.length == 0) return; // no nedd to push something to database
    connectionChecker();
    if (connected) {
      Intent intent = new Intent(this, Logger.class);
      startService(intent);
    }
  }

  private void connectionChecker() {
    ConnectivityManager connectivityManager =
        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    assert connectivityManager != null;
    // we are connected to a network
    connected =
        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()
                == NetworkInfo.State.CONNECTED
            || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()
                == NetworkInfo.State.CONNECTED;
  }

  private Schedule.CourseInfo currentCourse(Date currentTime) {
    Schedule.CourseInfo current = null;
    for (Schedule.CourseInfo x : schedule.getCourses()) {
      String start = x.getHour();
      String end = x.getEnd_hour();
      try {
        if (currentTime.after(dateFormat.parse(start))
            && currentTime.before(dateFormat.parse(end))) {
          current = x;
        }
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    return current;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);
    return START_STICKY;
  }

  public class BluetoothChecker extends Thread {
    @Override
    public void run() {
      while (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
        if (BluetoothAdapter.getDefaultAdapter().isEnabled()) this.interrupt();
        else {
          BluetoothAdapter.getDefaultAdapter().enable();
          //   Log.i("BLUETOOTH", "STATUS GOING ON");
          stopRegularMode();
          try {
            Thread.sleep(2000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }
}
