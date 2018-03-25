package seniorproject.attendancetrackingsystem;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Melihşah AKIN on 26.03.2018.
 */

public class JsonHelper {
    private static JsonHelper mInstance;
    private static Context context;

    private JsonHelper(Context context) {
    this.context = context;
    }

    public static synchronized JsonHelper getmInstance(Context context) {
        if (mInstance == null)
            mInstance = new JsonHelper(context);
        return mInstance;
    }


    public ArrayList<Department> parseDepartmentList(String jsonString){
        ArrayList<Department> arrayList = new ArrayList<Department>();
        try{
            JSONArray jsonArray = new JSONArray(jsonString);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Department tempObject = new Department(
                        jsonObject.getInt("department_id"),
                        jsonObject.getString("abbreviation"),
                        jsonObject.getString("department_name")
                );
                arrayList.add(tempObject);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        finally {
            return arrayList;
        }
    }
}
