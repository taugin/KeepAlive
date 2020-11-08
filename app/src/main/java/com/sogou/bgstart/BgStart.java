package com.sogou.bgstart;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.sogou.daemon.demo.R;
import com.sogou.log.Log;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

public class BgStart {
    private static final List<String> sInterceptActivityBrands = Arrays.asList(new String[]{"vivo"});
    private static final String NOTIFICATION_TAG = "AA_TAG1";
    private static final int NOTIFICATION_ID = 10101;
    private static final String CHANNEL_ID = "sm_lkr_ntf_hl_pr_chn_id_7355608";
    private static WeakReference<Activity> sActivity;
    private static BroadcastReceiver sBroadcastReceiver;
    public static Handler sHandler = null;

    public static void init(Context context) {
        Log.v(Log.TAG, "Build.BRAND : " + Build.BRAND);
        NotificationUtils.init(context);
        registerScreen(context);
    }

    private static void registerScreen(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    if (TextUtils.equals(Intent.ACTION_SCREEN_ON, intent.getAction())) {
                        if (!isInterceptActivityInBg()) {
                            Log.v(Log.TAG, "isInterceptActivityInBg");
                            startBgActivity(context);
                        } else {
                            startActivityInBg(context, ScreenActivity.class);
                        }
                    }
                }
            }
        }, intentFilter);
    }

    private static void startBgActivity(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Intent activityIntent = new Intent(context, ScreenActivity.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);
        } else {
//            start2(context, "screen_off", ScreenActivity.class);
            notifyStart(context);
        }
    }

    private static void moveTaskToFront(Context context) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningTaskInfo next : activityManager.getRunningTasks(200)) {
                if (next.baseActivity.getPackageName().equals(context.getPackageName())) {
                    activityManager.moveTaskToFront(next.id, 0);
                    activityManager.moveTaskToFront(next.id, 0);
                    activityManager.moveTaskToFront(next.id, 0);
                    activityManager.moveTaskToFront(next.id, 0);
                    return;
                }
            }
        } catch (Exception e) {
            Log.e(Log.TAG, "error : " + e, e);
        }
    }

    private static void createNotificationChannel(Context context, NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= 26 && notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Weather", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("weather_remind_desc");
            notificationChannel.setLockscreenVisibility(-1);
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setShowBadge(false);
            notificationChannel.setSound((Uri) null, (AudioAttributes) null);
            notificationChannel.setBypassDnd(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private static void showNotification(Context context, PendingIntent pendingIntent) {
        try {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            createNotificationChannel(context, notificationManager);
            notificationManager.cancel(NOTIFICATION_TAG, NOTIFICATION_ID);
            notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID,
                    new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(context.getApplicationInfo().icon)
                            .setFullScreenIntent(pendingIntent, true)
                            .setCustomHeadsUpContentView(new RemoteViews(context.getPackageName(), R.layout.locker_layout_heads_up)).build());
            enableHandler(context);
            sHandler.removeMessages(101);
            sHandler.sendEmptyMessageDelayed(101, 1000);
        } catch (Exception e) {
            Log.e(Log.TAG, "error : " + e, e);
        }
    }

    private static class MyHandler extends Handler {
        private Context mContext;

        public MyHandler(Context context) {
            super(Looper.getMainLooper());
            mContext = context;
        }

        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 101) {
                NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(NOTIFICATION_TAG, NOTIFICATION_ID);
            }
        }
    }

    private static void notifyStart(final Context context) {
        Log.v(Log.TAG, "notifyStart111");
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService("keyguard");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.v(Log.TAG, "notifyStart222");
            Intent intent = new Intent(context, ScreenActivity.class);
            intent.setFlags(268435456);
            NotificationCompat.Builder fullScreenIntent = new NotificationCompat.Builder(context, "101").setSmallIcon(R.drawable.ad_close).setContentTitle("").setContentText("").setPriority(1).setFullScreenIntent(PendingIntent.getActivity(context, 0, intent, 0), true);
            NotificationChannel notificationChannel = new NotificationChannel("101", "lock_screen", 4);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(R.drawable.ad_close, fullScreenIntent.build());
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(R.drawable.ad_close);

                }
            }, 500);
        }
        Intent intent = new Intent(context, ScreenActivity.class);
        intent.addFlags(268435456);
        context.startActivity(intent);
    }

    private static void start2(Context context, String str, Class<?> clazz) {
        Log.v(Log.TAG, "start2");
        Intent intent = new Intent(context.getApplicationContext(), clazz);
        intent.setAction("inner_action");
        intent.putExtra("extra_from", str);
        Log.v(Log.TAG, "str : " + str);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        boolean startSuccess = false;
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 10102, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            pendingIntent.send();
            startSuccess = true;
        } catch (Exception e) {
            Log.e(Log.TAG, "error : " + e, e);
        }
        if (!startSuccess) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                Log.v(Log.TAG, "");
                context.getApplicationContext().startActivity(intent);
            } catch (Exception e) {
                Log.e(Log.TAG, "error : " + e, e);
            }
        }
        NotificationUtils.showNotification(context, pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startWithAlarm(context, intent, 200);
        }
    }

    private static void startActivityInBg2(Context context, String str, Class cls) {
        Log.v(Log.TAG, "clazz : " + cls);
        Context applicationContext = context.getApplicationContext();
        Intent intent = new Intent(applicationContext, cls);
        intent.setAction("inner_action");
        intent.putExtra("extra_from", str);
        startWithAlarm(applicationContext, intent, 200);
    }

    private static void startWithAlarm(Context context, Intent intent, int i2) {
        Log.v(Log.TAG, "i2 : " + i2);
        PendingIntent activity = PendingIntent.getActivity(context, 10102, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ((long) i2), activity);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(Log.TAG, "error : " + e);
        }
    }

    public static void startActivityInBg(final Context context, final Class<?> clazz) {
        Log.v(Log.TAG, "Build.VERSION.SDK_INT : " + Build.VERSION.SDK_INT);
        moveTaskToFront(context);
        enableHandler(context);
        sHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, clazz);
                intent.setAction("inner_action");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                boolean startSuccess = true;
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 10102, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                try {
                    pendingIntent.send();
                } catch (Exception e) {
                    Log.e(Log.TAG, "error : " + e, e);
                    startSuccess = false;
                }
                if (!startSuccess) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        Log.v(Log.TAG, "");
                        context.startActivity(intent);
                    } catch (Exception unused2) {
                        Log.e(Log.TAG, "error : " + unused2);
                    }
                }
                showNotification(context, pendingIntent);
            }
        }, 100);
    }

    public static void onBgActivityStart(Context context) {
        if (!isInterceptActivityInBg()) {
            return;
        }
        try {
            Intent intent = new Intent(context.getPackageName() + ".action.MOVE_BACK");
            intent.setPackage(context.getPackageName());
            context.sendBroadcast(intent);
        } catch (Exception e) {
        }
    }

    public static void onStartMainActivity(Activity activity) {
        if (!isInterceptActivityInBg()) {
            return;
        }
        sActivity = new WeakReference<Activity>(activity);
        IntentFilter filter = new IntentFilter();
        filter.addAction(activity.getPackageName() + ".action.MOVE_BACK");
        try {
            if (sBroadcastReceiver != null) {
                activity.unregisterReceiver(sBroadcastReceiver);
            }
        } catch (Exception e) {
        }
        sBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (sActivity != null && sActivity.get() != null) {
                    sActivity.get().moveTaskToBack(false);
                }
            }
        };
        try {
            activity.registerReceiver(sBroadcastReceiver, filter);
        } catch (Exception e) {
        }
    }

    public static boolean onBackPressed(final Activity activity) {
        if (!isInterceptActivityInBg()) {
            return false;
        }
        enableHandler(activity);
        sHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (activity != null && !activity.isFinishing()) {
                    Log.v(Log.TAG, "moveTaskToBack");
                    activity.moveTaskToBack(false);
                }
            }
        }, 1000);
        return true;
    }

    public static boolean isInterceptActivityInBg() {
        return sInterceptActivityBrands.contains(Build.BRAND);
    }

    private static void enableHandler(Context context) {
        if (sHandler == null) {
            sHandler = new MyHandler(context);
        }
    }
}
