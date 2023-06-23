package com.faceb.svr;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.faceb.pro.OFace;
import com.faceb.daemon.JavaDaemon;
import com.faceb.log.Log;
import com.faceb.utils.Utils;

public class DCService extends Service {
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
            JavaDaemon.getInstance().callProvider(this, OFace.getDaemonProcess(this));
        }
        Intent service1Intent = new Intent();
        service1Intent.setClassName(getPackageName(), ACService.class.getName());
        Intent service2Intent = new Intent();
        service2Intent.setClassName(getPackageName(), ABService.class.getName());
        try {
            startService(service1Intent);
        } catch (Exception | Error e) {
            JavaDaemon.getInstance().callProvider(this, OFace.getAssist1Process(this));
        }
        try {
            startService(service2Intent);
        } catch (Exception | Error e) {
            JavaDaemon.getInstance().callProvider(this, OFace.getAssist2Process(this));
        }
    }
}
