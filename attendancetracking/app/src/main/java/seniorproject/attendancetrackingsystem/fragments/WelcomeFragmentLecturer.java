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



}
