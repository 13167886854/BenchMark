package com.example.benchmark.utils;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @version 1.0
 * @description Created by HaiyuKing
 * * Used 判断手机ROM,检测ROM是MIUI、EMUI还是Flyme
 * * 来源于：https://www.cnblogs.com/whycxb/p/10095758.html
 * @time 2023/3/1 14:58
 */
public class RomUtil {

    // TAG
    private static final String TAG = "Rom";

    // MIUI
    public static final String ROM_MIUI = "MIUI";

    // EMUI
    public static final String ROM_EMUI = "EMUI";

    // FLYME
    public static final String ROM_FLYME = "FLYME";

    // OPPO
    public static final String ROM_OPPO = "OPPO";

    // SMARTISAN
    public static final String ROM_SMARTISAN = "SMARTISAN";

    // VIVO
    public static final String ROM_VIVO = "VIVO";

    // ROM_QIKU
    public static final String ROM_QIKU = "QIKU";
    private static final String KEY_VERSION_MIUI = "ro.miui.ui.version.name";
    private static final String KEY_VERSION_EMUI = "ro.build.version.emui";
    private static final String KEY_VERSION_OPPO = "ro.build.version.opporom";
    private static final String KEY_VERSION_SMARTISAN = "ro.smartisan.version";
    private static final String KEY_VERSION_VIVO = "ro.vivo.os.version";

    private static String sName;
    private static String sVersion;


    /**
     * @return boolean
     * @throws null
     * @description: isEmui 华为
     * @date 2023/3/1 14:55
     */
    public static boolean isEmui() {
        return check(ROM_EMUI);
    }

    /**
     * @return boolean
     * @throws null
     * @description: isMiui 小米
     * @date 2023/3/1 14:55
     */
    public static boolean isMiui() {
        return check(ROM_MIUI);
    }

    /**
     * @return boolean
     * @throws null
     * @description: isVivo
     * @date 2023/3/1 14:55
     */
    public static boolean isVivo() {
        return check(ROM_VIVO);
    }

    /**
     * @return boolean
     * @throws null
     * @description: isOppo
     * @date 2023/3/1 14:55
     */
    public static boolean isOppo() {
        return check(ROM_OPPO);
    }

    /**
     * @return boolean
     * @throws null
     * @description: isFlyme  魅族
     * @date 2023/3/1 14:56
     */
    public static boolean isFlyme() {
        return check(ROM_FLYME);
    }

    /**
     * @return boolean
     * @throws null
     * @description: is360
     * @date 2023/3/1 14:56
     */
    public static boolean is360() {
        return check(ROM_QIKU) || check("360");
    }

    /**
     * @return boolean
     * @throws null
     * @description: isSmart
     * @date 2023/3/1 14:56
     */
    public static boolean isSmart() {
        return check(ROM_SMARTISAN);
    }

    /**
     * @return java.lang.String
     * @throws null
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
     * @return java.lang.String
     * @throws null
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
     * @param rom description
     * @return boolean
     * @throws null
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
            if (sVersion.toUpperCase().contains(ROM_FLYME)) {
                sName = ROM_FLYME;
            } else {
                sVersion = Build.UNKNOWN;
                sName = Build.MANUFACTURER.toUpperCase();
            }
        }
        return sName.equals(rom);
    }

    /**
     * @param name description
     * @return java.lang.String
     * @throws null
     * @description: getProp
     * @date 2023/3/1 14:57
     */
    public static String getProp(String name) {
        String line = null;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + name);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            Log.e(TAG, "Unable to read prop " + name, ex);
            return null;
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

