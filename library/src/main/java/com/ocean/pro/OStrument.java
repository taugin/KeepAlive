package com.ocean.pro;

import android.app.Application;
import android.app.Instrumentation;
import android.os.Bundle;

import com.ocean.svr.DCService;
import com.ocean.utils.Utils;
import com.ocean.log.Log;

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
