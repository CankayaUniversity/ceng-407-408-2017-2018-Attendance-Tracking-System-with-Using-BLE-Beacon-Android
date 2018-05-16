package seniorproject.attendancetrackingsystem.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.DatabaseManager;

public class StudentRegister extends Fragment {
  private static final int CAM_REQUEST = 1313;
  String mCurrentPhotoPath;
  private EditText studentId;
  private EditText studentPassword;
  private EditText studentMail;
  private EditText studentName;
  private EditText studentSurname;
  private AwesomeValidation awesomeValidation;
  private ImageView uploadImageView;

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

    final Button uploadImage = view.findViewById(R.id.upload_image);
    Button registerButton = view.findViewById(R.id.register_button);
    registerButton.setOnClickListener(
        new View.OnClickListener() {
          @SuppressLint("HardwareIds")
          @Override
          public void onClick(View v) {
            if (awesomeValidation.validate() && hasImage(uploadImageView)) {
              String schoolId = studentId.getText().toString();
              String password = studentPassword.getText().toString();
              String mail = studentMail.getText().toString();
              String name = studentName.getText().toString();
              String surname = studentSurname.getText().toString();
              Bitmap bitmap = ((BitmapDrawable) uploadImageView.getDrawable()).getBitmap();
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
              postParameters.put("image", getStringImage(bitmap));

              DatabaseManager.getmInstance(getActivity()).execute("register", postParameters);
            }
          }
        });

    uploadImage.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            openCamera();
          }
        });
  }

  private String getStringImage(Bitmap img) {
    ByteArrayOutputStream bm = new ByteArrayOutputStream();
    img.compress(Bitmap.CompressFormat.JPEG, 100, bm);
    byte[] imageByte = bm.toByteArray();
    String encode = Base64.encodeToString(imageByte, Base64.DEFAULT);
    return encode;
  }

  private File createImageFile() throws IOException {
    // Create an image file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";
    File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    File image =
        File.createTempFile(
            imageFileName, /* prefix */ ".jpg", /* suffix */ storageDir /* directory */);

    // Save a file: path for use with ACTION_VIEW intents
    mCurrentPhotoPath = image.getAbsolutePath();
    return image;
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
      Uri photoURI =
          FileProvider.getUriForFile(getActivity(), "com.example.android.fileprovider", photoFile);
      intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
      startActivityForResult(intent, CAM_REQUEST);
    }
  }

  private boolean hasImage(ImageView view) {
    Drawable drawable = view.getDrawable();
    boolean hasImage = (drawable != null);

    if (hasImage && (drawable instanceof BitmapDrawable)) {
      hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
    }

    return hasImage;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK) {
      if (requestCode == CAM_REQUEST) {
        int targetW = uploadImageView.getWidth();
        int targetH = uploadImageView.getHeight();

        BitmapFactory.Options bmoptions = new BitmapFactory.Options();
        bmoptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmoptions);
        int photoW = bmoptions.outWidth;
        int photoH = bmoptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        bmoptions.inJustDecodeBounds = false;
        bmoptions.inSampleSize = scaleFactor;
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmoptions);
        uploadImageView.setImageBitmap(bitmap);
      }
    }
  }
}
