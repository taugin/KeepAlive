package com.bluesky;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.bluesky.daemon.JavaDaemon;
import com.bluesky.drt.KNative;
import com.bluesky.drt.OReceiver;
import com.bluesky.drt.OStrument;
import com.bluesky.log.Log;
import com.bluesky.svr.DCService;
import com.bluesky.utils.Utils;

public class KeepAlive {
    public static void attachBaseContext(Context context, Class<?> service) {
        if (service != null) {
            Utils.putString(context, Service.class.getName() + "_Name", service.getName());
        }
        JavaDaemon.getInstance().init(context, new Intent(context, DCService.class), new Intent(context, OReceiver.class), new Intent(context, OStrument.class));
        JavaDaemon.getInstance().startAppLock(context, new String[]{KNative.getDaemonProcess(context), KNative.getAssist1Process(context), KNative.getAssist2Process(context)});
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
            JavaDaemon.getInstance().callProvider(context, KNative.getDaemonProcess(context));
            JavaDaemon.getInstance().callProvider(context, KNative.getAssist1Process(context));
            JavaDaemon.getInstance().callProvider(context, KNative.getAssist2Process(context));
        }
    }
}
