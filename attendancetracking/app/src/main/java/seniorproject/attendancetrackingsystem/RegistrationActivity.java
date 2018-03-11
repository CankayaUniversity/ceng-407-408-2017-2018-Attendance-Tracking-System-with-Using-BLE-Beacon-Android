package seniorproject.attendancetrackingsystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;



public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        final TextView _student_schoolID = (TextView) findViewById(R.id.student_schoolID);
        final TextView _student_e_mail   = (TextView) findViewById(R.id.student_e_mail);
        final TextView _student_password = (TextView) findViewById(R.id.student_password);
        final TextView _student_name     = (TextView) findViewById(R.id.student_name);
        final TextView _student_surname  = (TextView) findViewById(R.id.student_surname);

        final TextView _lecturer_e_mail = (TextView) findViewById(R.id.lecturer_e_mail);
        final TextView _lecturer_password = (TextView) findViewById(R.id.lecturer_password);
        final TextView _lecturer_name = (TextView) findViewById(R.id.lecturer_name);
        final TextView _lecturer_surname = (TextView) findViewById(R.id.lecturer_surname);

        final Button   _register_button = (Button) findViewById(R.id.register_button);

        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);

        radioGroup.clearCheck();

        _student_schoolID.setVisibility(View.INVISIBLE);
        _student_e_mail.setVisibility(View.INVISIBLE);
        _student_password.setVisibility(View.INVISIBLE);
        _student_name.setVisibility(View.INVISIBLE);
        _student_surname.setVisibility(View.INVISIBLE);

        _lecturer_e_mail.setVisibility(View.INVISIBLE);
        _lecturer_password.setVisibility(View.INVISIBLE);
        _lecturer_name.setVisibility(View.INVISIBLE);
        _lecturer_surname.setVisibility(View.INVISIBLE);

       radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(RadioGroup group, int checkedId) {

               if(checkedId == R.id.radio_button_lecturer)
               {
                   _student_schoolID.setVisibility(View.INVISIBLE);
                   _student_schoolID.setText("");
                   _student_e_mail.setVisibility(View.INVISIBLE);
                   _student_e_mail.setText("");
                   _student_password.setVisibility(View.INVISIBLE);
                   _student_password.setText("");
                   _student_name.setVisibility(View.INVISIBLE);
                   _student_name.setText("");
                   _student_surname.setVisibility(View.INVISIBLE);
                   _student_surname.setText("");


                   _lecturer_e_mail.setVisibility(View.VISIBLE);
                   _lecturer_e_mail.setText("");
                   _lecturer_password.setVisibility(View.VISIBLE);
                   _lecturer_password.setText("");
                   _lecturer_name.setVisibility(View.VISIBLE);
                   _lecturer_name.setText("");
                   _lecturer_surname.setVisibility(View.VISIBLE);
                   _lecturer_surname.setText("");

               }

               else if(checkedId == R.id.radio_button_student)
               {
                   _lecturer_e_mail.setVisibility(View.INVISIBLE);
                   _lecturer_e_mail.setText("");
                   _lecturer_password.setVisibility(View.INVISIBLE);
                   _lecturer_password.setText("");
                   _lecturer_name.setVisibility(View.INVISIBLE);
                   _lecturer_name.setText("");
                   _lecturer_surname.setVisibility(View.INVISIBLE);
                   _lecturer_surname.setText("");

                   _student_schoolID.setVisibility(View.VISIBLE);
                   _student_schoolID.setText("");
                   _student_e_mail.setVisibility(View.VISIBLE);
                   _student_e_mail.setText("");
                   _student_password.setVisibility(View.VISIBLE);
                   _student_password.setText("");
                   _student_name.setVisibility(View.VISIBLE);
                   _student_name.setText("");
                   _student_surname.setVisibility(View.VISIBLE);
                   _student_surname.setText("");
               }

           }
       });

       _register_button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               RadioButton lecturerButton = (RadioButton) findViewById(R.id.radio_button_lecturer);
               RadioButton studentButton  = (RadioButton) findViewById(R.id.radio_button_student);

               if(lecturerButton.isActivated())
               {
                   String lecturerEmail = (String)_lecturer_e_mail.getText();
                   String lecturerPassword = (String) _lecturer_password.getText();
                   String lecturerName = (String) _lecturer_name.getText();
                   String lecturerSurname = (String) _lecturer_surname.getText();
               }

               else if(studentButton.isActivated())
               {
                   String studentEmail = (String)_student_e_mail.getText();
                   String studentPassword = (String) _student_password.getText();
                   String studentName = (String) _student_name.getText();
                   String studentSurname = (String) _student_surname.getText();
                   String studentID = (String) _student_schoolID.getText();
               }


           }
       });
    }
}
