package seniorproject.attendancetrackingsystem.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.SessionManager;

/* A simple {@link Fragment} subclass. */
public class WelcomeFragment extends Fragment {

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
    HashMap<String,String> userInfo = session.getUserDetails();

    TextView loggedUser = getActivity().findViewById(R.id.w_user_name);
    TextView loggedType = getActivity().findViewById(R.id.w_user_mail);

    loggedType.setText(userInfo.get(SessionManager.KEY_USER_TYPE));
    loggedUser.setText(userInfo.get(SessionManager.KEY_USER_NAME));
  }

}
