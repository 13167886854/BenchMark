/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.benchmark.data.Admin;
import com.example.benchmark.data.IpPort;
import com.example.benchmark.data.YinHuaData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import okhttp3.Call;

/**
 * ScoreUtil
 *
 * @version 1.0
 * @since 2023/3/7 17:28
 */
public class ScoreUtil {
    private static final String TAG = "ScoreUtil";

    private static int responseNum;

    private static float averageFPS;
    private static float frameShakeRate;
    private static float lowFrameRate;
    private static float frameInterval;
    private static float jankCount;
    private static float stutterRate;

    private static float averFpsScore;
    private static float frameShakeScore;
    private static float lowFrameScore;
    private static float frameIntervalScore;
    private static float jankCountScore;
    private static float stutterRateScore;

    private static float psnr;
    private static float ssim;
    private static float pesq;

    private static BigDecimal averageStartScore;
    private static BigDecimal averageQuitScore;
    private static BigDecimal startSuccessScore;

    private static BigDecimal resolutionValue;
    private static BigDecimal resolutionScore;
    private static BigDecimal maxDiffValueScore;

    /**
     * calcAndSaveCPUScores
     *
     * @param cpuCores description
     * @return void
     * @throws null
     * @date 2023/3/8 09:48
     */
    public static void calcAndSaveCPUScores(
            int cpuCores
    ) {
        // 保存CPU结果
        CacheUtil.put(CacheConst.KEY_CPU_CORES, cpuCores);

        if (cpuCores != 0) {
            OkHttpUtils.builder().url(CacheConst.GLOBAL_IP + "/cpu/save")
                    .addParam("adminName", Admin.getInstance().getAdminName())
                    .addParam("platformName", Admin.getInstance().getPlatformName())
                    .addParam("cores", cpuCores + "")
                    .addParam("time", Admin.getInstance().getTestTime())
                    .addParam("ip", IpPort.getInstance().getIp())
                    .addParam("port", IpPort.getInstance().getPort())
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .post(true)
                    .async(new OkHttpUtils.ICallBack() {
                        @Override
                        public void onSuccessful(Call call, String data) {
                            Log.d(TAG, "onSuccessful: cpu---"
                                    + data);
                        }

                        @Override
                        public void onFailure(Call call, String errorMsg) {
                            Log.d(TAG, "onFailure: cpu---" + errorMsg);
                        }
                    });
        }
    }

    /**
     * calcAndSaveGPUScores
     *
     * @param gpuVendor  description
     * @param gpuRender  description
     * @param gpuVersion description
     * @return void
     * @throws null
     * @date 2023/3/8 11:10
     */
    public static void calcAndSaveGPUScores(
            String gpuVendor,
            String gpuRender,
            String gpuVersion
    ) {
        // 保存GPU结果
        CacheUtil.put(CacheConst.KEY_GPU_VENDOR, gpuVendor);
        CacheUtil.put(CacheConst.KEY_GPU_RENDER, gpuRender);
        CacheUtil.put(CacheConst.KEY_GPU_VERSION, gpuVersion);
        if (gpuVendor != null && gpuRender != null && gpuVersion != null) {
            OkHttpUtils.builder().url(CacheConst.GLOBAL_IP + "/gpu/save")
                    .addParam("adminName", Admin.getInstance().getAdminName())
                    .addParam("platformName", Admin.getInstance().getPlatformName())
                    .addParam("time", Admin.getInstance().getTestTime())
                    .addParam("ip", IpPort.getInstance().getIp())
                    .addParam("port", IpPort.getInstance().getPort())
                    .addParam("gpuVendor", gpuVendor)
                    .addParam("gpuRender", gpuRender)
                    .addParam("gpuVersion", gpuVersion)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .post(true)
                    .async(new OkHttpUtils.ICallBack() {
                        @Override
                        public void onSuccessful(Call call, String data) {
                            Log.d(TAG, "onSuccessful: gpu---"
                                    + data);
                        }

                        @Override
                        public void onFailure(Call call, String errorMsg) {
                            Log.d(TAG, "onFailure: gpu---" + errorMsg);
                        }
                    });
        }
    }

