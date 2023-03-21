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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ECloudPhoneStabilityService
 *
 * @version 1.0
 * @since 2023/3/7 17:20
 */
public class ECloudPhoneStabilityService implements IStabilityService {
    private static final String TAG = "ECloudPhoneStabilityService";

    private final int screenHeight = CacheUtil.getInt(CacheConst.KEY_SCREEN_HEIGHT);
    private final int screenWidth = CacheUtil.getInt(CacheConst.KEY_SCREEN_WIDTH);
    private final String nodeIdClickView = "com.chinamobile.cmss.saas.cloundphone:id/index_img";
    private final String nodeIdQuitPhone = "com.chinamobile.cmss.saas.cloundphone:id/netwrok_ok";
    private final String nodeTextQuitPhone = "确认";
    private final String nodeIdNoNotice = "com.chinamobile.cmss.saas.cloundphone:id/netwrok_check";

    private final MyAccessibilityService service;

    private int mCurrentMonitorNum = 0;
    private long mQuitTime = 0L;
    private long mLastTapTime = 0L;

    private boolean isClickQuitNotice = false;
    private boolean isConnectSuccess = false;
    private boolean isTapSuccess = false;

    private ExecutorService threadPool = Executors.newCachedThreadPool();

    public ECloudPhoneStabilityService(MyAccessibilityService service) {
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
            if (mLastTapTime != 0L && System.currentTimeMillis() - mLastTapTime < 1000L) {
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
        long mStartTime = System.currentTimeMillis();
        AccessibilityNodeInfo clickNode = AccessibilityUtil.findNodeInfo(
                service, nodeIdClickView, "");
        while (clickNode != null || AccessibilityUtil.findIsExistClass(
                service, "android.widget.ProgressBar")) {
            clickNode = AccessibilityUtil.findNodeInfo(
                    service, nodeIdClickView, "");
        }
        Log.e("QT", "openTime:" + (System.currentTimeMillis() - mStartTime));
        service.mOpenTime.add(System.currentTimeMillis() - mStartTime);
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            Log.e(TAG, "startControlCloudPhone: " + e.toString());
        }
        service.mStartTimes.add(mStartTime);
    }

    @Override
    public void startQuitCloudPhone() {
        // 双击返回键退出云手机  Double click the back button to exit the cloud phone
        service.performGlobalAction(GLOBAL_ACTION_BACK);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            Log.e(TAG, "startQuitCloudPhone: ", e);
        }
        service.performGlobalAction(GLOBAL_ACTION_BACK);
        mQuitTime = System.currentTimeMillis();
        closeDialogIfExistWhenQuit();
        AccessibilityNodeInfo nodeClickView = null;
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

    private void closeDialogIfExistWhenQuit() {
        if (isClickQuitNotice) {
            return;
        }
        threadPool.execute(new Runnable() {
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
