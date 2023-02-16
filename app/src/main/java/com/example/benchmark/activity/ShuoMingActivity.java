package com.example.benchmark.activity;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.benchmark.adapter.ShuoMingAdapter;
import com.example.benchmark.InitbenchMarkData.InitShuoming;
import com.example.benchmark.R;

public class ShuoMingActivity extends Activity {
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        InitShuoming shuoming = new InitShuoming();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shuoming_activity);
        recyclerView = findViewById(R.id.shuoming_rv);
        ShuoMingAdapter adapter = new ShuoMingAdapter(ShuoMingActivity.this, shuoming.getList());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);

    }
}
