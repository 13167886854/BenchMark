/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;


import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * SDCardUtils
 *
 * @version 1.0
 * @since 2023/3/7 17:28
 */
public class SDCardUtils {
    private static final int INTERNAL_STORAGE = 0;
    private static final int EXTERNAL_STORAGE = 1;
    private static final String TAG = "SDCardUtils";

    /**
     * 获取 手机 RAM 信息
     */
    public static Map<String, String> getRAMInfo(Context context) {
        long totalSize = 0L;
        long availableSize = 0L;
        if (context.getSystemService(context.ACTIVITY_SERVICE) instanceof ActivityManager) {
            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(context.ACTIVITY_SERVICE);
            MemoryInfo memoryInfo = new MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            totalSize = memoryInfo.totalMem;
            availableSize = memoryInfo.availMem;
            Map<String, String> map = new HashMap<>();
            Log.d("TWT", "availableSize: " + availableSize);
            Log.d("TWT", "Formatter.formatFileSize(context, availableSize): "
                    + Formatter.formatFileSize(context, availableSize));
            map.put("可用", Formatter.formatFileSize(context, availableSize));
            map.put("总共", Formatter.formatFileSize(context, totalSize));
            return map;
        }
        return new HashMap<>();
    }

    /**
     * 判断SD是否挂载
     */
    public static boolean isSDCardMount() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取手机存储 ROM 信息
     * <p>
     * type： 用于区分内置存储于外置存储的方法
     * <p>
     * 内置SD卡 ：INTERNAL_STORAGE = 0;
     * <p>
     * 外置SD卡： EXTERNAL_STORAGE = 1;
     **/
    public static Map<String, String> getStorageInfo(Context context, int type) {
        String path = getStoragePath(context, type);
        /**
         * 无外置SD 卡判断
         * **/
        if (isSDCardMount() != true || TextUtils.isEmpty(path)) {
            return (Map<String, String>) new HashMap<>().put("无外置SD卡", null);
        }

        if (path == null) {
            return (Map<String, String>) new HashMap<>().put("无外置SD卡", null);
        }

        File file = new File(path);
        StatFs statFs = new StatFs(file.getPath());
        String stotageInfo;

        long blockCount = statFs.getBlockCountLong();
        long bloackSize = statFs.getBlockSizeLong();
        long totalSpace = bloackSize * blockCount;
        long availableBlocks = statFs.getAvailableBlocksLong();
        long availableSpace = availableBlocks * bloackSize;

        Map<String, String> res = new HashMap<>();
        res.put("可用", Formatter.formatFileSize(context, availableSpace));
        res.put("总共", Formatter.formatFileSize(context, totalSpace));
        stotageInfo = "可用" + File.separator + "总共："
                + Formatter.formatFileSize(context, availableSpace) + File.separator
                + Formatter.formatFileSize(context, totalSpace);

        return res;
    }

    /**
     * 使用反射方法 获取手机存储路径
     **/
    public static String getStoragePath(Context context, int type) {
        if (context.getSystemService(Context.STORAGE_SERVICE) instanceof StorageManager) {
            StorageManager sm = (StorageManager) context
                    .getSystemService(Context.STORAGE_SERVICE);
            try {
                Method getPathsMethod = sm.getClass().getMethod("getVolumePaths", new Class[0]);
                String[] path = (String[]) getPathsMethod.invoke(sm, new Object[0]);
                switch (type) {
                    case INTERNAL_STORAGE:
                        return path[type];
                    case EXTERNAL_STORAGE:
                        if (path.length > 1) {
                            return path[type];
                        } else {
                            return "null";
                        }
                    default:
                        break;
                }
            } catch (Exception e) {
                Log.e(TAG, "getStoragePath: ", e);
            }
        }

        return "null";
    }

    /**
     * 获取 手机 RAM 信息 方法 一
     */
    public static String getTotalRAM(Context context) {
        long size = 0L;

        if (context.getSystemService(context.ACTIVITY_SERVICE) instanceof ActivityManager) {
            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(context.ACTIVITY_SERVICE);
            MemoryInfo outInfo = new MemoryInfo();
            activityManager.getMemoryInfo(outInfo);
            size = outInfo.totalMem;
            return Formatter.formatFileSize(context, size);
        }
        return Formatter.formatFileSize(context, size);
    }

