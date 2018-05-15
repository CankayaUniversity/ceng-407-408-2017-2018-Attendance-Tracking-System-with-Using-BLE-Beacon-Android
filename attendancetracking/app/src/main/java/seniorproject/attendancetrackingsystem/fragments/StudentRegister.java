package seniorproject.attendancetrackingsystem.fragments;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.DatabaseManager;

public class StudentRegister extends Fragment {
  private EditText studentId;
  private EditText studentPassword;
  private EditText studentMail;
  private EditText studentName;
  private EditText studentSurname;
  private AwesomeValidation awesomeValidation;
  private ImageView uploadImageView;
  private static final int CAM_REQUEST=1313;

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.student_register, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initElements(view);
    awesomeValidation.addValidation(
        getActivity(), R.id.student_schoolID, "^20[0-9]{7}$", R.string.studentIDerror);
    awesomeValidation.addValidation(
        getActivity(),
        R.id.student_e_mail,
        "^([c]|(20))[0-9]{7}@student.cankaya.edu.tr$",
        R.string.emailerror);
    awesomeValidation.addValidation(
        getActivity(), R.id.student_name, "^[a-zA-Z]+$", R.string.nameerror);
    awesomeValidation.addValidation(
        getActivity(), R.id.student_surname, "^[a-zA-Z]+$", R.string.surnameerror);
    awesomeValidation.addValidation(
        getActivity(),
        R.id.student_password,
        "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[!_*.-]).{6,}$",
        R.string.passworderror);
  }

  private void initElements(View view) {
    studentId = view.findViewById(R.id.student_schoolID);
    studentPassword = view.findViewById(R.id.student_password);
    studentMail = view.findViewById(R.id.student_e_mail);
    studentName = view.findViewById(R.id.student_name);
    studentSurname = view.findViewById(R.id.student_surname);
    awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
    uploadImageView = view.findViewById(R.id.upload_image_view);
    uploadImageView.setVisibility(View.INVISIBLE);

    Button uploadImage = view.findViewById(R.id.upload_image);
    Button registerButton = view.findViewById(R.id.register_button);
    registerButton.setOnClickListener(
        new View.OnClickListener() {
          @SuppressLint("HardwareIds")
          @Override
          public void onClick(View v) {
            if (awesomeValidation.validate()) {
              String schoolId = studentId.getText().toString();
              String password = studentPassword.getText().toString();
              String mail = studentMail.getText().toString();
              String name = studentName.getText().toString();
              String surname = studentSurname.getText().toString();
              String bluetoothMac = "NULL";

              BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
              if (bluetoothAdapter != null) {
                  bluetoothMac = bluetoothAdapter.getAddress();
              }
              Map<String, String> postParameters = new HashMap<>();
              postParameters.put("schoolID", schoolId);
              postParameters.put("password", password);
              postParameters.put("mail", mail);
              postParameters.put("name", name);
              postParameters.put("surname", surname);
              postParameters.put("BluetoothMAC", bluetoothMac);
              postParameters.put("type", "studentRegister");

              DatabaseManager.getmInstance(getActivity()).execute("register", postParameters);
            }
          }
        });

    uploadImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        openCamera();
      }
    });
  }

  private void openCamera() {


    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    startActivityForResult(intent, CAM_REQUEST);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if(requestCode == CAM_REQUEST){
      Bitmap bitmap = (Bitmap) data.getExtras().get("data");
      uploadImageView.setImageBitmap(bitmap);
    }


        BitmapDrawable picture = ((BitmapDrawable)uploadImageView.getDrawable());
        Bitmap pictureBm = picture.getBitmap();


        FileOutputStream arrayStream = null;

        // Write to SD Card
        try {
          File sdCard = Environment.getExternalStorageDirectory();
          File dir = new File(sdCard.getAbsolutePath() + "/camtest");
          dir.mkdirs();

          String fileName = String.format("%d.jpeg", System.currentTimeMillis());
          File outFile = new File(dir, fileName);

          arrayStream = new FileOutputStream(outFile);
          pictureBm.compress(Bitmap.CompressFormat.JPEG,100,arrayStream);
          arrayStream.flush();
          arrayStream.close();

          Log.d("Taken Picture", "onPictureTaken - wrote to " + outFile.getAbsolutePath());
          uploadImageView.setVisibility(View.VISIBLE);

        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
  }
}
