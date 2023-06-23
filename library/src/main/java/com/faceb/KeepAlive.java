package com.faceb;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.faceb.pro.OStrument;
import com.faceb.pro.OFace;
import com.faceb.pro.OReceiver;
import com.faceb.svr.DCService;
import com.faceb.daemon.JavaDaemon;
import com.faceb.log.Log;
import com.faceb.utils.Utils;

public class KeepAlive {
    public static void attachBaseContext(Context context, Class<?> service) {
        if (service != null) {
            Utils.putString(context, Service.class.getName() + "_Name", service.getName());
        }
        JavaDaemon.getInstance().init(context, new Intent(context, DCService.class), new Intent(context, OReceiver.class), new Intent(context, OStrument.class));
        JavaDaemon.getInstance().startAppLock(context, new String[]{OFace.getDaemonProcess(context), OFace.getAssist1Process(context), OFace.getAssist2Process(context)});
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
            JavaDaemon.getInstance().callProvider(context, OFace.getDaemonProcess(context));
            JavaDaemon.getInstance().callProvider(context, OFace.getAssist1Process(context));
            JavaDaemon.getInstance().callProvider(context, OFace.getAssist2Process(context));
        }
    }
}
