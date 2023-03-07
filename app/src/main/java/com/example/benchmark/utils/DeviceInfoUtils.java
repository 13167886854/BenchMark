/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
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
     * 获取设备宽度（px）
     */
    public static int getDeviceWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取设备高度（px）
     */
    public static int getDeviceHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取厂商名
     **/
    public static String getDeviceManufacturer() {
        return android.os.Build.MANUFACTURER;
    }

    /**
     * 获取产品名
     **/
    public static String getDeviceProduct() {
        return android.os.Build.PRODUCT;
    }

    /**
     * 获取手机品牌
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机型号
     */
    public static String getDeviceModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机主板名
     */
    public static String getDeviceBoard() {
        return android.os.Build.BOARD;
    }

    /**
     * 设备名
     **/
    public static String getDeviceDevice() {
        return android.os.Build.DEVICE;
    }

    /**
     * fingerprit 信息
     **/
    public static String getDeviceFubgerprint() {
        return android.os.Build.FINGERPRINT;
    }

    /**
     * 硬件名
     **/
    public static String getDeviceHardware() {
        return android.os.Build.HARDWARE;
    }

    /**
     * 主机
     **/
    public static String getDeviceHost() {
        return android.os.Build.HOST;
    }

    /**
     * 显示ID
     **/
    public static String getDeviceDisplay() {
        return android.os.Build.DISPLAY;
    }

    /**
     * ID
     **/
    public static String getDeviceId() {
        return android.os.Build.ID;
    }

    /**
     * 获取手机用户名
     **/
    public static String getDeviceUser() {
        return android.os.Build.USER;
    }

    /**
     * 获取手机 硬件序列号
     **/
    public static String getDeviceSerial() {
        return android.os.Build.SERIAL;
    }

    /**
     * 获取手机Android 系统SDK
     *
     * @return
     */
    public static int getDeviceSDK() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 获取手机Android 版本
     *
     * @return
     */
    public static String getDeviceAndroidVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取当前手机系统语言。
     */
    public static String getDeviceDefaultLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取CPU信息
     */
    // 获取CPU名字
    public static String getCpuName() {
        try {
            FileReader fr = new FileReader(File.separator + "proc" + File.separator + "cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            return array[1];
        } catch (IOException e) {
            Log.e(TAG, "getCpuName: ", e);
        }
        return "null";
    }

    // 获取cpu总数
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
        try {
            // Get directory containing CPU info
            File dir = new File(File.separator + "sys" +
                    File.separator + "devices" + File.separator +
                    "system" + File.separator + "cpu" + File.separator);

            // Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());

            // Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            Log.e(TAG, "getCpuNumCores: ", e);
            return 1;
        }
    }


    // 获取可用的CPU数
    public static int getNumAvailableCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static String getMaxCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {File.separator + "system" + File.separator + "bin" + File.separator + "cat",
                    File.separator + "sys" + File.separator
                            + "devices" + File.separator + "system" + File.separator
                            + "cpu" + File.separator + "cpu0" + File.separator + "cpufreq"
                            + File.separator + "cpuinfo_max_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException e) {
            Log.e(TAG, "getMaxCpuFreq: ", e);
            result = "N/A";
        }
        return result.trim() + "Hz";
    }

    // 获取CPU最小频率（单位KHZ）
    public static String getMinCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {File.separator + "system" + File.separator + "bin" + File.separator + "cat",
                    File.separator + "sys" + File.separator
                            + "devices" + File.separator + "system" + File.separator
                            + "cpu" + File.separator + "cpu0" + File.separator + "cpufreq"
                            + File.separator + "cpuinfo_min_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException e) {
            Log.e(TAG, "getMinCpuFreq: ", e);
            result = "N/A";
        }
        return result.trim() + "Hz";
    }

    // 实时获取CPU当前频率（单位KHZ）
    public static String getCurCpuFreq() {
        String result = "N/A";
        try {
            FileReader fr = new FileReader(
                    File.separator + "sys" + File.separator + "devices"
                            + File.separator + "system" + File.separator + "cpu"
                            + File.separator + "cpu0" + File.separator + "cpufreq"
                            + File.separator + "scaling_cur_freq");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            result = text.trim() + "Hz";
        } catch (IOException e) {
            Log.e(TAG, "getCurCpuFreq: ", e);
        }
        return result;
    }


    /**
     * 获取当前系统上的语言列表(Locale列表)
     */
    public static String getDeviceSupportLanguage() {
        Log.e("wangjie", "Local:" + Locale.GERMAN);
        Log.e("wangjie", "Local:" + Locale.ENGLISH);
        Log.e("wangjie", "Local:" + Locale.US);
        Log.e("wangjie", "Local:" + Locale.CHINESE);
        Log.e("wangjie", "Local:" + Locale.TAIWAN);
        Log.e("wangjie", "Local:" + Locale.FRANCE);
        Log.e("wangjie", "Local:" + Locale.FRENCH);
        Log.e("wangjie", "Local:" + Locale.GERMANY);
        Log.e("wangjie", "Local:" + Locale.ITALIAN);
        Log.e("wangjie", "Local:" + Locale.JAPAN);
        Log.e("wangjie", "Local:" + Locale.JAPANESE);
        return Locale.getAvailableLocales().toString();
    }

    public static String getDeviceAllInfo(Context context) {
        JSONObject resultObject = new JSONObject();
        resultObject.put("RAM", SDCardUtils.getRAMInfo(context));
        resultObject.put("ROM", SDCardUtils.getStorageInfo(context, 0));

        return "\nRAM 信息:\t" + SDCardUtils.getRAMInfo(context) + "\n"
                + "\nROM 信息:\t" + SDCardUtils.getStorageInfo(context, 0) + "\n"
                + "\nCPU总数:\t" + getCpuNumCores() + "\n";
    }
}
