package seniorproject.attendancetrackingsystem.helpers;

import android.app.ActivityManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

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
  private static final String START_REGULAR = "09:20";
  private static final String STOP_REGULAR = "17:20";
  private boolean updatedForToday = false;
  private boolean noCourseForToday = false;
  private Schedule schedule = null;
  private Timer timer;
  private Handler handler;
  private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

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
                    if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) bluetoothChecker.start();
                    try {
                      bluetoothChecker.join();
                      if (!isServiceIsRunning(RegularMode.class)) startRegularMode();
                    } catch (InterruptedException e) {
                      e.printStackTrace();
                    }
                  } else {
                    updateSchedule();
                  }
                }
              } else if (currentDate.after(regularEnd)) {
                //  Log.i("ACTION", "STOP REGULAR MODE");
                // bluetoothChecker.interrupt();
                if (isServiceIsRunning(RegularMode.class)) stopRegularMode();
                updatedForToday = false;
                noCourseForToday = false;
                if (BluetoothAdapter.getDefaultAdapter().isEnabled())
                  BluetoothAdapter.getDefaultAdapter().disable();
              }
            } catch (ParseException e) {
              e.printStackTrace();
            }
          }
        },
        0,
        1000);
  }

  private void startRegularMode() {
    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    Intent intent = new Intent(getBaseContext(), RegularMode.class);
    intent.putExtra("schedule", schedule);
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
                          if (!result){
                            noCourseForToday = true;
                            Intent intent = new Intent();
                            intent.setAction(RegularMode.ACTION);
                            intent.putExtra("course_code", "no_course_for_today");
                            sendBroadcast(intent);
                          }
                        } catch (JSONException e) {
                          e.printStackTrace();
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
