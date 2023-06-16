package com.ocean.svr;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.ocean.log.Log;

public class ACService extends Service {
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        Log.iv(Log.TAG, "onCreate: ");
    }
}
