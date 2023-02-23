package com.example.benchmark.service;

import android.util.Log;

import com.example.benchmark.utils.TapUtil;

/**
 * @version 1.0
 * @description DefaultStabilityService 稳定性Service
 * @time 2023/2/22 14:35
 */
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
