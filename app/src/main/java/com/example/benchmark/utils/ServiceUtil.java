/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.benchmark.activity.CePingActivity;
import com.example.benchmark.service.FxService;

/**
 * ServiceUtil
 *
 * @version 1.0
 * @since 2023/3/7 17:28
 */
public class ServiceUtil {
    /**
     * isServiceRunning
     *
     * @param context     description
     * @param serviceName description
     * @return boolean
     * @description: isServiceRunning  校验某个服务是否还存在  Check whether a service still exists
     * @date 2023/2/22 14:52
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        PendingIntent intent = null;
        if (context.getSystemService(Context.ACTIVITY_SERVICE) instanceof ActivityManager) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName componentName = new ComponentName(context.getPackageName(), serviceName);
            intent = am.getRunningServiceControlPanel(componentName);
            return intent != null;
        } else {
            return false;
        }
    }

    /**
     * backToCePingActivity
     *
     * @param serviceName description
     * @description: backToCePingActivity
     * @date 2023/2/22 14:53
     */
    public static void backToCePingActivity(Service serviceName) {
        Intent resultIntent = new Intent(serviceName, CePingActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        serviceName.startActivity(resultIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && serviceName
                instanceof AccessibilityService) {
            ((AccessibilityService) serviceName).disableSelf();
        }
        serviceName.stopSelf();
    }

    /**
     * startFxService
     *
     * @param context           description
     * @param checkedPlat      description
     * @param resultCode        description
     * @param data              description
     * @param isCheckGroup      isCheckTouch,isCheckSoundFrame
     * @description: startFxService
     * @date 2023/2/22 14:53
     */
    public static void startFxService(Context context, String checkedPlat, int resultCode,
        Intent data, boolean[] isCheckGroup) {
        boolean isCheckTouch = isCheckGroup[0];
        boolean isCheckSoundFrame = isCheckGroup[1];
        if (!Settings.canDrawOverlays(context)) {
            toFloatGetPermission(context);
        } else {
            Intent fxService = new Intent(context, FxService.class)
                    .putExtra(CacheConst.KEY_PLATFORM_NAME, checkedPlat)
                    .putExtra("resultCode", resultCode)
                    .putExtra("data", data)
                    .putExtra("isCheckTouch", isCheckTouch)
                    .putExtra("isCheckSoundFrame", isCheckSoundFrame);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startService(fxService);
            } else {
                context.startService(fxService);
            }
        }
    }

    private static void toFloatGetPermission(Context context) {
        Toast.makeText(context, "请允许本应用显示悬浮窗！", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context.getPackageName()));

        Log.d("TWT", "toFloatGetPermission: "
                + Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }
}
