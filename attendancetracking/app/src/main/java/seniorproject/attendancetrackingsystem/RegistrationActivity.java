package seniorproject.attendancetrackingsystem;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import java.util.ArrayList;
import java.util.Collections;


public class RegistrationActivity extends AppCompatActivity implements BackgroundWorker.TaskCompleted {
    private EditText studentSchoolID, studentEmail, studentPassword, studentName, studentSurname,
            lecturerEmail, lecturerPassword, lecturerName, lecturerSurname;
    private RadioGroup radioGroup;
    private AwesomeValidation awesomeValidation;
    private Spinner departmentList;
    private ArrayList<String> departments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        studentSchoolID = (EditText) findViewById(R.id.student_schoolID);
        studentEmail = (EditText) findViewById(R.id.student_e_mail);
        studentPassword = (EditText) findViewById(R.id.student_password);
        studentName = (EditText) findViewById(R.id.student_name);
        studentSurname = (EditText) findViewById(R.id.student_surname);
        lecturerEmail = (EditText) findViewById(R.id.lecturer_e_mail);
        lecturerPassword = (EditText) findViewById(R.id.lecturer_password);
        lecturerName = (EditText) findViewById(R.id.lecturer_name);
        lecturerSurname = (EditText) findViewById(R.id.lecturer_surname);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        departmentList = (Spinner) findViewById(R.id.lecturer_courses);
        departments = new ArrayList<String>();
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);

        backgroundWorker.execute("get", "department-list","Request","true");

        Collections.sort(departments);

        departments.add(0, "Choose your department");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, departments);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentList.setAdapter(adapter);


        changeVisibilities((ViewGroup) findViewById(R.id.EditViewRelative), 0);
        radioGroup.clearCheck();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_button_student)
                    changeVisibilities((ViewGroup) findViewById(R.id.EditViewRelative), 1);
                if (checkedId == R.id.radio_button_lecturer)
                    changeVisibilities((ViewGroup) findViewById(R.id.EditViewRelative), 2);
            }
        });
    }


    private void changeVisibilities(ViewGroup viewGroup, int p) {
        if (p == 0) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                if (viewGroup.getChildAt(i) instanceof EditText)
                    ((EditText) viewGroup.getChildAt(i)).setVisibility(View.INVISIBLE);
                else if (viewGroup.getChildAt(i) instanceof Spinner)
                    ((Spinner) viewGroup.getChildAt(i)).setVisibility(View.INVISIBLE);
            }
        } else if (p == 1) {
            awesomeValidation.clear();
            awesomeValidation.addValidation(this, R.id.student_schoolID,
                    "^20[0-9]{7}$", R.string.studentIDerror);
            awesomeValidation.addValidation(this, R.id.student_e_mail,
                    Patterns.EMAIL_ADDRESS, R.string.emailerror);
            awesomeValidation.addValidation(this, R.id.student_name,
                    "^[a-zA-Z]+$", R.string.nameerror);
            awesomeValidation.addValidation(this, R.id.student_surname,
                    "^[a-zA-Z]+$", R.string.surnameerror);
            awesomeValidation.addValidation(this, R.id.student_password,
                    "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[!_*.-]).{6,}$",
                    R.string.passworderror);
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                if (viewGroup.getChildAt(i) instanceof EditText) {
                    if (viewGroup.getChildAt(i).getTag().equals("l")) {
                        ((EditText) viewGroup.getChildAt(i)).setVisibility(View.INVISIBLE);
                    } else {
                        ((EditText) viewGroup.getChildAt(i)).setVisibility(View.VISIBLE);
                    }
                } else if (viewGroup.getChildAt(i) instanceof Spinner)
                    ((Spinner) viewGroup.getChildAt(i)).setVisibility(View.INVISIBLE);
            }
        } else if (p == 2) {
            awesomeValidation.clear();
            awesomeValidation.addValidation(this, R.id.lecturer_e_mail,
                    Patterns.EMAIL_ADDRESS, R.string.emailerror);
            awesomeValidation.addValidation(this, R.id.lecturer_name,
                    "^[a-zA-Z]+$", R.string.nameerror);
            awesomeValidation.addValidation(this, R.id.lecturer_surname,
                    "^[a-zA-Z]+$", R.string.surnameerror);
            awesomeValidation.addValidation(this, R.id.lecturer_password,
                    "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[!_*.-]).{6,}$",
                    R.string.passworderror);
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                if (viewGroup.getChildAt(i) instanceof EditText) {
                    if (viewGroup.getChildAt(i).getTag().equals("s")) {
                        ((EditText) viewGroup.getChildAt(i)).setVisibility(View.INVISIBLE);
                    } else {
                        ((EditText) viewGroup.getChildAt(i)).setVisibility(View.VISIBLE);
                    }
                } else if (viewGroup.getChildAt(i) instanceof Spinner) {

                    if (viewGroup.getChildAt(i).getTag().equals("s")) {
                        ((Spinner) viewGroup.getChildAt(i)).setVisibility(View.INVISIBLE);
                    } else {
                        ((Spinner) viewGroup.getChildAt(i)).setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    public void onRegister(View view) {
        if (awesomeValidation.validate()) {
            if (radioGroup.getCheckedRadioButtonId() == R.id.radio_button_student) {
                String schoolID = studentSchoolID.getText().toString();
                String password = studentPassword.getText().toString();
                String mail = studentEmail.getText().toString();
                String name = studentName.getText().toString();
                String surname = studentSurname.getText().toString();
                String phoneNumber = "";
                try {
                    TelephonyManager telephonyManager =
                            (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    phoneNumber = telephonyManager.getLine1Number().toString();

                } catch (SecurityException e) {
                    e.printStackTrace();
                }

                BackgroundWorker backgroundWorker = new BackgroundWorker(this);
                backgroundWorker.execute("studentRegister", "schoolID", schoolID, "password",
                        password, "mail", mail, "name", name, "surname", surname,
                        "phoneNumber", phoneNumber);
            } else if (radioGroup.getCheckedRadioButtonId() == R.id.radio_button_lecturer) {
                if (departmentList.getSelectedItemId() == 0) {
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Department is empty");
                    alertDialog.setMessage("Please choose your department from the list");
                    alertDialog.show();
                    return;
                }
                String mail = lecturerEmail.getText().toString();
                String password = lecturerPassword.getText().toString();
                String name = lecturerName.getText().toString();
                String surname = lecturerSurname.getText().toString();
                String lecturerDepartment = departmentList.getSelectedItem().toString();
                BackgroundWorker backgroundWorker = new BackgroundWorker(this);
                backgroundWorker.execute("lecturerRegister", "mail", mail, "password", password,
                        "name", name, "surname", surname, "department", lecturerDepartment);
            }
        }
    }

    @Override
    public void onTaskComplete(String result) {
        String[] tokens = result.split("\n");
        String message = "";
        for (int i = 0; i < tokens.length; i++) {
            departments.add(tokens[i].substring(tokens[i].indexOf(" "), tokens[i].length()));
        }
    }
}
