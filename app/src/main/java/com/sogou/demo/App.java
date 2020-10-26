package com.sogou.demo;

import android.app.Application;
import android.content.Context;

import com.bossy.KeepAlive;

public class App extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        KeepAlive.attachBaseContext(base, NotifyResidentService.class);
    }
}
