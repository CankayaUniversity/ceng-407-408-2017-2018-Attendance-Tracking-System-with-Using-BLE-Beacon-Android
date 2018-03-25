package seniorproject.attendancetrackingsystem;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 */
public class AssignmentFragment extends Fragment implements View.OnClickListener {

    private TextView TW_SelectCourse;
    private TextView TW_SelectSection;

    private Spinner SP_CourseList;
    private Spinner SP_SectionList;

    private String username, userType;
    private ArrayList<String> courseList;
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

        SP_CourseList = (Spinner) view.findViewById(R.id.courseSelect);
        SP_SectionList = (Spinner) view.findViewById(R.id.section);

        TW_SelectCourse = (TextView) view.findViewById(R.id.courseName);
        TW_SelectSection = (TextView) view.findViewById(R.id.sectionNum);

        courseList = new ArrayList<String>();
        sectionList = new ArrayList<String>();

        sectionList.add("Choose your section");
        sectionList.add("Section 1");
        sectionList.add("Section 2");

 //TODO GET COURSES AND SECTIONS

        Button BT_Save = (Button) view.findViewById(R.id.submitCourseAssignment);

        BT_Save.setOnClickListener(this);

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
    public void onClick (View view) {




        if (SP_CourseList.getSelectedItemId() == 0) {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Department is empty");
            alertDialog.setMessage("Please choose your department from the list");
            alertDialog.show();
            return;
        }

        else if (SP_SectionList.getSelectedItemId() == 0) {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Section is empty");
            alertDialog.setMessage("Please choose your section from the list");
            alertDialog.show();
            return;
        }

        String course  = SP_CourseList.getSelectedItem().toString();
        String section = SP_SectionList.getSelectedItem().toString();



    }

    public void setData(String username, String usertype){
        this.username = username;
        this.userType = usertype;
    }
    public void update(String result) {
        String[] tokens = result.split("\n");
        String message = "";
        for (int i = 0; i < tokens.length; i++) {
            courseList.add(tokens[i].substring(tokens[i].indexOf(" "), tokens[i].length()));
        }
        Collections.sort(courseList);
        courseList.add(0, "Choose your department");

    }
}
