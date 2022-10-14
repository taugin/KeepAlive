package com.shrewd.cmp;

import android.app.Service;
import android.content.Intent;

import com.shrewd.KeepAlive;
import com.shrewd.daemon.JavaDaemon;

public abstract class DaemonBaseService extends Service {
    public void onCreate() {
        super.onCreate();
        Intent service1Intent = new Intent();
        service1Intent.setClassName(getPackageName(), AService1.class.getName());
        Intent service2Intent = new Intent();
        service2Intent.setClassName(getPackageName(), AService2.class.getName());
        Intent daemonIntent = new Intent();
        daemonIntent.setClassName(getPackageName(), DaemonService.class.getName());
        try {
            startService(service1Intent);
        } catch (Exception | Error e) {
            JavaDaemon.getInstance().callProvider(this, DaemonMain.PROCESS_ASSIST1);
        }
        try {
            startService(service2Intent);
        } catch (Exception | Error e) {
            JavaDaemon.getInstance().callProvider(this, DaemonMain.PROCESS_ASSIST2);
        }
        try {
            startService(daemonIntent);
        } catch (Exception | Error e) {
            JavaDaemon.getInstance().callProvider(this, DaemonMain.PROCESS_DAEMON);
        }
    }
}
