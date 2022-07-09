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

public class AccessibilityUtil {

    /**
     * Accessibility move function
     *
     * @param service   AccessibilityService instance
     * @param path      moving path
     * @param delayTime delayed time to start
     * @param duration  execution duration
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
     * find node info through Accessibility
     *
     * @param service AccessibilityService instance
     * @param id      view id
     * @param text    view text
     * @return target node, but no such node if it is null.
     */
    public static AccessibilityNodeInfo findNodeInfo(
            AccessibilityService service,
            String id,
            String text
    ) {
        AccessibilityNodeInfo rootInfo = service.getRootInActiveWindow();
        if (rootInfo == null) return null;
        List<AccessibilityNodeInfo> nodeInfoList = rootInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList.isEmpty()) return null;
        if (text == null || text.isEmpty()) return nodeInfoList.get(0);
        for (AccessibilityNodeInfo node : nodeInfoList) {
            if (text.equals(node.getText().toString()))
                return node;
        }
        return null;
    }

    public static AccessibilityNodeInfo findNodeInfoByIdAndClass(
            AccessibilityService service,
            String className,
            String id
    ) {
        AccessibilityNodeInfo rootInfo = service.getRootInActiveWindow();
        if (rootInfo == null) return null;
        List<AccessibilityNodeInfo> nodeInfoList = rootInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList.isEmpty()) return null;
        for (AccessibilityNodeInfo node : nodeInfoList) {
            if (className.equals(node.getClassName().toString()))
                return node;
        }
        return null;
    }

    public static AccessibilityNodeInfo findNodeInfoByText(
            AccessibilityService service,
            String className,
            String text
    ) {
        AccessibilityNodeInfo rootInfo = service.getRootInActiveWindow();
        if (rootInfo == null) return null;
        List<AccessibilityNodeInfo> nodeInfoList = rootInfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList.isEmpty()) return null;
        for (AccessibilityNodeInfo node : nodeInfoList) {
            if (className.equals(node.getClassName().toString()))
                return node;
        }
        return null;
    }

    public static AccessibilityNodeInfo findNodeInfoByText(
            AccessibilityNodeInfo parent,
            String className,
            String text
    ) {
        if (parent == null) return null;
        if (parent.getText() != null && parent.getText().toString().equals(text)
                && className.equals(parent.getClassName().toString())) return parent;
        for (int i = 0; i < parent.getChildCount(); i++) {
            AccessibilityNodeInfo findNode = findNodeInfoByText(parent.getChild(i), className, text);
            if (findNode != null) return findNode;
        }
        return null;
    }

    public static boolean findIsExistText(
            AccessibilityNodeInfo parent,
            String text
    ) {
        if (parent == null) return false;
        if (parent.getText() != null && parent.getText().toString().equals(text)) {
            return true;
        }
        for (int i = 0; i < parent.getChildCount(); i++) {
            if (findIsExistText(parent.getChild(i), text))
                return true;
        }
        return false;
    }

    public static boolean findIsExistText(
            AccessibilityService service,
            String text
    ) {
        return findIsExistText(service.getRootInActiveWindow(), text);
    }

    public static boolean findIsContainText(
            AccessibilityService service,
            String text
    ) {
        return findIsContainText(service.getRootInActiveWindow(), text);
    }

    public static boolean findIsContainText(
            AccessibilityNodeInfo parent,
            String text
    ) {
        if (parent == null) return false;
        if (parent.getText() != null && parent.getText().toString().contains(text)) {
            return true;
        }
        for (int i = 0; i < parent.getChildCount(); i++) {
            if (findIsContainText(parent.getChild(i), text))
                return true;
        }
        return false;
    }

    /**
     * Get view's text
     */
    public static String getTextById(
            AccessibilityService service,
            String id
    ) {
        AccessibilityNodeInfo rootInfo = service.getRootInActiveWindow();
        if (rootInfo == null) return null;
        List<AccessibilityNodeInfo> nodeInfoList = rootInfo.findAccessibilityNodeInfosByViewId(id);
        for (AccessibilityNodeInfo node : nodeInfoList) {
            String text = node.getText().toString();
            if (!text.isEmpty()) return text;
        }
        return null;
    }

    /**
     * click node action
     */
    public static boolean performClick(
            AccessibilityNodeInfo nodeInfo
    ) {
        if (nodeInfo == null) return false;
        if (nodeInfo.isClickable()) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return true;
        } else {
            // find clickable parent node
            AccessibilityNodeInfo parent = nodeInfo.getParent();
            if (parent == null) return false;
            boolean isParentClickSuccess = performClick(parent);
            parent.recycle();
            return isParentClickSuccess;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void tap(
            AccessibilityService service,
            int x,
            int y,
            AccessibilityCallback callback
    ) {
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path p = new Path();
        p.moveTo(x, y);
        builder.addStroke(new GestureDescription.StrokeDescription(p, 0L, 500L));
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

    public static AccessibilityNodeInfo findNodeByClassName(
            AccessibilityService service,
            String className,
            AccessibilityClassFindCallback callback
    ) {
        return findNodeByClassName(service.getRootInActiveWindow(), className, callback);
    }

    public static AccessibilityNodeInfo findNodeByClassName(
            AccessibilityNodeInfo rootInfo,
            String className,
            AccessibilityClassFindCallback callback
    ) {
        if (rootInfo == null || TextUtils.isEmpty(rootInfo.getClassName())) return null;
        if (className.equals(rootInfo.getClassName().toString()) && callback.caterTo(rootInfo)) {
            return rootInfo;
        }
        for (int i = 0; i < rootInfo.getChildCount(); i++) {
            AccessibilityNodeInfo findNode = findNodeByClassName(rootInfo.getChild(i), className, callback);
            if (findNode != null) return findNode;
        }
        return null;
    }

    public static void logAllChildNodesClass(AccessibilityService service, int index) {
        logAllChildNodesClass(service.getRootInActiveWindow(), index);
    }

    public static void logAllChildNodesClass(AccessibilityNodeInfo nodeInfo, int index) {
        if (nodeInfo == null) return;
        Log.e("AccessibilityUtil", index + ":" + nodeInfo.getClassName().toString());
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo childNode = nodeInfo.getChild(i);
            if (childNode == null) continue;
            index++;
            logAllChildNodesClass(childNode, index);
        }
    }

    /**
     * Check whether the accessibility function is enabled
     */
    public static boolean isAccessibilityServiceEnabled(Context context) {
        AccessibilityManager accessibilityManager = (AccessibilityManager)
                context.getSystemService(android.content.Context.ACCESSIBILITY_SERVICE);
        return accessibilityManager.isEnabled();
    }

    /**
     * Go to the Setting page to enable the accessibility when it is disabled.
     */
    public static void goToAccessibilitySetting(Context context) {
        context.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

}
