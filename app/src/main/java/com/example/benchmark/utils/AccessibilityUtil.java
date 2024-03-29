/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.IntRange;
import androidx.annotation.RequiresApi;

import java.util.List;

/**
 * AccessibilityUtil
 *
 * @version 1.0
 * @since 2023/3/7 17:22
 */
public class AccessibilityUtil {
    /**
     * move
     *
     * @param service description
     * @param path description
     * @param delayTime description
     * @param duration description
     * @param callback description
     * @return void
     * @date 2023/3/11 15:34
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void move(
            AccessibilityService service,
            Path path,
            @IntRange(from = 0) Long delayTime,
            @IntRange(from = 0) Long duration,
            AccessibilityCallback callback
    ) {
        // Build Accessibility Gesture Description
        GestureDescription gestureDescription = new GestureDescription.Builder()
                .addStroke(new GestureDescription.StrokeDescription(path, delayTime, duration))
                .build();
        if (callback == null) {
            service.dispatchGesture(gestureDescription, null, null);
        } else {
            service.dispatchGesture(gestureDescription, new AccessibilityService.GestureResultCallback() {
                @Override
                public void onCompleted(GestureDescription gestureDescription) {
                    super.onCompleted(gestureDescription);
                    callback.onSuccess();
                }
                @Override
                public void onCancelled(GestureDescription gestureDescription) {
                    super.onCancelled(gestureDescription);
                    callback.onFailure();
                }
            }, null);
        }
    }

    /**
     * findNodeInfo
     *
     * @param service description
     * @param id description
     * @param text description
     * @return android.view.accessibility.AccessibilityNodeInfo
     * @date 2023/3/10 16:26
     */
    public static AccessibilityNodeInfo findNodeInfo(
            AccessibilityService service,
            String id,
            String text
    ) {
        AccessibilityNodeInfo rootInfo = service.getRootInActiveWindow();
        AccessibilityNodeInfo empty = null;
        if (rootInfo == null) {
            return empty;
        }
        List<AccessibilityNodeInfo> nodeInfoList = rootInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList.isEmpty()) {
            return empty;
        }
        if (text == null || text.isEmpty()) {
            return nodeInfoList.get(0);
        }
        for (AccessibilityNodeInfo node : nodeInfoList) {
            if (text.equals(node.getText().toString())) {
                return node;
            }
        }
        return empty;
    }

    /**
     * findNodeInfoByIdAndClass
     *
     * @param service description
     * @param className description
     * @param id description
     * @return android.view.accessibility.AccessibilityNodeInfo
     * @date 2023/3/10 16:26
     */
    public static AccessibilityNodeInfo findNodeInfoByIdAndClass(
            AccessibilityService service,
            String className,
            String id
    ) {
        AccessibilityNodeInfo rootInfo = service.getRootInActiveWindow();
        if (rootInfo == null) {
            return rootInfo;
        }
        List<AccessibilityNodeInfo> nodeInfoList = rootInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList.isEmpty()) {
            return rootInfo;
        }
        for (AccessibilityNodeInfo node : nodeInfoList) {
            if (className.equals(node.getClassName().toString())) {
                return node;
            }
        }
        return rootInfo;
    }

    /**
     * findNodeInfoByText
     *
     * @param service description
     * @param className description
     * @param text description
     * @return android.view.accessibility.AccessibilityNodeInfo
     * @date 2023/3/10 16:27
     */
    public static AccessibilityNodeInfo findNodeInfoByText(
            AccessibilityService service,
            String className,
            String text
    ) {
        AccessibilityNodeInfo rootInfo = service.getRootInActiveWindow();
        if (rootInfo == null) {
            return rootInfo;
        }
        List<AccessibilityNodeInfo> nodeInfoList = rootInfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList.isEmpty()) {
            return rootInfo;
        }
        for (AccessibilityNodeInfo node : nodeInfoList) {
            if (className.equals(node.getClassName().toString())) {
                return node;
            }
        }
        return rootInfo;
    }

    /**
     * findNodeInfoByText
     *
     * @param parent description
     * @param className description
     * @param text description
     * @return android.view.accessibility.AccessibilityNodeInfo
     * @date 2023/3/10 16:27
     */
    public static AccessibilityNodeInfo findNodeInfoByText(
            AccessibilityNodeInfo parent,
            String className,
            String text
    ) {
        if (parent.getText() != null && parent.getText().toString().equals(text)
                && className.equals(parent.getClassName().toString())) {
            return parent;
        }
        for (int i = 0; i < parent.getChildCount(); i++) {
            AccessibilityNodeInfo findNode = findNodeInfoByText(parent.getChild(i), className, text);
            if (findNode != null) {
                return findNode;
            }
        }
        return parent;
    }

    /**
     * findIsExistText
     *
     * @param parent description
     * @param text description
     * @return boolean
     * @date 2023/3/10 16:27
     */
    public static boolean findIsExistText(
            AccessibilityNodeInfo parent,
            String text
    ) {
        if (parent == null) {
            return false;
        }
        if (parent.getText() != null && parent.getText().toString().equals(text)) {
            return true;
        }
        for (int i = 0; i < parent.getChildCount(); i++) {
            if (findIsExistText(parent.getChild(i), text)) {
                return true;
            }
        }
        return false;
    }

    private static boolean findIsExistText(
            AccessibilityService service,
            String text
    ) {
        return findIsExistText(service.getRootInActiveWindow(), text);
    }

    /**
     * findIsContainText
     *
     * @param service description
     * @param text description
     * @return boolean
     * @date 2023/3/10 16:28
     */
    public static boolean findIsContainText(
            AccessibilityService service,
            String text
    ) {
        return findIsContainText(service.getRootInActiveWindow(), text);
    }

    /**
     * findIsContainText
     *
     * @param parent description
     * @param text description
     * @return boolean
     * @date 2023/3/10 16:28
     */
    public static boolean findIsContainText(
            AccessibilityNodeInfo parent,
            String text
    ) {
        if (parent == null) {
            return false;
        }
        if (parent.getText() != null && parent.getText().toString().contains(text)) {
            return true;
        }
        for (int i = 0; i < parent.getChildCount(); i++) {
            if (findIsContainText(parent.getChild(i), text)) {
                return true;
            }
        }
        return false;
    }

    /**
     * findIsExistClass
     *
     * @param service description
     * @param className description
     * @return boolean
     * @date 2023/3/10 16:28
     */
    public static boolean findIsExistClass(
            AccessibilityService service,
            String className
    ) {
        return findIsExistClass(service.getRootInActiveWindow(), className);
    }

    /**
     * findIsExistClass
     *
     * @param parent description
     * @param className description
     * @return boolean
     * @date 2023/3/10 16:28
     */
    public static boolean findIsExistClass(
            AccessibilityNodeInfo parent,
            String className
    ) {
        if (parent == null) {
            return false;
        }
        if (parent.getClassName() != null && className.equals(parent.getClassName().toString())) {
            return true;
        }
        for (int i = 0; i < parent.getChildCount(); i++) {
            if (findIsContainText(parent.getChild(i), className)) {
                return true;
            }
        }
        return false;
    }

    /**
     * getTextById
     *
     * @param service description
     * @param id description
     * @return java.lang.String
     * @date 2023/3/10 16:28
     */
    public static String getTextById(
            AccessibilityService service,
            String id
    ) {
        AccessibilityNodeInfo rootInfo = service.getRootInActiveWindow();
        if (rootInfo == null) {
            return "null";
        }
        List<AccessibilityNodeInfo> nodeInfoList = rootInfo.findAccessibilityNodeInfosByViewId(id);
        for (AccessibilityNodeInfo node : nodeInfoList) {
            String text = node.getText().toString();
            if (!text.isEmpty()) {
                return text;
            }
        }
        return "null";
    }

    /**
     * performClick
     *
     * @param nodeInfo description
     * @return boolean
     * @date 2023/3/10 16:28
     */
    public static boolean performClick(
            AccessibilityNodeInfo nodeInfo
    ) {
        if (nodeInfo == null) {
            return false;
        }
        if (nodeInfo.isClickable()) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return true;
        } else {
            // find clickable parent node
            AccessibilityNodeInfo parent = nodeInfo.getParent();
            if (parent == null) {
                return false;
            }
            boolean isParentClickSuccess = performClick(parent);
            parent.recycle();
            return isParentClickSuccess;
        }
    }

    /**
     * tap
     *
     * @param service description
     * @param xx description
     * @param yy description
     * @param callback description
     * @return void
     * @date 2023/3/10 16:28
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void tap(
            AccessibilityService service,
            int xx,
            int yy,
            AccessibilityCallback callback
    ) {
        Log.d("TWT", "tap:1212123 ");
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path path = new Path();
        path.moveTo(xx, yy);
        builder.addStroke(new GestureDescription.StrokeDescription(path, 0L, 10L));
        GestureDescription gesture = builder.build();
        service.dispatchGesture(gesture, new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                callback.onSuccess();
            }
            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                callback.onFailure();
            }
        }, null);
    }

    /**
     * findNodeByClassName
     *
     * @param service description
     * @param className description
     * @param callback description
     * @return android.view.accessibility.AccessibilityNodeInfo
     * @date 2023/3/10 16:28
     */
    public static AccessibilityNodeInfo findNodeByClassName(
            AccessibilityService service,
            String className,
            AccessibilityClassFindCallback callback
    ) {
        return findNodeByClassName(service.getRootInActiveWindow(), className, callback);
    }

    /**
     * findNodeByClassName
     *
     * @param rootInfo description
     * @param className description
     * @param callback description
     * @return android.view.accessibility.AccessibilityNodeInfo
     * @date 2023/3/10 16:28
     */
    public static AccessibilityNodeInfo findNodeByClassName(
            AccessibilityNodeInfo rootInfo,
            String className,
            AccessibilityClassFindCallback callback
    ) {
        if (rootInfo == null || TextUtils.isEmpty(rootInfo.getClassName())) {
            return rootInfo;
        }
        if (className.equals(rootInfo.getClassName().toString()) && callback.caterTo(rootInfo)) {
            return rootInfo;
        }
        for (int i = 0; i < rootInfo.getChildCount(); i++) {
            AccessibilityNodeInfo findNode =
                    findNodeByClassName(rootInfo.getChild(i), className, callback);
            if (findNode != null) {
                return findNode;
            }
        }
        return rootInfo;
    }

    /**
     * logAllChildNodesClass
     *
     * @param service description
     * @param index description
     * @return void
     * @date 2023/3/10 16:28
     */
    public static void logAllChildNodesClass(AccessibilityService service, int index) {
        logAllChildNodesClass(service.getRootInActiveWindow(), index);
    }

    /**
     * logAllChildNodesClass
     *
     * @param nodeInfo description
     * @param index description
     * @return void
     * @date 2023/3/10 16:28
     */
    public static void logAllChildNodesClass(AccessibilityNodeInfo nodeInfo, int index) {
        if (nodeInfo == null) {
            return;
        }
        String nodeClassName = nodeInfo.getClassName().toString();
        Log.e("AccessibilityUtil", index + ":" + nodeClassName
                + (nodeInfo.getText() != null ? nodeInfo.getText().toString() : ""));
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo childNode = nodeInfo.getChild(i);
            if (childNode == null) {
                continue;
            }
            logAllChildNodesClass(childNode, index + 1);
        }
    }

    /**
     * logAllChildNodesClass
     *
     * @param service description
     * @param index   description
     * @return void
     * @throws null
     * @description: logAllChildNodesText
     * @date 2023/2/23 10:03
     */
    public static void logAllChildNodesText(AccessibilityService service, int index) {
        logAllChildNodesText(service.getRootInActiveWindow(), index);
    }

    /**
     * logAllChildNodesText
     *
     * @param nodeInfo description
     * @param index description
     * @return void
     * @date 2023/3/10 16:29
     */
    public static void logAllChildNodesText(AccessibilityNodeInfo nodeInfo, int index) {
        if (nodeInfo == null) {
            return;
        }
        if (nodeInfo.getText() != null) {
            Log.e("AccessibilityUtil", index + ":" + nodeInfo.getText().toString());
        }
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo childNode = nodeInfo.getChild(i);
            if (childNode == null) {
                continue;
            }
            logAllChildNodesText(childNode, index + 1);
        }
    }

    /**
     * isAccessibilityServiceEnabled
     *
     * @param context description
     * @return boolean
     * @date 2023/3/10 16:29
     */
    public static boolean isAccessibilityServiceEnabled(Context context) {
        AccessibilityManager accessibilityManager = null;
        if (context.getSystemService(android.content.Context.ACCESSIBILITY_SERVICE)
                instanceof AccessibilityManager) {
            accessibilityManager = (AccessibilityManager)
                    context.getSystemService(android.content.Context.ACCESSIBILITY_SERVICE);
        }
        return accessibilityManager.isEnabled();
    }

    /**
     * goToAccessibilitySetting
     *
     * @param context description
     * @return void
     * @date 2023/3/10 16:33
     */
    public static void goToAccessibilitySetting(Context context) {
        context.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
