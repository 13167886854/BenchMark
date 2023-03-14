/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

/**
 * CacheConst
 *
 * @version 1.0
 * @since 2023/3/7 17:23
 */
public class CacheConst {
    private static final CacheConst CACHE_CONST = new CacheConst();

    /** KEY_SCREEN_WIDTH */
    public static final String KEY_SCREEN_WIDTH = "SCREEN_WIDTH";

    /** KEY_SCREEN_HEIGHT */
    public static final String KEY_SCREEN_HEIGHT = "SCREEN_HEIGHT";

    /** KEY_SCREEN_DPI */
    public static final String KEY_SCREEN_DPI = "SCREEN_DPI";

    /** KEY_PLATFORM_KIND */
    public static final String KEY_PLATFORM_KIND = "测试平台类型";

    /** PLATFORM_KIND_CLOUD_PHONE */
    public static final String PLATFORM_KIND_CLOUD_PHONE = "云手机";

    /** PLATFORM_KIND_CLOUD_GAME */
    public static final String PLATFORM_KIND_CLOUD_GAME = "云游戏";

    /** KEY_PLATFORM_NAME */
    public static final String KEY_PLATFORM_NAME = "测试平台名称";

    /** KEY_STABILITY_IS_MONITORED */
    public static final String KEY_STABILITY_IS_MONITORED = "稳定性是否已经测试结束";

    /** KEY_PERFORMANCE_IS_MONITORED */
    public static final String KEY_PERFORMANCE_IS_MONITORED = "流畅性、触控体验和音画质量是否已经测试结束";

    /** KEY_IS_HAVING_OTHER_PERFORMANCE_MONITOR */
    public static final String KEY_IS_HAVING_OTHER_PERFORMANCE_MONITOR = "是否还有流畅性、触控体验和音画质量的测试";

    /** PLATFORM_NAME_RED_FINGER_CLOUD_PHONE */
    public static final String PLATFORM_NAME_RED_FINGER_CLOUD_PHONE = "红手指云手机";

    /** PLATFORM_NAME_HUAWEI_CLOUD_PHONE */
    public static final String PLATFORM_NAME_HUAWEI_CLOUD_PHONE = "华为指令流";

    /** PLATFORM_NAME_HUAWEI_CLOUD_GAME */
    public static final String PLATFORM_NAME_HUAWEI_CLOUD_GAME = "华为视频流";

    /** PLATFORM_NAME_E_CLOUD_PHONE */
    public static final String PLATFORM_NAME_E_CLOUD_PHONE = "移动云手机";

    /** PLATFORM_NAME_NET_EASE_CLOUD_PHONE */
    public static final String PLATFORM_NAME_NET_EASE_CLOUD_PHONE = "网易云手机";

    /** PLATFORM_NAME_NET_EASE_CLOUD_GAME */
    public static final String PLATFORM_NAME_NET_EASE_CLOUD_GAME = "网易云游戏";

    /** PLATFORM_NAME_MI_GU_GAME */
    public static final String PLATFORM_NAME_MI_GU_GAME = "咪咕快游";

    /** PLATFORM_NAME_TENCENT_GAME */
    public static final String PLATFORM_NAME_TENCENT_GAME = "腾讯先锋";

    /** KEY_HARDWARE_INFO */
    public static final String KEY_HARDWARE_INFO = "硬件信息";

    /** KEY_RAM_INFO */
    public static final String KEY_RAM_INFO = "硬件RAM信息";

    /** KEY_RAM_SCORE */
    public static final String KEY_RAM_SCORE = "RAM分数";

    /** KEY_AVAILABLE_RAM */
    public static final String KEY_AVAILABLE_RAM = "可用RAM";

    /** KEY_TOTAL_RAM */
    public static final String KEY_TOTAL_RAM = "总RAM";

    /** KEY_ROM_INFO */
    public static final String KEY_ROM_INFO = "硬件ROM信息";

    /** KEY_ROM_SCORE */
    public static final String KEY_ROM_SCORE = "ROM分数";

    /** KEY_AVAILABLE_STORAGE */
    public static final String KEY_AVAILABLE_STORAGE = "可用ROM";

    /** KEY_TOTAL_STORAGE */
    public static final String KEY_TOTAL_STORAGE = "总ROM";

    /** KEY_CPU_INFO */
    public static final String KEY_CPU_INFO = "硬件CPU信息";

    /** KEY_CPU_SCORE */
    public static final String KEY_CPU_SCORE = "CPU分数";

    /** KEY_CPU_NAME */
    public static final String KEY_CPU_NAME = "CPU名称";

    /** KEY_CPU_CORES */
    public static final String KEY_CPU_CORES = "CPU核心数";

    /** KEY_GPU_INFO */
    public static final String KEY_GPU_INFO = "硬件GPU信息";

    /** KEY_GPU_SCORE */
    public static final String KEY_GPU_SCORE = "GPU分数";

    /** KEY_GPU_VENDOR */
    public static final String KEY_GPU_VENDOR = "GPU供应商";

    /** KEY_GPU_RENDER */
    public static final String KEY_GPU_RENDER = "GPU渲染器";

    /** KEY_GPU_VERSION */
    public static final String KEY_GPU_VERSION = "GPU版本";

    /** KEY_FLUENCY_INFO */
    public static final String KEY_FLUENCY_INFO = "流畅性信息";

