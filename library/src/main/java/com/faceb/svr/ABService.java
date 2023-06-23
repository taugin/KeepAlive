package com.faceb.svr;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.faceb.log.Log;

public class ABService extends Service {
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        Log.iv(Log.TAG, "onCreate: ");
    }
}
