package com.sogou.bgstart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.sogou.log.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018-10-16.
 */

public class ScreenActivity extends Activity {

    private Handler mHandler = null;
    private ViewGroup mLockAdLayout;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BgStart.onBgActivityStart(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        try {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        } catch (Exception e) {
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setFinishOnTouchOutside(false);
        }
        mHandler = new Handler();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        updateDataAndView();
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        try {
            super.setRequestedOrientation(requestedOrientation);
        } catch (Exception | Error e) {
            Log.e(Log.TAG, "error : " + e);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        updateDataAndView();
    }

    private void updateFullScreenState() {
        try {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    WindowManager.LayoutParams attributes = window.getAttributes();
                    window.setAttributes(attributes);
                    window.setStatusBarColor(Color.TRANSPARENT);
                    try {
                        if (isLockView()) {
                            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
                            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
                            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                        }
                    } catch (Exception | Error e) {
                    }
                } else {
                    Window window = getWindow();
                    WindowManager.LayoutParams attributes = window.getAttributes();
                    attributes.flags |= flagTranslucentStatus;
                    window.setAttributes(attributes);
                }
            }
        } catch (Exception | Error e) {
        }
    }

    private void updateDataAndView() {
        updateFullScreenState();
        showLockScreenView();
    }

    @Override
    public void onBackPressed() {
        if (isLockView()) {
            return;
        }
        super.onBackPressed();
    }

    private void finishActivityWithDelay() {
        finishActivityWithDelay(500);
    }

    private void finishActivityWithDelay(final int delay) {
        if (mHandler != null) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fa();
                }
            }, delay);
        } else {
            fa();
        }
    }

    private void fa() {
        try {
            finish();
        } catch (Exception e) {
        } catch (Error e) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimeUpdate();
    }

    /////////////////////////////////////////////////////////////////////////////////

    protected Drawable getBackgroudDrawable() {
        return null;
    }

    protected int getBackgroundColor() {
        return Color.parseColor("#FF699CFF");
    }

    private boolean isLockView() {
        return true;
    }

    /**
     * 展示锁屏界面
     */
    private TextView mTimeTextView;
    private TextView mWeekTextView;
    private BroadcastReceiver mTimeReceiver;

    /**
     * 创建ViewPager，异常时返回空值
     *
     * @return
     */
    private ViewPager createViewPager() {
        try {
            ViewPager viewPager = new ViewPager(this);
            ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    if (position == 0) {
                        fa();
                        overridePendingTransition(0, 0);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            };
            try {
                viewPager.setOffscreenPageLimit(2);
            } catch (Exception | Error e) {
                Log.e(Log.TAG, "error : " + e);
            }
            try {
                viewPager.addOnPageChangeListener(listener);
            } catch (Exception | Error e) {
                try {
                    viewPager.setOnPageChangeListener(listener);
                } catch (Exception | Error error) {
                    Log.e(Log.TAG, "error : " + e);
                    return null;
                }
            }
            return viewPager;
        } catch (Exception | Error e) {
            Log.e(Log.TAG, "error : " + e);
        }
        return null;
    }

    private void showLockScreenView() {
        Log.v(Log.TAG, "show ls view");

        // 1，create Activity layout
        LinearLayout layout = new LinearLayout(this);
        super.setContentView(layout);

        layout.setOrientation(LinearLayout.VERTICAL);

        // 2，create Ad Pager layout
        LinearLayout pagerLayout = new LinearLayout(this);
        pagerLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        pagerLayout.setOrientation(LinearLayout.VERTICAL);
        Drawable drawable = getBackgroudDrawable();
        if (drawable == null) {
            try {
                WallpaperManager wm = WallpaperManager.getInstance(this);
                drawable = wm.getDrawable();
            } catch (Exception e) {
//                Log.e(Log.TAG, "error : " + e);
            }
        }
        if (drawable == null) {
            layout.setBackgroundColor(getBackgroundColor());
        } else {
            layout.setBackground(drawable);
        }

        // 2.1，create TimeView
        mTimeTextView = new TextView(this);
        mTimeTextView.setTextColor(Color.WHITE);
        mTimeTextView.setGravity(Gravity.CENTER);
        mTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 72);
        mTimeTextView.setPadding(0, Utils.dp2px(this, 24f), 0, Utils.dp2px(this, 6f));
        LinearLayout.LayoutParams timeViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        timeViewParams.weight = 0;
        timeViewParams.topMargin = Utils.dp2px(this, 24f);
        pagerLayout.addView(mTimeTextView, timeViewParams);

        // 2.2，create WeekView
        mWeekTextView = new TextView(this);
        mWeekTextView.setTextColor(Color.WHITE);
        mWeekTextView.setGravity(Gravity.CENTER);
        mWeekTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        mWeekTextView.setPadding(0, Utils.dp2px(this, 0f), 0, Utils.dp2px(this, 30f));
        LinearLayout.LayoutParams weekViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        weekViewParams.weight = 0;
        pagerLayout.addView(mWeekTextView, weekViewParams);

        // 2.3，create Ad Layout
        RelativeLayout adLayout = new RelativeLayout(this);
        adLayout.setGravity(Gravity.CENTER);
        adLayout.setPadding(Utils.dp2px(this, 8f), 0, Utils.dp2px(this, 8f), 0);
        mLockAdLayout = adLayout;
        LinearLayout.LayoutParams adLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        adLayoutParams.weight = 1;
        pagerLayout.addView(mLockAdLayout, adLayoutParams);

        // 2.4，create Scroll View
        TextView slideView = new TextView(this);
        slideView.setText("Slide To Lock >>");
        slideView.setTextColor(Color.WHITE);
        TextPaint tp = slideView.getPaint();
        if (tp != null) {
            tp.setFakeBoldText(true);
        }
        slideView.setGravity(Gravity.CENTER);
        slideView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        LinearLayout.LayoutParams slideViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        slideViewParams.weight = 0;
        slideViewParams.bottomMargin = Utils.dp2px(this, 16f);
        slideViewParams.topMargin = Utils.dp2px(this, 16f);
        pagerLayout.addView(slideView, slideViewParams);

        View tempLayout = null;
        // 3，create ViewPager, set ViewPager Adapter
        try {
            ViewPager viewPager = createViewPager();
            LsViewPagerAdapter adapter = new LsViewPagerAdapter(pagerLayout);
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(1);
            tempLayout = viewPager;
        } catch (Exception | Error e) {
            Log.e(Log.TAG, "error : " + e);
        }

        // 5，add ViewPager to Activity layout
        layout.addView(tempLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        startTimeUpdate();
    }

    private void startTimeUpdate() {
        updateTime();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);

        mTimeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_TIME_TICK)) {
                    updateTime();
                }
            }
        };

        registerReceiver(mTimeReceiver, filter);
    }

    private void updateTime() {
        Date date = new Date();
        mTimeTextView.setText(new SimpleDateFormat("H:mm").format(date));
        mWeekTextView.setText(new SimpleDateFormat("yyyy/MM/dd  EEE").format(date));
    }

    private void stopTimeUpdate() {
        try {
            if (mTimeReceiver != null) {
                unregisterReceiver(mTimeReceiver);
            }
        } catch (Exception | Error e) {
            Log.e(Log.TAG, "error : " + e, e);
        }
    }

    public static void hideNavigationBar(Activity activity) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) { // lower api
                View v = activity.getWindow().getDecorView();
                v.setSystemUiVisibility(View.GONE);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                View decorView = activity.getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                decorView.setSystemUiVisibility(uiOptions);
            }
        } catch (Exception | Error e) {
            Log.e(Log.TAG, "error : " + e, e);
        }
    }

    private class LsViewPagerAdapter extends PagerAdapter {

        private ViewGroup mAdView;

        public LsViewPagerAdapter(ViewGroup adView) {
            mAdView = adView;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (position == 0) {
                View view = new View(container.getContext());
                view.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                return view;
            } else if (position == 1) {
                container.addView(mAdView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                return mAdView;
            }
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (object instanceof View) {
                container.removeView((View) object);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

}