package com.example.benchmark.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.List;

public class AccessUtils {
    private Context context;
    public AccessUtils(Context context){
        this.context=context;
    }
    /**
     * 打开无障碍服务设置
     */
    public  void openAccessibilitySettings() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * 辅助服务是否开启
     *
     * @return
     */
    public  boolean isAccessibilityServiceOpen() {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(Short.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo info : services) {
//            LogUtils.v(info.service.getClassName());

//            if (info.service.getClassName().equals(AblService.class.getName())) {
//                return true;
//            }
        }
        return false;
    }


    //判断后台权限是否已经开启
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean isIgnoringBatteryOptimizations() {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
        }
        return isIgnoring;
    }

    //打开后台无限制运行，保证不会被杀死
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestIgnoreBatteryOptimizations() {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("AccessUtils", "requestIgnoreBatteryOptimizations: ", e);
        }
    }

    //开启无障碍服务权限
    public  void  openAccessibilityService(){
        try{
            Intent intent1 = new Intent();
            Toast.makeText(context, "请开启无障碍服务权限", Toast.LENGTH_LONG).show();
            intent1.setAction(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            context.startActivity(intent1);
        }catch (Exception e){
            Log.e("AccessUtils", "openAccessibilityService: ", e);
        }
    }
}
