package seniorproject.attendancetrackingsystem.fragments;


import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.DatabaseManager;
import seniorproject.attendancetrackingsystem.utils.Department;
import seniorproject.attendancetrackingsystem.utils.Globals;

public class LecturerRegister extends Fragment {
    private AwesomeValidation awesomeValidation;
    private Spinner departmentList;
    private EditText lecturerMail, lecturerName, lecturerSurname, lecturerPassword;
    private ArrayList<String> departments;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lecturer_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initElements(view);
        awesomeValidation.addValidation(getActivity(), R.id.lecturer_e_mail,
                Patterns.EMAIL_ADDRESS, R.string.emailerror);
        awesomeValidation.addValidation(getActivity(), R.id.lecturer_name,
                "^[a-zA-Z]+$", R.string.nameerror);
        awesomeValidation.addValidation(getActivity(), R.id.lecturer_surname,
                "^[a-zA-Z]+$", R.string.surnameerror);
        awesomeValidation.addValidation(getActivity(), R.id.lecturer_password,
                "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[!_*.-]).{6,}$",
                R.string.passworderror);
    }

    private void initElements(View view) {
        departmentList = (Spinner) view.findViewById(R.id.departments);
        lecturerMail = (EditText) view.findViewById(R.id.lecturer_e_mail);
        lecturerName = (EditText) view.findViewById(R.id.lecturer_name);
        lecturerSurname = (EditText) view.findViewById(R.id.lecturer_surname);
        lecturerPassword = (EditText) view.findViewById(R.id.lecturer_password);
        Button registerButton = (Button) view.findViewById(R.id.register_button);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        departments = new ArrayList<>();

        departments.add(0, "Choose your department");

        if (((Globals) getActivity().getApplication()).getDepartments() == null) {
            DatabaseManager.getmInstance(getActivity())
                    .execute("get", "department-list", departments);
        } else {
            for (Department department
                    :
                    ((Globals) getActivity().getApplication()).getDepartments()) {
                departments.add(department.getDepartmentName());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_item, departments);
        departmentList.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentList.setAdapter(adapter);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidation.validate()) {
                    if (departmentList.getSelectedItemId() == 0) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("Department is empty");
                        alertDialog.setMessage("Please choose your department from the list");
                        alertDialog.show();
                        return;
                    }
                    String mail = lecturerMail.getText().toString();
                    String password = lecturerPassword.getText().toString();
                    String name = lecturerName.getText().toString();
                    String surname = lecturerSurname.getText().toString();
                    String lecturerDepartment = departmentList.getSelectedItem().toString();
                    ArrayList<Department> departments = ((Globals) getActivity().getApplication()).getDepartments();
                    int departmentID = -1;
                    for (Department department : departments) {
                        if (department.getDepartmentName() == lecturerDepartment) {
                            departmentID = departments.indexOf(department);
                            break;
                        }
                    }
                    Map<String, String> postParameters = new HashMap<>();
                    postParameters.put("mail", mail);
                    postParameters.put("name", name);
                    postParameters.put("surname", surname);
                    postParameters.put("password", password);
                    postParameters.put("departmentID", String.valueOf(departmentID));
                    postParameters.put("type", "lecturerRegister");

                    DatabaseManager.getmInstance(getActivity()).execute("register",postParameters);
                }
            }
        });
    }

}
