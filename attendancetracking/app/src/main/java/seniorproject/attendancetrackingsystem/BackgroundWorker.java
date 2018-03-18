package seniorproject.attendancetrackingsystem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;


public class BackgroundWorker extends AsyncTask<String, Void, String> {
    private Context context;
    private AlertDialog alertDialog;
    private String type;

    BackgroundWorker(Context ctx) {
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
    protected void onPreExecute() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Network Connection");
            alertDialog.setMessage("This application requires internet connection!");
            alertDialog.show();
            cancel(true);
        }

    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null && result.contains("Successful")) {
            Intent newIntent;
            switch (type) {
                case "studentLogin":
                case "lecturerLogin":
                    newIntent = new Intent(context, WelcomePage.class);
                    newIntent.putExtra("username", result.substring(11, result.length()));
                    if (type == "studentLogin")
                        newIntent.putExtra("userType", "student");
                    else if (type == "lecturerLogin")
                        newIntent.putExtra("usertype", "lecturer");
                    context.startActivity(newIntent);
                    break;
                case "studentRegister":
                case "lecturerRegister":
                    Toast.makeText(context, "Register Successful", Toast.LENGTH_SHORT);
                    newIntent = new Intent(context, MainActivity.class);
                    newIntent.putExtra("message", "Registration is successful");
                    context.startActivity(newIntent);
                    break;
            }
        } else {
            alertDialog = new AlertDialog.Builder(context).create();
            if (type == "studentLogin" || type == "lecturerLogin")
                alertDialog.setTitle("Login Status");
            else if (type == "studentRegister" || type == "lecturerRegister")
                alertDialog.setTitle("Registration Status");
            alertDialog.setMessage("An error has been occurred while doing your action!");
            alertDialog.show();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
