package com.example.benchmark.utils;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.benchmark.Data.Admin;
import com.example.benchmark.Data.YinHuaData;

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
            //String cpuName,
            int cpuCores
    ) {
        // 保存CPU结果
        //CacheUtil.put(CacheConst.KEY_CPU_NAME, cpuName);
        CacheUtil.put(CacheConst.KEY_CPU_CORES, cpuCores);
        //// 计算CPU分数
        //float cpuCoresScore = cpuCores <= 8 ? 100f * cpuCores / (9 * 8) : 100f / 9;
        //int cpuScore = (int) (cpuCoresScore);
        //// 保存CPU分数
        //CacheUtil.put(CacheConst.KEY_CPU_SCORE, cpuScore);

        if (cpuCores != 0) {
            OkHttpUtils.builder().url(CacheConst.GLOBAL_IP + "/cpu/save")
                    .addParam("adminName", Admin.adminName)
                    .addParam("platformName", Admin.platformName)
                    .addParam("cores", cpuCores + "")
                    .addParam("time", Admin.testTime)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .post(true)
                    .async(new OkHttpUtils.ICallBack() {
                        @Override
                        public void onSuccessful(Call call, String data) {
                            Log.d(TAG, "onSuccessful: cpu---" + data);
                        }

                        @Override
                        public void onFailure(Call call, String errorMsg) {
                            Log.d(TAG, "onFailure: cpu---" + errorMsg);
                        }
                    });
        }

    }

    //public static int getCPUScore() {
    //    return CacheUtil.getInt(CacheConst.KEY_CPU_SCORE);
    //}

    public static void calcAndSaveGPUScores(
            String gpuVendor,
            String gpuRender,
            String gpuVersion
    ) {
        // 保存GPU结果
        CacheUtil.put(CacheConst.KEY_GPU_VENDOR, gpuVendor);
        CacheUtil.put(CacheConst.KEY_GPU_RENDER, gpuRender);
        if (gpuVersion != null) {
            String res = "";
            String select_plat = CacheUtil.getString(CacheConst.KEY_PLATFORM_NAME, "select_plat");
            Log.d(TAG, "calcAndSaveGPUScores: select_plat--" + select_plat);
            if (select_plat.startsWith("红手指")) {
                String[] strings = gpuVersion.split(" ");
                for (int i = 0; i < strings.length - 1; i++) {
                    res += (i == strings.length - 2) ? strings[i] : (strings[i] + " ");
                }
                CacheUtil.put(CacheConst.KEY_GPU_VERSION, res);
            } else {
                CacheUtil.put(CacheConst.KEY_GPU_VERSION, gpuVersion);
            }
        } else {
            CacheUtil.put(CacheConst.KEY_GPU_VERSION, gpuVersion);

        }
        if (gpuVendor != null && gpuRender != null && gpuVersion != null) {
            OkHttpUtils.builder().url(CacheConst.GLOBAL_IP + "/gpu/save")
                    .addParam("adminName", Admin.adminName)
                    .addParam("platformName", Admin.platformName)
                    .addParam("time", Admin.testTime)
                    .addParam("gpuVendor", gpuVendor)
                    .addParam("gpuRender", gpuRender)
                    .addParam("gpuVersion", gpuVersion)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .post(true)
                    .async(new OkHttpUtils.ICallBack() {
                        @Override
                        public void onSuccessful(Call call, String data) {
                            Log.d(TAG, "onSuccessful: gpu---" + data);
                        }

                        @Override
                        public void onFailure(Call call, String errorMsg) {
                            Log.d(TAG, "onFailure: gpu---" + errorMsg);
                        }
                    });
        }


        // 计算GPU分数
        // 保存GPU分数
        //CacheUtil.put(CacheConst.KEY_GPU_SCORE, 0);
    }

    //public static int getGPUScore() {
    //    return CacheUtil.getInt(CacheConst.KEY_GPU_SCORE);
    //}
    //
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
                    .addParam("availableRam", availableRAM)
                    .addParam("totalRam", totalRAM)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .post(true)
                    .async(new OkHttpUtils.ICallBack() {
                        @Override
                        public void onSuccessful(Call call, String data) {
                            Log.d(TAG, "onSuccessful: ram---" + data);
                        }

                        @Override
                        public void onFailure(Call call, String errorMsg) {
                            Log.d(TAG, "onFailure: ram---" + errorMsg);
                        }
                    });
        }

        // 计算RAM分数
        //float totalRAMScore = totalRAM <= 16 ? 100f * totalRAM / (9 * 16) : 100f / 9;
        //int ramScore = (int) (totalRAMScore);
        // 保存RAM分数
        //CacheUtil.put(CacheConst.KEY_RAM_SCORE, ramScore);
    }

    //public static int getRAMScore() {
    //    return CacheUtil.getInt(CacheConst.KEY_RAM_SCORE);
    //}

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
                    .addParam("availableRom", availableROM)
                    .addParam("totalRom", totalROM)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .post(true)
                    .async(new OkHttpUtils.ICallBack() {
                        @Override
                        public void onSuccessful(Call call, String data) {
                            Log.d(TAG, "onSuccessful: rom---" + data);
                        }

                        @Override
                        public void onFailure(Call call, String errorMsg) {
                            Log.d(TAG, "onFailure: rom---" + errorMsg);
                        }
                    });
        }


        // 计算ROM分数
        //float totalROMScore = totalROM <= 16 ? 100f * totalROM / (9 * 16) : 100f / 9;
        //int romScore = (int) (totalROMScore);
        // 保存ROM分数
        //CacheUtil.put(CacheConst.KEY_ROM_SCORE, romScore);
    }

    //public static int getROMScore() {
    //    return CacheUtil.getInt(CacheConst.KEY_ROM_SCORE);
    //}

    public static void calcAndSaveFluencyScores(
            float averageFPS,
            float frameShakeRate,
            float lowFrameRate,
            float frameInterval,
            float jankCount,
            float stutterRate
    ) {
        lowFrameRate *= 100;
        stutterRate *= 100;
        // 保存流畅性结果
        CacheUtil.put(CacheConst.KEY_AVERAGE_FPS, averageFPS);
        CacheUtil.put(CacheConst.KEY_FRAME_SHAKE_RATE, frameShakeRate);
        CacheUtil.put(CacheConst.KEY_LOW_FRAME_RATE, lowFrameRate);
        CacheUtil.put(CacheConst.KEY_FRAME_INTERVAL, frameInterval);
        CacheUtil.put(CacheConst.KEY_JANK_COUNT, jankCount);
        CacheUtil.put(CacheConst.KEY_STUTTER_RATE, stutterRate);
        // 计算流畅性分数
        lowFrameRate /= 100;
        stutterRate /= 100;
        float averFpsScore = averageFPS <= 120 ? 100f * averageFPS / (6 * 120) : 100f / 6;
        float frameShakeScore = frameShakeRate < 10 ? 100f / 6 : 100f * 10 / (6 * frameShakeRate);
        float lowFrameScore = 100f * (1 - lowFrameRate) / 6;
        float frameIntervalScore = frameInterval < 50 ? 100f / 6 : 100f * 50 / (6 * frameInterval);
        float jankCountScore = 100f / (6 * (1 + jankCount));
        float stutterRateScore = 100f / (6 * (1 - stutterRate));
        int fluencyScore = (int) (averFpsScore + frameShakeScore + lowFrameScore +
                frameIntervalScore + jankCountScore + stutterRateScore);
        // 保存流畅性分数
        CacheUtil.put(CacheConst.KEY_FLUENCY_SCORE, fluencyScore);

        // 判断数据是否为空
        if (averageFPS + frameShakeRate + lowFrameRate + jankCount + stutterRate != 0.0f) {
            OkHttpUtils.builder().url(CacheConst.GLOBAL_IP + "/fluency/save")
                    .addParam("adminName", Admin.adminName)
                    .addParam("platformName", Admin.platformName)
                    .addParam("time", Admin.testTime)
                    .addParam("averageFps", averageFPS + "")
                    .addParam("frameShakeRate", frameShakeRate + "")
                    .addParam("lowFrameRate", lowFrameRate + "")
                    .addParam("jankCount", jankCount + "")
                    .addParam("frameInterval", frameInterval + "")
                    .addParam("stutterRate", stutterRate + "")
                    .addParam("fluencyScore", fluencyScore + "")
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
//        // 保存触控体验分数
        CacheUtil.put(CacheConst.KEY_TOUCH_SCORE, touchScore);

        averageAccuracy *= 100;
        CacheUtil.put(CacheConst.KEY_AVERAGE_ACCURACY, averageAccuracy);
        CacheUtil.put(CacheConst.KEY_RESPONSE_TIME, time);
        Log.e("TWT", "KEY_AVERAGE_ACCURACY: " + averageAccuracy);
        Log.e("TWT", "KEY_RESPONSE_TIME: " + time);


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void calcAndSaveTouchScores(
            //Long cloudSpendTime0, Long cloudSpendTime1, Long cloudSpendTime2, Long cloudSpendTime3, Long cloudSpendTime4,
            //Long cloudSpendTime5, Long cloudSpendTime6, Long cloudSpendTime7, Long cloudSpendTime8, Long cloudSpendTime9,
            //Long cloudSpendTime10, Long cloudSpendTime11,
            //long time0, long time1, long time2, long time3, long time4, long time5,
            //long time6, long time7, long time8, long time9, long time10, long time11
            String cloudDownTimeList, String cloudSpendTimeList) {
        //Log.d("zzl", "cloudSpendTime0: ");
        //没有进行触控测试 没有数据时 直接返回
        if (cloudDownTimeList == null || cloudSpendTimeList == null) {
            return;
        }
        String cloudDownTimeListSub = cloudDownTimeList.substring(1, cloudDownTimeList.length() - 1);
        String cloudSpendTimeListSub = cloudSpendTimeList.substring(1, cloudSpendTimeList.length() - 1);
        String[] cloudDownTimeListArr = cloudDownTimeListSub.split(",");
        String[] cloudSpendTimeListArr = cloudSpendTimeListSub.split(",");

        Log.d("zzl", "calcAndSaveTouchScores: cloudDownTimeListArr==>" + Arrays.toString(cloudDownTimeListArr));
        Log.d("zzl", "calcAndSaveTouchScores: cloudSpendTimeListArr==>" + Arrays.toString(cloudSpendTimeListArr));

        for (int i = 0; i < cloudDownTimeListArr.length; i++) {
            cloudDownTimeListArr[i] = cloudDownTimeListArr[i].substring(1, cloudDownTimeListArr[i].length() - 1);
            //if (i == cloudDownTimeListArr.length - 1) {
            //    cloudDownTimeListArr[i] = cloudDownTimeListArr[i].substring(1, cloudDownTimeListArr[i].length() - 1);
            //}
            Log.d("zzll", "calcAndSaveTouchScores: --" + cloudDownTimeListArr[i]);
        }

        for (int i = 0; i < cloudSpendTimeListArr.length; i++) {
            cloudSpendTimeListArr[i] = cloudSpendTimeListArr[i].substring(1, cloudSpendTimeListArr[i].length() - 1);
            //if (i == cloudSpendTimeListArr.length - 1) {
            //    cloudSpendTimeListArr[i] = cloudSpendTimeListArr[i].substring(1, cloudSpendTimeListArr[i].length() - 1);
            //}
            Log.d("zzll", "calcAndSaveTouchScores: --" + cloudSpendTimeListArr[i]);
        }

        //Log.d("zzl", "calcAndSaveTouchScores: "+cloudDownTimeList.getClass());
        //Log.d("zzl", "calcAndSaveTouchScores: "+cloudSpendTimeList.getClass());
        // 判断测试平台
        String checkPlatform = CacheUtil.getString(CacheConst.KEY_PLATFORM_NAME);
        Log.d("zzl", "测试平台===>" + checkPlatform);
        TreeSet<String> localTapTimes = (TreeSet<String>) CacheUtil.getSet(CacheConst.KEY_AUTO_TAP_TIMES);
        Log.d("zzl", "localTapTimes: " + localTapTimes);

        // 响应次数
        int responseNum = 0;
        long ResponseTime0 = (Long.parseLong(cloudDownTimeListArr[0]) - CacheUtil.getLong("tapTimeOnLocal0") + Long.parseLong(cloudSpendTimeListArr[0])) * 2;
        long ResponseTime1 = (Long.parseLong(cloudDownTimeListArr[1]) - CacheUtil.getLong("tapTimeOnLocal1") + Long.parseLong(cloudSpendTimeListArr[1])) * 2;
        long ResponseTime2 = (Long.parseLong(cloudDownTimeListArr[2]) - CacheUtil.getLong("tapTimeOnLocal2") + Long.parseLong(cloudSpendTimeListArr[2])) * 2;
        long ResponseTime3 = (Long.parseLong(cloudDownTimeListArr[3]) - CacheUtil.getLong("tapTimeOnLocal3") + Long.parseLong(cloudSpendTimeListArr[3])) * 2;
        long ResponseTime4 = (Long.parseLong(cloudDownTimeListArr[4]) - CacheUtil.getLong("tapTimeOnLocal4") + Long.parseLong(cloudSpendTimeListArr[4])) * 2;
        long ResponseTime5 = (Long.parseLong(cloudDownTimeListArr[5]) - CacheUtil.getLong("tapTimeOnLocal5") + Long.parseLong(cloudSpendTimeListArr[5])) * 2;
        long ResponseTime6 = (Long.parseLong(cloudDownTimeListArr[6]) - CacheUtil.getLong("tapTimeOnLocal6") + Long.parseLong(cloudSpendTimeListArr[6])) * 2;
        long ResponseTime7 = (Long.parseLong(cloudDownTimeListArr[7]) - CacheUtil.getLong("tapTimeOnLocal7") + Long.parseLong(cloudSpendTimeListArr[7])) * 2;
        long ResponseTime8 = (Long.parseLong(cloudDownTimeListArr[8]) - CacheUtil.getLong("tapTimeOnLocal8") + Long.parseLong(cloudSpendTimeListArr[8])) * 2;
        long ResponseTime9 = (Long.parseLong(cloudDownTimeListArr[9]) - CacheUtil.getLong("tapTimeOnLocal9") + Long.parseLong(cloudSpendTimeListArr[9])) * 2;
        long ResponseTime10 = (Long.parseLong(cloudDownTimeListArr[10]) - CacheUtil.getLong("tapTimeOnLocal10") + Long.parseLong(cloudSpendTimeListArr[10])) * 2;


        Log.d("zzl", "触控体验总共耗时===>" + (CacheUtil.getLong("tapTimeOnLocal10") - CacheUtil.getLong("tapTimeOnLocal0")));
        ArrayList<Long> longs = new ArrayList<>();

        if (ResponseTime0 != 0L) {
            responseNum++;
            longs.add(ResponseTime0);

        }

        if (ResponseTime1 != 0L) {
            responseNum++;
            longs.add(ResponseTime1);

        }
        if (ResponseTime2 != 0L) {

            responseNum++;
            longs.add(ResponseTime2);

        }
        if (ResponseTime3 != 0L) {
            responseNum++;
            longs.add(ResponseTime3);

        }
        if (ResponseTime4 != 0L) {
            responseNum++;
            longs.add(ResponseTime4);

        }
        if (ResponseTime5 != 0L) {
            responseNum++;
            longs.add(ResponseTime5);

        }
        if (ResponseTime6 != 0L) {
            responseNum++;
            longs.add(ResponseTime6);

        }
        if (ResponseTime7 != 0L) {
            responseNum++;
            longs.add(ResponseTime7);

        }
        if (ResponseTime8 != 0L) {
            responseNum++;
            longs.add(ResponseTime8);

        }
        if (ResponseTime9 != 0L) {
            responseNum++;
            longs.add(ResponseTime9);

        }
        if (ResponseTime10 != 0L) {
            responseNum++;
            longs.add(ResponseTime10);

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
        long allResponseTime = 0;
        for (Long aLong : longs) {
            allResponseTime += aLong;
        }
        float avgResponseTime = allResponseTime / longs.size();
        //long avgResponseTime = (ResponseTime1 + ResponseTime2 + ResponseTime3 + ResponseTime4 + ResponseTime5 +
        //        ResponseTime6 + ResponseTime7 + ResponseTime8 + ResponseTime9) / 9;

        // 正确率
        float averageAccuracy = (float) (responseNum - 4) / longs.size();

//        Log.d("zzl", "ResponseTime0: " + ResponseTime0);
//        Log.d("zzl", "ResponseTime1: " + ResponseTime1);
//        Log.d("zzl", "ResponseTime2: " + ResponseTime2);
//        Log.d("zzl", "ResponseTime3: " + ResponseTime3);
//        Log.d("zzl", "ResponseTime4: " + ResponseTime4);
//        Log.d("zzl", "ResponseTime5: " + ResponseTime5);
//        Log.d("zzl", "ResponseTime6: " + ResponseTime6);
//        Log.d("zzl", "ResponseTime7: " + ResponseTime7);
//        Log.d("zzl", "ResponseTime8: " + ResponseTime8);
//        Log.d("zzl", "ResponseTime9: " + ResponseTime9);
//        Log.d("zzl", "ResponseTime10: " + ResponseTime10);
//        //Log.d("zzl", "ResponseTime11: " + ResponseTime11);
//        Log.d("zzl", "avgResponseTime: " + avgResponseTime);
//        Log.d("zzl", "averageAccuracy: " + averageAccuracy);
//        float averageAccuracy = cloudTapTimes.size() / (float) localTapTimes.size();
//        float responseTime = 0f;
//        int index = 0;
//        for (String localTapTimeStr : localTapTimes) {
//            if (index < cloudTapTimes.size()) {
//                long localTapTime = Long.parseLong(localTapTimeStr);
//                responseTime += cloudTapTimes.get(index) - localTapTime;
//                index++;
//            }
//        }
//        if (cloudTapTimes.size() != 0) responseTime /= cloudTapTimes.size();
//        // 保存触控体验结果
        //CacheUtil.put(CacheConst.KEY_AVERAGE_RESPONSE_TIME, avgResponseTime);
//        // 计算触控体验分数
//        averageAccuracy *= 100;
        float averAccuracyScore = 100f * averageAccuracy / 2;
        float responseTimeScore = avgResponseTime < 50 ? 50 : 100f * 50 / (2 * avgResponseTime);
        int touchScore = (int) (averAccuracyScore + responseTimeScore);
//        // 保存触控体验分数
        CacheUtil.put(CacheConst.KEY_TOUCH_SCORE, touchScore);

        averageAccuracy *= 100;
        CacheUtil.put(CacheConst.KEY_AVERAGE_ACCURACY, averageAccuracy);
        CacheUtil.put(CacheConst.KEY_RESPONSE_TIME, avgResponseTime);

        if (avgResponseTime + averageAccuracy != 0.0f) {
            OkHttpUtils.builder().url(CacheConst.GLOBAL_IP + "/touch/save")
                    .addParam("adminName", Admin.adminName)
                    .addParam("platformName", Admin.platformName)
                    .addParam("time", Admin.testTime)
                    .addParam("touchTimeDelay", avgResponseTime + "")
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

//    public static float getAverageResponseTime() {
//        return CacheUtil.getFloat(CacheConst.KEY_AVERAGE_RESPONSE_TIME);
//    }

    public static int getTouchScore() {
        return CacheUtil.getInt(CacheConst.KEY_TOUCH_SCORE);
    }

    public static void calcAndSaveSoundFrameScores(
            String resolution,
            float maxDiffValue
    ) {
        if (YinHuaData.PESQ == null || YinHuaData.SSIM == null || YinHuaData.PSNR == null) {
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
        float PSNR = Float.parseFloat(YinHuaData.PSNR);
        float SSIM = Float.parseFloat(YinHuaData.SSIM);
        float PESQ = Float.parseFloat(YinHuaData.PESQ);

            // 保存音画质量结果
        CacheUtil.put(CacheConst.KEY_RESOLUTION, resolution);
        CacheUtil.put(CacheConst.KEY_MAX_DIFF_VALUE, maxDiffValue);
        CacheUtil.put(CacheConst.KEY_PESQ, YinHuaData.PESQ);
        CacheUtil.put(CacheConst.KEY_PSNR, YinHuaData.PSNR);
        CacheUtil.put(CacheConst.KEY_SSIM, YinHuaData.SSIM);





        // 计算音画质量分数

        String[] resolutionArray = resolution.split("X");
        float resolutionValue = Integer.parseInt(resolutionArray[0]) * Integer.parseInt(resolutionArray[1]);
        float resolutionScore = 100f * resolutionValue / (4 * 1920 * 1080);
        float maxDiffValueScore = maxDiffValue < 50 ? 50 : 100f * 50 / (4 * maxDiffValue);
        PSNR = PSNR > 40 ? 40 : PSNR;
        float D3 = (100 * ((PSNR / 40) + SSIM)) / 8;
        float D4 = (float) ((100 * PESQ) / (4.5 * 4));
        int soundFrameScore = (int) (resolutionScore + maxDiffValueScore + D3 + D4);
        // 保存音画质量分数
        CacheUtil.put(CacheConst.KEY_SOUND_FRAME_SCORE, soundFrameScore);
        if (YinHuaData.PESQ != null && YinHuaData.SSIM != null && YinHuaData.PSNR != null) {
            OkHttpUtils.builder().url(CacheConst.GLOBAL_IP + "/AudioVideo/save")
                    .addParam("adminName", Admin.adminName)
                    .addParam("platformName", Admin.platformName)
                    .addParam("time", Admin.testTime)
                    .addParam("resolution", resolution + "")
                    .addParam("maxDiffValue", maxDiffValue + "")
                    .addParam("pesq", PESQ + "")
                    .addParam("psnr", PSNR + "")
                    .addParam("ssim", SSIM + "")
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
