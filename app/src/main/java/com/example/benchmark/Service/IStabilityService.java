package com.example.benchmark.Service;
import com.example.benchmark.utils.TapUtil;



public interface IStabilityService {


    void onMonitor();

    void startControlCloudPhone();

    void startQuitCloudPhone();

    int getCurrentMonitorNum();

    default float getStartSuccessRate() {
        return getCurrentMonitorNum() / (float) TapUtil.mWholeMonitorNum * 100;
    }

    default boolean isFinished() {
        return getCurrentMonitorNum() == TapUtil.mWholeMonitorNum;
    }

}
