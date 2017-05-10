package com.example.songchiyun.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

//login후 Hom web화면을 보여주는 activity
public class Web extends AppCompatActivity {
    WebView browser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        startService(new Intent(this, BackgroundService.class));
        browser = (WebView) findViewById(R.id.webkit);
        browser.getSettings().setJavaScriptEnabled(true); // allow scripts browser.setWebViewClient(new WebViewClient() );
        browser.loadUrl("http://www.homcare.xyz/wordpress");
        browser.setWebViewClient( new WebViewClient() );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }// onCreateOptionsMenu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String option = item.getTitle().toString();
        if (option.equals("register")) {
            startActivity(new Intent(this, LoginActivity.class));
        }else if(option.equals("bluetooth")){
            startActivity(new Intent(this, BLmode.class));

        }
         return true;
    }//onOptionsItemSelected


}
