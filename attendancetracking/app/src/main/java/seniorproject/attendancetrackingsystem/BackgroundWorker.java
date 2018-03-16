package seniorproject.attendancetrackingsystem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;


public class BackgroundWorker extends AsyncTask<String,Void,String>{
    Context context;
    AlertDialog alertDialog;
    String type;
    BackgroundWorker(Context ctx){
        context = ctx;
    }
    @Override
    protected String doInBackground(String... params) {
        type = params[0];

        DataManager DM = new DataManager(params);
        DM.sendData("POST");
        String result = DM.getResult();
        DM.disconnect();

        return result;
    }

    @Override
    protected void onPreExecute(){

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if(activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Network Connection");
            alertDialog.setMessage("This application requires internet connection!");
            alertDialog.show();
            cancel(true);
        }

    }

    @Override
    protected void onPostExecute(String result) {


        if(result.contains("Successful")) {
            Intent newIntent = new Intent(context,WelcomePage.class);
            newIntent.putExtra("type", type);
            if(type.equals("studentLogin") || type.equals("lecturerLogin"))
                newIntent.putExtra("username",result.substring(11,result.length()));
            context.startActivity(newIntent);

        }
        else {
            alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Login Status");
            alertDialog.setMessage(result);
            alertDialog.show();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
