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
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;

import com.example.benchmark.Activity.CePingActivity;
import com.example.benchmark.Activity.MainActivity;
import com.example.benchmark.Data.TestMode;
import com.example.benchmark.Data.WenDingData;
import com.example.benchmark.R;
import com.example.benchmark.utils.ApkUtil;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;
import com.example.benchmark.utils.ScoreUtil;
import com.example.benchmark.utils.ServiceUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class StabilityMonitorService extends AccessibilityService {

    private final int MSG_CONTINUE_MONITOR = 0;
    private final int MSG_MONITOR_OVER = 1;
    private final int screenHeight = CacheUtil.getInt(CacheConst.KEY_SCREEN_HEIGHT);
    private final int screenWidth = CacheUtil.getInt(CacheConst.KEY_SCREEN_WIDTH);
    private final int screenDpi = CacheUtil.getInt(CacheConst.KEY_SCREEN_DPI);

    private int resultCode;
    private Intent data;

    private IStabilityService service;

    private MediaProjection mProjection;
    private ImageReader mImageReader;
    private VirtualDisplay mDisplay;
    private final Queue<Pair<Bitmap, Long>> mBitmapWithTime = new LinkedList<>();
    public final ArrayList<Long> mOpenTime = new ArrayList<>();
    public final List<Long> mStartTimes = new CopyOnWriteArrayList<>();
    public final ArrayList<Long> mQuitTimes = new ArrayList<>();

    private final Thread mCaptureScreenThread = new Thread(this::captureScreen);
    private final Thread mDealBitmapThread = new Thread(this::dealWithBitmap);

    private boolean isStartCaptureScreen = false;
    private boolean isStartDealBitmap = false;
    private boolean isDealResult = false;
    private boolean isHaveOtherPerformance = false;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_CONTINUE_MONITOR) {
                Toast.makeText(StabilityMonitorService.this,
                        "稳定性测试结束，请继续在云端手机内测试", Toast.LENGTH_SHORT).show();
            } else if (msg.what == MSG_MONITOR_OVER) {
                Toast.makeText(StabilityMonitorService.this,
                        "稳定性测试结束", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        resultCode = intent.getIntExtra("resultCode", Integer.MAX_VALUE);
        data = intent.getParcelableExtra("data");
        if (resultCode != Integer.MAX_VALUE && data != null) {
            MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)
                    getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            mProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
            mImageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2);
            mDisplay = mProjection.createVirtualDisplay("ScreenShot", screenWidth, screenHeight, screenDpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.getSurface(), null, null);
        }
        isHaveOtherPerformance = intent.getBooleanExtra(CacheConst.KEY_IS_HAVING_OTHER_PERFORMANCE_MONITOR, false);
        String platformName = intent.getStringExtra(CacheConst.KEY_PLATFORM_NAME);
//        platformName = CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_GAME;
        if (CacheConst.PLATFORM_NAME_RED_FINGER_CLOUD_PHONE.equals(platformName)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_red_finger_game));
            service = new RedFingerStabilityService(this);
        } else if (CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_GAME.equals(platformName)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_huawei_cloud_game));
            service = new HuaweiCloudGameStabilityService(this);
        } else if (CacheConst.PLATFORM_NAME_E_CLOUD_PHONE.equals(platformName)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_e_cloud_phone));
            service = new ECloudPhoneStabilityService(this);
        } else if (CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_PHONE.equals(platformName)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_huawei_cloud_phone));
            service = new HuaweiCloudPhoneStabilityService(this);
        } else if (CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_PHONE.equals(platformName)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_net_ease_cloud_phone));
            service = new NetEaseCloudPhoneStabilityService(this);
        } else if (CacheConst.PLATFORM_NAME_Tencent_GAME.equals(platformName)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_tencent_gamer));
            service = new TencentGamerStabilityService(this);
        } else if (CacheConst.PLATFORM_NAME_MI_GU_GAME.equals(platformName)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_mi_gu_play));
            service = new MiGuPlayStabilityService(this);
        }
        if (!mCaptureScreenThread.isAlive() && (
                CacheConst.PLATFORM_NAME_RED_FINGER_CLOUD_PHONE.equals(platformName)
                        || CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_GAME.equals(platformName)
                        || CacheConst.PLATFORM_NAME_E_CLOUD_PHONE.equals(platformName)
        )) mCaptureScreenThread.start();
        return START_NOT_STICKY;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (service.isFinished()) {
            if (!isDealResult) dealWithResult();
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
                    if (!mDealBitmapThread.isAlive()) mDealBitmapThread.start();
                    startDealBitmap();
                    image.close();
                }
            }
        }
    }

    private void dealWithBitmap() {
        while (!service.isFinished()) {
//            if (!isStartDealBitmap || mBitmapWithTime.size() < 40) continue;
            Pair<Bitmap, Long> bitmapWithTime;
            boolean isBlackOrWhiteExist = false;
            while (mStartTimes.size() > mOpenTime.size()
                    && (bitmapWithTime = mBitmapWithTime.poll()) != null) {
                if (!isBlackOrWhiteExist && isBitmapBlackOrWhite(bitmapWithTime.first)) {
                    isBlackOrWhiteExist = true;
                } else if (isBlackOrWhiteExist && !isBitmapBlackOrWhite(bitmapWithTime.first)) {
                    isBlackOrWhiteExist = false;
                    mOpenTime.add(bitmapWithTime.second - mStartTimes.get(mOpenTime.size()));
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
        dealWithResult();
    }

    private void dealWithResult() {
        isDealResult = true;
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
            mHandler.sendEmptyMessage(MSG_CONTINUE_MONITOR);
            ServiceUtil.startFxService(this, resultCode, data);
        } else {
            mHandler.sendEmptyMessage(MSG_MONITOR_OVER);
            ServiceUtil.backToCePingActivity(this);
        }
    }

    private boolean isBitmapBlackOrWhite(Bitmap bitmap) {
        Palette palette = Palette.from(bitmap).generate();
        int domainColor = palette.getDominantColor(Color.BLACK);
//        Log.e("QT-Color", domainColor + "");
        if (domainColor == Color.BLACK || domainColor == Color.WHITE)
            Log.e("QT-Color", "True");
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

    private void createNotificationChannel() {
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //获取一个Notification构造器
        Intent nfIntent = new Intent(this, MainActivity.class); //点击后跳转的界面，可以设置跳转数据

        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, PendingIntent.FLAG_IMMUTABLE)) // 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
                .setContentTitle("红手指云手机稳定性评测服务") // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                .setContentText("正在评测中......") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        /*以下是对Android 8.0的适配*/
        //普通notification适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("notification_id");
        }
        //前台服务notification适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("notification_id", "notification_name", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        startForeground(110, notification);
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
