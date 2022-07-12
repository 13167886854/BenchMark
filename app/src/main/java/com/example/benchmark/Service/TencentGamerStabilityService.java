package com.example.benchmark.Service;

import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.benchmark.utils.AccessibilityCallback;
import com.example.benchmark.utils.AccessibilityClassFindCallback;
import com.example.benchmark.utils.AccessibilityUtil;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;
import com.example.benchmark.utils.StatusBarUtil;

import java.util.List;

public class TencentGamerStabilityService implements IStabilityService {

    private final int screenHeight = CacheUtil.getInt(CacheConst.KEY_SCREEN_HEIGHT);
    private final int screenWidth = CacheUtil.getInt(CacheConst.KEY_SCREEN_WIDTH);
    private final String NODE_CLASS_PARENT_TAB_HOME = "android.widget.RelativeLayout";
    private final String NODE_CLASS_TAB_HOME = "android.widget.TextView";
    private final String NODE_TEXT_TAB_HOME = "首页";
    private final String NODE_ID_TAB_HOME = "com.tencent.gamereva:id/tv_tab_title";
    private final String NODE_CLASS_PARENT_TAB_RANK = "android.view.ViewGroup";
    private final String NODE_CLASS_TAB_RANK = "android.widget.TextView";
    private final String NODE_TEXT_TAB_RANK = "排行榜";
    private final String NODE_ID_TAB_RANK = "com.tencent.gamereva:id/tv_tab_title";
    private final String NODE_ID_GAME_ITEM = "com.tencent.gamereva:id/appointment_card";
    private final String NODE_CLASS_INSTANT_PLAY = "android.widget.Button";
    private final String NODE_TEXT_INSTANT_PLAY = "秒玩";
    private final String NODE_ID_INSTANT_PLAY = "com.tencent.gamereva:id/game_play";
    private final String NODE_ID_DETAIL_INSTANT_PLAY = "com.tencent.gamereva:id/banner_game_play";
    private final String NODE_TEXT_DETAIL_INSTANT_PLAY = "秒玩";
    private final String NODE_ID_CONTINUE_GAME = "com.tencent.gamereva:id/sub_button";
    private final String NODE_TEXT_CONTINUE_GAME = "继续游戏";
    private final String NODE_CLASS_LOADING_GAME = "android.widget.ProgressBar";
    private final String NODE_ID_LOADING_GAME = "com.tencent.gamereva:id/loading_view";
    private final String NODE_TEXT_QUIT_GAME = "退出游戏";
    private final String NODE_ID_QUIT_GAME = "com.tencent.gamereva:id/tab_name_tv";
    private final String NODE_TEXT_CONFIRM_QUIT = "确认";
    private final String NODE_ID_CONFIRM_QUIT = "com.tencent.gamereva:id/main_button";
    private final String NODE_TEXT_CLOSE_AFTER_QUIT = "关闭";
    private final String NODE_ID_CLOSE_AFTER_QUIT = "com.tencent.gamereva:id/delete_device_record";

    private int mCurrentMonitorNum = 0;

    private final StabilityMonitorService service;

    private boolean isClickHome = false;
    private boolean isClickRank = false;
    private boolean isClickInstantPlay = false;

    public TencentGamerStabilityService(StabilityMonitorService service) {
        this.service = service;
    }

    @Override
    public void onMonitor() {
        if (!isClickInstantPlay) {
            AccessibilityNodeInfo instantPlayNode = AccessibilityUtil.findNodeInfo(
                    service, NODE_ID_DETAIL_INSTANT_PLAY, NODE_TEXT_DETAIL_INSTANT_PLAY);
            if (instantPlayNode == null) return;
            AccessibilityUtil.performClick(instantPlayNode);
            isClickInstantPlay = true;
            startControlCloudPhone();
        }
    }

    @Override
    public void startControlCloudPhone() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return;
        long startTime = System.currentTimeMillis();
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AccessibilityNodeInfo continueGameNode = AccessibilityUtil.findNodeInfo(
                service, NODE_ID_CONTINUE_GAME, NODE_TEXT_CONTINUE_GAME);
        if (continueGameNode != null) {
            AccessibilityUtil.performClick(continueGameNode);
            startTime = System.currentTimeMillis();
        }
        boolean isStartSuccess = false;
        long startTapTime = System.currentTimeMillis();
        while (!isStartSuccess) {
            AccessibilityNodeInfo quitGameNode = AccessibilityUtil
                    .findNodeInfo(service, NODE_ID_QUIT_GAME, NODE_TEXT_QUIT_GAME);
            if (quitGameNode != null) {
                service.mOpenTime.add(System.currentTimeMillis() - startTime);
                AccessibilityUtil.performClick(quitGameNode);
                isStartSuccess = true;
                startQuitCloudPhone();
            }
            if (System.currentTimeMillis() - startTapTime < 1000L) continue;
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
            e.printStackTrace();
        }
        AccessibilityNodeInfo confirmQuitNode = AccessibilityUtil.findNodeInfo(
                service, NODE_ID_CONFIRM_QUIT, NODE_TEXT_CONFIRM_QUIT);
        while (confirmQuitNode == null) {
            confirmQuitNode = AccessibilityUtil.findNodeInfo(
                    service, NODE_ID_CONFIRM_QUIT, NODE_TEXT_CONFIRM_QUIT);
        }
        AccessibilityUtil.performClick(confirmQuitNode);
        long quitTime = System.currentTimeMillis();
        AccessibilityNodeInfo closeAfterQuitNode = AccessibilityUtil.findNodeInfo(
                service, NODE_ID_CLOSE_AFTER_QUIT, NODE_TEXT_CLOSE_AFTER_QUIT);
        while (closeAfterQuitNode == null) {
            closeAfterQuitNode = AccessibilityUtil.findNodeInfo(
                    service, NODE_ID_CLOSE_AFTER_QUIT, NODE_TEXT_CLOSE_AFTER_QUIT);
        }
        AccessibilityUtil.performClick(closeAfterQuitNode);
        service.mQuitTimes.add(System.currentTimeMillis() - quitTime);
        mCurrentMonitorNum++;
        isClickInstantPlay = false;
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCurrentMonitorNum() {
        return mCurrentMonitorNum;
    }
}
