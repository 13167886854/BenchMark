/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.service;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK;

import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.benchmark.utils.AccessibilityCallback;
import com.example.benchmark.utils.AccessibilityUtil;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;
import com.example.benchmark.utils.ThreadPoolUtil;

/**
 * RedFingerStabilityService
 *
 * @version 1.0
 * @since 2023/3/7 17:21
 */
public class RedFingerStabilityService implements IStabilityService {
    private static final String TAG = "RedFingerStabilityService";

    private final int screenHeight = CacheUtil.getInt(CacheConst.KEY_SCREEN_HEIGHT);
    private final int screenWidth = CacheUtil.getInt(CacheConst.KEY_SCREEN_WIDTH);
    private final String nodeIdClickView = "com.redfinger.app:id/click_view";
    private final String nodeIdStartControl = "com.redfinger.app:id/btnConfirm";
    private final String nodeTextStartControl = "开始控制";
    private final String nodeIdNoNotice = "com.redfinger.app:id/check_box";
    private final String nodeIdContinueControl = "com.redfinger.app:id/tv_ok";
    private final String nodeTextContinueControl = "继续控制";
    private final String nodeIdQuitPhone = "com.redfinger.app:id/tv_ok";
    private final String nodeTextQuitPhone = "确定";

    private final MyAccessibilityService service;

    private int mCurrentMonitorNum = 0;
    private long mStartTime = 0L;
    private long mQuitTime = 0L;
    private long mLastTapTime = 0L;
    private boolean isClickStartControl = false;
    private boolean isClickContinueControl = false;
    private boolean isClickQuitNotice = false;
    private boolean isConnectSuccess = false;
    private boolean isTapSuccess = false;

    /**
     * RedFingerStabilityService
     *
     * @param service description
     * @return
     * @throws null
     * @date 2023/3/8 11:02
     */
    public RedFingerStabilityService(MyAccessibilityService service) {
        this.service = service;
    }

