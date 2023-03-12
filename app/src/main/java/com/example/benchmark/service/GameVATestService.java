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
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.ImageReader;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.benchmark.activity.AudioVideoActivity;
import com.example.benchmark.activity.CePingActivity;
import com.example.benchmark.data.YinHuaData;
import com.example.benchmark.R;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;
import com.example.benchmark.utils.Recorder;
import com.example.benchmark.utils.TapUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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
 * GameVATestService
 *
 * @version 1.0
 * @since 2023/3/7 17:20
 */
public class GameVATestService extends Service {
    private static final String TAG = "TWT";
    private static final int START_RECORD = 1;
    private static final int STOP_RECORD = 2;

    private final int computePesq = 222;
    private final int screenHeight = CacheUtil.getInt(CacheConst.KEY_SCREEN_HEIGHT);
    private final int screenWidth = CacheUtil.getInt(CacheConst.KEY_SCREEN_WIDTH);

    // 定义浮动窗口布局
    private LinearLayout mFloatLayout;
    private LayoutParams wmParams;

    // 创建浮动窗口设置布局参数的对象
    private WindowManager mWindowManager;
    private TextView mFloatView;
    private TextView snap;

    private MediaProjection mediaProjection;
    private MediaRecorder mediaRecorder;
    private VirtualDisplay virtualDisplay;
    private String path = "";
    private boolean isRunning;
    private int width = screenWidth;
    private int height = screenHeight;
    private int dpi;

    private ImageReader mImageReader;

    private TapUtil tapUtil;

    // 视频音频录制变量初始化
    private Recorder mRecorder;
    private boolean isAble = true;

    private Context mContext;

    private long startTime;
    private long endTime;

    private boolean isRecording = false;
    private int statusBarHeight;

    private LinearLayout menu2;

    private GameVATestService service;
    private VirtualDisplay mVirtualDisplay = null;

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
                    isAble = false;
                    stopRecord();
                    Intent intent = new Intent(mContext, AudioVideoActivity.class);
                    intent.putExtra("path", path);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                case computePesq:
                    if (YinHuaData.pesq != null) {
                        Toast.makeText(mContext, (YinHuaData.platformType
                                + "音频质量计算完成~"), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * onCreate
     *
     * @return void
     * @date 2023/3/9 16:54
     */
    @Override
    public void onCreate() {
        super.onCreate();
        service = GameVATestService.this;
        tapUtil = TapUtil.getUtil();
        mContext = GameVATestService.this;
        isRunning = false;
        mediaRecorder = new MediaRecorder();
        createFloatView();
    }

    /**
     * onBind
     *
     * @param intent description
     * @return android.os.IBinder
     * @date 2023/3/9 16:54
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
     * @date 2023/3/9 16:54
     */
    public void setConfig(int width, int height, int dpi) {
        this.width = width;
        this.height = height;
        this.dpi = dpi;
    }

    private void createFloatView() {
        initFloatView1();

        // 获取状态栏的高度
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        statusBarHeight = getResources().getDimensionPixelSize(resourceId);

        // 浮动窗口按钮
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        // 设置监听浮动窗口的触摸移动
        mFloatView.setOnTouchListener(new OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public boolean onTouch(View vi, MotionEvent event) {
                boolean isClick = touch1(event);
                // 响应点击事件
                if (isClick && isAble) {
                    Log.d(TAG, "screenWidth: " + screenWidth + " screenWidth" + screenWidth);
                    tapUtil.tap(500, 500);
                    menu2.setVisibility(View.GONE);
                    startAudioRecord();
                    startVideoRecord();
                    Timer timer = new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            stopAudioRecord();
                            stopVideoRecord();
                            handler.sendEmptyMessage(STOP_RECORD);
                        }
                    };
                    timer.schedule(task, 20000);
                }
                return true;
            }
        });

