package com.alive.demo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.lioned.cmp.DaemonBaseService;
import com.lioned.utils.Utils;
import com.alive.log.Log;

import java.text.SimpleDateFormat;
import java.util.Date;


public class NotifyResidentService extends DaemonBaseService {
    private static Object sObject = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "daemon");
            builder.setContentTitle(getApplicationInfo().loadLabel(getPackageManager()) + "正在运行中");
            builder.setContentText("App已经被复活" + getAliveTimes() + "次 : " + new SimpleDateFormat("MM/dd HH:mm:ss").format(new Date()));
            builder.setSmallIcon(getApplicationInfo().icon);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), getSettingsDetail(), PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel("daemon", "daemon", NotificationManager.IMPORTANCE_LOW);
                notificationChannel.enableLights(false);
                notificationChannel.enableVibration(false);
                notificationChannel.setVibrationPattern(new long[]{0});
                notificationChannel.setSound(null, null);
                NotificationManagerCompat.from(this).createNotificationChannel(notificationChannel);
            }
            startForeground(123456, builder.build());
        } catch (Exception e) {
            Log.v(Log.TAG, "error : " + e);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private long getAliveTimes() {
        long times = Utils.getLong(this, "pref_alive_times", 0);
        if (sObject == null) {
            sObject = new Object();
            times = times + 1;
            Utils.putLong(this, "pref_alive_times", times);
        }
        return times;
    }

    private Intent getSettingsDetail() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        return intent;
    }
}
