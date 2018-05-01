package seniorproject.attendancetrackingsystem.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.SessionManager;
import seniorproject.attendancetrackingsystem.utils.Schedule;

/* A simple {@link Fragment} subclass. */
public class WelcomeFragmentLecturer extends Fragment {

    private Schedule schedule;

  public WelcomeFragmentLecturer() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_welcome_lecturer, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    SessionManager session = new SessionManager(getActivity().getApplicationContext());
    HashMap<String, String> userInfo = session.getUserDetails();
    TextView nameSurnameField = getActivity().findViewById(R.id.w_user_name);
    TextView description = getActivity().findViewById(R.id.w_user_mail);
    final Switch secureSwitch = getActivity().findViewById(R.id.secure_switch);
    String nameText =
            userInfo.get(SessionManager.KEY_USER_NAME)
                    + " "
                    + userInfo.get(SessionManager.KEY_USER_SURNAME).toUpperCase();
    String mailText = userInfo.get(SessionManager.KEY_USER_MAIL);
    nameSurnameField.setText(nameText);
    description.setText(mailText);

    secureSwitch.setOnCheckedChangeListener(
            new CompoundButton.OnCheckedChangeListener() {
              @Override
              public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (secureSwitch.isChecked()) {
                  Toast.makeText(getActivity().getApplicationContext(),
                          "Secure mode is activated",
                          Toast.LENGTH_LONG).show();
                  try
                  {
                      SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                      Date currentDate = dateFormat.parse(dateFormat.format(new Date()));
                      Schedule.CourseInfo currentCourse = null;
                      for (Schedule.CourseInfo x : schedule.getCourses()) {
                          String start = x.getHour();
                          String end = start.substring(0, 2);
                          end = String.valueOf(Integer.parseInt(end) + 1) + ":10";
                          if (currentDate.after(dateFormat.parse(start))
                                  && currentDate.before(dateFormat.parse(end))) {
                              currentCourse = x;
                          }

                      }
                  }
                  catch(ParseException e) {
                      e.printStackTrace();
                  }
                }
              }
            });
  }
}
