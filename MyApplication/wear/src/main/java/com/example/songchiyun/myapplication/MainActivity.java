package com.example.songchiyun.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Element;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.server.response.FastJsonResponse;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.SensorsApi;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        SensorEventListener,
        GoogleApiClient.OnConnectionFailedListener,
        MessageApi.MessageListener{
    private SensorManager mSensorManager;
    private Sensor mHeartRateSensor;
    GoogleApiClient gClient;
    boolean flag = false;
    EditText console;
    HTTPClient httpClient;
    String nodeId;
    Button btnStart, btnPause;
    TextView mTextView;
    Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Sensor Status:", "zzz");
        i = new Intent(this, MyService.class);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        startService(new Intent(this, MyService.class));
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.heartRateText);
                btnStart = (Button) stub.findViewById(R.id.btnStart);
                btnPause = (Button) stub.findViewById(R.id.btnPause);

                btnStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnStart.setVisibility(ImageButton.GONE);
                        btnPause.setVisibility(ImageButton.VISIBLE);
                        mTextView.setText("Please wait...");
                        startMeasure();
                    }
                });

                btnPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnPause.setVisibility(ImageButton.GONE);
                        btnStart.setVisibility(ImageButton.VISIBLE);
                        mTextView.setText("--");
                        stopMeasure();
                    }
                });


            }
        });
        gClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        checkGooglePlayService(this);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.BODY_SENSORS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.BODY_SENSORS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BODY_SENSORS},
                       2);
            Log.d("check","request");
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        }



        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        if(mHeartRateSensor == null)
            Log.d("check","heart null");

    }
    private void startMeasure() {
        boolean sensorRegistered = mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_FASTEST);
        Log.d("Sensor Status:", " Sensor registered: " + (sensorRegistered ? "yes" : "no"));
    }
    private void stopMeasure() {
        mSensorManager.unregisterListener(this);
        mTextView.setText("--");

    }
    @Override
    protected void onResume(){
        super.onResume();



        gClient.connect();
        //Print all the sensors
        if (mHeartRateSensor != null) {
            List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
            for (Sensor sensor1 : sensors) {
                Log.i("Sensor list", sensor1.getName() + ": " + sensor1.getType());
            }
        }
        else
            Log.d("s","no");

    }
    @Override
    protected void onStop(){
        Wearable.MessageApi.removeListener(gClient,this);
        gClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        printLog("onConnected()");
        Wearable.MessageApi.addListener(gClient,this);
    }
    public void printLog(String msg){
        Message m = new Message();
        m.obj = msg;
        handler.sendMessage(m);
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            String str = (String)msg.obj;


        }
    };
    @Override
    public void onConnectionSuspended(int i) {
        printLog("onConnectionSuspend()");
    }
    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        printLog("onConnectionFailed");
        printLog(String.valueOf(connectionResult.API_UNAVAILABLE));


        //printLog(connectionResult.toString());
    }

    //연결된 Node List 를 리턴하는 메소드
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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float mHeartRateFloat = sensorEvent.values[0];

        int mHeartRate = Math.round(mHeartRateFloat);
        Log.d("디버그","heart :"+mHeartRate);
        mTextView.setText("measuring :"+String.valueOf(mHeartRate));
       new SendMessageTask().execute(String.valueOf(mHeartRate));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public class SendMessageTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //전송할 문자열을 읽어온다.
            String msg=params[0];
            Log.d("디버그",msg);
            //연결된 node id 목록을 얻어온다.
            Collection<String> nodes= getNodes();
            //반복문 돌면서 모든 node 에 전송한다.
            for(final String node: nodes){
                //콘손에 node id 출력해보기
                Log.d("디버그",node);
                printLog(node);
                //MessageApi 를 이용해서 전송한다.
                //(GoogleApiClient, node id, msg, byte[])
                Wearable.MessageApi
                        .sendMessage(gClient,node,"bpm/"+msg,new byte[0])
                        .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                            @Override
                            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                //전송 성공이라면
                                if(sendMessageResult.getStatus().isSuccess()){
                                    Log.d("디버그","성공");
                                }else{//전송 실패라면
                                    Log.d("디버그","실패");
                                }
                            }
                        });
            }
            return null;
        }
    }
    static public boolean checkGooglePlayService(Activity activity) {
        Integer resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode == ConnectionResult.SUCCESS) {
            return true;
        }
        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, activity, 0);
        if (dialog != null) {
            dialog.show();
        }
        return false;
    }
    @Override
    protected void onPause() {

        stopMeasure();
        super.onPause();
    }

}
