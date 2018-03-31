package seniorproject.attendancetrackingsystem.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.Map;
import java.util.Objects;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.fragments.AssignmentFragment;
import seniorproject.attendancetrackingsystem.fragments.ReportFragment;
import seniorproject.attendancetrackingsystem.fragments.ServicesFragment;
import seniorproject.attendancetrackingsystem.fragments.SettingsFragment;
import seniorproject.attendancetrackingsystem.fragments.WelcomeFragment;
import seniorproject.attendancetrackingsystem.helpers.SessionManager;
import seniorproject.attendancetrackingsystem.utils.Actor;
import seniorproject.attendancetrackingsystem.utils.Globals;
import seniorproject.attendancetrackingsystem.utils.Lecturer;
import seniorproject.attendancetrackingsystem.utils.Student;

public class StudentActivity extends AppCompatActivity {

  private BottomNavigationView mainNav;

  private WelcomeFragment welcomeFragment;
  private ServicesFragment servicesFragment;
  private SettingsFragment settingsFragment;
  private ReportFragment reportFragment;
  private AssignmentFragment assignmentFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    SessionManager session = new SessionManager(getApplicationContext());
    session.checkLogin();
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_student);
    Toolbar toolbar = findViewById(R.id.toolbar);
    // toolbar.setTitle("Ç.Ü. Attendance Tracking System");
    setSupportActionBar(toolbar);
    mainNav = findViewById(R.id.main_nav);
    setLoggedUser();
    welcomeFragment = new WelcomeFragment();
    servicesFragment = new ServicesFragment();
    settingsFragment = new SettingsFragment();
    reportFragment = new ReportFragment();
    assignmentFragment = new AssignmentFragment();
    setFragment(welcomeFragment);

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

                    return true;
                  case R.id.nav_settings:
                    setFragment(settingsFragment);
                    Objects.requireNonNull(getSupportActionBar()).setLogo(R.drawable.kdefault);
                    getSupportActionBar().setTitle("Ç.Ü. Attendance Tracking System");

                    getSupportActionBar().setSubtitle("/Settings");

                    return true;
                  case R.id.nav_report:
                    setFragment(reportFragment);
                    Objects.requireNonNull(getSupportActionBar()).setLogo(R.drawable.kdefault);
                    getSupportActionBar().setTitle("Ç.Ü. Attendance Tracking System");

                    getSupportActionBar().setSubtitle("/Report");
                    return true;
                  case R.id.nav_assignment:
                    setFragment(assignmentFragment);
                    Objects.requireNonNull(getSupportActionBar()).setLogo(R.drawable.kdefault);
                    getSupportActionBar().setTitle("Ç.Ü. Attendance Tracking System");

                    getSupportActionBar().setSubtitle("/Assignment");
                    return true;
                  case R.id.nav_services:
                    setFragment(servicesFragment);
                    Objects.requireNonNull(getSupportActionBar()).setLogo(R.drawable.kdefault);
                    getSupportActionBar().setTitle("Ç.Ü. Attendance Tracking System");

                    getSupportActionBar().setSubtitle("/Services");
                    return true;
                  default:
                    return false;
                }
              }
            });
  }

  private void setFragment(Fragment fragment) {
    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
    fragmentTransaction.replace(R.id.main_frame, fragment);
    fragmentTransaction.commit();
  }

  @Override
  public void onBackPressed() {
    setFragment(welcomeFragment);
    mainNav.setSelectedItemId(R.id.nav_home);
  }

  private void setLoggedUser() {
    if (((Globals) getApplication()).getLoggedUser() == null) {
      SessionManager sessionManager = new SessionManager(getApplicationContext());
      Map<String, String> userInfo = sessionManager.getUserDetails();
      Actor actor;
      if (userInfo.get(SessionManager.KEY_USER_TYPE).equals("student"))
        actor = new Student();
      else
        actor = new Lecturer();

      actor.setId(Integer.parseInt(userInfo.get(SessionManager.KEY_USER_ID)));
      actor.setName(userInfo.get(SessionManager.KEY_USER_NAME));
      actor.setSurname(userInfo.get(SessionManager.KEY_USER_SURNAME));
      actor.setMail(userInfo.get(SessionManager.KEY_USER_MAIL));
      //TODO AN SQL QUERY WILL WORK HERE TO RECIEVE LOGGED USER INFO
      ((Globals) getApplication()).setLoggedUser(actor);
    }
  }
}
