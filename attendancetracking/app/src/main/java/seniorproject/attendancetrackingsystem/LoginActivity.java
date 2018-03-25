package seniorproject.attendancetrackingsystem;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;


public class LoginActivity extends AppCompatActivity {
    private Switch roleSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (savedInstanceState == null) {

            getFragmentManager().beginTransaction().replace(R.id.login_layout,
                    new StudentLogin()).commit();
        }

        roleSwitch = (Switch) findViewById(R.id.role_switch);
        roleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (roleSwitch.isChecked())
                    getFragmentManager().beginTransaction().replace(R.id.login_layout,
                            new LecturerLogin()).commit();
                else
                    getFragmentManager().beginTransaction().replace(R.id.login_layout,
                            new StudentLogin()).commit();
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0)
            roleSwitch.setChecked(!roleSwitch.isChecked());
        super.onBackPressed();

    }
}
