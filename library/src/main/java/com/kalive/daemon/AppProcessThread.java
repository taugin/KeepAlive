package com.kalive.daemon;

import android.content.Context;

import com.kalive.daemon.JavaDaemon;
import com.kalive.daemon.ShellExecutor;
import com.kalive.log.Log;
import com.kalive.env.DaemonEntity;
import com.kalive.env.DaemonEnv;
import com.sogou.daemon.DaemonMain;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class AppProcessThread extends Thread {
    private Context context;
    private String[] b;

    /* renamed from: c  reason: collision with root package name */
    private String processName;

    public AppProcessThread(Context context, String[] strArr, String str) {
        this.context = context;
        this.processName = str;
        this.b = strArr;
    }

    public void run() {
        DaemonEnv b2 = JavaDaemon.getInstance().getDaemonEnv();
        DaemonEntity daemonEntity = new DaemonEntity();
        daemonEntity.b = this.processName;
        daemonEntity.a = this.b;
        daemonEntity.f4740c = b2.serviceIntent;
        daemonEntity.d = b2.receiverIntent;
        daemonEntity.e = b2.instrumentIntent;
        String str = b2.publicDir;
        ArrayList arrayList = new ArrayList();
        arrayList.add("export CLASSPATH=$CLASSPATH:" + str);
        String str2 = b2.nativeDir;
        String str3 = "export _LD_LIBRARY_PATH=/system/lib/:/vendor/lib/:" + str2;
        arrayList.add(str3);
        arrayList.add("export LD_LIBRARY_PATH=/system/lib/:/vendor/lib/:" + str2);
        arrayList.add(String.format("%s / %s %s --application --nice-name=%s &", new Object[]{new File("/system/bin/app_process32").exists() ? "app_process32" : "app_process", DaemonMain.class.getName(), daemonEntity.toString(), this.processName}));
        File file = new File("/");
        String[] strArr = new String[arrayList.size()];
        for (int i = 0; i < strArr.length; i++) {
            strArr[i] = (String) arrayList.get(i);
            Log.v(Log.TAG, strArr[i]);
        }
        ShellExecutor.exec(file, (Map) null, strArr);
    }
}
