package seniorproject.attendancetrackingsystem.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.fragments.LecturerRegister;
import seniorproject.attendancetrackingsystem.fragments.StudentRegister;

public class RegistrationActivity extends AppCompatActivity {
  private Switch roleSwitch;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_registration);
    if (savedInstanceState == null) {
      getSupportFragmentManager()
          .beginTransaction()
          .replace(R.id.register_layout, new StudentRegister())
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
                  .replace(R.id.register_layout, new LecturerRegister())
                  .commit();
            } else {
              getSupportFragmentManager()
                  .beginTransaction()
                  .replace(R.id.register_layout, new StudentRegister())
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
