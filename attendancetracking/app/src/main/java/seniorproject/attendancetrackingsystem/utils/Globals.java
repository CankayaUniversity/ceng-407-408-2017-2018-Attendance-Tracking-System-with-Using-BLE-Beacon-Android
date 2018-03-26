package seniorproject.attendancetrackingsystem.utils;


import android.app.Application;

import java.util.ArrayList;

public class Globals extends Application {
    private ArrayList<Department> departments;


    public ArrayList<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(ArrayList<Department> departments) {
        this.departments = departments;
    }





}
