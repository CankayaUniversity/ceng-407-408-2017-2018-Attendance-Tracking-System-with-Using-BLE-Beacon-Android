package seniorproject.attendancetrackingsystem.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.DatabaseManager;

public class StudentRegister extends Fragment {
  private static final int CAM_REQUEST = 1313;
  private String mCurrentPhotoPath;
  private EditText studentId;
  private EditText studentPassword;
  private EditText studentMail;
  private EditText studentName;
  private EditText studentSurname;
  private AwesomeValidation awesomeValidation;
  private ImageView uploadImageView;
  private Uri photoURI;

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

  @Nullable
  @Override
  public View onCreateView(
          @NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.student_register, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initElements(view);
    awesomeValidation.addValidation(
        getActivity(), R.id.student_schoolID, "^20[0-9]{7}$", R.string.student_ID_error);
    awesomeValidation.addValidation(
        getActivity(),
        R.id.student_e_mail,
        "^([c]|(20))[0-9]{7}@student.cankaya.edu.tr$",
        R.string.email_error);
    awesomeValidation.addValidation(
        getActivity(), R.id.student_name, "^[a-zA-ZğüşöçİĞÜŞÖÇ]+$", R.string.name_error);
    awesomeValidation.addValidation(
        getActivity(), R.id.student_surname, "^[a-zA-ZğüşöçİĞÜŞÖÇ]+$", R.string.surname_error);
    awesomeValidation.addValidation(
        getActivity(),
        R.id.student_password,
        "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[!_*.-]).{6,}$",
        R.string.password_error);
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
              Bitmap bitmap = rotateBitmapOrientation(mCurrentPhotoPath);
              bitmap = Bitmap.createScaledBitmap(bitmap, 400, 500, false);

              name = toTitleCase(name);
              surname = surname.toUpperCase(new Locale("tr", "TR"));
              String android_id =  Settings.Secure.getString(Objects.requireNonNull(getActivity()).getContentResolver(),
                      Settings
                      .Secure.ANDROID_ID);
              Map<String, String> postParameters = new HashMap<>();
              postParameters.put("schoolID", schoolId);
              postParameters.put("password", password);
              postParameters.put("mail", mail);
              postParameters.put("name", name);
              postParameters.put("surname", surname);
              postParameters.put("android_id", android_id);
              postParameters.put("type", "studentRegister");
              postParameters.put("image", getStringImage(bitmap));

              DatabaseManager.getInstance(getActivity()).execute("register", postParameters);
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
    return Base64.encodeToString(imageByte, Base64.DEFAULT);
  }

  private File createImageFile() throws IOException {
    // Create an image file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";
    File storageDir;
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

  private void openCamera() {

    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    File photoFile = null;
    try {
      photoFile = createImageFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (photoFile != null) {
      if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
        photoURI =
            FileProvider.getUriForFile(
                    Objects.requireNonNull(getActivity()), "com.example.android.fileprovider", photoFile);
      else photoURI = Uri.fromFile(photoFile);
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

  private int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath) {
    int rotate = 0;
    try {
      context.getContentResolver().notifyChange(imageUri, null);
      File imageFile = new File(imagePath);
      ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
      int orientation =
          exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

      switch (orientation) {
        case ExifInterface.ORIENTATION_ROTATE_270:
          rotate = 270;
          break;
        case ExifInterface.ORIENTATION_ROTATE_180:
          rotate = 180;
          break;
        case ExifInterface.ORIENTATION_ROTATE_90:
          rotate = 90;
          break;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return rotate;
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

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK) {
      if (requestCode == CAM_REQUEST) {
        int rotateImage = getCameraPhotoOrientation(getActivity(), photoURI, mCurrentPhotoPath);

        uploadImageView.setImageURI(photoURI);
        uploadImageView.setRotation(rotateImage);
        uploadImageView.setVisibility(View.VISIBLE);
      }
    }
  }
}
