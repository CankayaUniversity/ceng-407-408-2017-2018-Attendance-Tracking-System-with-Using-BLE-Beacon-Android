package seniorproject.attendancetrackingsystem.securemode;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


public class BlinkTracker extends Tracker<Face> {

    private boolean leftFace = false;
    private boolean rightFace = false;
    private Context context;
    private Handler handler;
    private Activity activity;
    private GraphicOverlay pre;
    private final View view;

    public BlinkTracker(Context context, GraphicOverlay pre, Activity activity) {

        this.context = context;
        this.handler = new Handler(Looper.getMainLooper());
        this.activity = activity;
        this.view = activity.getWindow().getDecorView().getRootView();
        this.pre = pre;

    }

    public void onUpdate(Detector.Detections<Face> detections, Face face) {

        boolean leftEye = false;
        boolean rightEye = false;
        boolean allowRight = true;
        boolean allowLeft = true;
        boolean allowFace = false;
        List<Landmark> landmarks = face.getLandmarks();
        int count = 0;
        for (Landmark landmark : landmarks) {

            if (landmark.getType() == Landmark.LEFT_EYE || landmark.getType() == Landmark.LEFT_MOUTH ||
                    landmark.getType() == Landmark.RIGHT_EYE || landmark.getType() == Landmark.RIGHT_MOUTH) {
                count++;
            }
            if (count >= 4) {
                takeScreenshot(context);
            }
        }

        for (int i = 0; i < landmarks.size(); i++) {

            if (landmarks.get(i).getType() == Landmark.LEFT_EYE) {
                leftEye = true;
            } else if (landmarks.get(i).getType() == Landmark.RIGHT_EYE) {
                rightEye = true;
            }
        }

        if (leftEye && !rightEye) {
            if (allowLeft) {
                toastMessage("Left face caught");
            }
            leftFace = true;
            allowLeft = false;

        } else if (!leftEye && rightEye) {
            if (allowRight) {
                toastMessage("Right face caught");
            }
            rightFace = true;
            allowRight = false;
        }

        if (rightFace && leftFace) {

            if (!allowFace) {
                toastMessage("Face is tracked successfully");

            }

            rightFace = false;
            leftFace = false;
            allowFace = true;
        }

    }


    public void takeScreenshot(Context context_edit) {

        /*final Bitmap bitmap;
        view.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(view.getDrawingCache());

        final File f = new File(Environment.getExternalStorageDirectory()
                + File.separator + "test");

        Permissions.check(context_edit, Manifest.permission.WRITE_EXTERNAL_STORAGE, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                try {

                    f.mkdirs();
                    f.createNewFile();
                    out = new FileOutputStream(f);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , out);
                    out.flush();
                    out.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/
        /*Display display = ((Activity)context_edit).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

// Get root view

// Create the bitmap to use to draw the screenshot
        final Bitmap bitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_4444);*/
        pre.setDrawingCacheEnabled(true);
        Permissions.check(context_edit, Manifest.permission.WRITE_EXTERNAL_STORAGE, null, new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        try {
                            File folder = new File(Environment.getExternalStorageDirectory().getPath() + File.separator);
                            if (!folder.exists()) {
                                folder.mkdir();
                            }
                            String path = folder.getAbsolutePath() + File.separator + "snapshot2.PNG";
                            File file = new File(path);
                            try
                            {
                                file.createNewFile();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }

                            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bitmap);
                            view.draw(canvas);
                            FileOutputStream fos = null;
                            try {
                                fos = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 80, fos);
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                            finally {
                                bitmap.recycle();
                                if (fos != null) {
                                    try {
                                        fos.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        toastMessage("Screenshot is saved.");
    }

    private void toastMessage(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
