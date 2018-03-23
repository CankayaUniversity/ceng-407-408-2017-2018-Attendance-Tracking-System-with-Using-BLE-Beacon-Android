package seniorproject.attendancetrackingsystem;


import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;


public class RegistrationActivity extends AppCompatActivity implements BackgroundWorker.TaskCompleted {
    private Switch roleSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(R.id.register_layout,
                    new StudentRegister()).commit();
        }

        roleSwitch = (Switch) findViewById(R.id.role_switch);
        roleSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (roleSwitch.isChecked()) {
                    getFragmentManager().beginTransaction().replace(R.id.register_layout,
                            new LecturerRegister()).commit();
                } else
                    getFragmentManager().beginTransaction().replace(R.id.register_layout,
                            new StudentRegister()).commit();
            }
        });

    }

    @Override
    public void onTaskComplete(String result) {
        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.register_layout);
        if (currentFragment instanceof LecturerRegister)
            ((LecturerRegister) currentFragment).update(result);

    }
}
