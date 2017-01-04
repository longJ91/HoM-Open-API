package com.example.songchiyun.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.Time;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.sql.Date;
import java.util.Collection;
import java.util.HashSet;

public class MyService extends Service implements GoogleApiClient.ConnectionCallbacks,
        SensorEventListener,
        GoogleApiClient.OnConnectionFailedListener,
        MessageApi.MessageListener {
    GoogleApiClient gClient;
    float walk_count;
    String node;
    private SensorManager sensorManager;
    Time today;
    Collection<String> nodes;
    boolean first = true;
    boolean connect = false;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("server",String.valueOf(walk_count));

        gClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        gClient.connect();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        registerSensor();
        walk_count = 0;
        today = new Time(Time.getCurrentTimezone());
        today.setToNow();


        Log.d("server","service on");
    }
    public Collection<String> getNodes(){
        HashSet<String> results=new HashSet<String>();
        //연결된 NodeList 를 읽어온다.
        NodeApi.GetConnectedNodesResult nodes=
                Wearable.NodeApi.getConnectedNodes(gClient)
                        .await();
        for(Node node:nodes.getNodes()){
            results.add(node.getId());
        }
        //연결된 node의 아이디 값을 담고 있는 HashSet 객체 리턴해주기
        return results;
    }

    private void registerSensor(){
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Log.d("디버그","Count sensor not available!");
        }
    }
    @Override
    public void onDestroy() {
        Wearable.MessageApi.removeListener(gClient,this);
        gClient.disconnect();
        super.onDestroy();
        connect = false;

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        today.setToNow();
        Log.d("server","start thread2");

        new MyThread().start();
        return START_STICKY;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.MessageApi.addListener(gClient,this);
        Log.d("디버그","google api connect");
        connect = true;

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        String msg = messageEvent.getPath();

        Log.d("service","receive and start");
        sendBroadcast(new Intent("Setting"));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("디버그", "connection fail");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        walk_count ++;
        Log.d("디버그", String.valueOf(event.values[0]));
        Log.d("디버그", String.valueOf(walk_count));
     //   Log.d("디버그",String.valueOf(walk_count));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    class MyThread extends Thread
    {
        float interval;
        public MyThread(){
            Log.d("디버그","thread 시작");
        }
        public void run()
        {
            Log.d("디버그","thread 런");


            while(true) {
                try {

                    Thread.sleep(1800000);
                    //send walk count
                    Log.d("connect",String.valueOf(connect));
                    if(connect) {
                        nodes = getNodes();
                        for (final String node : nodes) {
                            Log.d("디버그", "step/"+walk_count);
                            //콘손에 node id 출력해보기

                            //MessageApi 를 이용해서 전송한다.
                            //(GoogleApiClient, node id, msg, byte[])
                            Wearable.MessageApi
                                    .sendMessage(gClient, node, "step/"+String.valueOf(walk_count), new byte[0])
                                    .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                                        @Override
                                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                            //전송 성공이라면
                                            if (sendMessageResult.getStatus().isSuccess()) {
                                                Log.d("디버그", "send success");
                                            } else {//전송 실패라면
                                                Log.d("디버그", "send fail");
                                            }
                                        }
                                    });
                        }
                        walk_count = 0;
                    }
                    else{
                        gClient.connect();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // thread body of execution
        }
    }
}
