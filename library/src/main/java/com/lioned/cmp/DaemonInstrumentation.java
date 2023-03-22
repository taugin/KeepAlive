package com.lioned.cmp;

import android.app.Application;
import android.app.Instrumentation;
import android.os.Bundle;
import com.lioned.utils.Utils;
import com.lioned.log.Log;

public class DaemonInstrumentation extends Instrumentation {
    public void callApplicationOnCreate(Application application) {
        super.callApplicationOnCreate(application);
        Log.iv(Log.TAG, "callApplicationOnCreate");
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.iv(Log.TAG, "onCreate");
        Utils.startServiceOrBindService(getTargetContext(), DaemonService.class);
    }
}
