package seniorproject.attendancetrackingsystem.fragments;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
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
import java.util.Map;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.DatabaseManager;


public class StudentRegister extends Fragment {
    private EditText studentID, studentPassword, studentMail, studentName, studentSurname;
    private AwesomeValidation awesomeValidation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.student_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initElements(view);
        awesomeValidation.addValidation(getActivity(), R.id.student_schoolID,
                "^20[0-9]{7}$", R.string.studentIDerror);
        awesomeValidation.addValidation(getActivity(), R.id.student_e_mail,
                Patterns.EMAIL_ADDRESS, R.string.emailerror);
        awesomeValidation.addValidation(getActivity(), R.id.student_name,
                "^[a-zA-Z]+$", R.string.nameerror);
        awesomeValidation.addValidation(getActivity(), R.id.student_surname,
                "^[a-zA-Z]+$", R.string.surnameerror);
        awesomeValidation.addValidation(getActivity(), R.id.student_password,
                "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[!_*.-]).{6,}$",
                R.string.passworderror);
    }

    private void initElements(View view) {
        studentID = (EditText) view.findViewById(R.id.student_schoolID);
        studentPassword = (EditText) view.findViewById(R.id.student_password);
        studentMail = (EditText) view.findViewById(R.id.student_e_mail);
        studentName = (EditText) view.findViewById(R.id.student_name);
        studentSurname = (EditText) view.findViewById(R.id.student_surname);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        Button registerButton = (Button) view.findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidation.validate()) {
                    String schoolID = studentID.getText().toString();
                    String password = studentPassword.getText().toString();
                    String mail = studentMail.getText().toString();
                    String name = studentName.getText().toString();
                    String surname = studentSurname.getText().toString();
                    String bluetoothMac = "NULL";

                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (bluetoothAdapter != null) bluetoothMac = bluetoothAdapter.getAddress();

                    Map<String, String> postParameters = new HashMap<>();
                    postParameters.put("schoolID", schoolID);
                    postParameters.put("password",password);
                    postParameters.put("mail", mail);
                    postParameters.put("name", name);
                    postParameters.put("surname",surname);
                    postParameters.put("BluetoothMAC", bluetoothMac);
                    postParameters.put("type", "studentRegister");

                    DatabaseManager.getmInstance(getActivity())
                    .execute("register",postParameters);

                }
            }
        });
    }
}
