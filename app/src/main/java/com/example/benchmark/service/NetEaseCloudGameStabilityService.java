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
import com.example.benchmark.utils.TapUtil;

/**
 * NetEaseCloudGameStabilityService
 *
 * @version 1.0
 * @since 2023/3/7 17:21
 */
public class NetEaseCloudGameStabilityService implements IStabilityService {
    private static final String TAG = "NetEaseCloudGameStabilityService";

    // 超过一定时间仍未进入游戏则表示加载失败  If you do not enter the game after a certain time, the loading fails
    private final long monitorFialTime = 8000L;
    private final String nodeIdInstantPlay = "com.netease.android.cloudgame:id/start_game_btn";
    private final String nodeTextInstantPlay = "秒玩";
    private final String nodeIdEnterGame = "com.netease.android.cloudgame:id/btn_enter_game";
    private final String nodeTextEnterGame = "进入游戏";
    private final String nodeIdLoadingGame = "com.netease.android.cloudgame:id/gaming_reconnect_text";
    private final String nodeTextLoadingGame = "游戏加载中，请稍等";
    private final String nodeIdOkAfterEnter = "com.netease.android.cloudgame:id/ok_btn";
    private final String nodeTextOkAfterEnter = "知道了";
    private final String nodeIdSureQuit = "com.netease.android.cloudgame:id/dialog_sure";
    private final String nodeTextSureQuit = "退出";
    private final String nodeIdStillQuit = "com.netease.android.cloudgame:id/quit_btn";
    private final String nodeTextStillQuit = "仍要退出";

    private int mCurrentMonitorNum = 0;
    private int mFailMonitorNum = 0;

    private final MyAccessibilityService service;

    private boolean isEnterGame = false;

    private long mStartTime;

    /**
     * NetEaseCloudGameStabilityService
     *
     * @param service description
     * @return 
     * @date 2023/3/10 16:11
     */
    public NetEaseCloudGameStabilityService(MyAccessibilityService service) {
        this.service = service;
    }

    /**
     * onMonitor
     *
     * @return void
     * @date 2023/3/10 16:11
     */
    @Override
    public void onMonitor() {
        if (!isEnterGame) {
            AccessibilityNodeInfo instantPlayNode = AccessibilityUtil.findNodeInfo(
                    service, nodeIdInstantPlay, nodeTextInstantPlay);
            if (instantPlayNode == null) {
                return;
            }
            AccessibilityUtil.performClick(instantPlayNode);
            mStartTime = System.currentTimeMillis();
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                Log.e(TAG, "onMonitor: ", e);
            }
            AccessibilityNodeInfo enterGameNode = AccessibilityUtil.findNodeInfo(
                    service, nodeIdEnterGame, nodeTextEnterGame);
            if (enterGameNode != null) {
                AccessibilityUtil.performClick(enterGameNode);
                mStartTime = System.currentTimeMillis();
            }
            long startWaitLoadTime = System.currentTimeMillis();
            AccessibilityNodeInfo loadGameNode = AccessibilityUtil.findNodeInfo(service,
                    nodeIdLoadingGame, nodeTextLoadingGame);
            while (loadGameNode == null && System.currentTimeMillis() - startWaitLoadTime < monitorFialTime) {
                loadGameNode = AccessibilityUtil.findNodeInfo(
                        service, nodeIdLoadingGame, nodeTextLoadingGame);
            }
            if (loadGameNode == null) {
                mFailMonitorNum++;
                return;
            }
            isEnterGame = true;
            while (loadGameNode != null) {
                loadGameNode = AccessibilityUtil.findNodeInfo(
                        service, nodeIdLoadingGame, nodeTextLoadingGame);
            }
            startControlCloudPhone();
            startQuitCloudPhone();
        }
    }

    /**
     * startControlCloudPhone
     *
     * @return void
     * @date 2023/3/10 16:11
     */
    @Override
    public void startControlCloudPhone() {
        service.mOpenTime.add(System.currentTimeMillis() - mStartTime);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            Log.e(TAG, "startControlCloudPhone: ", e);
        }
        AccessibilityNodeInfo okNode = AccessibilityUtil.findNodeInfo(
                service, nodeIdOkAfterEnter, nodeTextOkAfterEnter);
        if (okNode != null) {
            AccessibilityUtil.performClick(okNode);
        }
    }

    /**
     * startQuitCloudPhone
     *
     * @return void
     * @date 2023/3/10 16:11
     */
    @Override
    public void startQuitCloudPhone() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            Log.e(TAG, "startQuitCloudPhone: ", e);
        }
        service.performGlobalAction(GLOBAL_ACTION_BACK);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            Log.e(TAG, "startQuitCloudPhone: ", e);
        }
        AccessibilityNodeInfo sureQuitNode = AccessibilityUtil.findNodeInfo(
                service, nodeIdSureQuit, nodeTextSureQuit);
        AccessibilityNodeInfo stillQuitNode = AccessibilityUtil.findNodeInfo(service,
                nodeIdStillQuit, nodeTextStillQuit);
        while (sureQuitNode == null && stillQuitNode == null) {
            sureQuitNode = AccessibilityUtil.findNodeInfo(
                    service, nodeIdSureQuit, nodeTextSureQuit);
            stillQuitNode = AccessibilityUtil.findNodeInfo(service,
                    nodeIdStillQuit, nodeTextStillQuit);
        }
        if (sureQuitNode != null) {
            AccessibilityUtil.performClick(sureQuitNode);
        }
        if (stillQuitNode != null) {
            AccessibilityUtil.performClick(stillQuitNode);
        }
        long quitTime = System.currentTimeMillis();
        AccessibilityNodeInfo instantPlayNode = AccessibilityUtil.findNodeInfo(
                service, nodeIdInstantPlay, nodeTextInstantPlay);
        while (instantPlayNode == null) {
            instantPlayNode = AccessibilityUtil.findNodeInfo(
                    service, nodeIdInstantPlay, nodeTextInstantPlay);
        }
        service.mQuitTimes.add(System.currentTimeMillis() - quitTime);
        mCurrentMonitorNum++;
        isEnterGame = false;
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            Log.e(TAG, "startQuitCloudPhone: ", e);
        }
    }

    /**
     * getStartSuccessRate
     *
     * @return float
     * @date 2023/3/10 16:12
     */
    @Override
    public float getStartSuccessRate() {
        return (mCurrentMonitorNum - mFailMonitorNum) / (float) TapUtil.mWholeMonitorNum * 100;
    }

    /**
     * getCurrentMonitorNum
     *
     * @return int
     * @date 2023/3/10 16:12
     */
    @Override
    public int getCurrentMonitorNum() {
        return mCurrentMonitorNum;
    }
}
