package com.example.benchmark.Service;

import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.benchmark.utils.AccessibilityCallback;
import com.example.benchmark.utils.AccessibilityUtil;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;
import com.example.benchmark.utils.StatusBarUtil;
import com.example.benchmark.utils.TapUtil;

public class HuaweiCloudPhoneStabilityService implements IStabilityService {

    private final int screenHeight = CacheUtil.getInt(CacheConst.KEY_SCREEN_HEIGHT);
    private final int screenWidth = CacheUtil.getInt(CacheConst.KEY_SCREEN_WIDTH);
    private final String NODE_ID_BTN_START_CONNECT = "com.huawei.instructionstream.appui:id/btn_startGame";
    private final String NODE_ID_QUIT_PHONE = "com.huawei.instructionstream.appui:id/rotate_exit";
    private final String NODE_TEXT_QUIT_PHONE = "退出云手机";
    private final String NODE_ID_CONNECT_SUCCESS_VIEW = "com.huawei.instructionstream.appui:id/total_view";
    private final String NODE_ID_CONNECT_FAIL_EXIT = "android:id/button1";
    private final String NODE_TEXT_CONNECT_FAIL_EXIT = "退出云手机";
    private final String NODE_ID_RECONNECT = "android:id/button2";
    private final String NODE_TEXT_RECONNECT = "重连云手机";

    private final MyAccessibilityService service;

    private int mCurrentMonitorNum = 0;
    private int mFailMonitorNum = 0;

    private long mStartTime = 0L;
    private long mQuitTime = 0L;

    private boolean isConnectSuccess = false;
    private boolean isTapping = false;
    private boolean isClickStartConnectBtn = false;

    public HuaweiCloudPhoneStabilityService(MyAccessibilityService service) {
        this.service = service;
    }

    @Override
    public void onMonitor() {
        if (isFinished()) return;
        if (!isClickStartConnectBtn) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            AccessibilityNodeInfo nodeBtnStartGame = AccessibilityUtil.findNodeInfo(
                    service, NODE_ID_BTN_START_CONNECT, "");
            if (nodeBtnStartGame != null) {
                AccessibilityUtil.performClick(nodeBtnStartGame);
                isClickStartConnectBtn = true;
                startControlCloudPhone();
                mCurrentMonitorNum++;
                isClickStartConnectBtn = false;
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void startControlCloudPhone() {
        mStartTime = System.currentTimeMillis();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || isFinished()) return;
        isConnectSuccess = false;
        long startTime = System.currentTimeMillis();
        while (!isConnectSuccess) {
            AccessibilityNodeInfo nodeConnectFail = AccessibilityUtil.findNodeInfo(
                    service, NODE_ID_CONNECT_FAIL_EXIT, NODE_TEXT_CONNECT_FAIL_EXIT);
            if (nodeConnectFail != null) {
                mFailMonitorNum++;
                AccessibilityUtil.performClick(nodeConnectFail);
                break;
            }
            AccessibilityNodeInfo nodeBtnQuit = AccessibilityUtil.findNodeInfo(
                    service, NODE_ID_QUIT_PHONE, NODE_TEXT_QUIT_PHONE);
            if (nodeBtnQuit != null) {
                service.mOpenTime.add(System.currentTimeMillis() - mStartTime);
                AccessibilityUtil.performClick(nodeBtnQuit);
                mQuitTime = System.currentTimeMillis();
                startQuitCloudPhone();
                isConnectSuccess = true;
            }
            if (System.currentTimeMillis() - startTime < 800L) continue;
            startTime = System.currentTimeMillis();
            AccessibilityUtil.tap(
                    service,
                    screenWidth - 25,
                    screenHeight / 2 + StatusBarUtil.getStatusBarHeight(service) - 25,
                    new AccessibilityCallback() {
                        @Override
                        public void onSuccess() {
                            Log.e("qt", "TAP SUCCESS");
                        }

                        @Override
                        public void onFailure() {
                        }
                    });
        }
    }

    @Override
    public void startQuitCloudPhone() {
        Log.e("QT", "startQuitCloudPhone");
        AccessibilityNodeInfo nodeBtnStartGame = AccessibilityUtil.findNodeInfo(
                service, NODE_ID_BTN_START_CONNECT, "");
        while (nodeBtnStartGame == null) nodeBtnStartGame = AccessibilityUtil
                .findNodeInfo(service, NODE_ID_BTN_START_CONNECT, "");
        service.mQuitTimes.add(System.currentTimeMillis() - mQuitTime);
    }

    @Override
    public float getStartSuccessRate() {
        return (mCurrentMonitorNum - mFailMonitorNum) / (float) TapUtil.mWholeMonitorNum * 100;
    }

    @Override
    public int getCurrentMonitorNum() {
        return mCurrentMonitorNum;
    }
}
