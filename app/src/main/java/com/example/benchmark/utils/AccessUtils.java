package com.example.benchmark.utils;

<<<<<<< HEAD
import android.annotation.SuppressLint;
=======
>>>>>>> 211fdf0 ([feat]华为云手机、云手游，红手指、移动云手机稳定性测评)
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

<<<<<<< HEAD
import com.example.benchmark.Service.AblService;

=======
>>>>>>> 211fdf0 ([feat]华为云手机、云手游，红手指、移动云手机稳定性测评)
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
<<<<<<< HEAD
            if (info.service.getClassName().equals(AblService.class.getName())) {
                return true;
            }
=======
//            if (info.service.getClassName().equals(AblService.class.getName())) {
//                return true;
//            }
>>>>>>> 211fdf0 ([feat]华为云手机、云手游，红手指、移动云手机稳定性测评)
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
<<<<<<< HEAD
            @SuppressLint("BatteryLife")
=======
>>>>>>> 211fdf0 ([feat]华为云手机、云手游，红手指、移动云手机稳定性测评)
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

<<<<<<< HEAD
    //开启无障碍服务权限
=======
    //开启无障碍五服务权限
>>>>>>> 211fdf0 ([feat]华为云手机、云手游，红手指、移动云手机稳定性测评)
    public  void  openAccessibilityService(){
        try{
            Intent intent1 = new Intent();
            Toast.makeText(context, "请开启无障碍服务权限", Toast.LENGTH_LONG).show();
            intent1.setAction(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            context.startActivity(intent1);
        }catch (Exception e){

        }
    }
}