    /** KEY_FLUENCY_SCORE */
    public static final String KEY_FLUENCY_SCORE = "流畅性分数";

    /** KEY_AVERAGE_FPS */
    public static final String KEY_AVERAGE_FPS = "avergeFPS";

    /** KEY_FRAME_SHAKE_RATE */
    public static final String KEY_FRAME_SHAKE_RATE = "frameShakingRate";

    /** KEY_LOW_FRAME_RATE */
    public static final String KEY_LOW_FRAME_RATE = "lowFrameRate";

    /** KEY_FRAME_INTERVAL */
    public static final String KEY_FRAME_INTERVAL = "frameInterval";

    /** KEY_JANK_COUNT */
    public static final String KEY_JANK_COUNT = "jankCount";

    /** KEY_STUTTER_RATE */
    public static final String KEY_STUTTER_RATE = "stutterRate";

    /** KEY_STABILITY_INFO */
    public static final String KEY_STABILITY_INFO = "稳定性信息";

    /** KEY_STABILITY_SCORE */
    public static final String KEY_STABILITY_SCORE = "稳定性分数";

    /** KEY_START_SUCCESS_RATE */
    public static final String KEY_START_SUCCESS_RATE = "START_SUCCESS_RATE";

    /** KEY_AVERAGE_START_TIME */
    public static final String KEY_AVERAGE_START_TIME = "AVERAGE_START_TIME";

    /** KEY_AVERAGE_QUIT_TIME */
    public static final String KEY_AVERAGE_QUIT_TIME = "AVERAGE_QUIT_TIME";

    /** WEB_TIME_URL */
    public static final String WEB_TIME_URL = "http://api.m.taobao.com/rest/api3.do?api=mtop.common.getTimestamp";

    /** KEY_TOUCH_INFO */
    public static final String KEY_TOUCH_INFO = "触控体验信息";

    /** KEY_TOUCH_SCORE */
    public static final String KEY_TOUCH_SCORE = "触控体验分数";

    /** KEY_AVERAGE_ACCURACY */
    public static final String KEY_AVERAGE_ACCURACY = "averageAccuracy";

    /** KEY_RESPONSE_TIME */
    public static final String KEY_RESPONSE_TIME = "responseTime";

    /** KEY_AVERAGE_RESPONSE_TIME */
    public static final String KEY_AVERAGE_RESPONSE_TIME = "averageResponseTime";

    /** KEY_AUTO_TAP_TIMES */
    public static final String KEY_AUTO_TAP_TIMES = "autoTapTimes";

    /** KEY_IS_AUTO_TAP */
    public static final String KEY_IS_AUTO_TAP = "isAutoTap";

    /** KEY_SOUND_FRAME_INFO */
    public static final String KEY_SOUND_FRAME_INFO = "音画质量信息";

    /** KEY_SOUND_FRAME_SCORE */
    public static final String KEY_SOUND_FRAME_SCORE = "音画质量分数";

    /** KEY_RESOLUTION */
    public static final String KEY_RESOLUTION = "resolution";

    /** KEY_MAX_DIFF_VALUE */
    public static final String KEY_MAX_DIFF_VALUE = "maxdifferencevalue";

    /** KEY_PESQ */
    public static final String KEY_PESQ = "PESQ";

    /** KEY_PSNR */
    public static final String KEY_PSNR = "PSNR";

    /** KEY_SSIM */
    public static final String KEY_SSIM = "SSIM";

    /** audioPath */
    private String audioPath = "";

    /** AUDIO_PHONE_NAME */
    public static final String AUDIO_PHONE_NAME = "phone_audio_record.pcm";

    /** AUDIO_GAME_NAME */
    public static final String AUDIO_GAME_NAME = "game_audio_record.pcm";

    /** videoPath */
    private String videoPath = "";

    /** VIDEO_PHONE_NAME */
    public static final String VIDEO_PHONE_NAME = "phone_video_record.mp4";

    /** VIDEO_GAME_NAME */
    public static final String VIDEO_GAME_NAME = "game_video_record.mp4";

    /** IMAGE_GAME */
    public static final String IMAGE_GAME = "test.jpg";

    /** ALIYUN_IP */
    public static final String ALIYUN_IP = "http://175.38.1.81:8080";

    /** HUAWEI_IP */
    public static final String HUAWEI_IP = "http://175.38.1.81:8080";

    /** GLOBAL_IP */
    public static final String GLOBAL_IP = "http://175.38.1.81:8080";

    private CacheConst(){
    }

    /**
     * getInstance
     *
     * @return com.example.benchmark.utils.CacheConst
     * @date 2023/3/14 15:04
     */
    public static CacheConst getInstance(){
        return CACHE_CONST;
    }

    /**
     * getAudioPath
     *
     * @return java.lang.String
     * @date 2023/3/14 15:08
     */
    public String getAudioPath() {
        return audioPath;
    }

    /**
     * setAudioPath
     *
     * @param audioPath description
     * @return void
     * @date 2023/3/14 15:08
     */
    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    /**
     * getVideoPath
     *
     * @return java.lang.String
     * @date 2023/3/14 15:08
     */
    public String getVideoPath() {
        return videoPath;
    }

    /**
     * setVideoPath
     *
     * @param videoPath description
     * @return void
     * @date 2023/3/14 15:08
     */
    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }
}

