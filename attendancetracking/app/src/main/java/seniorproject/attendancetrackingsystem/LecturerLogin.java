package seniorproject.attendancetrackingsystem;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import java.util.HashMap;


public class LecturerLogin extends Fragment implements View.OnClickListener {
    private EditText ET_Mail, ET_Password;
    private AwesomeValidation awesomeValidation;
    private static DatabaseManager DATABASE_MANAGER;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lecturer_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DATABASE_MANAGER = DatabaseManager.getmInstance(getActivity());
        initElements(view);
        awesomeValidation.addValidation(getActivity(), R.id.input_email,
                Patterns.EMAIL_ADDRESS, R.string.emailerror);
    }

    private void initElements(View view) {
        Button loginButton = (Button) view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);
        ET_Mail = (EditText) view.findViewById(R.id.input_email);
        ET_Password = (EditText) view.findViewById(R.id.input_password);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
    }

    @Override
    public void onClick(View v) {
        if (awesomeValidation.validate() && !ET_Password.getText().toString().isEmpty()) {
            String mail = ET_Mail.getText().toString();
            String password = ET_Password.getText().toString();
            HashMap<String, String> postParameters = new HashMap<String, String>();
            postParameters.put("username", mail);
            postParameters.put("password", password);
            postParameters.put("type", "lecturerLogin");
            DATABASE_MANAGER.execute("login",postParameters);

        } else if (ET_Password.getText().toString().isEmpty())
            ET_Password.setError("Enter your password");
    }
}
