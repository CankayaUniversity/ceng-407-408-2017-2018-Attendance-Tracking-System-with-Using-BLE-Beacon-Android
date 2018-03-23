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


public class LecturerLogin extends Fragment implements View.OnClickListener {
    private EditText ET_Mail, ET_Password;
    private AwesomeValidation awesomeValidation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lecturer_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
            BackgroundWorker backgroundWorker = new BackgroundWorker(getActivity());
            backgroundWorker.execute("lecturerLogin", "username", mail, "password", password);
        } else if (ET_Password.getText().toString().isEmpty())
            ET_Password.setError("Enter your password");
    }
}
