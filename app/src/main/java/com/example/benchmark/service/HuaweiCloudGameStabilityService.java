/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.service;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK;

import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.benchmark.utils.AccessibilityUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * HuaweiCloudGameStabilityService
 *
 * @version 1.0
 * @since 2023/3/7 17:21
 */
public class HuaweiCloudGameStabilityService implements IStabilityService {
    private static final String TAG = "HuaweiCloudGameStabilityService";

    private final String nodeIdBtnTest = "com.huawei.cloudphonedaily:id/btn_test";
    private final String nodeIdBtnStartGame = "com.huawei.cloudphonedaily:id/btn_startGame";
    private final String nodeIdBtnContinueGame = "android:id/button1";
    private final String nodeIdBtnQuitCloudPhone = "com.huawei.cloudphonedaily:id/tv_positive";

    private final MyAccessibilityService service;

    private ExecutorService threadPool = Executors.newCachedThreadPool();

    private int mCurrentMonitorNum = 0;
    private long mStartTime = 0L;

    private boolean isClickTestBtn = false;
    private boolean isClickStartGameBtn = false;

    public HuaweiCloudGameStabilityService(MyAccessibilityService service) {
        this.service = service;
    }

    @Override
    public void onMonitor() {
        if (!isClickTestBtn) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                Log.e(TAG, "onMonitor: ", e);
            }
            AccessibilityNodeInfo nodeBtnTest = AccessibilityUtil.findNodeInfo(
                    service, nodeIdBtnTest, "");
            if (nodeBtnTest != null) {
                AccessibilityUtil.performClick(nodeBtnTest);
                isClickTestBtn = true;
            }
        }
        if (!isClickStartGameBtn) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                Log.e(TAG, "onMonitor: ", e);
            }
            AccessibilityNodeInfo nodeBtnStartGame = AccessibilityUtil.findNodeInfo(
                    service, nodeIdBtnStartGame, "");
            if (nodeBtnStartGame != null) {
                service.startCaptureScreen();
                AccessibilityUtil.performClick(nodeBtnStartGame);
                isClickTestBtn = true;
                isClickStartGameBtn = true;
                startControlCloudPhone();
                startQuitCloudPhone();
                mCurrentMonitorNum++;
                isClickStartGameBtn = false;
            }
        }
    }

    @Override
    public void startControlCloudPhone() {
        mStartTime = System.currentTimeMillis();
        clickContinueGameIfExist();
        AccessibilityNodeInfo nodeBtnStartGame = AccessibilityUtil.findNodeInfo(
                service, nodeIdBtnStartGame, "");
        AccessibilityNodeInfo nodeBtnContinueGame = AccessibilityUtil.findNodeInfo(
                service, nodeIdBtnContinueGame, "");
        while (nodeBtnStartGame != null || nodeBtnContinueGame != null) {
            nodeBtnStartGame = AccessibilityUtil.findNodeInfo(
                    service, nodeIdBtnStartGame, "");
            nodeBtnContinueGame = AccessibilityUtil.findNodeInfo(
                    service, nodeIdBtnContinueGame, "");
        }
        service.mOpenTime.add(System.currentTimeMillis() - mStartTime);
        try {
            // wait cloud phone loading
            Thread.sleep(6000L);
        } catch (InterruptedException e) {
            Log.e(TAG, "startControlCloudPhone: ", e);
        }
        service.mStartTimes.add(mStartTime);
    }

    @Override
    public void startQuitCloudPhone() {
        service.performGlobalAction(GLOBAL_ACTION_BACK);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            Log.e(TAG, "startQuitCloudPhone: ", e);
        }
        AccessibilityNodeInfo nodeBtnQuit = AccessibilityUtil.findNodeInfo(
                service, nodeIdBtnQuitCloudPhone, "");
        while (nodeBtnQuit == null) {
            service.performGlobalAction(GLOBAL_ACTION_BACK);
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                Log.e(TAG, "startQuitCloudPhone: ", e);
            }
            nodeBtnQuit = AccessibilityUtil
                    .findNodeInfo(service, nodeIdBtnQuitCloudPhone, "");
        }
        AccessibilityUtil.performClick(nodeBtnQuit);
        long mQuitTime = System.currentTimeMillis();
        AccessibilityNodeInfo nodeBtnStartGame = AccessibilityUtil.findNodeInfo(
                service, nodeIdBtnStartGame, "");
        while (nodeBtnStartGame == null) {
            nodeBtnStartGame = AccessibilityUtil
                    .findNodeInfo(service, nodeIdBtnStartGame, "");
        }
        service.mQuitTimes.add(System.currentTimeMillis() - mQuitTime);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            Log.e(TAG, "startQuitCloudPhone: ", e);
        }
    }

    private void clickContinueGameIfExist() {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000L);
                    AccessibilityNodeInfo nodeBtnContinueGame = AccessibilityUtil.findNodeInfo(
                            service, nodeIdBtnContinueGame, "");
                    if (nodeBtnContinueGame != null) {
                        AccessibilityUtil.performClick(nodeBtnContinueGame);
                        mStartTime = System.currentTimeMillis();
                    }
                } catch (InterruptedException ex) {
                    Log.e(TAG, "startQuitCloudPhone: ", ex);
                }
            }
        });
    }

    @Override
    public int getCurrentMonitorNum() {
        return mCurrentMonitorNum;
    }
}
