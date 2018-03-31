package seniorproject.attendancetrackingsystem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Map;
import java.util.Objects;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.SessionManager;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    SessionManager session = new SessionManager(getApplicationContext());
    if (session.isLoggedIn()) {
      Map<String, String> userInfo = session.getUserDetails();
      Intent intent;
      if(userInfo.get(SessionManager.KEY_USER_TYPE).equals("student"))
        intent = new Intent(MainActivity.this, StudentActivity.class);
        else
          intent = new Intent(MainActivity.this, LecturerActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
    }
    Button login = findViewById(R.id.login_button);
    Button register = findViewById(R.id.register_button);
    Intent intent = getIntent();
    Bundle bundle = intent.getExtras();
    if (bundle != null) {
      try {
        String message = Objects.requireNonNull(bundle.get("message")).toString();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
      } catch (NullPointerException e) {
        e.printStackTrace();
      }
    }
    login.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
              }
            });

    register.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                Intent registerIntent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(registerIntent);
              }
            });
  }

  @Override
  public void onBackPressed() {

  }
}
