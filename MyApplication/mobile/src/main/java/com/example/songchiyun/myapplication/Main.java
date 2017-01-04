package com.example.songchiyun.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

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

public class
Main extends Activity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        MessageApi.MessageListener {

    ImageButton send;
    ImageButton stop;
    GoogleApiClient gClient;
    Intent service;
    boolean flag = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        send = (ImageButton)findViewById(R.id.send);
        service = new Intent(this, BackgroundService.class);
        gClient=new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("check", "send");
                String msg = "send";
                if (msg.equals("")) return;
                //비동기 작업에 전달할 파라미터를 배열에 담는다.
                String[] params = new String[1];
                params[0] = msg;
                //파라미터를 전달하면서 비동기 작업 시작 시키기
                if(flag) {
                    send.setBackground(getDrawable(R.drawable.cast_ic_expanded_controller_pause));
                    startService(service);
                    new SendMessageTask().execute(params);
                    flag = false;
                }
                else{
                    send.setBackground(getDrawable(R.drawable.cast_ic_expanded_controller_play));
                    stopService(service);
                    new SendMessageTask().execute(params);
                    flag = true;
                }
            }
        });
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
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



    public class SendMessageTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //전송할 문자열을 읽어온다.
            String msg=params[0];
            //연결된 node id 목록을 얻어온다.
            Collection<String> nodes=getNodes();
            //반복문 돌면서 모든 node 에 전송한다.
            if(nodes.isEmpty()){
                Log.d("check","empty wear");
            }
            for(final String node: nodes){
                //콘손에 node id 출력해보기
                //MessageApi 를 이용해서 전송한다.
                //(GoogleApiClient, node id, msg, byte[])
                Log.d("node",node.toString());
                Wearable.MessageApi
                        .sendMessage(gClient,node,msg,new byte[0])
                        .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                            @Override
                            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                //전송 성공이라면
                                if(sendMessageResult.getStatus().isSuccess()){
                                    Log.d("check","전송 성공");
                                }else{//전송 실패라면
                                    Log.d("check","전송 실패");
                                }
                            }
                        });
            }
            return null;
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
