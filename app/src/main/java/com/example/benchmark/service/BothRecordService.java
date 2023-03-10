/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.example.benchmark.utils.Recorder;

import java.io.File;
import java.io.IOException;

/**
 * BothRecordService
 *
 * @version 1.0
 * @since 2023/3/7 17:19
 */
public class BothRecordService extends Service {
    // path
    private static String path = "";

    private boolean isRunning;
    private int width;
    private int height;
    private int dpi;
    private MediaProjection mProjection;
    private Recorder mRecorder;
    private VirtualDisplay virtualDisplay;
    private MediaRecorder mediaRecorder;

    /**
     * onCreate
     *
     * @return void
     * @date 2023/3/10 15:02
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onCreate() {
        HandlerThread serviceThread = new HandlerThread("service_thread",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        serviceThread.start();
        isRunning = false;
        mediaRecorder = new MediaRecorder();
        super.onCreate();
    }

    /**
     * onStartCommand
     *
     * @param intent description
     * @param flags description
     * @param startId description
     * @return int
     * @date 2023/3/10 15:02
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    /**
     * onDestroy
     *
     * @return void
     * @date 2023/3/10 15:02
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * setMediaProject
     *
     * @param project description
     * @return void
     * @date 2023/3/10 15:02
     */
    public void setMediaProject(MediaProjection project) {
        mProjection = project;
    }

    /**
     * isRunning
     *
     * @return boolean
     * @date 2023/3/10 15:02
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * record
     *
     * @return void
     * @date 2023/3/10 15:02
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    void record() {
        mRecorder = new Recorder();
        boolean isSupported = mRecorder.start(this, mProjection);
        if (!isSupported) {
            mProjection.stop();
            stopSelf();
        }
    }

    /**
     * startVideoRecord
     *
     * @return void
     * @date 2023/3/10 15:01
     */
    public void startVideoRecord() {
        if (mProjection == null || isRunning) {
            Log.d("TWT", "startRecord: mediaProjection == null");
            return;
        }
        DisplayMetrics metrics = new DisplayMetrics();
        if (this.getSystemService(Context.WINDOW_SERVICE) instanceof WindowManager) {
            WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);
        }
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        dpi = metrics.densityDpi;
        Log.d("TWT", "startRecord: start");
        initRecorder();
        createVirtualDisplay();
        mediaRecorder.start();
        isRunning = true;
    }

    /**
     * stopVideoRecord
     *
     * @return void
     * @date 2023/3/10 15:02
     */
    public void stopVideoRecord() {
        if (!isRunning) {
            return;
        }
        Log.d("TWT", "startRecord: stop");
        isRunning = false;
        mediaRecorder.stop();
        mediaRecorder.reset();
        virtualDisplay.release();
    }

    /**
     * startAudioRecord
     *
     * @return void
     * @date 2023/3/10 15:02
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void startAudioRecord() {
        record();
    }

    /**
     * stopAudioRecord
     *
     * @return void
     * @date 2023/3/10 15:01
     */
    public void stopAudioRecord() {
        if (mRecorder != null) {
            try {
                mRecorder.startProcessing();
            } catch (IOException e) {
                Log.e("BothRecordService", e.toString());
            }
        }
        stopSelf();
    }

    private void createVirtualDisplay() {
        virtualDisplay = mProjection.createVirtualDisplay("MainScreen", width, height, dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mediaRecorder.getSurface(), null, null);
    }

    private void initRecorder() {
        try {
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            path = getsaveDirectory() + System.currentTimeMillis() + ".mp4";
            mediaRecorder.setOutputFile(path);
            mediaRecorder.setVideoSize(width, height);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
            mediaRecorder.setVideoFrameRate(60);
            mediaRecorder.prepare();
        } catch (IOException e) {
            Log.e("BothRecordService", e.toString());
        }
    }

    /**
     * getsaveDirectory
     *
     * @return java.lang.String
     * @date 2023/3/10 15:01
     */
    public String getsaveDirectory() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "ScreenRecorder" + File.separator;
            File file = new File(rootDir);
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    return "";
                }
            }
            return rootDir;
        } else {
            return "";
        }
    }

    /**
     * onBind
     *
     * @param intent description
     * @return android.os.IBinder
     * @date 2023/3/10 15:01
     */
    @Override
    public IBinder onBind(Intent intent) {
        return new RecordBinder();
    }

    /**
     * BothRecordService.java
     *
     * @Author benchmark
     * @Version 1.0 
     * @since 2023/3/10 15:01
     */
    public class RecordBinder extends Binder {
        /**
         * getRecordService
         *
         * @return com.example.benchmark.service.BothRecordService
         * @date 2023/3/10 15:01
         */
        public BothRecordService getRecordService() {
            return BothRecordService.this;
        }
    }
}
