package seniorproject.attendancetrackingsystem.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.fragments.ReportFragmentLecturer;
import seniorproject.attendancetrackingsystem.fragments.WelcomeFragmentLecturer;
import seniorproject.attendancetrackingsystem.helpers.BeaconBuilder;
import seniorproject.attendancetrackingsystem.helpers.DatabaseManager;
import seniorproject.attendancetrackingsystem.helpers.SessionManager;

public class LecturerActivity extends AppCompatActivity {
    private Receiver mReceiver;
    private BeaconBuilder beaconBuilder;
    private boolean mServiceBound = false;
    private BottomNavigationView mainNav;
    private AlertDialog alertDialog;
    private ProgressDialog progressDialog;
    private WelcomeFragmentLecturer welcomeFragmentLecturer;
    private ReportFragmentLecturer reportFragmentLecturer;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
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
                                SessionManager session = new SessionManager(getApplicationContext());
                                session.logoutUser();
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
        if (item.toString().equals("Beacon Configuration")) {
            showProgressDialog();
            Intent intent = new Intent(this, BeaconBuilder.class);
            // startService(intent);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            // new BeaconBuilder();
        } else if (item.toString().equals("Change Password")) {
            buildAlertDialog().show();
        }
        return super.onOptionsItemSelected(item);
    }

    private AlertDialog.Builder buildAlertDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        final LinearLayout layout = new LinearLayout(this);
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
        newPasswordRepeat.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newPasswordRepeat.setId(R.id.new_password_repeat);

        layout.addView(newPasswordRepeat);
        alert.setView(layout);

        alert.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String old_password = oldPassword.getText().toString();
                String new_password = newPassword.getText().toString();
                String new_password_repeat = newPasswordRepeat.getText().toString();

                if (old_password.isEmpty() || new_password.isEmpty() || new_password_repeat.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Empty field error", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!new_password.equals(new_password_repeat)) {
                    Toast.makeText(getApplicationContext(), "New passwords don't match", Toast
                            .LENGTH_SHORT).show();
                    return;
                }
                Map<String, String> params = new HashMap<>();
                SessionManager session = new SessionManager(getApplicationContext());
                Map<String, String> userInfo = session.getUserDetails();
                params.put("old_password", old_password);
                params.put("new_password", new_password);
                params.put("user_type", userInfo.get(SessionManager.KEY_USER_TYPE));
                params.put("user_id", userInfo.get(SessionManager.KEY_USER_ID));
                DatabaseManager.getmInstance(getApplicationContext()).execute("change-password", params);

                Log.d("old_password", old_password);
                Log.d("new_password", new_password);
                Log.d("user_type", userInfo.get(SessionManager.KEY_USER_TYPE));
                Log.d("user_id", userInfo.get(SessionManager.KEY_USER_ID));
            }
        });


        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return alert;
    }

    @Override
    public void onBackPressed() {
        setFragment(welcomeFragmentLecturer);
        mainNav.setSelectedItemId(R.id.nav_home);
    }

    private void showProgressDialog() {
        progressDialog.setTitle("Beacon syncronizer");
        progressDialog.setMessage("Searching nearyby beacons");
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
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

    private void showAlertDialog(final String mac) {
        if(mac == null || mac.isEmpty() || mac.equals("null"))
            return;
        progressDialog.hide();
        alertDialog.setTitle("Found Beacon");
        alertDialog.setMessage("MAC: " + mac);
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
                        if(mServiceBound){
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
        Log.i("reciever", "unregistered");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mReceiver = new Receiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BeaconBuilder.ACTION);
        registerReceiver(mReceiver, filter);
        Log.i("receiver", "registered");
    }

    private class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String permission = intent.getStringExtra("permission");
            if (permission != null && !permission.isEmpty() && permission.equals("request")) {
                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(LecturerActivity.this);
                    builder.setTitle("This app needs location access");
                    builder.setMessage("Please grant location access so this app can detect beacons.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                            }
                        }

                    });
                    builder.show();
                }

            }
           // if(intent.getStringExtra("MAC") != null)
            showAlertDialog(intent.getStringExtra("MAC"));
        }
    }
}
