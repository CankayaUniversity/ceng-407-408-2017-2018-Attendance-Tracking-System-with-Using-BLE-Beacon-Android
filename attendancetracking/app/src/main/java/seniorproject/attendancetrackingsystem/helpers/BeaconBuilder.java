package seniorproject.attendancetrackingsystem.helpers;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.aprilbrother.aprilbrothersdk.Beacon;
import com.aprilbrother.aprilbrothersdk.BeaconManager;
import com.aprilbrother.aprilbrothersdk.Region;
import com.aprilbrother.aprilbrothersdk.utils.AprilL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeaconBuilder {
  private static final Region ALL_BEACONS_REGION = new Region("REGION01", null, null, null);
  private ProgressDialog progressDialog;
  private Context context;
  private BeaconManager beaconManager;
  private ArrayList<Beacon> beacons;

  public BeaconBuilder(Context context) {
    this.context = context;
    progressDialog = new ProgressDialog(context);
    progressDialog.setTitle("Beacon syncronizer");
    progressDialog.setMessage("GOING");
    progressDialog.show();
    initialize();
    connectToTheService();
  }

  private void initialize() {
    BluetoothAdapter.getDefaultAdapter().enable();
    beacons = new ArrayList<>();
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

            if (list.size() > 0) {
              try {
                progressDialog.setMessage("Beacon: " + beacons.get(0).getMacAddress());
                //TODO CONFIRMATION DIALOG WILL BE CALLED
                beaconManager.stopRanging(ALL_BEACONS_REGION);
                Map<String, String> postParameter = new HashMap<>();
                postParameter.put("beacon", beacons.get(0).getMacAddress());
                Log.d("BEACON", "Beacon: "+ beacons.get(0).getMacAddress());
                //TODO UPDATE LECTURER BEACON COLUMN
              } catch (RemoteException e) {
                e.printStackTrace();

              }
            }
          }
        });
  }

  private void connectToTheService() {
    progressDialog.setMessage("Searching...");

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
