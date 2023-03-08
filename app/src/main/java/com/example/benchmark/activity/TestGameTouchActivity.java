/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.benchmark.R;
import com.example.benchmark.utils.GameTouchUtil;
import com.example.benchmark.utils.ScoreUtil;

/**
 * TestGameTouchActivity
 *
 * @version 1.0
 * @since 2023/3/7 15:07
 */
public class TestGameTouchActivity extends AppCompatActivity {
    private static final String TAG = "TWT";

    private GameTouchUtil gameTouchUtil = GameTouchUtil.getGameTouchUtil();
    private final int HandlingFrame = 111;
    private final int TestCompleted = 222;
    private int lastRgb = 0;
    private int thisRgb = 0;
    private long count = 0L;
    private long duration = 0L;
    private Bitmap bitmap;
    private String path;
    private TextView handlingTv;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HandlingFrame:
                    handlingTv.setText("测试中----(" + msg.arg1 + "/" + count + ")");
                    break;
                case TestCompleted:
                    try {
                        float delayTime = gameTouchUtil.getAvgTime(GameTouchUtil.testNum);
                        handlingTv.setText(gameTouchUtil.getDelayTime() + "\navgTime:" + delayTime);
                        ScoreUtil.calaAndSaveGameTouchScores(gameTouchUtil.getDetectNum(), delayTime);
                        Log.d(TAG, "gameTouchUtil.getDetectNum(): " + gameTouchUtil.getDetectNum());
                        Log.d(TAG, "delayTime): " + delayTime);
                    } catch (Exception e) {
                        Log.d(TAG, "Error:" + e.toString());
                        Toast.makeText(TestGameTouchActivity.this, "检测到游戏中点击次数少于测试次数.", Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent(TestGameTouchActivity.this, CePingActivity.class);
                    intent.putExtra("isGameTouchTested", true);
                    startActivity(intent);
            }
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_game_touch);
        handlingTv = findViewById(R.id.handling);
        String[] permissions = {
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.READ_EXTERNAL_STORAGE"};
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(permissions, 1);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                path = getIntent().getStringExtra("path");
                retriever.setDataSource(path);
                String countS = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT);
                String durationS = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                Log.d(TAG, "durationS: " + durationS);
                count = Long.valueOf(countS);
                duration = Long.valueOf(durationS);
                float frameTime = (float) duration / count;
                Bitmap bitmap1 = retriever.getFrameAtIndex(0);
                if (bitmap1.getWidth() > bitmap1.getHeight()) {
                    thisRgb = bitmap1.getPixel(1060, 830);
                    lastRgb = thisRgb;
                    for (int i = 1; i < count; i++) {
                        bitmap = retriever.getFrameAtIndex(i);
                        thisRgb = bitmap.getPixel(1060, 830);
                        Log.d(TAG, "Image: <" + i + "> :" + thisRgb);
                        Message mes = new Message();
                        mes.arg1 = (i + 1);
                        mes.what = HandlingFrame;
                        handler.sendMessage(mes);
                        if (Math.abs(thisRgb - lastRgb) > 10000000) {
                            Log.e(TAG, "rgb changed!!! : " + i);
                            gameTouchUtil.getUpdateTime(gameTouchUtil.getVideoStartTime() + (long) (duration * ((float) (i) / count)));
                            gameTouchUtil.getTestTime((long) (duration * ((float) (i) / count)));
                        }
                        lastRgb = thisRgb;
                    }
                } else {
                    thisRgb = bitmap1.getPixel(1060, 830);
                    lastRgb = thisRgb;
                    for (int i = 1; i < count; i++) {
                        bitmap = retriever.getFrameAtIndex(i);
                        thisRgb = bitmap.getPixel(1060, 830);
                        Log.d(TAG, "Image: <" + i + "> :" + thisRgb);
                        Message mes = new Message();
                        mes.arg1 = (i + 1);
                        mes.what = HandlingFrame;
                        handler.sendMessage(mes);

                        if (Math.abs(thisRgb - lastRgb) > 10000000) {
                            Log.e(TAG, "rgb changed!!! : " + i);
                            gameTouchUtil.getUpdateTime(gameTouchUtil.getVideoStartTime() + (long) (duration * ((float) (i) / count)));
                            gameTouchUtil.getTestTime((long) (duration * ((float) (i) / count)));
                        }
                        lastRgb = thisRgb;
                    }
                }
                gameTouchUtil.printTestTime();
                handler.sendEmptyMessage(TestCompleted);
            }
        }).start();
    }
}