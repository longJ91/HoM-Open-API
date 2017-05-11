package com.example.songchiyun.myapplication;

/**
 * Created by chiyo on 2016-08-08.
 */

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

//wearable에서 전송되는 데이터를 받아서 가공처리하는 service class
public class WearListCallListenerService extends WearableListenerService {

    public static String SERVICE_CALLED_WEAR = "WearListClicked";


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        String event = messageEvent.getPath();

        Log.d("Listclicked", messageEvent.toString());

        String[] message = event.split("/");
        Log.d("Listclicked", message[0]);
        if(message[0].equals("bpm")) {
            Intent intent = new Intent("Setting");
            Bundle b = new Bundle();
            b.putString("Type", "bpm");
            b.putString("HeartRate", message[1]);
            intent.putExtras(b);
            sendBroadcast(intent);
            Log.d("디버그", "get bpm :"+message[1]);
        }
        else if(message[0].equals("step")){
            Intent intent = new Intent("Setting");
            Bundle b = new Bundle();
            b.putString("Type", "step");
            b.putString("StepCount", message[1]);
            intent.putExtras(b);
            sendBroadcast(intent);
            Log.d("디버그", "get step :"+message[1]);
        }


    }
}
