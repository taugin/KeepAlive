package com.blue.env;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

public class DaemonEntity implements Parcelable {
    public static final Creator<DaemonEntity> CREATOR = new Creator<DaemonEntity>() {
        /* renamed from: a */
        public DaemonEntity createFromParcel(Parcel parcel) {
            return new DaemonEntity(parcel);
        }

        /* renamed from: a */
        public DaemonEntity[] newArray(int i) {
            return new DaemonEntity[i];
        }
    };
    public String[] daemonPath;
    public String processName;

    /* renamed from: c  reason: collision with root package name */
    public Intent serviceIntent;
    public Intent broadcastIntent;
    public Intent instrumentIntent;

    public int describeContents() {
        return 0;
    }

    public DaemonEntity() {
    }

    protected DaemonEntity(Parcel parcel) {
        this.daemonPath = parcel.createStringArray();
        this.processName = parcel.readString();
        if (parcel.readInt() != 0) {
            this.serviceIntent = (Intent) Intent.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.broadcastIntent = (Intent) Intent.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.instrumentIntent = (Intent) Intent.CREATOR.createFromParcel(parcel);
        }
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(this.daemonPath);
        parcel.writeString(this.processName);
        if (this.serviceIntent == null) {
            parcel.writeInt(0);
        } else {
            parcel.writeInt(1);
            this.serviceIntent.writeToParcel(parcel, i);
        }
        if (this.broadcastIntent == null) {
            parcel.writeInt(0);
        } else {
            parcel.writeInt(1);
            this.broadcastIntent.writeToParcel(parcel, i);
        }
        if (this.instrumentIntent == null) {
            parcel.writeInt(0);
            return;
        }
        parcel.writeInt(1);
        this.instrumentIntent.writeToParcel(parcel, i);
    }

    public static DaemonEntity toObject(String str) {
        byte[] decode = Base64.decode(str, 2);
        Parcel obtain = Parcel.obtain();
        obtain.unmarshall(decode, 0, decode.length);
        obtain.setDataPosition(0);
        return CREATOR.createFromParcel(obtain);
    }

    public String toString() {
        Parcel obtain = Parcel.obtain();
        writeToParcel(obtain, 0);
        String encodeToString = Base64.encodeToString(obtain.marshall(), 2);
        obtain.recycle();
        return encodeToString;
    }
}
