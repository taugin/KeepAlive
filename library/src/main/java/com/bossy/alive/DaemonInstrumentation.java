package com.bossy.alive;

import android.app.Instrumentation;
import android.os.Build;
import android.os.Bundle;

import com.bossy.log.Log;

public class DaemonInstrumentation extends Instrumentation {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String processName = Build.VERSION.SDK_INT >= 26 ? getProcessName() : "";
        Log.v(Log.TAG, "onCreate processName : " + processName);
        CGNative.startAllService(getContext(), "instrumentation");
    }
}