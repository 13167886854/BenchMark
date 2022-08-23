package com.example.benchmark.Service;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.os.Looper;
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

import com.example.benchmark.Activity.AudioVideoActivity;
import com.example.benchmark.Activity.CePingActivity;
import com.example.benchmark.Activity.TestGameTouchActivity;
import com.example.benchmark.Data.YinHuaData;
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

public class GameVATestService extends Service {
    private static final int START_RECORD = 1;
    private static final int STOP_RECORD = 2;

    private final int STOP_RECORD2 = 111;
    private final int COMPUTE_PESQ = 222;


    private final int screenHeight = CacheUtil.getInt(CacheConst.KEY_SCREEN_HEIGHT);
    private final int screenWidth = CacheUtil.getInt(CacheConst.KEY_SCREEN_WIDTH);
    private MediaProjection mediaProjection;
    private MediaRecorder mediaRecorder;
    private VirtualDisplay virtualDisplay;
    private String path = "";
    private boolean running;
    private int width = screenWidth;
    private int height = screenHeight;
    private int dpi;

    private TapUtil tapUtil;

    //视频音频录制变量初始化
    private Recorder mRecorder;


    private boolean isAble=true;
    //定义浮动窗口布局
    LinearLayout mFloatLayout;
    LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;
    private Context mContext;
    TextView mFloatView;
    TextView mFloatView2;
    private long startTime;
    private long endTime;
    //private boolean isColor=true;
    private boolean isRecording=false;
    private int statusBarHeight;

    private LinearLayout menu2;

    private GameVATestService service;

    //private Messenger mMessenger;

