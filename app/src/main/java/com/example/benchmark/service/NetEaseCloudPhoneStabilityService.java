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

/**
 * NetEaseCloudPhoneStabilityService
 *
 * @version 1.0
 * @since 2023/3/7 17:21
 */
public class NetEaseCloudPhoneStabilityService implements IStabilityService {
    private static final String TAG = "NetEaseCloudPhoneStabilityService";

    private final String nodeTextMoreGame = "更多游戏";
    private final String nodeTextPhoneGame = "手机游戏";
    private final String nodeTextCloudPhone = "云手机";
    private final String nodeTextInstantPlay = "秒玩";
    private final String nodeIdLoadingGame = "com.netease.android.cloudgame:id/gaming_reconnect_text";
    private final String nodeTextLoadingGame = "游戏加载中，请稍等";
    private final String nodeIdCloudPhoneIntroductionBtn = "com.netease.android.cloudgame:id/ok_btn";
    private final String nodeTextCloudPhoneIntroductionBtn = "知道了";
    private final String nodeIdStillQuit = "com.netease.android.cloudgame:id/quit_btn";
    private final String nodeTextStillQuit = "仍要退出";
    private final String nodeIdCancelStillQuit = "com.netease.android.cloudgame:id/cancel_btn";
    private final String nodeIdQuitPhone = "com.netease.android.cloudgame:id/dialog_sure";
    private final String nodeTextQuitPhone = "退出";
    private final String nodeTextContinueWait = "继续等待";
    private final String nodeIdContinueWait = "com.netease.android.cloudgame:id/vip_continue_wait_btn";

    private final MyAccessibilityService service;

    private int mCurrentMonitorNum = 0;

    private boolean isFirstMonitor = true;
    private boolean isClickPhoneGame = false;
    private boolean isClickInstantPlay = false;

    private long mStartTime;

    public NetEaseCloudPhoneStabilityService(MyAccessibilityService service) {
        this.service = service;
    }

    @Override
    public void onMonitor() {
        if (!isClickPhoneGame) {
            if (!isFirstMonitor) {
                AccessibilityNodeInfo moreGame = AccessibilityUtil.findNodeInfoByText(
                        service, "android.widget.TextView", nodeTextMoreGame);
                if (moreGame == null) {
                    return;
                }
                AccessibilityUtil.performClick(moreGame);
                isFirstMonitor = false;
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    Log.e(TAG, "onMonitor: ", e);
                }
            }
            AccessibilityNodeInfo phoneGame = AccessibilityUtil.findNodeInfoByText(
                    service, "android.widget.TextView", nodeTextMoreGame);
            if (phoneGame == null) {
                return;
            }
            AccessibilityUtil.performClick(phoneGame);
            isClickPhoneGame = true;
        }
        if (!isClickInstantPlay) {
            clickInstantPlay();
        }
    }

    private void clickInstantPlay() {
        AccessibilityNodeInfo viewPager = AccessibilityUtil.findNodeInfo(service,
                "com.netease.android.cloudgame:id/view_pager", "");
        if (viewPager == null) {
            return;
        }
        forMethod(viewPager);
    }

    private void forMethod(AccessibilityNodeInfo viewPager) {
        for (int i = 0; i < viewPager.getChildCount(); i++) {
            AccessibilityNodeInfo recyclerView = viewPager.getChild(i);
            for (int j = 0; j < recyclerView.getChildCount(); j++) {
                AccessibilityNodeInfo frameLayout = recyclerView.getChild(j);
                if (!"android.widget.FrameLayout".equals(frameLayout.getClassName().toString())) {
                    continue;
                }
                boolean isCloudPhone = false;
                if (forMethod(frameLayout, isCloudPhone)) {
                    return;
                }
            }
        }
    }

    private boolean forMethod(AccessibilityNodeInfo frameLayout, boolean isCloudPhone) {
        boolean isCloudPhoneTemp = isCloudPhone;
        for (int k = 0; k < frameLayout.getChildCount(); k++) {
            AccessibilityNodeInfo node = frameLayout.getChild(k);
            if (node.getText() != null && nodeTextCloudPhone.equals(node.getText().toString())) {
                isCloudPhoneTemp = true;
            }
            if (isCloudPhoneTemp
                    && "android.view.ViewGroup".equals(node.getClassName().toString())) {
                Log.e("NetEase", "Click");
                AccessibilityUtil.performClick(node.getChild(0));
                isClickInstantPlay = true;
                mStartTime = System.currentTimeMillis();
                startControlCloudPhone();
                startQuitCloudPhone();
                return true;
            }
        }
        return false;
    }

    @Override
    public void startControlCloudPhone() {
        AccessibilityNodeInfo loadGameNode = AccessibilityUtil.findNodeInfo(service,
                nodeIdLoadingGame, nodeTextLoadingGame);
        while (loadGameNode == null) {
            AccessibilityNodeInfo continueWaitNode = AccessibilityUtil.findNodeInfo(service,
                    nodeIdContinueWait, nodeTextContinueWait);
            if (continueWaitNode != null) {
                AccessibilityUtil.performClick(continueWaitNode);
            }
            AccessibilityNodeInfo enterGameNode = AccessibilityUtil.findNodeInfoByText(
                    service, "android.widget.Button", "进入游戏");
            if (enterGameNode != null) {
                AccessibilityUtil.performClick(enterGameNode);
            }
            loadGameNode = AccessibilityUtil
                    .findNodeInfo(service, nodeIdLoadingGame, nodeTextLoadingGame);
        }
        while (loadGameNode != null) {
            loadGameNode = AccessibilityUtil
                    .findNodeInfo(service, nodeIdLoadingGame, nodeTextLoadingGame);
        }
        service.mOpenTime.add(System.currentTimeMillis() - mStartTime);
    }

    @Override
    public void startQuitCloudPhone() {
        service.performGlobalAction(GLOBAL_ACTION_BACK);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            Log.e(TAG, "startQuitCloudPhone: ", e);
        }
        AccessibilityNodeInfo nodeBtnQuit = AccessibilityUtil.findNodeInfo(service,
                nodeIdStillQuit, nodeTextStillQuit);
        while (nodeBtnQuit == null) {
            nodeBtnQuit = AccessibilityUtil.findNodeInfo(service,
                    nodeIdStillQuit, nodeTextStillQuit);
        }
        AccessibilityUtil.performClick(nodeBtnQuit);
        long quitTime = System.currentTimeMillis();
        AccessibilityNodeInfo phoneGame = AccessibilityUtil.findNodeInfoByText(
                service, "android.widget.TextView", nodeTextPhoneGame);
        while (phoneGame == null) {
            phoneGame = AccessibilityUtil.findNodeInfoByText(
                    service, "android.widget.TextView", nodeTextPhoneGame);
        }
        service.mQuitTimes.add(System.currentTimeMillis() - quitTime);
        mCurrentMonitorNum++;
        isClickPhoneGame = false;
        isClickInstantPlay = false;
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            Log.e(TAG, "startQuitCloudPhone: ", e);
        }
    }

    @Override
    public int getCurrentMonitorNum() {
        return mCurrentMonitorNum;
    }
}
