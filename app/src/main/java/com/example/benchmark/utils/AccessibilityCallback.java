/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

/**
 * AccessibilityCallback
 *
 * @version 1.0
 * @since 2023/3/7 17:22
 */
public interface AccessibilityCallback {
    /**
     * onSuccess
     *
     * @return void
     * @date 2023/3/10 16:26
     */
    void onSuccess();

    /**
     * onFailure
     *
     * @return void
     * @date 2023/3/10 16:26
     */
    void onFailure();
}
