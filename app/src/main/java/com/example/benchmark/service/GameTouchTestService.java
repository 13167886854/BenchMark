/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.service;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.benchmark.activity.TestGameTouchActivity;
import com.example.benchmark.R;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;
import com.example.benchmark.utils.GameTouchUtil;
import com.example.benchmark.utils.TapUtil;

import java.io.File;
import java.io.IOException;

/**
 * GameTouchTestService
 *
 * @version 1.0
 * @since 2023/3/7 17:20
 */
public class GameTouchTestService extends Service {
    private static final int START_RECORD = 1;
    private static final int STOP_RECORD = 2;
    private static final String TAG = "TWT";

    private final int screenHeight = CacheUtil.getInt(CacheConst.KEY_SCREEN_HEIGHT);
    private final int screenWidth = CacheUtil.getInt(CacheConst.KEY_SCREEN_WIDTH);

    // 定义浮动窗口布局  Define the floating window layout
    private LinearLayout mFloatLayout;
    private LayoutParams wmParams;

    // 创建浮动窗口设置布局参数的对象  Create a floating window to set the layout parameters of the object
    private WindowManager mWindowManager;
    private TextView mFloatView;

    private TapUtil tapUtil = TapUtil.getUtil();
    private GameTouchUtil gameTouchUtil = GameTouchUtil.getGameTouchUtil();

    private MediaProjection mediaProjection;
    private MediaRecorder mediaRecorder;
    private VirtualDisplay virtualDisplay;
    private String path = "";
    private boolean isRunning;
    private int width = 720;
    private int height = 1080;
    private int dpi;
    private boolean isAble = true;
    private Context mContext;
    private long startTime;
    private long endTime;

    private boolean isRecording = false;
    private int statusBarHeight;
    private Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case START_RECORD:
                    isRecording = !isRecording;
                    startRecord();
                    break;
                case STOP_RECORD:
                    isRecording = !isRecording;

