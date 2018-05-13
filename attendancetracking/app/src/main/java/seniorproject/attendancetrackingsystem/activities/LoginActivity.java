package seniorproject.attendancetrackingsystem.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.fragments.LecturerLogin;
import seniorproject.attendancetrackingsystem.fragments.StudentLogin;

public class LoginActivity extends AppCompatActivity {
  private Switch roleSwitch;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    if (savedInstanceState == null) {

      getSupportFragmentManager()
          .beginTransaction()
          .replace(R.id.login_layout, new StudentLogin())
          .commit();
    }

    roleSwitch = findViewById(R.id.role_switch);
    roleSwitch.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (roleSwitch.isChecked()) {
              getSupportFragmentManager()
                  .beginTransaction()
                  .replace(R.id.login_layout, new LecturerLogin())
                  .commit();
            } else {
              getSupportFragmentManager()
                  .beginTransaction()
                  .replace(R.id.login_layout, new StudentLogin())
                  .commit();
            }
          }
        });
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }
}
