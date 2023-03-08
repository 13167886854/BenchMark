/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MemInfoUtil
 *
 * @version 1.0
 * @since 2023/3/7 17:25
 */
public class MemInfoUtil {
    // 定义GB的计算常量
    private static final int GB = 1024 * 1024 * 1024;

    // 定义MB的计算常量
    private static final int MB = 1024 * 1024;

    // 定义KB的计算常量
    private static final int KB = 1024;
    private static final String TAG = "MemInfoUtil";

    public static List<String> getMemInfo() {
        List<String> result = new ArrayList<>();

        try {
            String line;
            BufferedReader br = new BufferedReader(
                    new FileReader(File.separator + "proc" + File.separator + "meminfo"));
            while ((line = br.readLine()) != null) {
                result.add(line);
            }
            br.close();
        } catch (IOException e) {
            Log.e(TAG, "getMemInfo: ", e);
        }

        return result;
    }

    public static String getFieldFromMeminfo(String field) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(File.separator + "proc" + File.separator + "meminfo"));
        Pattern pa = Pattern.compile(field + "\\s*:\\s*(.*)");

        try {
            String line;
            while ((line = br.readLine()) != null) {
                Matcher ma = pa.matcher(line);
                if (ma.matches()) {
                    return ma.group(1);
                }
            }
        } finally {
            br.close();
        }

        return "null";
    }

    public static String getMemTotal() {
        String result = null;

        try {
            result = getFieldFromMeminfo("MemTotal");
        } catch (IOException e) {
            Log.e(TAG, "getMemTotal: ", e);
        }

        return result;
    }


    public static String getMemAvailable() {
        String result = null;

        try {
            result = getFieldFromMeminfo("MemAvailable");
        } catch (IOException e) {
            Log.e(TAG, "getMemAvailable: ", e);
        }
        return bytes2kb(Float.parseFloat(result.substring(0, result.length() - 3)) * 1024 + 282 * MB);
    }

    /**
     * 转换单位
     * @param bytes
     * @return
     */
    public static String bytes2kb(BigDecimal bytes) {
        int numGb = bytes.divide(BigDecimal.valueOf(GB)).intValue();
        int numMb = bytes.divide(BigDecimal.valueOf(MB)).intValue();
        int numKb = bytes.divide(BigDecimal.valueOf(KB)).intValue();
        // 格式化小数
        DecimalFormat format = new DecimalFormat("###.00");
        if (numGb >= 1) {
            return format.format(numGb) + " GB";
        } else if (numMb >= 1) {
            return format.format(numMb) + " MB";
        } else {
            if (numKb >= 1) {
                return format.format(numKb) + " KB";
            } else {
                return bytes + " B";
            }
        }
    }
}
