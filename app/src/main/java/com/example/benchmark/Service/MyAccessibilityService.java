package com.example.benchmark.Service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.palette.graphics.Palette;

import com.example.benchmark.Activity.CePingActivity;
import com.example.benchmark.R;
import com.example.benchmark.utils.ApkUtil;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;
import com.example.benchmark.utils.ScoreUtil;
import com.example.benchmark.utils.ServiceUtil;
import com.example.benchmark.utils.TapUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

public class MyAccessibilityService extends AccessibilityService {

    private final int MSG_CONTINUE_MONITOR = 0;
    private final int MSG_MONITOR_OVER = 1;
    private final int screenHeight = CacheUtil.getInt(CacheConst.KEY_SCREEN_HEIGHT);
    private final int screenWidth = CacheUtil.getInt(CacheConst.KEY_SCREEN_WIDTH);
    private final int screenDpi = CacheUtil.getInt(CacheConst.KEY_SCREEN_DPI);

    //自动点击
    private TapUtil tapUtil;

    private int resultCode;
    private Intent data;
    private Boolean isCheckTouch;
    private Boolean isCheckSoundFrame;
    private Boolean isCheckStability = false;
    private String checkPlatform;

    private IStabilityService service;

    private MediaProjection mProjection;
    private ImageReader mImageReader;
    private VirtualDisplay mDisplay;
    private final Queue<Pair<Bitmap, Long>> mBitmapWithTime = new LinkedList<>();
    public final Set<String> mTapStartTimes = new TreeSet<>();
    public final ArrayList<Long> mOpenTime = new ArrayList<>();
    public final List<Long> mStartTimes = new CopyOnWriteArrayList<>();
    public final ArrayList<Long> mQuitTimes = new ArrayList<>();

    private final Thread mCaptureScreenThread = new Thread(this::captureScreen);
    private final Thread mDealBitmapThread = new Thread(this::dealWithBitmap);

