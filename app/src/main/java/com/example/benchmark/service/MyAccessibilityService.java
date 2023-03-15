/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.service;

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

import com.example.benchmark.activity.CePingActivity;
import com.example.benchmark.R;
import com.example.benchmark.utils.ApkUtil;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;
import com.example.benchmark.utils.ScoreUtil;
import com.example.benchmark.utils.ServiceUtil;
import com.example.benchmark.utils.TapUtil;
import com.example.benchmark.utils.ThreadPoolUtil;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

/**
 * MyAccessibilityService
 *
 * @version 1.0
 * @since 2023/3/7 17:21
 */
public class MyAccessibilityService extends AccessibilityService {
    /**
     * mTapStartTimes
     */
    public final Set<String> mTapStartTimes = new TreeSet<>();

    /**
     * mOpenTime
     */
    public final ArrayList<Long> mOpenTime = new ArrayList<>();

    /**
     * mStartTimes
     */
    public final List<Long> mStartTimes = new CopyOnWriteArrayList<>();

    /**
     * mQuitTimes
     */
    public final ArrayList<Long> mQuitTimes = new ArrayList<>();

    private final int msgContinueMonitor = 0;
    private final int msgMonitorOver = 1;
    private final int screenHeight = CacheUtil.getInt(CacheConst.KEY_SCREEN_HEIGHT);
    private final int screenWidth = CacheUtil.getInt(CacheConst.KEY_SCREEN_WIDTH);
    private final int screenDpi = CacheUtil.getInt(CacheConst.KEY_SCREEN_DPI);
    private final Queue<Pair<Bitmap, Long>> mBitmapWithTime = new LinkedList<>();

    private Future<?> captureScreen;
    private Future<?> dealBitmap;

    // 自动点击
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


