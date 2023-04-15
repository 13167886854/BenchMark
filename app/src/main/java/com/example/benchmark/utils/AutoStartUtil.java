/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

/**
 * AutoStartUtil
 *
 * @version 1.0
 * @since 2023/3/7 17:23
 */
public class AutoStartUtil {
    /** 是否已经打开过设置自启动界面的标记，存储起来
     *  Whether you have opened the flag for
     *  setting the automatic boot interface, and store it
     */
    public static final String HAS_OPEN_SETTING_AUTO_START = "hasOpenSettingAutoStart";

    /**
     * openStart
     *
     * @param context description
     * @return void
     * @date 2023/3/10 16:33
     */
    public static void openStart(Context context) {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        Intent intent = new Intent();
        if (RomUtil.isEmui()) { // 华为  Huawei
            ComponentName componentName = new ComponentName("com.huawei.systemmanager",
                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
            intent.setComponent(componentName);
        } else if (RomUtil.isMiui()) { // 小米  MIUI
            ComponentName componentName = new ComponentName("com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity");
            intent.setComponent(componentName);
        } else if (RomUtil.isOppo()) { // oppo
            oppo(intent);
        } else if (RomUtil.isVivo()) { // Vivo
            vivo(intent);
        } else if (RomUtil.isFlyme()) {
            flyme(intent);
        } else {
            // 以上只是市面上主流机型，由于公司你懂的，所以很不容易才凑齐以上设备
            // 针对于其他设备，我们只能调整当前系统app查看详情界面
            // 在此根据用户手机当前版本跳转系统设置界面
            if (Build.VERSION.SDK_INT >= 9) {
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", context.getPackageName(), null));
            } else if (Build.VERSION.SDK_INT <= 8) {
                intent.setAction(Intent.ACTION_VIEW);
                intent.setClassName("com.android.settings",
                        "com.android.settings.InstalledAppDetails");
                intent.putExtra("com.android.settings.ApplicationPkgName",
                        context.getPackageName());
            } else {
                Log.e("TAG", "openStart: SDK-VERSION-ERROR");
            }
            intent = new Intent(Settings.ACTION_SETTINGS);
        }
        context.startActivity(intent);
    }

    private static void flyme(Intent intent) {
        // 魅族  MeiZu
        ComponentName componentName = ComponentName.unflattenFromString("com.meizu.safe"
                + "/.permission.PermissionMainActivity");
        intent.setComponent(componentName);
    }

    private static void vivo(Intent intent) {
        ComponentName componentName = null;
        if (Build.VERSION.SDK_INT >= 26) {
            componentName = new ComponentName("com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.PurviewTabActivity");
        } else {
            componentName = new ComponentName("com.iqoo.secure",
                    "com.iqoo.secure.ui.phoneoptimize.SoftwareManagerActivity");
        }
        intent.setComponent(componentName);
    }

    private static void oppo(Intent intent) {
        ComponentName componentName = null;
        if (Build.VERSION.SDK_INT >= 26) {
            componentName = new ComponentName("com.coloros.safecenter",
                    "com.coloros.safecenter.startupapp.StartupAppListActivity");
        } else {
            componentName = new ComponentName("com.color.safecenter",
                    "com.color.safecenter.permission.startup.StartupAppListActivity");
        }
        intent.setComponent(componentName);
    }
}


