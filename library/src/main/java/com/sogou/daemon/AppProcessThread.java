package com.sogou.daemon;

import android.content.Context;

import com.sogou.log.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class AppProcessThread extends Thread {
    private Context a;
    private String[] b;

    /* renamed from: c  reason: collision with root package name */
    private String f4739c;

    public AppProcessThread(Context context, String[] strArr, String str) {
        this.a = context;
        this.f4739c = str;
        this.b = strArr;
    }

    public void run() {
        DaemonEnv b2 = JavaDaemon.getInstance().b();
        DaemonEntity daemonEntity = new DaemonEntity();
        daemonEntity.b = this.f4739c;
        daemonEntity.a = this.b;
        daemonEntity.f4740c = b2.d;
        daemonEntity.d = b2.e;
        daemonEntity.e = b2.f;
        String str = b2.b;
        ArrayList arrayList = new ArrayList();
        arrayList.add("export CLASSPATH=$CLASSPATH:" + str);
        String str2 = b2.f4741c;
        String str3 = "export _LD_LIBRARY_PATH=/system/lib/:/vendor/lib/:" + str2;
        arrayList.add(str3);
        arrayList.add("export LD_LIBRARY_PATH=/system/lib/:/vendor/lib/:" + str2);
        arrayList.add(String.format("%s / %s %s --application --nice-name=%s &", new Object[]{new File("/system/bin/app_process32").exists() ? "app_process32" : "app_process", DaemonMain.class.getName(), daemonEntity.toString(), this.f4739c}));
        File file = new File("/");
        String[] strArr = new String[arrayList.size()];
        for (int i = 0; i < strArr.length; i++) {
            strArr[i] = (String) arrayList.get(i);
            Log.v(Log.TAG, strArr[i]);
        }
        ShellExecutor.a(file, (Map) null, strArr);
    }
}
