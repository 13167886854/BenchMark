/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

import android.util.Log;

import java.util.ArrayList;

/**
 * GameTouchUtil
 *
 * @version 1.0
 * @since 2023/3/7 17:24
 */
public class GameTouchUtil {

    // 测试次数
    public static final int testNum = 10;

    // readyToTapTime
    public long readyToTapTime = 0;
    private long videoStartTime = 0;
    private long videoEndTime = 0;
    private ArrayList<Long> frameUpdateTime = new ArrayList<>();
    private ArrayList<Long> autoTapTime = new ArrayList<>();
    private ArrayList<Long> testTime = new ArrayList<>();

    //单例模式
    private static GameTouchUtil gameTouchUtil = new GameTouchUtil();

    private GameTouchUtil() {
    }

    /**
     * @return com.example.benchmark.utils.GameTouchUtil
     * @throws null
     * @description: getGameTouchUtil
     * @date 2023/3/2 09:52
     */
    public static GameTouchUtil getGameTouchUtil() {
        if (gameTouchUtil == null) {
            gameTouchUtil = new GameTouchUtil();
        }
        return gameTouchUtil;
    }

    /**
     * @return long
     * @throws null
     * @description: getVideoStartTime
     * @date 2023/3/2 09:54
     */
    public long getVideoStartTime() {
        return videoStartTime;
    }

    /**
     * @return long
     * @throws null
     * @description: getVideoEndTime
     * @date 2023/3/2 09:54
     */
    public long getVideoEndTime() {
        return videoEndTime;
    }

    /**
     * @param time description
     * @return void
     * @throws null
     * @description: setVideoStartTime
     * @date 2023/3/2 09:54
     */
    public void setVideoStartTime(long time) {
        videoStartTime = time;
    }

    /**
     * @param time description
     * @return void
     * @throws null
     * @description: setVideoEndTime
     * @date 2023/3/2 09:54
     */
    public void setVideoEndTime(long time) {
        videoEndTime = time;
    }

    /**
     * @param time description
     * @return void
     * @throws null
     * @description: getUpdateTime
     * @date 2023/3/2 09:54
     */
    public void getUpdateTime(long time) {
        frameUpdateTime.add(time);
    }

    /**
     * @param time description
     * @return void
     * @throws null
     * @description: getTapTime
     * @date 2023/3/2 09:54
     */
    public void getTapTime(long time) {
        autoTapTime.add(time);
    }

    /**
     * @param time description
     * @return void
     * @throws null
     * @description: getTestTime
     * @date 2023/3/2 09:54
     */
    public void getTestTime(long time) {
        testTime.add(time);
    }

    /**
     * @return void
     * @throws null
     * @description: printFrameUpdateTime
     * @date 2023/3/2 09:54
     */
    public void printFrameUpdateTime() {
        for (int i = 0; i < frameUpdateTime.size(); i++) {
            Log.d("TWT", "第" + (i + 1) + "次画面刷新时间戳:" + frameUpdateTime.get(i));
        }
    }

    /**
     * @return void
     * @throws null
     * @description: printTestTime
     * @date 2023/3/2 09:54
     */
    public void printTestTime() {
        for (int i = 0; i < testTime.size(); i++) {
            Log.d("TWT", "第" + (i + 1) + "次画面刷新时间戳:" + testTime.get(i));
        }
    }

    /**
     * @return void
     * @throws null
     * @description: printAutoTapTime
     * @date 2023/3/2 09:54
     */
    public void printAutoTapTime() {
        Log.d("TWT", "printAutoTapTime: ");
        for (int i = 0; i < autoTapTime.size(); i++) {
            Log.d("TWT", "第" + (i + 1) + "次自动点击时间戳:" + autoTapTime.get(i));
        }
    }

    /**
     * @return void
     * @throws null
     * @description: printDelayTime
     * @date 2023/3/2 09:54
     */
    public void printDelayTime() {
        for (int i = 0; i < autoTapTime.size(); i++) {
            Log.d("TWT", "第" + (i + 1) + "次自动点击响应延迟时间:"
                    + (frameUpdateTime.get(i) - autoTapTime.get(i)));
        }
    }

    /**
     * @return java.lang.String
     * @throws null
     * @description: getDelayTime
     * @date 2023/3/2 09:55
     */
    public String getDelayTime() {
        String str = "";
        for (int i = 0; i < frameUpdateTime.size(); i++) {
            str += ("第" + (i + 1) + "次自动点击响应延迟时间:"
                    + (frameUpdateTime.get(i) - autoTapTime.get(i))) ;
        }
        return str;
    }

    /**
     * @return void
     * @throws null
     * @description: clear
     * @date 2023/3/2 09:55
     */
    public void clear() {
        frameUpdateTime.clear();
        autoTapTime.clear();
    }

    /**
     * @param TestNum description
     * @return void
     * @throws null
     * @description: printAvgTime
     * @date 2023/3/2 09:55
     */
    public void printAvgTime(int TestNum) {
        long sum = 0;
        for (int i = 0; i < autoTapTime.size(); i++) {
            sum += (frameUpdateTime.get(i) - autoTapTime.get(i));
        }
        float avgtime = (float) sum / TestNum;
        Log.d("TWT", "printAvgTime: 平均自动点击响应时间为：" + avgtime);
    }

    /**
     * @param TestNum description
     * @return float
     * @throws null
     * @description: getAvgTime
     * @date 2023/3/2 09:55
     */
    public float getAvgTime(int TestNum) {
        long sum = 0;
        for (int i = 0; i < autoTapTime.size(); i++) {
            sum += (frameUpdateTime.get(i) - autoTapTime.get(i));
        }
        float avgtime = (float) sum / TestNum;
        Log.e("TWT", "getAvgTime:+frameUpdateTime.size " + frameUpdateTime.size());
        Log.e("TWT", "getAvgTime:+autoTapTime.size " + autoTapTime.size());
        return avgtime;
    }

    /**
     * @return int
     * @throws null
     * @description: getDetectNum
     * @date 2023/3/2 09:55
     */
    public int getDetectNum() {
        return frameUpdateTime.size();
    }
}
