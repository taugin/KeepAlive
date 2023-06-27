package com.blue.wdt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.blue.log.Log;

public class OReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.iv(Log.TAG, "onReceiver");
    }
}
