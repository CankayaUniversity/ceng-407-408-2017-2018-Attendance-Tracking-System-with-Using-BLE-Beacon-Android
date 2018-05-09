package seniorproject.attendancetrackingsystem.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.util.ArrayList;

import seniorproject.attendancetrackingsystem.R;

public class ReportProblem extends Fragment {

  private EditText issue;
  private EditText messages;
  private ArrayList<String> issues;
  private ArrayList<String> message;
  private ArrayAdapter<String> adapter;



  public ReportProblem(){}

  @Override
  public View onCreateView(
          LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.report_problem, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    issue = view.findViewById(R.id.issue);
    messages = view.findViewById(R.id.messages);
  }
}
