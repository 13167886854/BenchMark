package com.example.benchmark.utils;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;

public class ServiceUtil {
    /**
     * 校验某个服务是否还存在
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName componentName=new ComponentName(context.getPackageName(),serviceName);
        PendingIntent intent = am.getRunningServiceControlPanel(componentName);
        return intent != null;
    }
}
