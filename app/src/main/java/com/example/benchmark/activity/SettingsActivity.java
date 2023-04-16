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
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.benchmark.R;
import com.example.benchmark.data.SettingData;
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

    private EditText serverIp1;
    private EditText serverIp2;
    private EditText serverIp3;
    private EditText serverIp4;
    private EditText serverPort;
    private SettingData settingData;

    /**
     * onCreate
     *
     * @param savedInstanceState description
     * @return void
     * @date 2023/3/9 19:50
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        settingData = SettingData.getInstance();

        num = findViewById(R.id.num);
        serverIp1 = findViewById(R.id.serverIp1);
        serverIp2 = findViewById(R.id.serverIp2);
        serverIp3 = findViewById(R.id.serverIp3);
        serverIp4 = findViewById(R.id.serverIp4);
        serverPort = findViewById(R.id.serverPort);
        sharedPreferences = getSharedPreferences("Setting", this.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        settingData.setStabilityTestNum(sharedPreferences.getInt("stabilityTestNum",
                settingData.getStabilityTestNum()));
        settingData.setServerIp1(sharedPreferences.getString("serverIp1", settingData.getServerIp1()));
        settingData.setServerIp2(sharedPreferences.getString("serverIp2", settingData.getServerIp2()));
        settingData.setServerIp3(sharedPreferences.getString("serverIp3", settingData.getServerIp3()));
        settingData.setServerIp4(sharedPreferences.getString("serverIp4", settingData.getServerIp4()));
        settingData.setServerPort(sharedPreferences.getString("serverPort", settingData.getServerPort()));
        num.setText(String.valueOf(settingData.getStabilityTestNum()));
        serverIp1.setText(settingData.getServerIp1());
        serverIp2.setText(settingData.getServerIp2());
        serverIp3.setText(settingData.getServerIp3());
        serverIp4.setText(settingData.getServerIp4());
        serverPort.setText(settingData.getServerPort());
    }

    /**
     * sub
     *
     * @param view description
     * @return void
     * @date 2023/3/9 19:50
     */
    public void sub(View view) {
        Log.d(TAG, "sub");
        if (settingData.getStabilityTestNum() == 1) {
            return;
        }
        settingData.setStabilityTestNum(settingData.getStabilityTestNum() - 1);
        num.setText(String.valueOf(settingData.getStabilityTestNum()));
    }

    /**
     * add
     *
     * @param view description
     * @return void
     * @date 2023/3/9 19:50
     */
    public void add(View view) {
        Log.d(TAG, "add: add");
        settingData.setStabilityTestNum(settingData.getStabilityTestNum() + 1);
        num.setText(String.valueOf(settingData.getStabilityTestNum()));
    }

    /**
     * save
     *
     * @param view description
     * @return void
     * @date 2023/3/9 19:50
     */
    public void save(View view) {
        TapUtil.getUtil().setmWholeMonitorNum(settingData.getStabilityTestNum());
        settingData.setServerIp1(serverIp1.getText().toString());
        settingData.setServerIp2(serverIp2.getText().toString());
        settingData.setServerIp3(serverIp3.getText().toString());
        settingData.setServerIp4(serverIp4.getText().toString());
        settingData.setServerPort(serverPort.getText().toString());
        editor.putInt("stabilityTestNum", settingData.getStabilityTestNum());
        editor.putString("serverIp1", settingData.getServerIp1());
        editor.putString("serverIp2", settingData.getServerIp2());
        editor.putString("serverIp3", settingData.getServerIp3());
        editor.putString("serverIp4", settingData.getServerIp4());
        editor.putString("serverPort", settingData.getServerPort());
        editor.apply();
        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
    }

    /**
     * back
     *
     * @param view description
     * @return void
     * @date 2023/3/9 19:50
     */
    public void back(View view) {
        onBackPressed();
    }
}