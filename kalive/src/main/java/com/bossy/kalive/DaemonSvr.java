package com.bossy.kalive;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.MessageQueue;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.bossy.kalive.log.Log;
import com.bossy.kalive.utils.Utils;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Keep;

/**
 * Created by Administrator on 2018/1/8.
 */

public class DaemonSvr extends Service {

    private static final int MSG_TASK_EXECUTE = Integer.parseInt("1000");
    private static final int DELAY_TASK_EXECUTE = Integer.parseInt("60000");
    public static final long ALARM_INTERVAL_TIME = 1 * 60 * 1000;
    private static Handler mHandler = new Handler();
    private static final Notification sNotification = new Notification();
    private Notification mLastNotification;
    private MediaPlayer mMediaPlayer;
    private static ExecutorService sExecutorService = Executors.newSingleThreadExecutor();

    @Override
    public IBinder onBind(Intent intent) {
        Log.iv(Log.TAG, "");
        return new Binder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundForService();
        Log.iv(Log.TAG, "");
        startOnePixel();
        bindService(new Intent(this, DaemonSvr.class), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.iv(Log.TAG, "");
                try {
                    service.linkToDeath(new IBinder.DeathRecipient() {
                        @Override
                        public void binderDied() {
                        }
                    }, 0);
                } catch (Exception e) {
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.iv(Log.TAG, "");
            }
        }, BIND_AUTO_CREATE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            foregroundService();
        }
        alarm();
    }

    private void startOnePixel() {
        DaemonPui.init(this);
    }

    private void alarm() {
        try {
            sExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        setAlarmInThread();
                    } catch (Exception e) {
                    }
                }
            });
        } catch (Exception e) {
        }
    }

    private void setAlarmInThread() {
        Intent alarmIntent = new Intent(this, DaemonSvr.class);
        alarmIntent.setAction(getAlarmAction(this));
        alarmIntent.setPackage(getPackageName());
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= 26) {
            pendingIntent = PendingIntent.getForegroundService(this, 8888, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pendingIntent = PendingIntent.getService(this, 8888, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
        long pendingTime = SystemClock.elapsedRealtime() + ALARM_INTERVAL_TIME;
        alarmMgr.set(AlarmManager.ELAPSED_REALTIME, pendingTime, pendingIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundForService();
        if (intent != null) {
            if (getAlarmAction(this).equals(intent.getAction())) {
                alarm();
                scheduleExecute(this, true);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startPlaySong();
        }
        return START_REDELIVER_INTENT;
    }

    private void startForegroundForService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = sNotification;
            createChannel();
            setChannel(notification, getPackageName());
            if (mLastNotification != notification) {
                try {
                    stopForeground(true);
                } catch (Exception e) {
                }
            }
            try {
                startForeground(getPackageName().hashCode(), notification);
            } catch (Exception e) {
            }
            mLastNotification = notification;
        }
    }

    private void setChannel(Notification notification, String channel) {
        if (notification == null || TextUtils.isEmpty(channel)) {
            return;
        }
        try {
            Class<?> clazz = Notification.class;
            Field field = clazz.getDeclaredField("mChannelId");
            if (field != null) {
                field.setAccessible(true);
                field.set(notification, channel);
                field.setAccessible(false);
            }
        } catch (Exception e) {
        } catch (Error e) {
        }
        try {
            notification.icon = getApplicationInfo().icon;
        } catch (Exception e) {
        } catch (Error e) {
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
        try {
            return getResources().getString(R.string.hbs_channel_name);
        } catch (Exception e) {
        }
        return "Default Channel";
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.iv(Log.TAG, "");
        setComponentEnable(this, false);
        startPService(this);

    }

    private static void setComponentEnable(Context context, boolean enable) {
        try {
            ComponentName cmp = new ComponentName(context, InternalReceiver.class.getName());
            int state = enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
            String className = "android.content.pm.PackageManager";
            String methodName2 = "setComponentEnabledSetting";
            Class<?>[] argType2 = new Class[]{ComponentName.class, int.class, int.class};
            Object[] argValue2 = new Object[]{cmp, state, PackageManager.DONT_KILL_APP};
            Utils.reflectCall(context.getPackageManager(), className, methodName2, argType2, argValue2);
        } catch (Exception e) {
            Log.e(Log.TAG, "error : " + e);
        }
    }

    public static void startPService(Context context) {
        Log.iv(Log.TAG, "");
        setComponentEnable(context, true);
        try {
            Intent service = new Intent(context, DaemonSvr.class);
            service.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(service);
            } else {
                context.startService(service);
            }
        } catch (Exception e) {
            Log.e(Log.TAG, "error : " + e);
        }
    }

    private void startPlaySong() {
        Log.iv(Log.TAG, "");
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.hbs_white_bg);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.start();
            } else {
                mMediaPlayer.start();
            }
        } catch (Exception e) {
        }
        if (mHandler == null) {
            return;
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cancelNotification();
                try {
                    if (mMediaPlayer != null) {
                        mMediaPlayer.pause();
                    }
                } catch (Exception e) {
                    Log.e(Log.TAG, "error : " + e);
                }
            }
        }, 500);

    }

    private void cancelNotification() {
        stopForeground(true);
    }

    //停止播放销毁对象
    private void stopPlaySong() {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlaySong();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(getNotificationId(this));
        }
        setComponentEnable(this, false);
        startPService(this);
    }

    private static void scheduleExecute(Context context, boolean fromAlarm) {
        Log.iv(Log.TAG, "fromAlarm : " + fromAlarm);
        if (mHandler.hasMessages(MSG_TASK_EXECUTE)) {
            Log.iv(Log.TAG, "task has executed");
            return;
        }
        mHandler.sendEmptyMessageDelayed(MSG_TASK_EXECUTE, DELAY_TASK_EXECUTE);
        try {
            updatePreference(context);
        } catch (Exception e) {
        }
    }

    private static void updatePreference(final Context context) {
        try {
            sExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        try {
                            PreferenceManager.getDefaultSharedPreferences(context)
                                    .edit()
                                    .putLong(Utils.string2MD5(context.getPackageName() + ".e90921ae"), System.currentTimeMillis())
                                    .apply();
                        } catch (Exception e) {
                        }
                    } catch (Exception e) {
                    }
                }
            });
        } catch (Exception e) {
        }
    }

    private static String getAlarmAction(Context context) {
        try {
            return context.getPackageName() + ".action.VIEW";
        } catch (Exception e) {
        }
        return Intent.ACTION_SEND + "_VIEW";
    }

    private static int getNotificationId(Context context) {
        try {
            return context.getPackageName().hashCode();
        } catch (Exception e) {
        }
        return DaemonSvr.class.hashCode();
    }

    private void foregroundService() {
        Log.iv(Log.TAG, "");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.drawable.hbs_ic_small_icon);
            builder.setContentTitle("basic");
            builder.setContentText("basic running");
            startForeground(getNotificationId(this), builder.build());
            Intent intent = new Intent(this, NSvr.class);
            startService(intent);
        } else {
            startForeground(getNotificationId(this), new Notification());
        }
    }

    public static class InternalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            startPService(context);
        }
    }

    public static class NSvr extends Service {

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                Notification.Builder builder = new Notification.Builder(this);
                builder.setSmallIcon(R.drawable.hbs_ic_small_icon);
                startForeground(DaemonSvr.getNotificationId(this), builder.build());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(1000);
                        stopForeground(true);
                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        manager.cancel(DaemonSvr.getNotificationId(NSvr.this));
                        stopSelf();
                    }
                }).start();
            }
            return super.onStartCommand(intent, flags, startId);
        }
    }

    @TargetApi(21)
    public static class JobSvr extends JobService {
        @Override
        public boolean onStartJob(JobParameters params) {
            Log.iv(Log.TAG, "");
            new Thread() {
                @Override
                public void run() {
                    doInnerJobService2(getBaseContext());
                }
            }.start();
            scheduleExecute(getApplication(), false);
            return false;
        }

        @Override
        public boolean onStopJob(JobParameters params) {
            Log.iv(Log.TAG, "");
            return false;
        }

        @TargetApi(21)
        private static void doInnerJobService2(Context context) {
            try {
                JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Service.JOB_SCHEDULER_SERVICE);
                JobInfo.Builder builder = new JobInfo.Builder(getJobId(context), new ComponentName(context, JobSvr.class));  //指定哪个JobService执行操作
                builder.setMinimumLatency(TimeUnit.MILLISECONDS.toMillis(60 * 1000)); //执行的最小延迟时间
                builder.setOverrideDeadline(TimeUnit.MILLISECONDS.toMillis(60 * 1000));  //执行的最长延时时间
                builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NOT_ROAMING);  //非漫游网络状态
                builder.setBackoffCriteria(TimeUnit.MINUTES.toMillis(10), JobInfo.BACKOFF_POLICY_LINEAR);  //线性重试方案
                builder.setRequiresCharging(false); // 未充电状态
                jobScheduler.schedule(builder.build());
            } catch (Exception e) {
                Log.e(Log.TAG, "error : " + e);
            }
        }
    }

    private static int getJobId(Context context) {
        try {
            return context.getPackageName().hashCode();
        } catch (Exception e) {
        }
        return 1;
    }


    /**
     * 初始化核心服务
     *
     * @param context
     */
    @Keep
    public static void initKeepALive(Context context) {
        startCoreService(context, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.iv(Log.TAG, "start job service");
            doJobService2(context);
        }
    }

    /**
     * 启动核心服务
     *
     * @param context
     * @param init
     */
    private static void startCoreService(final Context context, boolean init) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && init) {
            try {
                Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
                    @Override
                    public boolean queueIdle() {
                        DaemonSvr.startPService(context);
                        return false;
                    }
                });
            } catch (Exception | Error e) {
                DaemonSvr.startPService(context);
            }
        } else {
            DaemonSvr.startPService(context);
        }
    }

    @TargetApi(21)
    private static void doJobService2(Context context) {
        try {
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Service.JOB_SCHEDULER_SERVICE);
            JobInfo.Builder builder = new JobInfo.Builder(getJobId(context), new ComponentName(context, DaemonSvr.JobSvr.class));  //指定哪个JobService执行操作
            builder.setMinimumLatency(TimeUnit.MILLISECONDS.toMillis(60 * 1000)); //执行的最小延迟时间
            builder.setOverrideDeadline(TimeUnit.MILLISECONDS.toMillis(60 * 1000));  //执行的最长延时时间
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);  //非漫游网络状态
            builder.setBackoffCriteria(TimeUnit.MINUTES.toMillis(10), JobInfo.BACKOFF_POLICY_LINEAR);  //线性重试方案
            builder.setRequiresCharging(false); // 未充电状态
            jobScheduler.schedule(builder.build());
        } catch (Exception e) {
            Log.d(Log.TAG, "error : " + e);
        }
    }
}
