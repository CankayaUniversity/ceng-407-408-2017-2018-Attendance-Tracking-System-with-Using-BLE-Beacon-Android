package seniorproject.attendancetrackingsystem.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.DatabaseManager;
import seniorproject.attendancetrackingsystem.helpers.JsonHelper;
import seniorproject.attendancetrackingsystem.helpers.SessionManager;
import seniorproject.attendancetrackingsystem.utils.Actor;
import seniorproject.attendancetrackingsystem.utils.Course;
import seniorproject.attendancetrackingsystem.utils.Globals;
import seniorproject.attendancetrackingsystem.utils.Student;
import seniorproject.attendancetrackingsystem.utils.TakenCourses;

/* A simple {@link Fragment} subclass. */
public class ReportFragment extends Fragment {

  public ReportFragment() {
    // Required empty public constructor
  }



  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_report, container, false);
  }

}
