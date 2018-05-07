package seniorproject.attendancetrackingsystem.fragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.SessionManager;
import seniorproject.attendancetrackingsystem.utils.RegularMode;

/* A simple {@link Fragment} subclass. */
public class WelcomeFragment extends Fragment {
  ArrayAdapter<String> adapter;
  private Receiver mReceiver;
  private ArrayList<String> messages;
  private ListView listView;
  private String currentCourse;
  private String latestFoundTime;
  private String checkedTime;

  public WelcomeFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_welcome, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    SessionManager session = new SessionManager(getActivity().getApplicationContext());
    HashMap<String, String> userInfo = session.getUserDetails();
    TextView nameSurnameField = getActivity().findViewById(R.id.w_user_name);
    TextView description = getActivity().findViewById(R.id.w_user_mail);
    String nameText =
        userInfo.get(SessionManager.KEY_USER_NAME)
            + " "
            + userInfo.get(SessionManager.KEY_USER_SURNAME).toUpperCase();
    String mailText = userInfo.get(SessionManager.KEY_USER_MAIL);
    nameSurnameField.setText(nameText);
    description.setText(mailText);
    listView = view.findViewById(R.id.notification_list);
    messages = new ArrayList<>();
    adapter =
        new ArrayAdapter<>(
            getActivity().getApplicationContext(), R.layout.notification_item, messages);
    if (currentCourse != null)
      messages.add("Current Course: " + currentCourse + "\n" + checkedTime);
    if (latestFoundTime != null) messages.add("Latest Interaction: " + latestFoundTime);

    listView.setAdapter(adapter);
  }

  @Override
  public void onResume() {
    super.onResume();
    mReceiver = new Receiver();
    IntentFilter filter = new IntentFilter();
    filter.addAction(RegularMode.ACTION);
    filter.addAction("RegularModeStatus");
    getActivity().registerReceiver(mReceiver, filter);
  }

  @Override
  public void onPause() {
    super.onPause();
    getActivity().unregisterReceiver(mReceiver);
  }

  private void showMessages() {
    if (messages.size() != 0) messages.remove(0);
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH);
    Date currentDate = new Date();
    if (currentCourse.equals("null")){
      messages.add(0,"Break time!");
      latestFoundTime = null;
    }
     else if(currentCourse.equals("no_course_for_today")){
      messages.add(0,"There is no course for today");
      latestFoundTime = null;
    }
    else{
      checkedTime = dateFormat.format(currentDate);
      messages.add(0, "Current Course: " + this.currentCourse + "\n" + checkedTime);
    }

    listView.setAdapter(adapter);
  }

  private class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (Objects.equals(intent.getAction(), "RegularModeStatus")) {
        boolean status = intent.getBooleanExtra("status", true);
        if (!status) {
          if (messages.size() > 1) {
            messages.remove(0);
            messages.remove(1);
          } else if (messages.size() == 1) messages.remove(0);

          messages.add(0, "End of the day");
          listView.setAdapter(adapter);
        }
      } else {
        String course_code = intent.getStringExtra("course_code");
        if (course_code != null) currentCourse = intent.getStringExtra("course_code");
        showMessages();
        boolean found = intent.getBooleanExtra("found", false);
        if (found) {
          Date current = new Date();
          SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
          if (messages.size() != 1) messages.remove(1);
          latestFoundTime = dateFormat.format(current);
          messages.add(1, "Latest Interaction: " + dateFormat.format(current));
        }
      }
    }
  }
}
