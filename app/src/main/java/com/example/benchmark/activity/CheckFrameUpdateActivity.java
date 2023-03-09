/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.benchmark.R;
import com.example.benchmark.utils.CheckVedioUpdateFrameRenderer;
import com.example.benchmark.utils.GameTouchUtil;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * CheckFrameUpdateActivity
 *
 * @version 1.0
 * @since 2023/3/7 15:05
 */
public class CheckFrameUpdateActivity extends AppCompatActivity {
    private GLSurfaceView glView;
    private CheckVedioUpdateFrameRenderer glVideoRenderer;

    private GameTouchUtil gameTouchUtil = GameTouchUtil.getGameTouchUtil();

    private TextView textInfo;

    private int fileRequestCode = 50;

    private Context mContext = this;
    private String path = "";

    // 百分比显示
    private DecimalFormat df = new DecimalFormat("0.00%");

    // SharePreferences测试结果保存
    private String storeName = "LastTestResult";

    // 判断是否正在测试
    private boolean isTesting = false;

    /**
     * start
     *
     * @param context description
     * @param path    description
     * @return void
     * @date 2023/3/9 19:52
     */
    public static void start(Context context, String path) {
        Intent intent = new Intent(context, CheckFrameUpdateActivity.class);
        intent.putExtra("path", path);
        context.startActivity(intent);
    }

    /**
     * onCreate
     *
     * @param savedInstanceState description
     * @return void
     * @date 2023/3/9 19:52
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkframeupdate);

        // 获取显示文本
        textInfo = findViewById(R.id.textview);

        // 初始化视频播放器
        glView = findViewById(R.id.play_surface_view);
        glView.setEGLContextClientVersion(2);
        glVideoRenderer = new CheckVedioUpdateFrameRenderer(this); // 创建renderer
        glView.setRenderer(glVideoRenderer); // 设置renderer
        glVideoRenderer.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d("TWT", "onCompletion: 播放结束");
                try {
                    stopTest();
                } catch (IOException ex) {
                    Log.e("CheckFrameUpdate", ex.toString());
                }
            }
        });
        path = getIntent().getStringExtra("path");
        try {
            glVideoRenderer.getMediaPlayer().reset();
            glVideoRenderer.getMediaPlayer().setDataSource(CheckFrameUpdateActivity.this, Uri.parse(path));
            glVideoRenderer.getMediaPlayer().prepare();
        } catch (IOException ex) {
            Log.e("CheckFrameUpdate", ex.toString());
        }

        // 自动开始
        try {
            doTest();
        } catch (IOException ex) {
            Log.e("CheckFrameUpdate", ex.toString());
        }
    }

    /**
     * doTest
     *
     * @return void
     * @date 2023/3/9 19:52
     */
    public void doTest() throws IOException {
        isTesting = true;
        Log.d("TWT", "doTest: 开始播放");
        Log.d("TWT", "doTest: 开始播放时间戳:" + System.currentTimeMillis());
        glVideoRenderer.getMediaPlayer().start();
    }

    /**
     * stopTest
     *
     * @return void
     * @date 2023/3/9 19:52
     */
    public void stopTest() throws IOException {
        isTesting = false;
        glVideoRenderer.getMediaPlayer().stop();
        glVideoRenderer.getMediaPlayer().prepare();

        gameTouchUtil.printFrameUpdateTime();
        gameTouchUtil.printAutoTapTime();
        gameTouchUtil.clear();

        Intent intent = new Intent(CheckFrameUpdateActivity.this, CePingActivity.class);
        intent.putExtra("isFluencyUntested", false);
        startActivity(intent);
    }

    /**
     * onPause
     *
     * @return void
     * @date 2023/3/9 19:52
     */
    @Override
    protected void onPause() {
        super.onPause();

        // 判断是否正在测试
        if (isTesting) {
            glVideoRenderer.getMediaPlayer().stop();
            try {
                stopTest();
            } catch (IOException ex) {
                Log.e("CheckFrameUpdate", ex.toString());
            }
        }
    }

    /**
     * onResume
     *
     * @return void
     * @date 2023/3/9 19:53
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * onActivityResult
     *
     * @param requestCode description
     * @param resultCode  description
     * @param data        description
     * @return void
     * @date 2023/3/9 19:53
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == fileRequestCode) {
            if (data.getData() != null) {
                Uri uri = data.getData();
                Log.d("TWT", "uri:" + uri.toString());
                try {
                    glVideoRenderer.getMediaPlayer().reset();
                    glVideoRenderer.getMediaPlayer().setDataSource(CheckFrameUpdateActivity.this, uri);
                    glVideoRenderer.getMediaPlayer().prepare();
                } catch (IOException ex) {
                    Log.e("CheckFrameUpdate", ex.toString());
                }
            }
        }
    }
}