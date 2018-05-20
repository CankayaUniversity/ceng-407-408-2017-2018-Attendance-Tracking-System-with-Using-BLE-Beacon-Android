package seniorproject.attendancetrackingsystem.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.DatabaseManager;
import seniorproject.attendancetrackingsystem.utils.Department;
import seniorproject.attendancetrackingsystem.utils.Globals;

public class LecturerRegister extends Fragment {
  private AwesomeValidation awesomeValidation;
  private Spinner departmentList;
  private EditText lecturerMail;
  private EditText lecturerName;
  private EditText lecturerSurname;
  private EditText lecturerPassword;

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.lecturer_register, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initElements(view);
    awesomeValidation.addValidation(
        getActivity(),
        R.id.lecturer_e_mail,
        "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@cankaya.edu.tr$",
        R.string.emailerror);
    awesomeValidation.addValidation(
        getActivity(), R.id.lecturer_name, "^[a-zA-ZğüşöçİĞÜŞÖÇ]+$", R.string.nameerror);
    awesomeValidation.addValidation(
        getActivity(), R.id.lecturer_surname, "^[a-zA-ZğüşöçİĞÜŞÖÇ]+$", R.string.surnameerror);
    awesomeValidation.addValidation(
        getActivity(),
        R.id.lecturer_password,
        "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[!_*.-]).{6,}$",
        R.string.passworderror);
  }
  private static String toTitleCase(String givenString) {

    String[] arr = givenString.split(" ");
    StringBuilder sb = new StringBuilder();

    for (String anArr : arr) {
      sb.append(anArr.substring(0, 1).toUpperCase(new Locale("tr", "TR")))
              .append(anArr.substring(1))
              .append(" ");
    }
    return sb.toString().trim();
  }
  private void initElements(View view) {
    departmentList = view.findViewById(R.id.departments);
    lecturerMail = view.findViewById(R.id.lecturer_e_mail);
    lecturerName = view.findViewById(R.id.lecturer_name);
    lecturerSurname = view.findViewById(R.id.lecturer_surname);
    lecturerPassword = view.findViewById(R.id.lecturer_password);
    final Button registerButton = view.findViewById(R.id.register_button);
    awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
    ArrayList<String> departments = new ArrayList<>();

    departments.add(0, "Choose your department");

    if (((Globals) getActivity().getApplication()).getDepartments() == null) {
      DatabaseManager.getmInstance(getActivity()).execute("get", "department-list", departments);
    } else {
      for (Department department : ((Globals) getActivity().getApplication()).getDepartments()) {
        departments.add(department.getDepartmentName());
      }
    }

    ArrayAdapter<String> adapter =
        new ArrayAdapter<>(getActivity(), R.layout.spinner_item, departments);
    departmentList.setAdapter(adapter);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    departmentList.setAdapter(adapter);
    registerButton.setOnClickListener(
        new View.OnClickListener() {
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
              ArrayList<Department> departments =
                  ((Globals) getActivity().getApplication()).getDepartments();
              int departmentId = -1;
              for (Department department : departments) {
                if (Objects.equals(department.getDepartmentName(), lecturerDepartment)) {
                  departmentId = department.getDepartmentId();
                  break;
                }
              }

              name = toTitleCase(name);
              surname = surname.toUpperCase(new Locale("tr","TR"));
              Map<String, String> postParameters = new HashMap<>();
              postParameters.put("mail", mail);
              postParameters.put("name", name);
              postParameters.put("surname", surname);
              postParameters.put("password", password);
              postParameters.put("departmentID", String.valueOf(departmentId));
              postParameters.put("type", "lecturerRegister");

              DatabaseManager.getmInstance(getActivity()).execute("register", postParameters);
            }
          }
        });
  }
}
