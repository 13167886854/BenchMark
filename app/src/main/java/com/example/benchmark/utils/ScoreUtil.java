package com.example.benchmark.utils;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.benchmark.data.Admin;
import com.example.benchmark.data.IpPort;
import com.example.benchmark.data.YinHuaData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import okhttp3.Call;

/**
 * 计算分数
 */
public class ScoreUtil {
    private static final String TAG = "ScoreUtil";

    public static void calcAndSaveCPUScores(
            int cpuCores
    ) {
        // 保存CPU结果
        CacheUtil.put(CacheConst.KEY_CPU_CORES, cpuCores);

        if (cpuCores != 0) {
            OkHttpUtils.builder().url(CacheConst.GLOBAL_IP + "/cpu/save")
                    .addParam("adminName", Admin.adminName)
                    .addParam("platformName", Admin.platformName)
                    .addParam("cores", cpuCores + "")
                    .addParam("time", Admin.testTime)
                    .addParam("ip", IpPort.ip)
                    .addParam("port", IpPort.port)
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
                    .addParam("adminName", Admin.adminName)
                    .addParam("platformName", Admin.platformName)
                    .addParam("time", Admin.testTime)
                    .addParam("ip", IpPort.ip)
                    .addParam("port", IpPort.port)
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

    public static void calcAndSaveRAMScores(
            String availableRAM,
            String totalRAM
    ) {
        // 保存RAM结果
        CacheUtil.put(CacheConst.KEY_AVAILABLE_RAM, availableRAM);
        CacheUtil.put(CacheConst.KEY_TOTAL_RAM, totalRAM);

        if (availableRAM != null && totalRAM != null) {
            OkHttpUtils.builder().url(CacheConst.GLOBAL_IP + "/ram/save")
                    .addParam("adminName", Admin.adminName)
                    .addParam("platformName", Admin.platformName)
                    .addParam("time", Admin.testTime)
                    .addParam("ip", IpPort.ip)
                    .addParam("port", IpPort.port)
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

    public static void calcAndSaveROMScores(
            String availableROM,
            String totalROM
    ) {
        // 保存ROM结果
        CacheUtil.put(CacheConst.KEY_AVAILABLE_STORAGE, availableROM);
        CacheUtil.put(CacheConst.KEY_TOTAL_STORAGE, totalROM);
        if (availableROM != null && totalROM != null) {
            OkHttpUtils.builder().url(CacheConst.GLOBAL_IP + "/rom/save")
                    .addParam("adminName", Admin.adminName)
                    .addParam("platformName", Admin.platformName)
                    .addParam("time", Admin.testTime)
                    .addParam("ip", IpPort.ip)
                    .addParam("port", IpPort.port)
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

    public static void calcAndSaveFluencyScores(
            float averageFPS,
            float frameShakeRate,
            float lowFrameRate,
            float frameInterval,
            float jankCount,
            float stutterRate,
            String eachFps
    ) {
        // 保存流畅性结果
        CacheUtil.put(CacheConst.KEY_AVERAGE_FPS, averageFPS);
        CacheUtil.put(CacheConst.KEY_FRAME_SHAKE_RATE, frameShakeRate);
        CacheUtil.put(CacheConst.KEY_LOW_FRAME_RATE, lowFrameRate*100);
        CacheUtil.put(CacheConst.KEY_FRAME_INTERVAL, frameInterval);
        CacheUtil.put(CacheConst.KEY_JANK_COUNT, jankCount);
        CacheUtil.put(CacheConst.KEY_STUTTER_RATE, stutterRate*100);

        // 计算流畅性分数
        float averFpsScore = averageFPS <= 120 ? 100f * averageFPS / (6 * 120) : 100f / 6;
        float frameShakeScore = frameShakeRate < 10 ? 100f / 6 : 100f * 10 / (6 * frameShakeRate);
        float lowFrameScore = 100f * (1 - lowFrameRate) / 6;
        float frameIntervalScore = frameInterval < 50 ? 100f / 6 : 100f * 50 / (6 * frameInterval);
        float jankCountScore = 100f / (6 * (1 + jankCount));
        float stutterRateScore = 100f / (6 * (1 - stutterRate));
        int fluencyScore = (int) (averFpsScore + frameShakeScore + lowFrameScore
                + frameIntervalScore + jankCountScore + stutterRateScore);

        // 保存流畅性分数
        CacheUtil.put(CacheConst.KEY_FLUENCY_SCORE, fluencyScore);

        // 判断数据是否为空
        if (averageFPS + frameShakeRate + lowFrameRate + jankCount + stutterRate != 0.0f) {
            OkHttpUtils.builder().url(CacheConst.GLOBAL_IP + "/fluency/save")
                    .addParam("adminName", Admin.adminName)
                    .addParam("platformName", Admin.platformName)
                    .addParam("time", Admin.testTime)
                    .addParam("ip", IpPort.ip)
                    .addParam("port", IpPort.port)
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

    public static float getAverageFPS() {
        return CacheUtil.getFloat(CacheConst.KEY_AVERAGE_FPS);
    }

    public static float getFrameShakeRate() {
        return CacheUtil.getFloat(CacheConst.KEY_FRAME_SHAKE_RATE);
    }

    public static float getLowFrameRate() {
        return CacheUtil.getFloat(CacheConst.KEY_LOW_FRAME_RATE);
    }

    public static float getFrameInterval() {
        return CacheUtil.getFloat(CacheConst.KEY_FRAME_INTERVAL);
    }

    public static float getJankCount() {
        return CacheUtil.getFloat(CacheConst.KEY_JANK_COUNT);
    }

    public static float getStutterRate() {
        return CacheUtil.getFloat(CacheConst.KEY_STUTTER_RATE);
    }

    public static int getFluencyScore() {
        return CacheUtil.getInt(CacheConst.KEY_FLUENCY_SCORE);
    }

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
        startSuccessRate /= 100;
        float startSuccessScore = 100f * startSuccessRate / 3;
        float averageStartScore = averageStartTime < 50 ? 100f / 3 : 100f * (50 / averageStartTime) / 3;
        float averageQuitScore = averageQuitTime < 50 ? 100f / 3 : 100f * (50 / averageQuitTime) / 3;
        int stabilityScores = (int) (startSuccessScore + averageStartScore + averageQuitScore);

        // 保存稳定性分数
        CacheUtil.put(CacheConst.KEY_STABILITY_SCORE, stabilityScores);

        if (startSuccessRate + averageStartTime + averageQuitTime != 0.0f) {
            OkHttpUtils.builder().url(CacheConst.GLOBAL_IP + "/stability/save")
                    .addParam("adminName", Admin.adminName)
                    .addParam("platformName", Admin.platformName)
                    .addParam("time", Admin.testTime)
                    .addParam("ip", IpPort.ip)
                    .addParam("port", IpPort.port)
                    .addParam("startSuccessRate", startSuccessRate + "")
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

    public static float getStartSuccessRate() {
        return CacheUtil.getFloat(CacheConst.KEY_START_SUCCESS_RATE);
    }

    public static float getAverageStartTime() {
        return CacheUtil.getFloat(CacheConst.KEY_AVERAGE_START_TIME);
    }

    public static float getAverageQuitTime() {
        return CacheUtil.getFloat(CacheConst.KEY_AVERAGE_QUIT_TIME);
    }

    public static int getStabilityScore() {
        return CacheUtil.getInt(CacheConst.KEY_STABILITY_SCORE);
    }


    public static void calaAndSaveGameTouchScores(int testNum, float time) {
        // 正确率
        float averageAccuracy = (float) (testNum) / GameTouchUtil.testNum;
        Log.e("TWT", "GameTouchUtil.testNum: " + GameTouchUtil.testNum);
        Log.e("TWT", "testNum: " + testNum);
        Log.e("TWT", "time: " + time);

        float averAccuracyScore = 100f * averageAccuracy / 2;
        float responseTimeScore = time < 50 ? 50 : 100f * 50 / (2 * time);
        int touchScore = (int) (averAccuracyScore + responseTimeScore);

        // 保存触控体验分数
        CacheUtil.put(CacheConst.KEY_TOUCH_SCORE, touchScore);

        averageAccuracy *= 100;
        CacheUtil.put(CacheConst.KEY_AVERAGE_ACCURACY, averageAccuracy);
        CacheUtil.put(CacheConst.KEY_RESPONSE_TIME, time);
        Log.e("TWT", "KEY_AVERAGE_ACCURACY: " + averageAccuracy);
        Log.e("TWT", "KEY_RESPONSE_TIME: " + time);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void calcAndSaveTouchScores(
            String cloudDownTimeList, String cloudSpendTimeList) {
        //没有进行触控测试 没有数据时 直接返回
        if (cloudDownTimeList == null || cloudSpendTimeList == null) {
            return;
        }
        String cloudDownTimeListSub = cloudDownTimeList.substring(1, cloudDownTimeList.length() - 1);
        String cloudSpendTimeListSub = cloudSpendTimeList.substring(1, cloudSpendTimeList.length() - 1);
        String[] cloudDownTimeListArr = cloudDownTimeListSub.split(",");
        String[] cloudSpendTimeListArr = cloudSpendTimeListSub.split(",");

        Log.d("zzl", "calcAndSaveTouchScores: cloudDownTimeListArr==>"
                + Arrays.toString(cloudDownTimeListArr));
        Log.d("zzl", "calcAndSaveTouchScores: cloudSpendTimeListArr==>"
                + Arrays.toString(cloudSpendTimeListArr));

        for (int i = 0; i < cloudDownTimeListArr.length; i++) {
            cloudDownTimeListArr[i] = cloudDownTimeListArr[i].substring(1, cloudDownTimeListArr[i].length() - 1);
            Log.d("zzll", "calcAndSaveTouchScores: --" + cloudDownTimeListArr[i]);
        }

        for (int i = 0; i < cloudSpendTimeListArr.length; i++) {
            cloudSpendTimeListArr[i] = cloudSpendTimeListArr[i].substring(1, cloudSpendTimeListArr[i].length() - 1);
            Log.d("zzll", "calcAndSaveTouchScores: --" + cloudSpendTimeListArr[i]);
        }

        // 判断测试平台
        String checkPlatform = CacheUtil.getString(CacheConst.KEY_PLATFORM_NAME);
        Log.d("zzl", "测试平台===>" + checkPlatform);
        TreeSet<String> localTapTimes = (TreeSet<String>) CacheUtil.getSet(CacheConst.KEY_AUTO_TAP_TIMES);
        Log.d("zzl", "localTapTimes: " + localTapTimes);


        long responseTime0;
        responseTime0 = (Long.parseLong(cloudDownTimeListArr[0]) - CacheUtil.getLong("tapTimeOnLocal1")
                + Long.parseLong(cloudSpendTimeListArr[0])) * 2;
        long responseTime1;
        responseTime1 = (Long.parseLong(cloudDownTimeListArr[1]) - CacheUtil.getLong("tapTimeOnLocal2")
                + Long.parseLong(cloudSpendTimeListArr[1])) * 2;
        long responseTime2;
        responseTime2 = (Long.parseLong(cloudDownTimeListArr[2]) - CacheUtil.getLong("tapTimeOnLocal3")
                + Long.parseLong(cloudSpendTimeListArr[2])) * 2;
        long responseTime3;
        responseTime3 = (Long.parseLong(cloudDownTimeListArr[3]) - CacheUtil.getLong("tapTimeOnLocal4")
                + Long.parseLong(cloudSpendTimeListArr[3])) * 2;
        long responseTime4;
        responseTime4 = (Long.parseLong(cloudDownTimeListArr[4]) - CacheUtil.getLong("tapTimeOnLocal5")
                + Long.parseLong(cloudSpendTimeListArr[4])) * 2;
        long responseTime5;
        responseTime5 = (Long.parseLong(cloudDownTimeListArr[5]) - CacheUtil.getLong("tapTimeOnLocal6")
                + Long.parseLong(cloudSpendTimeListArr[5])) * 2;
        long responseTime6;
        responseTime6 = (Long.parseLong(cloudDownTimeListArr[6]) - CacheUtil.getLong("tapTimeOnLocal7")
                + Long.parseLong(cloudSpendTimeListArr[6])) * 2;
        long responseTime7;
        responseTime7 = (Long.parseLong(cloudDownTimeListArr[7]) - CacheUtil.getLong("tapTimeOnLocal8")
                + Long.parseLong(cloudSpendTimeListArr[7])) * 2;
        long responseTime8;
        responseTime8 = (Long.parseLong(cloudDownTimeListArr[8]) - CacheUtil.getLong("tapTimeOnLocal9")
                + Long.parseLong(cloudSpendTimeListArr[8])) * 2;
        long responseTime9;
        responseTime9 = (Long.parseLong(cloudDownTimeListArr[9]) - CacheUtil.getLong("tapTimeOnLocal10")
                + Long.parseLong(cloudSpendTimeListArr[9])) * 2;
        long responseTime10;
        responseTime10 = (Long.parseLong(cloudDownTimeListArr[10]) - CacheUtil.getLong("tapTimeOnLocal11")
                + Long.parseLong(cloudSpendTimeListArr[10])) * 2;

        Log.d("zzl", "触控体验总共耗时===>" + (CacheUtil.getLong("tapTimeOnLocal11") -
                CacheUtil.getLong("tapTimeOnLocal1")));
        ArrayList<Long> longs = new ArrayList<>();

        // 响应次数
        int responseNum = 0;

        if (responseTime0 != 0L) {
            responseNum++;
            longs.add(responseTime0);
        }

        if (responseTime1 != 0L) {
            responseNum++;
            longs.add(responseTime1);
        }
        if (responseTime2 != 0L) {
            responseNum++;
            longs.add(responseTime2);
        }
        if (responseTime3 != 0L) {
            responseNum++;
            longs.add(responseTime3);
        }
        if (responseTime4 != 0L) {
            responseNum++;
            longs.add(responseTime4);
        }
        if (responseTime5 != 0L) {
            responseNum++;
            longs.add(responseTime5);
        }
        if (responseTime6 != 0L) {
            responseNum++;
            longs.add(responseTime6);
        }
        if (responseTime7 != 0L) {
            responseNum++;
            longs.add(responseTime7);
        }
        if (responseTime8 != 0L) {
            responseNum++;
            longs.add(responseTime8);
        }
        if (responseTime9 != 0L) {
            responseNum++;
            longs.add(responseTime9);
        }
        if (responseTime10 != 0L) {
            responseNum++;
            longs.add(responseTime10);
        }

        longs.sort(Long::compareTo);
        Log.d("zzl", "calcAndSaveTouchScores: longs==>" + longs);

        // 去掉两个最高延时，去掉两个最低延时，然后求平均值
        longs.remove(longs.size() - 1);
        longs.remove(longs.size() - 1);
        longs.remove(0);
        longs.remove(0);
        Log.d("zzl", "calcAndSaveTouchScores: longs==>" + longs);

        // 平均触控时延
        long allResponseTime = 0L;
        for (Long aLong : longs) {
            allResponseTime += aLong;
        }
        float avgResponseTime = allResponseTime / longs.size();

        // 正确率
        float averageAccuracy = (float) (responseNum - 4) / longs.size();

        float averAccuracyScore = 100f * averageAccuracy / 2;
        float responseTimeScore = avgResponseTime < 50 ? 50 : 100f * 50 / (2 * avgResponseTime);
        int touchScore = (int) (averAccuracyScore + responseTimeScore);

        // 保存触控体验分数
        CacheUtil.put(CacheConst.KEY_TOUCH_SCORE, touchScore);

        averageAccuracy *= 100;
        CacheUtil.put(CacheConst.KEY_AVERAGE_ACCURACY, averageAccuracy);
        CacheUtil.put(CacheConst.KEY_RESPONSE_TIME, avgResponseTime);

        if (avgResponseTime + averageAccuracy != 0.0f) {
            OkHttpUtils.builder().url(CacheConst.GLOBAL_IP + "/touch/save")
                    .addParam("adminName", Admin.adminName)
                    .addParam("platformName", Admin.platformName)
                    .addParam("time", Admin.testTime)
                    .addParam("ip", IpPort.ip)
                    .addParam("port", IpPort.port)
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

    public static float getAverageAccuracy() {
        return CacheUtil.getFloat(CacheConst.KEY_AVERAGE_ACCURACY);
    }

    public static float getResponseTime() {
        return CacheUtil.getFloat(CacheConst.KEY_RESPONSE_TIME);
    }

    public static int getTouchScore() {
        return CacheUtil.getInt(CacheConst.KEY_TOUCH_SCORE);
    }

    public static void calcAndSaveSoundFrameScores(
            String resolution,
            float maxDiffValue
    ) {
        if (YinHuaData.pesq == null || YinHuaData.ssim == null || YinHuaData.psnr == null) {
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    calcAndSaveSoundFrameScores(resolution, maxDiffValue);
                    timer.cancel();
                }
            };
            timer.schedule(task, 5000);
            return;
        }
        float psnr = Float.parseFloat(YinHuaData.psnr);
        float ssim = Float.parseFloat(YinHuaData.ssim);
        float pesq = Float.parseFloat(YinHuaData.pesq);

        // 保存音画质量结果
        CacheUtil.put(CacheConst.KEY_RESOLUTION, resolution);
        CacheUtil.put(CacheConst.KEY_MAX_DIFF_VALUE, maxDiffValue);
        CacheUtil.put(CacheConst.KEY_PESQ, YinHuaData.pesq);
        CacheUtil.put(CacheConst.KEY_PSNR, YinHuaData.psnr);
        CacheUtil.put(CacheConst.KEY_SSIM, YinHuaData.ssim);

        // 计算音画质量分数
        String[] resolutionArray = resolution.split("X");
        float resolutionValue = Integer.parseInt(resolutionArray[0]) * Integer.parseInt(resolutionArray[1]);
        float resolutionScore = 100f * resolutionValue / (4 * 1920 * 1080);
        float maxDiffValueScore = maxDiffValue < 50 ? 50 : 100f * 50 / (4 * maxDiffValue);
        psnr = psnr > 40 ? 40 : psnr;
        float d3 = (100 * ((psnr / 40) + ssim)) / 8;
        float d4 = (float) ((100 * pesq) / (4.5 * 4));
        int soundFrameScore = (int) (resolutionScore + maxDiffValueScore + d3 + d4);

        // 保存音画质量分数
        CacheUtil.put(CacheConst.KEY_SOUND_FRAME_SCORE, soundFrameScore);
        if (YinHuaData.pesq != null && YinHuaData.ssim != null && YinHuaData.psnr != null) {
            OkHttpUtils.builder().url(CacheConst.GLOBAL_IP + "/AudioVideo/save")
                    .addParam("adminName", Admin.adminName)
                    .addParam("platformName", Admin.platformName)
                    .addParam("time", Admin.testTime)
                    .addParam("ip", IpPort.ip)
                    .addParam("port", IpPort.port)
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

    public static String getResolution() {
        return CacheUtil.getString(CacheConst.KEY_RESOLUTION);
    }

    public static float getMaxDiffValue() {
        return CacheUtil.getFloat(CacheConst.KEY_MAX_DIFF_VALUE);
    }

    public static int getSoundFrameScore() {
        return CacheUtil.getInt(CacheConst.KEY_SOUND_FRAME_SCORE);
    }
}
