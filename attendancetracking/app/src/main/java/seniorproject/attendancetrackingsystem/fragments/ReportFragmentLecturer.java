package seniorproject.attendancetrackingsystem.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;

import seniorproject.attendancetrackingsystem.R;

/* A simple {@link Fragment} subclass. */
public class ReportFragmentLecturer extends Fragment {

  public ReportFragmentLecturer() {
    // Required empty public constructor
  }
  ListView listView;


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

    //TODO get from database with the current classroom
    StudentRow student_data[] = new StudentRow[]{
            new StudentRow("Melihşah Akın",201411202, 2),
            new StudentRow("Ensar Elmas",201411545, 2),
            new StudentRow("N. Cem Altunbulduk",201411341, 2),
            new StudentRow("Merve Şanlı",201411231, 1),
            new StudentRow("D. Mertcan Kökcür", 201411036, 1),
            new StudentRow("Bahar Şengez", 201511374, 0),
            new StudentRow("Berkan Gürel", 201611672,0),
            new StudentRow("Buğra Gülay",201411909, 0),
            new StudentRow("Koray Çıbık", 201611677, 0)
    };


    StudentAdapter adapter = new StudentAdapter(getActivity(), R.layout.liststudent,student_data);
    listView.setAdapter(adapter);

  }

  public class StudentRow {
    public String name;
    public int number;
    public int state;

    public StudentRow(String name, int number, int state){
      this.name = name;
      this.number = number;
      this.state = state;
    }
    public StudentRow(){super();}
  }
  public class StudentAdapter extends ArrayAdapter<StudentRow>{
    Context context;
    int layoutResourseId;
    StudentRow data[] = null;

    public StudentAdapter(Context context, int layoutResourseId, StudentRow[] data){
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

      if(row == null){
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        row = inflater.inflate(layoutResourseId, parent, false);

        holder = new StudentHolder();
        holder.txtName = (TextView)row.findViewById(R.id.studentName);
        holder.txtNumber = (TextView)row.findViewById(R.id.studentNo);

        row.setTag(holder);
      }else
      {
        holder = (StudentHolder)row.getTag();
      }

      StudentRow student = data[position];
      holder.txtName.setText(student.name);
      holder.txtNumber.setText(String.valueOf(student.number));
      if(student.state == 0) row.setBackgroundColor(getResources().getColor(R.color.stateRed));
      else if(student.state == 1) row.setBackgroundColor(getResources().getColor(R.color.stateYellow));
      else if(student.state == 2) row.setBackgroundColor(getResources().getColor(R.color.stateGreen));

      return row;
    }
   class StudentHolder{
      TextView txtNumber;
      TextView txtName;
  }
  }
}
