package com.faceb.pro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.faceb.log.Log;

public class OReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.iv(Log.TAG, "onReceiver");
    }
}