    /**
     * calcAndSaveRAMScores
     *
     * @param availableRAM description
     * @param totalRAM     description
     * @return void
     * @throws null
     * @date 2023/3/8 11:10
     */
    public static void calcAndSaveRAMScores(
            String availableRAM,
            String totalRAM
    ) {
        // 保存RAM结果
        CacheUtil.put(CacheConst.KEY_AVAILABLE_RAM, availableRAM);
        CacheUtil.put(CacheConst.KEY_TOTAL_RAM, totalRAM);

        if (availableRAM != null && totalRAM != null) {
            OkHttpUtils.builder().url(CacheConst.GLOBAL_IP + "/ram/save")
                    .addParam("adminName", Admin.getInstance().getAdminName())
                    .addParam("platformName", Admin.getInstance().getPlatformName())
                    .addParam("time", Admin.getInstance().getTestTime())
                    .addParam("ip", IpPort.getInstance().getIp())
                    .addParam("port", IpPort.getInstance().getPort())
                    .addParam("availableRam", availableRAM)
                    .addParam("totalRam", totalRAM)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .post(true)
                    .async(new OkHttpUtils.ICallBack() {
                        @Override
                        public void onSuccessful(Call call, String data) {
                            Log.d(TAG, "onSuccessful: ram---"
                                    + data);
                        }

                        @Override
                        public void onFailure(Call call, String errorMsg) {
                            Log.d(TAG, "onFailure: ram---" + errorMsg);
                        }
                    });
        }
    }

    /**
     * calcAndSaveROMScores
     *
     * @param availableROM description
     * @param totalROM     description
     * @return void
     * @throws null
     * @date 2023/3/8 11:10
     */
    public static void calcAndSaveROMScores(
            String availableROM,
            String totalROM
    ) {
        // 保存ROM结果
        CacheUtil.put(CacheConst.KEY_AVAILABLE_STORAGE, availableROM);
        CacheUtil.put(CacheConst.KEY_TOTAL_STORAGE, totalROM);
        if (availableROM != null && totalROM != null) {
            OkHttpUtils.builder().url(CacheConst.GLOBAL_IP + "/rom/save")
                    .addParam("adminName", Admin.getInstance().getAdminName())
                    .addParam("platformName", Admin.getInstance().getPlatformName())
                    .addParam("time", Admin.getInstance().getTestTime())
                    .addParam("ip", IpPort.getInstance().getIp())
                    .addParam("port", IpPort.getInstance().getPort())
                    .addParam("availableRom", availableROM)
                    .addParam("totalRom", totalROM)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .post(true)
                    .async(new OkHttpUtils.ICallBack() {
                        @Override
                        public void onSuccessful(Call call, String data) {
                            Log.d(TAG, "onSuccessful: rom---"
                                    + data);
                        }

                        @Override
                        public void onFailure(Call call, String errorMsg) {
                            Log.d(TAG, "onFailure: rom---" + errorMsg);
                        }
                    });
        }
    }

    /**
     * calcAndSaveFluencyScores
     *
     * @param info    averageFPS,frameShakeRate,lowFrameRate,frameInterval,jankCount,stutterRate
     * @param eachFps description
     * @return void
     * @throws null
     * @date 2023/3/8 11:10
     */
    public static void calcAndSaveFluencyScores(
            float[] info,
            String eachFps
    ) {
        initInfo(info);

        // 计算流畅性分数
        BigDecimal[] infoB = new BigDecimal[6];
        infoB[0] = BigDecimal.valueOf(info[0]);
        infoB[1] = BigDecimal.valueOf(info[1]);
        infoB[2] = BigDecimal.valueOf(info[2]);
        infoB[3] = BigDecimal.valueOf(info[3]);
        infoB[4] = BigDecimal.valueOf(info[4]);
        infoB[5] = BigDecimal.valueOf(info[5]);
        calculate(infoB);
        int fluencyScore = (BigDecimal.valueOf(averFpsScore).add(BigDecimal.valueOf(frameShakeScore))
                .add(BigDecimal.valueOf(lowFrameScore)).add(BigDecimal.valueOf(frameIntervalScore))
                .add(BigDecimal.valueOf(jankCountScore)).add(BigDecimal.valueOf(stutterRateScore))).intValue();

        // 保存流畅性分数
        CacheUtil.put(CacheConst.KEY_FLUENCY_SCORE, fluencyScore);

        // 判断数据是否为空
        if (fluencyScore != 0) {
            OkHttpUtils.builder().url(CacheConst.GLOBAL_IP + "/fluency/save")
                    .addParam("adminName", Admin.getInstance().getAdminName())
                    .addParam("platformName", Admin.getInstance().getPlatformName())
                    .addParam("time", Admin.getInstance().getTestTime())
                    .addParam("ip", IpPort.getInstance().getIp())
                    .addParam("port", IpPort.getInstance().getPort())
                    .addParam("averageFps", averageFPS + "")
                    .addParam("frameShakeRate", frameShakeRate + "")
                    .addParam("lowFrameRate", lowFrameRate + "")
                    .addParam("jankCount", jankCount + "")
                    .addParam("frameInterval", frameInterval + "")
                    .addParam("stutterRate", stutterRate + "")
                    .addParam("fluencyScore", fluencyScore + "")
                    .addParam("eachFps", eachFps)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .post(true)
                    .async(new OkHttpUtils.ICallBack() {
                        @Override
                        public void onSuccessful(Call call, String data) {
                            Log.d(TAG, "onSuccessful: Fluency---" + data);
                        }

                        @Override
                        public void onFailure(Call call, String errorMsg) {
                            Log.d(TAG, "onFailure: Fluency---" + errorMsg);
                        }
                    });
        }
    }

