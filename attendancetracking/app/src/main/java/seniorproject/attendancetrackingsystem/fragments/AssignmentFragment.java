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


/**
 * A simple {@link Fragment} subclass.
 */
public class AssignmentFragment extends Fragment implements View.OnClickListener {
    private Spinner SP_CourseList;
    private Spinner SP_SectionList;

    private ArrayList<String> sectionList;

    public AssignmentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_assignment, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initElements(view);
    }

    private void initElements(View view) {

        SP_CourseList = view.findViewById(R.id.courseSelect);
        SP_SectionList = view.findViewById(R.id.section);
        ArrayList<String> courseList;

        courseList = new ArrayList<>();
        sectionList = new ArrayList<>();
        courseList.add("Choose your course");
        sectionList.add("Choose your section");

        if (((Globals) getActivity().getApplication()).getCourses() == null) {
            DatabaseManager.getmInstance(getActivity())
                    .execute("get", "course-list", courseList);
        } else {
            for (Course course
                    :
                    ((Globals) getActivity().getApplication()).getCourses()) {
                courseList.add(course.getCourseName());
            }
        }

        Button BT_Save = view.findViewById(R.id.submitCourseAssignment);

        BT_Save.setOnClickListener(this);
        SP_CourseList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (SP_CourseList.getSelectedItemId() == 0) return;
                sectionList.clear();
                SP_SectionList.setSelection(0);
                sectionList.add("Choose your section");

                String course_name = SP_CourseList.getSelectedItem().toString();

                for (Course course
                        :
                        ((Globals) getActivity().getApplication()).getCourses()) {
                    if (course_name.equals(course.getCourseName())) {
                        for (int i = 1; i <= course.getSectionNumber(); i++)
                            sectionList.add(String.valueOf(i));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayAdapter<String> course_adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_item, courseList);
        course_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SP_CourseList.setAdapter(course_adapter);


        ArrayAdapter<String> section_adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_item, sectionList);
        section_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SP_SectionList.setAdapter(section_adapter);

    }

    @Override
    public void onClick(View view) {


        //TODO MAKE ASSIGNMENT


    }


}
