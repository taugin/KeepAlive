package com.sogou.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bossy.KeepAlive;
import com.sogou.bgstart.BgStart;
import com.sogou.bgstart.ScreenActivity;
import com.sogou.daemon.demo.R;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BgStart.onStartMainActivity(this);
        setContentView(R.layout.activity_main);
        KeepAlive.startKeepAlive(this);
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
