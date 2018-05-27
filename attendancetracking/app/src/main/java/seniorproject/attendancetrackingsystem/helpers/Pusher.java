package seniorproject.attendancetrackingsystem.helpers;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Pusher extends IntentService {
  private final String filename = "pusher.queue";
  private boolean connected = false;

  public Pusher() {
    super("PusherService");
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {
    assert intent != null;
    int classroom_id = intent.getIntExtra("classroom_id", 0);
    long elapsed = intent.getLongExtra("elapsed", 0);
    int user_id = Integer.parseInt(
            new SessionManager(getBaseContext()).getUserDetails().get(SessionManager.KEY_USER_ID));
    zip();
    checkNetworkConnection();
    if (connected) {
      tryPushEverything();
      updateAttended(classroom_id, user_id, elapsed);
    } else {
      addToPusherFile(classroom_id, user_id, elapsed);
    }

    stopSelf();
  }

  private void checkNetworkConnection() {
    ConnectivityManager connectivityManager =
        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    assert connectivityManager != null;
    // we are connected to a network
    connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()
            == NetworkInfo.State.CONNECTED
            || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()
            == NetworkInfo.State.CONNECTED;
  }

  private void updateAttended(final int classroom_id, final int user_id, final long elapsed) {
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
                  if (!result){
                   stopSelf();
                  }
                } catch (JSONException e) {
                  e.printStackTrace();
                }
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                stopSelf();
              }
            }) {
          @Override
          protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<>();
            params.put("user_id", String.valueOf(user_id));
            params.put("classroom_id", String.valueOf(classroom_id));
            params.put("total_time", String.valueOf(elapsed));
            params.put("operation", "attendance");
            return params;
          }
        };
    DatabaseManager.getInstance(getBaseContext()).execute(request);
  }

  private void addToPusherFile(final int classroom_id, final int user_id, final long elapsed) {
    File root = new File(Environment.getExternalStorageDirectory(), "AttendanceTracking");
    if (!root.exists()) root.mkdirs();

    File pusherFile = new File(root, filename);
    try {
      FileWriter fileWriter = new FileWriter(pusherFile, true);
      String info =
          String.valueOf(classroom_id)
              + "_"
              + String.valueOf(user_id)
              + "_"
              + String.valueOf(elapsed)
              + "\n";
      fileWriter.append(info);
      fileWriter.flush();
      fileWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void tryPushEverything() {
    File root = new File(Environment.getExternalStorageDirectory(), "AttendanceTracking");

    if (!root.exists()) return;

    File pusherFile = new File(root, filename);
    if (!pusherFile.exists()) return;

    try {
      BufferedReader br = new BufferedReader(new FileReader(pusherFile));
      String line;

      while ((line = br.readLine()) != null) {
        String[] parts = line.split("_");
        updateAttended(
            Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Long.parseLong(parts[2]));
      }
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    pusherFile.delete();
  }

  private void zip() {
    boolean changes = false;
    File root = new File(Environment.getExternalStorageDirectory(), "AttendanceTracking");
    ArrayList<PusherFormat> list = new ArrayList<>();
    if (!root.exists()) return;

    File pusherFile = new File(root, filename);
    if (!pusherFile.exists()) return;

    try {
      BufferedReader br = new BufferedReader(new FileReader(pusherFile));
      String line;
      boolean flag = true;
      while ((line = br.readLine()) != null) {
        String[] parts = line.split("_");
        PusherFormat temp = new PusherFormat(parts[0], parts[1], parts[2]);
        for (PusherFormat x : list) {
          if (x.classroom_id.equals(temp.classroom_id) && x.user_id.equals(temp.user_id)) {
            long elapsed1 = Long.parseLong(x.elapsed);
            long elapsed2 = Long.parseLong(temp.elapsed);

            if (elapsed1 < elapsed2) x.elapsed = temp.elapsed;
            flag = false;
            changes = true;
            break;
          }
        }
        if (flag) {
          list.add(temp);
        }
      }
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (changes) {
      pusherFile.delete();
      try {
        FileWriter fileWriter = new FileWriter(pusherFile, true);
        for (PusherFormat x : list) {
          String info = x.classroom_id + "_" + x.user_id + "_" + x.elapsed + "\n";
          fileWriter.append(info);
          fileWriter.flush();
          fileWriter.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private class PusherFormat {
    final String classroom_id;
    String elapsed;
    final String user_id;

    PusherFormat(String classroom_id, String user_id, String elapsed) {
      this.classroom_id = classroom_id;
      this.elapsed = elapsed;
      this.user_id = user_id;
    }
  }
}
