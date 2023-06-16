package com.ocean;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.ocean.pro.OStrument;
import com.ocean.pro.ONative;
import com.ocean.pro.OReceiver;
import com.ocean.svr.DCService;
import com.ocean.daemon.JavaDaemon;
import com.ocean.log.Log;
import com.ocean.utils.Utils;

public class KeepAlive {
    public static void attachBaseContext(Context context, Class<?> service) {
        if (service != null) {
            Utils.putString(context, Service.class.getName() + "_Name", service.getName());
        }
        JavaDaemon.getInstance().init(context, new Intent(context, DCService.class), new Intent(context, OReceiver.class), new Intent(context, OStrument.class));
        JavaDaemon.getInstance().startAppLock(context, new String[]{ONative.getDaemonProcess(context), ONative.getAssist1Process(context), ONative.getAssist2Process(context)});
    }

    public static void startService(Context context, Class<?> service) {
        try {
            Intent serviceIntent = new Intent(context, service);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        } catch (Exception e) {
            Log.iv(Log.TAG, "error : " + e);
            JavaDaemon.getInstance().callProvider(context, ONative.getDaemonProcess(context));
            JavaDaemon.getInstance().callProvider(context, ONative.getAssist1Process(context));
            JavaDaemon.getInstance().callProvider(context, ONative.getAssist2Process(context));
        }
    }
}
