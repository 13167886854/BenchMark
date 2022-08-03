package com.example.benchmark.Service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.benchmark.Activity.CePingActivity;
import com.example.benchmark.utils.AccessibilityCallback;
import com.example.benchmark.utils.AccessibilityUtil;
import com.example.benchmark.utils.TapUtil;

public class AutoTapService extends AccessibilityService {
    private TapUtil tapUtil;

    @Override
    public void onCreate() {
        super.onCreate();
        Notification notification = createForegroundNotification();
        //启动前台服务
        startForeground(1,notification);
        tapUtil = TapUtil.getUtil();
        tapUtil.setService(this);
        //Log.d("TWT", "onCreate: 123123");
        //tap(1000,1000);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }

    protected void onServiceConnected() {

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //查看辅助事件信息
        //Log.d("TWT", "event: "+event.toString());
    }

    @Override
    public void onInterrupt() {

    }
    
    private Notification createForegroundNotification(){
        //前台通知的id名，任意
        String channelId = "ForegroundService";
        //前台通知的名称，任意
        String channelName = "Service";
        //发送通知的等级，此处为高，根据业务情况而定
        int importance = NotificationManager.IMPORTANCE_HIGH;
        //判断Android版本，不同的Android版本请求不一样，以下代码为官方写法
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(channelId,channelName,importance);
            channel.setLightColor(Color.BLUE);
            //channel.setLockscreenVisiability(Notification.VISIBILITY_PRIVATE);
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        //点击通知时可进入的Activity
        Intent notificationIntent = new Intent(this, CePingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

        //最终创建的通知，以下代码为官方写法
        //注释部分是可扩展的参数，根据自己的功能需求添加
        return new NotificationCompat.Builder(this,channelId)
                .setContentTitle("无障碍服务AutoTap")
                .setContentText("自动点击工作中")
                //.setSmallIcon(Icon)//通知显示的图标
                .setContentIntent(pendingIntent)//点击通知进入Activity
                //.setTicker("通知的提示语")
                .build();
        //.setOngoing(true)
        //.setPriority(NotificationCompat.PRIORITY_MAX)
        //.setCategory(Notification.CATEGORY_TRANSPORT)
        //.setLargeIcon(Icon)
        //.setWhen(System.currentTimeMillis())
    }




}
