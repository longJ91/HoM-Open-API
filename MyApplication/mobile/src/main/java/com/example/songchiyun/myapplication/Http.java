package com.example.songchiyun.myapplication;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by chiyo on 2016-08-08.
 */
public class Http {

    JSONObject data;
    String result = "";
    String mLat,mLng;
    String heartRate;
    public Http(){
        data = new JSONObject();
        mLat = "";
        mLng = "";
        heartRate = "";
        Log.d("http","http connect");
    }
    public String getResult(){
        return result;
    }
    public void insertToDatabase(String rate, String lat, String lng) {
        Log.d("http","http");
        this.heartRate = rate;
        this.mLat = lat;
        this.mLng = lng;

        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                result = s;
                Log.d("http",result);
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String id = "a2c222";
                    String hearRate = (String) params[0];
                    String lat = (String)params[1];
                    String lng = (String)params[2];

                    String link = "http://52.78.19.78/insert.php";
                    String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
                    data += "&" + URLEncoder.encode("hearRate", "UTF-8") + "=" + URLEncoder.encode(hearRate, "UTF-8");
                    data += "&" + URLEncoder.encode("Lat", "UTF-8") + "=" + URLEncoder.encode(lat, "UTF-8");
                    data += "&" + URLEncoder.encode("Lng", "UTF-8") + "=" + URLEncoder.encode(lng, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    Log.d("http send",data);
                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        Log.d("http get",line);
                        break;
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }

            }
        }

        InsertData task = new InsertData();
        task.execute(heartRate, mLat, mLng);
    }
}
