/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.service;

import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.benchmark.utils.AccessibilityCallback;
import com.example.benchmark.utils.AccessibilityUtil;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;
import com.example.benchmark.utils.StatusBarUtil;

/**
 * TencentGamerStabilityService
 *
 * @version 1.0
 * @since 2023/3/7 17:21
 */
public class TencentGamerStabilityService implements IStabilityService {
    private static final String TAG = "TencentGamerStabilityService";
    private final int screenHeight = CacheUtil.getInt(CacheConst.KEY_SCREEN_HEIGHT);
    private final int screenWidth = CacheUtil.getInt(CacheConst.KEY_SCREEN_WIDTH);
    private final String nodeClassParentTabHome = "android.widget.RelativeLayout";
    private final String nodeClassTabHome = "android.widget.TextView";
    private final String nodeTextTabHome = "首页";
    private final String nodeIdTabHome = "com.tencent.gamereva:id/tv_tab_title";
    private final String nodeClassParentTabRank = "android.view.ViewGroup";
    private final String nodeClassTabRank = "android.widget.TextView";
    private final String nodeTextTabRank = "排行榜";
    private final String nodeIdTabRank = "com.tencent.gamereva:id/tv_tab_title";
    private final String nodeIdGameItem = "com.tencent.gamereva:id/appointment_card";
    private final String nodeClassInstantPlay = "android.widget.Button";
    private final String nodeTextInstantPlay = "秒玩";
    private final String nodeIdInstantPlay = "com.tencent.gamereva:id/game_play";
    private final String nodeIdDetailInstantPlay = "com.tencent.gamereva:id/banner_game_play";
    private final String nodeTextDetailInstantPlay = "秒玩|游戏充值送时长";
    private final String nodeIdContinueGame = "com.tencent.gamereva:id/sub_button";
    private final String nodeTextContinueGame = "继续游戏";
    private final String nodeClassLoadingGame = "android.widget.ProgressBar";
    private final String nodeIdLoadingGame = "com.tencent.gamereva:id/loading_view";
    private final String nodeTextQuitGame = "退出游戏";
    private final String nodeIdQuitGame = "com.tencent.gamereva:id/tab_name_tv";
    private final String nodeTextConfirmQuit = "确认";
    private final String nodeIdConfirmQuit = "com.tencent.gamereva:id/main_button";
    private final String nodeTextCloseAfterQuit = "关闭";
    private final String nodeIdCloseAfterQuit = "com.tencent.gamereva:id/delete_device_record";

    private int mCurrentMonitorNum = 0;

    private final MyAccessibilityService service;

    private boolean isClickHome = false;
    private boolean isClickRank = false;
    private boolean isClickInstantPlay = false;

    public TencentGamerStabilityService(MyAccessibilityService service) {
        this.service = service;
    }

    @Override
    public void onMonitor() {
        if (!isClickInstantPlay) {
            AccessibilityNodeInfo instantPlayNode = AccessibilityUtil.findNodeInfo(
                    service, nodeIdDetailInstantPlay, "");
            if (instantPlayNode == null) {
                return;
            }
            AccessibilityUtil.performClick(instantPlayNode);
            isClickInstantPlay = true;
            startControlCloudPhone();
            Log.e("TWT", "onMonitor: 123123");
        }
    }

    @Override
    public void startControlCloudPhone() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return;
        }
        long startTime = System.currentTimeMillis();
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            Log.e(TAG, "startControlCloudPhone: ", e);
        }
        AccessibilityNodeInfo continueGameNode = AccessibilityUtil.findNodeInfo(
                service, nodeIdContinueGame, nodeTextContinueGame);
        if (continueGameNode != null) {
            AccessibilityUtil.performClick(continueGameNode);
            startTime = System.currentTimeMillis();
        }
        boolean isStartSuccess = false;
        long startTapTime = System.currentTimeMillis();
        while (!isStartSuccess) {
            AccessibilityNodeInfo quitGameNode = AccessibilityUtil
                    .findNodeInfo(service, nodeIdQuitGame, nodeTextQuitGame);
            if (quitGameNode != null) {
                service.mOpenTime.add(System.currentTimeMillis() - startTime);
                AccessibilityUtil.performClick(quitGameNode);
                isStartSuccess = true;
                startQuitCloudPhone();
            }
            if (System.currentTimeMillis() - startTapTime < 1000L) {
                continue;
            }
            startTapTime = System.currentTimeMillis();
            AccessibilityUtil.tap(
                    service,
                    StatusBarUtil.getStatusBarHeight(service) + 8,
                    screenWidth / 3,
                    new AccessibilityCallback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onFailure() {
                        }
                    }
            );
        }
    }

    @Override
    public void startQuitCloudPhone() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            Log.e(TAG, "startQuitCloudPhone: ", e);
        }
        AccessibilityNodeInfo confirmQuitNode = AccessibilityUtil.findNodeInfo(
                service, nodeIdConfirmQuit, nodeTextConfirmQuit);
        while (confirmQuitNode == null) {
            confirmQuitNode = AccessibilityUtil.findNodeInfo(
                    service, nodeIdConfirmQuit, nodeTextConfirmQuit);
        }
        AccessibilityUtil.performClick(confirmQuitNode);
        long quitTime = System.currentTimeMillis();
        AccessibilityNodeInfo closeAfterQuitNode = AccessibilityUtil.findNodeInfo(
                service, nodeIdCloseAfterQuit, nodeTextCloseAfterQuit);
        while (closeAfterQuitNode == null) {
            closeAfterQuitNode = AccessibilityUtil.findNodeInfo(
                    service, nodeIdCloseAfterQuit, nodeTextCloseAfterQuit);
        }
        AccessibilityUtil.performClick(closeAfterQuitNode);
        service.mQuitTimes.add(System.currentTimeMillis() - quitTime);
        mCurrentMonitorNum++;
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
