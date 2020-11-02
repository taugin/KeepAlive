package com.sogou.bgstart;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.sogou.daemon.demo.R;
import com.sogou.log.Log;

import java.util.concurrent.TimeUnit;

/* compiled from: filemagic */
public class ao {
    public static final long a = TimeUnit.SECONDS.toMillis(1);
    public static final Handler sHandler = new MyHandler(Looper.getMainLooper());
    private static final int MEDIA_INFO_PLAY_TO_END = 10101;
    private static Context sContext;

    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }

    /* compiled from: filemagic */
    private static class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 101) {
                ao.cancelNotification((NotificationManager) sContext.getSystemService(Context.NOTIFICATION_SERVICE));
            }
        }
    }

    public static void cancel(Context context) {
        cancelNotification((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
    }

    public static void createNotificationChannel(Context context, NotificationManager notificationManager) {
        try {
            if (Build.VERSION.SDK_INT >= 26 && notificationManager.getNotificationChannel("sm_lkr_ntf_hl_pr_chn_id_7355608") == null) {
                NotificationChannel notificationChannel = new NotificationChannel("sm_lkr_ntf_hl_pr_chn_id_7355608", context.getString(R.string.throne_weather_title), NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription(context.getString(R.string.weather_remind_desc));
                notificationChannel.setLockscreenVisibility(-1);
                notificationChannel.enableLights(false);
                notificationChannel.enableVibration(false);
                notificationChannel.setShowBadge(false);
                notificationChannel.setSound((Uri) null, (AudioAttributes) null);
                notificationChannel.setBypassDnd(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        } catch (Exception e) {
            Log.e(Log.TAG, "error : " + e);
        }
    }

    public static void showNotification(Context context, PendingIntent pendingIntent) {
        try {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            createNotificationChannel(context, notificationManager);
            notificationManager.cancel("AA_TAG1", MEDIA_INFO_PLAY_TO_END);
            notificationManager.notify("AA_TAG1", MEDIA_INFO_PLAY_TO_END,
                    new NotificationCompat.Builder(context, "sm_lkr_ntf_hl_pr_chn_id_7355608")
                            .setSmallIcon(R.drawable.ad_close)
                            .setFullScreenIntent(pendingIntent, true)
                            .setCustomHeadsUpContentView(new RemoteViews(context.getPackageName(), R.layout.locker_layout_heads_up)).build());
            sHandler.removeMessages(101);
            sHandler.sendEmptyMessageDelayed(101, a);
        } catch (Exception e) {
            Log.e(Log.TAG, "error : " + e);
        }
    }

    public static void cancelNotification(NotificationManager notificationManager) {
        try {
            notificationManager.cancel("AA_TAG1", MEDIA_INFO_PLAY_TO_END);
        } catch (Exception e) {
            Log.e(Log.TAG, "error : " + e);
        }
    }
}