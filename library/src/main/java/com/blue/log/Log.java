package com.blue.log;

import android.annotation.SuppressLint;
import android.os.Environment;


import com.blue.BuildConfig;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Log {

    private static final boolean GLOBAL_TAG = true;
    private static final int VERBOSE = android.util.Log.VERBOSE;
    private static final int DEBUG = android.util.Log.DEBUG;
    private static final int INFO = android.util.Log.INFO;
    private static final int ERROR = android.util.Log.ERROR;
    private static final int WARN = android.util.Log.WARN;
    private static final boolean INTERNAL_LOG_ENABLE;

    public static final String TAG = "k-alive";
    public static final boolean DB = BuildConfig.DEBUG;

    static {
        boolean internal = false;
        try {
            File tagFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File debugFile = new File(tagFolder, ".debug");
            internal = debugFile.exists();
        } catch (Exception e) {
        }
        INTERNAL_LOG_ENABLE = DB ? DB : internal;
    }

    private static boolean isLoggable(String tag, int level) {
        if (DB) {
            return true;
        }
        return android.util.Log.isLoggable(tag, level);
    }

    public static void d(String tag, String message) {
        tag = checkLogTag(tag);
        if (isLoggable(tag, DEBUG)) {
            String extraString = getMethodNameAndLineNumber();
            tag = privateTag() ? tag : getTag();
            android.util.Log.d(tag, extraString + message);
        }
    }

    public static void v(String tag, String message) {
        tag = checkLogTag(tag);
        if (isLoggable(tag, VERBOSE)) {
            String extraString = getMethodNameAndLineNumber();
            tag = privateTag() ? tag : getTag();
            android.util.Log.v(tag, extraString + message);
        }
    }

    public static void iv(String tag, String message) {
        tag = checkLogTag(tag);
        if (isLoggable(tag, VERBOSE) && INTERNAL_LOG_ENABLE) {
            String extraString = getMethodNameAndLineNumber();
            tag = privateTag() ? tag : getTag();
            android.util.Log.v(tag, extraString + message);
        }
    }

    public static void i(String tag, String message) {
        tag = checkLogTag(tag);
        if (isLoggable(tag, INFO)) {
            String extraString = getMethodNameAndLineNumber();
            tag = privateTag() ? tag : getTag();
            android.util.Log.i(tag, extraString + message);
        }
    }

    public static void w(String tag, String message) {
        tag = checkLogTag(tag);
        if (isLoggable(tag, WARN)) {
            String extraString = getMethodNameAndLineNumber();
            tag = privateTag() ? tag : getTag();
            android.util.Log.w(tag, extraString + message);
        }
    }

    public static void e(String tag, String message) {
        tag = checkLogTag(tag);
        if (isLoggable(tag, ERROR)) {
            String extraString = getMethodNameAndLineNumber();
            tag = privateTag() ? tag : getTag();
            android.util.Log.e(tag, extraString + message);
        }
    }

    public static void e(String tag, String message, Throwable throwable) {
        tag = checkLogTag(tag);
        if (isLoggable(tag, ERROR)) {
            String extraString = getMethodNameAndLineNumber();
            tag = privateTag() ? tag : getTag();
            android.util.Log.e(tag, extraString + message, throwable);
        }
    }

    private static boolean privateTag() {
        return GLOBAL_TAG;
    }

    private static String checkLogTag(String tag) {
        if (tag != null && tag.length() > 23) {
            tag = TAG;
        }
        return tag;
    }

    @SuppressLint("DefaultLocale")
    private static String getMethodNameAndLineNumber() {
        StackTraceElement element[] = Thread.currentThread().getStackTrace();
        if (element != null && element.length >= 4) {
            String methodName = element[4].getMethodName();
            int lineNumber = element[4].getLineNumber();
            return String.format("%s.%s : %d ---> ", getClassName(),
                    methodName, lineNumber, Locale.CHINESE);
        }
        return null;
    }

    private static String getTag() {
        StackTraceElement element[] = Thread.currentThread().getStackTrace();
        if (element != null && element.length >= 4) {
            String className = element[4].getClassName();
            if (className == null) {
                return null;
            }
            int index = className.lastIndexOf(".");
            if (index != -1) {
                className = className.substring(index + 1);
            }
            index = className.indexOf('$');
            if (index != -1) {
                className = className.substring(0, index);
            }
            return className;
        }
        return null;
    }

    private static String getClassName() {
        StackTraceElement element[] = Thread.currentThread().getStackTrace();
        if (element != null && element.length >= 4) {
            String className = element[5].getClassName();
            if (className == null) {
                return null;
            }
            int index = className.lastIndexOf(".");
            if (index != -1) {
                className = className.substring(index + 1);
            }
            index = className.indexOf('$');
            if (index != -1) {
                className = className.substring(0, index);
            }
            return className;
        }
        return null;
    }

    public static void recordOperation(String operation) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
        String time = sdf.format(new Date(System.currentTimeMillis())) + " : ";
        try {
            File external = Environment.getExternalStorageDirectory();
            String dir = external.getAbsoluteFile() + File.separator
                    + "mysee/log";
            File dirFile = new File(dir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            if (external != null) {
                FileWriter fp = new FileWriter(
                        dir + File.separator + "log.txt", true);
                fp.write(time + operation + "\n");
                fp.close();
            }
        } catch (Exception e) {
            android.util.Log.d(Log.TAG, "error : " + e);
        }
    }
}