        mFloatView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View vi) {
                Log.d(TAG, "Click");
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
                wmParams.x = (int) event.getRawX() - mFloatView.getMeasuredWidth() / 2;
                wmParams.y = (int) event.getRawY() - mFloatView.getMeasuredHeight() / 2 - statusBarHeight;

                // 刷新
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                break;
            case MotionEvent.ACTION_UP:
                endTime = System.currentTimeMillis();

                // 小于0.2秒被判断为点击
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

        // 获取WindowManagerImpl.CompatModeWrapper
        if (mContext.getSystemService(Context.WINDOW_SERVICE) instanceof WindowManager) {
            mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        }

        // 设置window type
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            wmParams.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            wmParams.type = LayoutParams.TYPE_TOAST;
        }

        // 设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;

        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;

        // 调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.START | Gravity.TOP;

        // 以屏幕左上角为原点，设置x、y初始值(设置最大直接显示在右下角)
        wmParams.x = screenWidth / 2;
        wmParams.y = screenHeight;
        Log.d(TAG, "screenWidth: " + screenWidth + "  screenHeight: " + screenHeight);

        // 设置悬浮窗口长宽数据
        wmParams.width = LayoutParams.WRAP_CONTENT;
        wmParams.height = LayoutParams.WRAP_CONTENT;
        LayoutInflater inflater = LayoutInflater.from(getApplication());

        // 获取浮动窗口视图所在布局
        if (inflater.inflate(R.layout.record_float, null) instanceof LinearLayout) {
            mFloatLayout = (LinearLayout) inflater.inflate(R.layout.record_float, null);
        }
        if (mFloatLayout.findViewById(R.id.menu2) instanceof LinearLayout) {
            menu2 = (LinearLayout) mFloatLayout.findViewById(R.id.menu2);
        }
        if (mFloatLayout.findViewById(R.id.recordText) instanceof TextView) {
            mFloatView = (TextView) mFloatLayout.findViewById(R.id.recordText);
        }
        if (mFloatLayout.findViewById(R.id.KaCa) instanceof TextView) {
            snap = (TextView) mFloatLayout.findViewById(R.id.KaCa);
        }
        snap.setVisibility(View.GONE);
        mWindowManager.addView(mFloatLayout, wmParams);
    }

    /**
     * setMediaProject
     *
     * @param project description
     * @return void
     * @date 2023/3/9 16:54
     */
    public void setMediaProject(MediaProjection project) {
        mediaProjection = project;
    }

    /**
     * startVirtual
     *
     * @return void
     * @date 2023/3/9 16:55
     */
    public void startVirtual() {
        if (mediaProjection != null) {
            Log.i(TAG, "want to display virtual");
        } else {
            Log.e(TAG, "start screen capture intent");
            Log.e(TAG, "want to build mediaprojection and display virtual");
        }
    }

    /**
     * startRecord
     *
     * @return boolean
     * @date 2023/3/9 16:55
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
            isRunning = true;
            Log.d(TAG, "begin:开始录制 ");
            return true;
        }
        return false;
    }

    /**
     * stopRecord
     *
     * @return boolean
     * @date 2023/3/9 16:55
     */
    public boolean stopRecord() {
        if (!isRunning) {
            return false;
        }

        isRunning = false;
        mediaRecorder.stop();
        mediaRecorder.reset();
        virtualDisplay.release();
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

    /**
     * startVideoRecord
     *
     * @return void
     * @date 2023/3/9 16:55
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
        return;
    }

    /**
     * stopVideoRecord
     *
     * @return void
     * @date 2023/3/9 16:55
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
        String platformKind = YinHuaData.platformType;
        Log.d("zzl", "stopAudioRecord: 平台类型==> " + platformKind);

        boolean isTestCloudPhone = platformKind.equals(CacheConst.PLATFORM_NAME_RED_FINGER_CLOUD_PHONE)
                || platformKind.equals(CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_PHONE)
                || platformKind.equals(CacheConst.PLATFORM_NAME_E_CLOUD_PHONE);
        isTestCloudPhone = isTestCloudPhone || platformKind.equals(CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_PHONE)
                || platformKind.equals(CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_GAME);

        // 如果是云手机
        if (isTestCloudPhone) {
            stopVideoRecord1();
        } else {
            // file是要上传的文件 File()
            File file = new File(CacheConst.videoPath + File.separator + CacheConst.VIDEO_GAME_NAME);
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart" + File.separator
                    + "form-data"), file);
            MultipartBody multipartBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("VideoRecord", CacheConst.VIDEO_GAME_NAME, requestBody)
                    .build();
            Log.d("zzl", "stopAudioRecord: " + file.getName());
            Log.d("zzl", "stopAudioRecord: CacheConst.audioPath--" + CacheConst.videoPath);
            Log.d("zzl", "stopAudioRecord: CacheConst.VIDEO_GAME_NAME--" + CacheConst.VIDEO_GAME_NAME);
            Request request = new Request.Builder()
                    .url(CacheConst.GLOBAL_IP + File.separator + "AudioVideo" + File.separator + "VideoRecord")
                    .post(multipartBody)
                    .build();
            stopVideoRecord2(request);
        }
    }

    private void stopVideoRecord2(Request request) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60 * 1000, TimeUnit.MILLISECONDS) // 连接超时
                .readTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS) // 读取超时
                .writeTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS) // 写入超时
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
                        String[] resArr = res.split("=");
                        Log.d(TAG, "onResponse: resArr  " + Arrays.toString(resArr));

                        YinHuaData.psnr = resArr[1];
                        YinHuaData.ssim = resArr[3];
                        YinHuaData.resolution = resArr[5];
                        Log.d(TAG, "onResponse: YinHuaData.PSNR==>" + YinHuaData.psnr);
                        Log.d(TAG, "onResponse: YinHuaData.SSIM==>" + YinHuaData.ssim);
                        Log.d(TAG, "onResponse: YinHuaData.SSIM==>" + YinHuaData.resolution);
                        Log.d(TAG, "onResponse: YinHuaData.PSNR==>" + YinHuaData.psnr);
                        Log.d(TAG, "onResponse: YinHuaData.SSIM==>" + YinHuaData.ssim);
                        Intent intent = new Intent(getApplicationContext(), CePingActivity.class);
                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
    }

    private void stopVideoRecord1() {
        File file = new File(CacheConst.videoPath + File.separator + CacheConst.VIDEO_PHONE_NAME);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart" + File.separator
                + "form-data"), file);
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("VideoRecord", CacheConst.VIDEO_PHONE_NAME, requestBody)
                .build();
        Log.d("zzl", "stopAudioRecord: " + file.getName());
        Log.d("zzl", "stopAudioRecord: CacheConst.audioPath--" + CacheConst.videoPath);
        Log.d("zzl", "stopAudioRecord: CacheConst.AUDIO_NAME--" + CacheConst.VIDEO_PHONE_NAME);
        Request request = new Request.Builder()
                .url(CacheConst.GLOBAL_IP + File.separator + "AudioVideo" + File.separator + "VideoRecord")
                .post(multipartBody)
                .build();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60 * 1000, TimeUnit.MILLISECONDS) // 连接超时
                .readTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS) // 读取超时
                .writeTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS) // 写入超时
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
                        YinHuaData.psnr = resArr[1];
                        YinHuaData.ssim = resArr[3];
                        YinHuaData.resolution = resArr[5];

                        Log.d(TAG, "onResponse: YinHuaData.PSNR==>" + YinHuaData.psnr);
                        Log.d(TAG, "onResponse: YinHuaData.SSIM==>" + YinHuaData.ssim);
                        Log.d(TAG, "onResponse: YinHuaData.SSIM==>" + YinHuaData.resolution);
                    }
                });
    }

    private void initRecorder() {
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        // 平台类型
        String platformKind = YinHuaData.platformType;
        Log.d("zzl", "stopAudioRecord: 平台类型==> " + platformKind);

        // 如果是云手机
        if (platformKind.equals(CacheConst.PLATFORM_NAME_RED_FINGER_CLOUD_PHONE)
                || platformKind.equals(CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_PHONE)
                || platformKind.equals(CacheConst.PLATFORM_NAME_E_CLOUD_PHONE)
        ) {
            path = getsaveDirectory() + CacheConst.VIDEO_PHONE_NAME;
        } else if (platformKind.equals(CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_PHONE)
                || platformKind.equals(CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_GAME)) {
            path = getsaveDirectory() + CacheConst.VIDEO_PHONE_NAME;
        } else {
            path = getsaveDirectory() + CacheConst.VIDEO_GAME_NAME;
        }

        mediaRecorder.setOutputFile(path);
        mediaRecorder.setVideoSize(width, height);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
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
     * getsaveDirectory
     *
     * @return java.lang.String
     * @date 2023/3/9 16:55
     */
    public String getsaveDirectory() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "ScreenRecorder" + File.separator;
            CacheConst.videoPath = rootDir;
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
     * record
     *
     * @return void
     * @date 2023/3/9 16:55
     */
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
     * startAudioRecord
     *
     * @return void
     * @date 2023/3/9 16:55
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void startAudioRecord() {
        record();
    }

    /**
     * stopAudioRecord
     *
     * @return void
     * @date 2023/3/9 16:56
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
        String platformKind = YinHuaData.platformType;
        Log.d("zzl", "stopAudioRecord: 平台类型==> " + platformKind);

        boolean isTestCloudPhone;
        isTestCloudPhone = platformKind.equals(CacheConst.PLATFORM_NAME_RED_FINGER_CLOUD_PHONE)
                || platformKind.equals(CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_PHONE)
                || platformKind.equals(CacheConst.PLATFORM_NAME_E_CLOUD_PHONE);
        isTestCloudPhone = isTestCloudPhone || platformKind.equals(CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_PHONE)
                || platformKind.equals(CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_GAME);

        // 如果是云手机
        if (isTestCloudPhone) {
            stopAudioRecord1();
        } else {
            stopAudioRecord2();
        }
    }

    private void stopAudioRecord2() {
        File file = new File(CacheConst.audioPath + File.separator + CacheConst.AUDIO_GAME_NAME);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart" + File.separator
                + "form-data"), file);
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("AudioRecord", CacheConst.AUDIO_GAME_NAME, requestBody)
                .build();
        Log.d("zzl", "stopAudioRecord: " + file.getName());
        Log.d("zzl", "stopAudioRecord: CacheConst.audioPath--" + CacheConst.audioPath);
        Log.d("zzl", "stopAudioRecord: CacheConst.AUDIO_GAME_NAME--" + CacheConst.AUDIO_GAME_NAME);
        Request request = new Request.Builder()
                .url(CacheConst.GLOBAL_IP + File.separator + "AudioVideo" + File.separator + "AudioRecord")
                .post(multipartBody)
                .build();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60 * 1000, TimeUnit.MILLISECONDS) // 连接超时
                .readTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS) // 读取超时
                .writeTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS) // 写入超时
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
                        YinHuaData.pesq = resArr[resArr.length - 1];
                        Log.d(TAG, "onResponse: YinHuaData.PESQ==>" + YinHuaData.pesq);
                        handler.sendEmptyMessage(computePesq);
                    }
                });
    }

    private void stopAudioRecord1() {
        File file = new File(CacheConst.audioPath + File.separator + CacheConst.AUDIO_PHONE_NAME);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart" + File.separator
                + "form-data"), file);
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("AudioRecord", CacheConst.AUDIO_PHONE_NAME, requestBody)
                .build();
        Log.d("zzl", "stopAudioRecord: " + file.getName());
        Log.d("zzl", "stopAudioRecord: CacheConst.audioPath--" + CacheConst.audioPath);
        Log.d("zzl", "stopAudioRecord: CacheConst.AUDIO_PHONE_NAME--" + CacheConst.AUDIO_PHONE_NAME);
        Request request = new Request.Builder()
                .url(CacheConst.GLOBAL_IP + File.separator + "AudioVideo" + File.separator + "AudioRecord")
                .post(multipartBody)
                .build();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60 * 1000, TimeUnit.MILLISECONDS) // 连接超时
                .readTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS) // 读取超时
                .writeTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS) // 写入超时
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
                        YinHuaData.pesq = resArr[resArr.length - 1];
                        Log.d(TAG, "onResponse: YinHuaData.PESQ==>" + YinHuaData.pesq);
                        handler.sendEmptyMessage(computePesq);
                    }
                });
    }

    /**
     * onDestroy
     *
     * @return void
     * @date 2023/3/9 16:56
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
     * @date 2023/3/9 16:56
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * ClassName: RecordBinder
     * Description:
     *
     * @Author benchmark
     * Version 1.0
     */
    public class RecordBinder extends Binder {
        /**
         * getRecordService
         *
         * @return com.example.benchmark.service.GameVATestService
         * @date 2023/3/10 15:44
         */
        public GameVATestService getRecordService() {
            return GameVATestService.this;
        }
    }
}


