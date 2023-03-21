/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.service;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.benchmark.R;
import com.example.benchmark.data.YinHuaData;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;
import com.example.benchmark.utils.CodeUtils;
import com.example.benchmark.utils.Recorder;
import com.example.benchmark.utils.ScoreUtil;
import com.example.benchmark.utils.ServiceUtil;
import com.example.benchmark.utils.TapUtil;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * FxService
 *
 * @version 1.0
 * @since 2023/3/7 17:20
 */
public class FxService extends Service {
    /**
     * 文件路径地址  File path address
     */
    private static final String TAG = "TWT";

    private final int screenHeight = CacheUtil.getInt(CacheConst.KEY_SCREEN_HEIGHT);
    private final int screenWidth = CacheUtil.getInt(CacheConst.KEY_SCREEN_WIDTH);
    private final int screenDpi = CacheUtil.getInt(CacheConst.KEY_SCREEN_DPI);
    private final int stopRecord = 111;
    private final int computePesq = 222;
    private boolean isCodeTouchAble = true;
    private boolean isRunning;

    private String path = "";
    private int width;
    private int height;
    private int dpi;
    private Boolean isCheckTouch;
    private Boolean isCheckSoundFrame;
    private long startTime;
    private long endTime;
    private int statusBarHeight;

    private MediaProjection mediaProjection;
    private FxService service;
    private TapUtil tapUtil;

    // 视频音频录制变量初始化  Initialize the video audio recording variable
    private Recorder mRecorder;
    private VirtualDisplay virtualDisplay;
    private MediaRecorder mediaRecorder;

    // 定义浮动窗口布局  Define the floating window layout
    private LinearLayout mFloatLayout;
    private LayoutParams wmParams;