    private static void calculate(BigDecimal[] infoB) {
        if (infoB[0].compareTo(BigDecimal.valueOf(120)) == 0
                || infoB[0].compareTo(BigDecimal.valueOf(120)) == -1) {
            averFpsScore = BigDecimal.valueOf(100).multiply(infoB[0])
                    .divide(BigDecimal.valueOf(6 * 120), BigDecimal.ROUND_CEILING).floatValue();
        } else {
            averFpsScore = BigDecimal.valueOf(100f).divide(BigDecimal.valueOf(6),
                    BigDecimal.ROUND_CEILING).floatValue();
        }
        if (infoB[1].compareTo(BigDecimal.valueOf(10)) == -1) {
            frameShakeScore = BigDecimal.valueOf(100f).divide(BigDecimal.valueOf(6),
                    BigDecimal.ROUND_CEILING).floatValue();
        } else {
            frameShakeScore = BigDecimal.valueOf(1000).divide(BigDecimal.valueOf(6).multiply(infoB[1]),
                    BigDecimal.ROUND_CEILING).floatValue();
        }

        lowFrameScore = BigDecimal.valueOf(100f).multiply(BigDecimal.valueOf(1f)
                .subtract(BigDecimal.valueOf(lowFrameRate))).divide(BigDecimal.valueOf(6),
                BigDecimal.ROUND_CEILING).floatValue();
        if (infoB[3].compareTo(BigDecimal.valueOf(50)) == -1) {
            frameIntervalScore = BigDecimal.valueOf(100f).divide(BigDecimal.valueOf(6),
                    BigDecimal.ROUND_CEILING).floatValue();
        } else {
            frameIntervalScore = BigDecimal.valueOf(5000f).divide(BigDecimal.valueOf(6)
                    .multiply(BigDecimal.valueOf(frameInterval)), BigDecimal.ROUND_CEILING).floatValue();
        }
        jankCountScore = BigDecimal.valueOf(100f).divide(BigDecimal.valueOf(6)
                        .multiply(BigDecimal.valueOf(1).add(BigDecimal.valueOf(jankCount))),
                BigDecimal.ROUND_CEILING).floatValue();

        stutterRateScore = BigDecimal.valueOf(100f).divide(BigDecimal.valueOf(6)
                        .multiply(BigDecimal.valueOf(1).subtract(BigDecimal.valueOf(stutterRate))),
                BigDecimal.ROUND_CEILING).floatValue();
    }

    private static void initInfo(float[] info) {
        // 保存流畅性结果
        averageFPS = info[0];
        CacheUtil.put(CacheConst.KEY_AVERAGE_FPS, averageFPS);
        frameShakeRate = info[1];
        CacheUtil.put(CacheConst.KEY_FRAME_SHAKE_RATE, frameShakeRate);
        lowFrameRate = info[2];
        CacheUtil.put(CacheConst.KEY_LOW_FRAME_RATE, lowFrameRate);
        frameInterval = info[3];
        CacheUtil.put(CacheConst.KEY_FRAME_INTERVAL, frameInterval);
        jankCount = info[4];
        CacheUtil.put(CacheConst.KEY_JANK_COUNT, jankCount);
        stutterRate = info[5];
        CacheUtil.put(CacheConst.KEY_STUTTER_RATE, stutterRate);
    }

    /**
     * getAverageFPS
     *
     * @return float
     * @throws null
     * @date 2023/3/8 09:51
     */
    public static float getAverageFPS() {
        return CacheUtil.getFloat(CacheConst.KEY_AVERAGE_FPS);
    }

    /**
     * getFrameShakeRate
     *
     * @return float
     * @throws null
     * @date 2023/3/8 09:51
     */
    public static float getFrameShakeRate() {
        return CacheUtil.getFloat(CacheConst.KEY_FRAME_SHAKE_RATE);
    }

    /**
     * getLowFrameRate
     *
     * @return float
     * @throws null
     * @date 2023/3/8 09:51
     */
    public static float getLowFrameRate() {
        return CacheUtil.getFloat(CacheConst.KEY_LOW_FRAME_RATE);
    }

