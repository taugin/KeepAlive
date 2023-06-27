package com.blue.wdt;

import android.app.Application;
import android.app.Instrumentation;
import android.os.Bundle;

import com.blue.svr.DCService;
import com.blue.utils.Utils;
import com.blue.log.Log;

public class OStrument extends Instrumentation {
    public void callApplicationOnCreate(Application application) {
        super.callApplicationOnCreate(application);
        Log.iv(Log.TAG, "callApplicationOnCreate");
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.iv(Log.TAG, "onCreate");
        Utils.startServiceOrBindService(getTargetContext(), DCService.class);
    }
}
