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

import com.example.benchmark.Activity.CePingActivity;
import com.example.benchmark.Service.FxService;

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

    public static void backToCePingActivity(Service service) {
        Intent resultIntent = new Intent(service, CePingActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        service.startActivity(resultIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && service instanceof AccessibilityService) {
            ((AccessibilityService) service).disableSelf();
        }
        service.stopSelf();
    }

    public static void startFxService(Context context, String checked_plat, int resultCode, Intent data, boolean isCheckTouch) {
        if (!Settings.canDrawOverlays(context)) {
            toFloatGetPermission(context);
        }else{
            Intent fxService = new Intent(context, FxService.class)
                    .putExtra(CacheConst.KEY_PLATFORM_NAME, checked_plat)
                    .putExtra("resultCode", resultCode)
                    .putExtra("data", data)
                    .putExtra("isCheckTouch", isCheckTouch);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startService(fxService);
            } else {
                context.startService(fxService);
            }
        }
    }

    private static void toFloatGetPermission(Context context) {
        Toast.makeText(context, "请允许本应用显示悬浮窗！", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
        Log.d("TWT", "toFloatGetPermission: " + Uri.parse("package:" + context.getPackageName()));
        //intent.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        context.startActivity(intent);
        //startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")), 0);
    }
}
