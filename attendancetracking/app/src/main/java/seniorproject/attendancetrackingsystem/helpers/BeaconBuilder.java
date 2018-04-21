package seniorproject.attendancetrackingsystem.helpers;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Binder;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class BeaconBuilder extends Service implements BeaconConsumer {
  public final static String ACTION = "BeaconBuilder";
  private static final Region ALL_BEACONS= new Region("ALL_BEACONS", null, null, null);
  private BeaconManager beaconManager;
  private ArrayList<Beacon> beacons;
  private ArrayList<String> ignoreList;
  private IBinder mBinder = new ServiceBinder();
  @Override
  public void onCreate() {
    super.onCreate();
    if(!BluetoothAdapter.getDefaultAdapter().isEnabled())
      BluetoothAdapter.getDefaultAdapter().enable();
    beacons = new ArrayList<>();
    ignoreList = new ArrayList<>();
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

    beaconManager.bind(this);
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return mBinder;
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
            beacons.clear();
            beacons.addAll(collection);
            Collections.sort(beacons, new Comparator<Beacon>() {
              @Override
              public int compare(Beacon o1, Beacon o2) {
                return Integer.compare(o1.getRssi(), o2.getRssi());
              }
            });
            if(beacons.size() > 0 && getFirstBeacon() != -1){
              try{
                beaconManager.stopRangingBeaconsInRegion(ALL_BEACONS);
              }catch (RemoteException e){
                e.printStackTrace();
              }
              Intent  intent = new Intent();
              intent.setAction(ACTION);
              intent.putExtra("MAC", beacons.get(getFirstBeacon()).getBluetoothAddress());
              sendBroadcast(intent);
            }
          }
        });
  }

  private int getFirstBeacon() {
    int index = 0;
    for (Beacon x : beacons) {
      if (!ignoreList.contains(x.getBluetoothAddress())) return index;
      index++;
    }
    return -1;
  }

  public void addToIgnoreList(String mac){
    ignoreList.add(mac);
    Log.i("IGNORE", mac);
  }

  public void continueTracking(){
    try{
      beaconManager.startRangingBeaconsInRegion(ALL_BEACONS);
    }catch (RemoteException e){
      e.printStackTrace();
    }
  }
public class ServiceBinder extends Binder {
    public BeaconBuilder getService(){
      return  BeaconBuilder.this;
    }
}
}
