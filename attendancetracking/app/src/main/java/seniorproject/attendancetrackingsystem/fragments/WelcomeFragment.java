package seniorproject.attendancetrackingsystem.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.activities.SelectCurrentCourse;
import seniorproject.attendancetrackingsystem.activities.StudentActivity;
import seniorproject.attendancetrackingsystem.helpers.DatabaseManager;
import seniorproject.attendancetrackingsystem.helpers.SessionManager;
import seniorproject.attendancetrackingsystem.utils.RegularMode;

/* A simple {@link Fragment} subclass. */
public class WelcomeFragment extends Fragment {
  private static final int CAM_REQUEST = 1313;
  private ArrayAdapter<String> adapter;
  private Receiver mReceiver;
  private ArrayList<String> messages;
  private ListView listView;
  private Handler handler;
  private int classroom_id = 0;
  private String course_code = "";
  private boolean secure_mode = false;
  private boolean expired = false;
  private Timer timer;
  private final ArrayList<LatestCourses> latestCourses = new ArrayList<>();
  private String mCurrentPhotoPath;
  private Bitmap bitmap;

  public WelcomeFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(
          @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_welcome, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    handler = new Handler();
    timer = new Timer();
    SessionManager session = new SessionManager(Objects.requireNonNull(getActivity()).getApplicationContext());
    HashMap<String, String> userInfo = session.getUserDetails();
    ImageView avatar = getActivity().findViewById(R.id.avatar);
    if (userInfo.get(SessionManager.KEY_USER_IMG).isEmpty()
        || userInfo.get(SessionManager.KEY_USER_IMG) == null) {
      Picasso.with(getActivity()).load(R.drawable.unknown_trainer).fit().centerCrop().into(avatar);
    } else {
      String IMG_PREF = "http://attendancesystem.xyz/attendancetracking/";
      Picasso.with(getActivity())
          .load(IMG_PREF + userInfo.get(SessionManager.KEY_USER_IMG))
          .fit()
          .centerCrop()
          .placeholder(R.drawable.unknown_trainer)
          .into(avatar);
    }
    TextView nameSurnameField = getActivity().findViewById(R.id.w_user_name);
    TextView description = getActivity().findViewById(R.id.w_user_mail);
    String nameText =
        userInfo.get(SessionManager.KEY_USER_NAME)
            + " "
            + userInfo.get(SessionManager.KEY_USER_SURNAME).toUpperCase();
    String mailText = userInfo.get(SessionManager.KEY_USER_MAIL);
    nameSurnameField.setText(nameText);
    description.setText(mailText);
    listView = view.findViewById(R.id.notification_list);
    messages = new ArrayList<>();
    adapter =
        new ArrayAdapter<>(
            getActivity().getApplicationContext(), R.layout.notification_item, messages);
    Parcelable state = listView.onSaveInstanceState();
    listView.setAdapter(adapter);
    listView.onRestoreInstanceState(state);
    showMessages();
    timer.scheduleAtFixedRate(
        new TimerTask() {
          @Override
          public void run() {
            getLatestCoursesList();
          }
        },
        0,
        600000);
  }

  @Override
  public void onResume() {
    super.onResume();
    mReceiver = new Receiver();
    IntentFilter filter = new IntentFilter();
    filter.addAction(RegularMode.ACTION);
    filter.addAction("RegularModeStatus");
    Objects.requireNonNull(getActivity()).registerReceiver(mReceiver, filter);
  }

  @Override
  public void onPause() {
    super.onPause();
    Objects.requireNonNull(getActivity()).unregisterReceiver(mReceiver);
  }

  private void showMessages() {
    messages.clear();
    /* SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH);
    Date currentDate = new Date();*/
    if (classroom_id != 0) {
      if (secure_mode && !expired)
        messages.add(0, "Current Course: " + course_code + " \n(Secure Mode)");
      else if (secure_mode) {
        messages.add(0, "Current Course: " + course_code + " \n(Secure Mode - Expired)");
      } else messages.add(0, "Current Course: " + course_code);
    } else if (course_code.equals("end_of_the_day")) {
      messages.add(0, "End of the day");
    } else if (course_code.equals("null")) {
      secure_mode = false;
      expired = false;
      messages.add("There is not active course for now");
    } else if (course_code.equals("no_course_for_today")) {
      secure_mode = false;
      expired = false;
      messages.add("There is no course for today");
    } else if (course_code.equals("weekend")) {
      secure_mode = false;
      expired = false;
      messages.add("Weekend!");
    }else if(course_code.equals("conflict")){
      secure_mode = false;
      expired = false;
      messages.add("PLEASE SELECT COURSE YOU WANT TO ATTEND");
    }
    addAllLatestCourses();
    Parcelable state = listView.onSaveInstanceState();
    listView.setAdapter(adapter);
    listView.onRestoreInstanceState(state);
  }

