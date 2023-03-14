/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.benchmark.R;
import com.example.benchmark.utils.TapUtil;

/**
 * StartActivity
 *
 * @version 1.0
 * @since 2023/3/7 15:07
 */
public class StartActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        SharedPreferences sharedPreferences = getSharedPreferences("Setting", this.MODE_PRIVATE);
        int num = sharedPreferences.getInt("TestNum", 5);
        TapUtil.getUtil().setmWholeMonitorNum(num);

        // 去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);
        new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                return true;
            }
        }).sendEmptyMessageDelayed(1, 1000);
    }
}
