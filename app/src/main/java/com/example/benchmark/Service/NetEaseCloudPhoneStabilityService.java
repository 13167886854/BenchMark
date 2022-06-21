package com.example.benchmark.Service;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK;

import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;

import com.example.benchmark.utils.AccessibilityUtil;

import java.util.List;

public class NetEaseCloudPhoneStabilityService implements IStabilityService {

    private final String NODE_TEXT_MORE_GAME = "更多游戏";
    private final String NODE_TEXT_PHONE_GAME = "手机游戏";
    private final String NODE_TEXT_CLOUD_PHONE = "云手机";
    private final String NODE_TEXT_INSTANT_PLAY = "秒玩";
    private final String NODE_ID_LOADING_GAME = "com.netease.android.cloudgame:id/gaming_reconnect_text";
    private final String NODE_TEXT_LOADING_GAME = "游戏加载中，请稍等";
    private final String NODE_ID_CLOUD_PHONE_INTRODUCTION_BTN = "com.netease.android.cloudgame:id/ok_btn";
    private final String NODE_TEXT_CLOUD_PHONE_INTRODUCTION_BTN = "知道了";
    private final String NODE_ID_STILL_QUIT = "com.netease.android.cloudgame:id/quit_btn";
    private final String NODE_TEXT_STILL_QUIT = "仍要退出";
    private final String NODE_ID_CANCEL_STILL_QUIT = "com.netease.android.cloudgame:id/cancel_btn";
    private final String NODE_ID_QUIT_PHONE = "com.netease.android.cloudgame:id/dialog_sure";
    private final String NODE_TEXT_QUIT_PHONE = "退出";

    private final StabilityMonitorService service;

    private int mCurrentMonitorNum = 0;

    private boolean isFirstMonitor = true;
    private boolean isClickPhoneGame = false;
    private boolean isClickInstantPlay = false;

    private long mStartTime;

    public NetEaseCloudPhoneStabilityService(StabilityMonitorService service) {
        this.service = service;
    }

    @Override
    public void onMonitor() {
        if (!isClickPhoneGame) {
            if (!isFirstMonitor) {
                AccessibilityNodeInfo moreGame = AccessibilityUtil.findNodeInfoByText(
                        service, "android.widget.TextView", NODE_TEXT_MORE_GAME);
                if (moreGame == null) return;
                AccessibilityUtil.performClick(moreGame);
                isFirstMonitor = false;
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            AccessibilityNodeInfo phoneGame = AccessibilityUtil.findNodeInfoByText(
                    service, "android.widget.TextView", NODE_TEXT_MORE_GAME);
            if (phoneGame == null) return;
            AccessibilityUtil.performClick(phoneGame);
            isClickPhoneGame = true;
        }
        if (!isClickInstantPlay) {
            clickInstantPlay();
        }
    }

    private void clickInstantPlay() {
        AccessibilityNodeInfo viewPager = AccessibilityUtil.findNodeInfo(service, "com.netease.android.cloudgame:id/view_pager", "");
        if (viewPager == null) return;
        for (int i = 0; i < viewPager.getChildCount(); i++) {
            AccessibilityNodeInfo recyclerView = viewPager.getChild(i);
            for (int j = 0; j < recyclerView.getChildCount(); j++) {
                AccessibilityNodeInfo frameLayout = recyclerView.getChild(j);
                if (!"android.widget.FrameLayout".equals(frameLayout.getClassName().toString()))
                    continue;
                boolean isCloudPhone = false;
                for (int k = 0; k < frameLayout.getChildCount(); k++) {
                    AccessibilityNodeInfo node = frameLayout.getChild(k);
                    if (node.getText() != null && NODE_TEXT_CLOUD_PHONE.equals(node.getText().toString())) {
                        isCloudPhone = true;
                    }
                    if (isCloudPhone &&
                            "android.widget.FrameLayout".equals(node.getClassName().toString())) {
                        AccessibilityUtil.performClick(node.getChild(0));
                        isClickInstantPlay = true;
                        mStartTime = System.currentTimeMillis();
                        startControlCloudPhone();
                        startQuitCloudPhone();
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void startControlCloudPhone() {
        AccessibilityNodeInfo loadGameNode = AccessibilityUtil.findNodeInfo(service,
                NODE_ID_LOADING_GAME, NODE_TEXT_LOADING_GAME);
        while (loadGameNode == null) {
            loadGameNode = AccessibilityUtil
                    .findNodeInfo(service, NODE_ID_LOADING_GAME, NODE_TEXT_LOADING_GAME);
        }
        while (loadGameNode != null) {
            loadGameNode = AccessibilityUtil
                    .findNodeInfo(service, NODE_ID_LOADING_GAME, NODE_TEXT_LOADING_GAME);
        }
        Log.e("QT", "openTime:"+(System.currentTimeMillis() - mStartTime));
        service.mOpenTime.add(System.currentTimeMillis() - mStartTime);
        //TODO 此处检测黑屏
    }

    @Override
    public void startQuitCloudPhone() {
        service.performGlobalAction(GLOBAL_ACTION_BACK);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AccessibilityNodeInfo nodeBtnQuit = AccessibilityUtil.findNodeInfo(service,
                NODE_ID_STILL_QUIT, NODE_TEXT_STILL_QUIT);
        while (nodeBtnQuit == null) {
            nodeBtnQuit = AccessibilityUtil.findNodeInfo(service,
                    NODE_ID_STILL_QUIT, NODE_TEXT_STILL_QUIT);
        }
        AccessibilityUtil.performClick(nodeBtnQuit);
        long quitTime = System.currentTimeMillis();
        AccessibilityNodeInfo phoneGame = AccessibilityUtil.findNodeInfoByText(
                service, "android.widget.TextView", NODE_TEXT_PHONE_GAME);
        while (phoneGame == null) {
            phoneGame = AccessibilityUtil.findNodeInfoByText(
                    service, "android.widget.TextView", NODE_TEXT_PHONE_GAME);
        }
        service.mQuitTimes.add(System.currentTimeMillis() - quitTime);
        mCurrentMonitorNum++;
        isClickPhoneGame = false;
        isClickInstantPlay = false;
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public float getStartSuccessRate() {
        return mCurrentMonitorNum / (float) mWholeMonitorNum * 100;
    }

    @Override
    public boolean isFinished() {
        return mCurrentMonitorNum == mWholeMonitorNum;
    }
}
