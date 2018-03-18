package seniorproject.attendancetrackingsystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

public class LoginStudentActivity extends AppCompatActivity {
    private EditText ET_StudentID, ET_Password;
    private AwesomeValidation awesomeValidation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        ET_StudentID = (EditText) findViewById(R.id.input_school_id);
        ET_Password = (EditText) findViewById(R.id.input_password);

        awesomeValidation.addValidation(this,R.id.input_school_id,
                "^([cC]|(20))[0-9]{7}$",R.string.studentIDerror);
    }

    public void OnLogin(View view){
        if(awesomeValidation.validate() && !ET_Password.getText().toString().isEmpty()) {
            String studentID = ET_StudentID.getText().toString();
            String password = ET_Password.getText().toString();
            if (!studentID.isEmpty() && !password.isEmpty()) {
                BackgroundWorker backgroundWorker = new BackgroundWorker(this);
                backgroundWorker.execute("studentLogin", "username", studentID, "password", password);
            }
            else if(ET_Password.getText().toString().isEmpty())
                ET_Password.setError("Enter your password");
        }
    }
}
