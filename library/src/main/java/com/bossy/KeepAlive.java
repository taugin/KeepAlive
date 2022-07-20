package com.bossy;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.bossy.component.DaemonInstrumentation;
import com.bossy.component.DaemonReceiver;
import com.bossy.component.DaemonService;
import com.bossy.daemon.JavaDaemon;
import com.bossy.utils.Utils;

public class KeepAlive {
    public static void attachBaseContext(Context context, Class<?> service) {
        if (context != null && service != null) {
            Utils.putString(context, Service.class.getName() + "_Name", service.getName());
        }
        initKeepAlive(context);
    }

    private static void initKeepAlive(Context context) {
        JavaDaemon.getInstance().init(context, new Intent(context, DaemonService.class), new Intent(context, DaemonReceiver.class), new Intent(context, DaemonInstrumentation.class));
        JavaDaemon.getInstance().startAppLock(context, new String[]{"daemon", "assist1", "assist2"});
    }

    public static void startKeepAlive(Context context) {
        try {
            String serviceName = Utils.getString(context, Service.class.getName() + "_Name");
            Intent intent = new Intent();
            intent.setClassName(context.getPackageName(), serviceName);
            if (Build.VERSION.SDK_INT >= 26) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        } catch (Exception e) {
        }
    }
}
