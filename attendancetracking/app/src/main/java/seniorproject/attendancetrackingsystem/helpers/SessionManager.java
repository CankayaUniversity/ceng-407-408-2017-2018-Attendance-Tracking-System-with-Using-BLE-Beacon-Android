package seniorproject.attendancetrackingsystem.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

import seniorproject.attendancetrackingsystem.activities.MainActivity;

public class SessionManager {
  public static final String KEY_USERTYPE = "userType";
  public static final String KEY_USERNAME = "username";
  private static final String PREF_NAME = "SessionPref";
  private static final String IS_LOGIN = "IsLoggedIn";
  private static final int PRIVATE_MODE = 0;
  final Context context;
  private final SharedPreferences pref;
  private Editor editor;

  public SessionManager(Context context) {
    this.context = context;
    pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
  }

  public void createLoginSession(String userType, String username) {
    editor = pref.edit();
    editor.putBoolean(IS_LOGIN, true);
    editor.putString(KEY_USERNAME, username);
    editor.putString(KEY_USERTYPE, userType);

    editor.apply();
  }

  public void checkLogin() {
    if (!this.isLoggedIn()) {
      Intent intent = new Intent(context, MainActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(intent);
    }
  }

  public HashMap<String, String> getUserDetails() {
    HashMap<String, String> user = new HashMap<>();
    user.put(KEY_USERTYPE, pref.getString(KEY_USERTYPE, null));
    user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));
    return user;
  }

  public void logoutUser() {
    editor.clear();
    editor.commit();
    Intent intent = new Intent(context, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }

  public boolean isLoggedIn() {
    return pref.getBoolean(IS_LOGIN, false);
  }
}
