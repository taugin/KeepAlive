package com.alive.demo;

import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.alive.log.Log;
import com.bluesky.KeepAlive;
import com.bluesky.drt.KNative;

import java.io.File;

public class App extends Application {
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();
        register();
        if (isNonOrganic(this)) {
            KeepAlive.startService(this, NotifyResidentService.class);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if (isNonOrganic(base)) {
            KeepAlive.attachBaseContext(base, NotifyResidentService.class);
        }
    }

    public boolean isMainProcess() {
        return getApplicationContext().getPackageName().equals(getCurrentProcessName());
    }

    /**
     * 获取当前进程名
     */
    private String getCurrentProcessName() {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (null != manager) {
            for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
                if (process.pid == pid) {
                    processName = process.processName;
                }
            }
        }
        return processName;
    }


    public static boolean isNonOrganic(Context context) {
        File bossyFile = getNonOrganicFile(context);
        return bossyFile != null && bossyFile.exists();
    }

    private static File getNonOrganicFile(Context context) {
        File file = new File(context.getFilesDir(), "bossy_start_up");
        return file;
    }

    public static void setNonOrganic(Context context) {
        File bossyFile = getNonOrganicFile(context);
        try {
            if (bossyFile != null) {
                bossyFile.createNewFile();
            }
        } catch (Exception e) {
        }
    }

    public static void removeNonOrganic(Context context) {
        File bossyFile = getNonOrganicFile(context);
        try {
            if (bossyFile != null && bossyFile.exists()) {
                bossyFile.delete();
            }
        } catch (Exception e) {
            Log.v(Log.TAG, "error : " + e);
        }
    }

    private void register() {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (TextUtils.equals(action, Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                        String reason = intent.getStringExtra("reason");
                        if (TextUtils.equals(reason, "homekey") && !mHandler.hasMessages(0x1234)) {
                            mHandler.sendEmptyMessageDelayed(0x1234, 2000);
                            Intent intent1 = new Intent(context, ReminderActivity.class);
                            if (context != null) {
                                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent1);
                            }
                        }
                    }
                }
            }, filter);
        } catch (Exception e) {
            Log.e(Log.TAG, "error : " + e);
        }
    }
}
