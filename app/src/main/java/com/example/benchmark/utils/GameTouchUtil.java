package com.example.benchmark.utils;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

public class GameTouchUtil {
    //private int count = 0;
    private long videoStartTime = 0;
    private long firstFrameChangeTime = 0;
    private ArrayList<Long> frameUpdateTime = new ArrayList<>();


    //单例模式
    private static GameTouchUtil gameTouchUtil = new GameTouchUtil();
    private GameTouchUtil(){}
    public static GameTouchUtil getGameTouchUtil(){
        if(gameTouchUtil==null){
            gameTouchUtil = new GameTouchUtil();
        }
        return gameTouchUtil;
    }


    public void getUpdateTime(long time){
        Log.d("TWT", "getUpdateTime: OKOKOK");
        frameUpdateTime.add(time);
    }

    public void print(){
        Log.d("TWT", "print:frameUpdateTime.size="+frameUpdateTime.size());
        Log.d("TWT", "PRINT!!!!");
        for(int i=0;i<frameUpdateTime.size();i++){
            Log.d("TWT", "第" +(i+1)+ "次画面刷新时间戳:"+frameUpdateTime.get(i));
        }
    }

    public void clear(){
        frameUpdateTime.clear();
    }

}
