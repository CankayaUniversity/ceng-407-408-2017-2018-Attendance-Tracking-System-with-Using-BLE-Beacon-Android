package seniorproject.attendancetrackingsystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        Button login_student  = (Button) findViewById(R.id.student_login);
        Button login_lecturer = (Button) findViewById(R.id.lecturer_login);

        login_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent studentLoginIntent = new Intent (MainLoginActivity.this, LoginStudentActivity.class);
                startActivity(studentLoginIntent);

            }
        });

        login_lecturer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent lecturerLoginIntent = new Intent(MainLoginActivity.this, LoginLecturerActivity.class);
                startActivity(lecturerLoginIntent);
            }
        });
    }
}
