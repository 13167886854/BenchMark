package com.example.benchmark.Service;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.benchmark.utils.AccessibilityCallback;
import com.example.benchmark.utils.AccessibilityUtil;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Date;

import okhttp3.OkHttpClient;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AutoTapAccessibilityService implements IStabilityService {
    private final int screenHeight = CacheUtil.getInt(CacheConst.KEY_SCREEN_HEIGHT);
    private final int screenWidth = CacheUtil.getInt(CacheConst.KEY_SCREEN_WIDTH);

    // 触控测试模拟点击次数
    public final int TOTAL_TAP_NUM = 12;

    private final OkHttpClient client = new OkHttpClient();

    private final StabilityMonitorService service;

    private int mCurrentTapNum = 0;

    private long mLastTapTime = 0L;
    //private long mCurrentTime = 0;

    private long startTime = 0L;
    private long endTime = 0L;
    private long responseTime = 0L;

    private boolean isGamePlatform;

    private boolean mTapFlag = false;

    public AutoTapAccessibilityService(StabilityMonitorService service, boolean isGamePlatform) {
        this.service = service;
        this.isGamePlatform = isGamePlatform;
    }

    @Override
    public void onMonitor() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || isFinished()) return;
        //mLastTapTime = System.currentTimeMillis();
        //mLastTapTime = System.currentTimeMillis();
        if (isGamePlatform) gameAutoTap();
        //else phoneAutoTap();
        if (mLastTapTime != 0L && System.currentTimeMillis() - endTime <= 2000) return;
        // 判断测试平台，如果是云手机测试平台
        String checkPlatform = CacheUtil.getString(CacheConst.KEY_PLATFORM_NAME);
        if (checkPlatform.equals(CacheConst.PLATFORM_NAME_E_CLOUD_PHONE) ||
                checkPlatform.equals(CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_PHONE) ||
                checkPlatform.equals(CacheConst.PLATFORM_NAME_RED_FINGER_CLOUD_PHONE) ||
                checkPlatform.equals(CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_GAME) ||
                checkPlatform.equals(CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_PHONE)
        ) {
            AccessibilityUtil.tap(service, screenWidth / 2, screenHeight / 2,
                    // 515  783
                    //AccessibilityUtil.tap(service, 475, 1278,
                    //AccessibilityUtil.tap(service, 514, 782,
                    new AccessibilityCallback() {
                        @Override
                        public void onSuccess() {
                            /**
                             * 模拟点击成功，记录每次点击的时间戳
                             * 使用okhttp3发送 GET 请求，获取一个标准的时间戳
                             */
                            // 记录发送请求时的系统时间戳
                            //startTime = System.currentTimeMillis();
                            //
                            //Request request = new Request.Builder()
                            //        .get()
                            //        .url(CacheConst.WEB_TIME_URL)
                            //        .build();
                            //new Thread(new Runnable() {
                            //    @Override
                            //    public void run() {
                            //        client.newCall(request)
                            //                .enqueue(new Callback() {
                            //                    @Override
                            //                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            //                        e.printStackTrace();
                            //                        Log.d("zzl", "onFailure: call===>" + e.toString());
                            //                    }
                            //
                            //                    @Override
                            //                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            //                        // 获取成功响应的系统时间戳
                            //                        endTime = System.currentTimeMillis();
                            //                        responseTime = endTime - startTime;
                            //
                            //                        String result = response.body().string();
                            //                        String res = result.substring(81, 94);
                            //                        Log.d("zzl", "onResponse: result===>" + result);
                            //                        Log.d("zzl", "onResponse: res===>" + res);
                            //                        //Log.d("zzl", "onResponse: response===>" + response);
                            //                        // 获取到的时间戳，应该减去响应时延
                            //                        mLastTapTime = Long.valueOf(res) - responseTime;
                            //
                            //                        Log.e("TWT zzl", "Tap Time mCurrentTapNum-" + mCurrentTapNum + ": " + mLastTapTime);
                            //                        Log.e("TWT zzl", "Tap Time mCurrentTapNum-" + mCurrentTapNum + "System.currentTimeMillis(): " + System.currentTimeMillis());
                            //                        //Log.e("TWT", "Tap Time mCurrentTapNum-" + mCurrentTapNum + ": " +new Date().getTime());
                            //                        service.mTapStartTimes.add(String.valueOf(mLastTapTime));
                            //                        CacheUtil.put(("tapTimeOnLocal" + (mCurrentTapNum)), mLastTapTime);
                            //                        mCurrentTapNum++;
                            //                        //Log.e("Auto Tap", "Tap Time:" + System.currentTimeMillis());
                            //                        //Message msg = Message.obtain();
                            //                        //msg.what = 1;
                            //                        //msg.obj = result;
                            //                        //mHandler.sendMessage(msg);
                            //                    }
                            //                });
                            //    }
                            //}).start();
                        }

                        @Override
                        public void onFailure() {
                        }
                    });
        }
    }

    private void gameAutoTap() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return;
        AccessibilityUtil.tap(service, mTapFlag ? 1638 :2165, mTapFlag ? 225 : 980,
                new AccessibilityCallback() {
                    @Override
                    public void onSuccess() {
                        Log.e("TWT", "Tap Time mCurrentTapNum-" + mCurrentTapNum + ": " + System.currentTimeMillis());
                        Log.e("TWT", "Tap Time mCurrentTapNum-" + mCurrentTapNum + ": " +new Date().getTime());
                        mCurrentTapNum++;
                        service.mTapStartTimes.add(String.valueOf(System.currentTimeMillis()));
                        CacheUtil.put(("tapTimeOnLocal" + (mCurrentTapNum - 1)), System.currentTimeMillis());
                        mTapFlag = !mTapFlag;
                    }

                    @Override
                    public void onFailure() {
                    }
                });
    }

    private void phoneAutoTap() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || isFinished()) return;
        AccessibilityUtil.tap(service, screenWidth / 2, screenHeight / 2,
                // 515  783
                //AccessibilityUtil.tap(service, 475, 1278,
                //AccessibilityUtil.tap(service, 514, 782,
                new AccessibilityCallback() {
                    @Override
                    public void onSuccess() {
                        Log.e("TWT", "Tap Time mCurrentTapNum-" + mCurrentTapNum + ": " + System.currentTimeMillis());
                        Log.e("TWT", "Tap Time mCurrentTapNum-" + mCurrentTapNum + ": " +new Date().getTime());
                        mCurrentTapNum++;
                        service.mTapStartTimes.add(String.valueOf(System.currentTimeMillis()));
                        CacheUtil.put(("tapTimeOnLocal" + (mCurrentTapNum - 1)), System.currentTimeMillis());
                        //Log.e("Auto Tap", "Tap Time:" + System.currentTimeMillis());
                    }

                    @Override
                    public void onFailure() {
                    }
                });
    }
    @Override
    public void startControlCloudPhone() {

    }

    @Override
    public void startQuitCloudPhone() {

    }

    @Override
    public int getCurrentMonitorNum() {
        return mCurrentTapNum;
    }

    @Override
    public boolean isFinished() {
        return mCurrentTapNum == TOTAL_TAP_NUM;
    }
}
