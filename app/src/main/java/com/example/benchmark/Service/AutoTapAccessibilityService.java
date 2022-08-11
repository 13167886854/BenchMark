package com.example.benchmark.Service;

import android.os.Build;

import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;

public class AutoTapAccessibilityService implements IStabilityService {
    private final int screenHeight = CacheUtil.getInt(CacheConst.KEY_SCREEN_HEIGHT);
    private final int screenWidth = CacheUtil.getInt(CacheConst.KEY_SCREEN_WIDTH);


    private final MyAccessibilityService service;




    public AutoTapAccessibilityService(MyAccessibilityService service, boolean isGamePlatform) {
        this.service = service;
    }

    @Override
    public void onMonitor() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || isFinished()) return;

    }


    @Override
    public void startControlCloudPhone() {

    }

    @Override
    public void startQuitCloudPhone() {

    }

    @Override
    public int getCurrentMonitorNum() {
        return 0;
    }

    @Override
    public float getStartSuccessRate() {
        return IStabilityService.super.getStartSuccessRate();
    }

    @Override
    public boolean isFinished() {
        return IStabilityService.super.isFinished();
    }


}
