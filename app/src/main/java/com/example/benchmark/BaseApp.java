/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.example.benchmark.utils.CacheUtil;

/**
 * BaseApp
 *
 * @version 1.0
 * @since 2023/3/7 17:29
 */
public class BaseApp extends Application {
    /** context */
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static Context getContext() {
        return context;
    }

    /**
     * onCreate
     *
     * @date 2023/3/8 15:24
     */
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        CacheUtil.init(this);
    }
}

