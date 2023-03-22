package com.lioned.cmp;

import android.app.Service;
import android.content.Intent;

import com.lioned.daemon.JavaDaemon;

public abstract class DaemonBaseService extends Service {
    public void onCreate() {
        super.onCreate();
        Intent service1Intent = new Intent();
        service1Intent.setClassName(getPackageName(), A1Service.class.getName());
        Intent service2Intent = new Intent();
        service2Intent.setClassName(getPackageName(), A2Service.class.getName());
        Intent daemonIntent = new Intent();
        daemonIntent.setClassName(getPackageName(), DaemonService.class.getName());
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
        try {
            startService(daemonIntent);
        } catch (Exception | Error e) {
            JavaDaemon.getInstance().callProvider(this, DaemonMain.getDaemonProcess(this));
        }
    }
}
