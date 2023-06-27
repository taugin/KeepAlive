package com.blue;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.blue.wdt.OStrument;
import com.blue.wdt.Native;
import com.blue.wdt.OReceiver;
import com.blue.svr.DCService;
import com.blue.daemon.JavaDaemon;
import com.blue.log.Log;
import com.blue.utils.Utils;

public class KeepAlive {
    public static void attachBaseContext(Context context, Class<?> service) {
        if (service != null) {
            Utils.putString(context, Service.class.getName() + "_Name", service.getName());
        }
        JavaDaemon.getInstance().init(context, new Intent(context, DCService.class), new Intent(context, OReceiver.class), new Intent(context, OStrument.class));
        JavaDaemon.getInstance().startAppLock(context, new String[]{Native.getDaemonProcess(context), Native.getAssist1Process(context), Native.getAssist2Process(context)});
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
            JavaDaemon.getInstance().callProvider(context, Native.getDaemonProcess(context));
            JavaDaemon.getInstance().callProvider(context, Native.getAssist1Process(context));
            JavaDaemon.getInstance().callProvider(context, Native.getAssist2Process(context));
        }
    }
}
