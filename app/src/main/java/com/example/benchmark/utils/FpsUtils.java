/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

import android.os.Handler;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * FpsUtils
 *
 * @version 1.0
 * @since 2023/3/7 17:24
 */
public class FpsUtils {
    /**
     * 监听时的刷新时间间隔,设置为1S
     */
    public static final long FPS_INTERVAL_TIME = 1000L;

    /**
     * 低帧判断  fps<25判断为低帧
     */
    public static final double LOW_FRAME_TIME = 41.666; // 每帧传输时间超过fps25的判断为低帧 1000/24

    private static Handler mainHandler = new Handler();

    private static FpsUtils fpsUtils = new FpsUtils();

    // 记录单位时间内帧数
    private int count = 0;

    // 记录总帧数
    private int totalCount = 0;

    // 记录低帧数
    private int lowCount = 0;

    // 记录JANK数
    private int jankCount = 0;

    // 记录卡顿时间
    private long shutterTime = 0L;

    // 存储每个单位时间帧数
    private ArrayList<Integer> countPerSecond = new ArrayList();

    // 记录开始测试时间戳
    private long startTime = 0L;

    // 记录结束时间戳
    private long endTime = 0L;

    // 记录上一帧渲染的时间戳
    private long lastTime = 0L;

    // 记录前3次帧耗时
    private ArrayList<Integer> last3FrameTimes = new ArrayList();

    // 单例模式

    private boolean isFpsOpen = false;

    private FpsUtils() {
    }

    /**
     * getFpsUtils
     *
     * @return com.example.benchmark.utils.FpsUtils
     * @date 2023/3/8 09:02
     */
    public static FpsUtils getFpsUtils() {
        if (fpsUtils == null) {
            fpsUtils = new FpsUtils();
        }
        return fpsUtils;
    }

    public static Handler getMainHandler() {
        return mainHandler;
    }

    /**
     * startMonitor
     *
     * @param runnable description
     * @date 2023/3/8 16:02
     */
    public void startMonitor(Runnable runnable) {
        init();
        startTime = System.currentTimeMillis();
        this.lastTime = System.currentTimeMillis();
        if (!isFpsOpen) {
            isFpsOpen = true;
            mainHandler.postDelayed(runnable, FPS_INTERVAL_TIME);
        }
    }

    /**
     * stopMonitor
     *
     * @param runnable description
     * @date 2023/3/8 09:02
     */
    public void stopMonitor(Runnable runnable) {
        endTime = System.currentTimeMillis();
        count = 0;
        mainHandler.removeCallbacks(runnable);
        isFpsOpen = false;
    }

    /**
     * init
     *
     * @date 2023/3/8 09:02
     */
    public void init() {
        countPerSecond.clear();
        startTime = 0;
        endTime = 0;
        count = 0;
        totalCount = 0;
        lowCount = 0;
        lastTime = 0;
        jankCount = 0;
        shutterTime = 0;
    }

    /**
     * addFrame
     *
     * @date 2023/3/8 09:02
     */
    public void addFrame() {
        int time = (int) (System.currentTimeMillis() - lastTime);
        this.count++;
        this.totalCount++;

        // 判断是否为低帧
        if (time > LOW_FRAME_TIME) {
            lowCount++;
        }
        // 判断是否为jank帧
        if (isJank(time)) {
            jankCount++;
        }
        // 更新渲染帧时间戳
        this.lastTime = System.currentTimeMillis();
    }

    /**
     * getCount
     *
     * @return int
     * @date 2023/3/8 09:02
     */
    public int getCount() {
        return this.count;
    }

    /**
     * getTotalCount
     *
     * @return int
     * @date 2023/3/8 09:02
     */
    public int getTotalCount() {
        return this.totalCount;
    }

    /**
     * updateBeforeGetInfo
     *
     * @date 2023/3/8 09:03
     */
    public void updateBeforeGetInfo() {
        countPerSecond.add(count);
    }

