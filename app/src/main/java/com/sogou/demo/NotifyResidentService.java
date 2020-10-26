package com.sogou.demo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.kalive.component.DaemonBaseService;


public class NotifyResidentService extends DaemonBaseService {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "daemon");
            builder.setContentTitle("Title");
            builder.setContentText("Content");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManagerCompat.from(this).createNotificationChannel(new NotificationChannel("daemon", "daemon", NotificationManager.IMPORTANCE_DEFAULT));
            }
            startForeground(123456, builder.build());
        } catch (Exception e) {
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
