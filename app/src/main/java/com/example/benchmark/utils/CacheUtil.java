/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;
import java.util.TreeSet;

/**
 * CacheUtil
 *
 * @version 1.0
 * @since 2023/3/7 17:23
 */
public class CacheUtil {
    private static SharedPreferences mSp;
    private static SharedPreferences.Editor mEditor;

    /**
     * @param context description
     * @return void
     * @throws null
     * @description: init 初始化
     * @date 2023/2/22 14:47
     */
    public static void init(Context context) {
        mSp = context.getSharedPreferences("CacheFile", Context.MODE_PRIVATE);
        mEditor = mSp.edit();
    }

    /**
     * @param key   description
     * @param value description
     * @return void
     * @throws null
     * @description: put
     * @date 2023/2/22 14:48
     */
    public static void put(String key, String value) {
        mEditor.putString(key, value).apply();
    }

    /**
     * @param key   description
     * @param value description
     * @return void
     * @throws null
     * @description: put
     * @date 2023/2/22 14:48
     */
    public static void put(String key, int value) {
        mEditor.putInt(key, value).apply();
    }

    /**
     * @param key   description
     * @param value description
     * @return void
     * @throws null
     * @description: put
     * @date 2023/2/22 14:48
     */
    public static void put(String key, long value) {
        mEditor.putLong(key, value).apply();
    }

    /**
     * @param key   description
     * @param value description
     * @return void
     * @throws null
     * @description: put
     * @date 2023/2/22 14:48
     */
    public static void put(String key, float value) {
        mEditor.putFloat(key, value).apply();
    }

    /**
     * @param key   description
     * @param isValue description
     * @return void
     * @throws
     * @description: put
     * @date 2023/2/22 14:49
     */
    public static void put(String key, boolean isValue) {
        mEditor.putBoolean(key, isValue).apply();
    }

    /**
     * @param key   description
     * @param value description
     * @return void
     * @throws null
     * @description: put
     * @date 2023/2/22 14:49
     */
    public static void put(String key, Set<String> value) {
        mEditor.putStringSet(key, value).apply();
    }

    /**
     * @param key      description
     * @param defValue description
     * @return java.lang.String
     * @throws null
     * @description: getString
     * @date 2023/2/22 14:49
     */
    public static String getString(String key, String defValue) {
        return mSp.getString(key, defValue);
    }

    /**
     * @param key description
     * @return java.lang.String
     * @throws null
     * @description: getString
     * @date 2023/2/22 14:49
     */
    public static String getString(String key) {
        return mSp.getString(key, "");
    }

    /**
     * @param key      description
     * @param defValue description
     * @return int
     * @throws null
     * @description: getInt
     * @date 2023/2/22 14:50
     */
    public static int getInt(String key, int defValue) {
        return mSp.getInt(key, defValue);
    }

    /**
     * @param key description
     * @return int
     * @throws null
     * @description: getInt
     * @date 2023/2/22 14:50
     */
    public static int getInt(String key) {
        return mSp.getInt(key, 0);
    }

    /**
     * @param key      description
     * @param defValue description
     * @return long
     * @throws null
     * @description: getLong
     * @date 2023/2/22 14:50
     */
    public static long getLong(String key, long defValue) {
        return mSp.getLong(key, defValue);
    }

    /**
     * @param key description
     * @return long
     * @throws null
     * @description: getLong
     * @date 2023/2/22 14:50
     */
    public static long getLong(String key) {
        return mSp.getLong(key, 0);
    }

    /**
     * @param key      description
     * @param defValue description
     * @return float
     * @throws null
     * @description: getFloat
     * @date 2023/2/22 14:50
     */
    public static float getFloat(String key, float defValue) {
        return mSp.getFloat(key, defValue);
    }

    /**
     * @param key description
     * @return float
     * @throws null
     * @description: getFloat
     * @date 2023/2/22 14:50
     */
    public static float getFloat(String key) {
        return mSp.getFloat(key, 0);
    }

    /**
     * @param key      description
     * @param isValue description
     * @return boolean
     * @throws null
     * @description: getBoolean
     * @date 2023/2/22 14:50
     */
    public static boolean getBoolean(String key, boolean isValue) {
        return mSp.getBoolean(key, isValue);
    }

    /**
     * @param key description
     * @return boolean
     * @throws null
     * @description: getBoolean
     * @date 2023/2/22 14:50
     */
    public static boolean getBoolean(String key) {
        return mSp.getBoolean(key, false);
    }

    /**
     * @param key description
     * @return java.util.Set<java.lang.String>
     * @throws null
     * @description: getSet
     * @date 2023/2/22 14:50
     */
    public static Set<String> getSet(String key) {
        return mSp.getStringSet(key, new TreeSet<>());
    }
}

