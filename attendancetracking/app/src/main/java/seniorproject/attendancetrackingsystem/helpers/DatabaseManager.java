package seniorproject.attendancetrackingsystem.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import seniorproject.attendancetrackingsystem.activities.LecturerActivity;
import seniorproject.attendancetrackingsystem.activities.MainActivity;
import seniorproject.attendancetrackingsystem.activities.StudentActivity;
import seniorproject.attendancetrackingsystem.utils.Actor;
import seniorproject.attendancetrackingsystem.utils.Course;
import seniorproject.attendancetrackingsystem.utils.Department;
import seniorproject.attendancetrackingsystem.utils.Globals;

public class DatabaseManager {

  private static final String Domain = "http://attendancesystem.xyz/attendancetracking/";
  private static final String AccountOperations = Domain + "account-operations.php";
  private static final String GetOperations = Domain + "get-something.php";
  private static DatabaseManager mInstance;
  private final JsonHelper jsonHelper;
  private final Context context;
  private RequestQueue requestQueue;
  private final AlertDialog alertDialog;

  private DatabaseManager(Context context) {
    this.context = context;
    requestQueue = getRequestQueue();
    jsonHelper = JsonHelper.getmInstance(context);
    alertDialog = new AlertDialog.Builder(context).create();
  }

  /** synchronize the DatabaseManager to make common for whole activity. */
  public static synchronized DatabaseManager getmInstance(Context context) {
    if (mInstance == null) {
      mInstance = new DatabaseManager(context);
    }
    return mInstance;
  }

  private RequestQueue getRequestQueue() {
    if (requestQueue == null) {
      requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }
    return requestQueue;
  }

  private <T> void addToRequestQueue(Request<T> request) {
    requestQueue.add(request);
  }

  private StringRequest createStringRequest(String action, final Map<String, String> params) {
    StringRequest request = null;
    switch (action) {
      case "login":
        request =
            new StringRequest(
                Request.Method.POST,
                AccountOperations,
                new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                  try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean result = jsonObject.getBoolean("success");
                    if (result) {
                      Actor actor = jsonHelper.parseUser(response);
                      ((Globals)context.getApplicationContext()).setLoggedUser(actor);
                      SessionManager sessionManager = new SessionManager(context);
                      sessionManager.createLoginSession(jsonObject.getString("user_type"), actor
                              .getName(), actor.getSurname(), actor.getMail(), actor.getId());
                      Intent intent;
                      if(jsonObject.getString("user_type").equals("student"))
                        intent = new Intent(context, StudentActivity.class);
                      else
                        intent = new Intent(context, LecturerActivity.class);
                      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                      context.startActivity(intent);
                    } else {
                      alertDialog.setTitle("Login Failed");
                      alertDialog.setMessage(jsonObject.getString("message"));
                      alertDialog.show();
                    }
                  } catch (JSONException e) {
                    e.printStackTrace();
                  }
                }
              },
                new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                  Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_LONG)
                      .show();
                }
              }) {
              @Override
              protected Map<String, String> getParams() {
                params.put("operation", "login");
                return params;
              }
            };
        break;
      case "register":
        request =
            new StringRequest(
                Request.Method.POST,
                AccountOperations,
                new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                  try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean result = jsonObject.getBoolean("success");
                    if (result) {
                      Intent intent = new Intent(context, MainActivity.class);
                      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                      context.startActivity(intent);
                    } else {
                      alertDialog.setTitle("Registration Error");
                      alertDialog.setMessage(jsonObject.getString("message"));
                    }
                  } catch (JSONException e) {
                    e.printStackTrace();
                  }
                }
              },
                new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                  Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_LONG)
                      .show();
                }
              }) {
              @Override
              protected Map<String, String> getParams() {
                params.put("operation", "register");
                return params;
              }
            };
        break;
      default:
    }
    return request;
  }

  private StringRequest createStringRequest(
      String action, final String param, final ArrayList<String> array) {
    StringRequest request = null;
    switch (action) {
      // GET OPERATIONS
      case "get":
        if (param.equals("department-list")) {
          request =
              new StringRequest(
                  Request.Method.POST,
                  GetOperations,
                  new Response.Listener<String>() {
                  @Override
                  public void onResponse(String response) {
                    ((Globals) context.getApplicationContext())
                        .setDepartments(
                            JsonHelper.getmInstance(context).parseDepartmentList(response));
                    for (Department department :
                        ((Globals) context.getApplicationContext()).getDepartments()) {
                      array.add(department.getDepartmentName());
                    }
                  }
                },
                  new Response.ErrorListener() {
                  @Override
                  public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_LONG)
                        .show();
                  }
                }) {
              @Override
              protected Map<String, String> getParams() {
                Map<String, String> postParameters = new HashMap<>();
                postParameters.put("operation", "department-list");
                return postParameters;
              }
            };
        } else if (param.equals("course-list")) {
          request =
              new StringRequest(
                  Request.Method.POST,
                  GetOperations,
                  new Response.Listener<String>() {
                  @Override
                  public void onResponse(String response) {
                    ((Globals) context.getApplicationContext())
                        .setCourses(jsonHelper.parseCourseList(response));
                    for (Course course :
                        ((Globals) context.getApplicationContext()).getCourses()) {
                      array.add(course.getCourseName());
                    }
                  }
                },
                new Response.ErrorListener() {
                  @Override
                  public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_LONG)
                        .show();
                  }
                }) {
                @Override
                protected Map<String, String> getParams() {
                  Map<String, String> postParameters = new HashMap<>();
                  postParameters.put("operation", "course-list");
                  return postParameters;
                }
              };
        }
        break;
      case "set":
        // set operations
        break;
      default:
    }
    return request;
  }

  public void execute(String action, Map<String, String> params) {
    getmInstance(context).addToRequestQueue(createStringRequest(action, params));
  }

  public void execute(String action, String param, ArrayList<String> array) {
    getmInstance(context).addToRequestQueue(createStringRequest(action, param, array));
  }
}
