package seniorproject.attendancetrackingsystem.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Map;
import java.util.Objects;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.fragments.ReportFragmentLecturer;
import seniorproject.attendancetrackingsystem.fragments.WelcomeFragmentLecturer;
import seniorproject.attendancetrackingsystem.helpers.BeaconBuilder;
import seniorproject.attendancetrackingsystem.helpers.SessionManager;
import seniorproject.attendancetrackingsystem.utils.Actor;
import seniorproject.attendancetrackingsystem.utils.Globals;
import seniorproject.attendancetrackingsystem.utils.Lecturer;
import seniorproject.attendancetrackingsystem.utils.Student;

public class LecturerActivity extends AppCompatActivity {

  private BottomNavigationView mainNav;

  private WelcomeFragmentLecturer welcomeFragmentLecturer;
  private ReportFragmentLecturer reportFragmentLecturer;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    SessionManager session = new SessionManager(getApplicationContext());
    session.checkLogin();
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_lecturer);
    Toolbar toolbar = findViewById(R.id.toolbar);

    setSupportActionBar(toolbar);
    mainNav = findViewById(R.id.main_nav);
    setLoggedUser();
    welcomeFragmentLecturer = new WelcomeFragmentLecturer();
    reportFragmentLecturer = new ReportFragmentLecturer();

    setFragment(welcomeFragmentLecturer);
    Objects.requireNonNull(getSupportActionBar()).setLogo(R.drawable.kdefault);
    getSupportActionBar().setTitle("Ç.Ü. Attendance Tracking System");
    getSupportActionBar().setSubtitle("/Home");
    mainNav.setOnNavigationItemSelectedListener(
        new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
              case R.id.nav_home:
                setFragment(welcomeFragmentLecturer);
                Objects.requireNonNull(getSupportActionBar()).setLogo(R.drawable.kdefault);
                getSupportActionBar().setTitle("Ç.Ü. Attendance Tracking System");
                getSupportActionBar().setSubtitle("/Home");
                break;

              case R.id.nav_report:
                setFragment(reportFragmentLecturer);
                Objects.requireNonNull(getSupportActionBar()).setLogo(R.drawable.kdefault);
                getSupportActionBar().setTitle("Ç.Ü. Attendance Tracking System");
                getSupportActionBar().setSubtitle("/Report");
                break;

              case R.id.logout:
                SessionManager session = new SessionManager(getApplicationContext());
                session.logoutUser();
                break;
              default:
                break;
            }
            return true;
          }
        });
  }

  private void setFragment(Fragment fragment) {
    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
    fragmentTransaction.replace(R.id.main_frame, fragment);
    fragmentTransaction.commit();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.toolbar_menu_lecturer, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    setFinishOnTouchOutside(false);
    if(item.toString().equals("Beacon Configuration")){
      new BeaconBuilder(this);
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    setFragment(welcomeFragmentLecturer);
    mainNav.setSelectedItemId(R.id.nav_home);
  }

  private void setLoggedUser() {
    if (((Globals) getApplication()).getLoggedUser() == null) {
      SessionManager sessionManager = new SessionManager(getApplicationContext());
      Map<String, String> userInfo = sessionManager.getUserDetails();
      Actor actor;
      if (userInfo.get(SessionManager.KEY_USER_TYPE).equals("student")) actor = new Student();
      else actor = new Lecturer();

      actor.setId(Integer.parseInt(userInfo.get(SessionManager.KEY_USER_ID)));
      actor.setName(userInfo.get(SessionManager.KEY_USER_NAME));
      actor.setSurname(userInfo.get(SessionManager.KEY_USER_SURNAME));
      actor.setMail(userInfo.get(SessionManager.KEY_USER_MAIL));
      // TODO AN SQL QUERY WILL WORK HERE TO RECIEVE LOGGED USER INFO
      ((Globals) getApplication()).setLoggedUser(actor);
    }
  }
}
