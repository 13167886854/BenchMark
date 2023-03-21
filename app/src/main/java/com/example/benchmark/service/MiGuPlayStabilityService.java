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
 * MiGuPlayStabilityService
 *
 * @version 1.0
 * @since 2023/3/7 17:21
 */
public class MiGuPlayStabilityService implements IStabilityService {
    private static final String TAG = "MiGuPlayStabilityService";
    private final String nodeClassParentTabHome = "android.widget.RelativeLayout";
    private final String nodeIdTabHome = "cn.emagsoftware.gamehall:id/tv_tab_title";
    private final String nodeClassTabHome = "android.widget.TextView";
    private final String nodeTextTabHome = "首页";
    private final String nodeClassParentTabRecommend = "android.widget.FrameLayout";
    private final String nodeIdTabRecommend = "cn.emagsoftware.gamehall:id/tab_title";
    private final String nodeClassTabRecommend = "android.widget.TextView";
    private final String nodeTextTabRecommend = "推荐";
    private final String nodeIdHotWeek = "cn.emagsoftware.gamehall:id/week_hot_recycler";
    private final String nodeClassHotWeek = "android.support.v7.widget.RecyclerView";
    private final String nodeIdInstantPlay = "cn.emagsoftware.gamehall:id/detail_gameInfo_play_icon";
    private final String nodeTextInstantPlay = "秒 玩";
    private final String nodeIdSelfPlay = "cn.emagsoftware.gamehall:id/play_self_img";
    private final String nodeIdContinueGame = "cn.emagsoftware.gamehall:id/dialog_button2_id";
    private final String nodeTextContinueGame = "继续游戏";
    private final String nodeIdEnterGameVip = "cn.emagsoftware.gamehall:id/vip_tip_content";
    private final String nodeTextEnterGameVip = "你已进入会员专属通道，排队快人一步";
    private final String nodeTextEnterGameNormal = "当前账户剩余时长";
    private final String nodeIdFlowView = "cn.emagsoftware.gamehall:id/cloud_game_flow_view";
    private final String nodeClassFlowView = "android.view.ViewGroup";

    private final String nodeIdConfirmQuit = "cn.emagsoftware.gamehall:id/dialog_button2_id";
    private final String nodeTextConfirmQuit = "确定";
    private final String nodeIdCancelAfterQuitGame = "cn.emagsoftware.gamehall:id/cancle_txt";
    private final String nodeTextCancelAfterQuitGame = "下次吧";

    private int mCurrentMonitorNum = 0;
    private int mFailMonitorNum = 0;

    private final MyAccessibilityService service;

    private boolean isClickInstantPlay = false;

    /**
     * MiGuPlayStabilityService
     *
     * @param service description
     * @return
     * @date 2023/3/10 16:12
     */
    public MiGuPlayStabilityService(MyAccessibilityService service) {
        this.service = service;
    }

    /**
     * onMonitor
     *
     * @return void
     * @date 2023/3/10 16:12
     */
    @Override
    public void onMonitor() {
        if (!isClickInstantPlay) {
            AccessibilityNodeInfo instantPlay = AccessibilityUtil.findNodeInfo(service,
                    nodeIdInstantPlay, nodeTextInstantPlay);
            if (instantPlay == null) {
                return;
            }
            AccessibilityUtil.performClick(instantPlay);
            startControlCloudPhone();
            startQuitCloudPhone();
        }
    }

    /**
     * startControlCloudPhone
     *
     * @return void
     * @date 2023/3/10 16:12
     */
    @Override
    public void startControlCloudPhone() {
        long startTime = System.currentTimeMillis();
        Log.e("QT", "startTime:" + startTime);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            Log.e(TAG, "startControlCloudPhone: ", e);
        }
        AccessibilityNodeInfo selfPlayNode = AccessibilityUtil.findNodeInfo(
                service, nodeIdSelfPlay, "");
        if (selfPlayNode != null) {
            AccessibilityUtil.performClick(selfPlayNode);
        }
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            Log.e(TAG, "startControlCloudPhone: ", e);
        }
        AccessibilityNodeInfo continueGameNode = AccessibilityUtil.findNodeInfo(
                service, nodeIdContinueGame, nodeTextContinueGame);
        if (continueGameNode != null) {
            AccessibilityUtil.performClick(continueGameNode);
        }
        while (!AccessibilityUtil.findIsContainText(service, "100%")
                || !AccessibilityUtil.findIsContainText(service, "启动完成")) {
            Log.d("QT", "服务启动中");
        }
        Log.e("QT", "openTime:" + (System.currentTimeMillis() - startTime));
        service.mOpenTime.add(System.currentTimeMillis() - startTime);
    }

    /**
     * startQuitCloudPhone
     *
     * @return void
     * @date 2023/3/10 16:12
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
        AccessibilityNodeInfo confirmQuitNode = AccessibilityUtil.findNodeInfo(
                service, nodeIdConfirmQuit, nodeTextConfirmQuit);
        if (confirmQuitNode != null) {
            AccessibilityUtil.performClick(confirmQuitNode);
        }
        long quitTime = System.currentTimeMillis();
        Log.e("QT", "quitTime:" + quitTime);
        AccessibilityNodeInfo gameViewNode = null;
        boolean isCancelNode = false;
        while (gameViewNode == null) {
            gameViewNode = AccessibilityUtil.findNodeInfo(
                    service, nodeIdInstantPlay, nodeTextInstantPlay);
            if (gameViewNode == null) {
                gameViewNode = AccessibilityUtil.findNodeInfo(
                        service, nodeIdCancelAfterQuitGame, nodeTextCancelAfterQuitGame);
                if (gameViewNode != null) {
                    isCancelNode = true;
                }
            }
        }
        service.mQuitTimes.add(System.currentTimeMillis() - quitTime);
        if (isCancelNode) {
            AccessibilityUtil.performClick(gameViewNode);
        }
        isClickInstantPlay = false;
        mCurrentMonitorNum++;
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
