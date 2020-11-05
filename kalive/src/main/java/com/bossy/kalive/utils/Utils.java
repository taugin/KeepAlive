package com.bossy.kalive.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;

import com.bossy.kalive.log.Log;

import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2018/2/11.
 */

public class Utils {
    private static final Random sRandom = new Random(System.currentTimeMillis());

    public static boolean isEnableOnePixel() {
        String str = getRemoteConfig("enable_one_pixel");
        if (!TextUtils.isEmpty(str)) {
            try {
                return Boolean.parseBoolean(str);
            } catch (Exception e) {
                Log.e(Log.TAG, "error : " + e);
            }
        }
        return false;
    }

    private static String getRemoteConfig(String key) {
        String remoteValue = getConfigFromUmeng(key);
        if (!TextUtils.isEmpty(remoteValue)) {
            Log.iv(Log.TAG, "umeng config | " + key + " : " + remoteValue);
            return remoteValue;
        }
        remoteValue = getConfigFromFirebase(key);
        if (!TextUtils.isEmpty(remoteValue)) {
            Log.iv(Log.TAG, "firebase config | " + key + " : " + remoteValue);
            return remoteValue;
        }
        return null;
    }

    private static String getConfigFromFirebase(String key) {
        String error = null;
        try {
            Class<?> clazz = Class.forName("com.google.firebase.remoteconfig.FirebaseRemoteConfig");
            Method method = clazz.getMethod("getInstance");
            Object instance = method.invoke(null);
            method = clazz.getMethod("getString", String.class);
            Object value = method.invoke(instance, key);
            if (value != null) {
                return (String) value;
            }
        } catch (Exception e) {
            error = String.valueOf(e);
        } catch (Error e) {
            error = String.valueOf(e);
        }
        if (!TextUtils.isEmpty(error)) {
            Log.iv(Log.TAG, "firebase error : " + error);
        }
        return null;
    }

    private static String getConfigFromUmeng(String key) {
        String error = null;
        try {
            Class<?> clazz = Class.forName("com.umeng.cconfig.UMRemoteConfig");
            Method method = clazz.getMethod("getInstance");
            Object instance = method.invoke(null);
            method = clazz.getMethod("getConfigValue", String.class);
            Object value = method.invoke(instance, key);
            if (value != null) {
                return (String) value;
            }
        } catch (Exception e) {
            error = String.valueOf(e);
        } catch (Error e) {
            error = String.valueOf(e);
        }
        if (!TextUtils.isEmpty(error)) {
            Log.iv(Log.TAG, "umeng error : " + error);
        }
        return null;
    }

    public static String string2MD5(String str) {
        MessageDigest md5 = null;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            Log.d(Log.TAG, "error : " + e);
            return "";
        }
        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = md5Bytes[i] & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    public static Intent getIntentByAction(Context context, String action) {
        Intent intent = null;
        if (TextUtils.isEmpty(action)) {
            return intent;
        }
        try {
            Intent queryIntent = new Intent(action);
            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(queryIntent, 0);
            List<String> activitiyNames = new ArrayList<String>();
            for (ResolveInfo info : list) {
                if (info != null && info.activityInfo != null && !TextUtils.isEmpty(info.activityInfo.name)) {
                    activitiyNames.add(info.activityInfo.name);
                }
            }
            if (!activitiyNames.isEmpty()) {
                int size = activitiyNames.size();
                String className = activitiyNames.get(sRandom.nextInt(size));
                intent = new Intent(action);
                intent.setClassName(context.getPackageName(), className);
            }
        } catch (Exception e) {
            Log.e(Log.TAG, "error : " + e);
        }
        return intent;
    }

    public static Object reflectCall(Object object, String className, String methodName, Class<?>[] argType, Object[] argValue) {
        String error = null;
        try {
            Class<?> cls = Class.forName(className);
            Method method = cls.getMethod(methodName, argType);
            return method.invoke(object, argValue);
        } catch (Exception e) {
            error = String.valueOf(e);
        } catch (Error e) {
            error = String.valueOf(e);
        }
        if (!TextUtils.isEmpty(error)) {
            Log.iv(Log.TAG, "error : " + error);
        }
        return null;
    }
}