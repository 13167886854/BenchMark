/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.service;

import android.util.Log;

import com.example.benchmark.utils.TapUtil;

/**
 * DefaultStabilityService
 *
 * @version 1.0
 * @since 2023/3/7 17:20
 */
public class DefaultStabilityService implements IStabilityService {
    @Override
    public void onMonitor() {
        Log.e("DefaultStabilityService", "onMonitor");
    }

    @Override
    public void startControlCloudPhone() {
        Log.e("DefaultStabilityService", "startControlCloudPhone");
    }

    @Override
    public void startQuitCloudPhone() {
        Log.e("DefaultStabilityService", "startQuitCloudPhone");
    }

    @Override
    public int getCurrentMonitorNum() {
        return TapUtil.getUtil().getmWholeMonitorNum();
    }
}
