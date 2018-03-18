package seniorproject.attendancetrackingsystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;


public class RegistrationActivity extends AppCompatActivity {
    private EditText studentSchoolID, studentEmail, studentPassword, studentName, studentSurname,
            lecturerEmail, lecturerPassword, lecturerName, lecturerSurname;
    private RadioGroup radioGroup;
    private AwesomeValidation awesomeValidation;

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
                if (viewGroup.getChildAt(i) instanceof EditText)
                    if (viewGroup.getChildAt(i).getTag().equals("l")) {
                        ((EditText) viewGroup.getChildAt(i)).setVisibility(View.INVISIBLE);
                    } else {
                        ((EditText) viewGroup.getChildAt(i)).setVisibility(View.VISIBLE);
                    }
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
                if (viewGroup.getChildAt(i) instanceof EditText)
                    if (viewGroup.getChildAt(i).getTag().equals("s")) {
                        ((EditText) viewGroup.getChildAt(i)).setVisibility(View.INVISIBLE);
                    } else {
                        ((EditText) viewGroup.getChildAt(i)).setVisibility(View.VISIBLE);
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

                BackgroundWorker backgroundWorker = new BackgroundWorker(this);
                backgroundWorker.execute("studentRegister", "schoolID", schoolID, "password",
                        password, "mail", mail, "name", name, "surname", surname);
            } else if (radioGroup.getCheckedRadioButtonId() == R.id.radio_button_lecturer) {
                String mail = lecturerEmail.getText().toString();
                String password = lecturerPassword.getText().toString();
                String name = lecturerName.getText().toString();
                String surname = lecturerSurname.getText().toString();
                BackgroundWorker backgroundWorker = new BackgroundWorker(this);
                backgroundWorker.execute("lecturerRegister", "mail", mail, "password", password,
                        "name", name, "surname", surname);
            }
        }
    }
}
