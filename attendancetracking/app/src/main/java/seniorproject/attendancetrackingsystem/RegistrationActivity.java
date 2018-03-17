package seniorproject.attendancetrackingsystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;



public class RegistrationActivity extends AppCompatActivity {
    private EditText studentSchoolID, studentEmail, studentPassword, studentName, studentSurname,
    lecturerEmail, lecturerPassword, lecturerName, lecturerSurname;
    private RadioGroup radioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

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

        changeVisibilities((ViewGroup)findViewById(R.id.EditViewRelative),0);
        radioGroup.clearCheck();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radio_button_student)
                    changeVisibilities((ViewGroup)findViewById(R.id.EditViewRelative),1);
                if(checkedId == R.id.radio_button_lecturer)
                    changeVisibilities((ViewGroup)findViewById(R.id.EditViewRelative),2);
            }
        });
    }
    private void changeVisibilities(ViewGroup viewGroup, int p){
        if(p == 0) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                if (viewGroup.getChildAt(i) instanceof EditText)
                    ((EditText) viewGroup.getChildAt(i)).setVisibility(View.INVISIBLE);
            }
        }
        else if(p == 1){
            for (int i = 0; i < viewGroup.getChildCount(); i++){
                if(viewGroup.getChildAt(i) instanceof  EditText)
                    if(viewGroup.getChildAt(i).getTag().equals("l")) {
                        ((EditText) viewGroup.getChildAt(i)).setVisibility(View.INVISIBLE);
                    }else {
                        ((EditText) viewGroup.getChildAt(i)).setVisibility(View.VISIBLE);
                    }
            }
        }
        else if(p == 2){
            for (int i = 0; i < viewGroup.getChildCount(); i++){
                if(viewGroup.getChildAt(i) instanceof  EditText)
                    if(viewGroup.getChildAt(i).getTag().equals("s")) {
                        ((EditText) viewGroup.getChildAt(i)).setVisibility(View.INVISIBLE);
                    }else {
                        ((EditText) viewGroup.getChildAt(i)).setVisibility(View.VISIBLE);
                    }
            }
        }
    }
}
