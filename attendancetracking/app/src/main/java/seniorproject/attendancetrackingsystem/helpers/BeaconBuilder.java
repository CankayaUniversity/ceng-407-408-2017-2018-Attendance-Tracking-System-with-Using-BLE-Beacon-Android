package seniorproject.attendancetrackingsystem.helpers;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.os.RemoteException;

import com.aprilbrother.aprilbrothersdk.Beacon;
import com.aprilbrother.aprilbrothersdk.BeaconManager;
import com.aprilbrother.aprilbrothersdk.Region;
import com.aprilbrother.aprilbrothersdk.utils.AprilL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BeaconBuilder {
  private static final Region ALL_BEACONS_REGION = new Region("REGION01", null, null, null);
  private ProgressDialog progressDialog;
  private AlertDialog alertDialog;
  private Context context;
  private BeaconManager beaconManager;
  private ArrayList<Beacon> beacons;
  private LinkedList<String> ignoreList;

  public BeaconBuilder(Context context) {
    this.context = context;
    progressDialog = new ProgressDialog(context);
    progressDialog.setTitle("Beacon syncronizer");
    progressDialog.setMessage("GOING");
    alertDialog = new AlertDialog.Builder(context).create();
    alertDialog.setTitle("Beacon is detected");
    progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        try{
          beaconManager.stopRanging(ALL_BEACONS_REGION);
        }catch (RemoteException e){
          e.printStackTrace();
        }
      }
    });
    initialize();
    connectToTheService();
  }
private int getFirstBeacon(){
    int index = 0;
    for(Beacon x : beacons){
      if(!ignoreList.contains(x.getMacAddress())) return index;
      index++;
    }
    return -1;
}
  private void initialize() {
    BluetoothAdapter.getDefaultAdapter().enable();
    beacons = new ArrayList<>();
    ignoreList = new LinkedList<>();
    AprilL.enableDebugLogging(true);
    beaconManager = new BeaconManager(context.getApplicationContext());
    beaconManager.setRangingListener(
        new BeaconManager.RangingListener() {
          @Override
          public void onBeaconsDiscovered(Region region, List<Beacon> list) {
            beacons.clear();
            beacons.addAll(list);

            ComparatorBeaconByRssi com = new ComparatorBeaconByRssi();
            Collections.sort(beacons, com);

            if (beacons.size() > 0 && getFirstBeacon() != -1) {
              try {
                final String mac = beacons.get(getFirstBeacon()).getMacAddress();
                progressDialog.setMessage("Beacon: " + beacons.get(0).getMacAddress());
                //TODO CONFIRMATION DIALOG WILL BE CALLED
                beaconManager.stopRanging(ALL_BEACONS_REGION);
               progressDialog.hide();
               alertDialog.setMessage("Mac address: " + mac);
               alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Ignore",
                       new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                   ignoreList.add(mac);
                   connectToTheService();
                 }
               });
               alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Save",
                       new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                   //TODO UPDATE LECTURER BEACON COLUMN
                 }
               });
               alertDialog.show();


              } catch (RemoteException e) {
                e.printStackTrace();

              }
            }
          }
        });
  }

  private void connectToTheService() {
    progressDialog.setMessage("Searching...");
    progressDialog.show();
    alertDialog.hide();
    beaconManager.connect(
        new BeaconManager.ServiceReadyCallback() {
          @Override
          public void onServiceReady() {
            try {
              beaconManager.startRanging(ALL_BEACONS_REGION);
            } catch (RemoteException e) {
              e.printStackTrace();
            }
          }
        });
  }
}
