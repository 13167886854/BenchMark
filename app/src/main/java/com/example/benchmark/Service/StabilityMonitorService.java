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
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.example.benchmark.Activity.CePingActivity;
import com.example.benchmark.Activity.MainActivity;
import com.example.benchmark.Data.TestMode;
import com.example.benchmark.Data.WenDingData;
import com.example.benchmark.R;
import com.example.benchmark.utils.ApkUtil;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class StabilityMonitorService extends AccessibilityService {

    private final int screenHeight = CacheUtil.getInt(CacheConst.KEY_SCREEN_HEIGHT);
    private final int screenWidth = CacheUtil.getInt(CacheConst.KEY_SCREEN_WIDTH);
    private final int screenDpi = CacheUtil.getInt(CacheConst.KEY_SCREEN_DPI);

    private String platformName;

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

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        int resultCode = intent.getIntExtra("resultCode", 0);
        Intent data = intent.getParcelableExtra("data");
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)
                getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
        mImageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2);
        mDisplay = mProjection.createVirtualDisplay("ScreenShot", screenWidth, screenHeight, screenDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.getSurface(), null, null);
        platformName = intent.getStringExtra(CacheConst.KEY_PLATFORM_NAME);
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
        }
        if (!mCaptureScreenThread.isAlive()
                && !CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_PHONE.equals(platformName)
                && !CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_PHONE.equals(platformName))
            mCaptureScreenThread.start();
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
            if (!isStartDealBitmap || mBitmapWithTime.size() < 40) continue;
            for (long startGameTime : mStartTimes) {
                Pair<Bitmap, Long> bitmapWithTime = mBitmapWithTime.poll();
                // black bitmap appear
                while (bitmapWithTime != null && !isBitmapInvalid(bitmapWithTime.first))
                    bitmapWithTime = mBitmapWithTime.poll();
                if (bitmapWithTime == null) break;
                long blackStartTime = bitmapWithTime.second;
                // black bitmap disappear
                while (bitmapWithTime != null && isBitmapInvalid(bitmapWithTime.first))
                    bitmapWithTime = mBitmapWithTime.poll();
                if (bitmapWithTime == null) break;
                mOpenTime.add(bitmapWithTime.second - startGameTime);
                mStartTimes.remove(0);
                Log.e("QT", "Open Time: " + (bitmapWithTime.second - startGameTime) +
                        " blackStartTime:" + blackStartTime + " startGameTime:" + startGameTime);
            }
        }
        mProjection.stop();
        mImageReader.close();
        mDisplay.release();
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

        //保存测试结果
        CacheUtil.put(CacheConst.KEY_START_SUCCESS_RATE, startSuccessRate);
        CacheUtil.put(CacheConst.KEY_AVERAGE_START_TIME, averageStartTime);
        CacheUtil.put(CacheConst.KEY_AVERAGE_QUIT_TIME, averageQuitTime);

        if(TestMode.TestMode==2){//当仅仅测试稳定性时，不需要后续在云端apk测试便直接返回到测试结果activity
            Intent resultIntent = new Intent(this, CePingActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Log.e("QT", "startSuccessRate:" + startSuccessRate +
                    " averageStartTime:" + averageStartTime + " averageQuitTime:" + averageQuitTime);
            startActivity(resultIntent);
            if(Looper.myLooper()==null){
                Looper.prepare();
            }
            Toast.makeText(getApplicationContext(),"稳定性测试结束！",Toast.LENGTH_SHORT).show();
            Looper.loop();

        }else if(TestMode.TestMode==3){//后续测试需要在对应apk进行,打开相应云手机平台apk

            if(Looper.myLooper()==null){
                Looper.prepare();
            }
            Toast.makeText(getApplicationContext(),"稳定性测试结束,继续在云手机上完成后续测试！",Toast.LENGTH_SHORT).show();
            Looper.loop();
        }

        Log.e("QT", "Over");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            disableSelf();
        }
        stopSelf();
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
