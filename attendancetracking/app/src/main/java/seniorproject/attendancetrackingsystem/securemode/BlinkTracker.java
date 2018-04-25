package seniorproject.attendancetrackingsystem.securemode;
import android.content.Context;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

import java.util.List;


public class BlinkTracker extends Tracker<Face> {

  private boolean leftFace  = false;
  private boolean rightFace = false;
  Context context;
  private Handler handler;

  public BlinkTracker(Context context){

    this.context = context;
    this.handler = new Handler(Looper.getMainLooper());

  }

  public void onUpdate(Detector.Detections<Face> detections, Face face){

    boolean leftEye = false;
    boolean rightEye = false;
    boolean allowRight = true;
    boolean allowLeft = true;
    boolean allowFace = false;
    List<Landmark> landmarks = face.getLandmarks();

    for (int i = 0; i<landmarks.size(); i++) {

      if( landmarks.get(i).getType() == Landmark.LEFT_EYE) {
        leftEye = true;
      }
      else if(landmarks.get(i).getType() == Landmark.RIGHT_EYE){
        rightEye = true;
      }
    }

    if(leftEye && !rightEye){
      if(allowLeft)
      {
        toastMessage("Left face caught");
      }
      leftFace = true;
      allowLeft = false;

    }
    else if(!leftEye && rightEye){
      if(allowRight)
      {
        toastMessage("Right face caught");
      }
      rightFace = true;
      allowRight = false;
    }

    if(rightFace && leftFace) {

      if(!allowFace)
      {
        toastMessage("Face is tracked successfully");

      }

      rightFace = false;
      leftFace = false;
      allowFace = true;
    }

  }

  private void toastMessage(final String message) {
    handler.post(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
      }
    });
  }
}