    // 创建浮动窗口设置布局参数的对象  Create a floating window to set the layout parameters of the object
    private WindowManager mWindowManager;
    private Context mContext;
    private TextView mFloatView;
    private LinearLayout btnMenu;
    private Button btnToPrCode;
    private Button btnToTap;
    private Button btnToBack;
    private Button btnToRecord;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case stopRecord:
                    btnToRecord.setClickable(false);
                    btnMenu.setVisibility(View.VISIBLE);
                    Toast.makeText(mContext, "录制结束，请耐心等待音频质量计算结果~",
                            Toast.LENGTH_SHORT).show();
                    break;
                case computePesq:
                    if (YinHuaData.getInstance().getPesq() != null) {
                        Toast.makeText(mContext, (YinHuaData.getInstance().getPlatformType() + "音频质量计算完成~"),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    /**
     * setPara
     *
     * @param isCheckTouch      description
     * @param isCheckSoundFrame description
     * @return void
     * @date 2023/3/10 15:23
     */
    public void setPara(boolean isCheckTouch, boolean isCheckSoundFrame) {
        this.isCheckTouch = isCheckTouch;
        this.isCheckSoundFrame = isCheckTouch;
    }

    /**
     * onCreate
     *
     * @return void
     * @date 2023/3/10 15:23
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = FxService.this;
        service = this.service;
        tapUtil = TapUtil.getUtil();

        HandlerThread serviceThread = new HandlerThread("service_thread",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        serviceThread.start();
        isRunning = false;
        mediaRecorder = new MediaRecorder();
    }

    /**
     * createNotificationChannel
     *
     * @return void
     * @date 2023/3/10 15:23
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel("Benchmark 悬浮窗",
                "Benchmark 悬浮窗",
                NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createFloatView() {
        initFloatView1();

        // 获取状态栏的高度  Gets the height of the status bar
        int resourceId = getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        statusBarHeight = getResources().getDimensionPixelSize(resourceId);

        // 浮动窗口按钮  Floating window button
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        initFloatView2();
        mFloatView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View vi) {
            }
        });
        btnToPrCode = btnMenu.findViewById(R.id.btnToPrCode);
        if (this.isCheckSoundFrame) {
            btnToPrCode.setTextColor(0xFFCCCCCC);
            isCodeTouchAble = false;
        }
        initFloatView3();

        btnToTap = btnMenu.findViewById(R.id.btnToTap);
        initFloatView4();

        btnToTap.setVisibility(isCheckTouch ? View.VISIBLE : View.GONE);
        btnToBack = btnMenu.findViewById(R.id.btnToBack);
        initFloatView5();

        btnToRecord = btnMenu.findViewById(R.id.btnToRecord);
        initFloatView6();
        btnToRecord.setVisibility(isCheckSoundFrame ? View.VISIBLE : View.GONE);
    }

    private void initFloatView6() {
        btnToRecord.setOnTouchListener(new OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public boolean onTouch(View vi, MotionEvent event) {
                boolean isClick = false;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                        // getRawX is the coordinate of the touch position with respect to the screen
                        // getX is the coordinate with respect to the button
                        wmParams.x = (int) event.getRawX() - btnToBack.getMeasuredWidth() / 2;
                        wmParams.y = (int) event.getRawY() - btnToBack.getMeasuredHeight() - statusBarHeight;

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
                // 响应返回点击事件  The response returns the click event
                if (isClick) {
                    tapUtil.tap(screenWidth / 2, screenHeight / 2);
                    btnMenu.setVisibility(View.GONE);
                    startAudioRecord();
                    startVideoRecord();
                    Timer timer = new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            stopAudioRecord();
                            stopVideoRecord();
                            handler.sendEmptyMessage(stopRecord);
                        }
                    };
                    timer.schedule(task, 45000);
                }
                return true;
            }
        }); // 设置监听浮动窗口的触摸移动  Set to listen for touch movements in the floating window
    }

    private void initFloatView5() {
        btnToBack.setOnTouchListener(new OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onTouch(View vi, MotionEvent event) {
                boolean isClick = false;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                        // getRawX is the coordinate of the touch position with respect to the screen
                        // getX is the coordinate with respect to the button
                        wmParams.x = (int) event.getRawX() - btnToBack.getMeasuredWidth() / 2;
                        wmParams.y = (int) event.getRawY()
                                - btnToBack.getMeasuredHeight() - statusBarHeight;
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
                // 响应返回点击事件  The response returns the click event
                if (isClick) {
                    btnMenu.setVisibility(View.GONE);
                    mFloatView.setVisibility(View.VISIBLE);
                }
                return true;
            }
        }); // 设置监听浮动窗口的触摸移动  Set to listen for touch movements in the floating window
    }

    private void initFloatView4() {
        btnToTap.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View vi, MotionEvent event) {
                boolean isClick = false;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                        // getRawX is the coordinate of the touch position with respect to the screen
                        // getX is the coordinate with respect to the button
                        wmParams.x = (int) event.getRawX() - btnToTap.getMeasuredWidth() / 2;
                        wmParams.y = (int) event.getRawY() - btnToTap.getMeasuredHeight() - statusBarHeight;

                        // 刷新  renovate  Less than 0.2 seconds is considered a click
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
                // 响应触控点击事件  Respond to touch click events
                if (isClick) {
                    // 这里写开启触控服务
                    Toast.makeText(mContext, "点击了开启触控服务", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onTouch: 点击了开启触控服务");
                    tapUtil.phoneTouchTap();
                }
                return true;
            }
        }); // 设置监听浮动窗口的触摸移动  Set to listen for touch movements in the floating window
    }

    private void initFloatView3() {
        btnToPrCode.setOnTouchListener(new OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onTouch(View vi, MotionEvent event) {
                boolean isClick = false;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:

                        // getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                        // getRawX is the coordinate of the touch position with respect to the screen
                        // getX is the coordinate with respect to the button
                        wmParams.x = (int) event.getRawX() - btnToPrCode.getMeasuredWidth() / 2;
                        wmParams.y = (int) event.getRawY()
                                - btnToPrCode.getMeasuredHeight() - statusBarHeight;

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

                // 响应点击事件  Response click event
                if (isClick) {
                    if (isCodeTouchAble) {
                        toCatchScreen();
                    }
                }
                return true;
            }
        }); // 设置监听浮动窗口的触摸移动  Set to listen for touch movements in the floating window
    }

    private void initFloatView2() {
        mFloatView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View vi, MotionEvent event) {
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
                        wmParams.y = (int) event.getRawY()
                                - mFloatView.getMeasuredHeight() / 2 - statusBarHeight;

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

                // 响应点击事件  Response click event
                // 点击按钮进行截屏bitmap形式  Click the button to take a screenshot in bitmap form
                if (isClick) {
                    mFloatView.setVisibility(View.GONE);
                    btnMenu.setVisibility(View.VISIBLE);
                }

                // 设置监听浮动窗口的触摸移动  Set to listen for touch movements in the floating window
                return true;
            }
        });
    }

    private void initFloatView1() {
        wmParams = new LayoutParams();

        // 获取WindowManagerImpl.CompatModeWrapper  Get WindowManagerImpl.CompatModeWrapper
        if (mContext.getSystemService(Context.WINDOW_SERVICE) instanceof WindowManager) {
            mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        }
        // 设置window type  Set Window type
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            wmParams.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            wmParams.type = LayoutParams.TYPE_TOAST;
        }

        // 设置图片格式，效果为背景透明  Format the image so that the background is transparent
        wmParams.format = PixelFormat.RGBA_8888;

        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        // Make the floating window unfocused (implements operations on visible Windows other than the floating window)
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;

        // 调整悬浮窗显示的停靠位置为左侧置顶  Adjust the parking window display to the left
        wmParams.gravity = Gravity.START | Gravity.TOP;

        /*
            以屏幕左上角为原点，设置x、y初始值(设置最大直接显示在右下角) Take the upper left corner of the screen as the origin,
            set the initial value of x and y (set the maximum value to be displayed directly in the lower right corner)
         */
        wmParams.x = screenWidth - 50;
        wmParams.y = screenHeight / 4 * 3;

        // 设置悬浮窗口长宽数据  Set the length and width of the floating window
        wmParams.width = LayoutParams.WRAP_CONTENT;
        wmParams.height = LayoutParams.WRAP_CONTENT;
        LayoutInflater inflater = LayoutInflater.from(getApplication());

        // 获取浮动窗口视图所在布局  Gets the layout of the floating window view
        if (inflater.inflate(R.layout.float_layout, null) instanceof LinearLayout) {
            mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
        }

        btnMenu = mFloatLayout.findViewById(R.id.btnMenu);
        btnMenu.setVisibility(View.GONE);

        if (mFloatLayout.findViewById(R.id.textinfo) instanceof TextView) {
            mFloatView = (TextView) mFloatLayout.findViewById(R.id.textinfo);
        }

        mWindowManager.addView(mFloatLayout, wmParams);
    }

    /**
     * onDestroy
     *
     * @return void
     * @date 2023/3/10 15:27
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mFloatLayout != null) {
                mWindowManager.removeView(mFloatLayout);
            }
        } finally {
            mediaProjection.stop();
            stopSelf();
        }
    }

    /**
     * onStartCommand
     *
     * @param intent description
     * @param flags description
     * @param startId description
     * @return int
     * @date 2023/3/10 15:27
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * screenShot
     *
     * @return android.graphics.Bitmap
     * @date 2023/3/10 15:27
     */
    public Bitmap screenShot() {
        Objects.requireNonNull(mediaProjection);
        @SuppressLint("WrongConstant")
        ImageReader imageReader = ImageReader.newInstance(screenWidth, screenHeight,
                PixelFormat.RGBA_8888, 60);
        VirtualDisplay virtualDisplayTemp = mediaProjection.createVirtualDisplay(
                "screen", screenWidth, screenHeight, 1,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(), null, null);
        SystemClock.sleep(1000);

        // 取最新的图片  Get the latest picture
        Image image = imageReader.acquireLatestImage();

        // 释放 virtualDisplay,不释放会报错
        virtualDisplay.release();
        return image2Bitmap(image);
    }

    /**
     * image2Bitmap
     *
     * @param image description
     * @return android.graphics.Bitmap
     * @date 2023/3/10 15:25
     */
    public static Bitmap image2Bitmap(Image image) {
        if (image == null) {
            Log.e(TAG, "image 为空");
        }
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * imageWidth;
        Bitmap bitmap = Bitmap.createBitmap(imageWidth
                + rowPadding / pixelStride, imageHeight, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        image.close();
        return bitmap;
    }

    private float getFloatDataFromJson(JSONObject jsonObject, String name) {
        Object target = jsonObject.getString(name);
        if (target == null) {
            return 0F;
        } else {
            return Float.parseFloat(target.toString().split("吉")[0]);
        }
    }

    private long getLongDataFromJson(JSONObject jsonObject, String name) {
        Object target = jsonObject.getString(name);
        if (target == null) {
            return 0;
        } else {
            return Long.parseLong(target.toString());
        }
    }

    private int getIntDataFromJson(JSONObject jsonObject, String name) {
        Object target = jsonObject.getString(name);
        if (target == null) {
            return 0;
        } else {
            return Integer.parseInt(target.toString());
        }
    }

    private JSONArray getListFromJson(JSONObject jsonObject, String name) {
        Object target = jsonObject.getString(name);
        Log.d("getIntDataFromJson", "getIntDataFromJson: ==>" + target);
        Log.d("getIntDataFromJson", "getIntDataFromJson: ==>"
                + jsonObject.get("endTimeList"));
        if (target == null) {
            return JSON.parseArray(String.valueOf(""));
        } else {
            return JSON.parseArray(String.valueOf(target));
        }
    }

    private String getCloudListDataFromJson(JSONObject jsonObject, String name) {
        Object target = jsonObject.getString(name);
        if (target == null) {
            return "null";
        } else {
            if (target instanceof String) {
                return (String) target;
            } else {
                return "";
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void toCatchScreen() {
        Bitmap bitmap = screenShot();
        Log.d(TAG, "bitmap getWidth: " + bitmap.getWidth() + "  getHeight" + bitmap.getHeight());
        String result = CodeUtils.parseCode(bitmap);
        Log.e("QT-1", result + "---123");
        Log.e("QT-1", "123---" + result);
        if ("{}".equals(result)) {
            // 空数据，点击无效
            return;
        }
        JSONObject jsonData = JSON.parseObject(result);

        getInfo1(jsonData);

        Log.d("TWT", "云端测试数据JSON: " + jsonData);
        // 触控测试数据
        ScoreUtil.calcAndSaveTouchScores(
                getCloudListDataFromJson(jsonData, "cloudDownTimeList"),
                getCloudListDataFromJson(jsonData, "cloudSpendTimeList")
        );
        Log.d("TWT", "云端测试数据JSON: " + jsonData);
        if (jsonData.get("resolution") != null) {
            ScoreUtil.calcAndSaveSoundFrameScores(
                    jsonData.get("resolution") instanceof String ? (String) jsonData.get("resolution") : "",
                    getFloatDataFromJson(jsonData, "maxdifferencevalue")
            );
        }
        CacheUtil.put(CacheConst.KEY_PERFORMANCE_IS_MONITORED, true);
        Toast.makeText(FxService.this, "测试结束！", Toast.LENGTH_SHORT).show();
        ServiceUtil.backToCePingActivity(FxService.this);
        stopSelf();
        onDestroy();
    }

    private void getInfo1(JSONObject jsonData) {
        // 信息获取  Information retrieval
        Log.e("QT-2", jsonData.toJSONString());
        ScoreUtil.calcAndSaveCPUScores(
                getIntDataFromJson(jsonData, "cpuCores")
        );
        ScoreUtil.calcAndSaveGPUScores(
                jsonData.get("gpuVendor") instanceof String ? (String) jsonData.get("gpuVendor") : "",
                jsonData.get("gpuRenderer") instanceof String ? (String) jsonData.get("gpuRenderer") : "",
                jsonData.get("gpuVersion") instanceof String ? (String) jsonData.get("gpuVersion") : ""
        );
        ScoreUtil.calcAndSaveRAMScores(
                jsonData.get("availRam") instanceof String ? (String) jsonData.get("availRam") : "",
                jsonData.get("totalRam") instanceof String ? (String) jsonData.get("totalRam") : ""
        );
        ScoreUtil.calcAndSaveROMScores(
                jsonData.get("availStorage") instanceof String ? (String) jsonData.get("availStorage") : "",
                jsonData.get("totalStorage") instanceof String ? (String) jsonData.get("totalStorage") : ""

        );

        float[] info = new float[6];
        info[0] = getFloatDataFromJson(jsonData, "avergeFPS");
        info[1] = getFloatDataFromJson(jsonData, "frameShakingRate");
        info[2] = getFloatDataFromJson(jsonData, "lowFrameRate");
        info[3] = getFloatDataFromJson(jsonData, "frameInterval");
        info[4] = getFloatDataFromJson(jsonData, "jankCount");
        info[5] = getFloatDataFromJson(jsonData, "stutterRate");
        ScoreUtil.calcAndSaveFluencyScores(
                info,
                jsonData.getString("eachFps")
        );
    }

    /**
     * setMediaProject
     *
     * @param project description
     * @return void
     * @date 2023/3/11 15:16
     */
    public void setMediaProject(MediaProjection project) {
        mediaProjection = project;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    void record() {
        mRecorder = new Recorder();
        boolean isSupported = mRecorder.start(this, mediaProjection);
        if (!isSupported) {
            mediaProjection.stop();
            stopSelf();
        }
    }

    /**
     * startVideoRecord
     *
     * @return void
     * @date 2023/3/10 15:27
     */
    public void startVideoRecord() {
        if (mediaProjection == null || isRunning) {
            Log.d("TWT", "startRecord: mediaProjection == null");
            return;
        }
        DisplayMetrics metrics = new DisplayMetrics();
        if (this.getSystemService(Context.WINDOW_SERVICE) instanceof WindowManager) {
            WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);
            width = metrics.widthPixels;
            height = metrics.heightPixels;
            dpi = metrics.densityDpi;
            Log.d("TWT", "startRecord: start");
            initRecorder();
            createVirtualDisplay();
            mediaRecorder.start();
            isRunning = true;
        }
    }

    /**
     * stopVideoRecord
     *
     * @return void
     * @date 2023/3/10 15:14
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

        // 平台类型
        String platformKind = YinHuaData.getInstance().getPlatformType();
        Log.d("zzl", "stopAudioRecord: 平台类型==> " + platformKind);

        // 如果是云手机  If it's a cloud phone
        if (platformKind.equals(CacheConst.PLATFORM_NAME_RED_FINGER_CLOUD_PHONE)
                || platformKind.equals(CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_PHONE)
                || platformKind.equals(CacheConst.PLATFORM_NAME_E_CLOUD_PHONE)) {
            stopRecord3();
        } else if (platformKind.equals(CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_PHONE)
                || platformKind.equals(CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_GAME)) {
            stopRecord4();
        } else {
            Log.d(TAG, "stopVideoRecord: lastElse");
        }
    }

    private void stopRecord4() {
        MediaType type = MediaType.parse("application" + File.separator + "octet-stream");

        // file是要上传的文件 File() "/"
        File file = new File(CacheConst.getInstance().getVideoPath() + File.separator
                + CacheConst.VIDEO_PHONE_NAME);
        RequestBody requestBody = RequestBody.create
                (MediaType.parse("multipart" + File.separator + "form-data"), file);
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("VideoRecord", CacheConst.VIDEO_PHONE_NAME, requestBody)
                .build();
        Log.d("zzl", "stopAudioRecord: " + file.getName());
        Log.d("zzl", "stopAudioRecord: CacheConst.audioPath--"
                + CacheConst.getInstance().getVideoPath());
        Log.d("zzl", "stopAudioRecord: CacheConst.AUDIO_NAME--"
                + CacheConst.VIDEO_PHONE_NAME);
        Request request = new Request.Builder()
                .url(CacheConst.HUAWEI_IP + File.separator + "AudioVideo" + File.separator + "VideoRecord")
                .post(multipartBody)
                .build();
        stopRecord2(request);
    }

    private void stopRecord3() {
        MediaType type = MediaType.parse("application" + File.separator + "octet-stream");
        // file是要上传的文件 File() "/"
        File file = new File(CacheConst.getInstance().getVideoPath()
                + File.separator + CacheConst.VIDEO_PHONE_NAME);
        RequestBody requestBody = RequestBody.create
                (MediaType.parse("multipart" + File.separator + "form-data"), file);
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("VideoRecord", CacheConst.VIDEO_PHONE_NAME, requestBody)
                .build();
        Log.d("zzl", "stopAudioRecord: " + file.getName());
        Log.d("zzl", "stopAudioRecord: CacheConst.audioPath--"
                + CacheConst.getInstance().getVideoPath());
        Log.d("zzl", "stopAudioRecord: CacheConst.AUDIO_NAME--"
                + CacheConst.VIDEO_PHONE_NAME);
        Request request = new Request.Builder()
                .url(CacheConst.ALIYUN_IP + File.separator + "AudioVideo" + File.separator + "VideoRecord")
                .post(multipartBody)
                .build();
        stopRecord1(request);
    }

    private void stopRecord2(Request request) {
        OkHttpClient client = new OkHttpClient.Builder()
                // 连接超时  Connection timeout
                .connectTimeout(100 * 60 * 1000, TimeUnit.MILLISECONDS)

                // 读取超时  Read timeout
                .readTimeout(100 * 60 * 1000, TimeUnit.MILLISECONDS)

                // 写入超时  Write timeout
                .writeTimeout(100 * 60 * 1000, TimeUnit.MILLISECONDS)
                .build();
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException ex) {
                        Log.d(TAG, "onFailure: call " + call);
                        Log.d(TAG, "onFailure: ex" + ex.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d(TAG, "onResponse: response==>" + response);
                        Log.d(TAG, "onResponse: response==>" + response.body());
                        String res = response.body().string();
                        Log.d(TAG, "onResponse:" + res);
                        String[] resArr = res.split("=");
                        Log.d(TAG, "onResponse: resArr  " + Arrays.toString(resArr));
                        YinHuaData.getInstance().setPsnr(resArr[1]);
                        YinHuaData.getInstance().setSsim(resArr[3]);
                        Log.d(TAG, "onResponse: YinHuaData.PSNR==>" + YinHuaData.getInstance().getPsnr());
                        Log.d(TAG, "onResponse: YinHuaData.SSIM==>" + YinHuaData.getInstance().getSsim());
                        if (YinHuaData.getInstance().getPsnr() != null
                                && YinHuaData.getInstance().getSsim() != null
                                && YinHuaData.getInstance().getPesq() != null) {
                            isCodeTouchAble = true;
                            btnToPrCode.setTextColor(0xff000000);
                            Looper.prepare();
                            Toast.makeText(getBaseContext(), "音视频质量测试结束~",
                                    Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                });
    }

    private void stopRecord1(Request request) {
        OkHttpClient client = new OkHttpClient.Builder()
                // 连接超时  Connection timeout
                .connectTimeout(100 * 60 * 1000, TimeUnit.MILLISECONDS)

                // 读取超时  Read timeout
                .readTimeout(100 * 60 * 1000, TimeUnit.MILLISECONDS)

                // 写入超时 Write timeout
                .writeTimeout(100 * 60 * 1000, TimeUnit.MILLISECONDS)
                .build();
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException ex) {
                        Log.d(TAG, "onFailure: call " + call);
                        Log.d(TAG, "onFailure: e" + ex.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d(TAG, "onResponse: response==>" + response);
                        Log.d(TAG, "onResponse: response==>" + response.body());
                        String res = response.body().string();
                        Log.d(TAG, "onResponse:" + res);
                        String[] resArr = res.split("=");
                        Log.d(TAG, "onResponse: resArr  " + Arrays.toString(resArr));
                        YinHuaData.getInstance().setPsnr(resArr[1]);
                        YinHuaData.getInstance().setSsim(resArr[3]);
                        Log.d(TAG, "onResponse: YinHuaData.PSNR==>" + YinHuaData.getInstance().getPsnr());
                        Log.d(TAG, "onResponse: YinHuaData.SSIM==>" + YinHuaData.getInstance().getSsim());
                        if (YinHuaData.getInstance().getPsnr() != null
                                && YinHuaData.getInstance().getSsim() != null
                                && YinHuaData.getInstance().getPesq() != null) {
                            isCodeTouchAble = true;
                            btnToPrCode.setTextColor(0xff000000);
                            Looper.prepare();
                            Toast.makeText(getBaseContext(), "音视频质量测试结束~",
                                    Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                });
    }

    /**
     * startAudioRecord
     *
     * @return void
     * @date 2023/3/10 15:16
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void startAudioRecord() {
        record();
    }

    /**
     * stopAudioRecord
     *
     * @return void
     * @date 2023/3/10 15:16
     */
    public void stopAudioRecord() {
        if (mRecorder != null) {
            try {
                mRecorder.startProcessing();
            } catch (IOException ex) {
                Log.e(TAG, "stopAudioRecord: ", ex);
            }
        }
        // 平台类型
        String platformKind = YinHuaData.getInstance().getPlatformType();
        Log.d("zzl", "stopAudioRecord: 平台类型==> " + platformKind);

        // 如果是云手机  If it's a cloud phone
        if (platformKind.equals(CacheConst.PLATFORM_NAME_RED_FINGER_CLOUD_PHONE)
                || platformKind.equals(CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_PHONE)
                || platformKind.equals(CacheConst.PLATFORM_NAME_E_CLOUD_PHONE)) {
            stopAudioRecord3();
        } else if (platformKind.equals(CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_PHONE)
                || platformKind.equals(CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_GAME)) {
            stopAudioRecord4();
        } else {
            Log.d(TAG, "stopAudioRecord: lastElse");
        }
    }

    private void stopAudioRecord4() {
        MediaType type = MediaType.parse("application" + File.separator + "octet-stream");
        File file = new File(CacheConst.getInstance().getAudioPath() + File.separator
                + CacheConst.AUDIO_PHONE_NAME);
        RequestBody requestBody = RequestBody.create
                (MediaType.parse("multipart" + File.separator + "form-data"), file);
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("AudioRecord", CacheConst.AUDIO_PHONE_NAME, requestBody)
                .build();
        Log.d("zzl", "stopAudioRecord: " + file.getName());
        Log.d("zzl", "stopAudioRecord: CacheConst.audioPath--"
                + CacheConst.getInstance().getAudioPath());
        Log.d("zzl", "stopAudioRecord: CacheConst.AUDIO_NAME--"
                + CacheConst.AUDIO_PHONE_NAME);
        Request request = new Request.Builder()
                .url(CacheConst.HUAWEI_IP + File.separator + "AudioVideo" + File.separator + "AudioRecord")
                .post(multipartBody)
                .build();
        stopAudioRecord2(request);
    }

    private void stopAudioRecord3() {
        MediaType type = MediaType.parse("application" + File.separator + "octet-stream");
        File file = new File(CacheConst.getInstance().getAudioPath() + File.separator
                + CacheConst.AUDIO_PHONE_NAME);
        RequestBody requestBody = RequestBody.create
                (MediaType.parse("multipart" + File.separator + "form-data"), file);
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("AudioRecord", CacheConst.AUDIO_PHONE_NAME, requestBody)
                .build();
        Log.d("zzl", "stopAudioRecord: " + file.getName());
        Log.d("zzl", "stopAudioRecord: CacheConst.audioPath--"
                + CacheConst.getInstance().getAudioPath());
        Log.d("zzl", "stopAudioRecord: CacheConst.AUDIO_NAME--"
                + CacheConst.AUDIO_PHONE_NAME);
        Request request = new Request.Builder()
                .url(CacheConst.ALIYUN_IP + File.separator + "AudioVideo" + File.separator + "AudioRecord")
                .post(multipartBody)
                .build();
        stopAudioRecord1(request);
    }

    private void stopAudioRecord2(Request request) {
        OkHttpClient client = new OkHttpClient.Builder()
                // 连接超时  Connection timeout
                .connectTimeout(100 * 60 * 1000, TimeUnit.MILLISECONDS)

                // 读取超时  Read timeout
                .readTimeout(100 * 60 * 1000, TimeUnit.MILLISECONDS)

                // 写入超时  Write timeout
                .writeTimeout(100 * 60 * 1000, TimeUnit.MILLISECONDS)
                .build();
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException ex) {
                        Log.d(TAG, "onFailure: call " + call);
                        Log.d(TAG, "onFailure: e" + ex.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d(TAG, "onResponse: response==>" + response);
                        Log.d(TAG, "onResponse: response==>" + response.body());
                        String res = response.body().string();
                        String[] resArr = res.split(" ");
                        Log.d(TAG, "onResponse: resArr " + Arrays.toString(resArr));
                        YinHuaData.getInstance().setPesq(resArr[resArr.length - 1]);
                        Log.d(TAG, "onResponse: YinHuaData.PESQ==>" + YinHuaData.getInstance().getPesq());
                        handler.sendEmptyMessage(computePesq);
                        if (YinHuaData.getInstance().getPsnr() != null
                                && YinHuaData.getInstance().getSsim() != null
                                && YinHuaData.getInstance().getPesq() != null) {
                            isCodeTouchAble = true;
                            btnToPrCode.setTextColor(0xff000000);
                            Looper.prepare();
                            Toast.makeText(getBaseContext(), "音视频质量测试结束~",
                                    Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                });
    }

    private void stopAudioRecord1(Request request) {
        OkHttpClient client = new OkHttpClient.Builder()
                // 连接超时  Connection timeout
                .connectTimeout(100 * 60 * 1000, TimeUnit.MILLISECONDS)

                // 读取超时  Read timeout
                .readTimeout(100 * 60 * 1000, TimeUnit.MILLISECONDS)

                // 写入超时  Write timeout
                .writeTimeout(100 * 60 * 1000, TimeUnit.MILLISECONDS)
                .build();
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException ex) {
                        Log.d(TAG, "onFailure: call " + call);
                        Log.d(TAG, "onFailure: e" + ex.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d(TAG, "onResponse: response==>" + response);
                        Log.d(TAG, "onResponse: response==>" + response.body());
                        String res = response.body().string();
                        String[] resArr = res.split(" ");
                        Log.d(TAG, "onResponse: resArr  " + Arrays.toString(resArr));
                        YinHuaData.getInstance().setPesq(resArr[resArr.length - 1]);
                        Log.d(TAG, "onResponse: YinHuaData.PESQ==>" + YinHuaData.getInstance().getPesq());
                        handler.sendEmptyMessage(computePesq);
                        if (YinHuaData.getInstance().getPsnr() != null
                                && YinHuaData.getInstance().getSsim() != null
                                && YinHuaData.getInstance().getPesq() != null) {
                            isCodeTouchAble = true;
                            btnToPrCode.setTextColor(0xff000000);
                            Looper.prepare();
                            Toast.makeText(getBaseContext(), "音视频质量测试结束~",
                                    Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                });
    }

    private void createVirtualDisplay() {
        virtualDisplay = mediaProjection.createVirtualDisplay("MainScreen",
                width, height, dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mediaRecorder.getSurface(), null, null);
    }

    private void initRecorder() {
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        // 平台类型
        String platformKind = YinHuaData.getInstance().getPlatformType();
        Log.d("zzl", "stopAudioRecord: 平台类型==> " + platformKind);

        // 如果是云手机  If it's a cloud phone
        if (platformKind.equals(CacheConst.PLATFORM_NAME_RED_FINGER_CLOUD_PHONE)
                || platformKind.equals(CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_PHONE)
                || platformKind.equals(CacheConst.PLATFORM_NAME_E_CLOUD_PHONE)) {
            path = getSaveDirectory() + CacheConst.VIDEO_PHONE_NAME;
        } else if (platformKind.equals(CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_PHONE)
                || platformKind.equals(CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_GAME)) {
            path = getSaveDirectory() + CacheConst.VIDEO_PHONE_NAME;
        } else {
            path = getSaveDirectory() + CacheConst.VIDEO_GAME_NAME;
        }
        mediaRecorder.setOutputFile(path);
        mediaRecorder.setVideoSize(width, height);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
        mediaRecorder.setVideoFrameRate(60);

        try {
            mediaRecorder.prepare();
        } catch (IOException ex) {
            Log.e(TAG, "initRecorder: ", ex);
        }
    }

    /**
     * getSaveDirectory
     *
     * @return java.lang.String
     * @date 2023/3/10 15:16
     */
    public String getSaveDirectory() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "ScreenRecorder" + File.separator;
            CacheConst.getInstance().setVideoPath(rootDir);
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
     * onBind
     *
     * @param intent description
     * @return android.os.IBinder
     * @date 2023/3/9 18:58
     */
    @Override
    public IBinder onBind(Intent intent) {
        this.isCheckTouch = intent.getBooleanExtra("isCheckTouch", false);
        this.isCheckSoundFrame = intent.getBooleanExtra("isCheckSoundFrame",
                false);
        createFloatView();
        return new RecordBinder();
    }

    /**
     * @return
     * @date 2023/3/10 15:17
     */
    public class RecordBinder extends Binder {
        /**
         * getRecordService
         *
         * @return com.example.benchmark.service.FxService
         * @date 2023/3/10 15:28
         */
        public FxService getRecordService() {
            return FxService.this;
        }
    }
}


