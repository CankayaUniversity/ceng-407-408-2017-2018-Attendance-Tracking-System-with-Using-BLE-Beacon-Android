package seniorproject.attendancetrackingsystem.fragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

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
import seniorproject.attendancetrackingsystem.utils.RegularMode;

/* A simple {@link Fragment} subclass. */
public class WelcomeFragment extends Fragment {
  ArrayAdapter<String> adapter;
  private Receiver mReceiver;
  private ArrayList<String> messages;
  private ListView listView;
  private Handler handler;
  private Timer timer;
  private int classroom_id = 0;
  private String course_code = "";
  private boolean secure_mode = false;

  public WelcomeFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_welcome, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    handler = new Handler();
    timer = new Timer();
    SessionManager session = new SessionManager(getActivity().getApplicationContext());
    HashMap<String, String> userInfo = session.getUserDetails();
    TextView nameSurnameField = getActivity().findViewById(R.id.w_user_name);
    TextView description = getActivity().findViewById(R.id.w_user_mail);
    String nameText =
        userInfo.get(SessionManager.KEY_USER_NAME)
            + " "
            + userInfo.get(SessionManager.KEY_USER_SURNAME).toUpperCase();
    String mailText = userInfo.get(SessionManager.KEY_USER_MAIL);
    nameSurnameField.setText(nameText);
    description.setText(mailText);
    listView = view.findViewById(R.id.notification_list);
    messages = new ArrayList<>();
    adapter =
        new ArrayAdapter<>(
            getActivity().getApplicationContext(), R.layout.notification_item, messages);
    if(classroom_id != 0){
      messages.add("Current Course: "+ course_code);
    }else if(course_code.equals("null")){
      messages.add("There is not active course for now");
    }else if(course_code.equals("no_course_for_today")){
      messages.add("There is no course for today");
    }
    listView.setAdapter(adapter);

    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        if(isConnected() && classroom_id != 0) tokenListener();
      }
    },0,  30000); // runs every 30 seconds
  }

  @Override
  public void onResume() {
    super.onResume();
    mReceiver = new Receiver();
    IntentFilter filter = new IntentFilter();
    filter.addAction(RegularMode.ACTION);
    filter.addAction("RegularModeStatus");
    getActivity().registerReceiver(mReceiver, filter);
  }

  @Override
  public void onPause() {
    super.onPause();
    getActivity().unregisterReceiver(mReceiver);
  }

  private void showMessages() {
    if (messages.size() != 0) messages.remove(0);
   /* SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH);
    Date currentDate = new Date();*/
    if(classroom_id != 0){
      if(secure_mode)
        messages.add("Current Course: " + course_code + " (Secure Mode)");
      else
      messages.add("Current Course: " + course_code);
    }else if(course_code.equals("null")){
      messages.add("There is not active course for now");
    }else if(course_code.equals("no_course_for_today")){
      messages.add("There is no course for today");
    }

    listView.setAdapter(adapter);
  }

  private void tokenListener() {
    StringRequest request = new StringRequest(Request.Method.POST, DatabaseManager.GetOperations,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
                try
                {
                  JSONObject jsonObject = new JSONObject(response);
                  boolean result = jsonObject.getBoolean("success");
                  if(result){
                    boolean experied = jsonObject.getBoolean("experied");
                    if(experied){
                      // ZAMAN GEÇTİ YİĞENİM
                    }else
                    {
                      secure_mode = true;
                    }
                  }
                }catch (JSONException e){
                  e.printStackTrace();
                }
              }
            }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {

      }
    }){
      @Override
      protected Map<String, String> getParams(){
        Map<String, String> params = new HashMap<>();
        params.put("operation", "get-token-status");
        params.put("classroom_id", String.valueOf(classroom_id));
        return params;
      }
    };
    DatabaseManager.getmInstance(getActivity().getApplicationContext()).execute(request);
  }
private boolean isConnected(){
  ConnectivityManager connectivityManager =
          (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
  assert connectivityManager != null;
  // we are connected to a network
  return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()
          == NetworkInfo.State.CONNECTED
          || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()
          == NetworkInfo.State.CONNECTED;
}
  private class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        course_code = intent.getStringExtra("course_code");
        Log.d("course_code", course_code);
      classroom_id = intent.getIntExtra("classroom_id", 0);
      showMessages();
    }
  }
}
