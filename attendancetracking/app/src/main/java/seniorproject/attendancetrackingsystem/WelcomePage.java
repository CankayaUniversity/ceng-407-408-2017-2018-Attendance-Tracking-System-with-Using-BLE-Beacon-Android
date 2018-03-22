package seniorproject.attendancetrackingsystem;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

public class WelcomePage extends AppCompatActivity implements BackgroundWorker.TaskCompleted {
    private Actor user;
    private TextView TV_Name, TV_Mail;
    private SessionManager session;
    private HashMap<String, String> userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        TV_Name = (TextView) findViewById(R.id.w_user_name);
        TV_Mail = (TextView) findViewById(R.id.w_user_mail);

        session = new SessionManager(getApplicationContext());
        userInfo = session.getUserDetails();


        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        if (userInfo.get(SessionManager.KEY_USERTYPE).equals("student")) {
            user = new Student();
            backgroundWorker.execute("get", "student-info", "Request", "true", "id", userInfo.get(SessionManager.KEY_USERNAME));
        } else if (userInfo.get(SessionManager.KEY_USERTYPE).equals("lecturer")) {
            user = new Lecturer();
            backgroundWorker.execute("get", "lecturer-info", "Request", "true", "id", userInfo.get(SessionManager.KEY_USERNAME));
        }


    }


    @Override
    public void onTaskComplete(String result) {
        String[] tokens = result.split("[\n]+");


        if (userInfo.get(SessionManager.KEY_USERTYPE).equals("student")) {
            user = new Student();
            ((Student) user).setStudentNumber(Integer.parseInt(tokens[0]));
            user.setName(tokens[1]);
            user.setSurname(tokens[2]);
            user.setMail(tokens[3]);
            ((Student) user).setPhoneNumber(tokens[4]);
        } else if (userInfo.get(SessionManager.KEY_USERTYPE).equals("lecturer")) {
            user = new Lecturer();
            user.setName(tokens[0]);
            user.setSurname(tokens[1]);
            user.setMail(tokens[2]);
        }

        TV_Mail.setText(user.getMail());
        TV_Name.setText(user.getName() + " " + user.getSurname());
    }

    public void onLogout(View view) {
        session.logoutUser();
    }
}
