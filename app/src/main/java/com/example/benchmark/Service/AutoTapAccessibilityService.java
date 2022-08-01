package com.example.benchmark.Service;

import android.os.Build;
import android.util.Log;

import com.example.benchmark.utils.AccessibilityCallback;
import com.example.benchmark.utils.AccessibilityUtil;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;

import java.util.Date;

public class AutoTapAccessibilityService implements IStabilityService {

    private final int screenHeight = CacheUtil.getInt(CacheConst.KEY_SCREEN_HEIGHT);
    private final int screenWidth = CacheUtil.getInt(CacheConst.KEY_SCREEN_WIDTH);

    // 触控测试模拟点击次数
    private final int TOTAL_TAP_NUM = 5;

    private final StabilityMonitorService service;

    private int mCurrentTapNum = 0;

    private long mLastTapTime = 0L;

    private boolean isGamePlatform;

    private boolean mTapFlag = false;

    public AutoTapAccessibilityService(StabilityMonitorService service, boolean isGamePlatform) {
        this.service = service;
        this.isGamePlatform = isGamePlatform;
    }

    @Override
    public void onMonitor() {
        if (isFinished()) return;
        Log.e("QT", "Auto Tap Monitor");
        if (mLastTapTime != 0L && System.currentTimeMillis() - mLastTapTime <= 1500) return;
        mLastTapTime = System.currentTimeMillis();
        if (isGamePlatform) gameAutoTap();
        else phoneAutoTap();
    }

    private void gameAutoTap() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return;
        AccessibilityUtil.tap(service, 300, mTapFlag ? 200 : 300,
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
