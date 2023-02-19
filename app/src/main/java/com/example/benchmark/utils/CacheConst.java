package com.example.benchmark.utils;

public class CacheConst {
    public static final String KEY_SCREEN_WIDTH = "SCREEN_WIDTH";
    public static final String KEY_SCREEN_HEIGHT = "SCREEN_HEIGHT";
    public static final String KEY_SCREEN_DPI = "SCREEN_DPI";

    public static final String KEY_PLATFORM_KIND = "测试平台类型";
    public static final String PLATFORM_KIND_CLOUD_PHONE = "云手机";
    public static final String PLATFORM_KIND_CLOUD_GAME = "云游戏";
    public static final String KEY_PLATFORM_NAME = "测试平台名称";
    public static final String KEY_STABILITY_IS_MONITORED = "稳定性是否已经测试结束";
    public static final String KEY_PERFORMANCE_IS_MONITORED = "流畅性、触控体验和音画质量是否已经测试结束";
    public static final String KEY_IS_HAVING_OTHER_PERFORMANCE_MONITOR = "是否还有流畅性、触控体验和音画质量的测试";
    public static final String PLATFORM_NAME_RED_FINGER_CLOUD_PHONE = "红手指云手机";
    public static final String PLATFORM_NAME_HUAWEI_CLOUD_PHONE = "华为指令流";
    public static final String PLATFORM_NAME_HUAWEI_CLOUD_GAME = "华为视频流";
    public static final String PLATFORM_NAME_E_CLOUD_PHONE = "移动云手机";
    public static final String PLATFORM_NAME_NET_EASE_CLOUD_PHONE = "网易云手机";
    public static final String PLATFORM_NAME_NET_EASE_CLOUD_GAME = "网易云游戏";
    public static final String PLATFORM_NAME_MI_GU_GAME = "咪咕快游";
    public static final String PLATFORM_NAME_Tencent_GAME = "腾讯先锋";

    // 硬件信息
    public static final String KEY_HARDWARE_INFO = "硬件信息";

    // RAM
    public static final String KEY_RAM_INFO = "硬件RAM信息";
    public static final String KEY_RAM_SCORE = "RAM分数";
    public static final String KEY_AVAILABLE_RAM = "可用RAM";
    public static final String KEY_TOTAL_RAM = "总RAM";

    // ROM
    public static final String KEY_ROM_INFO = "硬件ROM信息";
    public static final String KEY_ROM_SCORE = "ROM分数";
    public static final String KEY_AVAILABLE_STORAGE = "可用ROM";
    public static final String KEY_TOTAL_STORAGE = "总ROM";

    // CPU
    public static final String KEY_CPU_INFO = "硬件CPU信息";
    public static final String KEY_CPU_SCORE = "CPU分数";
    public static final String KEY_CPU_NAME = "CPU名称";
    public static final String KEY_CPU_CORES = "CPU核心数";

    // GPU
    public static final String KEY_GPU_INFO = "硬件GPU信息";
    public static final String KEY_GPU_SCORE = "GPU分数";
    public static final String KEY_GPU_VENDOR = "GPU供应商";
    public static final String KEY_GPU_RENDER = "GPU渲染器";
    public static final String KEY_GPU_VERSION = "GPU版本";

    // 流畅性信息
    public static final String KEY_FLUENCY_INFO = "流畅性信息";
    public static final String KEY_FLUENCY_SCORE = "流畅性分数";
    public static final String KEY_AVERAGE_FPS = "avergeFPS";
    public static final String KEY_FRAME_SHAKE_RATE = "frameShakingRate";
    public static final String KEY_LOW_FRAME_RATE = "lowFrameRate";
    public static final String KEY_FRAME_INTERVAL = "frameInterval";
    public static final String KEY_JANK_COUNT = "jankCount";
    public static final String KEY_STUTTER_RATE = "stutterRate";

    // 稳定性信息
    public static final String KEY_STABILITY_INFO = "稳定性信息";
    public static final String KEY_STABILITY_SCORE = "稳定性分数";
    public static final String KEY_START_SUCCESS_RATE = "START_SUCCESS_RATE";
    public static final String KEY_AVERAGE_START_TIME = "AVERAGE_START_TIME";
    public static final String KEY_AVERAGE_QUIT_TIME = "AVERAGE_QUIT_TIME";

    // 触控信息
    public static final String WEB_TIME_URL = "http://api.m.taobao.com/rest/api3.do?api=mtop.common.getTimestamp"; // 淘宝标准时间戳
    public static final String KEY_TOUCH_INFO = "触控体验信息";
    public static final String KEY_TOUCH_SCORE = "触控体验分数";
    public static final String KEY_AVERAGE_ACCURACY = "averageAccuracy";          // 触控正确率
    public static final String KEY_RESPONSE_TIME = "responseTime";
    public static final String KEY_AVERAGE_RESPONSE_TIME = "averageResponseTime"; // 平均触控时延
    public static final String KEY_AUTO_TAP_TIMES = "autoTapTimes";
    public static final String KEY_IS_AUTO_TAP = "isAutoTap";

    // 音画质量信息
    public static final String KEY_SOUND_FRAME_INFO = "音画质量信息";
    public static final String KEY_SOUND_FRAME_SCORE = "音画质量分数";
    public static final String KEY_RESOLUTION = "resolution";
    public static final String KEY_MAX_DIFF_VALUE = "maxdifferencevalue";
    public static final String KEY_PESQ = "PESQ";
    public static final String KEY_PSNR = "PSNR";
    public static final String KEY_SSIM = "SSIM";
    public static String audioPath = "";
    public static final String AUDIO_PHONE_NAME = "phone_audio_record.pcm";
    public static final String AUDIO_GAME_NAME = "game_audio_record.pcm";
    public static String videoPath = "";
    public static final String VIDEO_PHONE_NAME = "phone_video_record.mp4";
    public static final String VIDEO_GAME_NAME = "game_video_record.mp4";
    public static final String IMAGE_GAME = "test.jpg";

    // a阿里云IP地址
    public static final String ALIYUN_IP = "http://175.38.1.81:8080";

    // 华为云IP地址
    public static final String HUAWEI_IP = "http://175.38.1.81:8080";

    // 临时地址
    public static final String GLOBAL_IP = "http://175.38.1.81:8080";
}

