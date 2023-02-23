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
 * @version 1.0
 * @description 说明页面
 * @time 2022/6/14 15:30
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

