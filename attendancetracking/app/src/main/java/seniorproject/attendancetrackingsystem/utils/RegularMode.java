package seniorproject.attendancetrackingsystem.utils;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.instacart.library.truetime.TrueTime;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.TimeZone;

import seniorproject.attendancetrackingsystem.helpers.Logger;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class RegularMode extends Service implements BeaconConsumer {
  public static final String ACTION = "REGULAR_MODE";
  private static final Region ALL_BEACONS = new Region("ALL_BEACONS", null, null, null);
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
  private final SimpleDateFormat dateFormatLog = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

  private Schedule.CourseInfo currentCourse = null;
  private BeaconManager beaconManager;
  private String search;
  private String filename;
  private final Queue<String> queue = new Queue<>();

  @Override
  public void onCreate() {
    super.onCreate();

    TrueTime.clearCachedInfo(getBaseContext());
    if (!TrueTime.isInitialized()) {
      try {
        TrueTime.build()
                .withNtpHost("time.google.com")
                .withConnectionTimeout(41328)
                .withLoggingEnabled(true)
                .withSharedPreferences(getBaseContext())
                .withServerResponseDelayMax(60000)
                .initialize();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    beaconManager = BeaconManager.getInstanceForApplication(this);
    beaconManager.getBeaconParsers().clear();
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
        .add(new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));
    beaconManager.setBackgroundMode(true);
    beaconManager.setBackgroundScanPeriod(10000); // 10 seconds scans
    beaconManager.setBackgroundBetweenScanPeriod(90000); // 90 seconds waits
    BeaconManager.setAndroidLScanningDisabled(true);
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
            SimpleDateFormat currentTimeFormatter = new SimpleDateFormat("HH:mm:ss", Locale
                    .ENGLISH);
            currentTimeFormatter.setTimeZone(TimeZone.getTimeZone("GMT+3"));
            for (Beacon x : collection) {

              if (x.getBluetoothAddress().equals(search)) {
                String value = currentTimeFormatter.format
                        (TrueTime.now());
                queue.enqueueDistinct(value);
                if (queue.size() >= 3) writeLog();
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
    SimpleDateFormat currentDateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale
            .ENGLISH);
    currentDateFormatter.setTimeZone(TimeZone.getTimeZone("GMT+3"));
    filename =
        currentCourse.getCourse_code()
            + "_"
            + currentCourse.getClassroom_id()
            + "_"
            + currentDateFormatter.format
                (TrueTime.now())
            + ".log";
    return START_NOT_STICKY;
  }
}
