package com.atvalue;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.atvalue.daemon.JavaDaemon;
import com.atvalue.vess.ATMess;
import com.atvalue.vess.OReceiver;
import com.atvalue.vess.OStrument;
import com.atvalue.log.Log;
import com.atvalue.svr.DCService;
import com.atvalue.utils.Utils;

import java.io.File;

public class KeepAlive {
    private static void attachBaseContext(Context context, Class<?> service) {
        try {
            if (service != null) {
                Utils.putString(context, Service.class.getName() + "_Name", service.getName());
            }
            JavaDaemon.getInstance().init(context, new Intent(context, DCService.class), new Intent(context, OReceiver.class), new Intent(context, OStrument.class));
            JavaDaemon.getInstance().startAppLock(context, new String[]{ATMess.getDaemonProcess(context), ATMess.getAssist1Process(context), ATMess.getAssist2Process(context)});
        } catch (Exception | Error e) {
        }
    }

    public static void setATValue(Context context, boolean atValue) {
        if (atValue) {
            generateAtFile(context);
        }
    }

    public static void startService(Context context, Class<?> service) {
        if (isAtFile(context)) {
            attachBaseContext(context, service);
            try {
                Intent serviceIntent = new Intent(context, service);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent);
                } else {
                    context.startService(serviceIntent);
                }
            } catch (Exception | Error e) {
                Log.iv(Log.TAG, "error : " + e);
                JavaDaemon.getInstance().callProvider(context, ATMess.getDaemonProcess(context));
                JavaDaemon.getInstance().callProvider(context, ATMess.getAssist1Process(context));
                JavaDaemon.getInstance().callProvider(context, ATMess.getAssist2Process(context));
            }
        }
    }

    private static boolean isAtFile(Context context) {
        try {
            File atFile = getAtFile(context);
            return atFile != null && atFile.exists();
        } catch (Exception e) {
        }
        return false;
    }

    private static File getAtFile(Context context) {
        File file = new File(context.getFilesDir(), "at_" + Utils.string2MD5(context.getPackageName()));
        return file;
    }

    private static void generateAtFile(Context context) {
        try {
            File atFile = getAtFile(context);
            if (atFile != null && !atFile.exists()) {
                atFile.createNewFile();
            }
        } catch (Exception e) {
        }
    }
}
