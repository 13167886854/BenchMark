/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.benchmark.data.YinHuaData;
import com.example.benchmark.R;
import com.example.benchmark.thread.AudioDecodeThread;
import com.example.benchmark.thread.VideoDecodeThread;
import com.example.benchmark.utils.ScoreUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * AudioVideoActivity
 *
 * @version 1.0
 * @since 2023/3/7 15:05
 */
public class AudioVideoActivity extends AppCompatActivity implements View.OnClickListener {
    private String mMp4FilePath;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    private TextView yinhuaxinxi;
    private long videocurtime;
    private long audiocurtime;
    private long lastVideoCurtime = -1;
    private long lastAudioCurtime = -1;
    private int heightPixels;
    private int widthPixels;
    private long maxDifferenceValue;
    private boolean isCompleted = false;
    private VideoDecodeThread videoDecodeThread;
    private AudioDecodeThread audioDecodeThread;

    private Handler handler = new Handler() {
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        @Override
        public void handleMessage(Message msg) {
            videocurtime = videoDecodeThread.getcurTime() / 10000;
            audiocurtime = audioDecodeThread.getcurTime() / 10000;

            if (isCompleted && YinHuaData.getInstance().getResolution() != null) {
                yinhuaxinxi.setText("测试结束！"
                        + System.getProperty("line.separator")
                        + "当前云手机像素为" + heightPixels + "X" + widthPixels + "像素"
                        + System.getProperty("line.separator")
                        + "当前视频帧" + videocurtime
                        + System.getProperty("line.separator")
                        + "当前音频帧" + audiocurtime
                        + System.getProperty("line.separator")
                        + "最大音画同步差" + maxDifferenceValue);
                ScoreUtil.calcAndSaveSoundFrameScores(YinHuaData.getInstance().getResolution(), maxDifferenceValue);
                YinHuaData.getInstance().setTestOver(true);
                return;
            }

            // 当视频帧和音频帧不再增加判断播放结束 When video frames and audio frames are no
            // longer added judge the end of play
            if (lastAudioCurtime == audiocurtime && lastVideoCurtime == videocurtime && !isCompleted) {
                isCompleted = true;
            }

            if (Math.abs(videocurtime - audiocurtime) > maxDifferenceValue) {
                maxDifferenceValue = Math.abs(videocurtime - audiocurtime);
            }
            yinhuaxinxi.setText("手机像素为" + heightPixels + "X" + widthPixels + "像素"
                    + System.getProperty("line.separator")
                    + "当前视频帧" + videocurtime
                    + System.getProperty("line.separator")
                    + "当前音频帧" + audiocurtime
                    + System.getProperty("line.separator")
                    + "当前音画同步差" + Math.abs((videocurtime - audiocurtime))
            );
            lastAudioCurtime = audiocurtime;
            lastVideoCurtime = videocurtime;
        }
    };

    /**
     * start
     *
     * @param context description
     * @return void
     * @date 2023/3/9 19:56
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, AudioVideoActivity.class);
        intent.putExtra("isAudioVideoTested", true);
        context.startActivity(intent);
    }

    /**
     * onCreate
     *
     * @param savedInstanceState description
     * @return void
     * @date 2023/3/9 19:57
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_video);
        init();
        Log.d("TWT", "资源文件的路径" + mMp4FilePath);
        // 直接开始测试崩溃 等待1S后开始测试  Start the test crash directly and wait 1S to start the test
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                startTest();
            }
        };
        timer.schedule(task, 1000);
    }

    private void init() {
        isCompleted = false;
        YinHuaData.getInstance().setTestOver(false);
        mSurfaceView = findViewById(R.id.surface_view);
        mHolder = mSurfaceView.getHolder();
        yinhuaxinxi = findViewById(R.id.yinhua_item);
        mMp4FilePath = getIntent().getStringExtra("path");
        if (getSystemService(Context.WINDOW_SERVICE) instanceof WindowManager) {
            if (AudioVideoActivity.this.getSystemService(Context.WINDOW_SERVICE) instanceof WindowManager) {
                WindowManager wm = (WindowManager) AudioVideoActivity.this.getSystemService(Context.WINDOW_SERVICE);
                DisplayMetrics outMetrics = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(outMetrics);
                // 分辨率   Resolution ratio
                heightPixels = outMetrics.heightPixels;
                widthPixels = outMetrics.widthPixels;
                // 最大同步差   Maximum synchronization difference
                maxDifferenceValue = 0;
            }
        }
    }

    /**
     * onClick
     *
     * @param view description
     * @return void
     * @date 2023/3/9 19:57
     */
    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public void onClick(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
            return;
        }
    }

    private void startTest() {
        videoDecodeThread = new VideoDecodeThread(mMp4FilePath, AudioVideoActivity.this);
        videoDecodeThread.setSurfaceView(mSurfaceView);
        // 开启音频解码线程   Start the audio decoding thread
        audioDecodeThread = new AudioDecodeThread(mMp4FilePath, AudioVideoActivity.this);
        if (getSystemService(AUDIO_SERVICE) instanceof AudioManager) {
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            int sessionId = audioManager.generateAudioSessionId();
            audioDecodeThread.setSessionId(sessionId);
            videoDecodeThread.start();
            audioDecodeThread.start();
            if (videoDecodeThread.isAlive() && audioDecodeThread.isAlive()) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        handler.sendMessage(message);
                        if (YinHuaData.getInstance().isTestOver()) {
                            timer.cancel();
                        }
                    }
                }, 0, 1000);
            } else {
                Toast.makeText(AudioVideoActivity.this, "视频播放结束", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * onBackPressed
     *
     * @return void
     * @date 2023/3/9 19:57
     */
    @Override
    public void onBackPressed() {
        super.onDestroy();
    }

    /**
     * onDestroy
     *
     * @return void
     * @date 2023/3/9 19:57
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}