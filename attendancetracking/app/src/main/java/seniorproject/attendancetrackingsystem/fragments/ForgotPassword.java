package seniorproject.attendancetrackingsystem.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import seniorproject.attendancetrackingsystem.R;

public class ForgotPassword extends Fragment {
  public ForgotPassword() {}

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.forgot_password, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    Bundle args = getArguments();
    if (args == null || args.getString("user_type").isEmpty()) getActivity().onBackPressed();

    Switch loginSwitch = getActivity().findViewById(R.id.role_switch);
    if (loginSwitch != null) loginSwitch.setVisibility(View.INVISIBLE);

    final EditText mail = view.findViewById(R.id.forgot_mail);
    final Button sendMail = view.findViewById(R.id.send_mail);

    sendMail.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            // TODO validate mail according to the user_type and send request
          }
        });
  }
}
