package com.alive.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.alive.daemon.demo.R;
import com.bossy.KeepBossy;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openApplicationDetail() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", getPackageName(), (String) null));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.set_non_organic) {
            App.setNonOrganic(this);
            KeepBossy.startBossy(getApplicationContext(), "attach");
        } else if (v.getId() == R.id.remove_non_organic) {
            App.removeNonOrganic(this);
        } else if (v.getId() == R.id.start_app_detail) {
            openApplicationDetail();
        }
    }
}
