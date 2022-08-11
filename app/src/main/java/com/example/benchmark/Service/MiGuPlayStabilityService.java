package com.example.benchmark.Service;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK;

import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.benchmark.utils.AccessibilityUtil;

public class MiGuPlayStabilityService implements IStabilityService {

    private final String NODE_CLASS_PARENT_TAB_HOME = "android.widget.RelativeLayout";
    private final String NODE_ID_TAB_HOME = "cn.emagsoftware.gamehall:id/tv_tab_title";
    private final String NODE_CLASS_TAB_HOME = "android.widget.TextView";
    private final String NODE_TEXT_TAB_HOME = "首页";
    private final String NODE_CLASS_PARENT_TAB_RECOMMEND = "android.widget.FrameLayout";
    private final String NODE_ID_TAB_RECOMMEND = "cn.emagsoftware.gamehall:id/tab_title";
    private final String NODE_CLASS_TAB_RECOMMEND = "android.widget.TextView";
    private final String NODE_TEXT_TAB_RECOMMEND = "推荐";
    private final String NODE_ID_HOT_WEEK = "cn.emagsoftware.gamehall:id/week_hot_recycler";
    private final String NODE_CLASS_HOT_WEEK = "android.support.v7.widget.RecyclerView";
    private final String NODE_ID_INSTANT_PLAY = "cn.emagsoftware.gamehall:id/detail_gameInfo_play_icon";
    private final String NODE_TEXT_INSTANT_PLAY = "秒 玩";
    private final String NODE_ID_SELF_PLAY = "cn.emagsoftware.gamehall:id/play_self_img";
    private final String NODE_ID_CONTINUE_GAME = "cn.emagsoftware.gamehall:id/dialog_button2_id";
    private final String NODE_TEXT_CONTINUE_GAME = "继续游戏";
    private final String NODE_ID_ENTER_GAME_VIP = "cn.emagsoftware.gamehall:id/vip_tip_content";
    private final String NODE_TEXT_ENTER_GAME_VIP = "你已进入会员专属通道，排队快人一步";
    private final String NODE_TEXT_ENTER_GAME_NORMAL = "当前账户剩余时长";
    private final String NODE_ID_FLOW_VIW = "cn.emagsoftware.gamehall:id/cloud_game_flow_view";
    private final String NODE_CLASS_FLOW_VIW = "android.view.ViewGroup";
    private final String NODE_ID_CONFIRM_QUIT = "cn.emagsoftware.gamehall:id/dialog_button2_id";
    private final String NODE_TEXT_CONFIRM_QUIT = "确定";
    private final String NODE_ID_CANCEL_AFTER_QUIT_GAME = "cn.emagsoftware.gamehall:id/cancle_txt";
    private final String NODE_TEXT_CANCEL_AFTER_QUIT_GAME = "下次吧";

    private int mCurrentMonitorNum = 0;
    private int mFailMonitorNum = 0;

    private final MyAccessibilityService service;

    private boolean isClickInstantPlay = false;

    public MiGuPlayStabilityService(MyAccessibilityService service) {
        this.service = service;
    }

    @Override
    public void onMonitor() {
        if (!isClickInstantPlay) {
            AccessibilityNodeInfo instantPlay = AccessibilityUtil.findNodeInfo(service,
                    NODE_ID_INSTANT_PLAY, NODE_TEXT_INSTANT_PLAY);
            if (instantPlay == null) return;
            AccessibilityUtil.performClick(instantPlay);
            startControlCloudPhone();
            startQuitCloudPhone();
        }
    }

    @Override
    public void startControlCloudPhone() {
        long startTime = System.currentTimeMillis();
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AccessibilityNodeInfo selfPlayNode = AccessibilityUtil.findNodeInfo(
                service, NODE_ID_SELF_PLAY, "");
        if (selfPlayNode != null) {
            AccessibilityUtil.performClick(selfPlayNode);
        }
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AccessibilityNodeInfo continueGameNode = AccessibilityUtil.findNodeInfo(
                service, NODE_ID_CONTINUE_GAME, NODE_TEXT_CONTINUE_GAME);
        if (continueGameNode != null) {
            AccessibilityUtil.performClick(continueGameNode);
        }
        while (!AccessibilityUtil.findIsContainText(service, "100%")
                || !AccessibilityUtil.findIsContainText(service, "启动完成"));
//        AccessibilityNodeInfo enterGameNode = null;
//        while (enterGameNode == null && !AccessibilityUtil.findIsContainText(service, NODE_TEXT_ENTER_GAME_NORMAL)) {
//            AccessibilityUtil.logAllChildNodesText(service, 0);
//            enterGameNode = AccessibilityUtil.findNodeInfo(
//                    service, NODE_ID_ENTER_GAME_VIP, NODE_TEXT_ENTER_GAME_VIP);
//        }
        Log.e("QT", "openTime:" + (System.currentTimeMillis() - startTime));
        service.mOpenTime.add(System.currentTimeMillis() - startTime);
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
        AccessibilityNodeInfo confirmQuitNode = AccessibilityUtil.findNodeInfo(
                service, NODE_ID_CONFIRM_QUIT, NODE_TEXT_CONFIRM_QUIT);
        if (confirmQuitNode != null) {
            AccessibilityUtil.performClick(confirmQuitNode);
        }
        long quitTime = System.currentTimeMillis();
        AccessibilityNodeInfo gameViewNode = null;
        boolean isCancelNode = false;
        while (gameViewNode == null) {
            gameViewNode = AccessibilityUtil.findNodeInfo(
                    service, NODE_ID_INSTANT_PLAY, NODE_TEXT_INSTANT_PLAY);
            if (gameViewNode == null) {
                gameViewNode = AccessibilityUtil.findNodeInfo(
                        service, NODE_ID_CANCEL_AFTER_QUIT_GAME, NODE_TEXT_CANCEL_AFTER_QUIT_GAME);
                if (gameViewNode != null) isCancelNode = true;
            }
        }
        service.mQuitTimes.add(System.currentTimeMillis() - quitTime);
        if (isCancelNode) AccessibilityUtil.performClick(gameViewNode);
        isClickInstantPlay = false;
        mCurrentMonitorNum++;
    }

    @Override
    public int getCurrentMonitorNum() {
        return mCurrentMonitorNum;
    }
}
