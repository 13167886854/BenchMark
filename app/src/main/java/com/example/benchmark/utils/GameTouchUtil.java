package com.example.benchmark.utils;

import android.util.Log;

import java.util.ArrayList;

public class GameTouchUtil {
    public static int testNum = 10;
    //private int count = 0;
    public long readyToTapTime = 0;
    private long videoStartTime = 0;
    private long videoEndTime = 0;
    private long firstFrameChangeTime = 0;
    private ArrayList<Long> frameUpdateTime = new ArrayList<>();
    private ArrayList<Long> autoTapTime = new ArrayList<>();
    private ArrayList<Long> testTime = new ArrayList<>();


    //单例模式
    private static GameTouchUtil gameTouchUtil = new GameTouchUtil();
    private GameTouchUtil(){}
    public static GameTouchUtil getGameTouchUtil(){
        if(gameTouchUtil==null){
            gameTouchUtil = new GameTouchUtil();
        }
        return gameTouchUtil;
    }

    public long getVideoStartTime(){
        return videoStartTime;
    }

    public long getVideoEndTime(){
        return videoEndTime;
    }

    public void setVideoStartTime(long time){
        videoStartTime = time;
    }

    public void setVideoEndTime(long time){
        videoEndTime = time;
    }

    public void getUpdateTime(long time){
        frameUpdateTime.add(time);
    }

    public void getTapTime(long time){
        autoTapTime.add(time);
    }

    public void getTestTime(long time){ testTime.add(time);}


    public void printFrameUpdateTime(){
        for(int i=0;i<frameUpdateTime.size();i++){
            Log.d("TWT", "第" +(i+1)+ "次画面刷新时间戳:"+frameUpdateTime.get(i));
        }
    }


    public void printTestTime(){
        for(int i=0;i<testTime.size();i++){
            Log.d("TWT", "第" +(i+1)+ "次画面刷新时间戳:"+testTime.get(i));
        }
    }




    public void printAutoTapTime(){
        Log.d("TWT", "printAutoTapTime: ");
        for(int i=0;i<autoTapTime.size();i++){
            Log.d("TWT", "第" +(i+1)+ "次自动点击时间戳:"+autoTapTime.get(i));
        }
    }

    public void printDelayTime(){
        for(int i=0;i<autoTapTime.size();i++){
            Log.d("TWT", "第" +(i+1)+ "次自动点击响应延迟时间:"+(frameUpdateTime.get(i)-autoTapTime.get(i)));
        }
    }

    public String getDelayTime(){
        String str = "";
        for(int i=0;i<frameUpdateTime.size();i++){

            str += ("第" +(i+1)+ "次自动点击响应延迟时间:"+(frameUpdateTime.get(i)-autoTapTime.get(i)))+"\n";
        }
        return str;
    }

    public void clear(){
        frameUpdateTime.clear();
        autoTapTime.clear();
    }

    public void printAvgTime(int TestNum){
        long sum = 0;
        for(int i=0;i<autoTapTime.size();i++){
            sum += (frameUpdateTime.get(i)-autoTapTime.get(i));
        }
        float avgtime = (float)sum / TestNum;
        Log.d("TWT", "printAvgTime: 平均自动点击响应时间为："+avgtime);
    }

    public float getAvgTime(int TestNum){
        long sum = 0;
        for(int i=0;i<autoTapTime.size();i++){
            try{
                sum += (frameUpdateTime.get(i)-autoTapTime.get(i));
            }catch (Exception e){
                //Log.e("TWT", "error:"+e.toString() );
            }
        }
        float avgtime = (float)sum / TestNum;
        Log.e("TWT", "getAvgTime:+frameUpdateTime.size "+frameUpdateTime.size() );
        Log.e("TWT", "getAvgTime:+autoTapTime.size "+autoTapTime.size() );
        return avgtime;
    }

    public int getDetectNum(){
        return frameUpdateTime.size();
    }


}
