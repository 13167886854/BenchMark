package com.example.benchmark.Service;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK;

import android.view.accessibility.AccessibilityNodeInfo;

import com.example.benchmark.utils.AccessibilityUtil;

public class HuaweiCloudGameStabilityService implements IStabilityService {

    private final String NODE_ID_BTN_TEST = "com.huawei.cloudphonedaily:id/btn_test";
    private final String NODE_ID_BTN_START_GAME = "com.huawei.cloudphonedaily:id/btn_startGame";
    private final String NODE_ID_BTN_CONTINUE_GAME = "android:id/button1";
    private final String NODE_ID_BTN_QUIT_CLOUD_PHONE = "com.huawei.cloudphonedaily:id/tv_positive";

    private final StabilityMonitorService service;

    private int mCurrentMonitorNum = 0;
    private long mStartTime = 0L;

    private boolean isClickTestBtn = false;
    private boolean isClickStartGameBtn = false;

    public HuaweiCloudGameStabilityService(StabilityMonitorService service) {
        this.service = service;
    }

    @Override
    public void onMonitor() {
        if (!isClickTestBtn) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            AccessibilityNodeInfo nodeBtnTest = AccessibilityUtil.findNodeInfo(
                    service, NODE_ID_BTN_TEST, "");
            if (nodeBtnTest != null) {
                AccessibilityUtil.performClick(nodeBtnTest);
                isClickTestBtn = true;
            }
        }
        if (!isClickStartGameBtn) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            AccessibilityNodeInfo nodeBtnStartGame = AccessibilityUtil.findNodeInfo(
                    service, NODE_ID_BTN_START_GAME, "");
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
                service, NODE_ID_BTN_START_GAME, "");
        AccessibilityNodeInfo nodeBtnContinueGame = AccessibilityUtil.findNodeInfo(
                service, NODE_ID_BTN_CONTINUE_GAME, "");
        while (nodeBtnStartGame != null || nodeBtnContinueGame != null) {
            nodeBtnStartGame = AccessibilityUtil.findNodeInfo(
                    service, NODE_ID_BTN_START_GAME, "");
            nodeBtnContinueGame = AccessibilityUtil.findNodeInfo(
                    service, NODE_ID_BTN_CONTINUE_GAME, "");
        }
        service.mOpenTime.add(System.currentTimeMillis() - mStartTime);
        try {
            // wait cloud phone loading
            Thread.sleep(6000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        service.mStartTimes.add(mStartTime);
    }

    @Override
    public void startQuitCloudPhone() {
        service.performGlobalAction(GLOBAL_ACTION_BACK);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AccessibilityNodeInfo nodeBtnQuit = AccessibilityUtil.findNodeInfo(
                service, NODE_ID_BTN_QUIT_CLOUD_PHONE, "");
        while (nodeBtnQuit == null) {
            service.performGlobalAction(GLOBAL_ACTION_BACK);
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            nodeBtnQuit = AccessibilityUtil
                    .findNodeInfo(service, NODE_ID_BTN_QUIT_CLOUD_PHONE, "");
        }
        AccessibilityUtil.performClick(nodeBtnQuit);
        long mQuitTime = System.currentTimeMillis();
        AccessibilityNodeInfo nodeBtnStartGame = AccessibilityUtil.findNodeInfo(
                service, NODE_ID_BTN_START_GAME, "");
        while (nodeBtnStartGame == null) nodeBtnStartGame = AccessibilityUtil
                .findNodeInfo(service, NODE_ID_BTN_START_GAME, "");
        service.mQuitTimes.add(System.currentTimeMillis() - mQuitTime);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void clickContinueGameIfExist() {
        new Thread(() -> {
            try {
                Thread.sleep(1000L);
                AccessibilityNodeInfo nodeBtnContinueGame = AccessibilityUtil.findNodeInfo(
                        service, NODE_ID_BTN_CONTINUE_GAME, "");
                if (nodeBtnContinueGame != null) {
                    AccessibilityUtil.performClick(nodeBtnContinueGame);
                    mStartTime = System.currentTimeMillis();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public int getCurrentMonitorNum() {
        return mCurrentMonitorNum;
    }
}