    /**
     * getFrameInterval
     *
     * @return float
     * @throws null
     * @date 2023/3/8 09:51
     */
    public static float getFrameInterval() {
        return CacheUtil.getFloat(CacheConst.KEY_FRAME_INTERVAL);
    }

    /**
     * getJankCount
     *
     * @return float
     * @throws null
     * @date 2023/3/8 09:51
     */
    public static float getJankCount() {
        return CacheUtil.getFloat(CacheConst.KEY_JANK_COUNT);
    }

    /**
     * getStutterRate
     *
     * @return float
     * @throws null
     * @date 2023/3/8 09:51
     */
    public static float getStutterRate() {
        return CacheUtil.getFloat(CacheConst.KEY_STUTTER_RATE);
    }

    /**
     * getFluencyScore
     *
     * @return int
     * @throws null
     * @date 2023/3/8 09:51
     */
    public static int getFluencyScore() {
        return CacheUtil.getInt(CacheConst.KEY_FLUENCY_SCORE);
    }

    /**
     * calcAndSaveStabilityScores
     *
     * @param startSuccessRate description
     * @param averageStartTime description
     * @param averageQuitTime  description
     * @return void
     * @throws null
     * @date 2023/3/8 09:51
     */
    public static void calcAndSaveStabilityScores(
            float startSuccessRate,
            float averageStartTime,
            float averageQuitTime
    ) {
        // 保存稳定性结果
        CacheUtil.put(CacheConst.KEY_START_SUCCESS_RATE, startSuccessRate);
        CacheUtil.put(CacheConst.KEY_AVERAGE_START_TIME, averageStartTime);
        CacheUtil.put(CacheConst.KEY_AVERAGE_QUIT_TIME, averageQuitTime);

        // 计算稳定性分数
        float startSuccessRateDivide100 = BigDecimal.valueOf(startSuccessRate)
                .divide(BigDecimal.valueOf(100), BigDecimal.ROUND_CEILING).floatValue();
        calculate2(averageStartTime, averageQuitTime, startSuccessRateDivide100);
        int stabilityScores = (startSuccessScore.add(averageStartScore.add(averageQuitScore))).intValue();

        // 保存稳定性分数
        CacheUtil.put(CacheConst.KEY_STABILITY_SCORE, stabilityScores);
        if (stabilityScores != 0) {
            OkHttpUtils.builder().url(CacheConst.GLOBAL_IP + "/stability/save")
                    .addParam("adminName", Admin.getInstance().getAdminName())
                    .addParam("platformName", Admin.getInstance().getPlatformName())
                    .addParam("time", Admin.getInstance().getTestTime())
                    .addParam("ip", IpPort.getInstance().getIp())
                    .addParam("port", IpPort.getInstance().getPort())
                    .addParam("startSuccessRate", startSuccessRateDivide100 + "")
                    .addParam("averageStartTime", averageStartTime + "")
                    .addParam("averageQuitTime", averageQuitTime + "")
                    .addParam("stabilityScore", stabilityScores + "")
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .post(true)
                    .async(new OkHttpUtils.ICallBack() {
                        @Override
                        public void onSuccessful(Call call, String data) {
                            Log.d(TAG, "onSuccessful: Stability---" + data);
                        }

                        @Override
                        public void onFailure(Call call, String errorMsg) {
                            Log.d(TAG, "onFailure: Stability---" + errorMsg);
                        }
                    });
        }
    }

    private static void calculate2(float averageStartTime, float averageQuitTime, float startSuccessRateDivide100) {
        startSuccessScore = (BigDecimal.valueOf(100f).multiply(BigDecimal.valueOf(startSuccessRateDivide100)))
                .divide(BigDecimal.valueOf(3), BigDecimal.ROUND_CEILING);

        if (BigDecimal.valueOf(averageStartTime).compareTo(BigDecimal.valueOf(50)) == -1) {
            averageStartScore = BigDecimal.valueOf(100f).divide(BigDecimal.valueOf(3), BigDecimal.ROUND_CEILING);
        } else {
            averageStartScore = BigDecimal.valueOf(500f).divide(BigDecimal.valueOf(3)
                    .multiply(BigDecimal.valueOf(averageStartTime)), BigDecimal.ROUND_CEILING);
        }
        if (BigDecimal.valueOf(averageQuitTime).compareTo(BigDecimal.valueOf(50)) == -1) {
            averageQuitScore = BigDecimal.valueOf(100f).divide(BigDecimal.valueOf(3),
                    BigDecimal.ROUND_CEILING);
        } else {
            averageQuitScore = BigDecimal.valueOf(500f).divide(BigDecimal.valueOf(3)
                    .multiply(BigDecimal.valueOf(averageQuitTime)), BigDecimal.ROUND_CEILING);
        }
    }

