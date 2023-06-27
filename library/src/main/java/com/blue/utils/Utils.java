package com.blue.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.blue.log.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Utils {
    public static String getCmdLine() {
        try {
            return new BufferedReader(new FileReader(new File("/proc/self/cmdline"))).readLine().trim();
        } catch (Exception e) {
            Log.iv(Log.TAG, "error : " + e);
            return null;
        }
    }

    public static void startServiceOrBindService(Context context, Class cls) {
        Intent intent = new Intent(context, cls);
        try {
            context.startService(intent);
        } catch (Exception e) {
            Log.iv(Log.TAG, "error : " + e);
            context.bindService(intent, new ServiceConnection() {
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                }

                public void onServiceDisconnected(ComponentName componentName) {
                }
            }, 0);
        }
    }

    public static void putString(Context context, String key, String value) {
        try {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).apply();
        } catch (Exception | Error e) {
            Log.iv(Log.TAG, "error : " + e);
        }
    }

    public static String getString(Context context, String key) {
        return getString(context, key, null);
    }

    public static String getString(Context context, String key, String defValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defValue);
    }


    public static void putLong(Context context, String key, long value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(key, value).apply();
    }

    public static long getLong(Context context, String key) {
        return getLong(context, key, 0);
    }

    public static long getLong(Context context, String key, long defValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(key, defValue);
    }

    public static void startService(Context context, Intent intent) {
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        } catch (Exception e) {
            Log.iv(Log.TAG, "error : " + e);
        }
    }

    /**
     * 获取当前进程名
     */
    public static int getMainProcessId(Context context) {
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (null != manager) {
                for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
                    if (TextUtils.equals(process.processName, context.getPackageName())) {
                        return process.pid;
                    }
                }
            }
        } catch (Exception e) {
        }
        return -1;
    }

    public static String queryProcessName(Context context, Class<?> className) {
        try {
            ServiceInfo serviceInfo = context.getPackageManager().getServiceInfo(new ComponentName(context, className), 0);
            return serviceInfo.processName.replace(context.getPackageName() + ":", "");
        } catch (Exception e) {
            Log.e(Log.TAG, "error : " + e);
        }
        return null;
    }
}
