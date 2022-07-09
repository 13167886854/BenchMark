package com.example.benchmark.utils;

import android.view.accessibility.AccessibilityNodeInfo;

public interface AccessibilityClassFindCallback {
    boolean caterTo(AccessibilityNodeInfo nodeInfo);
}