    /**
     * 手机 RAM 信息 方法 二
     */
    public static String getTotalRAMOther(Context context) {
        String path = File.separator + "proc" + File.separator + "meminfo";
        String firstLine = null;
        int totalRam = 0;
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader, 8192);
            firstLine = br.readLine().split("\\s+")[1];
            br.close();
        } catch (Exception e) {
            Log.e(TAG, "getTotalRAMOther: ", e);
        }
        if (firstLine != null) {
            totalRam = (int) Math.ceil((new Float(Float.valueOf(firstLine)
                    / (1024 * 1024)).doubleValue()));
            long totalBytes = 0L;
        }
        return Formatter.formatFileSize(context, totalRam);
    }

    /**
     * 获取 手机 可用 RAM
     */
    public static String getAvailableRAM(Context context) {
        long size = 0L;
        if (context.getSystemService(context.ACTIVITY_SERVICE) instanceof ActivityManager) {
            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(context.ACTIVITY_SERVICE);
            MemoryInfo outInfo = new MemoryInfo();
            activityManager.getMemoryInfo(outInfo);
            size = outInfo.availMem;
            return Formatter.formatFileSize(context, size);
        }
        return Formatter.formatFileSize(context, size);
    }

    /**
     * 获取手机内部存储空间
     *
     * @param context
     * @return 以M, G为单位的容量
     */
    public static String getTotalInternalMemorySize(Context context) {
        File file = Environment.getDataDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long blockSizeLong = statFs.getBlockSizeLong();
        long blockCountLong = statFs.getBlockCountLong();
        long size = blockCountLong * blockSizeLong;
        return Formatter.formatFileSize(context, size);
    }

    /**
     * 获取手机内部可用存储空间
     *
     * @param context
     * @return 以M, G为单位的容量
     */
    public static String getAvailableInternalMemorySize(Context context) {
        File file = Environment.getDataDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long availableBlocksLong = statFs.getAvailableBlocksLong();
        long blockSizeLong = statFs.getBlockSizeLong();
        return Formatter.formatFileSize(context, availableBlocksLong
                * blockSizeLong);
    }

    /**
     * 获取手机外部存储空间
     *
     * @param context
     * @return 以M, G为单位的容量
     */
    public static String getTotalExternalMemorySize(Context context) {
        File file = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long blockSizeLong = statFs.getBlockSizeLong();
        long blockCountLong = statFs.getBlockCountLong();
        return Formatter
                .formatFileSize(context, blockCountLong * blockSizeLong);
    }

    /**
     * 获取手机外部可用存储空间
     *
     * @param context
     * @return 以M, G为单位的容量
     */
    public static String getAvailableExternalMemorySize(Context context) {
        File file = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long availableBlocksLong = statFs.getAvailableBlocksLong();
        long blockSizeLong = statFs.getBlockSizeLong();
        return Formatter.formatFileSize(context, availableBlocksLong
                * blockSizeLong);
    }

    /**
     * SD 卡信息
     */

    public static String getSDCardInfo() {
        SDCardInfo sd = new SDCardInfo();
        if (!isSDCardMount()) {
            return "SD card 未挂载!";
        }
        sd.isExist = true;
        StatFs sf = new StatFs(Environment.getExternalStorageDirectory()
                .getPath());

        sd.totalBlocks = sf.getBlockCountLong();
        sd.blockByteSize = sf.getBlockSizeLong();
        sd.availableBlocks = sf.getAvailableBlocksLong();
        sd.availableBytes = sf.getAvailableBytes();
        sd.freeBlocks = sf.getFreeBlocksLong();
        sd.freeBytes = sf.getFreeBytes();
        sd.totalBytes = sf.getTotalBytes();
        return sd.toString();
    }

    public static class SDCardInfo {
        boolean isExist;
        long totalBlocks;
        long freeBlocks;
        long availableBlocks;
        long blockByteSize;
        long totalBytes;
        long freeBytes;
        long availableBytes;

        @Override
        public String toString() {
            return "isExist=" + isExist + System.getProperty("line.separator") + "totalBlocks=" + totalBlocks
                    + System.getProperty("line.separator") + "freeBlocks=" + freeBlocks
                    + System.getProperty("line.separator") + "availableBlocks="
                    + availableBlocks + System.getProperty("line.separator") + "blockByteSize=" + blockByteSize
                    + System.getProperty("line.separator") + "totalBytes=" + totalBytes
                    + System.getProperty("line.separator") + "freeBytes=" + freeBytes
                    + System.getProperty("line.separator") + "availableBytes=" + availableBytes;
        }
    }

    // add start by wangjie for SDCard TotalStorage
    public static String getSDCardTotalStorage(long totalByte) {
        double byte2GB = totalByte / 1024.00 / 1024.00 / 1024.00;
        double totalStorage;
        if (byte2GB > 1) {
            totalStorage = Math.ceil(byte2GB);
            if (totalStorage > 1 && totalStorage < 3) {
                return 2.0 + "GB";
            } else if (totalStorage > 2 && totalStorage < 5) {
                return 4.0 + "GB";
            } else if (totalStorage >= 5 && totalStorage < 10) {
                return 8.0 + "GB";
            } else if (totalStorage >= 10 && totalStorage < 18) {
                return 16.0 + "GB";
            } else if (totalStorage >= 18 && totalStorage < 34) {
                return 32.0 + "GB";
            } else if (totalStorage >= 34 && totalStorage < 50) {
                return 48.0 + "GB";
            } else if (totalStorage >= 50 && totalStorage < 66) {
                return 64.0 + "GB";
            } else if (totalStorage >= 66 && totalStorage < 130) {
                return 128.0 + "GB";
            } else {
                Log.e(TAG, "getSDCardTotalStorage: totalStorage >= 130");
            }
        } else {
            // below 1G return get values
            totalStorage = totalByte / 1024.00 / 1024.00;

            if (totalStorage >= 515 && totalStorage < 1024) {
                return 1 + "GB";
            } else if (totalStorage >= 260 && totalStorage < 515) {
                return 512 + "MB";
            } else if (totalStorage >= 130 && totalStorage < 260) {
                return 256 + "MB";
            } else if (totalStorage > 70 && totalStorage < 130) {
                return 128 + "MB";
            } else if (totalStorage > 50 && totalStorage < 70) {
                return 64 + "MB";
            } else {
                Log.e(TAG, "getSDCardTotalStorage: totalStorage >= 70 ");
            }
        }
        return totalStorage + "GB";
    }
}
