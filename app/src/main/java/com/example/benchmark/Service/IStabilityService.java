package com.example.benchmark.Service;

public interface IStabilityService {

    int mWholeMonitorNum = 5;

    void onMonitor();

    void startControlCloudPhone();

    void startQuitCloudPhone();

    float getStartSuccessRate();

    boolean isFinished();

}
