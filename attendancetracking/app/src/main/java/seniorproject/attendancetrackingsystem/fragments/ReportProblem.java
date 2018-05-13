package seniorproject.attendancetrackingsystem.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.DatabaseManager;
import seniorproject.attendancetrackingsystem.helpers.SessionManager;

public class ReportProblem extends Fragment {

  private EditText issue;
  private EditText messages;
  private Handler handler;

  public ReportProblem() {}

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.report_problem, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    handler = new Handler(Looper.getMainLooper());
    issue = view.findViewById(R.id.issue);
    messages = view.findViewById(R.id.messages);
    Button save = view.findViewById(R.id.sendMessage);
    save.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            String i = issue.getText().toString();
            i = i.trim();
            String m = messages.getText().toString();
            m = m.trim();
            if (i.isEmpty() || m.isEmpty()) {
              toastWithHandler("Empty field error");
            } else {
              setIssue(issue.getText().toString(), messages.getText().toString());
            }
          }
        });
  }

  private void toastWithHandler(final String text) {
    handler.post(
        new Runnable() {
          @Override
          public void run() {
            Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_SHORT).show();
          }
        });
  }

  private void setIssue(final String problem, final String message) {
    StringRequest request =
        new StringRequest(
            Request.Method.POST,
            DatabaseManager.SetOperations,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
                try{
                  JSONObject jsonObject = new JSONObject(response);
                  boolean result = jsonObject.getBoolean("success");
                  if(!result){
                    toastWithHandler(jsonObject.getString("message"));
                  }else
                  {
                    toastWithHandler("Issue is sent successfully");
                    issue.setText("");
                    messages.setText("");
                  }
                }catch (JSONException e){
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
            params.put("subject", problem);
            params.put("message", message);
            params.put(
                "sender_mail",
                new SessionManager(getActivity().getApplicationContext())
                    .getUserDetails()
                    .get(SessionManager.KEY_USER_MAIL));
            params.put("operation","issue");
            return params;
          }
        };
    try {
      DatabaseManager.getmInstance(getActivity().getApplicationContext()).execute(request);
    } catch (NullPointerException e) {
      // do nothing
    }
  }
}
