package com.shrewd.cmp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.shrewd.log.Log;

public class AService1 extends Service {
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        Log.iv(Log.TAG, "onCreate: ");
    }
}
