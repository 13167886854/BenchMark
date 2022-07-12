package com.example.benchmark.Service;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK;

import android.view.accessibility.AccessibilityNodeInfo;

import com.example.benchmark.utils.AccessibilityUtil;

public class NetEaseCloudGameStabilityService implements IStabilityService {

    // 超过一定时间仍未进入游戏则表示加载失败
    private final long MONITOR_FAIL_TIME = 8000L;
    private final String NODE_ID_INSTANT_PLAY = "com.netease.android.cloudgame:id/start_game_btn";
    private final String NODE_TEXT_INSTANT_PLAY = "秒玩";
    private final String NODE_ID_ENTER_GAME = "com.netease.android.cloudgame:id/btn_enter_game";
    private final String NODE_TEXT_ENTER_GAME = "进入游戏";
    private final String NODE_ID_LOADING_GAME = "com.netease.android.cloudgame:id/gaming_reconnect_text";
    private final String NODE_TEXT_LOADING_GAME = "游戏加载中，请稍等";
    private final String NODE_ID_OK_AFTER_ENTER = "com.netease.android.cloudgame:id/ok_btn";
    private final String NODE_TEXT_OK_AFTER_ENTER = "知道了";
    private final String NODE_ID_SURE_QUIT = "com.netease.android.cloudgame:id/dialog_sure";
    private final String NODE_TEXT_SURE_QUIT = "退出";
    private final String NODE_ID_STILL_QUIT = "com.netease.android.cloudgame:id/quit_btn";
    private final String NODE_TEXT_STILL_QUIT = "仍要退出";

    private int mCurrentMonitorNum = 0;
    private int mFailMonitorNum = 0;

    private final StabilityMonitorService service;

    private boolean isEnterGame = false;

    private long mStartTime;

    public NetEaseCloudGameStabilityService(StabilityMonitorService service) {
        this.service = service;
    }

    @Override
    public void onMonitor() {
        if (!isEnterGame) {
            AccessibilityNodeInfo instantPlayNode = AccessibilityUtil.findNodeInfo(
                    service, NODE_ID_INSTANT_PLAY, NODE_TEXT_INSTANT_PLAY);
            if (instantPlayNode == null) return;
            AccessibilityUtil.performClick(instantPlayNode);
            mStartTime = System.currentTimeMillis();
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            AccessibilityNodeInfo enterGameNode = AccessibilityUtil.findNodeInfo(
                    service, NODE_ID_ENTER_GAME, NODE_TEXT_ENTER_GAME);
            if (enterGameNode != null) {
                AccessibilityUtil.performClick(enterGameNode);
                mStartTime = System.currentTimeMillis();
            }
            long startWaitLoadTime = System.currentTimeMillis();
            AccessibilityNodeInfo loadGameNode = AccessibilityUtil.findNodeInfo(service,
                    NODE_ID_LOADING_GAME, NODE_TEXT_LOADING_GAME);
            while (loadGameNode == null && System.currentTimeMillis() - startWaitLoadTime < MONITOR_FAIL_TIME) {
                loadGameNode = AccessibilityUtil.findNodeInfo(
                        service, NODE_ID_LOADING_GAME, NODE_TEXT_LOADING_GAME);
            }
            if (loadGameNode == null) {
                mFailMonitorNum++;
                return;
            }
            isEnterGame = true;
            while (loadGameNode != null) {
                loadGameNode = AccessibilityUtil.findNodeInfo(
                        service, NODE_ID_LOADING_GAME, NODE_TEXT_LOADING_GAME);
            }
            startControlCloudPhone();
            startQuitCloudPhone();
        }
    }

    @Override
    public void startControlCloudPhone() {
        service.mOpenTime.add(System.currentTimeMillis() - mStartTime);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AccessibilityNodeInfo okNode = AccessibilityUtil.findNodeInfo(
                service, NODE_ID_OK_AFTER_ENTER, NODE_TEXT_OK_AFTER_ENTER);
        if (okNode != null) {
            AccessibilityUtil.performClick(okNode);
        }
    }

    @Override
    public void startQuitCloudPhone() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        service.performGlobalAction(GLOBAL_ACTION_BACK);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AccessibilityNodeInfo sureQuitNode = AccessibilityUtil.findNodeInfo(
                service, NODE_ID_SURE_QUIT, NODE_TEXT_SURE_QUIT);
        AccessibilityNodeInfo stillQuitNode = AccessibilityUtil.findNodeInfo(service,
                NODE_ID_STILL_QUIT, NODE_TEXT_STILL_QUIT);
        while (sureQuitNode == null && stillQuitNode == null) {
            sureQuitNode = AccessibilityUtil.findNodeInfo(
                    service, NODE_ID_SURE_QUIT, NODE_TEXT_SURE_QUIT);
            stillQuitNode = AccessibilityUtil.findNodeInfo(service,
                    NODE_ID_STILL_QUIT, NODE_TEXT_STILL_QUIT);
        }
        if (sureQuitNode != null) AccessibilityUtil.performClick(sureQuitNode);
        if (stillQuitNode != null) AccessibilityUtil.performClick(stillQuitNode);
        long quitTime = System.currentTimeMillis();
        AccessibilityNodeInfo instantPlayNode = AccessibilityUtil.findNodeInfo(
                service, NODE_ID_INSTANT_PLAY, NODE_TEXT_INSTANT_PLAY);
        while (instantPlayNode == null) {
            instantPlayNode = AccessibilityUtil.findNodeInfo(
                    service, NODE_ID_INSTANT_PLAY, NODE_TEXT_INSTANT_PLAY);
        }
        service.mQuitTimes.add(System.currentTimeMillis() - quitTime);
        mCurrentMonitorNum++;
        isEnterGame = false;
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public float getStartSuccessRate() {
        return (mCurrentMonitorNum - mFailMonitorNum) / (float) mWholeMonitorNum * 100;
    }

    @Override
    public int getCurrentMonitorNum() {
        return mCurrentMonitorNum;
    }
}
