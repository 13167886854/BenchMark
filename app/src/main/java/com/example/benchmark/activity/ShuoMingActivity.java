/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.activity;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.benchmark.adapter.ShuoMingAdapter;
import com.example.benchmark.InitbenchMarkData.InitShuoming;
import com.example.benchmark.R;

/**
 * ShuoMingActivity
 *
 * @version 1.0
 * @since 2023/3/7 15:07
 */
public class ShuoMingActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        InitShuoming sm = new InitShuoming();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shuoming_activity);
        RecyclerView recyclerView = findViewById(R.id.shuoming_rv);
        ShuoMingAdapter adapter = new ShuoMingAdapter(ShuoMingActivity.this, sm.getList());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }
}

