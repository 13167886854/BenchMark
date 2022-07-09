package com.example.benchmark.Service;

import android.os.Build;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.benchmark.utils.AccessibilityCallback;
import com.example.benchmark.utils.AccessibilityUtil;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;
import com.example.benchmark.utils.StatusBarUtil;

import java.util.List;

public class TencentGamerStabilityService implements IStabilityService {

    private final int screenHeight = CacheUtil.getInt(CacheConst.KEY_SCREEN_HEIGHT);
    private final int screenWidth = CacheUtil.getInt(CacheConst.KEY_SCREEN_WIDTH);
    private final String TARGET_GAME_NAME = "王者荣耀";
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
    private boolean isStartSuccess = false;

    private long mStartTime;

    public TencentGamerStabilityService(StabilityMonitorService service) {
        this.service = service;
    }

    @Override
    public void onMonitor() {
        if (!isClickHome) {
            AccessibilityNodeInfo tabHomeNode = AccessibilityUtil.findNodeByClassName(
                    service, NODE_CLASS_PARENT_TAB_HOME, nodeInfo -> {
                        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                            AccessibilityNodeInfo childNode = nodeInfo.getChild(i);
                            if (NODE_CLASS_TAB_HOME.equals(childNode.getClassName().toString())
                                    && NODE_TEXT_TAB_HOME.equals(childNode.getText().toString())) {
                                return true;
                            }
                        }
                        return false;
                    });
            if (tabHomeNode != null) {
                AccessibilityUtil.performClick(tabHomeNode);
                isClickHome = true;
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!isClickRank && isClickHome) {
            AccessibilityNodeInfo tabRankNode = AccessibilityUtil.findNodeByClassName(
                    service, NODE_CLASS_PARENT_TAB_RANK, nodeInfo -> {
                        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                            AccessibilityNodeInfo childNode = nodeInfo.getChild(i);
                            if (childNode == null) continue;
                            if (NODE_CLASS_TAB_RANK.equals(childNode.getClassName().toString())
                                    && childNode.getText() != null
                                    && NODE_TEXT_TAB_RANK.equals(childNode.getText().toString()))
                                return true;
                        }
                        return false;
                    });
            if (tabRankNode != null) {
                AccessibilityUtil.performClick(tabRankNode);
                isClickRank = true;
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!isClickInstantPlay && isClickHome && isClickRank) {
            List<AccessibilityNodeInfo> gameItemsNode = service.getRootInActiveWindow()
                    .findAccessibilityNodeInfosByViewId(NODE_ID_GAME_ITEM);
            for (AccessibilityNodeInfo gameItemNode : gameItemsNode) {
                boolean isTargetGame = false;
                for (int i = 0; i < gameItemNode.getChildCount(); i++) {
                    AccessibilityNodeInfo childNode = gameItemNode.getChild(i);
                    if ("android.widget.TextView".equals(childNode.getClassName().toString())
                            && TARGET_GAME_NAME.equals(childNode.getText().toString()))
                        isTargetGame = true;
                    if (isTargetGame && NODE_CLASS_INSTANT_PLAY.equals(
                            childNode.getClassName().toString())
                            && NODE_TEXT_INSTANT_PLAY.equals(childNode.getText().toString())) {
                        AccessibilityUtil.performClick(childNode);
                        isClickInstantPlay = true;
                        mStartTime = System.currentTimeMillis();
                        startControlCloudPhone();
                        break;
                    }
                }
                if (isTargetGame) break;
            }
        }
    }

    @Override
    public void startControlCloudPhone() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return;
        isStartSuccess = false;
        long startTapTime = System.currentTimeMillis();
        while (!isStartSuccess) {
            AccessibilityNodeInfo quitGameNode = AccessibilityUtil
                    .findNodeInfo(service, NODE_ID_QUIT_GAME, NODE_TEXT_QUIT_GAME);
            if (quitGameNode != null) {
//                Log.e("QT", "openTime:"+(System.currentTimeMillis() - mStartTime)+" mStartTime:"+mStartTime+" curTime:"+System.currentTimeMillis());
                service.mOpenTime.add(System.currentTimeMillis() - mStartTime);
                AccessibilityUtil.performClick(quitGameNode);
                isStartSuccess = true;
                startQuitCloudPhone();
            }
            if (System.currentTimeMillis() - startTapTime < 500L) continue;
            startTapTime = System.currentTimeMillis();
            AccessibilityUtil.tap(
                    service,
                    StatusBarUtil.getStatusBarHeight(service) + 10,
                    screenWidth / 3,
                    new AccessibilityCallback() {
                        @Override
                        public void onSuccess() {
//                            Log.e("qt", "TAP SUCCESS");
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