    /**
     * updateAfterGetInfo
     *
     * @date 2023/3/8 09:03
     */
    public void updateAfterGetInfo() {
        // fps重新计数
        count = 0;
    }

    /**
     * getDurationTime
     *
     * @return long
     * @date 2023/3/9 16:13
     */
    public long getDurationTime() {
        endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    /**
     * getAvergeFps
     *
     * @return double
     * @date 2023/3/8 09:03
     */
    public double getAvergeFps() {
        return (double) totalCount * 1000 / getDurationTime();
    }

    /**
     * getFrameShakingRate
     *
     * @return double
     * @date 2023/3/8 09:03
     */
    public double getFrameShakingRate() {
        double fps = getAvergeFps();
        double res = 0d;
        for (int i = 0; i < countPerSecond.size(); i++) {
            res += Math.pow(countPerSecond.get(i) - fps, 2);
        }
        res /= countPerSecond.size();
        return res;
    }

    /**
     * getFrameIntervalTime
     *
     * @return double
     * @date 2023/3/8 09:03
     */
    public double getFrameIntervalTime() {
        return (double) getDurationTime() / (totalCount - 1);
    }

    /**
     * getLowCount
     *
     * @return int
     * @date 2023/3/8 09:03
     */
    public int getLowCount() {
        return lowCount;
    }

    /**
     * getLowFrameRate
     *
     * @return double
     * @date 2023/3/8 09:03
     */
    public double getLowFrameRate() {
        return (double) lowCount / totalCount;
    }

    /**
     * isJank
     *
     * @param time description
     * @return boolean
     * @date 2023/3/8 09:03
     */
    public boolean isJank(int time) {
        int size = last3FrameTimes.size();
        if (size < 3) {
            last3FrameTimes.add(time);
            return false;
        }
        int t1 = last3FrameTimes.get(size - 1);
        int t2 = last3FrameTimes.get(size - 2);
        int t3 = last3FrameTimes.get(size - 3);
        last3FrameTimes.remove(0);
        last3FrameTimes.add(time);
        BigDecimal total = BigDecimal.valueOf(t1 + t2 + t3);
        BigDecimal res = total.multiply(BigDecimal.valueOf(2)).divide(BigDecimal.valueOf(3), BigDecimal.ROUND_CEILING);
        BigDecimal timeB = BigDecimal.valueOf(time);
        if ((time > LOW_FRAME_TIME * 2) && (timeB.compareTo(res) == 1)) {
            shutterTime += time;
            return true;
        } else {
            return false;
        }
    }

    /**
     * getJankCount
     *
     * @return int
     * @date 2023/3/8 09:03
     */
    public int getJankCount() {
        return jankCount;
    }

    /**
     * getJankRate
     *
     * @return double
     * @date 2023/3/8 09:03
     */
    public double getJankRate() {
        return (double) jankCount / totalCount;
    }

    /**
     * getIntervalTime
     *
     * @return long
     * @date 2023/3/8 09:03
     */
    public long getIntervalTime() {
        return max(last3FrameTimes);
    }

    /**
     * max
     *
     * @param list description
     * @return int
     * @date 2023/3/8 09:04
     */
    public static int max(ArrayList<Integer> list) {
        int maxList = list.get(0);
        if (list == null || list.isEmpty()) {
            return 0;
        }
        for (Integer integer : list) {
            maxList = maxList > integer ? maxList : integer.intValue();
        }
        return maxList;
    }

    /**
     * getShutterTime
     *
     * @return long
     * @date 2023/3/8 09:04
     */
    public long getShutterTime() {
        return shutterTime;
    }

    /**
     * getShtutterRate
     *
     * @return double
     * @date 2023/3/8 09:04
     */
    public double getShtutterRate() {
        return (double) shutterTime / getDurationTime();
    }

    /**
     * getSizeOfCountArray
     *
     * @return int
     * @date 2023/3/8 09:04
     */
    public int getSizeOfCountArray() {
        return countPerSecond.size();
    }
}
