package seniorproject.attendancetrackingsystem.fragments;

import android.support.v4.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import seniorproject.attendancetrackingsystem.R;

/* A simple {@link Fragment} subclass. */
public class ServicesFragment extends Fragment {
  private static final int BT_REQUEST_CODE = 11202;
  private Switch bluetoothSwitch;
  private BluetoothAdapter bluetoothAdapter;

  @Override

  public View onCreateView(
          LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_services, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initElements(view);
    checkBluetoothAdapter();
  }

  private void initElements(View view) {
    bluetoothSwitch = view.findViewById(R.id.bluetooth_switch);
    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          Intent turnOnBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
          startActivityForResult(turnOnBluetooth, BT_REQUEST_CODE);
        } else {
          bluetoothAdapter.disable();
        }
      }
    });
  }

  void checkBluetoothAdapter() {
    if (bluetoothAdapter == null) {
      Toast.makeText(getActivity(), "Regular mode requires bluetooth adapter", Toast.LENGTH_LONG).show();
    } else if (bluetoothAdapter.isEnabled()) {
      bluetoothSwitch.setChecked(true);
    } else
      bluetoothSwitch.setChecked(false);
  }
}