  private void buildAlertDialog() {
    final AlertDialog.Builder alert =
        new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
    final LinearLayout layout = new LinearLayout(Objects.requireNonNull(getActivity()).getApplicationContext());
    layout.setOrientation(LinearLayout.HORIZONTAL);

    final EditText digit1 = new EditText(getActivity().getApplicationContext());
    final EditText digit2 = new EditText(getActivity().getApplicationContext());
    final EditText digit3 = new EditText(getActivity().getApplicationContext());
    final EditText digit4 = new EditText(getActivity().getApplicationContext());
    final EditText digit5 = new EditText(getActivity().getApplicationContext());
    final TextView info = new TextView(getActivity().getApplicationContext());
    info.setText(R.string.enter_token);
    info.setTextColor(Color.BLACK);
    info.setWidth(300);
    info.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    layout.addView(info);

    digit1.setWidth(22);
    digit1.setHeight(22);
    digit1.setTextColor(Color.BLACK);
    digit1.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    digit1.setId(R.id.digit1);
    digit1.setFocusable(true);
    digit1.setFilters(new InputFilter[] {new InputFilter.LengthFilter(1)});
    digit1.post(
        new Runnable() {
          @Override
          public void run() {
            final InputMethodManager imm =
                (InputMethodManager)
                    digit1.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(imm).showSoftInput(digit1, InputMethodManager.SHOW_IMPLICIT);
            digit1.requestFocus(); // needed if you have more then one input
          }
        });
    layout.addView(digit1);

    digit2.setWidth(22);
    digit2.setHeight(22);
    digit2.setTextColor(Color.BLACK);
    digit2.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    digit2.setId(R.id.digit2);
    digit2.setFocusable(true);
    digit2.setFilters(new InputFilter[] {new InputFilter.LengthFilter(1)});
    layout.addView(digit2);

    digit3.setWidth(22);
    digit3.setHeight(22);
    digit3.setTextColor(Color.BLACK);
    digit3.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    digit3.setId(R.id.digit3);
    digit3.setFocusable(true);
    digit3.setFilters(new InputFilter[] {new InputFilter.LengthFilter(1)});
    layout.addView(digit3);

    digit4.setWidth(22);
    digit4.setHeight(22);
    digit4.setTextColor(Color.BLACK);
    digit4.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    digit4.setId(R.id.digit4);
    digit4.setFilters(new InputFilter[] {new InputFilter.LengthFilter(1)});
    digit4.setFocusable(true);

    layout.addView(digit4);

    digit5.setWidth(22);
    digit5.setHeight(22);
    digit5.setTextColor(Color.BLACK);
    digit5.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    digit5.setId(R.id.digit5);
    digit5.setFocusable(true);
    digit5.setFilters(new InputFilter[] {new InputFilter.LengthFilter(1)});
    layout.addView(digit5);

    digit1.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (digit1.getText().length() == 1) digit2.requestFocus();
          }

