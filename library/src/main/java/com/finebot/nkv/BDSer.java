package com.finebot.nkv;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.finebot.R;

public class BDSer extends Service {
    public static final String EXTRA_FROM = "extra_from";
    private static Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundForService();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopForeground(true);
            }
        }, 500);
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForegroundForService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            Notification.Builder builder = new Notification.Builder(this, getPackageName());
            builder.setContentTitle("App");
            builder.setContentText("App is running");
            builder.setSmallIcon(R.drawable.ka_icon_trbg);
            Notification notification = builder.build();
            try {
                startForeground(getPackageName().hashCode(), notification);
            } catch (Exception e) {
            }
        }
    }

    public static void startKeepService(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= 26) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        try {
            NotificationChannel channel = new NotificationChannel(getPackageName(), getChannelName(), NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            channel.setDescription("android");
            channel.enableLights(false);
            channel.enableVibration(false);
            channel.setSound(null, null);
            nm.createNotificationChannel(channel);
        } catch (Exception e) {
        }
    }

    private String getChannelName() {
        return "Daemon Channel";
    }
}
