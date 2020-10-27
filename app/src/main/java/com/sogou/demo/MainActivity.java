package com.sogou.demo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sogou.bgstart.BgStart;
import com.sogou.bgstart.ScreenActivity;
import com.sogou.daemon.demo.R;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        BgStart.onStartMainActivity(this);
        setContentView(R.layout.activity_main);
        ContextCompat.startForegroundService(this, new Intent(this, NotifyResidentService.class));
    }

    public void onClick(View v) {
        startActivity(new Intent(this, ScreenActivity.class));
    }

    @Override
    public void onBackPressed() {
        if (BgStart.onBackPressed(this)) {
            return;
        }
        super.onBackPressed();
    }
}
