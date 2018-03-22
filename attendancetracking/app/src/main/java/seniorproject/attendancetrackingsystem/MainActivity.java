package seniorproject.attendancetrackingsystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = new SessionManager(getApplicationContext());
        if(session.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, WelcomePage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        Button login = (Button) findViewById(R.id.login_button);
        Button register = (Button) findViewById(R.id.register_button);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (intent != null && bundle != null) {
            try {
                String message = bundle.get("message").toString();
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }catch(NullPointerException e){
                e.printStackTrace();
            }
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(MainActivity.this, MainLoginActivity.class);
                startActivity(loginIntent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(registerIntent);
            }
        });
    }
}
