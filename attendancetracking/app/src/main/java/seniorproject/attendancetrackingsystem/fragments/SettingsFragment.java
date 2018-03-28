package seniorproject.attendancetrackingsystem.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.SessionManager;

/* A simple {@link Fragment} subclass. */
public class SettingsFragment extends ListFragment implements AdapterView.OnItemClickListener {

  public SettingsFragment() {}

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_settings, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ArrayAdapter adapter =
        ArrayAdapter.createFromResource(
            getActivity(), R.array.settings_items, android.R.layout.simple_selectable_list_item);
    setListAdapter(adapter);
    getListView().setOnItemClickListener(this);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    if (parent.getItemAtPosition(position).equals("Logout")) {
      SessionManager session = new SessionManager(getActivity().getApplicationContext());
      session.logoutUser();
    } else {
      Toast.makeText(
              getActivity(),
              getListView().getItemAtPosition(position).toString(),
              Toast.LENGTH_SHORT)
          .show();
    }
  }
}
