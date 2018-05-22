package seniorproject.attendancetrackingsystem.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
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

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.fragments.CourseSettings;
import seniorproject.attendancetrackingsystem.fragments.ReportFragmentLecturer;
import seniorproject.attendancetrackingsystem.fragments.ReportProblem;
import seniorproject.attendancetrackingsystem.fragments.WelcomeFragmentLecturer;
import seniorproject.attendancetrackingsystem.helpers.BeaconBuilder;
import seniorproject.attendancetrackingsystem.helpers.DatabaseManager;
import seniorproject.attendancetrackingsystem.helpers.SessionManager;

public class LecturerActivity extends AppCompatActivity {
  private Receiver mReceiver;
  private BeaconBuilder beaconBuilder;
  private boolean mServiceBound = false;
  private AwesomeValidation awesomeValidation;
  private BottomNavigationView mainNav;
  private AlertDialog alertDialog;
  private ProgressDialog progressDialog;
  private WelcomeFragmentLecturer welcomeFragmentLecturer;
  private ReportFragmentLecturer reportFragmentLecturer;
  private ServiceConnection serviceConnection =
      new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
          BeaconBuilder.ServiceBinder binder = (BeaconBuilder.ServiceBinder) service;
          beaconBuilder = binder.getService();
          mServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
          mServiceBound = false;
        }
      };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    SessionManager session = new SessionManager(getApplicationContext());
    session.checkLogin();
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_lecturer);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    mainNav = findViewById(R.id.main_nav);
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
                final AlertDialog alertDialog =
                    new AlertDialog.Builder(LecturerActivity.this, AlertDialog.THEME_HOLO_LIGHT)
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
                break;
              default:
                break;
            }
            return true;
          }
        });

    alertDialog = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT).create();
    alertDialog.setCanceledOnTouchOutside(false);
    progressDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
    progressDialog.setCanceledOnTouchOutside(false);
  }

  private void setFragment(Fragment fragment) {
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
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
    if (item.toString().equals("Beacon Configuration")) {
      showProgressDialog();
      Intent intent = new Intent(this, BeaconBuilder.class);
      // startService(intent);
      bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
      // new BeaconBuilder();
    } else if (item.toString().equals("Change Password")) {
      buildAlertDialog();
    } else if (item.toString().equals("Course Settings")) {
      CourseSettings f = new CourseSettings();
      Objects.requireNonNull(getSupportActionBar()).setLogo(R.drawable.kdefault);
      getSupportActionBar().setTitle("Ç.Ü. Attendance Tracking System");
      getSupportActionBar().setSubtitle("/Course Settings");
      getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, f).commit();
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
              DatabaseManager.getmInstance(getApplicationContext())
                      .execute("change-password", params);
              change_dialog.dismiss();
            }
            else
            {
              Toast.makeText(getApplicationContext(),"Password should be at least 6 characters.\n"+
                      "Password should contains at least 1 uppercase letter\n" +
                      "1 digit and 1 special character (. - _ ! *)",Toast.LENGTH_LONG).show();
              return;
            }
          }
        });

      }
    });
    change_dialog.show();
  }

  private void showProgressDialog() {
    progressDialog.setTitle("Beacon syncronizer");
    progressDialog.setMessage("Searching nearyby beacons");
    progressDialog.setButton(
        DialogInterface.BUTTON_NEGATIVE,
        "Cancel",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            BluetoothAdapter.getDefaultAdapter().disable();
            if (mServiceBound) {
              unbindService(serviceConnection);
              mServiceBound = false;
            }
          }
        });
    progressDialog.show();
  }

  @Override
  public void onBackPressed() {
    Fragment f = getSupportFragmentManager().findFragmentById(R.id.main_frame);
    if (f instanceof ReportFragmentLecturer) {
      setFragment(reportFragmentLecturer);
      mainNav.setSelectedItemId(R.id.nav_report);
    } else {
      setFragment(welcomeFragmentLecturer);
      mainNav.setSelectedItemId(R.id.nav_home);
    }
  }

  private void showAlertDialog(final String mac, final String name) {
    if (mac == null || mac.isEmpty() || mac.equals("null")) return;
    progressDialog.hide();
    alertDialog.setTitle("Found Beacon");
    DecimalFormat formatter = new DecimalFormat("#0.00");
    String info = "Name: " + name + "\n\nMAC Address: " + mac;
    alertDialog.setMessage(info);
    alertDialog.setButton(
        DialogInterface.BUTTON_NEGATIVE,
        "Ignore",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            beaconBuilder.addToIgnoreList(mac);
            beaconBuilder.continueTracking();
            showProgressDialog();
          }
        });
    alertDialog.setButton(
        DialogInterface.BUTTON_POSITIVE,
        "Save",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Map<String, String> postParameters = new HashMap<>();
            postParameters.put("beacon_mac", mac);
            postParameters.put(
                "user_id",
                new SessionManager(getApplicationContext())
                    .getUserDetails()
                    .get(SessionManager.KEY_USER_ID));
            DatabaseManager.getmInstance(getApplicationContext())
                .execute("set-beacon", postParameters);
            BluetoothAdapter.getDefaultAdapter().disable();
            if (mServiceBound) {
              unbindService(serviceConnection);
              mServiceBound = false;
            }
            //  stopService(new Intent(LecturerActivity.this, BeaconBuilder.class));
          }
        });

    alertDialog.show();
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (mServiceBound) {
      unbindService(serviceConnection);
      mServiceBound = false;
    }
    progressDialog.hide();
    alertDialog.hide();
    unregisterReceiver(mReceiver);
  }

  @Override
  protected void onStart() {
    super.onStart();
    mReceiver = new Receiver();
    IntentFilter filter = new IntentFilter();
    filter.addAction(BeaconBuilder.ACTION);
    registerReceiver(mReceiver, filter);
  }

  private class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getStringExtra("MAC") != null)
        showAlertDialog(intent.getStringExtra("MAC"), intent.getStringExtra("NAME"));
    }
  }
}
