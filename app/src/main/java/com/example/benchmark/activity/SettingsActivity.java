/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.benchmark.R;
import com.example.benchmark.utils.TapUtil;

/**
 * SettingsActivity
 *
 * @version 1.0
 * @since 2023/3/7 15:05
 */
public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "TWT";
    Button num;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    private int testNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        num = findViewById(R.id.num);
        sharedPreferences = getSharedPreferences("Setting", this.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        testNum = sharedPreferences.getInt("testNum", 5);
        num.setText(String.valueOf(testNum));
        Log.e(TAG, "onCreate+num: " + num);
    }
    public void sub(View view) {
        Log.d(TAG, "sub");
        if (testNum == 1) {
            return;
        }
        testNum--;
        num.setText(String.valueOf(testNum));
    }
    public void add(View view) {
        Log.d(TAG, "add: add");
        testNum++;
        num.setText(String.valueOf(testNum));
    }
    public void save(View view) {
        TapUtil.mWholeMonitorNum = testNum;
        editor.putInt("testNum", testNum);
        editor.apply();
        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
    }

    public void back(View view) {
        onBackPressed();
    }
}