    /**
     * getStartSuccessRate
     *
     * @return float
     * @throws null
     * @date 2023/3/8 09:52
     */
    public static float getStartSuccessRate() {
        return CacheUtil.getFloat(CacheConst.KEY_START_SUCCESS_RATE);
    }

    /**
     * getAverageStartTime
     *
     * @return float
     * @throws null
     * @date 2023/3/8 09:52
     */
    public static float getAverageStartTime() {
        return CacheUtil.getFloat(CacheConst.KEY_AVERAGE_START_TIME);
    }

    /**
     * getAverageQuitTime
     *
     * @return float
     * @throws null
     * @date 2023/3/8 09:52
     */
    public static float getAverageQuitTime() {
        return CacheUtil.getFloat(CacheConst.KEY_AVERAGE_QUIT_TIME);
    }

    /**
     * getStabilityScore
     *
     * @return int
     * @throws null
     * @date 2023/3/8 09:52
     */
    public static int getStabilityScore() {
        return CacheUtil.getInt(CacheConst.KEY_STABILITY_SCORE);
    }

    /**
     * calaAndSaveGameTouchScores
     *
     * @param testNum description
     * @param time    description
     * @date 2023/3/8 09:52
     */
    public static void calaAndSaveGameTouchScores(int testNum, float time) {
        // 正确率
        float averageAccuracy = (float) (testNum) / GameTouchUtil.TEST_NUM;
        Log.e("TWT", "GameTouchUtil.testNum: " + GameTouchUtil.TEST_NUM);
        Log.e("TWT", "testNum: " + testNum);
        Log.e("TWT", "time: " + time);

        BigDecimal averAccuracyScore = (BigDecimal.valueOf(100f).multiply(BigDecimal.valueOf(averageAccuracy)))
                .divide(BigDecimal.valueOf(2), BigDecimal.ROUND_CEILING);
        BigDecimal responseTimeScore;
        if (BigDecimal.valueOf(time).compareTo(BigDecimal.valueOf(50)) == -1) {
            responseTimeScore = BigDecimal.valueOf(50);
        } else {
            responseTimeScore = BigDecimal.valueOf(5000f)
                    .divide(BigDecimal.valueOf(time).multiply(BigDecimal.valueOf(2)), BigDecimal.ROUND_CEILING);
        }
        int touchScore = (averAccuracyScore.add(responseTimeScore)).intValue();

        // 保存触控体验分数
        CacheUtil.put(CacheConst.KEY_TOUCH_SCORE, touchScore);
        averageAccuracy *= 100;
        CacheUtil.put(CacheConst.KEY_AVERAGE_ACCURACY, averageAccuracy);
        CacheUtil.put(CacheConst.KEY_RESPONSE_TIME, time);
        Log.e("TWT", "KEY_AVERAGE_ACCURACY: " + averageAccuracy);
        Log.e("TWT", "KEY_RESPONSE_TIME: " + time);
    }

