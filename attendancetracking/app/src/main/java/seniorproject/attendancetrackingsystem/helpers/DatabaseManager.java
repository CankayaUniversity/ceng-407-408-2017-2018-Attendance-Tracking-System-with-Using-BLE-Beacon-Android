package seniorproject.attendancetrackingsystem.helpers;

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
import seniorproject.attendancetrackingsystem.utils.GivenCourses;
import seniorproject.attendancetrackingsystem.utils.Globals;
import seniorproject.attendancetrackingsystem.utils.Lecturer;
import seniorproject.attendancetrackingsystem.utils.Student;
import seniorproject.attendancetrackingsystem.utils.TakenCourses;

public class DatabaseManager {

  public static final String Domain = "http://attendancesystem.xyz/attendancetracking/";
  public static final String AccountOperations = Domain + "account-operations.php";
  public static final String GetOperations = Domain + "get-something.php";
  public static final String SetOperations = Domain + "set-something.php";
  private static DatabaseManager mInstance;
  private final JsonHelper jsonHelper;
  private final Context context;
  private RequestQueue requestQueue;

  private DatabaseManager(Context context) {
    this.context = context;
    requestQueue = getRequestQueue();
    jsonHelper = JsonHelper.getmInstance(context);
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
                        ((Globals) context.getApplicationContext()).setLoggedUser(actor);
                        SessionManager sessionManager = new SessionManager(context);
                        sessionManager.createLoginSession(
                            jsonObject.getString("user_type"),
                            actor.getName(),
                            actor.getSurname(),
                            actor.getMail(),
                            actor.getId());
                        Intent intent;
                        if (jsonObject.getString("user_type").equals("student"))
                          intent = new Intent(context, StudentActivity.class);
                        else intent = new Intent(context, LecturerActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                      } else {
                        Toast.makeText(
                                context.getApplicationContext(),
                                jsonObject.getString("message"),
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
                        Toast.makeText(
                                context.getApplicationContext(),
                                "Registration is " + "successful",
                                Toast.LENGTH_LONG)
                            .show();
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                      } else {
                        Toast.makeText(
                                context.getApplicationContext(),
                                jsonObject.getString("message"),
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
      case "set-beacon":
        request =
            new StringRequest(
                Request.Method.POST,
                SetOperations,
                new Response.Listener<String>() {
                  @Override
                  public void onResponse(String response) {
                    try {
                      JSONObject jsonObject = new JSONObject(response);
                      boolean result = jsonObject.getBoolean("success");
                      if (result) {
                        Toast.makeText(context, "Successful", Toast.LENGTH_LONG).show();
                      } else {
                        Toast.makeText(
                                context.getApplicationContext(),
                                jsonObject.getString("message"),
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
                    Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_LONG)
                        .show();
                  }
                }) {
              @Override
              protected Map<String, String> getParams() {
                params.put("operation", "beacon-mac");
                return params;
              }
            };
        break;
      case "get-taken-courses":
        request =
            new StringRequest(
                Request.Method.POST,
                GetOperations,
                new Response.Listener<String>() {
                  @Override
                  public void onResponse(String response) {
                    ArrayList<TakenCourses> takenCourses = jsonHelper.parseTakenCourses(response);
                    Actor actor = ((Globals) context.getApplicationContext()).getLoggedUser();
                    if (actor instanceof Student) ((Student) actor).setTakenCourses(takenCourses);
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
                params.put("operation", "taken-courses");
                return params;
              }
            };
        break;
      case "get-given-courses":
        request =
            new StringRequest(
                Request.Method.POST,
                GetOperations,
                new Response.Listener<String>() {
                  @Override
                  public void onResponse(String response) {
                    ArrayList<GivenCourses> givenCourses = jsonHelper.parseGivenCourses(response);
                    Actor actor = ((Globals) context.getApplicationContext()).getLoggedUser();
                    if (actor instanceof Lecturer) ((Lecturer) actor).setGivenCourses(givenCourses);
                  }
                },
                new Response.ErrorListener() {
                  @Override
                  public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_LONG)
                        .show();
                  }
                }) {
              protected Map<String, String> getParams() {
                params.put("operation", "given-courses");
                return params;
              }
            };
        break;
      case "change-password":
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
                        Toast.makeText(
                                context.getApplicationContext(),
                                "Password has been successfully changed",
                                Toast.LENGTH_LONG)
                            .show();
                      } else {
                        Toast.makeText(
                                context.getApplicationContext(),
                                jsonObject.getString("message"),
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
                    Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_LONG)
                        .show();
                  }
                }) {
              @Override
              protected Map<String, String> getParams() {
                params.put("operation", "change-password");
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

  private StringRequest createStringRequest(String action, String param) {
    StringRequest request = null;
    switch (action) {
      case "get":
        if (param.equals("user-info")) {
          request =
              new StringRequest(
                  Request.Method.POST,
                  GetOperations,
                  new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                      Actor actor = jsonHelper.parseUser(response);
                      ((Globals) context.getApplicationContext()).setLoggedUser(actor);
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
                  Map<String, String> userInfo = new SessionManager(context).getUserDetails();
                  Map<String, String> postParameters = new HashMap<>();
                  postParameters.put("user_id", userInfo.get(SessionManager.KEY_USER_ID));
                  postParameters.put("user_type", userInfo.get(SessionManager.KEY_USER_TYPE));
                  postParameters.put("operation", "user-info");
                  return postParameters;
                }
              };
        }

        break;
      default:
    }
    return request;
  }

  private void showDialog(final String message) {}

  public void execute(String action, Map<String, String> params) {
    getmInstance(context).addToRequestQueue(createStringRequest(action, params));
  }

  public void execute(String action, String param, ArrayList<String> array) {
    getmInstance(context).addToRequestQueue(createStringRequest(action, param, array));
  }

  public void execute(String action, String param) {
    getmInstance(context).addToRequestQueue(createStringRequest(action, param));
  }

  public void execute(StringRequest stringRequest) {
    getmInstance(context).addToRequestQueue(stringRequest);
  }
}
