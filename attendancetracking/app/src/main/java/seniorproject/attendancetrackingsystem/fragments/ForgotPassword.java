package seniorproject.attendancetrackingsystem.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.DatabaseManager;

public class ForgotPassword extends Fragment {
  private EditText mail;
  private AwesomeValidation awesomeValidation;
  private Bundle args;
  private Handler handler;

  public ForgotPassword() {}

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.forgot_password, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    args = getArguments();
    if (args == null || Objects.requireNonNull(args.getString("user_type")).isEmpty()) Objects.requireNonNull(getActivity()).onBackPressed();
    else {
      initElements(view);
      addValidation(Objects.requireNonNull(args.getString("user_type")));
    }
  }

  private void initElements(View view) {
    handler = new Handler(Looper.getMainLooper());
    awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

    Switch loginSwitch = Objects.requireNonNull(getActivity()).findViewById(R.id.role_switch);
    if (loginSwitch != null) loginSwitch.setVisibility(View.INVISIBLE);

    mail = view.findViewById(R.id.forgot_mail);
    Button sendMail = view.findViewById(R.id.send_mail);

    sendMail.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (awesomeValidation.validate()) {
              checkAndSendMail(mail.getText().toString());
            }
          }
        });
  }

  private void toastMessage(final String text) {
    handler.post(
        new Runnable() {
          @Override
          public void run() {
            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), text, Toast.LENGTH_LONG).show();
          }
        });
  }

  private void addValidation(String user_type) {
    awesomeValidation.clear();
    if (user_type.equals("student"))
      awesomeValidation.addValidation(
          getActivity(),
          R.id.forgot_mail,
          "^([c]|(20))[0-9]{7}@student.cankaya.edu.tr$",
          R.string.email_error);
    else
      awesomeValidation.addValidation(
          getActivity(),
          R.id.forgot_mail,
          "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@cankaya.edu.tr$",
          R.string.email_error);
  }

  private void checkAndSendMail(final String mail) {
    StringRequest request =
        new StringRequest(
            Request.Method.POST,
            DatabaseManager.AccountOperations,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
                try {
                  JSONObject jsonObject = new JSONObject(response);
                  boolean result = jsonObject.getBoolean("success");
                  if (result) {
                    toastMessage("A recovery mail has been sent to your mail address");
                    Objects.requireNonNull(getActivity()).onBackPressed();
                  } else {
                    toastMessage(jsonObject.getString("message"));
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
            params.put("mail_address", mail);
            params.put("user_type", args.getString("user_type"));
            params.put("operation", "recovery");
            return params;
          }
        };
    try {
      DatabaseManager.getInstance(Objects.requireNonNull(getActivity()).getApplicationContext()).execute(request);
    } catch (NullPointerException e) {
      // do nothing
    }
  }
}
