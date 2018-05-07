package seniorproject.attendancetrackingsystem.helpers;

import android.app.ActivityManager;
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
import android.util.Log;

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

import seniorproject.attendancetrackingsystem.utils.RegularMode;
import seniorproject.attendancetrackingsystem.utils.Schedule;

public class ServiceManager extends Service {
  private static final String UPDATE = "09:00";
  private static final String START_REGULAR = "00:20";
  private static final String STOP_REGULAR = "17:20";
  private boolean updatedForToday = false;
  private boolean noCourseForToday = false;
  private Schedule schedule = null;
  private Timer timer;
  private Handler handler;
  private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
  private boolean connected = false;

  @Override
  public void onCreate() {
    super.onCreate();
    final BluetoothChecker bluetoothChecker = new BluetoothChecker();
    handler = new Handler(getMainLooper());
    timer = new Timer();
    timer.scheduleAtFixedRate(
        new TimerTask() {
          Date currentDate = null;
          Date regularStart = null;
          Date regularEnd = null;
          Date updateDate = null;
          Date breakTime = null;

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
                      Schedule.CourseInfo currentCourse = currentCourse(currentDate);
                      if (currentCourse != null) {
                        broadcastCourseInfo(currentCourse.getCourse_code());
                        breakTime = dateFormat.parse(currentCourse.getEnd_hour());
                        if (!BluetoothAdapter.getDefaultAdapter().isEnabled())
                          bluetoothChecker.start();
                        try {
                          bluetoothChecker.join();
                          if (!isServiceIsRunning(RegularMode.class))
                            startRegularMode(currentCourse);
                        } catch (InterruptedException e) {
                          e.printStackTrace();
                        }
                      } else {
                        broadcastCourseInfo("null");
                      }
                    } else {
                      if (breakTime != null && currentDate.after(breakTime)) {
                        BluetoothAdapter.getDefaultAdapter().disable();
                        stopRegularMode();
                      }
                    }

                  } else {
                    updateSchedule();
                  }
                } else {
                  broadcastCourseInfo("no_course_for_today");
                }
              } else if (currentDate.after(regularEnd)) {
                // Log.i("ACTION", "STOP REGULAR MODE");
                // bluetoothChecker.interrupt();
                broadcastRegularModeInfo(false);
                if (isServiceIsRunning(RegularMode.class)) stopRegularMode();
                updatedForToday = false;
                noCourseForToday = false;
              }
            } catch (ParseException e) {
              e.printStackTrace();
            }
          if(!isServiceIsRunning(RegularMode.class)){
              runCollector();
          }
          }
        },
        0,
        1000);
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
    timer.cancel();
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
                         //Do Nothing
                        }
                        if (!noCourseForToday) {
                          schedule =
                              JsonHelper.getmInstance(getBaseContext()).parseSchedule(response);
                          if (schedule.getCourses().size() > 0) updatedForToday = true;
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
private void runCollector(){
  File root = new File(Environment.getExternalStorageDirectory(), "AttendanceTracking");
  if(!root.exists()) return; // no need to push something to database
  File[] list = root.listFiles();
  if(list.length == 0) return; // no nedd to push something to database
    connectionChecker();
    if (connected) {
      Intent intent = new Intent(this, Logger.class);
      startService(intent);
    }

}

private void connectionChecker(){
  ConnectivityManager connectivityManager =
          (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
  assert connectivityManager != null;
  // we are connected to a network
  connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()
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
