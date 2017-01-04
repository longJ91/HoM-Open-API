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
 * Created by songchiyun on 16. 7. 4..
 */
public class HTTPClient {

    JSONObject data;
    String result = "";
    String lat,lng;
    public HTTPClient(){
        data = new JSONObject();
        Log.d("err","http connect");
    }
    public String getResult(){
        return result;
    }
    public void insertToDatabase(String name, String address) {
        Log.d("http","http");
        lat = name;
        lng = address;

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
                    String name = (String) params[0];
                    String address = (String) params[1];

                    String link = "http://52.78.19.78/insert.php";
                    String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
                    data += "&" + URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(address, "UTF-8");

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
        task.execute(name, address);
    }
}
