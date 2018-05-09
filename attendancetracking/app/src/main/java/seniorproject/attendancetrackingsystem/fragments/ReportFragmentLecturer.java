package seniorproject.attendancetrackingsystem.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.DatabaseManager;

/* A simple {@link Fragment} subclass. */
public class ReportFragmentLecturer extends Fragment {
  private ListView listView;
  private ArrayList<Integer> classrooms = new ArrayList<>();
  private Handler handler;
  private ArrayList<StudentRow> studentList = new ArrayList<>();
  private StudentAdapter adapter;
  private Timer timer;

  public ReportFragmentLecturer() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_report_lecturer, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    listView = view.findViewById(R.id.studentlist);
    handler = new Handler(Looper.getMainLooper());
    timer = new Timer();
    Bundle args = getArguments();
    if (args != null) {
      classrooms = args.getIntegerArrayList("classrooms");

      adapter = new StudentAdapter(getActivity(), R.layout.liststudent, studentList);
      listView.setAdapter(adapter);

      studentListListener();
    }
  }

  private void studentListListener() {
    timer.scheduleAtFixedRate(
        new TimerTask() {
          @Override
          public void run() {
            for (int i = 0; i < classrooms.size(); i++) {
              getStudentList(classrooms.get(i));
            }
          }
        },
        0,
        10000); // 2 minutes
  }

  private void toastWithHandler(final String text) {
    handler.post(
        new Runnable() {
          @Override
          public void run() {
            Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_SHORT).show();
          }
        });
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    timer.cancel();
  }

  private void getStudentList(final int classroom_id) {
    StringRequest request =
        new StringRequest(
            Request.Method.POST,
            DatabaseManager.GetOperations,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
                try {
                  JSONObject jsonObject = new JSONObject(response);
                  boolean result = jsonObject.getBoolean("success");
                  if (!result) {
                    toastWithHandler(jsonObject.getString("message"));
                    return;
                  }
                } catch (JSONException e) {
                  // do nothing
                }
                try {
                  JSONArray jsonArray = new JSONArray(response);
                  studentList.clear();
                  for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    StudentRow studentRow =
                        new StudentRow(
                            jsonObject.getString("name") + " " + jsonObject.getString("surname"),
                            jsonObject.getInt("student_number"),
                            jsonObject.getInt("status"),
                            jsonObject.getInt("time"));
                    studentList.add(studentRow);
                  }
                  listView.setAdapter(adapter);
                } catch (JSONException e) {
                  e.printStackTrace();
                }
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {}
            }) {
          @Override
          protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<>();
            params.put("classroom_id", String.valueOf(classroom_id));
            params.put("operation", "attendance-list");
            return params;
          }
        };
    try {
      DatabaseManager.getmInstance(getActivity().getApplicationContext()).execute(request);
    } catch (NullPointerException e) {
      // Do nothing
    }
  }

  public class StudentRow {
    public String name;
    public int number;
    public int state;
    public int time;

    public StudentRow(String name, int number, int state, int time) {
      this.name = name;
      this.number = number;
      this.state = state;
      this.time = time;
    }

    public StudentRow() {
      super();
    }
  }

  public class StudentAdapter extends ArrayAdapter<StudentRow> {
    Context context;
    int layoutResourseId;
    ArrayList<StudentRow> data = new ArrayList<>();

    private StudentAdapter(Context context, int layoutResourseId, ArrayList<StudentRow> data) {
      super(context, layoutResourseId, data);
      this.layoutResourseId = layoutResourseId;
      this.context = context;
      this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
      View row = convertView;
      StudentHolder holder = null;

      if (row == null) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourseId, parent, false);

        holder = new StudentHolder();
        holder.txtName = row.findViewById(R.id.studentName);
        holder.txtNumber = row.findViewById(R.id.studentNo);

        row.setTag(holder);
      } else {
        holder = (StudentHolder) row.getTag();
      }

      StudentRow student = data.get(position);

      String info = student.name + " ["+student.time / 60000+" m]";
      holder.txtName.setText(info);
      holder.txtNumber.setText(String.valueOf(student.number));
      if (student.state == 0) row.setBackgroundColor(getResources().getColor(R.color.stateRed));
      else if (student.state == 1)
        row.setBackgroundColor(getResources().getColor(R.color.stateYellow));
      else if (student.state == 2 || student.state == 3)
        row.setBackgroundColor(getResources().getColor(R.color.stateGreen));

      return row;
    }

    class StudentHolder {
      TextView txtNumber;
      TextView txtName;
    }
  }
}
