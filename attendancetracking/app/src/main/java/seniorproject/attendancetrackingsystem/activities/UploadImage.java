package seniorproject.attendancetrackingsystem.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import seniorproject.attendancetrackingsystem.R;

public class UploadImage extends AppCompatActivity {

    Button btnpic;
    Button btnsave;
    ImageView imgTakenPic;
    private static final int CAM_REQUEST=1313;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        btnpic  = (Button) findViewById(R.id.button);
        btnsave = (Button) findViewById(R.id.save);
        imgTakenPic = (ImageView)findViewById(R.id.imageView);
        btnpic.setOnClickListener(new btnTakePhotoClicker());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAM_REQUEST){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imgTakenPic.setImageBitmap(bitmap);
        }
    }

    class btnTakePhotoClicker implements  Button.OnClickListener{

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,CAM_REQUEST);
        }
    }
}