          @Override
          public void afterTextChanged(Editable s) {}
        });
    digit2.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (digit2.getText().length() == 1) digit3.requestFocus();
          }

          @Override
          public void afterTextChanged(Editable s) {}
        });

    digit3.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (digit3.getText().length() == 1) digit4.requestFocus();
          }

          @Override
          public void afterTextChanged(Editable s) {}
        });

    digit4.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (digit4.getText().length() == 1) digit5.requestFocus();
          }

          @Override
          public void afterTextChanged(Editable s) {}
        });
    alert.setView(layout);

    alert.setPositiveButton(
        "Enter",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            String firstD = digit1.getText().toString();
            String secondD = digit2.getText().toString();
            String thirdD = digit3.getText().toString();
            String fourthD = digit4.getText().toString();
            String fifthD = digit5.getText().toString();

            if (!firstD.isEmpty()
                && !secondD.isEmpty()
                && !thirdD.isEmpty()
                && !fourthD.isEmpty()
                && !fifthD.isEmpty()) {
              String tokenize = firstD + secondD + thirdD + fourthD + fifthD;
              sendToken(tokenize);
            } else
              Toast.makeText(
                      getActivity().getApplicationContext(),
                      "Please fill all digits",
                      Toast.LENGTH_SHORT)
                  .show();
          }
        });

    alert.setNegativeButton(
        "Cancel",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
          }
        });
    alert.create().show();
  }

  private boolean isConnected() {
    ConnectivityManager connectivityManager =
        (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
    assert connectivityManager != null;
    // we are connected to a network
    return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()
            == NetworkInfo.State.CONNECTED
        || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()
            == NetworkInfo.State.CONNECTED;
  }

  private void sendToken(final String token) {
    if (isConnected()) {
      StringRequest request =
          new StringRequest(
              Request.Method.POST,
              DatabaseManager.SetOperations,
              new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                  try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean result = jsonObject.getBoolean("success");
                    if (result) {
                      boolean expired = jsonObject.getBoolean("expired");
                      if (!expired) {
                        openCamera();
                      } else {
                        toastMessageWithHandle("Secure mod is expired");
                      }
                    } else {
                      String message = jsonObject.getString("message");
                      toastMessageWithHandle(message);
                    }
                  } catch (JSONException e) {
                    e.printStackTrace();
                  }
                }
              },
              new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {}
              }) {
            @Override
            protected Map<String, String> getParams() {
              Map<String, String> params = new HashMap<>();
              params.put("classroom_id", String.valueOf(classroom_id));
              params.put("token_value", token);
              params.put("operation", "enter-token");
              return params;
            }
          };

      DatabaseManager.getInstance(Objects.requireNonNull(getActivity()).getApplicationContext()).execute(request);
    } else {
      toastMessageWithHandle("This action requires a network connection");
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    timer.cancel();
  }

  private void addAllLatestCourses() {
    for (LatestCourses x : latestCourses) {
      messages.add(x.toString());
      Parcelable state = listView.onSaveInstanceState();
      listView.setAdapter(adapter);
      listView.onRestoreInstanceState(state);
    }
  }

  private void getLatestCoursesList() {
    if (!isConnected()) return;
    StringRequest request =
        new StringRequest(
            Request.Method.POST,
            DatabaseManager.GetOperations,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
                try {
                  JSONObject jsonObject = new JSONObject(response);
                  boolean result = jsonObject.getBoolean("success");
                  if (!result) {
                    timer.cancel();
                    return;
                  }
                } catch (JSONException e) {
                  // do nothing
                }
                try {
                  JSONArray jsonArray = new JSONArray(response);
                  if (jsonArray.length() > 0) latestCourses.clear();
                  for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    LatestCourses temp =
                        new LatestCourses(
                            jsonObject.getString("course_code"),
                            jsonObject.getString("date"),
                            jsonObject.getString("hour"),
                            jsonObject.getInt("status"));
                    latestCourses.add(temp);
                  }
                } catch (JSONException e) {
                  e.printStackTrace();
                }
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {}
            }) {
          @Override
          protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<>();
            params.put(
                "user_id",
                new SessionManager(Objects.requireNonNull(getActivity()).getApplicationContext())
                    .getUserDetails()
                    .get(SessionManager.KEY_USER_ID));
            params.put("operation", "last-15-lectures");
            return params;
          }
        };
    try {
      DatabaseManager.getInstance(Objects.requireNonNull(getActivity()).getApplicationContext()).execute(request);
    } catch (NullPointerException e) {
      // do nothing
    }
  }

  private void toastMessageWithHandle(final String text) {
    handler.post(
        new Runnable() {
          @Override
          public void run() {
            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), text, Toast.LENGTH_SHORT).show();
          }
        });
  }

  private void openCamera() {

    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    File photoFile = null;
    try {
      photoFile = createImageFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (photoFile != null) {
      Uri photoURI;
      if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
        photoURI =
            FileProvider.getUriForFile(
                    Objects.requireNonNull(getActivity()), "com.example.android.fileprovider", photoFile);
      else photoURI = Uri.fromFile(photoFile);
      intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
      startActivityForResult(intent, CAM_REQUEST);
    }
  }

  private File createImageFile() throws IOException {
    // Create an image file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";
    File storageDir = null;
    if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
      storageDir = Objects.requireNonNull(getActivity()).getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    else storageDir = Objects.requireNonNull(getActivity()).getExternalFilesDir("Pictures");
    File image =
        File.createTempFile(
            imageFileName, /* prefix */ ".jpg", /* suffix */ storageDir /* directory */);

    // Save a file: path for use with ACTION_VIEW intents
    mCurrentPhotoPath = image.getAbsolutePath();
    return image;
  }

  private Bitmap rotateBitmapOrientation(String photoFilePath) {

    // Create and configure BitmapFactory
    BitmapFactory.Options bounds = new BitmapFactory.Options();
    bounds.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(photoFilePath, bounds);
    BitmapFactory.Options opts = new BitmapFactory.Options();
    Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
    // Read EXIF Data
    try {
      ExifInterface exif = new ExifInterface(photoFilePath);

      String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
      int orientation =
          orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
      int rotationAngle = 0;
      if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
      if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
      if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
      // Rotate Bitmap
      Matrix matrix = new Matrix();
      matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
      // Return result
      return Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
    } catch (IOException e) {
      // do nothing
    }
    return bm;
  }

  private String getStringImage(Bitmap img) {
    ByteArrayOutputStream bm = new ByteArrayOutputStream();
    img.compress(Bitmap.CompressFormat.JPEG, 100, bm);
    byte[] imageByte = bm.toByteArray();
    return Base64.encodeToString(imageByte, Base64.DEFAULT);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK) {
      if (requestCode == CAM_REQUEST) {
        bitmap = rotateBitmapOrientation(mCurrentPhotoPath);
        if (bitmap == null) {
          toastMessageWithHandle("Please re-take photo.");
          return;
        }
        bitmap = Bitmap.createScaledBitmap(bitmap, 400, 500, false);
        StringRequest request =
            new StringRequest(
                Request.Method.POST,
                DatabaseManager.SetOperations,
                new Response.Listener<String>() {
                  @Override
                  public void onResponse(String response) {
                    try {
                      JSONObject jsonObject = new JSONObject(response);
                      boolean result = jsonObject.getBoolean("success");
                      if (result) {
                        new SessionManager(getActivity()).disallowSecure();
                        toastMessageWithHandle("Your photograph is successfully saved.");
                      } else {
                        toastMessageWithHandle(jsonObject.getString("message"));
                      }
                    } catch (JSONException e) {
                      e.printStackTrace();
                    }
                  }
                },
                new Response.ErrorListener() {
                  @Override
                  public void onErrorResponse(VolleyError error) {}
                }) {
              @Override
              protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("operation", "secure-image");
                params.put(
                    "user_id",
                    new SessionManager(getActivity())
                        .getUserDetails()
                        .get(SessionManager.KEY_USER_ID));
                params.put("image", getStringImage(bitmap));
                params.put("classroom_id", String.valueOf(classroom_id));
                return params;
              }
            };
        try {
          DatabaseManager.getInstance(getActivity()).execute(request);
        } catch (NullPointerException e) {
          // do nothing
        }
      }
    }
  }

  private class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      course_code = intent.getStringExtra("course_code");
      classroom_id = intent.getIntExtra("classroom_id", 0);
      secure_mode = intent.getBooleanExtra("secure", false);
      expired = intent.getBooleanExtra("expired", false);
      if (secure_mode && !expired && !new SessionManager(getActivity()).secureStatus()) {
        listView.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
              @Override
              public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                  handler.post(
                      new Runnable() {
                        @Override
                        public void run() {
                          buildAlertDialog();
                        }
                      });
                }
              }
            });
      } else if(course_code.equals("conflict")){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            startActivity(new Intent(getContext(), SelectCurrentCourse.class));
          }
        });
      }
        else{
        listView.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
              @Override
              public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // clearing on item click event
              }
            });
      }
      showMessages();
    }
  }

  class LatestCourses {
    final String date;
    final String hour;
    final String course_code;
    final int status;

    LatestCourses(String course_code, String date, String hour, int status) {
      this.date = date;
      this.hour = hour;
      this.course_code = course_code;
      this.status = status;
    }

    @Override
    public String toString() {
      String output = date + " " + hour + " - " + course_code;
      switch (status) {
        case 0:
          output = output + " [Absent]";
          break;
        case 1:
          output = output + " [Nearly]";
          break;
        case 2:
        case 3:
          output = output + " [Attended]";
          break;
      }
      return output;
    }
  }
}
