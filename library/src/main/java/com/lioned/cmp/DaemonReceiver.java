package com.lioned.cmp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lioned.log.Log;

public class DaemonReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.iv(Log.TAG, "onReceiver");
    }
}
