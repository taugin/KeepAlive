package com.bossy.kalive;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Keep;

import com.bossy.kalive.log.Log;
import com.bossy.kalive.utils.Utils;


/**
 * Created by Administrator on 2018/1/18.
 */

public class DaemonPui extends Activity {
    private BroadcastReceiver mFinishReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置1像素
        Window window = getWindow();
        window.setGravity(Gravity.TOP | Gravity.LEFT);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        params.height = dm.heightPixels;
        params.width = dm.widthPixels;
        window.setAttributes(params);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        //结束该页面的广播
        mFinishReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                unregister();
                finish();
            }
        };
        unregister();
        register();
        //检查屏幕状态
        checkScreen();
    }

    private void register() {
        try {
            registerReceiver(mFinishReceiver, new IntentFilter(getPackageName() + ".FINISH"));
        } catch (Exception e) {
        }
    }

    private void unregister() {
        try {
            unregisterReceiver(mFinishReceiver);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkScreen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregister();
    }

    /**
     * 检查屏幕状态  isScreenOn为true  屏幕“亮”结束该Activity
     */
    private void checkScreen() {
        try {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = pm.isScreenOn();
            if (isScreenOn) {
                finish();
            }
        } catch (Exception e) {
        }
    }

    @Keep
    public static void init(Context context) {
        Log.iv(Log.TAG, "");
        ScreenReceiver screenReceiver = new ScreenReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        context.registerReceiver(screenReceiver, intentFilter);
    }

    public static class ScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (context == null || intent == null) {
                return;
            }
            if (Utils.isEnableOnePixel()) {
                if (TextUtils.equals(intent.getAction(), Intent.ACTION_SCREEN_OFF)) {
                    Intent it = new Intent(context, DaemonPui.class);
                    it.setPackage(context.getPackageName());
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    it.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    try {
                        context.startActivity(it);
                    } catch (Exception e) {
                        Log.e(Log.TAG, "error : " + e);
                    }
                } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_SCREEN_ON)
                        || TextUtils.equals(intent.getAction(), Intent.ACTION_USER_PRESENT)) {
                    try {
                        context.sendBroadcast(new Intent(context.getPackageName() + ".FINISH"));
                    } catch (Exception e) {
                    }
                }
            }
        }
    }
}
