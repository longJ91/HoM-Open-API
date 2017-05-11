package com.example.songchiyun.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.channels.FileChannel;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by songchiyun on 16. 5. 28..
 */
public class BackgroundService extends Service implements LocationListener {
    Location tempLoc;
    //  map이 켜져있을때 -> timer cancel 후 intervel 재설정/ 안켜져있을때 구별해서 서비스 돌려줘야함
    boolean useArdu = false;
    int rand = 0;
    String pid = "";
    String id = "";
    private SharedPreferences preferences;
    Timer timer = null;
    HTTPClient client;
    private JSONObject locationJson;
    String heartRate = "";
    String heartRateA = "";
    Location myLocation;
    private int DISTANCE_CHANGE_FOR_UPDATES = 5;
    private int MIN_TIME_BW_UPDATES = 5;
    XmlWriter writer;
    boolean receiveSensorA = false;
    boolean receiveSensor;
    String stepCount;
    Receiver receiver;
    boolean breakA = false;
    final int UPDATE_INTERVAL = 10000;
    DbAdapter dbAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        //   dbAdapter = new DbAdapter(this);
        receiveSensor = false;
        IntentFilter mainFilter = new IntentFilter("Setting");

        receiver = new Receiver();
        registerReceiver(receiver, mainFilter);
        Log.d("check", "Service Start");
        init();


    }

    private void init() {

        myLocation = new Location("myLocation");
        myLocation = getLocation();
        //   Log.d("get",myLocation.toString())
        locationJson = new JSONObject();
        client = new HTTPClient();
        writer = new XmlWriter();
        preferences = getSharedPreferences(PreferencePutter.PREF_FILE_NAME, Activity.MODE_PRIVATE);
        pid = preferences.getString(PreferencePutter.PREF_ID, "null");
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("check", "start onStratCommand");

        operating();

        return START_STICKY;
    }

    private void operating() {
        Log.d("check", "operating");
        tempLoc = new Location("temp");
        if (timer == null)
            timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                Log.d("check", "operating");
                boolean checkOnline = true;
                if (myLocation != null)
                    tempLoc = myLocation;
                myLocation = getLocation();
                Log.d("check", "location :" + myLocation);


                if (myLocation != null) {
                    myLocation = getLocation();

                }
                if (myLocation != null && receiveSensor) {
                        Log.d("sensor", "send for http");
                        receiveSensor = false;
                        client.setDoc(writer.getXmlForData(pid, myLocation, heartRate));
                        new Thread_network().start();
                }
                if(receiveSensorA){
                    receiveSensorA = false;
                    new Thread_network3().start();
                }
            }
        }, 0, UPDATE_INTERVAL);
    }

    public Location getLocation() {
        Location location = null;
        boolean isGPSEnabled, isNetworkEnabled;
        LocationManager locationManager;
        Context mContext = this;

        try {
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isGPSEnabled == false && isNetworkEnabled == false) {
                Log.d("check", "network & gps check");
            } else {
                if (isNetworkEnabled) {
                    Log.d("check", "network enable");
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    }
                }

                if (isGPSEnabled) {
                    Log.d("check", "gps enable");
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
//        Log.d("check","location check: "+location.getLatitude()+"/"+location.getLongitude());
        return location;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("check", "check destroy");
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            Log.e("MAIN-DESTROY>>>", e.getMessage());
        }
        timer.cancel();

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderDisabled(String provider) { // needed by Interface. Not used
    }

    @Override
    public void onProviderEnabled(String provider) { // needed by Interface. Not used
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { // needed by Interface. Not used
    }

    //bpm과 arduino 에서 받아온 data를 가공해주는 receiver
    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle data = intent.getExtras();
            String type = data.getString("Type");
            if(type.equals("bpm")) {
                receiveSensor = true;
                Log.d("check", heartRate);
                if (myLocation != null) {
                    heartRate = data.getString("HeartRate");

                }
            }
            else if(type.equals("step")){
                stepCount = data.getString("StepCount");
                Log.d("디버그","send sstep :"+stepCount);
                new Thread_network2().start();
            }else if(type.equals("ppg")){
                receiveSensorA = true;
                Log.d("check", heartRateA);
                if (myLocation != null) {
                    String temp = "";
                    temp = data.getString("arduino");
                    if (temp.equals("disconnect")) {
                        breakA = true;

                    } else {
                        receiveSensorA = true;
                        if (heartRateA.equals("")) {
                            heartRateA = temp;
                        } else {
                            if (((Integer.parseInt(heartRateA) - Integer.parseInt(temp)) *
                                    ((Integer.parseInt(heartRateA) - Integer.parseInt(temp))) > (10 * 10))) {
                                int t = Integer.parseInt(heartRateA);
                                if (rand == 0) {
                                    t += 4;
                                    heartRateA = t + "";
                                    rand++;
                                } else if (rand == 1) {
                                    t -= 5;
                                    heartRateA = t + "";
                                    rand++;
                                } else {
                                    rand = 0;
                                }
                            } else {
                                heartRateA = temp;
                            }

                        }
                    }
                }
            }
        }
    }
    class Thread_network extends Thread{
        @Override
        public void run() {
            client.sendSensor(pid);
        }
    }
    class Thread_network2 extends Thread{
        @Override
        public void run() {
            client.sendStep(pid,stepCount);
        }
    }
    class Thread_network3 extends Thread{
        @Override
        public void run() {

            client.sendPPG(pid, heartRateA);
            Log.d("debug", heartRateA + " from arduino");

        }

    }

}