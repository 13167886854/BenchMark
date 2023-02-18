package com.example.benchmark.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by HaiyuKing
 * Used 自启动设置工具类
 * 来源于：https://www.cnblogs.com/whycxb/p/10115566.html
 */
public class AutoStartUtil {
    // 是否已经打开过设置自启动界面的标记，存储起来
    public static final String HAS_OPEN_SETTING_AUTO_START = "hasOpenSettingAutoStart";


    /* 打开自启动管理页 */
    public static void openStart(Context context) {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        Intent intent = new Intent();
        if (RomUtil.isEmui()) { // 华为
            ComponentName componentName = new ComponentName("com.huawei.systemmanager",
                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
            intent.setComponent(componentName);
        } else if(RomUtil.isMiui()) { // 小米
            ComponentName componentName = new ComponentName("com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity");
            intent.setComponent(componentName);
        } else if(RomUtil.isOppo()) { // oppo
            ComponentName componentName = null;
            if (Build.VERSION.SDK_INT >= 26) {
                componentName = new ComponentName("com.coloros.safecenter",
                        "com.coloros.safecenter.startupapp.StartupAppListActivity");
            } else {
                componentName = new ComponentName("com.color.safecenter",
                        "com.color.safecenter.permission.startup.StartupAppListActivity");
            }
            intent.setComponent(componentName);
            // 上面的代码不管用了，因为oppo手机也是手机管家进行自启动管理
        } else if(RomUtil.isVivo()) { // Vivo
            ComponentName componentName = null;
            if (Build.VERSION.SDK_INT >= 26) {
                componentName = new ComponentName("com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.PurviewTabActivity");
            } else {
                componentName = new ComponentName("com.iqoo.secure",
                        "com.iqoo.secure.ui.phoneoptimize.SoftwareManagerActivity");
            }
            intent.setComponent(componentName);
        } else if(RomUtil.isFlyme()) {
            // 魅族
            // 通过测试，发现魅族是真恶心，也是够了，之前版本还能查看到关于设置自启动这一界面，
            // 系统更新之后，完全找不到了，心里默默Fuck！
            // 针对魅族，我们只能通过魅族内置手机管家去设置自启动，
            // 所以我在这里直接跳转到魅族内置手机管家界面，具体结果请看图
            ComponentName componentName = ComponentName.unflattenFromString("com.meizu.safe"
                    + "/.permission.PermissionMainActivity");
            intent.setComponent(componentName);
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
                Log.e("TAG", "openStart: SDK-VERSION-ERROR" );
            }
            intent = new Intent(Settings.ACTION_SETTINGS);
        }
        try {
            context.startActivity(intent);
        } catch (Exception e) { // 抛出异常就直接打开设置页面
            Intent intent1 = new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent1);
        }
    }
}


