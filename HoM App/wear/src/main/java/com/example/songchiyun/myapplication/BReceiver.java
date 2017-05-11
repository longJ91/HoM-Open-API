package com.example.songchiyun.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by songchiyun on 16. 9. 2..
 */
public class BReceiver  extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("service","receive in receiver");

    }
}