    private boolean isStartCaptureScreen = false;
    private boolean isStartDealBitmap = false;
    private boolean isDealResult = false;
    private boolean isHaveOtherPerformance = false;

    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == MSG_CONTINUE_MONITOR) {
                //Log.e("TWT", "handleMessage: "+"tteesstt" );
                Toast.makeText(MyAccessibilityService.this,
                        "稳定性测试结束，请继续在云端手机内测试", Toast.LENGTH_SHORT).show();
                //ServiceUtil.backToCePingActivity(MyAccessibilityService.this);
                 ServiceUtil.startFxService(MyAccessibilityService.this, checkPlatform, resultCode, data, isCheckTouch,isCheckSoundFrame);
            } else if (message.what == MSG_MONITOR_OVER) {
                Toast.makeText(MyAccessibilityService.this,
                        "稳定性测试结束", Toast.LENGTH_SHORT).show();
                ServiceUtil.backToCePingActivity(MyAccessibilityService.this);
            }
            return true;
        }
    });

    @Override
    public void onCreate() {
        super.onCreate();
        Notification notification = createForegroundNotification();
        //启动前台服务
        startForeground(1,notification);
        tapUtil = TapUtil.getUtil();
        tapUtil.setService(this);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }

    private Notification createForegroundNotification(){
        //前台通知的id名，任意
        String channelId = "ForegroundService";
        //前台通知的名称，任意
        String channelName = "Service";
        //发送通知的等级，此处为高，根据业务情况而定
        int importance = NotificationManager.IMPORTANCE_HIGH;
        //判断Android版本，不同的Android版本请求不一样，以下代码为官方写法
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(channelId,channelName,importance);
            channel.setLightColor(Color.BLUE);
            //channel.setLockscreenVisiability(Notification.VISIBILITY_PRIVATE);
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        //点击通知时可进入的Activity
        Intent notificationIntent = new Intent(this, CePingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

        //最终创建的通知，以下代码为官方写法
        //注释部分是可扩展的参数，根据自己的功能需求添加
        return new NotificationCompat.Builder(this,channelId)
                .setContentTitle("AccessibilityService")
                .setContentText("Benchmark无障碍服务工作中")
                //.setSmallIcon(Icon)//通知显示的图标
                .setContentIntent(pendingIntent)//点击通知进入Activity
                //.setTicker("通知的提示语")
                .build();
        //.setOngoing(true)
        //.setPriority(NotificationCompat.PRIORITY_MAX)
        //.setCategory(Notification.CATEGORY_TRANSPORT)
        //.setLargeIcon(Icon)
        //.setWhen(System.currentTimeMillis())
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        resultCode = intent.getIntExtra("resultCode", Integer.MAX_VALUE);
        data = intent.getParcelableExtra("data");
        isCheckTouch = intent.getBooleanExtra("isCheckTouch", false);
        isCheckSoundFrame = intent.getBooleanExtra("isCheckSoundFrame", false);
        checkPlatform = intent.getStringExtra(CacheConst.KEY_PLATFORM_NAME);
        if (resultCode != Integer.MAX_VALUE && data != null) {
            MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)
                    getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            mProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
            mImageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2);
            mDisplay = mProjection.createVirtualDisplay("ScreenShot", screenWidth, screenHeight, screenDpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.getSurface(), null, null);
        }
        isHaveOtherPerformance = intent.getBooleanExtra(CacheConst.KEY_IS_HAVING_OTHER_PERFORMANCE_MONITOR, false);
        if (CacheConst.PLATFORM_NAME_RED_FINGER_CLOUD_PHONE.equals(checkPlatform)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_red_finger_game));
            service = new RedFingerStabilityService(this);
        } else if (CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_GAME.equals(checkPlatform)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_huawei_cloud_game));
            service = new HuaweiCloudGameStabilityService(this);
        } else if (CacheConst.PLATFORM_NAME_E_CLOUD_PHONE.equals(checkPlatform)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_e_cloud_phone));
            service = new ECloudPhoneStabilityService(this);
        } else if (CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_PHONE.equals(checkPlatform)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_huawei_cloud_phone));
            service = new HuaweiCloudPhoneStabilityService(this);
        } else if (CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_PHONE.equals(checkPlatform)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_net_ease_cloud_phone));
            service = new NetEaseCloudPhoneStabilityService(this);
        } else if (CacheConst.PLATFORM_NAME_Tencent_GAME.equals(checkPlatform)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_tencent_gamer));
            service = new TencentGamerStabilityService(this);
        } else if (CacheConst.PLATFORM_NAME_MI_GU_GAME.equals(checkPlatform)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_mi_gu_play));
            service = new MiGuPlayStabilityService(this);
        } else if (CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_GAME.equals(checkPlatform)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_net_ease_cloud_phone));
            service = new NetEaseCloudGameStabilityService(this);
        } else {
            service = new DefaultStabilityService();
        }
        if (!mCaptureScreenThread.isAlive() && !mDealBitmapThread.isAlive() && (
                CacheConst.PLATFORM_NAME_RED_FINGER_CLOUD_PHONE.equals(checkPlatform)
                        || CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_GAME.equals(checkPlatform)
                        || CacheConst.PLATFORM_NAME_E_CLOUD_PHONE.equals(checkPlatform)
        )) {
            mCaptureScreenThread.start();
            mDealBitmapThread.start();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //Log.e("QT", "onAccessibilityEvent");
        if (service == null){
            return;
        }
        if (service.isFinished()) {
            if (!isDealResult && !isCheckTouch) dealWithResult();
            return;
        }
        service.onMonitor();
    }

    private void captureScreen() {
        while (!service.isFinished()) {
            if (!isStartCaptureScreen) continue;
            try (Image image = mImageReader.acquireLatestImage()) {
                if (image != null) {
                    Image.Plane[] planes = image.getPlanes();
                    ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * screenWidth;
                    Bitmap bitmap = Bitmap.createBitmap(screenWidth + rowPadding / pixelStride,
                            screenHeight, Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);
                    mBitmapWithTime.offer(new Pair<>(bitmap, System.currentTimeMillis()));
//                    if (!mDealBitmapThread.isAlive()) mDealBitmapThread.start();
                    startDealBitmap();
                    image.close();
                }
            }
        }
    }

    private void dealWithBitmap() {
        while (!service.isFinished() || mBitmapWithTime.size() > 0) {
            Pair<Bitmap, Long> bitmapWithTime;
            int dealIndex = 0;
            boolean isBlackOrWhiteExist = false;
            while (dealIndex < mStartTimes.size()) {
                bitmapWithTime = mBitmapWithTime.poll();
                if (bitmapWithTime == null) break;
                long startTime = mStartTimes.get(dealIndex);
                if (Math.abs(startTime - bitmapWithTime.second) < 1000L) {
                    if (!isBlackOrWhiteExist && isBitmapBlackOrWhite(bitmapWithTime.first)) {
                        isBlackOrWhiteExist = true;
                    } else if (isBlackOrWhiteExist && !isBitmapBlackOrWhite(bitmapWithTime.first)) {
                        isBlackOrWhiteExist = false;
                        mOpenTime.set(dealIndex, mOpenTime.get(dealIndex) +
                                bitmapWithTime.second - mStartTimes.get(dealIndex));
                        dealIndex++;
                    }
                }
            }
//            for (long startGameTime : mStartTimes) {
//                Pair<Bitmap, Long> bitmapWithTime = mBitmapWithTime.poll();
//                // black bitmap appear
//                while (bitmapWithTime != null && !isBitmapInvalid(bitmapWithTime.first))
//                    bitmapWithTime = mBitmapWithTime.poll();
//                if (bitmapWithTime == null) break;
//                long blackStartTime = bitmapWithTime.second;
//                // black bitmap disappear
//                while (bitmapWithTime != null && isBitmapInvalid(bitmapWithTime.first))
//                    bitmapWithTime = mBitmapWithTime.poll();
//                if (bitmapWithTime == null) break;
//                mOpenTime.add(bitmapWithTime.second - startGameTime);
//                mStartTimes.remove(0);
//                Log.e("QT", "Open Time: " + (bitmapWithTime.second - startGameTime) +
//                        " blackStartTime:" + blackStartTime + " startGameTime:" + startGameTime);
//            }
        }
//        mProjection.stop();
//        mImageReader.close();
//        mDisplay.release();
//        dealWithResult();
    }

    private void dealWithResult() {
        isDealResult = true;
        if (CacheUtil.getBoolean(CacheConst.KEY_IS_AUTO_TAP)) {
            CacheUtil.put(CacheConst.KEY_AUTO_TAP_TIMES, mTapStartTimes);
            return;
        }
        stopCaptureScreen();
        stopDealBitmap();
        float startSuccessRate = service.getStartSuccessRate();
        float averageStartTime = 0F;
        for (long time : mOpenTime) {
            averageStartTime += time;
        }
        averageStartTime /= mOpenTime.size();
        float averageQuitTime = 0F;
        for (long time : mQuitTimes) {
            averageQuitTime += time;
        }
        averageQuitTime /= mQuitTimes.size();

        ScoreUtil.calcAndSaveStabilityScores(startSuccessRate, averageStartTime, averageQuitTime);
        Log.e("QT", "startSuccessRate:" + startSuccessRate +
                " averageStartTime:" + averageStartTime + " averageQuitTime:" + averageQuitTime);
        CacheUtil.put(CacheConst.KEY_STABILITY_IS_MONITORED, true);
        if (isHaveOtherPerformance) {
            Log.d("TWT", "isHaveOtherPerformance:yep ");
            mHandler.sendEmptyMessage(MSG_CONTINUE_MONITOR);
        } else {
            Log.d("TWT", "isHaveOtherPerformance:nop ");
            mHandler.sendEmptyMessage(MSG_MONITOR_OVER);
        }
    }

    private boolean isBitmapBlackOrWhite(Bitmap bitmap) {
        Palette palette = Palette.from(bitmap).clearFilters().generate();
        int domainColor = palette.getDominantColor(Color.BLACK);
//        if (domainColor == Color.BLACK || domainColor == Color.WHITE)
//            Log.e("QT-Color", "True");
        return domainColor == Color.BLACK || domainColor == Color.WHITE;
    }

    private boolean isBitmapInvalid(Bitmap bitmap) {
        Random random = new Random();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int invalidPixelCount = 0;
        for (int i = 0; i < Math.min(width, height); i++) {
            if (invalidPixelCount > Math.min(width, height) * 3 / 4) return true;
            int pixel = bitmap.getPixel(random.nextInt(width), random.nextInt(height));
            if ((Color.red(pixel) == 0 && Color.green(pixel) == 0 && Color.blue(pixel) == 0) /*Black*/
                    || (Color.red(pixel) == 255 && Color.green(pixel) == 255 && Color.blue(pixel) == 255 /*White*/)) {
                invalidPixelCount++;
            }
        }
        return false;
    }



    private boolean isGamePlatform() {
        return CacheConst.PLATFORM_NAME_Tencent_GAME.equals(checkPlatform)
                || CacheConst.PLATFORM_NAME_MI_GU_GAME.equals(checkPlatform)
                || CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_GAME.equals(checkPlatform);
    }

    public void startCaptureScreen() {
        this.isStartCaptureScreen = true;
    }

    public void startDealBitmap() {
        this.isStartDealBitmap = true;
    }

    public void stopCaptureScreen() {
        this.isStartCaptureScreen = false;
    }

    public void stopDealBitmap() {
        this.isStartDealBitmap = false;
    }

    @Override
    public void onInterrupt() {
    }

}
