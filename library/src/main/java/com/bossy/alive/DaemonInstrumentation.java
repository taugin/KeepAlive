package com.bossy.alive;

import android.app.Instrumentation;
import android.os.Build;
import android.os.Bundle;

import com.bossy.KeepBossy;
import com.bossy.log.Log;

public class DaemonInstrumentation extends Instrumentation {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        KeepBossy.OnBossyListener listener = KeepBossy.getOnBossyListener();
        if (listener != null && listener.allowKeepBossy()) {
            String processName = Build.VERSION.SDK_INT >= 26 ? getProcessName() : "";
            Log.iv(Log.TAG, "onCreate processName : " + processName);
            KANative.startAllService(getContext(), "instrumentation");
        }
    }
}
