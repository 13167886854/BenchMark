package com.example.benchmark.Service;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK;

import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.benchmark.utils.AccessibilityCallback;
import com.example.benchmark.utils.AccessibilityUtil;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;

public class ECloudPhoneStabilityService implements IStabilityService{

    private final int screenHeight = CacheUtil.getInt(CacheConst.KEY_SCREEN_HEIGHT);
    private final int screenWidth = CacheUtil.getInt(CacheConst.KEY_SCREEN_WIDTH);
    private final String NODE_ID_CLICK_VIEW = "com.chinamobile.cmss.saas.cloundphone:id/main_layout";
    private final String NODE_ID_QUIT_PHONE = "com.chinamobile.cmss.saas.cloundphone:id/netwrok_ok";
    private final String NODE_TEXT_QUIT_PHONE = "确认";
    private final String NODE_ID_NO_NOTICE = "com.chinamobile.cmss.saas.cloundphone:id/netwrok_check";

    private final StabilityMonitorService service;

    private int mCurrentMonitorNum = 0;
    private long mQuitTime = 0L;

    private boolean isTapping = false;
    private boolean isClickQuitNotice = false;

    public ECloudPhoneStabilityService(StabilityMonitorService service) {
        this.service = service;
    }

    @Override
    public void onMonitor() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || isFinished()) return;
        isTapping = false;
        while (!isTapping) {
            if (AccessibilityUtil.findNodeInfo(service, NODE_ID_CLICK_VIEW, "") == null
                    || isTapping) continue;
//            Log.e("qt", "===TAP TAP===");
            isTapping = true;
            service.startCaptureScreen();
            AccessibilityUtil.tap(service, screenWidth / 2, screenHeight / 2,
                    new AccessibilityCallback() {
                        @Override
                        public void onSuccess() {
                            Log.e("qt", "TAP SUCCESS");
                            startControlCloudPhone();
                            startQuitCloudPhone();
                            mCurrentMonitorNum++;
                            isTapping = false;
                        }
                        @Override
                        public void onFailure() {
//                            Log.e("qt", "TAP FAILURE");
                            isTapping = false;
                        }
                    });
        }
    }

    @Override
    public void startControlCloudPhone() {
        long mStartTime = System.currentTimeMillis();
        try {
            // wait cloud phone loading
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        service.mStartTimes.add(mStartTime);
    }

    @Override
    public void startQuitCloudPhone() {
        // 双击返回键退出云手机
        service.performGlobalAction(GLOBAL_ACTION_BACK);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        service.performGlobalAction(GLOBAL_ACTION_BACK);
        mQuitTime = System.currentTimeMillis();
        closeDialogIfExistWhenQuit();
        AccessibilityNodeInfo nodeClickView = AccessibilityUtil.findNodeInfo(
                service, NODE_ID_CLICK_VIEW, "");
        while (nodeClickView == null) {
            nodeClickView = AccessibilityUtil.findNodeInfo(
                    service, NODE_ID_CLICK_VIEW, "");
        }
        service.mQuitTimes.add(System.currentTimeMillis() - mQuitTime);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void closeDialogIfExistWhenQuit() {
        if (isClickQuitNotice) return;
        new Thread(() -> {
            try {
                Thread.sleep(1000L);
                AccessibilityNodeInfo nodeBtnQuit = AccessibilityUtil.findNodeInfo(service,
                        NODE_ID_QUIT_PHONE, NODE_TEXT_QUIT_PHONE);
                if (nodeBtnQuit != null) {
                    AccessibilityNodeInfo noNotionNode = AccessibilityUtil.findNodeInfo(service,
                            NODE_ID_NO_NOTICE, "");
                    if (noNotionNode != null) AccessibilityUtil.performClick(noNotionNode);
                    AccessibilityUtil.performClick(nodeBtnQuit);
                    mQuitTime = System.currentTimeMillis();
                }
                isClickQuitNotice = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
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
