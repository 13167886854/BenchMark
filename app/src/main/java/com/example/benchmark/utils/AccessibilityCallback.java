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
     * @return void
     * @throws null
     * @description: onSuccess
     * @date 2023/3/2 10:03
     */
    void onSuccess();

    /**
     * @return void
     * @throws null
     * @description: onFailure
     * @date 2023/3/2 10:03
     */
    void onFailure();
}
