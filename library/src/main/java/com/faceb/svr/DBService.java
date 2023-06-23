package com.faceb.svr;

import android.app.Service;
import android.content.Intent;

import com.faceb.pro.OFace;
import com.faceb.daemon.JavaDaemon;

public abstract class DBService extends Service {
    public void onCreate() {
        super.onCreate();
        Intent service1Intent = new Intent();
        service1Intent.setClassName(getPackageName(), ACService.class.getName());
        Intent service2Intent = new Intent();
        service2Intent.setClassName(getPackageName(), ABService.class.getName());
        Intent daemonIntent = new Intent();
        daemonIntent.setClassName(getPackageName(), DCService.class.getName());
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
        try {
            startService(daemonIntent);
        } catch (Exception | Error e) {
            JavaDaemon.getInstance().callProvider(this, OFace.getDaemonProcess(this));
        }
    }
}
