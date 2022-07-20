package com.bossy;

import android.content.Context;
import android.text.TextUtils;

import com.bossy.alive.CGNative;
import com.bossy.log.Log;
import com.bossy.utils.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class KeepAlive {
    private static final String COLON_SEPARATOR = ":";

    public static void attachBaseContext(Context context) {
        String packageName = context.getPackageName();
        String processName = getProcessName(context);
        CGNative.init(context);
        Log.v(Log.TAG, "processName : " + processName);
        if (TextUtils.equals(packageName, processName)) {
            CGNative.onPersistentServiceCreate(context);
            CGNative.startAllService(context, "application");
            CGNative.callProvider(context, "service_p");
            CGNative.callProvider(context, "core_p");
        } else if (TextUtils.equals("core", processName)) {
            CGNative.onAssistantServiceCreate(context);
        } else if (TextUtils.equals("core_p", processName)) {
            CGNative.onAssistantProviderCreate(context);
        } else if (TextUtils.equals("service_p", processName)) {
            CGNative.onPersistentProviderCreate(context);
        }
        disableAPIDialog();
    }

    private static String getProcessName(Context context) {
        String cmdLine = Utils.getCmdLine();
        Log.v(Log.TAG, "cmdLine : " + cmdLine);
        if (cmdLine != null && cmdLine.startsWith(context.getPackageName()) && cmdLine.contains(COLON_SEPARATOR)) {
            String substring = cmdLine.substring(cmdLine.lastIndexOf(COLON_SEPARATOR) + 1);
            return substring;
        }
        return context.getPackageName();
    }

    private static void disableAPIDialog() {
        try {
            Class<?> cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread", new Class[0]);
            declaredMethod.setAccessible(true);
            Object invoke = declaredMethod.invoke(null, new Object[0]);
            Field declaredField = cls.getDeclaredField("mHiddenApiWarningShown");
            declaredField.setAccessible(true);
            declaredField.setBoolean(invoke, true);
        } catch (Exception | Error unused) {
        }
    }

    private static OnAliveListener sOnAliveListener;

    public static void setOnAliveListener(OnAliveListener l) {
        sOnAliveListener = l;
    }

    public static OnAliveListener getOnAliveListener() {
        return sOnAliveListener;
    }

    public interface OnAliveListener {
        void onAlive();
    }

}
