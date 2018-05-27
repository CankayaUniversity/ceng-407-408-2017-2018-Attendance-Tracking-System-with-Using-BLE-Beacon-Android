package seniorproject.attendancetrackingsystem.activities;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.fragments.ReportFragment;
import seniorproject.attendancetrackingsystem.fragments.ReportProblem;
import seniorproject.attendancetrackingsystem.fragments.WelcomeFragment;
import seniorproject.attendancetrackingsystem.helpers.DatabaseManager;
import seniorproject.attendancetrackingsystem.helpers.ServiceManager;
import seniorproject.attendancetrackingsystem.helpers.SessionManager;

public class StudentActivity extends AppCompatActivity {

  private BottomNavigationView mainNav;
  private AwesomeValidation awesomeValidation;
  private WelcomeFragment welcomeFragment;
  private ReportFragment reportFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    SessionManager session = new SessionManager(getApplicationContext());
    session.checkLogin();
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_student);
    Toolbar toolbar = findViewById(R.id.toolbar);
    if (!isServiceIsRunning(ServiceManager.class))
      startService(new Intent(this, ServiceManager.class));
    setSupportActionBar(toolbar);
    mainNav = findViewById(R.id.main_nav);
    welcomeFragment = new WelcomeFragment();
    reportFragment = new ReportFragment();

    setFragment(welcomeFragment);
    Objects.requireNonNull(getSupportActionBar()).setLogo(R.drawable.kdefault);
    getSupportActionBar().setTitle("Ç.Ü. Attendance Tracking System");
    getSupportActionBar().setSubtitle("/Home");
    mainNav.setOnNavigationItemSelectedListener(
        new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
              case R.id.nav_home:
                setFragment(welcomeFragment);
                Objects.requireNonNull(getSupportActionBar()).setLogo(R.drawable.kdefault);
                getSupportActionBar().setTitle("Ç.Ü. Attendance Tracking System");
                getSupportActionBar().setSubtitle("/Home");
                break;

              case R.id.nav_report:
                setFragment(reportFragment);
                Objects.requireNonNull(getSupportActionBar()).setLogo(R.drawable.kdefault);
                getSupportActionBar().setTitle("Ç.Ü. Attendance Tracking System");
                getSupportActionBar().setSubtitle("/Report");
                break;

              case R.id.logout:
                if (new ServiceManager().isLogFileExists()) {
                  Toast.makeText(
                          StudentActivity.this,
                          "While attendance tracking, you cannot " + "logout from the system",
                          Toast.LENGTH_SHORT)
                      .show();
                  return false;
                } else {
                  final AlertDialog alertDialog =
                      new AlertDialog.Builder(StudentActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                          .create();
                  alertDialog.setTitle("Warning");
                  alertDialog.setMessage("Are you sure to logout from the system?");
                  alertDialog.setButton(
                      DialogInterface.BUTTON_NEGATIVE,
                      "Cancel",
                      new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                          alertDialog.dismiss();
                        }
                      });
                  alertDialog.setButton(
                      DialogInterface.BUTTON_POSITIVE,
                      "Logout",
                      new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                          SessionManager session = new SessionManager(getApplicationContext());
                          session.logoutUser();
                        }
                      });
                  alertDialog.show();
                }

                break;
              default:
                break;
            }
            return true;
          }
        });
  }

  private void setFragment(Fragment fragment) {
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.replace(R.id.main_frame, fragment);
    fragmentTransaction.commit();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.toolbar_menu_student, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.toString().equals("Change Password")) {
      buildAlertDialog();
    } else if (item.toString().equals("Report Problem")) {
      mainNav.getMenu().findItem(R.id.nav_report).setChecked(true);
      ReportProblem f = new ReportProblem();
      Objects.requireNonNull(getSupportActionBar()).setLogo(R.drawable.kdefault);
      getSupportActionBar().setTitle("Ç.Ü. Attendance Tracking System");
      getSupportActionBar().setSubtitle("/Report Problem");
      getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, f).commit();
    }

    return super.onOptionsItemSelected(item);
  }

  private void buildAlertDialog() {
    final LinearLayout layout = new LinearLayout(this);
    awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
    layout.setOrientation(LinearLayout.VERTICAL);

    final EditText oldPassword = new EditText(this);
    oldPassword.setHint("Old password");
    oldPassword.setTextColor(Color.BLACK);
    oldPassword.setHintTextColor(Color.BLACK);
    oldPassword.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
    oldPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    oldPassword.setId(R.id.old_password);
    layout.addView(oldPassword);

    final EditText newPassword = new EditText(this);
    newPassword.setHint("New password");
    newPassword.setTextColor(Color.BLACK);
    newPassword.setHintTextColor(Color.BLACK);
    newPassword.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
    newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    newPassword.setId(R.id.new_password);

    layout.addView(newPassword);

    final EditText newPasswordRepeat = new EditText(this);
    newPasswordRepeat.setHint("Re-enter new password");
    newPasswordRepeat.setTextColor(Color.BLACK);
    newPasswordRepeat.setHintTextColor(Color.BLACK);
    newPasswordRepeat.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
    newPasswordRepeat.setInputType(
        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    newPasswordRepeat.setId(R.id.new_password_repeat);

    layout.addView(newPasswordRepeat);

   final AlertDialog change_dialog = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT)
            .setView(layout)
            .setPositiveButton(
                    "Change",
                    null)
            .setNegativeButton(
                    "Cancel",
                    new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                      }
                    })
            .create();

    change_dialog.setOnShowListener(new DialogInterface.OnShowListener() {
      @Override
      public void onShow(DialogInterface dialog) {

        Button pos_button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
        pos_button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
                awesomeValidation.addValidation(newPassword,
                        "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[!_*.-]).{6,}$",
                        "Password should be at least 6 characters.\n" +
                                "Password should contains at least 1 uppercase letter\n" +
                                "1 digit and 1 special character (. - _ ! *)");
                awesomeValidation.addValidation(newPasswordRepeat,
                        "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[!_*.-]).{6,}$",
                        "Password should be at least 6 characters.\n" +
                                "        Password should contains at least 1 uppercase letter\n" +
                                "        1 digit and 1 special character (. - _ ! *)");

                String old_password = oldPassword.getText().toString();
                String new_password = newPassword.getText().toString();
                String new_password_repeat = newPasswordRepeat.getText().toString();

                if (old_password.isEmpty() || new_password.isEmpty() || new_password_repeat.isEmpty()) {
                  Toast.makeText(getApplicationContext(), "Empty field error", Toast.LENGTH_SHORT)
                          .show();
                  return;
                }

                if (!new_password.equals(new_password_repeat)) {
                  Toast.makeText(
                          getApplicationContext(), "New passwords don't match", Toast.LENGTH_SHORT)
                          .show();
                  return;
                }

                if (awesomeValidation.validate()) {
                  Map<String, String> params = new HashMap<>();
                  SessionManager session = new SessionManager(getApplicationContext());
                  Map<String, String> userInfo = session.getUserDetails();
                  params.put("old_password", old_password);
                  params.put("new_password", new_password);
                  params.put("user_type", userInfo.get(SessionManager.KEY_USER_TYPE));
                  params.put("user_id", userInfo.get(SessionManager.KEY_USER_ID));
                  DatabaseManager.getInstance(getApplicationContext())
                          .execute("change-password", params);
                  change_dialog.dismiss();
                }
                else
                {
                  Toast.makeText(getApplicationContext(),"Password should be at least 6 characters.\n"+
                          "Password should contains at least 1 uppercase letter\n" +
                          "1 digit and 1 special character (. - _ ! *)",Toast.LENGTH_LONG).show();
                }
          }
        });

      }
    });
    change_dialog.show();
  }

  private boolean isServiceIsRunning(Class<?> serviceClass) {
    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningServiceInfo service :
        Objects.requireNonNull(manager).getRunningServices(Integer.MAX_VALUE)) {
      if (serviceClass.getName().equals(service.service.getClassName())) return true;
    }
    return false;
  }

  @Override
  public void onBackPressed() {
    setFragment(welcomeFragment);
    mainNav.setSelectedItemId(R.id.nav_home);
  }
}
