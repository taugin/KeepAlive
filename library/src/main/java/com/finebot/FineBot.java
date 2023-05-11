package com.finebot;

import android.content.Context;
import android.text.TextUtils;
import android.util.AndroidRuntimeException;

import com.finebot.log.Log;
import com.finebot.nkv.KANative;
import com.finebot.utils.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class FineBot {
    private static final String COLON_SEPARATOR = ":";
    private static boolean sAliveStarted = false;

    public static void startKeepAlive(Context context) {
        startKeepAlive(context, null);
    }

    public static void startKeepAlive(Context context, String subProcessName) {
        OnAliveListener listener = getOnAliveListener();
        if (listener != null) {
            if (listener.allowKeepAlive()) {
                String packageName = context.getPackageName();
                String processName = getProcessName(context);
                KANative.init(context, subProcessName);
                Log.iv(Log.TAG, "processName : " + processName);
                if (TextUtils.equals(packageName, processName)) {
                    if (!sAliveStarted) {
                        KANative.onPersistentServiceCreate(context);
                        KANative.startAllService(context, "application");
                        KANative.callProvider(context, "service_p");
                        KANative.callProvider(context, "core_p");
                        sAliveStarted = true;
                    } else {
                        Log.iv(Log.TAG, "process has started");
                    }
                } else if (TextUtils.equals("core", processName)) {
                    KANative.onAssistantServiceCreate(context);
                } else if (TextUtils.equals("core_p", processName)) {
                    KANative.onAssistantProviderCreate(context);
                } else if (TextUtils.equals("service_p", processName)) {
                    KANative.onPersistentProviderCreate(context);
                }
                disableAPIDialog();
            } else {
                Log.iv(Log.TAG, "not allow start");
            }
        } else {
            throw new AndroidRuntimeException("You must call setOnAliveListener first");
        }
    }

    private static String getProcessName(Context context) {
        String cmdLine = Utils.getCmdLine();
        Log.iv(Log.TAG, "cmdLine : " + cmdLine);
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
        boolean allowKeepAlive();
    }
}
