package seniorproject.attendancetrackingsystem.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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
  private int classroom_id = 0;
  private String course_code = "";
  private boolean secure_mode = false;
  private boolean expired = false;
  private Timer timer;
  private ArrayList<LatestCourses> latestCourses = new ArrayList<>();

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

    listView.setAdapter(adapter);
    showMessages();
    timer.scheduleAtFixedRate(
        new TimerTask() {
          @Override
          public void run() {
            getLatestCoursesList();
          }
        },
        0,
        600000);
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
    messages.clear();
    /* SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH);
    Date currentDate = new Date();*/
    if (classroom_id != 0) {
      if (secure_mode && !expired)
        messages.add(0,"Current Course: " + course_code + " \n(Secure Mode)");
      else if (secure_mode) {
        messages.add(0,"Current Course: " + course_code + " \n(Secure Mode - Expired)");
      } else messages.add(0,"Current Course: " + course_code);
    } else if (course_code.equals("null")) {
      secure_mode = false;
      expired = false;
      messages.add("There is not active course for now");
    } else if (course_code.equals("no_course_for_today")) {
      secure_mode = false;
      expired = false;
      messages.add("There is no course for today");
    }
    addAllLatestCourses();
    listView.setAdapter(adapter);
  }

  private void buildAlertDialog() {
    final AlertDialog.Builder alert =
        new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
    final LinearLayout layout = new LinearLayout(getActivity().getApplicationContext());
    layout.setOrientation(LinearLayout.HORIZONTAL);

    final EditText digit1 = new EditText(getActivity().getApplicationContext());
    final EditText digit2 = new EditText(getActivity().getApplicationContext());
    final EditText digit3 = new EditText(getActivity().getApplicationContext());
    final EditText digit4 = new EditText(getActivity().getApplicationContext());
    final EditText digit5 = new EditText(getActivity().getApplicationContext());
    final TextView info = new TextView(getActivity().getApplicationContext());
    info.setText("Enter Token:");
    info.setTextColor(Color.BLACK);
    info.setWidth(300);
    info.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    layout.addView(info);

    digit1.setWidth(15);
    digit1.setHeight(15);
    digit1.setTextColor(Color.BLACK);
    digit1.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    digit1.setId(R.id.digit1);
    digit1.setFocusable(true);
    digit1.setFilters(new InputFilter[] {new InputFilter.LengthFilter(1)});
    digit1.post(
        new Runnable() {
          @Override
          public void run() {
            final InputMethodManager imm =
                (InputMethodManager)
                    digit1.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(digit1, InputMethodManager.SHOW_IMPLICIT);
            digit1.requestFocus(); // needed if you have more then one input
          }
        });
    layout.addView(digit1);

    digit2.setWidth(15);
    digit2.setHeight(15);
    digit2.setTextColor(Color.BLACK);
    digit2.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    digit2.setId(R.id.digit2);
    digit2.setFocusable(true);
    digit2.setFilters(new InputFilter[] {new InputFilter.LengthFilter(1)});
    layout.addView(digit2);

    digit3.setWidth(15);
    digit3.setHeight(15);
    digit3.setTextColor(Color.BLACK);
    digit3.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    digit3.setId(R.id.digit3);
    digit3.setFocusable(true);
    digit3.setFilters(new InputFilter[] {new InputFilter.LengthFilter(1)});
    layout.addView(digit3);

    digit4.setWidth(15);
    digit4.setHeight(15);
    digit4.setTextColor(Color.BLACK);
    digit4.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    digit4.setId(R.id.digit4);
    digit4.setFilters(new InputFilter[] {new InputFilter.LengthFilter(1)});
    digit4.setFocusable(true);

    layout.addView(digit4);

    digit5.setWidth(15);
    digit5.setHeight(15);
    digit5.setTextColor(Color.BLACK);
    digit5.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    digit5.setId(R.id.digit5);
    digit5.setFocusable(true);
    digit5.setFilters(new InputFilter[] {new InputFilter.LengthFilter(1)});
    layout.addView(digit5);

    digit1.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (digit1.getText().length() == 1) digit2.requestFocus();
          }

          @Override
          public void afterTextChanged(Editable s) {}
        });
    digit2.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (digit2.getText().length() == 1) digit3.requestFocus();
          }

          @Override
          public void afterTextChanged(Editable s) {}
        });

    digit3.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (digit3.getText().length() == 1) digit4.requestFocus();
          }

          @Override
          public void afterTextChanged(Editable s) {}
        });

    digit4.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (digit4.getText().length() == 1) digit5.requestFocus();
          }

          @Override
          public void afterTextChanged(Editable s) {}
        });
    alert.setView(layout);

    alert.setPositiveButton(
        "Enter",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            String firstD = digit1.getText().toString();
            String secondD = digit2.getText().toString();
            String thirdD = digit3.getText().toString();
            String fourthD = digit4.getText().toString();
            String fifthD = digit5.getText().toString();

            if (!firstD.isEmpty()
                && !secondD.isEmpty()
                && !thirdD.isEmpty()
                && !fourthD.isEmpty()
                && !fifthD.isEmpty()) {
              String tokenize = firstD + secondD + thirdD + fourthD + fifthD;
              sendToken(tokenize);
            } else
              Toast.makeText(
                      getActivity().getApplicationContext(),
                      "Please fill all digits",
                      Toast.LENGTH_SHORT)
                  .show();
          }
        });

    alert.setNegativeButton(
        "Cancel",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
          }
        });
    alert.create().show();
  }

  private boolean isConnected() {
    ConnectivityManager connectivityManager =
        (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
    assert connectivityManager != null;
    // we are connected to a network
    return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()
            == NetworkInfo.State.CONNECTED
        || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()
            == NetworkInfo.State.CONNECTED;
  }

  private void sendToken(final String token) {
    if (isConnected()) {
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
                      boolean expired = jsonObject.getBoolean("expired");
                      if (!expired) {
                        Intent faceTracker =
                            new Intent(
                                getActivity().getApplicationContext(),
                                seniorproject.attendancetrackingsystem.securemode
                                    .FaceTrackerActivity.class);
                        startActivity(faceTracker);
                      } else {
                        toastMessageWithHandle("Secure mod is expired");
                      }
                    } else {
                      String message = jsonObject.getString("message");
                      toastMessageWithHandle(message);
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
              params.put("classroom_id", String.valueOf(classroom_id));
              params.put("token_value", token);
              params.put("operation", "enter-token");
              return params;
            }
          };

      DatabaseManager.getmInstance(getActivity().getApplicationContext()).execute(request);
    } else {
      toastMessageWithHandle("This action requires a network connection");
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    timer.cancel();
  }

  private void addAllLatestCourses() {
    for (LatestCourses x : latestCourses) {
      messages.add(x.toString());
      listView.setAdapter(adapter);
    }
  }

  private void getLatestCoursesList() {
    Log.d("get", "run");
    if (!isConnected()) return;
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
                    timer.cancel();
                    return;
                  }
                } catch (JSONException e) {
                  // do nothing
                }
                try {
                  JSONArray jsonArray = new JSONArray(response);
                  if (jsonArray.length() > 0) latestCourses.clear();
                  for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    LatestCourses temp =
                        new LatestCourses(
                            jsonObject.getString("course_code"),
                            jsonObject.getString("date"),
                            jsonObject.getString("hour"),
                            jsonObject.getInt("status"));
                    latestCourses.add(temp);
                  }
                  addAllLatestCourses();
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
            params.put("operation", "last-15-lectures");
            return params;
          }
        };
    try {
      DatabaseManager.getmInstance(getActivity().getApplicationContext()).execute(request);
    } catch (NullPointerException e) {
      // do nothing
    }
  }

  private void toastMessageWithHandle(final String text) {
    handler.post(
        new Runnable() {
          @Override
          public void run() {
            Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_SHORT).show();
          }
        });
  }

  private class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      course_code = intent.getStringExtra("course_code");
      classroom_id = intent.getIntExtra("classroom_id", 0);
      secure_mode = intent.getBooleanExtra("secure", false);
      expired = intent.getBooleanExtra("expired", false);
      if (secure_mode && !expired) {
        listView.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
              @Override
              public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                  handler.post(
                      new Runnable() {
                        @Override
                        public void run() {
                          buildAlertDialog();
                        }
                      });
                }
              }
            });
      } else {
        listView.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
              @Override
              public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // clearing on item click event
              }
            });
      }
      showMessages();
    }
  }

  class LatestCourses {
    String date;
    String hour;
    String course_code;
    int status;

    LatestCourses(String course_code, String date, String hour, int status) {
      this.date = date;
      this.hour = hour;
      this.course_code = course_code;
      this.status = status;
    }

    @Override
    public String toString() {
      String output = date + " " + hour + " - " + course_code;
      if (status == 0) {
        output = output + " [Absent]";
      } else if (status == 1) {
        output = output + " [Nearly-Attended]";
      } else if (status == 2 || status == 3) {
        output = output + " [Attended]";
      }
      return output;
    }
  }
}
