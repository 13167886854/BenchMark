/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.activity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

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
import com.example.benchmark.utils.FpsUtils;
import com.example.benchmark.render.GLVideoRenderer;
import com.example.benchmark.utils.ScoreUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * TestSMActivity
 *
 * @version 1.0
 * @since 2023/3/7 15:08
 */
public class TestSMActivity extends AppCompatActivity {
    private static final int FILE_REQUEST_CODE = 50;

    private GLSurfaceView glView;
    private GLVideoRenderer glVideoRenderer;
    private FpsUtils fpsUtil = FpsUtils.getFpsUtils();
    private TextView textInfo;
    private FpsRunnalbe fpsRunnalbe;
    private String path = "";

    // 百分比显示
    private DecimalFormat df = new DecimalFormat("0.00%");

    // 判断是否正在测试
    private boolean isTesting = false;
    private String eachFps = "";

    /**
     * Activity的启动方法
     *
     * @param context 上下文
     * @param path    Intent传过来的参数
     */
    public static void start(Context context, String path) {
        Intent intent = new Intent(context, TestSMActivity.class);
        intent.putExtra("path", path);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * onCreate
     *
     * @param savedInstanceState description
     * @return void
     * @date 2023/3/9 19:48
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_smactivity);

        // 获取显示文本
        textInfo = findViewById(R.id.textview);

        // 初始化监听器
        fpsRunnalbe = new FpsRunnalbe();

        // 初始化视频播放器
        glView = findViewById(R.id.surface_view);
        glView.setEGLContextClientVersion(2);
        glVideoRenderer = new GLVideoRenderer(this);

        // 创建renderer
        glView.setRenderer(glVideoRenderer);

        // 设置renderer
        glVideoRenderer.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d("TWT", "onCompletion: 播放结束");
                try {
                    stopTest();
                } catch (IOException exception) {
                    Log.e("TestSMActivity: ", exception.toString());
                }
            }
        });
        path = getIntent().getStringExtra("path");
        try {
            glVideoRenderer.getMediaPlayer().reset();
            glVideoRenderer.getMediaPlayer().setDataSource(TestSMActivity.this, Uri.parse(path));
            glVideoRenderer.getMediaPlayer().prepare();
        } catch (IOException ex) {
            Log.e("TestSMActivity: ", ex.toString());
        }

        // 自动开始
        try {
            doTest();
        } catch (IOException e) {
            Log.e("TestSMActivity: ", e.toString());
        }
    }

    private void doTest() throws IOException {
        isTesting = true;
        Log.d("TWT", "doTest: 开始播放");
        fpsUtil.startMonitor(fpsRunnalbe);
        glVideoRenderer.getMediaPlayer().start();
    }

    private void stopTest() throws IOException {
        isTesting = false;

        // 停止监听和播放
        fpsUtil.stopMonitor(fpsRunnalbe);
        glVideoRenderer.getMediaPlayer().stop();
        glVideoRenderer.getMediaPlayer().prepare();

        float[] info = new float[6];
        info[0] = getRoundNumber((float) fpsUtil.getAvergeFps());
        info[1] = getRoundNumber((float) fpsUtil.getFrameShakingRate());
        info[2] = getRoundNumber((float) fpsUtil.getLowFrameRate());
        info[3] = getRoundNumber((float) fpsUtil.getFrameIntervalTime());
        info[4] = fpsUtil.getJankCount();
        info[5] = getRoundNumber((float) fpsUtil.getShtutterRate());
        ScoreUtil.calcAndSaveFluencyScores(
                info,
                eachFps
        );
        Intent intent = new Intent(TestSMActivity.this, CePingActivity.class);
        intent.putExtra("isFluencyUntested", false);
        startActivity(intent);
    }

    private float getRoundNumber(float aaa) {
        BigDecimal bd = new BigDecimal(aaa);
        return bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * onPause
     *
     * @return void
     * @date 2023/3/9 19:48
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
                Log.e("TestSMActivity: ", ex.toString());
            }
        }
    }

    /**
     * onResume
     *
     * @return void
     * @date 2023/3/9 19:48
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    class FpsRunnalbe implements Runnable {
        @Override
        public void run() {
            fpsUtil.updateBeforeGetInfo();

            // 打印测试中数据到TextView上
            if (eachFps.equals("")) {
                eachFps += String.valueOf(fpsUtil.getCount());
            } else {
                eachFps += "," + String.valueOf(fpsUtil.getCount());
            }
            Log.d("TWT", "runing.......... ");
            textInfo.setText(
                    "当前FPS" + fpsUtil.getCount() + "帧/秒" + System.getProperty("line.separator")
                            + "帧抖动率" + String.format("%.2f", fpsUtil.getFrameShakingRate())
                            + System.getProperty("line.separator")
                            + "低帧率" + df.format(fpsUtil.getLowFrameRate()) + System.getProperty("line.separator")
                            + "当前帧间隔" + fpsUtil.getIntervalTime() + "ms" + System.getProperty("line.separator")
                            + "jank发生次数" + fpsUtil.getJankCount() + System.getProperty("line.separator")
                            + "卡顿率" + df.format(fpsUtil.getShtutterRate()) + System.getProperty("line.separator")
                            + "总帧数" + fpsUtil.getTotalCount() + System.getProperty("line.separator")
                            + "测试时长" + (fpsUtil.getSizeOfCountArray() + 1)
                            + "s" + System.getProperty("line.separator")
            );
            fpsUtil.updateAfterGetInfo();

            // 记录绘制次数和绘制时间，用于计算FPS
            FpsUtils.getMainHandler().postDelayed(this, FpsUtils.FPS_INTERVAL_TIME);
        }
    }

    /**
     * onActivityResult
     *
     * @param requestCode description
     * @param resultCode description
     * @param data description
     * @return void
     * @date 2023/3/9 19:48
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == FILE_REQUEST_CODE) {
            if (data.getData() != null) {
                try {
                    Uri uri = data.getData();
                    Log.d("TWT", "uri:" + uri.toString());
                    glVideoRenderer.getMediaPlayer().reset();
                    glVideoRenderer.getMediaPlayer().setDataSource(TestSMActivity.this, uri);
                    glVideoRenderer.getMediaPlayer().prepare();
                } catch (IOException ex) {
                    Log.e("TestSMActivity: ", ex.toString());
                }
            }
        }
    }
}