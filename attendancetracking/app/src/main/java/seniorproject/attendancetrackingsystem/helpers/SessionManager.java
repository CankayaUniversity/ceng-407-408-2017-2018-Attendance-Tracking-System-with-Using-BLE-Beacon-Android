package seniorproject.attendancetrackingsystem.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

import seniorproject.attendancetrackingsystem.activities.MainActivity;

public class SessionManager {
  public static final String KEY_USER_TYPE = "userType";
  public static final String KEY_USER_NAME = "name";
  public static final String KEY_USER_SURNAME = "surname";
  public static final String KEY_USER_MAIL = "mail";
  public static final String KEY_USER_ID = "user_id";
  private static final String IS_LOGIN = "IsLoggedIn";
  private static final String KEY_NOTIFICATION = "AllowNotification";
  private static final int PRIVATE_MODE = 0;
  private final Context context;
  private SharedPreferences pref;
  private Editor editor;

  public SessionManager(Context context) {
    this.context = context;
  }

  public void createLoginSession(String userType, String name, String surname, String mail, int
          id) {
    pref = context.getSharedPreferences("user-info", PRIVATE_MODE);
    editor = pref.edit();
    editor.putBoolean(IS_LOGIN, true);
    editor.putString(KEY_USER_NAME, name);
    editor.putString(KEY_USER_SURNAME, surname);
    editor.putString(KEY_USER_MAIL, mail);
    editor.putInt(KEY_USER_ID, id);
    editor.putString(KEY_USER_TYPE, userType);
    editor.putBoolean(KEY_NOTIFICATION,true);

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
    pref = context.getSharedPreferences("user-info", PRIVATE_MODE);
    HashMap<String, String> user = new HashMap<>();
    user.put(KEY_USER_TYPE, pref.getString(KEY_USER_TYPE, null));
    user.put(KEY_USER_NAME, pref.getString(KEY_USER_NAME, null));
    user.put(KEY_USER_SURNAME, pref.getString(KEY_USER_SURNAME, null));
    user.put(KEY_USER_MAIL, pref.getString(KEY_USER_MAIL, null));
    user.put(KEY_USER_ID, String.valueOf(pref.getInt(KEY_USER_ID, 0)));
    return user;
  }

  public void logoutUser() {
    pref = context.getSharedPreferences("user-info", PRIVATE_MODE);
    editor = pref.edit();
    editor.clear();
    editor.apply();
    Intent intent = new Intent(context, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }

  public boolean isLoggedIn() {
    pref = context.getSharedPreferences("user-info", PRIVATE_MODE);
    return pref.getBoolean(IS_LOGIN, false);
  }
public boolean dailyNotificationState(){
    pref = context.getSharedPreferences("user-info", PRIVATE_MODE);
    return pref.getBoolean(KEY_NOTIFICATION, true);
}
public void changeDailyNotificatonState(boolean state){
    pref = context.getSharedPreferences("user-info", PRIVATE_MODE);
    editor = pref.edit();
    editor.putBoolean(KEY_NOTIFICATION, state);
    editor.apply();
}
}
