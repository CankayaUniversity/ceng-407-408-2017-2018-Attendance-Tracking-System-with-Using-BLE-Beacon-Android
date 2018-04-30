package seniorproject.attendancetrackingsystem.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import java.util.Collection;

public class RegularMode extends Service implements BeaconConsumer {
  public static final String ACTION = "REGULAR_MODE";
  private static final Region ALL_BEACONS = new Region("ALL_BEACONS", null, null, null);
  private BeaconManager beaconManager;
  private String search;

  @Override
  public void onCreate() {
    super.onCreate();
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
            boolean flag = false;
            for(Beacon x : collection){
              if(x.getBluetoothAddress().equals(search)) flag = true;
            }
            broadcastMessage(flag);
          }
        });
  }
private void broadcastMessage(final boolean result){
    Intent intent = new Intent();
    intent.setAction(ACTION);
    intent.putExtra("found", result);
    sendBroadcast(intent);
}


  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
   search = intent.getStringExtra("search");
   if(search.isEmpty()) stopSelf();
    return START_NOT_STICKY;
  }
}
