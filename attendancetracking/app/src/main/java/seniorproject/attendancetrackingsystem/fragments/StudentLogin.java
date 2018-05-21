package seniorproject.attendancetrackingsystem.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import java.util.HashMap;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.DatabaseManager;
import seniorproject.attendancetrackingsystem.helpers.SessionManager;

public class StudentLogin extends Fragment implements View.OnClickListener {
  private EditText etStudentId;
  private EditText etPassword;
  private AwesomeValidation awesomeValidation;

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.student_login, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initElements(view);
    awesomeValidation.addValidation(
        getActivity(), R.id.input_school_id, "^([cC]|(20))[0-9]{7}$", R.string.studentIDerror);
  }

  private void initElements(View view) {
    Switch roleSwitch = getActivity().findViewById(R.id.role_switch);
    roleSwitch.setVisibility(View.VISIBLE);
    Button loginButton = view.findViewById(R.id.login_button);
    Button forgotPassword = view.findViewById(R.id.missing_password);
    forgotPassword.setOnClickListener(this);
    loginButton.setOnClickListener(this);
    etStudentId = view.findViewById(R.id.input_school_id);
    etPassword = view.findViewById(R.id.input_password);
    awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
  }

  @SuppressLint("HardwareIds")
  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.login_button) {
      if (awesomeValidation.validate() && !etPassword.getText().toString().isEmpty()) {
        String studentID = etStudentId.getText().toString();
        String password = etPassword.getText().toString();
        char first = studentID.charAt(0);
        if (first == 'c' || first == 'C') {
          String sub = studentID.substring(1);
          studentID = "20" + sub;
        }
        SessionManager sessionManager = new SessionManager(getActivity().getApplicationContext());
        String android_id;
        if(sessionManager.isEmptyAndroidId())
          android_id = Settings.Secure.getString(getActivity().getContentResolver(), Settings
                  .Secure.ANDROID_ID);
        else
        android_id = sessionManager.getAndroidId();

        HashMap<String, String> postParameters = new HashMap<>();
        postParameters.put("username", studentID);
        postParameters.put("password", password);
        postParameters.put("android_id", android_id);
        postParameters.put("type", "studentLogin");
        DatabaseManager.getmInstance(getActivity()).execute("login", postParameters);

      } else if (etPassword.getText().toString().isEmpty())
        etPassword.setError("Enter your password");
    } else if (v.getId() == R.id.missing_password) {
      ForgotPassword f = new ForgotPassword();
      Bundle bundle = new Bundle();
      bundle.putString("user_type", "student");
      f.setArguments(bundle);
      getActivity()
          .getSupportFragmentManager()
          .beginTransaction()
          .replace(R.id.login_layout, f)
          .commit();
    }
  }
}
