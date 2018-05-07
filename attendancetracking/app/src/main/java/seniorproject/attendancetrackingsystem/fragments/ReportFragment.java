package seniorproject.attendancetrackingsystem.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.helpers.DatabaseManager;
import seniorproject.attendancetrackingsystem.helpers.JsonHelper;
import seniorproject.attendancetrackingsystem.helpers.SessionManager;
import seniorproject.attendancetrackingsystem.utils.Actor;
import seniorproject.attendancetrackingsystem.utils.Course;
import seniorproject.attendancetrackingsystem.utils.Globals;
import seniorproject.attendancetrackingsystem.utils.Student;
import seniorproject.attendancetrackingsystem.utils.TakenCourses;

/* A simple {@link Fragment} subclass. */
public class ReportFragment extends Fragment {

  private Spinner studentlecturelist;
  private ArrayList<String> studentlecture;

  public ReportFragment() {
    // Required empty public constructor

  }



  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_report, container, false);
  }
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initElements(view);
  }

  private void initElements(View view) {
    studentlecturelist =view.findViewById(R.id.studentlecturelist);
    Actor loggedUser = ((Globals)getActivity().getApplicationContext()).getLoggedUser();
    ArrayList<TakenCourses> list = null;
    if( ((Student)loggedUser).getTakenCourses()!=null) list =  ((Student)loggedUser).getTakenCourses();
    if(list == null){
      Map<String,String> userInfo = new SessionManager(getActivity().getApplicationContext()).getUserDetails();
      Map<String,String> param = new HashMap<>();
      param.put("user_id", userInfo.get(SessionManager.KEY_USER_ID));

      DatabaseManager.getmInstance(getActivity().getApplicationContext()).execute("get-taken-courses", param);
      list = ((Student)loggedUser).getTakenCourses();
    }

    ArrayList<String> courseCodes = getCourseList(list);
    for(String x : courseCodes) Log.d("COURSE CIDE", x);
  }

  private ArrayList<String> getCourseList( final ArrayList<TakenCourses> list){
    final ArrayList<String>  takenCourseStrings = new ArrayList<>();
    if(((Globals) getActivity().getApplicationContext()).getCourses() != null){
      for(Course x : ((Globals)getActivity().getApplicationContext()).getCourses()){
        for(TakenCourses y : list){
          if(x.getCourseId() == y.getCourse_id()) takenCourseStrings.add(x.getCourseCode());
        }
      }
    return takenCourseStrings;
    }
    StringRequest request = new StringRequest(Request.Method.POST, DatabaseManager.GetOperations, new Response.Listener<String>() {
      @Override
      public void onResponse(String response) {
        JsonHelper jsonHelper = JsonHelper.getmInstance(getActivity().getApplicationContext());
        ((Globals) getActivity().getApplicationContext())
                .setCourses(jsonHelper.parseCourseList(response));
        for (Course course :
                ((Globals) getActivity().getApplicationContext()).getCourses()) {
          for(TakenCourses y : list){
            if(course.getCourseId() == y.getCourse_id()) takenCourseStrings.add(course.getCourseCode());
          }
        }

      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {

      }
    }){
      @Override
      protected Map<String, String> getParams(){
        Map<String, String> postParameters = new HashMap<>();
        postParameters.put("operation", "course-list");
        return postParameters;
      }
    };
    DatabaseManager.getmInstance(getActivity().getApplicationContext()).execute(request);
    return takenCourseStrings;
  }

}
