package com.bossy.daemon;

import android.content.Context;

import com.bossy.log.Log;
import com.bossy.env.DaemonEntity;
import com.bossy.env.DaemonEnv;
import com.bossy.component.DaemonMain;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class AppProcessThread extends Thread {
    private Context context;
    private String[] daemonPath;

    /* renamed from: c  reason: collision with root package name */
    private String processName;

    public AppProcessThread(Context context, String[] strArr, String str) {
        this.context = context;
        this.processName = str;
        this.daemonPath = strArr;
    }

    public void run() {
        DaemonEnv daemonEnv = JavaDaemon.getInstance().getDaemonEnv();
        DaemonEntity daemonEntity = new DaemonEntity();
        daemonEntity.processName = this.processName;
        daemonEntity.daemonPath = this.daemonPath;
        daemonEntity.serviceIntent = daemonEnv.serviceIntent;
        daemonEntity.broadcastIntent = daemonEnv.receiverIntent;
        daemonEntity.instrumentIntent = daemonEnv.instrumentIntent;
        String str = daemonEnv.publicDir;
        String str2 = daemonEnv.nativeDir;
        ArrayList arrayList = new ArrayList();
        if (str2 != null && str2.contains("64")) {
            arrayList.add("export CLASSPATH=$CLASSPATH:" + str);
            arrayList.add("export _LD_LIBRARY_PATH=/system/lib64/:/vendor/lib64/:" + str2);
            arrayList.add("export LD_LIBRARY_PATH=/system/lib64/:/vendor/lib64/:" + str2);
            arrayList.add(String.format("%s / %s %s --application --nice-name=%s &", new Object[]{new File("/system/bin/app_process").exists() ? "app_process" : "app_process32", DaemonMain.class.getName(), daemonEntity.toString(), this.processName}));
        } else {
            arrayList.add("export CLASSPATH=$CLASSPATH:" + str);
            arrayList.add("export _LD_LIBRARY_PATH=/system/lib/:/vendor/lib/:" + str2);
            arrayList.add("export LD_LIBRARY_PATH=/system/lib/:/vendor/lib/:" + str2);
            arrayList.add(String.format("%s / %s %s --application --nice-name=%s &", new Object[]{new File("/system/bin/app_process32").exists() ? "app_process32" : "app_process", DaemonMain.class.getName(), daemonEntity.toString(), this.processName}));
        }
        File file = new File("/");
        String[] strArr = new String[arrayList.size()];
        for (int i = 0; i < strArr.length; i++) {
            strArr[i] = (String) arrayList.get(i);
            Log.v(Log.TAG, strArr[i]);
        }
        ShellExecutor.exec(file, (Map) null, strArr);
    }
}
