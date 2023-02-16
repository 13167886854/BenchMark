package com.example.benchmark.service;

import android.util.Log;

import com.example.benchmark.utils.TapUtil;

public class DefaultStabilityService implements IStabilityService {
    @Override
    public void onMonitor() {
        Log.e("DefaultStabilityService", "onMonitor");
    }

    @Override
    public void startControlCloudPhone() {
        Log.e("DefaultStabilityService", "startControlCloudPhone");
    }

    @Override
    public void startQuitCloudPhone() {
        Log.e("DefaultStabilityService", "startQuitCloudPhone");
    }

    @Override
    public int getCurrentMonitorNum() {
        return TapUtil.mWholeMonitorNum;
    }
}
