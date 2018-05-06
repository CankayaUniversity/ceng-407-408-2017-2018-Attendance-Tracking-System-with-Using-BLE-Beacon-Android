package seniorproject.attendancetrackingsystem.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.SessionManager;
import seniorproject.attendancetrackingsystem.utils.Schedule;

/* A simple {@link Fragment} subclass. */
public class WelcomeFragmentLecturer extends Fragment {

    public int token;
    private Schedule schedule;
    private  Date date1;
    private  Date date2;
    private long firstTime;
    private long secondTime;

  public WelcomeFragmentLecturer() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_welcome_lecturer, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      SessionManager session = new SessionManager(getActivity().getApplicationContext());
      HashMap<String, String> userInfo = session.getUserDetails();
      TextView nameSurnameField = getActivity().findViewById(R.id.w_user_name);
      TextView description = getActivity().findViewById(R.id.w_user_mail);
      final Switch secureSwitch = getActivity().findViewById(R.id.secure_switch);
      String nameText =
              userInfo.get(SessionManager.KEY_USER_NAME)
                      + " "
                      + userInfo.get(SessionManager.KEY_USER_SURNAME).toUpperCase();
      String mailText = userInfo.get(SessionManager.KEY_USER_MAIL);
      nameSurnameField.setText(nameText);
      description.setText(mailText);

      secureSwitch.setOnCheckedChangeListener(
              new CompoundButton.OnCheckedChangeListener() {
                  @Override
                  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                      if (secureSwitch.isChecked()) {
                          Toast.makeText(getActivity().getApplicationContext(),
                                  "Secure mode is activated",
                                  Toast.LENGTH_SHORT).show();

                          Random r = new Random(System.currentTimeMillis());
                          token = ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));

                          Toast.makeText(getActivity().getApplicationContext(),
                                  "Token: " + token, Toast.LENGTH_LONG).show();
                          buildAlert().show();

                          date1 = new Date();
                          firstTime = date1.getTime();
                  /*try
                  {
                      SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                      Date currentDate = dateFormat.parse(dateFormat.format(new Date()));
                      Schedule.CourseInfo currentCourse = null;
                      for (Schedule.CourseInfo x : schedule.getCourses()) {
                          String start = x.getHour();
                          String end = start.substring(0, 2);
                          end = String.valueOf(Integer.parseInt(end) + 1) + ":10";
                          if (currentDate.after(dateFormat.parse(start))
                                  && currentDate.before(dateFormat.parse(end))) {
                              currentCourse = x;
                          }

                      }
                  }
                  catch(ParseException e) {
                      e.printStackTrace();
                  }*/
                      }
                  }
              });
  }
    private AlertDialog.Builder buildAlert(){ //TODO Database fetch
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(),
                AlertDialog.THEME_HOLO_LIGHT);
        final LinearLayout layout = new LinearLayout(getActivity().getApplicationContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);

        final EditText digit1 = new EditText(getActivity().getApplicationContext());

        digit1.setWidth(15);
        digit1.setHeight(15);
        digit1.setTextColor(Color.BLACK);
        digit1.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        digit1.setId(R.id.digit1);
        digit1.setFilters(new InputFilter[] {new InputFilter.LengthFilter(1)});

        layout.addView(digit1);

        final EditText digit2 = new EditText(getActivity().getApplicationContext());

        digit2.setWidth(15);
        digit2.setHeight(15);
        digit2.setTextColor(Color.BLACK);
        digit2.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        digit2.setId(R.id.digit2);
        digit2.setFilters(new InputFilter[] {new InputFilter.LengthFilter(1)});

        layout.addView(digit2);

        final EditText digit3 = new EditText(getActivity().getApplicationContext());

        digit3.setWidth(15);
        digit3.setHeight(15);
        digit3.setTextColor(Color.BLACK);
        digit3.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        digit3.setId(R.id.digit3);
        digit3.setFilters(new InputFilter[] {new InputFilter.LengthFilter(1)});
        layout.addView(digit3);

        final EditText digit4 = new EditText(getActivity().getApplicationContext());

        digit4.setWidth(15);
        digit4.setHeight(15);
        digit4.setTextColor(Color.BLACK);
        digit4.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        digit4.setId(R.id.digit4);
        digit4.setFilters(new InputFilter[] {new InputFilter.LengthFilter(1)});

        layout.addView(digit4);

        final EditText digit5 = new EditText(getActivity().getApplicationContext());

        digit5.setWidth(15);
        digit5.setHeight(15);
        digit5.setTextColor(Color.BLACK);
        digit5.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        digit5.setId(R.id.digit5);
        digit5.setFilters(new InputFilter[] {new InputFilter.LengthFilter(1)});

        layout.addView(digit5);
        alert.setView(layout);


        alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String firstD = digit1.getText().toString();
                String secondD = digit2.getText().toString();
                String thirdD = digit3.getText().toString();
                String fourthD = digit4.getText().toString();
                String fifthD = digit5.getText().toString();

                date2 = new Date();

                secondTime = date2.getTime();

                long diff = (secondTime - firstTime)/60;
                if(diff >= 5) {

                    Toast.makeText(getActivity().getApplicationContext(),
                            "Token entering deadline is passed",Toast.LENGTH_SHORT).show();
                }

                if (!firstD.isEmpty() && !secondD.isEmpty() && !thirdD.isEmpty() &&
                        !fourthD.isEmpty() && !fifthD.isEmpty()) {
                    String tokenize = firstD + secondD + thirdD + fourthD + fifthD;

                    if (tokenize.equals(String.valueOf(token))) {
                        Toast.makeText(getActivity().getApplicationContext(), "Token is entered successfully",
                                Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "You entered a wrong token",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    Toast.makeText(getActivity().getApplicationContext(), "Please fill all digits",
                            Toast.LENGTH_SHORT).show();

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        digit1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                digit1.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        digit1.post(new Runnable() {
                            @Override
                            public void run() {
                                if(digit1.getText().length() == digit1.getFilters().length)
                                    digit2.requestFocus();
                            }
                        });
                        return false;
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        digit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                digit2.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        digit2.post(new Runnable() {
                            @Override
                            public void run() {
                                if(digit2.getText().length() == digit2.getFilters().length)
                                    digit3.requestFocus();
                            }
                        });

                        return false;
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        digit3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                digit3.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        digit2.post(new Runnable() {
                            @Override
                            public void run() {
                                if(digit3.getText().length() == digit3.getFilters().length)
                                    digit4.requestFocus();
                            }
                        });
                        return false;
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        digit4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                digit4.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        digit2.post(new Runnable() {
                            @Override
                            public void run() {
                                if(digit4.getText().length() == digit4.getFilters().length)
                                    digit5.requestFocus();
                            }
                        });
                        return false;
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return alert;

  }
  }

