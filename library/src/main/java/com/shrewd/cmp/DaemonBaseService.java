package com.shrewd.cmp;

import android.app.Service;
import android.content.Intent;

public abstract class DaemonBaseService extends Service {
    public void onCreate() {
        super.onCreate();
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), AService1.class.getName());
        Intent intent2 = new Intent();
        intent2.setClassName(getPackageName(), AService2.class.getName());
        Intent intent3 = new Intent();
        intent3.setClassName(getPackageName(), DaemonService.class.getName());
        startService(intent);
        startService(intent2);
        startService(intent3);
    }
}
