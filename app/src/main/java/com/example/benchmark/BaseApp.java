package com.example.benchmark;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.example.benchmark.utils.CacheUtil;

public class BaseApp extends Application {

    @SuppressLint("StaticFieldLeak")
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        CacheUtil.init(this);
    }
}

