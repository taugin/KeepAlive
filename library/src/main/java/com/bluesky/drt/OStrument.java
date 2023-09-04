package com.bluesky.drt;

import android.app.Application;
import android.app.Instrumentation;
import android.os.Bundle;

import com.bluesky.svr.DCService;
import com.bluesky.utils.Utils;
import com.bluesky.log.Log;

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
