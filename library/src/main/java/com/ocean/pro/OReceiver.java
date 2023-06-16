package com.ocean.pro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ocean.log.Log;

public class OReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.iv(Log.TAG, "onReceiver");
    }
}
