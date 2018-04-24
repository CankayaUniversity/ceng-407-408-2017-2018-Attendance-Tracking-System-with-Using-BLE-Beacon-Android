package seniorproject.attendancetrackingsystem.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class RegularMode extends Service implements BeaconConsumer {
  public static final String ACTION = "REGULAR_MODE";
  private static final Region ALL_BEACONS = new Region("ALL_BEACONS", null, null, null);
  private BeaconManager beaconManager;
  private Schedule schedule;
  private ArrayList<Beacon> beacons;

  @Override
  public void onCreate() {
    super.onCreate();
    beacons = new ArrayList<>();
    beaconManager = BeaconManager.getInstanceForApplication(this);
    beaconManager
        .getBeaconParsers()
        .add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
    beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser
            .EDDYSTONE_UID_LAYOUT));
    beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser
            .EDDYSTONE_URL_LAYOUT));
    beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser
            .EDDYSTONE_TLM_LAYOUT));
    beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser
            .URI_BEACON_LAYOUT));
    beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser
            .ALTBEACON_LAYOUT));
    new BackgroundPowerSaver(this);
    beaconManager.bind(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    beaconManager.unbind(this);
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
            Schedule.CourseInfo current = filter(collection);
            if (current != null && beacons.size() == 1) {
              // TODO LOG TIME
              broadcastMessage(current.getCourse_code());
              Log.i("CURRENT COURSE", current.getCourse_code());
            } else if (current == null) {
              broadcastMessage("null");
              Log.i("CURRENT COURSE", "null");
            }
          }
        });
  }
private void broadcastMessage(final String message){
    Intent intent = new Intent();
    intent.setAction(ACTION);
    intent.putExtra("course_code", message);
    sendBroadcast(intent);
}
  private Schedule.CourseInfo filter(Collection<Beacon> beaconList) {
    beacons.clear();
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
      Date currentDate = dateFormat.parse(dateFormat.format(new Date()));
      Schedule.CourseInfo currentCourse = null;
      for (Schedule.CourseInfo x : schedule.getCourses()) {
        String start = x.getHour();
        String end = start.substring(0, 2);
        end = String.valueOf(Integer.parseInt(end) + 1) + ":10";
        if (currentDate.after(dateFormat.parse(start))
            && currentDate.before(dateFormat.parse(end))) {
          currentCourse = x;
        }
      }
      if (currentCourse == null) return null;
      for (Beacon x : beaconList) {
        if (x.getBluetoothAddress().equals(Objects.requireNonNull(currentCourse).getBeacon_mac())) {
          beacons.add(x);
        }
      }
      return currentCourse;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    schedule = (Schedule) intent.getSerializableExtra("schedule");
    if (schedule != null) {
      if (schedule.getCourses().size() <= 0) stopSelf();
    }
    return START_NOT_STICKY;
  }
}
