package com.example.benchmark.Service;

import android.os.Build;
import android.util.Log;

import com.example.benchmark.utils.AccessibilityCallback;
import com.example.benchmark.utils.AccessibilityUtil;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;

public class AutoTapAccessibilityService implements IStabilityService {

    private final int screenHeight = CacheUtil.getInt(CacheConst.KEY_SCREEN_HEIGHT);
    private final int screenWidth = CacheUtil.getInt(CacheConst.KEY_SCREEN_WIDTH);

    // 触控测试模拟点击次数
    private final int TOTAL_TAP_NUM = 5;

    private final StabilityMonitorService service;

    private int mCurrentTapNum = 0;

    private long mLastTapTime = 0L;

    public AutoTapAccessibilityService(StabilityMonitorService service) {
        this.service = service;
    }

    @Override
    public void onMonitor() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || isFinished()) return;
        if (mLastTapTime != 0L && System.currentTimeMillis() - mLastTapTime <= 2000L) return;
        mLastTapTime = System.currentTimeMillis();
        //AccessibilityUtil.tap(service, screenWidth / 2 - 100, screenHeight / 2 - 100,
        // 515  783
        AccessibilityUtil.tap(service, 475, 1278,
        //AccessibilityUtil.tap(service, 514, 782,
                new AccessibilityCallback() {
                    @Override
                    public void onSuccess() {
                        mCurrentTapNum++;
                        service.mTapStartTimes.add(String.valueOf(System.currentTimeMillis()));
                        Log.e("Auto Tap", "Tap Time:" + System.currentTimeMillis());
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
