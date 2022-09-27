package com.alive.demo;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.bossy.KeepBossy;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        KeepBossy.startBossy(this, "attach");
        KeepBossy.setOnBossyListener(new KeepBossy.OnBossyListener() {
            @Override
            public void onAlive() {
                try {
                    ContextCompat.startForegroundService(getApplicationContext(), new Intent(getApplicationContext(), DemoService.class));
                } catch (Exception e) {
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public boolean isMainProcess() {
        return getApplicationContext().getPackageName().equals(getCurrentProcessName());
    }

    /**
     * 获取当前进程名
     */
    private String getCurrentProcessName() {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (null != manager) {
            for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
                if (process.pid == pid) {
                    processName = process.processName;
                }
            }
        }
        return processName;
    }
}
