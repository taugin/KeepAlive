package com.shrewd.cmp;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.shrewd.utils.Utils;
import com.shrewd.log.Log;

public class DaemonService extends Service {
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        try {
            Intent intent = new Intent();
            String className = Utils.getString(this, Service.class.getName() + "_Name");
            Log.iv(Log.TAG, "foreground service name : " + className);
            intent.setClassName(getPackageName(), className);
            Utils.startService(this, intent);
        } catch (Exception e) {
            Log.e(Log.TAG, "error : " + e);
        }
        Intent intent2 = new Intent();
        intent2.setClassName(getPackageName(), AService1.class.getName());
        Intent intent3 = new Intent();
        intent3.setClassName(getPackageName(), AService2.class.getName());
        startService(intent2);
        startService(intent3);
    }
}
