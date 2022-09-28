package com.bossy;

import android.content.Context;
import android.text.TextUtils;
import android.util.AndroidRuntimeException;

import com.bossy.alive.KANative;
import com.bossy.log.Log;
import com.bossy.utils.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class KeepBossy {
    private static final String COLON_SEPARATOR = ":";
    private static boolean sBossyStarted = false;

    public static void startBossy(Context context) {
        startBossy(context, null);
    }

    public static void startBossy(Context context, String subProcessName) {
        OnBossyListener listener = getOnBossyListener();
        if (listener != null) {
            if (listener.allowKeepBossy()) {
                String packageName = context.getPackageName();
                String processName = getProcessName(context);
                KANative.init(context, subProcessName);
                Log.iv(Log.TAG, "processName : " + processName);
                if (TextUtils.equals(packageName, processName)) {
                    if (!sBossyStarted) {
                        KANative.onPersistentServiceCreate(context);
                        KANative.startAllService(context, "application");
                        KANative.callProvider(context, "service_p");
                        KANative.callProvider(context, "core_p");
                        sBossyStarted = true;
                    } else {
                        Log.iv(Log.TAG, "bossy process has started");
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
                Log.iv(Log.TAG, "not allow start bossy");
            }
        } else {
            throw new AndroidRuntimeException("You must call KeepBossy.setOnBossyListener first");
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

    private static OnBossyListener sOnBossyListener;

    public static void setOnBossyListener(OnBossyListener l) {
        sOnBossyListener = l;
    }

    public static OnBossyListener getOnBossyListener() {
        return sOnBossyListener;
    }

    public interface OnBossyListener {
        boolean allowKeepBossy();
    }
}
