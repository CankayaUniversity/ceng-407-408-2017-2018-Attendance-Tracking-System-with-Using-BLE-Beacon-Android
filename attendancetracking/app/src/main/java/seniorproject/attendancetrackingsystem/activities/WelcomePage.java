package seniorproject.attendancetrackingsystem.activities;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.fragments.AssignmentFragment;
import seniorproject.attendancetrackingsystem.fragments.ReportFragment;
import seniorproject.attendancetrackingsystem.fragments.ServicesFragment;
import seniorproject.attendancetrackingsystem.fragments.SettingsFragment;
import seniorproject.attendancetrackingsystem.fragments.WelcomeFragment;


public class WelcomePage extends AppCompatActivity {

    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;

    private WelcomeFragment welcomeFragment;
    private ServicesFragment servicesFragment;
    private SettingsFragment settingsFragment;
    private ReportFragment reportFragment;
    private AssignmentFragment assignmentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);


        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);

        welcomeFragment = new WelcomeFragment();
        servicesFragment = new ServicesFragment();
        settingsFragment = new SettingsFragment();
        reportFragment = new ReportFragment();
        assignmentFragment = new AssignmentFragment();
        setFragment(welcomeFragment);

        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        mMainNav.setItemBackgroundResource(R.color.darkNight);
                        setFragment(welcomeFragment);
                        return true;
                    case R.id.nav_settings:
                        mMainNav.setItemBackgroundResource(R.color.darkNight);
                        setFragment(settingsFragment);
                        return true;
                    case R.id.nav_report:
                        mMainNav.setItemBackgroundResource(R.color.darkNight);
                        setFragment(reportFragment);
                        return true;
                    case R.id.nav_assignment:
                        mMainNav.setItemBackgroundResource(R.color.darkNight);
                        setFragment(assignmentFragment);
                        return true;
                    case R.id.nav_services:
                        mMainNav.setItemBackgroundResource(R.color.darkNight);
                        setFragment(servicesFragment);
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
        mMainNav.setSelectedItemId(R.id.nav_home);
    }

}


