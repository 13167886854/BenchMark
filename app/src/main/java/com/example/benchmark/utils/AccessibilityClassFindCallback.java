/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

import android.view.accessibility.AccessibilityNodeInfo;

/**
 * AccessibilityClassFindCallback
 *
 * @version 1.0
 * @since 2023/3/7 17:22
 */
public interface AccessibilityClassFindCallback {

    /**
     * caterTo
     *
     * @param nodeInfo nodeInfo
     * @return boolean
     * @throws null
     * @date 2023/3/8 08:44
     */
    boolean caterTo(AccessibilityNodeInfo nodeInfo);
}
