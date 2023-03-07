/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.service;

import com.example.benchmark.utils.TapUtil;

/**
 * IStabilityService
 *
 * @version 1.0
 * @since 2023/3/7 17:21
 */
public interface IStabilityService {
    void onMonitor();

    void startControlCloudPhone();

    void startQuitCloudPhone();

    int getCurrentMonitorNum();

    default float getStartSuccessRate() {
        return getCurrentMonitorNum() / (float) TapUtil.mWholeMonitorNum * 100;
    }

    default boolean isFinished() {
        return getCurrentMonitorNum() == TapUtil.mWholeMonitorNum;
    }
}
