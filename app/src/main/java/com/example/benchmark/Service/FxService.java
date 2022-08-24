package com.example.benchmark.Service;

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
import android.media.projection.MediaProjectionManager;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
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
import com.example.benchmark.Data.YinHuaData;
import com.example.benchmark.R;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;
import com.example.benchmark.utils.CodeUtils;
import com.example.benchmark.utils.Recorder;
import com.example.benchmark.utils.ScoreUtil;
import com.example.benchmark.utils.ServiceUtil;
import com.example.benchmark.utils.TapUtil;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

public class FxService extends Service {
    private final int screenHeight = CacheUtil.getInt(CacheConst.KEY_SCREEN_HEIGHT);
    private final int screenWidth = CacheUtil.getInt(CacheConst.KEY_SCREEN_WIDTH);
    private final int screenDpi = CacheUtil.getInt(CacheConst.KEY_SCREEN_DPI);

    private final int STOP_RECORD = 111;
    private final int COMPUTE_PESQ = 222;

    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mediaProjection;
    private int resultCode;
    private Intent data;
    private Boolean isCheckTouch;
    private Boolean isCheckSoundFrame;
    private String checkPlatform;
    private Intent intent;
    private FxService service;

    private TapUtil tapUtil;


    //视频音频录制变量初始化
    private Recorder mRecorder;

    private boolean running;
    public static String path = "";
    private int width;
    private int height;
    private int dpi;
    private VirtualDisplay virtualDisplay;
    private MediaRecorder mediaRecorder;

    private
    //定义浮动窗口布局
    LinearLayout mFloatLayout;
    LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;
    private Context mContext;
    TextView mFloatView;
    LinearLayout btnMenu;
    Button btnToPrCode;
    Button btnToTap;
    Button btnToBack;
    Button btnToRecord;
    private long startTime;
    private long endTime;
    private int statusBarHeight;


