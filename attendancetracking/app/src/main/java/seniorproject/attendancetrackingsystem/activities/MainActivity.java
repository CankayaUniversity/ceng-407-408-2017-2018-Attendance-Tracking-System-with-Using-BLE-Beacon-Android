package seniorproject.attendancetrackingsystem.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.DatabaseManager;
import seniorproject.attendancetrackingsystem.helpers.SessionManager;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {
  private Button login;
  private Button register;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    login = findViewById(R.id.login_button);
    register = findViewById(R.id.register_button);
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
    checkPermissions();
  }

  private void checkPermissions() {
    Permissions.check(
        this,
        new String[] {
          Manifest.permission.CAMERA,
          Manifest.permission.WRITE_EXTERNAL_STORAGE,
          Manifest.permission.READ_EXTERNAL_STORAGE,
          Manifest.permission.ACCESS_COARSE_LOCATION
        },
        "This permissions are required to use Attendance Tracking System",
        new Permissions.Options()
            .setSettingsDialogTitle("Warning!")
            .setRationaleDialogTitle("Info"),
        new PermissionHandler() {
          @Override
          public void onGranted() {
            checkSession();
            login.setOnClickListener(
                new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                  }
                });

            if (!allowRegister()) unsetRegisterOnClick();
            else setRegisterOnClick();
          }
        });
  }

  private void setRegisterOnClick() {
    register.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent registerIntent = new Intent(MainActivity.this, RegistrationActivity.class);
            startActivity(registerIntent);
          }
        });
  }

  private void unsetRegisterOnClick() {
    register.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Toast.makeText(
                    getApplicationContext(),
                    "You cannot register because this device is bound" + ".",
                    Toast.LENGTH_LONG)
                .show();
          }
        });
  }

  private boolean allowRegister() {
    SessionManager sessionManager = new SessionManager(getApplicationContext());
    if (!sessionManager.isEmptyAndroidId()) return false;
    @SuppressLint("HardwareIds")
    final String android_id =
        Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

    StringRequest request =
        new StringRequest(
            Request.Method.POST,
            DatabaseManager.AccountOperations,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
                try {
                  JSONObject jsonObject = new JSONObject(response);
                  boolean result = jsonObject.getBoolean("success");
                  if (result) {
                    setRegisterOnClick();
                  } else {
                    unsetRegisterOnClick();
                  }
                } catch (JSONException e) {
                  e.printStackTrace();
                }
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {}
            }) {
          @Override
          protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<>();
            params.put("operation", "check-android-id");
            params.put("android_id", android_id);
            return params;
          }
        };
    DatabaseManager.getInstance(getApplicationContext()).execute(request);
    return true;
  }

  private void checkSession() {
    SessionManager session = new SessionManager(getApplicationContext());
    if (session.isLoggedIn()) {
      Map<String, String> userInfo = session.getUserDetails();
      Intent intent;
      if (userInfo.get(SessionManager.KEY_USER_TYPE).equals("student"))
        intent = new Intent(MainActivity.this, StudentActivity.class);
      else intent = new Intent(MainActivity.this, LecturerActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
    }
  }

  @Override
  public void onBackPressed() {}
}
