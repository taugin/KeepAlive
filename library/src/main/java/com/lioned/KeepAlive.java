package com.lioned;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.lioned.cmp.DaemonInstrumentation;
import com.lioned.cmp.DaemonMain;
import com.lioned.cmp.DaemonReceiver;
import com.lioned.cmp.DaemonService;
import com.lioned.daemon.JavaDaemon;
import com.lioned.log.Log;
import com.lioned.utils.Utils;

public class KeepAlive {
    public static void attachBaseContext(Context context, Class<?> service) {
        if (service != null) {
            Utils.putString(context, Service.class.getName() + "_Name", service.getName());
        }
        JavaDaemon.getInstance().init(context, new Intent(context, DaemonService.class), new Intent(context, DaemonReceiver.class), new Intent(context, DaemonInstrumentation.class));
        JavaDaemon.getInstance().startAppLock(context, new String[]{DaemonMain.getDaemonProcess(context), DaemonMain.getAssist1Process(context), DaemonMain.getAssist2Process(context)});
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
            JavaDaemon.getInstance().callProvider(context, DaemonMain.getDaemonProcess(context));
            JavaDaemon.getInstance().callProvider(context, DaemonMain.getAssist1Process(context));
            JavaDaemon.getInstance().callProvider(context, DaemonMain.getAssist2Process(context));
        }
    }
}
