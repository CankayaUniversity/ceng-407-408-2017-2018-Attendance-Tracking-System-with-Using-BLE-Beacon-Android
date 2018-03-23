package seniorproject.attendancetrackingsystem;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;


public class StudentLogin extends Fragment implements View.OnClickListener {
    private EditText ET_StudentID, ET_Password;
    private AwesomeValidation awesomeValidation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.student_login, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initElements(view);
        awesomeValidation.addValidation(getActivity(), R.id.input_school_id,
                "^([cC]|(20))[0-9]{7}$", R.string.studentIDerror);
    }

    private void initElements(View view) {
        Button loginButton = (Button) view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);
        ET_StudentID = (EditText) view.findViewById(R.id.input_school_id);
        ET_Password = (EditText) view.findViewById(R.id.input_password);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
    }

    @Override
    public void onClick(View v) {
        if (awesomeValidation.validate() && !ET_Password.getText().toString().isEmpty()) {
            String studentID = ET_StudentID.getText().toString();
            String password = ET_Password.getText().toString();
            if (!studentID.isEmpty() && !password.isEmpty()) {
                BackgroundWorker backgroundWorker = new BackgroundWorker(getActivity());
                backgroundWorker.execute("studentLogin", "username", studentID, "password", password);
            } else if (ET_Password.getText().toString().isEmpty())
                ET_Password.setError("Enter your password");
        }
    }
}
