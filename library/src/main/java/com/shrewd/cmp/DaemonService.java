package com.shrewd.cmp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.shrewd.daemon.JavaDaemon;
import com.shrewd.log.Log;
import com.shrewd.utils.Utils;

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
            Log.iv(Log.TAG, "error : " + e);
            JavaDaemon.getInstance().callProvider(this, DaemonMain.getDaemonProcess(this));
        }
        Intent service1Intent = new Intent();
        service1Intent.setClassName(getPackageName(), AService1.class.getName());
        Intent service2Intent = new Intent();
        service2Intent.setClassName(getPackageName(), AService2.class.getName());
        try {
            startService(service1Intent);
        } catch (Exception | Error e) {
            JavaDaemon.getInstance().callProvider(this, DaemonMain.getAssist1Process(this));
        }
        try {
            startService(service2Intent);
        } catch (Exception | Error e) {
            JavaDaemon.getInstance().callProvider(this, DaemonMain.getAssist2Process(this));
        }
    }
}
