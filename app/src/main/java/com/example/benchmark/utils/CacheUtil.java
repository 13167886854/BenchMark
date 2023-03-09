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
     * init
     *
     * @param context description
     * @return void
     * @date 2023/3/9 16:32
     */
    public static void init(Context context) {
        mSp = context.getSharedPreferences("CacheFile", Context.MODE_PRIVATE);
        mEditor = mSp.edit();
    }

    /**
     * put
     *
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
     * put
     *
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
     * put
     *
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
     * put
     *
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
     * put
     *
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
     * put
     *
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
     * getString
     *
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
     * getString
     *
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
     * getInt
     *
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
     * getInt
     *
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
     * getLong
     *
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
     * getLong
     *
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
     * getFloat
     *
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
     * getFloat
     *
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
     * getBoolean
     *
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
     * getBoolean
     *
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
     * getSet
     *
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

