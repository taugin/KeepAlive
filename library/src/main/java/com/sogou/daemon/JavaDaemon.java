package com.sogou.daemon;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;

import com.sogou.daemon.utils.Utils;
import com.sogou.log.Log;

import java.util.ArrayList;

public class JavaDaemon {
    private static final String COLON_SEPARATOR = ":";
    private static JavaDaemon a = new JavaDaemon();
    private DaemonEnv b;

    public static JavaDaemon getInstance() {
        return a;
    }

    public DaemonEnv b() {
        return this.b;
    }

    public void init(Context context, Intent intent, Intent intent2, Intent intent3) {
        this.b = new DaemonEnv();
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        this.b.b = applicationInfo.publicSourceDir;
        this.b.f4741c = applicationInfo.nativeLibraryDir;
        this.b.d = intent;
        this.b.e = intent2;
        this.b.f = intent3;
        this.b.a = Utils.a();
    }

    public void startAppLock(Context context, String[] strArr) {
        boolean z;
        String a2 = Utils.a();
        Log.v(Log.TAG, "a2 : " + a2);
        if (a2.startsWith(context.getPackageName()) && a2.contains(COLON_SEPARATOR)) {
            String substring = a2.substring(a2.lastIndexOf(COLON_SEPARATOR) + 1);
            ArrayList arrayList = new ArrayList();
            if (strArr != null) {
                z = false;
                for (String str : strArr) {
                    if (str.equals(substring)) {
                        z = true;
                    } else {
                        arrayList.add(str);
                    }
                }
            } else {
                z = false;
            }
            if (z) {
                Log.v(Log.TAG, "app lock file start : " + substring);
                NativeKeepAlive.lockFile(context.getFilesDir() + "/" + substring + "_daemon");
                Log.v(Log.TAG, "app lock file finish");
                String[] strArr2 = new String[arrayList.size()];
                for (int i = 0; i < strArr2.length; i++) {
                    strArr2[i] = context.getFilesDir() + "/" + ((String) arrayList.get(i)) + "_daemon";
                }
                new AppProcessThread(context, strArr2, "daemon").start();
            }
        }
    }
}
