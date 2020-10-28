package com.shrewd;

import android.app.Service;
import android.content.Context;
import android.content.Intent;

import com.shrewd.daemon.JavaDaemon;
import com.shrewd.cmp.DaemonInstrumentation;
import com.shrewd.cmp.DaemonReceiver;
import com.shrewd.cmp.DaemonService;
import com.shrewd.utils.Utils;

public class KeepAlive {
    public static void attachBaseContext(Context context, Class<?> service) {
        if (service != null) {
            Utils.putString(context,  Service.class.getName() + "_Name", service.getName());
        }
        JavaDaemon.getInstance().init(context, new Intent(context, DaemonService.class), new Intent(context, DaemonReceiver.class), new Intent(context, DaemonInstrumentation.class));
        JavaDaemon.getInstance().startAppLock(context, new String[]{"daemon", "assist1", "assist2"});
    }
}
