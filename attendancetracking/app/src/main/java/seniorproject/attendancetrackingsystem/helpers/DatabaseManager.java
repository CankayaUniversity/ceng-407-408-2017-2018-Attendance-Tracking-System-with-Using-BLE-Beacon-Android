package seniorproject.attendancetrackingsystem.helpers;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

import seniorproject.attendancetrackingsystem.activities.WelcomePage;
import seniorproject.attendancetrackingsystem.utils.Department;

public class DatabaseManager {

    private static final String Domain = "http://attendancesystem.xyz/attendancetracking/";
    private static final String AccountOperations = Domain + "account-operations.php";
    private static final String GetOperations = Domain + "get-something.php";
    private static DatabaseManager mInstance;
    private static JsonHelper jsonHelper;
    private static Context context;
    private RequestQueue requestQueue;
    private AlertDialog alertDialog;

    private DatabaseManager(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
        jsonHelper = JsonHelper.getmInstance(context);
    }

    public static synchronized DatabaseManager getmInstance(Context context) {
        if (mInstance == null)
            mInstance = new DatabaseManager(context);
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        requestQueue.add(request);
    }

    private StringRequest createStringRequest(String action, final Map<String, String> params) {
        StringRequest request = null;
        switch (action) {
            case "login":
                request = new StringRequest(Request.Method.POST, AccountOperations,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean result = jsonObject.getBoolean("success");
                                    if (result) {
                                        SessionManager sessionManager = new SessionManager(context);
                                        sessionManager.createLoginSession(params.get("type"), params.get("username"));
                                        Intent intent = new Intent(context, WelcomePage.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                    } else {
                                        alertDialog = new AlertDialog.Builder(context).create();
                                        alertDialog.setTitle("Login Failed");
                                        //TODO Error message will come in response JSON array
                                        alertDialog.setMessage("Wrong username or password");
                                        alertDialog.show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> postParameters = params;
                        postParameters.put("operation", "login");
                        return postParameters;
                    }
                };
                break;
        }
        return request;
    }

    public StringRequest createStringRequest(String action, String param, final ArrayList<String> array) {
        StringRequest request = null;
        switch (action) {
            //GET OPERATIONS
            case "get":
                switch (param) {
                    case "department-list":
                        request = new StringRequest(Request.Method.POST, GetOperations,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        ArrayList<Department> tempArray = jsonHelper.parseDepartmentList(response);
                                        for (Department department : tempArray) {
                                            array.add(department.getDepartmentName());
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> postParameters = new HashMap<String, String>();
                                postParameters.put("operation", "department-list");
                                return postParameters;
                            }
                        };
                        break;
                }
                break;
            case "set":
                //set operations
                break;
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
