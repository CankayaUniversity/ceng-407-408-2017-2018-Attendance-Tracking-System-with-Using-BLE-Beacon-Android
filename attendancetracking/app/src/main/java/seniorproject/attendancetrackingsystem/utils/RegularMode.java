package seniorproject.attendancetrackingsystem.utils;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import seniorproject.attendancetrackingsystem.helpers.Logger;
import seniorproject.attendancetrackingsystem.helpers.SessionManager;

public class RegularMode extends Service implements BeaconConsumer {
  public static final String ACTION = "REGULAR_MODE";
  private static final Region ALL_BEACONS = new Region("ALL_BEACONS", null, null, null);
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
  private final SimpleDateFormat dateFormatLog = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
  private Schedule.CourseInfo currentCourse = null;
  private BeaconManager beaconManager;
  private String search;
  private String filename;
  private Queue<String> queue = new Queue<>();

  @Override
  public void onCreate() {
    super.onCreate();
    beaconManager = BeaconManager.getInstanceForApplication(this);
    beaconManager
        .getBeaconParsers()
        .add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
    beaconManager
        .getBeaconParsers()
        .add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
    beaconManager
        .getBeaconParsers()
        .add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
    beaconManager
        .getBeaconParsers()
        .add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
    beaconManager
        .getBeaconParsers()
        .add(new BeaconParser().setBeaconLayout(BeaconParser.URI_BEACON_LAYOUT));
    beaconManager
        .getBeaconParsers()
        .add(new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));
    beaconManager.setBackgroundMode(true);
    beaconManager.setBackgroundScanPeriod(30000); // 30 seconds
    beaconManager.setBackgroundBetweenScanPeriod(60 * 1000 + 30000); // 1 minutes and 30 seconds
    try {
      beaconManager.updateScanPeriods();
    } catch (RemoteException e) {
    }
    // new BackgroundPowerSaver(this);
    beaconManager.bind(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    try {
      beaconManager.stopRangingBeaconsInRegion(ALL_BEACONS);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    beaconManager.unbind(this);
    if (queue.size() > 0) writeLog();
  }

  @Override
  public void onBeaconServiceConnect() {
    try {
      beaconManager.startRangingBeaconsInRegion(ALL_BEACONS);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    beaconManager.addRangeNotifier(
        new RangeNotifier() {
          @Override
          public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
            boolean flag = false;
            for (Beacon x : collection) {

              if (x.getBluetoothAddress().equals(search)) {
                String value = dateFormatLog.format(new Date());
                queue.enqueueDistinct(value);
                if (queue.size() >= 5) writeLog();
                flag = true;
              }
            }

          }
        });
  }



  private void writeLog() {
    try {
      File root = new File(Environment.getExternalStorageDirectory(), "AttendanceTracking");

      if (!root.exists()) root.mkdirs();

      File logfile = new File(root, filename);
      FileWriter writer = new FileWriter(logfile, true);
      while (!queue.isEmpty()) {
        String value = queue.dequeue() + "\n";
        writer.append(value);
      }
      queue.clear();
      writer.flush();
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    runLogger();
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  private void runLogger() {
    Intent log = new Intent(this, Logger.class);
    log.putExtra("start", currentCourse.getHour());
    log.putExtra("stop", currentCourse.getEnd_hour());
    log.putExtra("classroom_id", currentCourse.getClassroom_id());
    log.putExtra("filename", filename);
    startService(log);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    search = intent.getStringExtra("search");
    currentCourse = (Schedule.CourseInfo) intent.getSerializableExtra("course-info");
    if (search.isEmpty() || currentCourse == null) stopSelf();
    int user_id =
        Integer.parseInt(new SessionManager(getBaseContext()).getUserDetails().get("user_id"));
    filename =
        currentCourse.getCourse_code()
            + "_"
            + currentCourse.getClassroom_id()
            + "_"
            + dateFormat.format(new Date())
            + ".txt";
    return START_NOT_STICKY;
  }
}
