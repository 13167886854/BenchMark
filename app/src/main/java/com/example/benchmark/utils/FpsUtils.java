package com.example.benchmark.utils;

import android.os.Handler;

import java.util.ArrayList;

public class FpsUtils {
    // 监听时的刷新时间间隔,设置为1S
    public static final long FPS_INTERVAL_TIME = 1000L;

    // 低帧判断  fps<25判断为低帧
    public static final double LOW_FRAME_TIME = 1000.0 / 24; //每帧传输时间超过fps25的判断为低帧

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
    private static FpsUtils fpsUtils = new FpsUtils();
    private boolean isFpsOpen = false;
    public static Handler mainHandler = new Handler();

    private FpsUtils() {
    }

    public static FpsUtils getFpsUtils() {
        if (fpsUtils == null) {
            fpsUtils = new FpsUtils();
        }
        return fpsUtils;
    }

    // 开始监听
    public void startMonitor(Runnable runnable) {
        init();
        startTime = System.currentTimeMillis();
        this.lastTime = System.currentTimeMillis();
        if (!isFpsOpen) {
            isFpsOpen = true;
            mainHandler.postDelayed(runnable, FPS_INTERVAL_TIME);
        }
    }

    // 结束监听
    public void stopMonitor(Runnable runnable) {
        endTime = System.currentTimeMillis();
        count = 0;
        mainHandler.removeCallbacks(runnable);
        isFpsOpen = false;
    }

    // 初始化监听器
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

    // 每一帧刷新时的更新操作
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

    // 获取单位时间帧计数
    public int getCount() {
        return this.count;
    }

    public int getTotalCount() {
        return this.totalCount;
    }

    // 单位时间结束更新操作
    public void updateBeforeGetInfo() {
        countPerSecond.add(count);

    }

    public void updateAfterGetInfo() {
        // fps重新计数
        count = 0;
    }

    // 获取测试时间间隔
    public long getDurationTime() {
        endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    // 获取平均帧率
    public double getAvergeFps() {
        return (double) totalCount * 1000 / getDurationTime();
    }

    // 获取帧抖动率
    public double getFrameShakingRate() {
        double fps = getAvergeFps();
        double res = 0d;
        for (int i = 0; i < countPerSecond.size(); i++) {
            res += Math.pow(countPerSecond.get(i) - fps, 2);
        }
        res /= countPerSecond.size();
        return res;
    }

    // 获取帧间隔
    public double getFrameIntervalTime() {
        return (double) getDurationTime() / (totalCount - 1);
    }

    // 获取低帧数
    public int getLowCount() {
        return lowCount;
    }

    // 获取低帧率
    public double getLowFrameRate() {
        return (double) lowCount / totalCount;
    }

    // 判断是否为jank帧  (PerfDog Jank计算方法)
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
        if ((time > LOW_FRAME_TIME * 2) && (time > (double) (t1 + t2 + t3) * 2 / 3)) {
            shutterTime += time;
            return true;
        } else {
            return false;
        }
    }

    public int getJankCount() {
        return jankCount;
    }

    public double getJankRate() {
        return (double) jankCount / totalCount;
    }

    public long getIntervalTime() {
        return max(last3FrameTimes);
    }

    public static int max(ArrayList<Integer> list) {
        int maxList = list.get(0);
        if (list == null || list.isEmpty()){
            return 0;
        }
        for (Integer integer : list) {
            maxList = maxList > integer ? maxList : integer;
        }
        return maxList;
    }

    public long getShutterTime() {
        return shutterTime;
    }

    public double getShtutterRate() {
        return (double) shutterTime / getDurationTime();
    }

    public int getSizeOfCountArray() {
        return countPerSecond.size();
    }
}
