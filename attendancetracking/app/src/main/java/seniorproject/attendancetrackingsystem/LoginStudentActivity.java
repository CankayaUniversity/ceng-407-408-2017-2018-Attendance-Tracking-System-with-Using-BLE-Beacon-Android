package seniorproject.attendancetrackingsystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginStudentActivity extends AppCompatActivity {
    EditText ET_StudentID, ET_Password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);
        ET_StudentID = (EditText) findViewById(R.id.input_school_id);
        ET_Password = (EditText) findViewById(R.id.input_password);
    }

    public void OnLogin(View view){
        String studentID = ET_StudentID.getText().toString();
        String password = ET_Password.getText().toString();
        if(!studentID.isEmpty() && !password.isEmpty()) {
            BackgroundWorker backgroundWorker = new BackgroundWorker(this);
            backgroundWorker.execute("studentLogin", "username",studentID,"password", password);
        }
    }
}
