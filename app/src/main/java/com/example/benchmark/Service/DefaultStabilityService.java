package com.example.benchmark.Service;

import android.util.Log;

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
        return mWholeMonitorNum;
    }
}
