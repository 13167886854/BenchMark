package com.example.benchmark.Service;

public class MiGuPlayStabilityService implements IStabilityService{

    private int mCurrentMonitorNum = 0;

    @Override
    public void onMonitor() {

    }

    @Override
    public void startControlCloudPhone() {

    }

    @Override
    public void startQuitCloudPhone() {

    }

    @Override
    public int getCurrentMonitorNum() {
        return mCurrentMonitorNum;
    }
}
