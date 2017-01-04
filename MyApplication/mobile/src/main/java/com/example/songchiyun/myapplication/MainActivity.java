package com.example.songchiyun.myapplication;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;


public class MainActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        MessageApi.MessageListener{
    //필요한 맴버필드 정의하기
    EditText console, inputText;

    //Phone 과 Wear 의 통신을 도와주는 클래스
    GoogleApiClient gClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        marshmallowGPSPremissionCheck();
        startService(new Intent(this, BackgroundService.class));
        //필요한 객체의 참조값 얻어오기
        console=(EditText)findViewById(R.id.console);
        inputText=(EditText)findViewById(R.id.input);
        //GoogleApiClient 객체의 참조값 얻어오기
        gClient=new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.BODY_SENSORS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.BODY_SENSORS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.BODY_SENSORS},
                        2);
                Log.d("check","request");
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        }
    }
    private void marshmallowGPSPremissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && this.checkSelfPermission(
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && this.checkSelfPermission(
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                   2);
        } else {
            //   gps functions.
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        //GoogleApiClient 에 연결 요청하기
        gClient.connect();
    }

    @Override
    protected void onStop() {
        //GoogleApiClient 에 연결 해제하기
        gClient.disconnect();
        super.onStop();
    }

    //전송버튼 눌렀을때 호출되는 메소드
    public void send(View v){
        //입력한 문자열을 읽어온다
        String msg=inputText.getText().toString();
        if(msg.equals(""))return;
        //비동기 작업에 전달할 파라미터를 배열에 담는다.
        String[] params=new String[1];
        params[0]=msg;
        //파라미터를 전달하면서 비동기 작업 시작 시키기
        new SendMessageTask().execute(params);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
     //   getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    //GoogleApiClient 에 연결이 되었을 때 호출되는 메소드
    @Override
    public void onConnected(Bundle bundle) {
        printLoc("onConnected()");
    }

    //GoogleApiClient 에 연결이 연기 되었을 때 호출되는 메소드
    @Override
    public void onConnectionSuspended(int i) {
        printLoc("onConnectionSuspended()");

    }

    //GoogleApiClient 에 연결이 실패 되었을 때 호출되는 메소드
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        printLoc("onConnectionFailed()");
    }

    //로그를 출력하는 메소드
    public void printLoc(String msg){
        //출력할 문자열을 메세지 객체에 담는다.
        Message m=new Message();
        m.obj=msg;
        //핸들러에 메세지를 보낸다.
        handler.sendMessage(m);

    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //전달된 문자열을 읽어온다.
            String str=(String)msg.obj;
            //EditText 에 개행기호와 함께 출력하기
            console.append(str+"\n");
        }
    };

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
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("check","get message");
    }

    //비동기 작업 객체를 생성할 클래스
    public class SendMessageTask extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... params) {
            //전송할 문자열을 읽어온다.
            String msg=params[0];
            //연결된 node id 목록을 얻어온다.
            Collection<String> nodes=getNodes();
            //반복문 돌면서 모든 node 에 전송한다.
            for(final String node: nodes){
                //콘손에 node id 출력해보기
                printLoc(node);
                //MessageApi 를 이용해서 전송한다.
                //(GoogleApiClient, node id, msg, byte[])
                Wearable.MessageApi
                        .sendMessage(gClient,node,msg,new byte[0])
                        .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                            @Override
                            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                //전송 성공이라면
                                if(sendMessageResult.getStatus().isSuccess()){
                                    printLoc("전송 성공");
                                }else{//전송 실패라면
                                    printLoc("전송 실패");
                                }
                            }
                        });
            }
            return null;
        }
    }
}
