package com.bossy.component;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.bossy.utils.Utils;
import com.bossy.log.Log;

public class DaemonService extends Service {
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        try {
            Intent intent = new Intent();
            String className = Utils.getString(this, Service.class.getName() + "_Name");
            Log.v(Log.TAG, "foreground service name : " + className);
            intent.setClassName(getPackageName(), className);
            if (Build.VERSION.SDK_INT >= 26) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        } catch (Exception e) {
        }
        Intent intent2 = new Intent();
        intent2.setClassName(getPackageName(), AssistService1.class.getName());
        Intent intent3 = new Intent();
        intent3.setClassName(getPackageName(), AssistService2.class.getName());
        startService(intent2);
        startService(intent3);
    }
}
