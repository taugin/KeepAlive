package com.bluesky.daemon;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;

import com.bluesky.cmp.DMain;
import com.bluesky.env.DaemonEnv;
import com.bluesky.log.Log;
import com.bluesky.utils.Utils;

import java.util.ArrayList;

public class JavaDaemon {
    private static final String COLON_SEPARATOR = ":";
    private static JavaDaemon sInstance = new JavaDaemon();
    private DaemonEnv daemonEnv;

    public static JavaDaemon getInstance() {
        return sInstance;
    }

    public DaemonEnv getDaemonEnv() {
        return this.daemonEnv;
    }

    public void init(Context context, Intent intent, Intent intent2, Intent intent3) {
        this.daemonEnv = new DaemonEnv();
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        this.daemonEnv.publicDir = applicationInfo.publicSourceDir;
        this.daemonEnv.nativeDir = applicationInfo.nativeLibraryDir;
        this.daemonEnv.serviceIntent = intent;
        this.daemonEnv.receiverIntent = intent2;
        this.daemonEnv.instrumentIntent = intent3;
        this.daemonEnv.cmdLine = Utils.getCmdLine();
    }

    public void startAppLock(Context context, String[] strArr) {
        boolean isSpecProcess;
        String cmdLine = Utils.getCmdLine();
        Log.iv(Log.TAG, "cmdLine : " + cmdLine);
        if (cmdLine != null && cmdLine.startsWith(context.getPackageName()) && cmdLine.contains(COLON_SEPARATOR)) {
            String substring = cmdLine.substring(cmdLine.lastIndexOf(COLON_SEPARATOR) + 1);
            ArrayList arrayList = new ArrayList();
            if (strArr != null) {
                isSpecProcess = false;
                for (String str : strArr) {
                    if (str.equals(substring)) {
                        isSpecProcess = true;
                    } else {
                        arrayList.add(str);
                    }
                }
            } else {
                isSpecProcess = false;
            }
            Log.iv(Log.TAG, "process : " + substring + " , isSpecProcess : " + isSpecProcess);
            if (isSpecProcess) {
                Log.iv(Log.TAG, "app lock file start : " + substring);
                DMain.lockFile(context.getFilesDir() + "/" + substring + "_daemon");
                Log.iv(Log.TAG, "app lock file finish");
                String[] strArr2 = new String[arrayList.size()];
                for (int i = 0; i < strArr2.length; i++) {
                    strArr2[i] = context.getFilesDir() + "/" + ((String) arrayList.get(i)) + "_daemon";
                }
                new AppProcessThread(context, strArr2, /*"daemon"*/substring).start();
            }
        }
    }

    public void callProvider(Context context, String str) {
        try {
            Log.iv(Log.TAG, "call provider : " + str);
            ContentResolver contentResolver = context.getContentResolver();
            ContentProviderClient acquireUnstableContentProviderClient = contentResolver.acquireUnstableContentProviderClient(Uri.parse("content://" + context.getPackageName() + "." + str));
            if (acquireUnstableContentProviderClient != null) {
                acquireUnstableContentProviderClient.call("start", null, null);
                acquireUnstableContentProviderClient.release();
            }
        } catch (Exception | Error e) {
            Log.iv(Log.TAG, "[" + str + "] error : " + e);
        }
    }
}
