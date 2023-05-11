package com.alive.demo;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.alive.log.Log;
import com.finebot.KeepAlive;

import java.io.File;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        final boolean nonOrganic = isNonOrganic(this);
        Log.v(Log.TAG, "nonOrganic : " + nonOrganic);
        KeepAlive.setOnAliveListener(new KeepAlive.OnAliveListener() {
            @Override
            public boolean allowKeepAlive() {
                return isNonOrganic(getApplicationContext());
            }
        });
        KeepAlive.startKeepAlive(this, "attach");
        ContextCompat.startForegroundService(this, new Intent(this, DemoService.class));
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

    public static boolean isNonOrganic(Context context) {
        File rabbitFile = getNonOrganicFile(context);
        return rabbitFile != null && rabbitFile.exists();
    }

    private static File getNonOrganicFile(Context context) {
        File file = new File(context.getFilesDir(), "rabbit_start_up");
        return file;
    }

    public static void setNonOrganic(Context context) {
        File rabbitFile = getNonOrganicFile(context);
        try {
            if (rabbitFile != null) {
                rabbitFile.createNewFile();
            }
        } catch (Exception e) {
        }
    }

    public static void removeNonOrganic(Context context) {
        File rabbitFile = getNonOrganicFile(context);
        try {
            if (rabbitFile != null && rabbitFile.exists()) {
                rabbitFile.delete();
            }
        } catch (Exception e) {
            Log.e(Log.TAG, "error : " + e);
        }
    }
}
