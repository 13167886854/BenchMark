package com.example.benchmark.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.benchmark.Service.AutoTapService;

import java.util.Timer;
import java.util.TimerTask;


public class TapUtil {
    //单例模式
    private AutoTapService service;
    private static TapUtil util = new TapUtil();
//    public  Handler handler = new Handler(){
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            switch (msg.what){
//                case 0:
//                    timer.cancel();
//                    break;
//            }
//            super.handleMessage(msg);
//        }
//    };
    private TapUtil(){}
    public static TapUtil getUtil(){
        if(util==null){
            util = new TapUtil();
            //util.tap();
        }
        return util;
    }

    private Timer timer = new Timer();
    private int turn = 0;


    public void setService(AutoTapService service){
        this.service = service;
    }

    public void tap(int x,int y){
        if(service==null){
            Log.e("TWT", "service is not initial yet");
        }
        AccessibilityUtil.tap(service,x,y,
                new AccessibilityCallback() {
                                @Override
                                public void onSuccess() {
                                    Log.d("TWT", "do tap when time is "+System.currentTimeMillis());
                                }

                                @Override
                                public void onFailure() {
                                    Log.e("TWT", "tap failure");
                                }}
        );

    }

    public void GameTouchTap(){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Log.d("TWT", "turn = "+turn);
                turn++;
                if(turn%2==1){
                    tap(2165,860); //点击设置按钮

                }else {
                    tap(1000,830);  //点击取消按钮
                }
                if(turn==10){
                    Log.d("TWT", "run: stop");
                    cancel();
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task,3000,1000);
    }



}
