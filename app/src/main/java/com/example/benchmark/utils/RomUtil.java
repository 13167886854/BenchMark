/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * RomUtil
 *
 * @version 1.0
 * @since 2023/3/7 17:28
 */
public class RomUtil {
    /** 输出TAG */
    private static final String TAG = "Rom";

    /** MIUI */
    public static final String ROM_MIUI = "MIUI";

    /** EMUI */
    public static final String ROM_EMUI = "EMUI";

    /** FLYME */
    public static final String ROM_FLYME = "FLYME";

    /** OPPO */
    public static final String ROM_OPPO = "OPPO";

    /** SMARTISAN */
    public static final String ROM_SMARTISAN = "SMARTISAN";

    /** VIVO */
    public static final String ROM_VIVO = "VIVO";

    /** QIKU */
    public static final String ROM_QIKU = "QIKU";

    /** KEY_VERSION_MIUI */
    private static final String KEY_VERSION_MIUI = "ro.miui.ui.version.name";

    /** KEY_VERSION_EMUI */
    private static final String KEY_VERSION_EMUI = "ro.build.version.emui";

    /** KEY_VERSION_OPPO */
    private static final String KEY_VERSION_OPPO = "ro.build.version.opporom";

    /** KEY_VERSION_SMARTISAN */
    private static final String KEY_VERSION_SMARTISAN = "ro.smartisan.version";

    /** KEY_VERSION_VIVO */
    private static final String KEY_VERSION_VIVO = "ro.vivo.os.version";

    private static String sName;
    private static String sVersion;


    /**
     * isEmui
     *
     * @return boolean
     * @description: isEmui 华为
     * @date 2023/3/1 14:55
     */
    public static boolean isEmui() {
        return check(ROM_EMUI);
    }

    /**
     * isSmart
     *
     * @return boolean
     * @description: isMiui 小米
     * @date 2023/3/1 14:55
     */
    public static boolean isMiui() {
        return check(ROM_MIUI);
    }

    /**
     * isMiui
     *
     * @return boolean
     * @description: isVivo
     * @date 2023/3/1 14:55
     */
    public static boolean isVivo() {
        return check(ROM_VIVO);
    }

    /**
     * isVivo
     *
     * @return boolean
     * @description: isOppo
     * @date 2023/3/1 14:55
     */
    public static boolean isOppo() {
        return check(ROM_OPPO);
    }

    /**
     * isOppo
     *
     * @return boolean
     * @description: isFlyme  魅族
     * @date 2023/3/1 14:56
     */
    public static boolean isFlyme() {
        return check(ROM_FLYME);
    }

    /**
     * isFlyme
     *
     * @return boolean
     * @description: is360
     * @date 2023/3/1 14:56
     */
    public static boolean is360() {
        return check(ROM_QIKU) || check("360");
    }

    /**
     * is360
     *
     * @return boolean
     * @description: isSmart
     * @date 2023/3/1 14:56
     */
    public static boolean isSmart() {
        return check(ROM_SMARTISAN);
    }

    /**
     * getVersion
     *
     * @return java.lang.String
     * @description: getName
     * @date 2023/3/1 14:57
     */
    public static String getName() {
        if (sName == null) {
            check("");
        }
        return sName;
    }

    /**
     * getName
     *
     * @return java.lang.String
     * @description: getVersion
     * @date 2023/3/1 14:57
     */
    public static String getVersion() {
        if (sVersion == null) {
            check("");
        }
        return sVersion;
    }

    /**
     * check
     *
     * @param rom description
     * @return boolean
     * @description: check
     * @date 2023/3/1 14:57
     */
    public static boolean check(String rom) {
        if (sName != null) {
            return sName.equals(rom);
        }

        if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_MIUI))) {
            sName = ROM_MIUI;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_EMUI))) {
            sName = ROM_EMUI;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_OPPO))) {
            sName = ROM_OPPO;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_VIVO))) {
            sName = ROM_VIVO;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_SMARTISAN))) {
            sName = ROM_SMARTISAN;
        } else {
            sVersion = Build.DISPLAY;
            if (sVersion.toUpperCase(Locale.ROOT).contains(ROM_FLYME)) {
                sName = ROM_FLYME;
            } else {
                sVersion = Build.UNKNOWN;
                sName = Build.MANUFACTURER.toUpperCase();
            }
        }
        return sName.equals(rom);
    }

    /**
     * getProp
     *
     * @param name description
     * @return java.lang.String
     * @description: getProp
     * @date 2023/3/1 14:57
     */
    public static String getProp(String name) {
        String line = null;
        BufferedReader input = null;
        try {
            Process process = Runtime.getRuntime().exec("getprop " + name);
            input = new BufferedReader(new InputStreamReader(process.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            Log.e(TAG, "Unable to read prop " + name, ex);
            return "null";
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Log.e(TAG, "getProp: ", e);
                }
            }
        }
        return line;
    }
}

