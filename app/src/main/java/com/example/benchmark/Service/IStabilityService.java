package com.example.benchmark.Service;

public interface IStabilityService {

    int mWholeMonitorNum = 5;

    void onMonitor();

    void startControlCloudPhone();

    void startQuitCloudPhone();

    int getCurrentMonitorNum();

    default float getStartSuccessRate() {
        return getCurrentMonitorNum() / (float) mWholeMonitorNum * 100;
    }

    default boolean isFinished() {
        return getCurrentMonitorNum() == mWholeMonitorNum;
    }

}