    /**
     * calcAndSaveTouchScores
     *
     * @param cloudDownTimeList  description
     * @param cloudSpendTimeList description
     * @return void
     * @throws null
     * @date 2023/3/8 09:52
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void calcAndSaveTouchScores(
            String cloudDownTimeList, String cloudSpendTimeList) {
        // 没有进行触控测试 没有数据时 直接返回
        if (cloudDownTimeList == "null" || cloudSpendTimeList == "null") {
            return;
        }
        String cloudDownTimeListSub =
                cloudDownTimeList.substring(1, cloudDownTimeList.length() - 1);
        String cloudSpendTimeListSub =
                cloudSpendTimeList.substring(1, cloudSpendTimeList.length() - 1);
        String[] cloudDownTimeListArr = cloudDownTimeListSub.split(",");
        String[] cloudSpendTimeListArr = cloudSpendTimeListSub.split(",");
        for (int i = 0; i < cloudDownTimeListArr.length; i++) {
            cloudDownTimeListArr[i] =
                    cloudDownTimeListArr[i].substring(1, cloudDownTimeListArr[i].length() - 1);
        }
        for (int i = 0; i < cloudSpendTimeListArr.length; i++) {
            cloudSpendTimeListArr[i] =
                    cloudSpendTimeListArr[i].substring(1, cloudSpendTimeListArr[i].length() - 1);
        }

        // 判断测试平台
        String checkPlatform = CacheUtil.getString(CacheConst.KEY_PLATFORM_NAME);
        TreeSet<String> localTapTimes =
                (TreeSet<String>) CacheUtil.getSet(CacheConst.KEY_AUTO_TAP_TIMES);
        ArrayList<Long> longs = new ArrayList<>();

        // 响应次数
        responseNum = 0;
        responseNum = getResponseNum1(cloudDownTimeListArr, cloudSpendTimeListArr, longs);
        responseNum = getResponseNum2(cloudDownTimeListArr, cloudSpendTimeListArr, longs);
        extracted(longs);

        // 平均触控时延
        long allResponseTime = 0L;
        for (Long aLong : longs) {
            allResponseTime += aLong;
        }
        BigDecimal avgResponseTime = BigDecimal.valueOf(allResponseTime)
                .divide(BigDecimal.valueOf(longs.size()), BigDecimal.ROUND_CEILING);
        // 正确率
        BigDecimal averageAccuracy = (BigDecimal.valueOf(responseNum).subtract(BigDecimal.valueOf(4)))
                .divide(BigDecimal.valueOf(longs.size()), BigDecimal.ROUND_CEILING);
        BigDecimal averAccuracyScore = BigDecimal.valueOf(100f).multiply(averageAccuracy)
                .divide(BigDecimal.valueOf(2), BigDecimal.ROUND_CEILING);
        BigDecimal responseTimeScore;
        if (avgResponseTime.compareTo(BigDecimal.valueOf(50)) == -1) {
            responseTimeScore = BigDecimal.valueOf(50);
        } else {
            responseTimeScore = BigDecimal.valueOf(5000f).divide((BigDecimal.valueOf(2)
                    .multiply(avgResponseTime)), BigDecimal.ROUND_CEILING);
        }
        int touchScore = (averAccuracyScore.add(responseTimeScore)).intValue();
        // 保存触控体验分数
        CacheUtil.put(CacheConst.KEY_TOUCH_SCORE, touchScore);
        averageAccuracy = averageAccuracy.scaleByPowerOfTen(2);
        CacheUtil.put(CacheConst.KEY_AVERAGE_ACCURACY, (Set<String>) averageAccuracy);
        CacheUtil.put(CacheConst.KEY_RESPONSE_TIME, (Set<String>) avgResponseTime);
        extracted2(avgResponseTime, averageAccuracy, touchScore);
    }

    private static void extracted2(BigDecimal avgResponseTime, BigDecimal averageAccuracy, int touchScore) {
        if (avgResponseTime.add(averageAccuracy).intValue() != 0.0f) {
            OkHttpUtils.builder().url(CacheConst.GLOBAL_IP + "/touch/save")
                    .addParam("adminName", Admin.getInstance().getAdminName())
                    .addParam("platformName", Admin.getInstance().getPlatformName())
                    .addParam("time", Admin.getInstance().getTestTime())
                    .addParam("ip", IpPort.getInstance().getIp())
                    .addParam("port", IpPort.getInstance().getPort())
                    .addParam("touchTimeDelay", avgResponseTime + "ms")
                    .addParam("touchAccuracy", averageAccuracy + "%")
                    .addParam("touchScore", touchScore + "")
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .post(true)
                    .async(new OkHttpUtils.ICallBack() {
                        @Override
                        public void onSuccessful(Call call, String data) {
                            Log.d(TAG, "onSuccessful: Touch---" + data);
                        }

                        @Override
                        public void onFailure(Call call, String errorMsg) {
                            Log.d(TAG, "onFailure: Touch---" + errorMsg);
                        }
                    });
        }
    }

    private static void extracted(ArrayList<Long> longs) {
        // 去掉两个最高延时，去掉两个最低延时，然后求平均值
        longs.remove(longs.size() - 1);
        longs.remove(longs.size() - 1);
        longs.remove(0);
        longs.remove(0);
        Log.d("zzl", "calcAndSaveTouchScores: longs==>" + longs);
    }

    private static int getResponseNum2(String[] cloudDownTimeListArr,
                                        String[] cloudSpendTimeListArr, ArrayList<Long> longs) {
        long responseTime6 = (Long.parseLong(cloudDownTimeListArr[6]) - CacheUtil.getLong("tapTimeOnLocal7")
                + Long.parseLong(cloudSpendTimeListArr[6])) * 2;
        if (responseTime6 != 0L) {
            responseNum++;
            longs.add(responseTime6);
        }
        long responseTime7 = (Long.parseLong(cloudDownTimeListArr[7]) - CacheUtil.getLong("tapTimeOnLocal8")
                + Long.parseLong(cloudSpendTimeListArr[7])) * 2;
        if (responseTime7 != 0L) {
            responseNum++;
            longs.add(responseTime7);
        }
        long responseTime8 = (Long.parseLong(cloudDownTimeListArr[8]) - CacheUtil.getLong("tapTimeOnLocal9")
                + Long.parseLong(cloudSpendTimeListArr[8])) * 2;
        if (responseTime8 != 0L) {
            responseNum++;
            longs.add(responseTime8);
        }
        long responseTime9 = (Long.parseLong(cloudDownTimeListArr[9]) - CacheUtil.getLong("tapTimeOnLocal10")
                + Long.parseLong(cloudSpendTimeListArr[9])) * 2;
        if (responseTime9 != 0L) {
            responseNum++;
            longs.add(responseTime9);
        }
        long responseTime10 = (Long.parseLong(cloudDownTimeListArr[10]) - CacheUtil.getLong("tapTimeOnLocal11")
                + Long.parseLong(cloudSpendTimeListArr[10])) * 2;
        if (responseTime10 != 0L) {
            responseNum++;
            longs.add(responseTime10);
        }
        longs.sort(Long::compareTo);
        Log.d("zzl", "calcAndSaveTouchScores: longs==>" + longs);
        return responseNum;
    }

    private static int getResponseNum1(String[] cloudDownTimeListArr,
                                        String[] cloudSpendTimeListArr, ArrayList<Long> longs) {
        long responseTime0 = (Long.parseLong(cloudDownTimeListArr[0]) - CacheUtil.getLong("tapTimeOnLocal1")
                + Long.parseLong(cloudSpendTimeListArr[0])) * 2;
        if (responseTime0 != 0L) {
            responseNum++;
            longs.add(responseTime0);
        }
        long responseTime1 = (Long.parseLong(cloudDownTimeListArr[1]) - CacheUtil.getLong("tapTimeOnLocal2")
                + Long.parseLong(cloudSpendTimeListArr[1])) * 2;
        if (responseTime1 != 0L) {
            responseNum++;
            longs.add(responseTime1);
        }
        long responseTime2 = (Long.parseLong(cloudDownTimeListArr[2]) - CacheUtil.getLong("tapTimeOnLocal3")
                + Long.parseLong(cloudSpendTimeListArr[2])) * 2;
        if (responseTime2 != 0L) {
            responseNum++;
            longs.add(responseTime2);
        }
        long responseTime3 = (Long.parseLong(cloudDownTimeListArr[3]) - CacheUtil.getLong("tapTimeOnLocal4")
                + Long.parseLong(cloudSpendTimeListArr[3])) * 2;
        if (responseTime3 != 0L) {
            responseNum++;
            longs.add(responseTime3);
        }
        long responseTime4 = (Long.parseLong(cloudDownTimeListArr[4]) - CacheUtil.getLong("tapTimeOnLocal5")
                + Long.parseLong(cloudSpendTimeListArr[4])) * 2;
        if (responseTime4 != 0L) {
            responseNum++;
            longs.add(responseTime4);
        }
        long responseTime5 = (Long.parseLong(cloudDownTimeListArr[5]) - CacheUtil.getLong("tapTimeOnLocal6")
                + Long.parseLong(cloudSpendTimeListArr[5])) * 2;
        if (responseTime5 != 0L) {
            responseNum++;
            longs.add(responseTime5);
        }
        return responseNum;
    }

    /**
     * getAverageAccuracy
     *
     * @return float
     * @throws null
     * @date 2023/3/8 09:54
     */
    public static float getAverageAccuracy() {
        return CacheUtil.getFloat(CacheConst.KEY_AVERAGE_ACCURACY);
    }

