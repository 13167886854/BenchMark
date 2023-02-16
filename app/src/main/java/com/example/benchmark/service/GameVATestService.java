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

    private ImageReader mImageReader;

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
    TextView KaCa;
    private long startTime;
    private long endTime;
    //private boolean isColor=true;
    private boolean isRecording=false;
    private int statusBarHeight;

    private LinearLayout menu2;

    private GameVATestService service;
    private VirtualDisplay mVirtualDisplay = null;

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
                    if (YinHuaData.pesq != null) {
                        Toast.makeText(mContext, (YinHuaData.platformType + "音频质量计算完成~"), Toast.LENGTH_SHORT).show();
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
        //mFloatView.setVisibility(View.GONE);
        KaCa = (TextView)mFloatLayout.findViewById(R.id.KaCa);
        KaCa.setVisibility(View.GONE);
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
                    timer.schedule(task, 20000);
                    //Toast.makeText(mContext, "点击了", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

//        KaCa.setOnTouchListener(new OnTouchListener() {
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
//                        wmParams.x = (int) event.getRawX() - KaCa.getMeasuredWidth()/2;
//                        wmParams.y = (int) event.getRawY() - KaCa.getMeasuredHeight()/2-statusBarHeight;
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
//                if (isclick) {
//                    KaCa.setVisibility(View.GONE);
//                    Handler handler1 = new Handler();
//                    handler1.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            startVirtual();
//                        }
//                    },500);
//                    Handler handler2 = new Handler();
//                    handler2.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            startCapture();
//                        }
//                    },1500);
//                    Handler handler3 = new Handler();
//                    handler3.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mFloatView.setVisibility(View.VISIBLE);
//                        }
//                    },2000);
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


    public void startVirtual(){
        if (mediaProjection != null) {
            Log.i(TAG, "want to display virtual");
//            virtualDisplay2();
        }else{
            Log.e(TAG, "start screen capture intent");
            Log.e(TAG, "want to build mediaprojection and display virtual");
        }
    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    private void virtualDisplay2(){
//        DisplayMetrics metrics = new DisplayMetrics();
//        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
//        windowManager.getDefaultDisplay().getMetrics(metrics);
//        width = metrics.widthPixels;
//        height = metrics.heightPixels;
//        dpi = metrics.densityDpi;
//        mImageReader = ImageReader.newInstance(width, height, 0x1, 2);
//        mVirtualDisplay = mediaProjection.createVirtualDisplay("screen-mirror",
//                    width, height, dpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
//                mImageReader.getSurface(), null, null);
//        //Log.e(TAG, "width1: "+width );
//        //Log.e(TAG, "height1: "+height );
//        Log.i(TAG, "virtual displayed");
//    }

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

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public void startCapture(){
//        DisplayMetrics metrics = new DisplayMetrics();
//        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
//        windowManager.getDefaultDisplay().getMetrics(metrics);
//        width = metrics.widthPixels;
//        height = metrics.heightPixels;
//        dpi = metrics.densityDpi;
//
//        //mImageReader = ImageReader.newInstance(width, height, ImageFormat.RGB_565, 2);
//        //Log.e(TAG, "width2: "+width );
//        //Log.e(TAG, "height2: "+height );
//
//        Image image = mImageReader.acquireLatestImage();
//        int imgaeWidth = image.getWidth();
//        int imageHeight = image.getHeight();
//        //Log.e(TAG, "imgaeWidth: "+imgaeWidth );
//        //Log.e(TAG, "imgaeWidthimageHeight: "+imageHeight );
//        final Image.Plane[] planes = image.getPlanes();
//        final ByteBuffer buffer = planes[0].getBuffer();
//        int pixelStride = planes[0].getPixelStride();
//        int rowStride = planes[0].getRowStride();
//        int rowPadding = rowStride - pixelStride * imgaeWidth;
//        Bitmap bitmap = Bitmap.createBitmap(imgaeWidth+rowPadding/pixelStride, imageHeight, Bitmap.Config.ARGB_8888);
//        bitmap.copyPixelsFromBuffer(buffer);
//        bitmap = Bitmap.createBitmap(bitmap, 0, 0,imgaeWidth, imageHeight);
//        image.close();
//        Log.i(TAG, "image data captured");
//        if(bitmap != null) {
//            try{
//                String path = getsaveDirectory()+CacheConst.IMAGE_GAME;
//                File fileImage = new File(path);
//                if(!fileImage.exists()){
//                    fileImage.createNewFile();
//                    Log.i(TAG, "image file created");
//                    }
//                FileOutputStream out = new FileOutputStream(fileImage);
//                if(out != null){
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//                    out.flush();
//                    out.close();
//                    Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                    Uri contentUri = Uri.fromFile(fileImage);
//                    media.setData(contentUri);
//                    this.sendBroadcast(media);
//                    Log.i(TAG, "screen image saved");
//                    }
//                }catch(FileNotFoundException e) {
//                    e.printStackTrace();
//                }catch (IOException e){
//                    e.printStackTrace();
//                }
//        }
//
//        OkHttpClient client = new OkHttpClient.Builder()
//                .connectTimeout(100 * 60 * 1000, TimeUnit.MILLISECONDS)//连接超时
//                .readTimeout(100 * 60 * 1000, TimeUnit.MILLISECONDS)//读取超时
//                .writeTimeout(100 * 60 * 1000, TimeUnit.MILLISECONDS)//写入超时
//                .build();
//        MediaType type = MediaType.parse("application/octet-stream");//"text/xml;charset=utf-8"
//        // file是要上传的文件 File()
//        File file = new File(getsaveDirectory()  + CacheConst.IMAGE_GAME);
//        //Log.d(TAG, "onClick: " + AudioData.FILE_PATH);
//        //Log.d(TAG, "onClick: " + file.exists());
//        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        //Log.d(TAG, "onClick: "+AudioData.FILE_NAME);
//        MultipartBody multipartBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("image", CacheConst.IMAGE_GAME, requestBody)
//                //.addFormDataPart("AudioRecord",CacheConst.AUDIO_NAME, requestBody)
//                .build();
//
//        Request request = new Request.Builder()
//                .url(CacheConst.ALIYUN_IP + "/AudioVideo/image")
//                .post(multipartBody)
//                .build();
//        //Log.d(TAG, "onClick: " + request.header("Content-Type"));
//
//
//        client.newCall(request)
//                .enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        Log.d(TAG, "onFailure: call " + call);
//                        Log.d(TAG, "onFailure: e" + e.toString());
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        Log.d(TAG, "onResponse: response==>" + response);
//                        Log.d(TAG, "onResponse: response==>" + response.body());
//                        String res = response.body().string();
//                        Log.d(TAG, "onResponse:" + res);
//                        double x = Double.parseDouble(res);
//                        double score = 0;
//                        //Log.e(TAG, "onResponse: "+CacheUtil.put(CacheConst.KEY_PLATFORM_NAME, checked_plat);.);
//                        Log.e(TAG, "onResponse: "+CacheUtil.getString(CacheConst.KEY_PLATFORM_NAME));
////                        if(CacheUtil.getString(CacheConst.KEY_PLATFORM_NAME).equals(CacheConst.PLATFORM_NAME_Tencent_GAME)){
////                            double min = 915;
////                            double max = 920;
////                            score = (x-min)/(max-min);
////                        }else if(CacheUtil.getString(CacheConst.KEY_PLATFORM_NAME).equals(CacheConst.PLATFORM_NAME_MI_GU_GAME)){
////                            double min = 573;
////                            double max = 568;
////                            score = (x-min)/(max-min);
////                        }else if(CacheUtil.getString(CacheConst.KEY_PLATFORM_NAME).equals(CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_GAME)){
////                            double min = 431;
////                            double max = 443;
////                            score = (x-min)/(max-min);
////                        }
////                        if(CacheUtil.getString(CacheConst.KEY_PLATFORM_NAME).equals(CacheConst.PLATFORM_NAME_Tencent_GAME)){
////                            if(x<917){//清晰度小于916判断为标清 720p
////                                YinHuaData.Resolution = "1280X720";
////                            }else{//超过917判断为 高清、超清 1080p
////                                YinHuaData.Resolution = "1920X1080";
////                            }
////                        }else if(CacheUtil.getString(CacheConst.KEY_PLATFORM_NAME).equals(CacheConst.PLATFORM_NAME_MI_GU_GAME)){
////                            if(x<600){//清晰度小于600判断为高清、超清、蓝光 720p
////                                YinHuaData.Resolution = "1280X720";
////                            }else{//超过600判断为原画1080p
////                                YinHuaData.Resolution = "1920X1080";
////                            }
////                        }else if(CacheUtil.getString(CacheConst.KEY_PLATFORM_NAME).equals(CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_GAME)){
////                            if(x<436){//消息都小于436判断为标清
////                                YinHuaData.Resolution = "1280X720";
////                            }else{
////                                YinHuaData.Resolution = "1920X1080";
////                            }
////                        }
//
//                        //Log.d(TAG, "score: "+score);
//                        //double max = 574.199755;
//                        //double min = 566.853584;
//                        //double score = (x - min)/(max -min);
//                        //Log.e(TAG, "score: "+score);
////                        }
//
//                    }
//                });
//    }


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
        String platformKind = YinHuaData.platformType;
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
                            YinHuaData.psnr = resArr[1];
                            YinHuaData.ssim = resArr[3];
                            YinHuaData.resolution = resArr[5];

                            Log.d(TAG, "onResponse: YinHuaData.PSNR==>" + YinHuaData.psnr);
                            Log.d(TAG, "onResponse: YinHuaData.SSIM==>" + YinHuaData.ssim);
                            Log.d(TAG, "onResponse: YinHuaData.SSIM==>" + YinHuaData.resolution);
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

                            YinHuaData.psnr = resArr[1];
                            YinHuaData.ssim = resArr[3];
                            YinHuaData.resolution = resArr[5];
                            Log.d(TAG, "onResponse: YinHuaData.PSNR==>" + YinHuaData.psnr);
                            Log.d(TAG, "onResponse: YinHuaData.SSIM==>" + YinHuaData.ssim);
                            Log.d(TAG, "onResponse: YinHuaData.SSIM==>" + YinHuaData.resolution);
                            Log.d(TAG, "onResponse: YinHuaData.PSNR==>" + YinHuaData.psnr);
                            Log.d(TAG, "onResponse: YinHuaData.SSIM==>" + YinHuaData.ssim);
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
            String platformKind = YinHuaData.platformType;
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
            Log.e(TAG, "initRecorder: ", e);
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
                Log.e(TAG, "stopAudioRecord: ", e);
            }
        }
        // 平台类型
        String platformKind = YinHuaData.platformType;
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
                            YinHuaData.pesq = resArr[resArr.length - 1];
                            Log.d(TAG, "onResponse: YinHuaData.PESQ==>" + YinHuaData.pesq);
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
                            YinHuaData.pesq = resArr[resArr.length - 1];
                            Log.d(TAG, "onResponse: YinHuaData.PESQ==>" + YinHuaData.pesq);
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


