package seniorproject.attendancetrackingsystem.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import java.util.HashMap;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.DatabaseManager;

public class LecturerLogin extends Fragment implements View.OnClickListener {
  private EditText etMail;
  private EditText etPassword;
  private AwesomeValidation awesomeValidation;

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.lecturer_login, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initElements(view);
    awesomeValidation.addValidation(
        getActivity(),
        R.id.input_email,
        "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@cankaya.edu.tr$",
        R.string.emailerror);
  }

  private void initElements(View view) {
    Button loginButton = view.findViewById(R.id.login_button);
    loginButton.setOnClickListener(this);
    etMail = view.findViewById(R.id.input_email);
    etPassword = view.findViewById(R.id.input_password);
    awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
  }

  @Override
  public void onClick(View v) {
    if (awesomeValidation.validate() && !etPassword.getText().toString().isEmpty()) {
      String mail = etMail.getText().toString();
      String password = etPassword.getText().toString();
      HashMap<String, String> postParameters = new HashMap<>();
      postParameters.put("username", mail);
      postParameters.put("password", password);
      postParameters.put("type", "lecturerLogin");
      DatabaseManager.getmInstance(getActivity()).execute("login", postParameters);

    } else if (etPassword.getText().toString().isEmpty()) {
      etPassword.setError("Enter your password");
    }
  }
}