    /**
     * getResponseTime
     *
     * @return float
     * @throws null
     * @date 2023/3/8 09:54
     */
    public static float getResponseTime() {
        return CacheUtil.getFloat(CacheConst.KEY_RESPONSE_TIME);
    }

    /**
     * getTouchScore
     *
     * @return int
     * @throws null
     * @date 2023/3/8 09:54
     */
    public static int getTouchScore() {
        return CacheUtil.getInt(CacheConst.KEY_TOUCH_SCORE);
    }

    /**
     * calcAndSaveSoundFrameScores
     *
     * @param resolution   description
     * @param maxDiffValue description
     * @return void
     * @throws null
     * @date 2023/3/8 09:54
     */
    public static void calcAndSaveSoundFrameScores(String resolution, float maxDiffValue) {
        if (extracted1(resolution, maxDiffValue)) {
            return;
        }
        extracted2(resolution, maxDiffValue);
        BigDecimal temp = BigDecimal.valueOf(psnr).divide(BigDecimal.valueOf(40f),
                BigDecimal.ROUND_CEILING).add(BigDecimal.valueOf(ssim));
        BigDecimal d3 = BigDecimal.valueOf(12.5f).multiply(temp);
        BigDecimal d4 = BigDecimal.valueOf(pesq).multiply(BigDecimal.valueOf(100f))
                .divide(BigDecimal.valueOf(18f), BigDecimal.ROUND_CEILING);
        int soundFrameScore = (resolutionScore.add(maxDiffValueScore.add(d3.add(d4)))).intValue();

        // 保存音画质量分数
        CacheUtil.put(CacheConst.KEY_SOUND_FRAME_SCORE, soundFrameScore);
        if (YinHuaData.getInstance().getPesq() != null
                && YinHuaData.getInstance().getSsim() != null && YinHuaData.getInstance().getPsnr() != null) {
            OkHttpUtils.builder().url(CacheConst.GLOBAL_IP + "/AudioVideo/save")
                    .addParam("adminName", Admin.getInstance().getAdminName())
                    .addParam("platformName", Admin.getInstance().getPlatformName())
                    .addParam("time", Admin.getInstance().getTestTime())
                    .addParam("ip", IpPort.getInstance().getIp())
                    .addParam("port", IpPort.getInstance().getPort())
                    .addParam("resolution", resolution + "")
                    .addParam("maxDiffValue", maxDiffValue + "")
                    .addParam("pesq", pesq + "")
                    .addParam("psnr", psnr + "")
                    .addParam("ssim", ssim + "")
                    .addParam("qualityScore", soundFrameScore + "")
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .post(true)
                    .async(new OkHttpUtils.ICallBack() {
                        @Override
                        public void onSuccessful(Call call, String data) {
                            Log.d(TAG, "onSuccessful: AudioVideo---" + data);
                        }

                        @Override
                        public void onFailure(Call call, String errorMsg) {
                            Log.d(TAG, "onFailure: AudioVideo---" + errorMsg);
                        }
                    });
        }
    }

