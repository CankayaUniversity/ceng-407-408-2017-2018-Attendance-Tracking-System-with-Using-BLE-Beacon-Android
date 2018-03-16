package seniorproject.attendancetrackingsystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;



public class DataManager {
    private String type, targetURL;
    private ArrayList<String> parameters, values;
    private HttpURLConnection httpURLConnection;
    DataManager(String... params) {
        type = params[0];
        // Setting page URL according to type
        if(type == "studentLogin" || type == "lecturerLogin")
            targetURL = "http://attendancesystem.xyz/attendancetracking/login.php";

       try {
           URL url = new URL(targetURL);
           httpURLConnection =(HttpURLConnection)url.openConnection();
       }catch(MalformedURLException e){
           e.printStackTrace();
       }catch (IOException e){
           e.printStackTrace();
       }
        //Initiate and set arraylists
        parameters = new ArrayList<String>();
        values = new ArrayList<String>();
        try {
            for (int i = 1; i < params.length; i += 2) {
                parameters.add(params[i]);
                values.add(params[i + 1]);
            }
        }catch(IndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }
    public void sendData(String method){
        try {
            httpURLConnection.setRequestMethod(method);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(outputStream,"UTF-8"));
            bufferedWriter.write(dataGenerator());
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public String getResult(){
        try {
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, "iso-8859-1"));
            String result = "";
            String line = "";
            while((line = bufferedReader.readLine()) != null){
                result += line + "\n";
            }
            bufferedReader.close();
            inputStream.close();
            return result;

        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
    public void disconnect(){
        httpURLConnection.disconnect();
    }
    private String dataGenerator() {
        String data = "";
        try {
            for (int i = 0; i < parameters.size(); i ++) {
                data += URLEncoder.encode(parameters.get(i), "UTF-8") + "="
                        + URLEncoder.encode(values.get(i), "UTF-8") + "&";
            }
            data += URLEncoder.encode("type","UTF-8") + "="
                    + URLEncoder.encode(type,"UTF-8");
        }catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
