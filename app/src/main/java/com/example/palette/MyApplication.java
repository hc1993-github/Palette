package com.example.palette;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.example.palette.util.LogUtil;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MyApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        LogUtil.getInstance(this).start(LogUtil.LEVEL_ALL);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
//        LogUtil.getInstance(this).stop();
    }
}
