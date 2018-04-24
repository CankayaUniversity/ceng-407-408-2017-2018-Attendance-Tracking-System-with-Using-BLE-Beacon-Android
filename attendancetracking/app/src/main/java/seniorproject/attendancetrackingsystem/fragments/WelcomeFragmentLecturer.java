package seniorproject.attendancetrackingsystem.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.SessionManager;

/* A simple {@link Fragment} subclass. */
public class WelcomeFragmentLecturer extends Fragment {

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
    String nameText =
            userInfo.get(SessionManager.KEY_USER_NAME)
                    + " "
                    + userInfo.get(SessionManager.KEY_USER_SURNAME).toUpperCase();
    String mailText = userInfo.get(SessionManager.KEY_USER_MAIL);
    nameSurnameField.setText(nameText);
    description.setText(mailText);
  }
}
