package com.bluesky.cmp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bluesky.log.Log;

public class DReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.iv(Log.TAG, "onReceiver");
    }
}