    @Override
    public void onMonitor() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || isFinished()) {
            return;
        }
        if (isTapSuccess) {
            isConnectSuccess = true;
            startControlCloudPhone();
            startQuitCloudPhone();
            mCurrentMonitorNum++;
            isTapSuccess = false;
            isConnectSuccess = false;
            return;
        }
        if (!isConnectSuccess) {
            AccessibilityNodeInfo clickNode = AccessibilityUtil.findNodeInfo(
                    service, nodeIdClickView, "");
            if (clickNode == null) {
                return;
            }
            if (mLastTapTime != 0L && System.currentTimeMillis() - mLastTapTime < 1500L) {
                return;
            }
            mLastTapTime = System.currentTimeMillis();
            service.startCaptureScreen();
            AccessibilityUtil.tap(service, screenWidth / 2, screenHeight / 2,
                    new AccessibilityCallback() {
                        @Override
                        public void onSuccess() {
                            isTapSuccess = true;
                        }

                        @Override
                        public void onFailure() {
                            isTapSuccess = true;
                        }
                    });
        }
    }

    @Override
    public void startControlCloudPhone() {
        mStartTime = System.currentTimeMillis();
        closeDialogIfExistWhenStart();
        AccessibilityNodeInfo clickNode = AccessibilityUtil.findNodeInfo(
                service, nodeIdClickView, "");
        AccessibilityNodeInfo startControlNode = AccessibilityUtil.findNodeInfo(service,
                nodeIdStartControl, nodeTextStartControl);
        AccessibilityNodeInfo continueControlNode = AccessibilityUtil.findNodeInfo(service,
                nodeIdContinueControl, nodeTextContinueControl);
        while (clickNode != null || startControlNode != null || continueControlNode != null) {
            clickNode = AccessibilityUtil.findNodeInfo(
                    service, nodeIdClickView, "");
            startControlNode = AccessibilityUtil.findNodeInfo(service,
                    nodeIdStartControl, nodeTextStartControl);
            continueControlNode = AccessibilityUtil.findNodeInfo(service,
                    nodeIdContinueControl, nodeTextContinueControl);
        }
        service.mOpenTime.add(System.currentTimeMillis() - mStartTime);
        try {
            // wait cloud phone loading
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            Log.e(TAG, "startControlCloudPhone: ", e);
        }
        service.mStartTimes.add(mStartTime);
    }

    @Override
    public void startQuitCloudPhone() {
        // 双击返回键退出云手机
        service.performGlobalAction(GLOBAL_ACTION_BACK);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            Log.e(TAG, "startQuitCloudPhone: ", e);
        }
        service.performGlobalAction(GLOBAL_ACTION_BACK);
        mQuitTime = System.currentTimeMillis();
        closeDialogIfExistWhenQuit();
        AccessibilityNodeInfo nodeClickView = AccessibilityUtil.findNodeInfo(
                service, nodeIdClickView, "");
        while (nodeClickView == null) {
            nodeClickView = AccessibilityUtil.findNodeInfo(
                    service, nodeIdClickView, "");
        }
        service.mQuitTimes.add(System.currentTimeMillis() - mQuitTime);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            Log.e(TAG, "startQuitCloudPhone: ", e);
        }
    }

    private void closeDialogIfExistWhenStart() {
        if (isClickStartControl || isClickContinueControl) {
            return;
        }
        ThreadPoolUtil.getPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!isClickStartControl) {
                        Thread.sleep(1000L);
                        AccessibilityNodeInfo startControlNode = AccessibilityUtil.findNodeInfo(service,
                                nodeIdStartControl, nodeTextStartControl);
                        if (startControlNode != null) {
                            AccessibilityNodeInfo noNoticeNode = AccessibilityUtil.findNodeInfo(service,
                                    nodeIdNoNotice, "");
                            if (noNoticeNode != null) {
                                AccessibilityUtil.performClick(noNoticeNode);
                            }
                            AccessibilityUtil.performClick(startControlNode);
                            mStartTime = System.currentTimeMillis();
                        }
                        isClickStartControl = true;
                    }
                    if (!isClickContinueControl) {
                        Thread.sleep(1000L);
                        AccessibilityNodeInfo continueControlNode = AccessibilityUtil.findNodeInfo(service,
                                nodeIdContinueControl, nodeTextContinueControl);
                        if (continueControlNode != null) {
                            AccessibilityNodeInfo noNoticeNode = AccessibilityUtil.findNodeInfo(service,
                                    nodeIdNoNotice, "");
                            if (noNoticeNode != null) {
                                AccessibilityUtil.performClick(noNoticeNode);
                            }
                            AccessibilityUtil.performClick(continueControlNode);
                            mStartTime = System.currentTimeMillis();
                        }
                        isClickContinueControl = true;
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, "closeDialogIfExistWhenStart: ", e);
                }
            }
        });
    }

    private void closeDialogIfExistWhenQuit() {
        if (isClickQuitNotice) {
            return;
        }
        ThreadPoolUtil.getPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000L);
                    AccessibilityNodeInfo nodeBtnQuit = AccessibilityUtil.findNodeInfo(service,
                            nodeIdQuitPhone, nodeTextQuitPhone);
                    if (nodeBtnQuit != null) {
                        AccessibilityNodeInfo noNotionNode = AccessibilityUtil.findNodeInfo(service,
                                nodeIdNoNotice, "");
                        if (noNotionNode != null) {
                            AccessibilityUtil.performClick(noNotionNode);
                        }
                        AccessibilityUtil.performClick(nodeBtnQuit);
                        mQuitTime = System.currentTimeMillis();
                    }
                    isClickQuitNotice = true;
                } catch (InterruptedException e) {
                    Log.e(TAG, "closeDialogIfExistWhenQuit: ", e);
                }
            }
        });
    }

    @Override
    public int getCurrentMonitorNum() {
        return mCurrentMonitorNum;
    }
}
