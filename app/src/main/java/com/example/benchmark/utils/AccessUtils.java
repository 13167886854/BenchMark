/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.List;

/**
 * AccessUtils
 *
 * @version 1.0
 * @since 2023/3/7 17:22
 */
public class AccessUtils {
    private Context context;

    /**
     * AccessUtils
     *
     * @param context description
     * @return
     * @throws null
     * @date 2023/3/8 10:57
     */
    public AccessUtils(Context context) {
        this.context = context;
    }

    /**
     * @return void
     * @throws null
     * @description: 打开无障碍服务设置
     * @date 2023/2/23 15:25
     */
    public void openAccessibilitySettings() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * @return boolean
     * @throws null
     * @description: 辅助服务是否开启
     * @date 2023/2/23 15:25
     */
    public boolean isAccessibilityServiceOpen() {
        ActivityManager am = null;
        if (context.getSystemService(Context.ACTIVITY_SERVICE) instanceof ActivityManager) {
            am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        }
        List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(Short.MAX_VALUE);
        return false;
    }

    /**
     * @return boolean
     * @throws null
     * @description: 判断后台权限是否已经开启
     * @date 2023/2/23 15:26
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean isIgnoringBatteryOptimizations() {
        boolean isIgnoring = false;
        PowerManager powerManager = null;
        if (context.getSystemService(Context.POWER_SERVICE) instanceof PowerManager) {
            powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        }
        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
        }
        return isIgnoring;
    }

    /**
     * @return void
     * @throws null
     * @description: 打开后台无限制运行，保证不会被杀死
     * @date 2023/2/23 15:26
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestIgnoreBatteryOptimizations() {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        } catch (ParseException e) {
            Log.e("AccessUtils", "requestIgnoreBatteryOptimizations: ", e);
        }
    }

    /**
     * @return void
     * @throws null
     * @description: 开启无障碍服务权限
     * @date 2023/2/23 15:26
     */
    public void openAccessibilityService() {
        try {
            Intent intent1 = new Intent();
            Toast.makeText(context, "请开启无障碍服务权限", Toast.LENGTH_LONG).show();
            intent1.setAction(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            context.startActivity(intent1);
        } catch (ParseException e) {
            Log.e("AccessUtils", "openAccessibilityService: ", e);
        }
    }
}
