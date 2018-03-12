package seniorproject.attendancetrackingsystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginLecturerActivity extends AppCompatActivity {
    EditText ET_Mail, ET_Password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_lecturer);
        ET_Mail = (EditText) findViewById(R.id.input_email);
        ET_Password = (EditText) findViewById(R.id.input_password);
    }

    public void OnLogin(View view){
        String mail = ET_Mail.getText().toString();
        String password = ET_Password.getText().toString();
        if(!mail.isEmpty() && !password.isEmpty()){
            BackgroundWorker backgroundWorker = new BackgroundWorker(this);
            backgroundWorker.execute("lecturerLogin", mail, password);
        }
    }
}