    private boolean isStartCaptureScreen = false;
    private boolean isStartDealBitmap = false;
    private boolean isDealResult = false;
    private boolean isHaveOtherPerformance = false;

    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == msgContinueMonitor) {
                Toast.makeText(MyAccessibilityService.this,
                        "稳定性测试结束，请继续在云端手机内测试", Toast.LENGTH_SHORT).show();
                boolean[] isCheckGroup = new boolean[2];
                isCheckGroup[0] = isCheckTouch;
                isCheckGroup[1] = isCheckSoundFrame;
                ServiceUtil.startFxService(MyAccessibilityService.this,
                        checkPlatform, resultCode, data, isCheckGroup);
            } else if (message.what == msgMonitorOver) {
                Toast.makeText(MyAccessibilityService.this,
                        "稳定性测试结束", Toast.LENGTH_SHORT).show();
                ServiceUtil.backToCePingActivity(MyAccessibilityService.this);
            } else {
                Log.e("TAG", "handleMessage: ");
            }
            return true;
        }
    });

    /**
     * onCreate
     *
     * @return void
     * @date 2023/3/9 16:52
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Notification notification = createForegroundNotification();
        // 启动前台服务
        startForeground(1, notification);
        tapUtil = TapUtil.getUtil();
        tapUtil.setService(this);
    }

    /**
     * onDestroy
     *
     * @return void
     * @date 2023/3/9 16:53
     */
    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }

    private Notification createForegroundNotification() {
        // 前台通知的id名，任意
        String channelId = "ForegroundService";
        // 前台通知的名称，任意
        String channelName = "Service";
        // 发送通知的等级，此处为高，根据业务情况而定
        int importance = NotificationManager.IMPORTANCE_HIGH;
        // 判断Android版本，不同的Android版本请求不一样，以下代码为官方写法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setLightColor(Color.BLUE);
            if (getSystemService(Context.NOTIFICATION_SERVICE) instanceof NotificationManager) {
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);
            }
        }
        // 点击通知时可进入的Activity
        Intent notificationIntent = new Intent(this, CePingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        // 最终创建的通知，以下代码为官方写法
        // 注释部分是可扩展的参数，根据自己的功能需求添加
        return new NotificationCompat.Builder(this, channelId)
                .setContentTitle("AccessibilityService")
                .setContentText("Benchmark无障碍服务工作中")
                .setContentIntent(pendingIntent) // 点击通知进入Activity
                .build();
    }

    /**
     * onStartCommand
     *
     * @param intent  description
     * @param flags   description
     * @param startId description
     * @return int
     * @date 2023/3/9 16:53
     */
    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        resultCode = intent.getIntExtra("resultCode", Integer.MAX_VALUE);
        data = intent.getParcelableExtra("data");
        isCheckTouch = intent.getBooleanExtra("isCheckTouch", false);
        isCheckSoundFrame = intent.getBooleanExtra("isCheckSoundFrame", false);
        checkPlatform = intent.getStringExtra(CacheConst.KEY_PLATFORM_NAME);
        if (resultCode != Integer.MAX_VALUE && data != null) {
            if (getSystemService(Context.MEDIA_PROJECTION_SERVICE) instanceof MediaProjectionManager) {
                MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)
                        getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                mProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
                mImageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2);
                mDisplay = mProjection.createVirtualDisplay("ScreenShot", screenWidth, screenHeight, screenDpi,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.getSurface(),
                        null, null);
            }
        }
        command(intent);
        boolean isSelectCheckedPlatform = CacheConst.PLATFORM_NAME_RED_FINGER_CLOUD_PHONE.equals(checkPlatform)
                || CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_GAME.equals(checkPlatform)
                || CacheConst.PLATFORM_NAME_E_CLOUD_PHONE.equals(checkPlatform);

        if (captureScreen.isDone() && dealBitmap.isDone() && isSelectCheckedPlatform) {
            captureScreen = ThreadPoolUtil.getPool().submit(this::captureScreen);
            dealBitmap = ThreadPoolUtil.getPool().submit(this::captureScreen);
        }
        return START_NOT_STICKY;
    }

    private void command(Intent intent) {
        isHaveOtherPerformance =
                intent.getBooleanExtra(CacheConst.KEY_IS_HAVING_OTHER_PERFORMANCE_MONITOR, false);
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
        } else if (CacheConst.PLATFORM_NAME_TENCENT_GAME.equals(checkPlatform)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_tencent_gamer));
            service = new TencentGamerStabilityService(this);
        } else if (CacheConst.PLATFORM_NAME_MI_GU_GAME.equals(checkPlatform)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_mi_gu_play));
            ApkUtil.launchApp(this, getString(R.string.pkg_name_mi_gu_play2));
            service = new MiGuPlayStabilityService(this);
        } else if (CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_GAME.equals(checkPlatform)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_net_ease_cloud_phone));
            service = new NetEaseCloudGameStabilityService(this);
        } else {
            service = new DefaultStabilityService();
        }
    }

    /**
     * onAccessibilityEvent
     *
     * @param event description
     * @return void
     * @date 2023/3/9 16:53
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (service == null) {
            return;
        }
        if (service.isFinished()) {
            if (!isDealResult && !isCheckTouch) {
                dealWithResult();
            }
            return;
        }
        service.onMonitor();
    }

    private void captureScreen() {
        while (!service.isFinished()) {
            if (!isStartCaptureScreen) {
                continue;
            }
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
                    startDealBitmap();
                    image.close();
                }
            }
        }
    }

    private void dealWithBitmap() {
        while (!service.isFinished() || mBitmapWithTime.size() > 0) {
            detail();
        }
    }

    private void detail() {
        Pair<Bitmap, Long> bitmapWithTime;
        int dealIndex = 0;
        boolean isBlackOrWhiteExist = false;
        while (dealIndex < mStartTimes.size()) {
            bitmapWithTime = mBitmapWithTime.poll();
            if (bitmapWithTime == null) {
                break;
            }
            long startTime = mStartTimes.get(dealIndex);
            if (Math.abs(startTime - bitmapWithTime.second) < 1000L) {
                if (!isBlackOrWhiteExist && isBitmapBlackOrWhite(bitmapWithTime.first)) {
                    isBlackOrWhiteExist = true;
                } else if (isBlackOrWhiteExist && !isBitmapBlackOrWhite(bitmapWithTime.first)) {
                    isBlackOrWhiteExist = false;
                    mOpenTime.set(dealIndex, mOpenTime.get(dealIndex)
                            + bitmapWithTime.second - mStartTimes.get(dealIndex));
                    dealIndex++;
                } else {
                    Log.e("WZX", "OK");
                }
            }
        }
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
        Log.e("QT", "startSuccessRate:" + startSuccessRate
                + " averageStartTime:" + averageStartTime + " averageQuitTime:" + averageQuitTime);
        CacheUtil.put(CacheConst.KEY_STABILITY_IS_MONITORED, true);
        if (isHaveOtherPerformance) {
            Log.d("TWT", "isHaveOtherPerformance:yep ");
            mHandler.sendEmptyMessage(msgContinueMonitor);
        } else {
            Log.d("TWT", "isHaveOtherPerformance:nop ");
            mHandler.sendEmptyMessage(msgMonitorOver);
        }
    }

    private boolean isBitmapBlackOrWhite(Bitmap bitmap) {
        Palette palette = Palette.from(bitmap).clearFilters().generate();
        int domainColor = palette.getDominantColor(Color.BLACK);
        return domainColor == Color.BLACK || domainColor == Color.WHITE;
    }

    private boolean isBitmapInvalid(Bitmap bitmap) {
        SecureRandom secureRandom = new SecureRandom();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int invalidPixelCount = 0;
        for (int i = 0; i < Math.min(width, height); i++) {
            if (invalidPixelCount > Math.min(width, height) * 3 / 4) {
                return true;
            }
            int pixel = bitmap.getPixel(secureRandom.nextInt(width), secureRandom.nextInt(height));
            if ((Color.red(pixel) == 0 && Color.green(pixel) == 0 && Color.blue(pixel) == 0)) {
                invalidPixelCount++;
            } else if ((Color.red(pixel) == 255 && Color.green(pixel) == 255 && Color.blue(pixel) == 255)) {
                invalidPixelCount++;
            } else {
                Log.d("TAG", "isBitmapInvalid: lastElse");
            }
        }
        return false;
    }

    private boolean isGamePlatform() {
        return CacheConst.PLATFORM_NAME_TENCENT_GAME.equals(checkPlatform)
                || CacheConst.PLATFORM_NAME_MI_GU_GAME.equals(checkPlatform)
                || CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_GAME.equals(checkPlatform);
    }

    /**
     * startCaptureScreen
     *
     * @return void
     * @date 2023/3/9 16:53
     */
    public void startCaptureScreen() {
        this.isStartCaptureScreen = true;
    }

    /**
     * startDealBitmap
     *
     * @return void
     * @date 2023/3/9 16:53
     */
    public void startDealBitmap() {
        this.isStartDealBitmap = true;
    }

    /**
     * stopCaptureScreen
     *
     * @return void
     * @date 2023/3/9 16:53
     */
    public void stopCaptureScreen() {
        this.isStartCaptureScreen = false;
    }

    /**
     * stopDealBitmap
     *
     * @return void
     * @date 2023/3/9 16:53
     */
    public void stopDealBitmap() {
        this.isStartDealBitmap = false;
    }

    /**
     * onInterrupt
     *
     * @return void
     * @date 2023/3/9 16:53
     */
    @Override
    public void onInterrupt() {
    }
}
