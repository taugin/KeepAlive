package com.lioned.cmp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DaemonProcessService extends Service {
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
    }
}
