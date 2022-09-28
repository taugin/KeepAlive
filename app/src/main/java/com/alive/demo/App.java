package com.alive.demo;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.alive.log.Log;
import com.bossy.KeepBossy;

import java.io.File;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        final boolean nonOrganic = isNonOrganic(this);
        Log.v(Log.TAG, "nonOrganic : " + nonOrganic);
        KeepBossy.setOnBossyListener(new KeepBossy.OnBossyListener() {
            @Override
            public boolean allowKeepBossy() {
                return isNonOrganic(getApplicationContext());
            }
        });
        KeepBossy.startBossy(this, "attach");
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
        File bossyFile = getNonOrganicFile(context);
        return bossyFile != null && bossyFile.exists();
    }

    private static File getNonOrganicFile(Context context) {
        File file = new File(context.getFilesDir(), "bossy_start_up");
        return file;
    }

    public static void setNonOrganic(Context context) {
        File bossyFile = getNonOrganicFile(context);
        try {
            if (bossyFile != null) {
                bossyFile.createNewFile();
            }
        } catch (Exception e) {
        }
    }

    public static void removeNonOrganic(Context context) {
        File bossyFile = getNonOrganicFile(context);
        try {
            if (bossyFile != null && bossyFile.exists()) {
                bossyFile.delete();
            }
        } catch (Exception e) {
            Log.e(Log.TAG, "error : " + e);
        }
    }
}
