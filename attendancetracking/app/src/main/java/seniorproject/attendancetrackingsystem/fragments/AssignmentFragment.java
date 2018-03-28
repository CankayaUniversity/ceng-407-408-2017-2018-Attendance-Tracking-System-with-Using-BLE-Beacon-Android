package seniorproject.attendancetrackingsystem.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.DatabaseManager;
import seniorproject.attendancetrackingsystem.utils.Course;
import seniorproject.attendancetrackingsystem.utils.Globals;

/* A simple {@link Fragment} subclass. */
public class AssignmentFragment extends Fragment implements View.OnClickListener {
  private Spinner spCourseList;
  private Spinner spSectionList;

  private ArrayList<String> sectionList;

  public AssignmentFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_assignment, container, false);
  }

  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initElements(view);
  }

  private void initElements(View view) {

    spCourseList = view.findViewById(R.id.courseSelect);
    spSectionList = view.findViewById(R.id.section);
    ArrayList<String> courseList;

    courseList = new ArrayList<>();
    sectionList = new ArrayList<>();
    courseList.add("Choose your course");
    sectionList.add("Choose your section");

    if (((Globals) getActivity().getApplication()).getCourses() == null) {
      DatabaseManager.getmInstance(getActivity()).execute("get", "course-list", courseList);
    } else {
      for (Course course : ((Globals) getActivity().getApplication()).getCourses()) {
        courseList.add(course.getCourseName());
      }
    }

    Button saveButton = view.findViewById(R.id.submitCourseAssignment);

    saveButton.setOnClickListener(this);
    spCourseList.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (spCourseList.getSelectedItemId() == 0) {
              return;
            }
            sectionList.clear();
            spSectionList.setSelection(0);
            sectionList.add("Choose your section");

            String courseName = spCourseList.getSelectedItem().toString();

            for (Course course : ((Globals) getActivity().getApplication()).getCourses()) {
              if (courseName.equals(course.getCourseName())) {
                for (int i = 1; i <= course.getSectionNumber(); i++) {
                  sectionList.add(String.valueOf(i));
                }
              }
            }
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {}
        });

    ArrayAdapter<String> courseAdapter =
            new ArrayAdapter<>(getActivity(), R.layout.spinner_item, courseList);
    courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spCourseList.setAdapter(courseAdapter);

    ArrayAdapter<String> sectionAdapter =
            new ArrayAdapter<>(getActivity(), R.layout.spinner_item, sectionList);
    sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spSectionList.setAdapter(sectionAdapter);
  }

  @Override
  public void onClick(View view) {

    // TODO MAKE ASSIGNMENT

  }
}
