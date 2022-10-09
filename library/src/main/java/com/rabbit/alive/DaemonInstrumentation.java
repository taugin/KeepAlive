package com.rabbit.alive;

import android.app.Instrumentation;
import android.os.Build;
import android.os.Bundle;

import com.rabbit.KeepRabbit;
import com.rabbit.log.Log;

public class DaemonInstrumentation extends Instrumentation {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        KeepRabbit.OnRabbitListener listener = KeepRabbit.getOnRabbitListener();
        if (listener != null && listener.allowKeepRabbit()) {
            String processName = Build.VERSION.SDK_INT >= 26 ? getProcessName() : "";
            Log.iv(Log.TAG, "onCreate processName : " + processName);
            KANative.startAllService(getContext(), "instrumentation");
        }
    }
}