    private static void extracted2(String resolution, float maxDiffValue) {
        psnr = Float.parseFloat(YinHuaData.getInstance().getPsnr());
        ssim = Float.parseFloat(YinHuaData.getInstance().getSsim());
        pesq = Float.parseFloat(YinHuaData.getInstance().getPesq());
        extracted(resolution, maxDiffValue);

        // 计算音画质量分数
        String[] resolutionArray = resolution.split("X");
        resolutionValue = new BigDecimal(Integer.parseInt(resolutionArray[0])
                * Integer.parseInt(resolutionArray[1]));
        resolutionScore = BigDecimal.valueOf(100f).multiply(resolutionValue)
                .divide(BigDecimal.valueOf(4 * 1920 * 1080), BigDecimal.ROUND_CEILING);
        if (BigDecimal.valueOf(maxDiffValue).compareTo(BigDecimal.valueOf(50)) == -1) {
            maxDiffValueScore = BigDecimal.valueOf(50);
        } else {
            maxDiffValueScore = BigDecimal.valueOf(1250f)
                    .divide(BigDecimal.valueOf(maxDiffValue), BigDecimal.ROUND_CEILING);
        }
        if (BigDecimal.valueOf(psnr).compareTo(BigDecimal.valueOf(40)) == 1) {
            psnr = 40f;
        }
    }

    private static boolean extracted1(String resolution, float maxDiffValue) {
        if (YinHuaData.getInstance().getPesq() == null || YinHuaData.getInstance().getSsim() == null
                || YinHuaData.getInstance().getPsnr() == null) {
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    calcAndSaveSoundFrameScores(resolution, maxDiffValue);
                    timer.cancel();
                }
            };
            timer.schedule(task, 5000);
            return true;
        }
        return false;
    }

    private static void extracted(String resolution, float maxDiffValue) {
        // 保存音画质量结果
        CacheUtil.put(CacheConst.KEY_RESOLUTION, resolution);
        CacheUtil.put(CacheConst.KEY_MAX_DIFF_VALUE, maxDiffValue);
        CacheUtil.put(CacheConst.KEY_PESQ, YinHuaData.getInstance().getPesq());
        CacheUtil.put(CacheConst.KEY_PSNR, YinHuaData.getInstance().getPsnr());
        CacheUtil.put(CacheConst.KEY_SSIM, YinHuaData.getInstance().getSsim());
    }

    /**
     * getResolution
     *
     * @return java.lang.String
     * @throws null
     * @date 2023/3/8 09:54
     */
    public static String getResolution() {
        return CacheUtil.getString(CacheConst.KEY_RESOLUTION);
    }

    /**
     * getMaxDiffValue
     *
     * @return float
     * @throws null
     * @date 2023/3/8 09:54
     */
    public static float getMaxDiffValue() {
        return CacheUtil.getFloat(CacheConst.KEY_MAX_DIFF_VALUE);
    }

    /**
     * getSoundFrameScore
     *
     * @return int
     * @throws null
     * @date 2023/3/8 09:54
     */
    public static int getSoundFrameScore() {
        return CacheUtil.getInt(CacheConst.KEY_SOUND_FRAME_SCORE);
    }
}
