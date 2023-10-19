package com.atvalue.svr;

import android.app.Service;
import android.content.Intent;

import com.atvalue.vess.ATMess;
import com.atvalue.daemon.JavaDaemon;

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
            JavaDaemon.getInstance().callProvider(this, ATMess.getAssist1Process(this));
        }
        try {
            startService(service2Intent);
        } catch (Exception | Error e) {
            JavaDaemon.getInstance().callProvider(this, ATMess.getAssist2Process(this));
        }
        try {
            startService(daemonIntent);
        } catch (Exception | Error e) {
            JavaDaemon.getInstance().callProvider(this, ATMess.getDaemonProcess(this));
        }
    }
}
