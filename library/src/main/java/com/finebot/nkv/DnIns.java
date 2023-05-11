package com.finebot.nkv;

import android.app.Instrumentation;
import android.os.Build;
import android.os.Bundle;

import com.finebot.FineBot;
import com.finebot.log.Log;

public class DnIns extends Instrumentation {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FineBot.OnAliveListener listener = FineBot.getOnAliveListener();
        if (listener != null && listener.allowKeepAlive()) {
            String processName = Build.VERSION.SDK_INT >= 26 ? getProcessName() : "";
            Log.iv(Log.TAG, "onCreate processName : " + processName);
            KANative.startAllService(getContext(), "instrumentation");
        }
    }
}
