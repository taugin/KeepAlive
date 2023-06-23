package com.faceb.daemon;

import com.faceb.log.Log;

import java.lang.reflect.Field;

public class IBinderManager {
    private int mStartService_Transaction = getTransaction("TRANSACTION_startService", "START_SERVICE_TRANSACTION");
    private int mBroadcastIntent_Transaction = getTransaction("TRANSACTION_broadcastIntent", "BROADCAST_INTENT_TRANSACTION");

    /* renamed from: c  reason: collision with root package name */
    private int mStartInstrumentation_Transaction = getTransaction("TRANSACTION_startInstrumentation", "START_INSTRUMENTATION_TRANSACTION");

    public int getTransaction(String str, String str2) {
        try {
            Class<?> cls = Class.forName("android.app.IActivityManager$Stub");
            Field declaredField = cls.getDeclaredField(str);
            declaredField.setAccessible(true);
            return declaredField.getInt(cls);
        } catch (Exception unused) {
            Log.iv(Log.TAG, "error : " + unused);
            try {
                Class<?> cls2 = Class.forName("android.app.IActivityManager");
                Field declaredField2 = cls2.getDeclaredField(str2);
                declaredField2.setAccessible(true);
                return declaredField2.getInt(cls2);
            } catch (Exception unused2) {
                Log.iv(Log.TAG, "error : " + unused2);
                return -1;
            }
        }
    }

    public int getStartServiceTransaction() {
        return this.mStartService_Transaction;
    }

    public int getBroadcastTransaction() {
        return this.mBroadcastIntent_Transaction;
    }

    public int getInstrumentationTransaction() {
        return this.mStartInstrumentation_Transaction;
    }

    public void b(Throwable throwable) {
    }
}
