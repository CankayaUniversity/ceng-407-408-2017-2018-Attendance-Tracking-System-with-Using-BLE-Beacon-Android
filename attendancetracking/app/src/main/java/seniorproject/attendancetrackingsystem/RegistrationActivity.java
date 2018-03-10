package seniorproject.attendancetrackingsystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;



public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        final TextView schoolID = (TextView) findViewById(R.id.text_view_school);
        final TextView e_mail   = (TextView) findViewById(R.id.text_view_e_mail);
        final TextView password = (TextView) findViewById(R.id.text_view_password);
        final TextView name     = (TextView) findViewById(R.id.text_view_name);
        final TextView surname  = (TextView) findViewById(R.id.text_view_surname);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);

        radioGroup.clearCheck();

        schoolID.setVisibility(View.INVISIBLE);
        schoolID.setVisibility(View.INVISIBLE);
        e_mail.setVisibility(View.INVISIBLE);
        password.setVisibility(View.INVISIBLE);
        name.setVisibility(View.INVISIBLE);
        surname.setVisibility(View.INVISIBLE);

       radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(RadioGroup group, int checkedId) {

               if(checkedId == R.id.radio_button_lecturer)
               {

               }

               else if(checkedId == R.id.radio_button_student)
               {
                   schoolID.setVisibility(View.VISIBLE);
                   e_mail.setVisibility(View.VISIBLE);
                   password.setVisibility(View.VISIBLE);
                   name.setVisibility(View.VISIBLE);
                   surname.setVisibility(View.VISIBLE);
               }

           }
       });
    }
}
