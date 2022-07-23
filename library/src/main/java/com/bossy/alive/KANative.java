package com.bossy.alive;

import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.bossy.log.Log;

import java.io.File;

public class KANative {
    static {
        try {
            System.loadLibrary("bossy_daemon");
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private static final String FILE1 = "file1";
    private static final String FILE2 = "file2";
    private static final String FILE3 = "file3";
    private static final String FILE4 = "file4";
    private static final String FILE5 = "file5";
    private static final String FILE6 = "file6";
    private static final String FILE7 = "file7";
    private static final String FILE8 = "file8";
    private static final String FILE_DIR = "5FAD3EB1C8";

    private static final String SUB_PROCESS_NAME = "assist_daemon";

    private static String sSubProcessName;

    private static Context sContext;

    @SuppressWarnings("JavaJniMissingFunction")
    public static native boolean nativeMonitor(String file1, String file2, String file3, String file4, String processName);

    public static void notifyDead() {
        Log.v(Log.TAG, "notify dead");
        try {
            sContext.startInstrumentation(new ComponentName(sContext, DaemonInstrumentation.class), null, null);
            startAllService(sContext, "monitor");
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
        }
    }

    public static void startAllService(Context context, String label) {
        Intent intent1 = new Intent(context, DaemonService1.class);
        intent1.putExtra(BaseDaemonService.EXTRA_FROM, label);
        Intent intent2 = new Intent(context, DaemonService2.class);
        intent2.putExtra(BaseDaemonService.EXTRA_FROM, label);
        BaseDaemonService.startService(context, intent1);
        BaseDaemonService.startService(context, intent2);
    }

    public static void init(Context context, String subProcessName) {
        sContext = context;
        sSubProcessName = subProcessName;
        initIndicatorFiles(context);
    }

    private static void initIndicatorFiles(Context context) {
        try {
            String file1 = getAbsolutePath(context, FILE1);
            File newFile1 = new File(file1);
            if (!newFile1.exists()) {
                newFile1.createNewFile();
            }
            String file2 = getAbsolutePath(context, FILE2);
            File newFile2 = new File(file2);
            if (!newFile2.exists()) {
                newFile2.createNewFile();
            }
        } catch (Exception e) {
        }
    }

    public static void callProvider(Context context, String str) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            ContentProviderClient acquireUnstableContentProviderClient = contentResolver.acquireUnstableContentProviderClient(Uri.parse("content://" + context.getPackageName() + "." + str));
            if (acquireUnstableContentProviderClient != null) {
                acquireUnstableContentProviderClient.call("start", null, null);
                acquireUnstableContentProviderClient.release();
            }
        } catch (Exception e) {
            Log.e(Log.TAG, "error : " + e);
        }
    }


    private static String getAbsolutePath(Context context, String fileName) {
        if (context == null || TextUtils.isEmpty(fileName)) {
            return null;
        }
        File fileDir = context.getDir(FILE_DIR, 0);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return new File(fileDir, fileName).getAbsolutePath();
    }

    public static void onAssistantProviderCreate(final Context context) {
        new Thread() {
            @Override
            public void run() {
                final String file5 = getAbsolutePath(context, FILE5);
                final String file6 = getAbsolutePath(context, FILE6);
                final String file7 = getAbsolutePath(context, FILE7);
                final String file8 = getAbsolutePath(context, FILE8);
                nativeMonitor(file5, file6, file7, file8, getSubProcessName(context));
            }
        }.start();

    }

    public static void onPersistentProviderCreate(final Context context) {
        new Thread() {
            @Override
            public void run() {
                final String file5 = getAbsolutePath(context, FILE5);
                final String file6 = getAbsolutePath(context, FILE6);
                final String file7 = getAbsolutePath(context, FILE7);
                final String file8 = getAbsolutePath(context, FILE8);
                nativeMonitor(file6, file5, file8, file7, getSubProcessName(context));
            }
        }.start();
    }

    public static void onAssistantServiceCreate(final Context context) {
        new Thread() {
            @Override
            public void run() {
                final String file1 = getAbsolutePath(context, FILE1);
                final String file2 = getAbsolutePath(context, FILE2);
                final String file3 = getAbsolutePath(context, FILE3);
                final String file4 = getAbsolutePath(context, FILE4);
                nativeMonitor(file2, file1, file4, file3, getSubProcessName(context));
            }
        }.start();
    }

    public static void onPersistentServiceCreate(final Context context) {
        new Thread() {
            @Override
            public void run() {
                final String file1 = getAbsolutePath(context, FILE1);
                final String file2 = getAbsolutePath(context, FILE2);
                final String file3 = getAbsolutePath(context, FILE3);
                final String file4 = getAbsolutePath(context, FILE4);
                nativeMonitor(file1, file2, file3, file4, getSubProcessName(context));
            }
        }.start();
    }

    private static String getSubProcessName(final Context context) {
        if (!TextUtils.isEmpty(sSubProcessName)) {
            return sSubProcessName;
        }
        return SUB_PROCESS_NAME;
    }
}
