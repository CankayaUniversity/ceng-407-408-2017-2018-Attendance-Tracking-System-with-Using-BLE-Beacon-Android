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

import java.util.Map;
import java.util.Objects;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.fragments.ReportFragment;
import seniorproject.attendancetrackingsystem.fragments.WelcomeFragment;
import seniorproject.attendancetrackingsystem.helpers.DatabaseManager;
import seniorproject.attendancetrackingsystem.helpers.SessionManager;
import seniorproject.attendancetrackingsystem.utils.Globals;

public class StudentActivity extends AppCompatActivity {

  private BottomNavigationView mainNav;

  private WelcomeFragment welcomeFragment;
  private ReportFragment reportFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    SessionManager session = new SessionManager(getApplicationContext());
    session.checkLogin();
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_student);
    Toolbar toolbar = findViewById(R.id.toolbar);

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
    inflater.inflate(R.menu.toolbarmenu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    setFragment(welcomeFragment);
    mainNav.setSelectedItemId(R.id.nav_home);
  }

}
