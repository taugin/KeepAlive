package com.sogou.daemon;

import com.bossy.log.Log;

public class NativeKeepAlive {
    public static native void lockFile(String str);

    public static native void nativeSetSid();

    public static native void waitFileLock(String str);

    static {
        try {
            System.loadLibrary("bossy_daemon1");
        } catch (Exception e) {
            Log.e(Log.TAG, "error : " + e, e);
        }
    }
}
