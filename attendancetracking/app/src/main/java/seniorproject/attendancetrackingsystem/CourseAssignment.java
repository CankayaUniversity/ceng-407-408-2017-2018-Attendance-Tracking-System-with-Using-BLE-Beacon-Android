package seniorproject.attendancetrackingsystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class CourseAssignment extends AppCompatActivity implements OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_assignment);
        Spinner courseSelect = (Spinner) findViewById(R.id.courseSelect);
        Spinner section = (Spinner) findViewById(R.id.section);
        // Spinner click listener
        courseSelect.setOnItemSelectedListener(this);
        section.setOnItemSelectedListener(this);
        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("CENG");
        categories.add("ECE");
        categories.add("EE");
        categories.add("ME");
        categories.add("PHY");
        categories.add("MATH");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);


        // attaching data adapter to spinner
        courseSelect.setAdapter(dataAdapter);


        List<String> sections = new ArrayList<String>();
        sections.add("Section 1");
        sections.add("Section 2");
        sections.add("Section 3");
        sections.add("Section 4");
        sections.add("Section 5");
        sections.add("Section 6");

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sections);
        section.setAdapter(dataAdapter2);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {

    }
}

