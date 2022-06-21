package com.example.benchmark.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class CacheUtil {
    private static SharedPreferences mSp;
    private static SharedPreferences.Editor mEditor;

    public static void init(Context context) {
        mSp = context.getSharedPreferences("CacheFile", Context.MODE_PRIVATE);
        mEditor = mSp.edit();
    }

    public static void put(String key, String value) {
        mEditor.putString(key, value).apply();
    }

    public static void put(String key, int value) {
        mEditor.putInt(key, value).apply();
    }

    public static void put(String key, float value) {
        mEditor.putFloat(key, value).apply();
    }

    public static void put(String key, boolean value) {
        mEditor.putBoolean(key, value).apply();
    }

    public static String getString(String key, String defValue) {
        return mSp.getString(key, defValue);
    }

    public static String getString(String key) {
        return mSp.getString(key, "");
    }

    public static int getInt(String key, int defValue) {
        return mSp.getInt(key, defValue);
    }

    public static int getInt(String key) {
        return mSp.getInt(key, 0);
    }

    public static float getFloat(String key, float defValue) {
        return mSp.getFloat(key, defValue);
    }

    public static float getFloat(String key) {
        return mSp.getFloat(key, 0);
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return mSp.getBoolean(key, defValue);
    }

    public static boolean getBoolean(String key) {
        return mSp.getBoolean(key, false);
    }

}

