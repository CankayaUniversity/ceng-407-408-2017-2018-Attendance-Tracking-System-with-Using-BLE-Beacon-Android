package seniorproject.attendancetrackingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class WelcomePage extends AppCompatActivity implements BackgroundWorker.TaskCompleted {
    private Actor user;
    private Bundle bundle;
    private TextView TV_Name, TV_Mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        TV_Name = (EditText) findViewById(R.id.w_user_name);
        TV_Mail = (EditText) findViewById(R.id.w_user_mail);

        Intent intent = getIntent();
        bundle = intent.getExtras();


        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        if (bundle.get("userType").toString().equals("student")) {

            user = new Student();
            backgroundWorker.execute("get", "student-info", "Request", "true", "id", bundle.get("user_id").toString());
        } else if (bundle.get("userType").toString().equals("lecturer")) {
            user = new Lecturer();
            backgroundWorker.execute("get", "lecturer-info", "Request", "true", "id", bundle.get("user_id").toString());
        }


    }


    @Override
    public void onTaskComplete(String result) {
        String[] tokens = result.split("[\n]+");


        if (bundle.get("userType").toString().equals("student")) {
            user = new Student();
            ((Student) user).setStudentNumber(Integer.parseInt(tokens[0]));
            user.setName(tokens[1]);
            user.setSurname(tokens[2]);
            user.setMail(tokens[3]);
            ((Student) user).setPhoneNumber(tokens[4]);
        } else if (bundle.get("userType").toString() == "lecturer") {
            user = new Lecturer();
            user.setName(tokens[0]);
            user.setSurname(tokens[1]);
            user.setMail(tokens[2]);
        }

        TV_Mail.setText(user.getMail());
        TV_Name.setText(user.getName() + " " + user.getSurname());
    }
}