                    // 点击结束录制后休息1s后才能继续录制
                    // Click Finish recording and rest for 1 second before continuing recording
                    isAble = false;
                    stopRecord();
                    Intent intent = new Intent(mContext, TestGameTouchActivity.class);
                    intent.putExtra("path", path);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * onCreate
     *
     * @return void
     * @date 2023/3/10 15:40
     */
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: 121212");
        super.onCreate();
        mContext = GameTouchTestService.this;
        isRunning = false;
        mediaRecorder = new MediaRecorder();
        createFloatView();
    }

    /**
     * onBind
     *
     * @param intent description
     * @return android.os.IBinder
     * @date 2023/3/10 15:40
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind:12132 ");
        return new RecordBinder();
    }

    /**
     * setConfig
     *
     * @param width  description
     * @param height description
     * @param dpi    description
     * @return void
     * @date 2023/3/10 15:40
     */
    public void setConfig(int width, int height, int dpi) {
        this.width = width;
        this.height = height;
        this.dpi = dpi;
    }

    private void createFloatView() {
        initFloatView1();

        // 浮动窗口按钮  Floating window button
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        // 设置监听浮动窗口的触摸移动  Set to listen for touch movements in the floating window
        mFloatView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                boolean isClick = touch1(event);
                // 响应点击事件  Response click event
                if (isClick && isAble) {
                    if (!isRecording) {
                        mFloatView.setText("停止");
                        mFloatView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                getDrawable(R.drawable.ic_stop), null, null, null);

                        // 开始录制  Start recording
                        handler.sendEmptyMessage(START_RECORD);
                    } else {
                        // 停止录制  Stop recording
                        mFloatView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                getDrawable(R.drawable.ic_rest), null, null, null);
                        mFloatView.setTextColor(Color.parseColor("#9F9F9F"));

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (handler != null) {
                                    isAble = true;
                                    mFloatView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                            getDrawable(R.drawable.ic_start), null, null, null);
                                    mFloatView.setTextColor(Color.parseColor("#000000"));
                                }
                            }
                        };
                        // 主线程中调用：  Call from the main thread:
                        handler.postDelayed(runnable, 1000); // 延时1000毫秒  Delay 1000ms
                    }
                }
                return true;
            }
        });

        mFloatView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    private boolean touch1(MotionEvent event) {
        boolean isClick = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                // getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                // getRawX is the coordinate of the touch position with respect to the screen
                // getX is the coordinate with respect to the button
                wmParams.x = (int) event.getRawX() - mFloatView.getMeasuredWidth() / 2;
                wmParams.y = (int) event.getRawY() - mFloatView.getMeasuredHeight() / 2 - statusBarHeight;

                // 刷新  renovate
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                break;
            case MotionEvent.ACTION_UP:
                endTime = System.currentTimeMillis();

                // 小于0.2秒被判断为点击  Less than 0.2 seconds is considered a click
                if ((endTime - startTime) > 200) {
                    isClick = false;
                } else {
                    isClick = true;
                }
                break;
        }
        return isClick;
    }

    private void initFloatView1() {
        wmParams = new LayoutParams();
        // 获取WindowManagerImpl.CompatModeWrapper  Get WindowManagerImpl.CompatModeWrapper
        if (mContext.getSystemService(Context.WINDOW_SERVICE) instanceof WindowManager) {
            mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        }
        // 设置window type  Set window type
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            wmParams.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            wmParams.type = LayoutParams.TYPE_TOAST;
        }

        // 设置图片格式，效果为背景透明  Format the image so that the background is transparent
        wmParams.format = PixelFormat.RGBA_8888;

        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        // Make the floating window unfocused (implements operations
        // on visible Windows other than the floating window)
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;

        // 调整悬浮窗显示的停靠位置为左侧置顶  Adjust the dock position
        // displayed in the suspension window to the left top
        wmParams.gravity = Gravity.START | Gravity.TOP;

        // 以屏幕左上角为原点，设置x、y初始值(设置最大直接显示在右下角)  Take
        // the upper left corner of the screen as the origin,
        // set the initial value of x and y (set the maximum value
        // to be displayed directly in the lower right corner)
        wmParams.x = screenWidth / 2;
        wmParams.y = screenHeight;
        // 设置悬浮窗口长宽数据  Set the length and width of the floating window
        wmParams.width = LayoutParams.WRAP_CONTENT;
        wmParams.height = LayoutParams.WRAP_CONTENT;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        // 获取浮动窗口视图所在布局  Gets the layout of the floating window view
        if (inflater.inflate(R.layout.record_float2, null) instanceof LinearLayout) {
            mFloatLayout = (LinearLayout) inflater.inflate(R.layout.record_float2, null);
        }
        if (mFloatLayout.findViewById(R.id.recordText2) instanceof TextView) {
            mFloatView = (TextView) mFloatLayout.findViewById(R.id.recordText2);
        }
        mWindowManager.addView(mFloatLayout, wmParams);

        // 获取状态栏的高度  Gets the height of the status bar
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        statusBarHeight = getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * setMediaProject
     *
     * @param project description
     * @return void
     * @date 2023/3/10 15:40
     */
    public void setMediaProject(MediaProjection project) {
        mediaProjection = project;
    }

    /**
     * startRecord
     *
     * @return boolean
     * @date 2023/3/10 15:40
     */
    public boolean startRecord() {
        if (mediaProjection == null || isRunning) {
            return false;
        }
        DisplayMetrics metrics = new DisplayMetrics();
        if (this.getSystemService(Context.WINDOW_SERVICE) instanceof WindowManager) {
            WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);
            width = metrics.widthPixels;
            height = metrics.heightPixels;
            dpi = metrics.densityDpi;
            initRecorder();
            createVirtualDisplay();
            mediaRecorder.start();
            gameTouchUtil.setVideoStartTime(System.currentTimeMillis());
            tapUtil.gameTouchTap(GameTouchTestService.this);
            isRunning = true;
            Log.d(TAG, "begin:开始录制 ");
        }
        return true;
    }

    /**
     * sendStopMsg
     *
     * @return void
     * @date 2023/3/10 15:40
     */
    public void sendStopMsg() {
        handler.sendEmptyMessageDelayed(STOP_RECORD, 1000);
    }

    /**
     * stopRecord
     *
     * @return boolean
     * @date 2023/3/10 15:40
     */
    public boolean stopRecord() {
        if (!isRunning) {
            return false;
        }

        isRunning = false;
        mediaRecorder.stop();
        mediaRecorder.reset();
        virtualDisplay.release();
        Log.d(TAG, "begin:结束录制 ");
        gameTouchUtil.setVideoEndTime(System.currentTimeMillis());
        stopSelf();

        if (mFloatLayout != null) {
            mWindowManager.removeView(mFloatLayout);
        }
        return true;
    }

    private void createVirtualDisplay() {
        virtualDisplay = mediaProjection.createVirtualDisplay("MainScreen", width, height, dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mediaRecorder.getSurface(), null, null);
    }

    private void initRecorder() {
        path = getsaveDirectory() + System.currentTimeMillis() + ".mp4";
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(path);
        mediaRecorder.setVideoSize(width, height); // 横向录屏  Horizontal screen recording
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
        mediaRecorder.setVideoFrameRate(60);
        try {
            mediaRecorder.prepare();
        } catch (IOException ex) {
            Log.e(TAG, "initRecorder: ", ex);
        }
    }

    /**
     * getsaveDirectory
     *
     * @return java.lang.String
     * @date 2023/3/10 15:41
     */
    public String getsaveDirectory() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "ScreenRecorder" + File.separator;
            File file = new File(rootDir);
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    return "null";
                }
            }
            return rootDir;
        } else {
            return "null";
        }
    }

    /**
     * onDestroy
     *
     * @return void
     * @date 2023/3/10 15:41
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * onStartCommand
     *
     * @param intent  description
     * @param flags   description
     * @param startId description
     * @return int
     * @date 2023/3/10 15:41
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * GameTouchTestService.java
     *
     * @Author benchmark
     * @Version 1.0
     * @since 2023/3/10 15:41
     */
    public class RecordBinder extends Binder {
        /**
         * getRecordService
         *
         * @return com.example.benchmark.service.GameTouchTestService
         * @date 2023/3/10 15:41
         */
        public GameTouchTestService getRecordService() {
            return GameTouchTestService.this;
        }
    }
}


