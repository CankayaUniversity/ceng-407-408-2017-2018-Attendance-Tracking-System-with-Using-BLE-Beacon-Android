package seniorproject.attendancetrackingsystem.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import seniorproject.attendancetrackingsystem.R;

public class UploadImage extends AppCompatActivity {

    Button saveButton;
    ImageView imgTakenPic;
    private static final int CAM_REQUEST=1313;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        saveButton = (Button) findViewById(R.id.save);
        imgTakenPic = (ImageView)findViewById(R.id.avatar);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,CAM_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAM_REQUEST){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imgTakenPic.setImageBitmap(bitmap);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable picture = ((BitmapDrawable)imgTakenPic.getDrawable());
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

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                }
            }
        });
    }
}

