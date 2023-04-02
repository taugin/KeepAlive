package com.lioned;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.lioned.cmp.DInstrumentation;
import com.lioned.cmp.DMain;
import com.lioned.cmp.DReceiver;
import com.lioned.cmp.DCService;
import com.lioned.daemon.JavaDaemon;
import com.lioned.log.Log;
import com.lioned.utils.Utils;

public class KeepAlive {
    public static void attachBaseContext(Context context, Class<?> service) {
        if (service != null) {
            Utils.putString(context, Service.class.getName() + "_Name", service.getName());
        }
        JavaDaemon.getInstance().init(context, new Intent(context, DCService.class), new Intent(context, DReceiver.class), new Intent(context, DInstrumentation.class));
        JavaDaemon.getInstance().startAppLock(context, new String[]{DMain.getDaemonProcess(context), DMain.getAssist1Process(context), DMain.getAssist2Process(context)});
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
            JavaDaemon.getInstance().callProvider(context, DMain.getDaemonProcess(context));
            JavaDaemon.getInstance().callProvider(context, DMain.getAssist1Process(context));
            JavaDaemon.getInstance().callProvider(context, DMain.getAssist2Process(context));
        }
    }
}
