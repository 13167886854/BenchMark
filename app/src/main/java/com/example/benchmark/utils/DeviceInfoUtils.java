/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

/**
 * DeviceInfoUtils
 *
 * @version 1.0
 * @since 2023/3/7 17:24
 */
public class DeviceInfoUtils {
    private static final String TAG = "DeviceInfoUtils";

    /**
     * getCpuNumCores
     *
     * @return int
     * @date 2023/3/8 09:33
     */
    public static int getCpuNumCores() {
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File file) {
                if (Pattern.matches("cpu[0-9]", file.getName())) {
                    return true;
                }
                return false;
            }
        }
        // Get directory containing CPU info
        File dir = new File(File.separator + "sys"
                + File.separator + "devices" + File.separator
                + "system" + File.separator + "cpu" + File.separator);

        // Filter to only list the devices we care about
        File[] files = dir.listFiles(new CpuFilter());

        // Return the number of cores (virtual CPU devices)
        return files.length;
    }
}
