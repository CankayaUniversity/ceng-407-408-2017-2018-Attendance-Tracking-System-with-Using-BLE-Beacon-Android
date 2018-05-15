package seniorproject.attendancetrackingsystem.helpers;

import android.app.IntentService;
import android.content.Intent;
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
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import seniorproject.attendancetrackingsystem.utils.Queue;

public class Logger extends IntentService {
  private String start;
  private String stop;
  private String filename;
  private int classroom_id;

  private Queue<String> times = new Queue<>();
  private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
  private long elapsed = 0;

  public Logger() {
    super("Logger Service");
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {
    assert intent != null;
    start = intent.getStringExtra("start");
    stop = intent.getStringExtra("stop");
    start = start + ":00";
    stop = stop + ":00";
    filename = intent.getStringExtra("filename");
    classroom_id = intent.getIntExtra("classroom_id", 0);

    if (filename != null && !filename.isEmpty() && classroom_id != 0) {
      readLogs();
      decider();

      runPusher();
    }
    else
    {
      readAll();
    }

    stopSelf();
  }

  private void decider() {
    String first = null;
    String second;
    ignoreUnnecessaryData();
    try {
      while (!times.isEmpty()) {
        if (first == null) first = start;
        second = times.dequeue();

        long ftime = format.parse(first).getTime();
        long stime = format.parse(second).getTime();

        long diff = stime - ftime;
        first = second;
        if (diff < (1000 * 60 * 6)) {
          elapsed = elapsed + diff;
        }
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  private void ignoreUnnecessaryData() {
    Queue<String> newQueue = new Queue<>();
    String last = null;
    while (!times.isEmpty()) {
      String time = times.dequeue();
      if (last == null) last = time;
      try {
        if (format.parse(time).compareTo(format.parse(start)) >= 0
            && format.parse(time).compareTo(format.parse(stop)) < 0) {
          if (format.parse(time).compareTo(format.parse(last))>=0) newQueue.enqueue(time);
        }
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    times = newQueue;
  }

  private void readLogs() {
    File root = new File(Environment.getExternalStorageDirectory(), "AttendanceTracking");
    if (!root.exists()) return;
    File logfile = new File(root, filename);
    if (!logfile.exists()) return;
    try {
      BufferedReader br = new BufferedReader(new FileReader(logfile));
      String line;

      while ((line = br.readLine()) != null) times.enqueue(line);
      br.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void runPusher() {
    Intent intent = new Intent(this, Pusher.class);
    intent.putExtra("classroom_id", classroom_id);
    intent.putExtra("elapsed", elapsed);
    startService(intent);
  }

  private void getTimeAndDecide(){
    StringRequest request = new StringRequest(Request.Method.POST, DatabaseManager.GetOperations,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
               try{
                 JSONObject jsonObject = new JSONObject(response);

                boolean result = jsonObject.getBoolean("success");
                if(result){
                  start = jsonObject.getString("hour");
                  stop = start.substring(0,2);
                  stop = String.valueOf(Integer.parseInt(stop) + 1) + ":10:00";
                  start = start + ":00";
                  readLogs();
                  decider();
                  runPusher();
                  delete(filename);
                }
               }catch (JSONException e){
                 e.printStackTrace();
               }
              }
            }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        stopSelf();
      }
    }){
      @Override
      protected Map<String, String> getParams(){
        Map<String,String> params = new HashMap<>();
        params.put("classroom_id", String.valueOf(classroom_id));
        params.put("operation", "coursetime");
        return params;
      }
    };
    DatabaseManager.getmInstance(getBaseContext()).execute(request);
  }

  private void delete(String filename){
    File root = new File(Environment.getExternalStorageDirectory(), "AttendanceTracking");
    if(!root.exists()) return;
    File deleted= new File(root, filename);
    if(!deleted.exists()) return;
    deleted.delete();
  }
  private void readAll(){
    File root = new File(Environment.getExternalStorageDirectory(), "AttendanceTracking");
    if(!root.exists()) return;

    File[] files = root.listFiles();
    for(int i = 0; i < files.length; i++){
      if(!files[i].getName().equals("pusher.queue")){
        filename = files[i].getName();
        String[] parts = filename.split("_");
        classroom_id = Integer.parseInt(parts[1]);
        getTimeAndDecide();
      }
    }
  }
}
