package com.bossy;

import android.app.Service;
import android.content.Context;
import android.content.Intent;

import com.bossy.daemon.JavaDaemon;
import com.bossy.component.DaemonInstrumentation;
import com.bossy.component.DaemonReceiver;
import com.bossy.component.DaemonService;
import com.bossy.utils.Utils;

public class KeepAlive {
    public static void attachBaseContext(Context context, Class<?> service) {
        if (service != null) {
            Utils.putString(context,  Service.class.getName() + "_Name", service.getName());
        }
        JavaDaemon.getInstance().init(context, new Intent(context, DaemonService.class), new Intent(context, DaemonReceiver.class), new Intent(context, DaemonInstrumentation.class));
        JavaDaemon.getInstance().startAppLock(context, new String[]{"daemon", "assist1", "assist2"});
    }
}