    private static final String TAG = "TWT";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case STOP_RECORD:
                    btnToRecord.setClickable(false);
                    btnMenu.setVisibility(View.VISIBLE);
                    Toast.makeText(mContext, "录制结束，请耐心等待音频质量计算结果~", Toast.LENGTH_SHORT).show();
                case COMPUTE_PESQ:
                    if (YinHuaData.PESQ != null) {
                        Toast.makeText(mContext, (YinHuaData.platform_type + "音频质量计算完成~"), Toast.LENGTH_SHORT).show();
                    }
            }
        }
    };

    public void setPara(boolean isCheckTouch, boolean isCheckSoundFrame) {
        this.isCheckTouch = isCheckTouch;
        this.isCheckSoundFrame = isCheckTouch;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = FxService.this;
        service = this.service;
        tapUtil = TapUtil.getUtil();

        HandlerThread serviceThread = new HandlerThread("service_thread",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        serviceThread.start();
        running = false;
        mediaRecorder = new MediaRecorder();

        //resultCode = intent.getIntExtra("code", -1);
        //data = intent.getParcelableExtra("data");
        //checkPlatform = intent.getStringExtra(CacheConst.KEY_PLATFORM_NAME);
        //mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        //mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
//        if (mediaProjection == null) {`
//            Log.e(TAG, "media projection is null");
//        }
        //Bitmap bitmap = screenShot(mediaProjection);


    }


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
        wmParams = new LayoutParams();
        //获取WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        //设置window type
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            wmParams.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            wmParams.type = LayoutParams.TYPE_TOAST;
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
        wmParams.x = screenWidth - 50;
        wmParams.y = screenHeight / 4 * 3;
        //设置悬浮窗口长宽数据
        wmParams.width = LayoutParams.WRAP_CONTENT;
        wmParams.height = LayoutParams.WRAP_CONTENT;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
        //LinearLayout btnMenu = (LinearLayout) inflater.inflate(R.id.btnMenu,null);
        btnMenu = mFloatLayout.findViewById(R.id.btnMenu);
        btnMenu.setVisibility(View.GONE);
        mFloatView = (TextView) mFloatLayout.findViewById(R.id.textinfo);
        mWindowManager.addView(mFloatLayout, wmParams);

        //获取状态栏的高度
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        statusBarHeight = getResources().getDimensionPixelSize(resourceId);

        // handler.sendEmptyMessage(1);
        //浮动窗口按钮
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        mFloatView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean isclick = false;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                        wmParams.x = (int) event.getRawX() - mFloatView.getMeasuredWidth() / 2;
                        wmParams.y = (int) event.getRawY() - mFloatView.getMeasuredHeight() / 2 - statusBarHeight;
                        //Log.d("TWT", "onTouch: "+MainActivity.);
                        //刷新
                        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        endTime = System.currentTimeMillis();
                        //小于0.2秒被判断为点击
                        if ((endTime - startTime) > 200) {
                            isclick = false;
                        } else {
                            isclick = true;
                        }
                        break;
                }
                //响应点击事件
                if (isclick) {
                    mFloatView.setVisibility(View.GONE);
                    btnMenu.setVisibility(View.VISIBLE);
                    //Toast.makeText(mContext, "点击了", Toast.LENGTH_SHORT).show();
                    //点击按钮进行截屏bitmap形式

                }
                return true;
            }
        });//设置监听浮动窗口的触摸移动


        mFloatView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(FxService.this, "onClick", Toast.LENGTH_SHORT).show();
            }
        });


        btnToPrCode = btnMenu.findViewById(R.id.btnToPrCode);
        btnToPrCode.setOnTouchListener(new OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean isclick = false;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                        wmParams.x = (int) event.getRawX() - btnToPrCode.getMeasuredWidth() / 2;
                        wmParams.y = (int) event.getRawY() - btnToPrCode.getMeasuredHeight() - statusBarHeight;
                        //Log.d("TWT", "onTouch: "+MainActivity.);
                        //刷新
                        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        endTime = System.currentTimeMillis();
                        //小于0.2秒被判断为点击
                        if ((endTime - startTime) > 200) {
                            isclick = false;
                        } else {
                            isclick = true;
                        }
                        break;
                }
                //响应点击事件
                if (isclick) {
                    //mFloatView.setVisibility(View.GONE);
                    //btnMenu.setVisibility(View.VISIBLE);
                    toCatchScreen();
                    //点击按钮进行截屏bitmap形式
                }
                return true;
            }
        });//设置监听浮动窗口的触摸移动


        btnToTap = btnMenu.findViewById(R.id.btnToTap);
        btnToTap.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean isclick = false;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                        wmParams.x = (int) event.getRawX() - btnToTap.getMeasuredWidth() / 2;
                        wmParams.y = (int) event.getRawY() - btnToTap.getMeasuredHeight() - statusBarHeight;
                        //Log.d("TWT", "onTouch: "+MainActivity.);
                        //刷新
                        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        endTime = System.currentTimeMillis();
                        //小于0.2秒被判断为点击
                        if ((endTime - startTime) > 200) {
                            isclick = false;
                        } else {
                            isclick = true;
                        }
                        break;
                }
                //响应触控点击事件
                if (isclick) {
                    //这里写开启触控服务
                    Toast.makeText(mContext, "点击了开启触控服务", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onTouch: 点击了开启触控服务");
                    //startAutoTapService();
                    tapUtil.PhoneTouchTap();
                }
                return true;
            }
        });//设置监听浮动窗口的触摸移动

        btnToTap.setVisibility(isCheckTouch ? View.VISIBLE : View.GONE);

        btnToBack = btnMenu.findViewById(R.id.btnToBack);
        btnToBack.setOnTouchListener(new OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean isclick = false;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                        wmParams.x = (int) event.getRawX() - btnToBack.getMeasuredWidth() / 2;
                        wmParams.y = (int) event.getRawY() - btnToBack.getMeasuredHeight() - statusBarHeight;
                        //Log.d("TWT", "onTouch: "+MainActivity.);
                        //刷新
                        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        endTime = System.currentTimeMillis();
                        //小于0.2秒被判断为点击
                        if ((endTime - startTime) > 200) {
                            isclick = false;
                        } else {
                            isclick = true;
                        }
                        break;
                }
                //响应返回点击事件
                if (isclick) {
                    btnMenu.setVisibility(View.GONE);
                    mFloatView.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });//设置监听浮动窗口的触摸移动


        btnToRecord = btnMenu.findViewById(R.id.btnToRecord);
        btnToRecord.setOnTouchListener(new OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean isclick = false;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                        wmParams.x = (int) event.getRawX() - btnToBack.getMeasuredWidth() / 2;
                        wmParams.y = (int) event.getRawY() - btnToBack.getMeasuredHeight() - statusBarHeight;
                        //Log.d("TWT", "onTouch: "+MainActivity.);
                        //刷新
                        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        endTime = System.currentTimeMillis();
                        //小于0.2秒被判断为点击
                        if ((endTime - startTime) > 200) {
                            isclick = false;
                        } else {
                            isclick = true;
                        }
                        break;
                }
                //响应返回点击事件
                if (isclick) {
                    //Log.d("TWT", "screenWidth: "+screenWidth+" screenHeight"+screenHeight);
                    tapUtil.tap(screenWidth/2, screenHeight/2);
                    btnMenu.setVisibility(View.GONE);
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
                }
                return true;
            }
        });//设置监听浮动窗口的触摸移动
        btnToRecord.setVisibility(isCheckSoundFrame ? View.VISIBLE : View.GONE);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatLayout != null) {
            mWindowManager.removeView(mFloatLayout);
        }
        mediaProjection.stop();
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    public Bitmap screenShot() {
        Objects.requireNonNull(mediaProjection);
        @SuppressLint("WrongConstant")
        ImageReader imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 60);
        VirtualDisplay virtualDisplay = mediaProjection.createVirtualDisplay("screen", screenWidth, screenHeight, 1, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, imageReader.getSurface(), null, null);
        SystemClock.sleep(1000);
        //取最新的图片
        Image image = imageReader.acquireLatestImage();
        // Image image = imageReader.acquireNextImage();
        //释放 virtualDisplay,不释放会报错
        virtualDisplay.release();
        return image2Bitmap(image);
    }

    public static Bitmap image2Bitmap(Image image) {
        if (image == null) {
            System.out.println("image 为空");
            return null;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        //截取图片
        // Bitmap cutBitmap = Bitmap.createBitmap(bitmap,0,0,width/2,height/2);
        //压缩图片
        // Matrix matrix = new Matrix();
        // matrix.setScale(0.5F, 0.5F);
        // System.out.println(bitmap.isMutable());
        // bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        image.close();
        return bitmap;
    }

    private float getFloatDataFromJson(JSONObject jsonObject, String name) {
        Object target = jsonObject.getString(name);
        if (target == null) return 0F;
        else return Float.parseFloat(target.toString().split("吉")[0]);
    }

    private long getLongDataFromJson(JSONObject jsonObject, String name) {
        Object target = jsonObject.getString(name);
        if (target == null) return 0;
        else return Long.parseLong(target.toString());
    }

    private int getIntDataFromJson(JSONObject jsonObject, String name) {
        Object target = jsonObject.getString(name);
        if (target == null) return 0;
        else return Integer.parseInt(target.toString());
    }

    private JSONArray getListFromJson(JSONObject jsonObject, String name) {
        Object target = jsonObject.getString(name);
        Log.d("getIntDataFromJson", "getIntDataFromJson: ==>" + target);
        Log.d("getIntDataFromJson", "getIntDataFromJson: ==>" + jsonObject.get("endTimeList"));
        if (target == null) return null;
        else return JSON.parseArray(String.valueOf(target));
    }

    private String getCloudListDataFromJson(JSONObject jsonObject, String name) {
        Object target = jsonObject.getString(name);
        //Log.d("zzl", "getCloudListDataFromJson: JSONObject===>" +jsonObject);
        //Log.d("zzl", "getCloudListDataFromJson: target==>" + target);

        if (target == null) return null;
        else return (String) target;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void toCatchScreen() {
        Bitmap bitmap = screenShot();
        Log.d(TAG, "bitmap getWidth: " + bitmap.getWidth() + "  getHeight"+ bitmap.getHeight());
        String result = CodeUtils.parseCode(bitmap);
        Log.e("QT-1", result+"123");
        Log.e("QT-1", "123"+result);
        if ("{}".equals(result)) {
            // 空数据，点击无效
            return;
        }
        JSONObject JsonData = JSON.parseObject(result);
        // 信息获取
        Log.e("QT-2", JsonData.toJSONString());
        ScoreUtil.calcAndSaveCPUScores(
                //(String) JsonData.get("cpuName"),
                getIntDataFromJson(JsonData, "cpuCores")
        );
        ScoreUtil.calcAndSaveGPUScores(
                (String) JsonData.get("gpuVendor"),
                (String) JsonData.get("gpuRenderer"),
                (String) JsonData.get("gpuVersion")
        );
        ScoreUtil.calcAndSaveRAMScores(
                //getString(JsonData, "availRam"),
                //getString(JsonData, "totalRam")
                (String) JsonData.get("availRam"),
                (String) JsonData.get("totalRam")
        );
        ScoreUtil.calcAndSaveROMScores(
                //getString(JsonData, "availStorage"),
                //getString(JsonData, "totalStorage")
                (String) JsonData.get("availStorage"),
                (String) JsonData.get("totalStorage")

        );
        ScoreUtil.calcAndSaveFluencyScores(
                getFloatDataFromJson(JsonData, "avergeFPS"),
                getFloatDataFromJson(JsonData, "frameShakingRate"),
                getFloatDataFromJson(JsonData, "lowFrameRate"),
                getFloatDataFromJson(JsonData, "frameInterval"),
                getFloatDataFromJson(JsonData, "jankCount"),
                getFloatDataFromJson(JsonData, "stutterRate")
        );

        ScoreUtil.calcAndSaveTouchScores(
                getCloudListDataFromJson(JsonData, "cloudDownTimeList"),
                getCloudListDataFromJson(JsonData, "cloudSpendTimeList")
        );


        Log.d("TWT", "云端测试数据JSON: " + JsonData);
        //ScoreUtil.calcAndSaveTouchScores(
        //getFloatDataFromJson(JsonData, "averageAccuracy"),
        //getFloatDataFromJson(JsonData, "responseTime"),
        //getFloatDataFromJson(JsonData, "averageResponseTime")
        //);

        //for (int i = 0; i < 5; i++) {
        //    //Log.d(TAG, "toCatchScreen: ");
        //    //Log.d("TWT", "tapTimeOnCloud"+i+":"+JsonData.getInteger("tapTimeOnCloud"+i));
        //    Log.d("TWT", "tapTimeOnCloud" + i + ":" + JsonData.getLong("tapTimeOnCloud" + i));
        //}
        //JsonData.getInteger("tapTimeOnCloud"+i);

        //getListFromJson(JsonData, "cloudTapTimes1");


        if (JsonData.get("resolution") != null) {
            ScoreUtil.calcAndSaveSoundFrameScores(
                    (String) JsonData.get("resolution"),
                    getFloatDataFromJson(JsonData, "maxdifferencevalue")
            );
        }
        CacheUtil.put(CacheConst.KEY_PERFORMANCE_IS_MONITORED, true);
        Toast.makeText(FxService.this, "测试结束！", Toast.LENGTH_SHORT).show();
        ServiceUtil.backToCePingActivity(FxService.this);
        stopSelf();
    }

    public void setMediaProject(MediaProjection project) {
        mediaProjection = project;
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
                    .connectTimeout(100 * 60 * 1000, TimeUnit.MILLISECONDS)//连接超时
                    .readTimeout(100 * 60 * 1000, TimeUnit.MILLISECONDS)//读取超时
                    .writeTimeout(100 * 60 * 1000, TimeUnit.MILLISECONDS)//写入超时
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
                        }
                    });
        } else {
            Log.d(TAG, "stopAudioRecord: 不是云手机平台==》" + platformKind);
            Toast.makeText(service, platformKind, Toast.LENGTH_SHORT).show();
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
                    .connectTimeout(100 * 60 * 1000, TimeUnit.MILLISECONDS)//连接超时
                    .readTimeout(100 * 60 * 1000, TimeUnit.MILLISECONDS)//读取超时
                    .writeTimeout(100 * 60 * 1000, TimeUnit.MILLISECONDS)//写入超时
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
            Log.d("zzl", "stopAudioRecord: CacheConst.AUDIO_NAME--" + CacheConst.AUDIO_PHONE_NAME);
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
        } else {
            Log.d(TAG, "stopAudioRecord: 不是云手机平台==》" + platformKind);
            Toast.makeText(service, platformKind, Toast.LENGTH_SHORT).show();
        }


    }
    //stopSelf();

    private void createVirtualDisplay() {
        virtualDisplay = mediaProjection.createVirtualDisplay("MainScreen", width, height, dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mediaRecorder.getSurface(), null, null);
    }

    private void initRecorder() {
        try {
            //mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
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

    @Override
    public IBinder onBind(Intent intent) {
        this.isCheckTouch = intent.getBooleanExtra("isCheckTouch", false);
        this.isCheckSoundFrame = intent.getBooleanExtra("isCheckSoundFrame", false);
        createFloatView();
        return new RecordBinder();
    }

    public class RecordBinder extends Binder {
        public FxService getRecordService() {
            return FxService.this;
        }
    }
}


