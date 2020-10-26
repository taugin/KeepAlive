package com.bossy.component;

import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;

import com.bossy.daemon.IBinderManager;
import com.bossy.env.DaemonEntity;
import com.bossy.log.Log;
import com.sogou.daemon.NativeKeepAlive;

import java.io.Serializable;
import java.lang.reflect.Field;

public class DaemonMain implements Serializable {
    IBinderManager a = new IBinderManager();
    /* access modifiers changed from: private */
    public DaemonEntity b;

    /* renamed from: c  reason: collision with root package name */
    private Parcel f4742c;
    private Parcel d;
    private Parcel e;
    private IBinder f;

    public DaemonMain(DaemonEntity daemonEntity) {
        this.b = daemonEntity;
    }

    public static void main(String[] strArr) {
        DaemonEntity a2 = DaemonEntity.a(strArr[0]);
        if (a2 != null) {
            new DaemonMain(a2).a();
        }
        Process.killProcess(Process.myPid());
    }

    private void a() {
        try {
            i();
            f();
            NativeKeepAlive.nativeSetSid();
            try {
                Log.v(Log.TAG, "setargv0 " + this.b.b);
                Process.class.getMethod("setArgV0", new Class[]{String.class}).invoke((Object) null, new Object[]{this.b.b});
            } catch (Exception e2) {
            }
            for (int i = 1; i < this.b.a.length; i++) {
                new DaemonThread(i).start();
            }
            Log.v(Log.TAG, this.b.b + " start lock File" + this.b.a[0]);
            NativeKeepAlive.waitFileLock(this.b.a[0]);
            Log.v(Log.TAG, "lock File finish");
            d();
            c();
            b();
            Log.v(Log.TAG, "start android finish");
        } catch (Exception e3) {
            a.b(e3);
        }
    }

    /* access modifiers changed from: private */
    public void b() {
        if (this.e != null) {
            try {
                this.f.transact(this.a.c(), this.e, (Parcel) null, 1);
            } catch (Exception e2) {
                a.b(e2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void c() {
        if (this.d != null) {
            try {
                this.f.transact(this.a.b(), this.d, (Parcel) null, 1);
            } catch (Exception e2) {
                a.b(e2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void d() {
        if (this.f4742c != null) {
            try {
                this.f.transact(this.a.a(), this.f4742c, (Parcel) null, 1);
            } catch (Exception e2) {
                a.b(e2);
            }
        }
    }

    private void e() {
        this.f4742c = Parcel.obtain();
        this.f4742c.writeInterfaceToken("android.app.IActivityManager");
        this.f4742c.writeStrongBinder((IBinder) null);
        if (Build.VERSION.SDK_INT >= 26) {
            this.f4742c.writeInt(1);
        }
        this.b.f4740c.writeToParcel(this.f4742c, 0);
        this.f4742c.writeString((String) null);
        if (Build.VERSION.SDK_INT >= 26) {
            this.f4742c.writeInt(0);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            this.f4742c.writeString(this.b.f4740c.getComponent().getPackageName());
        }
        this.f4742c.writeInt(0);
    }

    private void f() {
        e();
        g();
        h();
    }

    private void g() {
        this.d = Parcel.obtain();
        this.d.writeInterfaceToken("android.app.IActivityManager");
        this.d.writeStrongBinder((IBinder) null);
        if (Build.VERSION.SDK_INT >= 26) {
            this.d.writeInt(1);
        }

        this.b.d.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        this.b.d.writeToParcel(this.d, 0);
        this.d.writeString((String) null);
        this.d.writeStrongBinder((IBinder) null);
        this.d.writeInt(-1);
        this.d.writeString((String) null);
        this.d.writeInt(0);
        this.d.writeStringArray((String[]) null);
        this.d.writeInt(-1);
        this.d.writeInt(0);
        this.d.writeInt(0);
        this.d.writeInt(0);
        this.d.writeInt(0);
    }

    private void h() {
        this.e = Parcel.obtain();
        this.e.writeInterfaceToken("android.app.IActivityManager");
        if (Build.VERSION.SDK_INT >= 26) {
            this.e.writeInt(1);
        }
        this.b.e.getComponent().writeToParcel(this.e, 0);
        this.e.writeString((String) null);
        this.e.writeInt(0);
        this.e.writeInt(0);
        this.e.writeStrongBinder((IBinder) null);
        this.e.writeStrongBinder((IBinder) null);
        this.e.writeInt(0);
        this.e.writeString((String) null);
    }

    private void i() {
        try {
            Class<?> cls = Class.forName("android.app.ActivityManagerNative");
            Object invoke = cls.getMethod("getDefault", new Class[0]).invoke(cls, new Object[0]);
            Field declaredField = invoke.getClass().getDeclaredField("mRemote");
            declaredField.setAccessible(true);
            this.f = (IBinder) declaredField.get(invoke);
            IBinder iBinder = (IBinder) Class.forName("android.os.ServiceManager").getMethod("getService", new Class[]{String.class}).invoke((Object) null, new Object[]{"activity"});
            Log.v(Log.TAG, "initAmsBinder: mRemote == iBinder " + this.f);
        } catch (Throwable th) {
            a.b(th);
        }
    }

    public class DaemonThread extends Thread {
        private int b;

        public DaemonThread(int i) {
            this.b = i;
        }

        public void run() {
            setPriority(10);
            NativeKeepAlive.waitFileLock(DaemonMain.this.b.a[this.b]);
            Log.v(Log.TAG, "Thread lock File finish");
            DaemonMain.this.d();
            DaemonMain.this.c();
            DaemonMain.this.b();
            Log.v(Log.TAG, "Thread start android finish");
            Log.v(Log.TAG, "Thread  exit ");
        }
    }
}
