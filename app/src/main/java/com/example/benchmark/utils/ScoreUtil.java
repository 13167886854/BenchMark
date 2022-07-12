package com.example.benchmark.utils;

/**
 * 计算分数
 */
public class ScoreUtil {

    public static void calcAndSaveCPUScores(
            String cpuName,
            int cpuCores
    ) {
        // 保存CPU结果
        CacheUtil.put(CacheConst.KEY_CPU_NAME, cpuName);
        CacheUtil.put(CacheConst.KEY_CPU_CORES, cpuCores);
        // 计算CPU分数
        float cpuCoresScore = cpuCores <= 8 ? 100f * cpuCores / (9 * 8) : 100f / 9;
        int cpuScore = (int) (cpuCoresScore);
        // 保存CPU分数
        CacheUtil.put(CacheConst.KEY_CPU_SCORE, cpuScore);
    }

    public static int getCPUScore() {
        return CacheUtil.getInt(CacheConst.KEY_CPU_SCORE);
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
        // 计算GPU分数
        // 保存GPU分数
        CacheUtil.put(CacheConst.KEY_GPU_SCORE, 0);
    }

    public static int getGPUScore() {
        return CacheUtil.getInt(CacheConst.KEY_GPU_SCORE);
    }

    public static void calcAndSaveRAMScores(
            float availableRAM,
            float totalRAM
    ) {
        // 保存RAM结果
        CacheUtil.put(CacheConst.KEY_AVAILABLE_RAM, availableRAM);
        CacheUtil.put(CacheConst.KEY_TOTAL_RAM, totalRAM);
        // 计算RAM分数
        float totalRAMScore = totalRAM <= 16 ? 100f * totalRAM / (9 * 16) : 100f / 9;
        int ramScore = (int) (totalRAMScore);
        // 保存RAM分数
        CacheUtil.put(CacheConst.KEY_RAM_SCORE, ramScore);
    }

    public static int getRAMScore() {
        return CacheUtil.getInt(CacheConst.KEY_RAM_SCORE);
    }

    public static void calcAndSaveROMScores(
            float availableROM,
            float totalROM
    ) {
        // 保存ROM结果
        CacheUtil.put(CacheConst.KEY_AVAILABLE_STORAGE, availableROM);
        CacheUtil.put(CacheConst.KEY_TOTAL_STORAGE, totalROM);
        // 计算ROM分数
        float totalROMScore = totalROM <= 16 ? 100f * totalROM / (9 * 16) : 100f / 9;
        int romScore = (int) (totalROMScore);
        // 保存ROM分数
        CacheUtil.put(CacheConst.KEY_ROM_SCORE, romScore);
    }

    public static int getROMScore() {
        return CacheUtil.getInt(CacheConst.KEY_ROM_SCORE);
    }

    public static void calcAndSaveFluencyScores(
            float averageFPS,
            float frameShakeRate,
            float lowFrameRate,
            float frameInterval,
            float jankCount,
            float stutterRate
    ) {
        // 保存流畅性结果
        CacheUtil.put(CacheConst.KEY_AVERAGE_FPS, averageFPS);
        CacheUtil.put(CacheConst.KEY_FRAME_SHAKE_RATE, frameShakeRate);
        CacheUtil.put(CacheConst.KEY_LOW_FRAME_RATE, lowFrameRate);
        CacheUtil.put(CacheConst.KEY_FRAME_INTERVAL, frameInterval);
        CacheUtil.put(CacheConst.KEY_JANK_COUNT, jankCount);
        CacheUtil.put(CacheConst.KEY_STUTTER_RATE, stutterRate);
        // 计算流畅性分数
        frameShakeRate /= 100;
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

    public static void calcAndSaveTouchScores(
            float averageAccuracy,
            float responseTime,
            float averageResponseTime
    ) {
        // 保存触控体验结果
        CacheUtil.put(CacheConst.KEY_AVERAGE_ACCURACY, averageAccuracy);
        CacheUtil.put(CacheConst.KEY_RESPONSE_TIME, responseTime);
        CacheUtil.put(CacheConst.KEY_AVERAGE_RESPONSE_TIME, averageResponseTime);
        // 计算触控体验分数
        averageAccuracy /= 100;
        float averAccuracyScore = 100f * averageAccuracy / 2;
        float responseTimeScore = responseTime < 50 ? 50 : 100f * 50 / (2 * responseTime);
        int touchScore = (int) (averAccuracyScore + responseTimeScore);
        // 保存触控体验分数
        CacheUtil.put(CacheConst.KEY_TOUCH_SCORE, touchScore);
    }

    public static float getAverageAccuracy() {
        return CacheUtil.getFloat(CacheConst.KEY_AVERAGE_ACCURACY);
    }

    public static float getResponseTime() {
        return CacheUtil.getFloat(CacheConst.KEY_RESPONSE_TIME);
    }

    public static float getAverageResponseTime() {
        return CacheUtil.getFloat(CacheConst.KEY_AVERAGE_RESPONSE_TIME);
    }

    public static int getTouchScore() {
        return CacheUtil.getInt(CacheConst.KEY_TOUCH_SCORE);
    }

    public static void calcAndSaveSoundFrameScores(
            String resolution,
            float maxDiffValue
    ) {
        // 保存音画质量结果
        CacheUtil.put(CacheConst.KEY_RESOLUTION, resolution);
        CacheUtil.put(CacheConst.KEY_MAX_DIFF_VALUE, maxDiffValue);
        // 计算音画质量分数
        String[] resolutionArray = resolution.split("X");
        float resolutionValue = Integer.parseInt(resolutionArray[0]) * Integer.parseInt(resolutionArray[1]);
        float resolutionScore = 100f * resolutionValue / (2 * 1920 * 1080);
        float maxDiffValueScore = maxDiffValue < 50 ? 50 : 100f * 50 / (2 * maxDiffValue);
        int soundFrameScore = (int) (resolutionScore + maxDiffValueScore);
        // 保存音画质量分数
        CacheUtil.put(CacheConst.KEY_SOUND_FRAME_SCORE, soundFrameScore);
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
