package seniorproject.attendancetrackingsystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;


public class LoginLecturerActivity extends AppCompatActivity implements BackgroundWorker.TaskCompleted {
    private EditText ET_Mail, ET_Password;
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_lecturer);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        ET_Mail = (EditText) findViewById(R.id.input_email);
        ET_Password = (EditText) findViewById(R.id.input_password);

        awesomeValidation.addValidation(this, R.id.input_email,
                Patterns.EMAIL_ADDRESS, R.string.emailerror);
    }

    public void OnLogin(View view) {
        if (awesomeValidation.validate() && !ET_Password.getText().toString().isEmpty()) {
            String mail = ET_Mail.getText().toString();
            String password = ET_Password.getText().toString();
            BackgroundWorker backgroundWorker = new BackgroundWorker(this);
            backgroundWorker.execute("lecturerLogin", "username", mail, "password", password);
        } else if (ET_Password.getText().toString().isEmpty())
            ET_Password.setError("Enter your password");
    }

    @Override
    public void onTaskComplete(String result) {

    }
}