    private static final String TAG = "TWT";
    Handler handler=new Handler(){
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case START_RECORD:
                    //mFloatView.setText("停止");
                    isRecording=!isRecording;
                    startRecord();
                    break;

                case STOP_RECORD:
                    isRecording=!isRecording;
                    //点击结束录制后休息1s后才能继续录制
                    isAble = false;
                    stopRecord();

                    Intent intent = new Intent(mContext, AudioVideoActivity.class);
                    intent.putExtra("path",path);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
//                case STOP_RECORD2:
//                    //btnToRecord.setClickable(false);
//                    menu2.setVisibility(View.VISIBLE);
//                    Toast.makeText(mContext, "录制结束，请耐心等待音频质量计算结果~", Toast.LENGTH_SHORT).show();
//                    break;
                case COMPUTE_PESQ:
                    if (YinHuaData.PESQ != null) {
                        Toast.makeText(mContext, (YinHuaData.platform_type + "音频质量计算完成~"), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    public void onCreate()
    {
        super.onCreate();
        service = GameVATestService.this;
        tapUtil = TapUtil.getUtil();
        mContext= GameVATestService.this;
        running = false;
        mediaRecorder = new MediaRecorder();
        createFloatView();

    }



    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind:12132 ");
        return new RecordBinder();
    }

    public void setConfig(int width, int height, int dpi) {
        this.width = width;
        this.height = height;
        this.dpi = dpi;
    }

    private void createFloatView()
    {
        Log.d(TAG, "createFloatView: 1212");
        wmParams = new LayoutParams();
        //获取WindowManagerImpl.CompatModeWrapper
        mWindowManager =  (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        //设置window type
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
            wmParams.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else{
            wmParams.type= LayoutParams.TYPE_TOAST;
        }
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags =
//          LayoutParams.FLAG_NOT_TOUCH_MODAL |
                LayoutParams.FLAG_NOT_FOCUSABLE
//          LayoutParams.FLAG_NOT_TOUCHABLE
        ;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.START | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值(设置最大直接显示在右下角)
        wmParams.x = screenWidth/2;
        wmParams.y =screenHeight;
        Log.d(TAG, "screenWidth: "+screenWidth+"  screenHeight: "+screenHeight);
        //设置悬浮窗口长宽数据
        wmParams.width = LayoutParams.WRAP_CONTENT;
        wmParams.height = LayoutParams.WRAP_CONTENT;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.record_float, null);
        menu2 = (LinearLayout) mFloatLayout.findViewById(R.id.menu2);
        mFloatView = (TextView)mFloatLayout.findViewById(R.id.recordText);
        //mFloatView2 = (TextView)mFloatLayout.findViewById(R.id.recordText2);
        mWindowManager.addView(mFloatLayout, wmParams);

        //获取状态栏的高度
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        statusBarHeight = getResources().getDimensionPixelSize(resourceId);

        // handler.sendEmptyMessage(1);
        //浮动窗口按钮
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        //设置监听浮动窗口的触摸移动
        mFloatView.setOnTouchListener(new OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean isclick=false;
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startTime=System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                        wmParams.x = (int) event.getRawX() - mFloatView.getMeasuredWidth()/2;
                        wmParams.y = (int) event.getRawY() - mFloatView.getMeasuredHeight()/2-statusBarHeight;
                        //Log.d("TWT", "onTouch: "+MainActivity.);
                        //刷新
                        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        endTime=System.currentTimeMillis();
                        //小于0.2秒被判断为点击
                        if ((endTime - startTime) > 200) {
                            isclick = false;
                        } else {
                            isclick = true;
                        }
                        break;
                }
                //响应点击事件
                if (isclick&&isAble) {
                    Log.d(TAG, "screenWidth: "+screenWidth+" screenWidth"+screenWidth);
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
                    timer.schedule(task, 45000);
                    //Toast.makeText(mContext, "点击了", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
//        mFloatView2.setOnTouchListener(new OnTouchListener() {
//            @RequiresApi(api = Build.VERSION_CODES.Q)
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                boolean isclick=false;
//                switch (event.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//                        startTime=System.currentTimeMillis();
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
//                        wmParams.x = (int) event.getRawX() - mFloatView2.getMeasuredWidth()/2;
//                        wmParams.y = (int) event.getRawY() - mFloatView2.getMeasuredHeight()/2-statusBarHeight;
//                        //Log.d("TWT", "onTouch: "+MainActivity.);
//                        //刷新
//                        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        endTime=System.currentTimeMillis();
//                        //小于0.2秒被判断为点击
//                        if ((endTime - startTime) > 200) {
//                            isclick = false;
//                        } else {
//                            isclick = true;
//                        }
//                        break;
//                }
//                //响应点击事件
//                if (isclick&&isAble) {
//
//                }
//                return true;
//            }
//        });

        mFloatView.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                //Toast.makeText(RecordService.this, "onClick", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setMediaProject(MediaProjection project) {
        mediaProjection = project;
    }




    public boolean startRecord() {
        if (mediaProjection == null || running) {
            return false;
        }
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        dpi = metrics.densityDpi;
        initRecorder();
        createVirtualDisplay();
        mediaRecorder.start();
        running = true;
        Log.d(TAG, "begin:开始录制 ");
        return true;
    }


    public boolean stopRecord() {
        if (!running) {
            return false;
        }

        running = false;
        mediaRecorder.stop();
        mediaRecorder.reset();
        virtualDisplay.release();
        stopSelf();


        if(mFloatLayout != null)
        {
            mWindowManager.removeView(mFloatLayout);
        }
        return true;
    }

    private void createVirtualDisplay() {
        virtualDisplay = mediaProjection.createVirtualDisplay("MainScreen", width, height, dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mediaRecorder.getSurface(), null, null);
    }


    public void startVideoRecord() {
        if (mediaProjection == null || running) {
            Log.d("TWT", "startRecord: mediaProjection == null");
            return;
        }
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        dpi = metrics.densityDpi;

        Log.d("TWT", "startRecord: start");

        initRecorder();
        createVirtualDisplay();
        mediaRecorder.start();

        running = true;
        return;
    }

    /**
     * 停止视频录制
     */
    public void stopVideoRecord() {
        if (!running) {
            return;
        }
        Log.d("TWT", "startRecord: stop");
        running = false;
        mediaRecorder.stop();
        mediaRecorder.reset();
        virtualDisplay.release();
        //mediaProjection.stop();

        // 平台类型
        String platformKind = YinHuaData.platform_type;
        Log.d("zzl", "stopAudioRecord: 平台类型==> " + platformKind);
        // 如果是云手机
        if (platformKind.equals(CacheConst.PLATFORM_NAME_RED_FINGER_CLOUD_PHONE) ||
                platformKind.equals(CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_PHONE) ||
                platformKind.equals(CacheConst.PLATFORM_NAME_E_CLOUD_PHONE) ||
                platformKind.equals(CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_PHONE) ||
                platformKind.equals(CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_GAME)) {
            //OkHttpClient client = new OkHttpClient();
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)//连接超时
                    .readTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)//读取超时
                    .writeTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)//写入超时
                    .build();
            MediaType type = MediaType.parse("application/octet-stream");//"text/xml;charset=utf-8"
            // file是要上传的文件 File()
            File file = new File(CacheConst.VIDEO_PATH + "/" + CacheConst.VIDEO_PHONE_NAME);
            //Log.d(TAG, "onClick: " + AudioData.FILE_PATH);
            //Log.d(TAG, "onClick: " + file.exists());
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            //Log.d(TAG, "onClick: "+AudioData.FILE_NAME);
            MultipartBody multipartBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("VideoRecord", CacheConst.VIDEO_PHONE_NAME, requestBody)
                    //.addFormDataPart("AudioRecord",CacheConst.AUDIO_NAME, requestBody)
                    .build();
            Log.d("zzl", "stopAudioRecord: " + file.getName());
            Log.d("zzl", "stopAudioRecord: CacheConst.AUDIO_PATH--" + CacheConst.VIDEO_PATH);
            Log.d("zzl", "stopAudioRecord: CacheConst.AUDIO_NAME--" + CacheConst.VIDEO_PHONE_NAME);
            Request request = new Request.Builder()
                    .url(CacheConst.GLOBAL_IP + "/AudioVideo/VideoRecord")
                    .post(multipartBody)
                    .build();
            //Log.d(TAG, "onClick: " + request.header("Content-Type"));


            client.newCall(request)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d(TAG, "onFailure: call " + call);
                            Log.d(TAG, "onFailure: e" + e.toString());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.d(TAG, "onResponse: response==>" + response);
                            Log.d(TAG, "onResponse: response==>" + response.body());
                            String res = response.body().string();
                            Log.d(TAG, "onResponse:" + res);
                            String[] resArr = res.split("=");
                            Log.d(TAG, "onResponse: resArr  " + Arrays.toString(resArr));
                            YinHuaData.PSNR = resArr[1];
                            YinHuaData.SSIM = resArr[3];
                            Log.d(TAG, "onResponse: YinHuaData.PSNR==>" + YinHuaData.PSNR);
                            Log.d(TAG, "onResponse: YinHuaData.SSIM==>" + YinHuaData.SSIM);
                            //handler.sendEmptyMessage(COMPUTE_PESQ);


                        }
                    });
        } else {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)//连接超时
                    .readTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)//读取超时
                    .writeTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)//写入超时
                    .build();
            MediaType type = MediaType.parse("application/octet-stream");//"text/xml;charset=utf-8"
            // file是要上传的文件 File()
            File file = new File(CacheConst.VIDEO_PATH + "/" + CacheConst.VIDEO_GAME_NAME);
            //Log.d(TAG, "onClick: " + AudioData.FILE_PATH);
            //Log.d(TAG, "onClick: " + file.exists());
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            //Log.d(TAG, "onClick: "+AudioData.FILE_NAME);
            MultipartBody multipartBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("VideoRecord", CacheConst.VIDEO_GAME_NAME, requestBody)
                    //.addFormDataPart("AudioRecord",CacheConst.AUDIO_NAME, requestBody)
                    .build();
            Log.d("zzl", "stopAudioRecord: " + file.getName());
            Log.d("zzl", "stopAudioRecord: CacheConst.AUDIO_PATH--" + CacheConst.VIDEO_PATH);
            Log.d("zzl", "stopAudioRecord: CacheConst.VIDEO_GAME_NAME--" + CacheConst.VIDEO_GAME_NAME);
            Request request = new Request.Builder()
                    .url(CacheConst.GLOBAL_IP + "/AudioVideo/VideoRecord")
                    .post(multipartBody)
                    .build();
            //Log.d(TAG, "onClick: " + request.header("Content-Type"));


            client.newCall(request)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d(TAG, "onFailure: call " + call);
                            Log.d(TAG, "onFailure: e" + e.toString());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.d(TAG, "onResponse: response==>" + response);
                            Log.d(TAG, "onResponse: response==>" + response.body());
                            String res = response.body().string();
                            String[] resArr = res.split("=");
                            Log.d(TAG, "onResponse: resArr  " + Arrays.toString(resArr));
                            YinHuaData.PSNR = resArr[1];
                            YinHuaData.SSIM = resArr[3];
                            Log.d(TAG, "onResponse: YinHuaData.PSNR==>" + YinHuaData.PSNR);
                            Log.d(TAG, "onResponse: YinHuaData.SSIM==>" + YinHuaData.SSIM);
                            //Log.d(TAG, "onResponse: YinHuaData.PESQ==>" + YinHuaData.PESQ);
                            //handler.sendEmptyMessage(COMPUTE_PESQ);
                            Intent intent = new Intent(getApplicationContext(), CePingActivity.class);
                            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }
                    });
        }

    }

    private void initRecorder() {
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            // 平台类型
            String platformKind = YinHuaData.platform_type;
            Log.d("zzl", "stopAudioRecord: 平台类型==> " + platformKind);
            // 如果是云手机
            if (platformKind.equals(CacheConst.PLATFORM_NAME_RED_FINGER_CLOUD_PHONE) ||
                    platformKind.equals(CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_PHONE) ||
                    platformKind.equals(CacheConst.PLATFORM_NAME_E_CLOUD_PHONE) ||
                    platformKind.equals(CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_PHONE) ||
                    platformKind.equals(CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_GAME)) {
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
        } catch (Exception e) {
            Log.e("TWT", "initRecorder: " + e.toString());
        }
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getsaveDirectory() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "ScreenRecorder" + "/";
            //CacheConst.VIDEO_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "ScreenRecorder" + "/";
            CacheConst.VIDEO_PATH = rootDir;
            File file = new File(rootDir);
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    return null;
                }
            }
            return rootDir;
        } else {
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    void record() {
        boolean isSupported;
        mRecorder = new Recorder();
        isSupported = mRecorder.start(this, mediaProjection);
        if (!isSupported) {
            mediaProjection.stop();
            stopSelf();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void startAudioRecord() {
        record();
    }

    /**
     * 停止录音
     */
    public void stopAudioRecord() {
        if (mRecorder != null) {
            try {
                mRecorder.startProcessing();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 平台类型
        String platformKind = YinHuaData.platform_type;
        Log.d("zzl", "stopAudioRecord: 平台类型==> " + platformKind);
        // 如果是云手机
        if (platformKind.equals(CacheConst.PLATFORM_NAME_RED_FINGER_CLOUD_PHONE) ||
                platformKind.equals(CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_PHONE) ||
                platformKind.equals(CacheConst.PLATFORM_NAME_E_CLOUD_PHONE) ||
                platformKind.equals(CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_PHONE) ||
                platformKind.equals(CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_GAME)) {
            //OkHttpClient client = new OkHttpClient();
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)//连接超时
                    .readTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)//读取超时
                    .writeTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)//写入超时
                    .build();
            MediaType type = MediaType.parse("application/octet-stream");//"text/xml;charset=utf-8"
            // file是要上传的文件 File()
            File file = new File(CacheConst.AUDIO_PATH + "/" + CacheConst.AUDIO_PHONE_NAME);
            //Log.d(TAG, "onClick: " + AudioData.FILE_PATH);
            //Log.d(TAG, "onClick: " + file.exists());
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            //Log.d(TAG, "onClick: "+AudioData.FILE_NAME);
            MultipartBody multipartBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("AudioRecord", CacheConst.AUDIO_PHONE_NAME, requestBody)
                    //.addFormDataPart("AudioRecord",CacheConst.AUDIO_NAME, requestBody)
                    .build();
            Log.d("zzl", "stopAudioRecord: " + file.getName());
            Log.d("zzl", "stopAudioRecord: CacheConst.AUDIO_PATH--" + CacheConst.AUDIO_PATH);
            Log.d("zzl", "stopAudioRecord: CacheConst.AUDIO_PHONE_NAME--" + CacheConst.AUDIO_PHONE_NAME);
            Request request = new Request.Builder()
                    .url(CacheConst.GLOBAL_IP + "/AudioVideo/AudioRecord")
                    .post(multipartBody)
                    .build();
            //Log.d(TAG, "onClick: " + request.header("Content-Type"));


            client.newCall(request)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d(TAG, "onFailure: call " + call);
                            Log.d(TAG, "onFailure: e" + e.toString());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.d(TAG, "onResponse: response==>" + response);
                            Log.d(TAG, "onResponse: response==>" + response.body());
                            String res = response.body().string();
                            String[] resArr = res.split(" ");
                            Log.d(TAG, "onResponse: resArr  " + Arrays.toString(resArr));
                            YinHuaData.PESQ = resArr[resArr.length - 1];
                            Log.d(TAG, "onResponse: YinHuaData.PESQ==>" + YinHuaData.PESQ);
                            handler.sendEmptyMessage(COMPUTE_PESQ);
//                            Looper.prepare();
//                            Toast.makeText(service,"音频测试结束",Toast.LENGTH_SHORT);
//                            Looper.loop();

                        }
                    });
        } else {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)//连接超时
                    .readTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)//读取超时
                    .writeTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)//写入超时
                    .build();
            MediaType type = MediaType.parse("application/octet-stream");//"text/xml;charset=utf-8"
            // file是要上传的文件 File()
            File file = new File(CacheConst.AUDIO_PATH + "/" + CacheConst.AUDIO_GAME_NAME);
            //Log.d(TAG, "onClick: " + AudioData.FILE_PATH);
            //Log.d(TAG, "onClick: " + file.exists());
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            //Log.d(TAG, "onClick: "+AudioData.FILE_NAME);
            MultipartBody multipartBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("AudioRecord", CacheConst.AUDIO_GAME_NAME, requestBody)
                    //.addFormDataPart("AudioRecord",CacheConst.AUDIO_NAME, requestBody)
                    .build();
            Log.d("zzl", "stopAudioRecord: " + file.getName());
            Log.d("zzl", "stopAudioRecord: CacheConst.AUDIO_PATH--" + CacheConst.AUDIO_PATH);
            Log.d("zzl", "stopAudioRecord: CacheConst.AUDIO_GAME_NAME--" + CacheConst.AUDIO_GAME_NAME);
            Request request = new Request.Builder()
                    .url(CacheConst.GLOBAL_IP + "/AudioVideo/AudioRecord")
                    .post(multipartBody)
                    .build();
            //Log.d(TAG, "onClick: " + request.header("Content-Type"));


            client.newCall(request)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d(TAG, "onFailure: call " + call);
                            Log.d(TAG, "onFailure: e" + e.toString());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.d(TAG, "onResponse: response==>" + response);
                            Log.d(TAG, "onResponse: response==>" + response.body());
                            String res = response.body().string();
                            String[] resArr = res.split(" ");
                            Log.d(TAG, "onResponse: resArr  " + Arrays.toString(resArr));
                            YinHuaData.PESQ = resArr[resArr.length - 1];
                            Log.d(TAG, "onResponse: YinHuaData.PESQ==>" + YinHuaData.PESQ);
                            handler.sendEmptyMessage(COMPUTE_PESQ);

                        }
                    });
        }


    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
//        if(mFloatLayout != null)
//        {
//            mWindowManager.removeView(mFloatLayout);
//        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    public class RecordBinder extends Binder {
        public GameVATestService getRecordService() {
            return GameVATestService.this;
        }
    }

}


