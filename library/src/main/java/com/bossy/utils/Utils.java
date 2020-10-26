package com.bossy.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Utils {
    public static String getCmdLine() {
        try {
            return new BufferedReader(new FileReader(new File("/proc/self/cmdline"))).readLine().trim();
        } catch (Exception e) {
            return null;
        }
    }

    public static void startServiceOrBindService(Context context, Class cls) {
        Intent intent = new Intent(context, cls);
        try {
            context.startService(intent);
        } catch (Exception e) {
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
        }
    }

    public static String getString(Context context, String key) {
        return getString(context, key, null);
    }

    public static String getString(Context context, String key, String defValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defValue);
    }
}
