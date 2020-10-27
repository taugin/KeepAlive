package com.sogou.demo;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.bossy.KeepAlive;
import com.sogou.bgstart.BgStart;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (isMainProcess()) {
            BgStart.init(this);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        KeepAlive.attachBaseContext(base, NotifyResidentService.class);
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
