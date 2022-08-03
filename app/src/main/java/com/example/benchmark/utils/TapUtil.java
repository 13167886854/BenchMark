package com.example.benchmark.utils;

import android.os.Handler;
import android.util.Log;

import com.example.benchmark.Service.AutoTapService;

public class TapUtil {
    //单例模式
    private AutoTapService service;
    private static TapUtil util = new TapUtil();
    public static Handler mainHandler = new Handler();
    private TapUtil(){}
    public static TapUtil getUtil(){
        if(util==null){
            util = new TapUtil();
            //util.tap();
        }
        return util;
    }

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


}
