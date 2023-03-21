/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.example.benchmark.BaseApp;
import com.example.benchmark.R;
import com.example.benchmark.utils.AccessUtils;
import com.example.benchmark.utils.AutoStartUtil;

/**
 * PopDiaLog
 *
 * @version 1.0
 * @since 2023/3/7 15:14
 */
public class PopDiaLog extends Dialog implements View.OnClickListener {
    private Button queRen;
    private Button quXiao;
    private Context context;
    private RelativeLayout inability;
    private RelativeLayout ziDoing;
    private RelativeLayout houTai;
    private CheckBox ibilityCheak;
    private CheckBox houTaiCheak;
    private AccessUtils accessUtils;

    /**
     * PopDiaLog
     *
     * @param context description
     * @return
     * @throws null
     * @description: PopDiaLog
     * @date 2023/3/2 10:12
     */
    public PopDiaLog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.access_popwindow);
        initView();
        boolean isAccessibilityServiceOpen = accessUtils.isAccessibilityServiceOpen();
        if (isAccessibilityServiceOpen) {
            ibilityCheak.setChecked(true);
        } else {
            ibilityCheak.setChecked(false);
        }
        if (accessUtils.isIgnoringBatteryOptimizations()) {
            houTaiCheak.setChecked(true);
        } else {
            houTaiCheak.setChecked(false);
        }
        inability.setOnClickListener(this::onClick);
        ziDoing.setOnClickListener(this::onClick);
        houTai.setOnClickListener(this::onClick);
        quXiao.setOnClickListener(this::onClick);
        queRen.setOnClickListener(this::onClick);
        setCanceledOnTouchOutside(false);
    }

    /**
     * onClick
     *
     * @param view description
     * @return void
     * @date 2023/3/9 19:47
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.access_ibility: {
                accessUtils.openAccessibilityService();
                dismiss();
                break;
            }
            case R.id.access_ziqidong: {
                AutoStartUtil.openStart(BaseApp.getContext());
                dismiss();
                break;
            }
            case R.id.access_houtai: {
                accessUtils.requestIgnoreBatteryOptimizations();
                dismiss();
                break;
            }
            case R.id.dialog_queren: {
                dismiss();
                break;
            }
            case R.id.dialog_quxiao: {
                dismiss();
            }
        }
    }

    private void initView() {
        inability = findViewById(R.id.access_ibility);
        ziDoing = findViewById(R.id.access_ziqidong);
        houTai = findViewById(R.id.access_houtai);
        ibilityCheak = findViewById(R.id.access_ibility_cheak);
        houTaiCheak = findViewById(R.id.access_houtai_cheak);
        accessUtils = new AccessUtils(context);
        queRen = findViewById(R.id.dialog_queren);
        quXiao = findViewById(R.id.dialog_quxiao);
    }
}
