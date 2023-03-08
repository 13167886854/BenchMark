/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * StatusBarUtil
 *
 * @version 1.0
 * @since 2023/3/7 17:29
 */
public class StatusBarUtil {

    /**
     * getStatusBarHeight
     *
     * @param context description
     * @return int
     * @throws null
     * @date 2023/3/8 09:15
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height"
                , "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * getNavigationBarHeight
     *
     * @param context description
     * @return int
     * @throws null
     * @date 2023/3/8 09:15
     */
    public static int getNavigationBarHeight(Context context) {
        if (!checkDeviceHasNavigationBar(context)) {
            return 0;
        }
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height"
                , "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * checkDeviceHasNavigationBar
     *
     * @param context description
     * @return boolean
     * @throws null
     * @date 2023/3/8 09:15
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar"
                , "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method med = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = null;
            if (med.invoke(systemPropertiesClass) instanceof String) {
                navBarOverride = (String) med.invoke(systemPropertiesClass
                        , "qemu.hw.mainkeys");
            }
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }else{
                Log.d("checkDeviceHasNavigationBar", "checkDeviceHasNavigationBar: ");
            }
        } catch (IllegalAccessException | NoSuchMethodException | ClassNotFoundException
                | InvocationTargetException exception) {
            Log.e("checkDeviceHasNavigationBar", "checkDeviceHasNavigationBar: "
                    , exception);
        }
        return hasNavigationBar;
    }
}
