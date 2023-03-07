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
    private int TestNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        num = findViewById(R.id.num);
        sharedPreferences = getSharedPreferences("Setting", this.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        TestNum = sharedPreferences.getInt("TestNum", 5);
        num.setText(String.valueOf(TestNum));
        Log.e(TAG, "onCreate+num: " + num);


    }


    public void sub(View view) {
        Log.d(TAG, "sub");
        if (TestNum == 1) {
            return;
        }
        TestNum--;
        num.setText(String.valueOf(TestNum));
    }


    public void add(View view) {
        Log.d(TAG, "add: add");
        TestNum++;
        num.setText(String.valueOf(TestNum));

    }

    public void save(View view) {
        TapUtil.mWholeMonitorNum = TestNum;
        editor.putInt("TestNum", TestNum);
        editor.apply();
        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
    }

    public void back(View view) {
        onBackPressed();
    }
}