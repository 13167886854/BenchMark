/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.benchmark.data.SettingData;
import com.example.benchmark.service.GameTouchTestService;
import com.example.benchmark.service.MyAccessibilityService;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * TapUtil
 *
 * @version 1.0
 * @since 2023/3/7 17:29
 */
public class TapUtil {
    /**
     * 点击次数  number of clicks
     */
    public static final int TOTAL_TAP_NUM = 12;

    private static TapUtil util = new TapUtil();

    private final int screenHeight = CacheUtil.getInt(CacheConst.KEY_SCREEN_HEIGHT);
    private final int screenWidth = CacheUtil.getInt(CacheConst.KEY_SCREEN_WIDTH);
    private final OkHttpClient client = new OkHttpClient();

    private MyAccessibilityService service;
    private GameTouchUtil gameTouchUtil = GameTouchUtil.getGameTouchUtil();

    private int phoneCurrentTapNum = 0;

    // 触控测试模拟点击次数  Touch test simulation click times
    private int mCurrentTapNum = 0;
    private long mLastTapTime = 0L;
    private int turn = 0;
    private long startTime = 0L;
    private long endTime = 0L;
    private long responseTime = 0L;

    private int mWholeMonitorNum;

    private TapUtil() {
    }

    /**
     * getUtil
     *
     * @return com.example.benchmark.utils.TapUtil
     * @date 2023/3/10 16:50
     */
    public static TapUtil getUtil() {
        if (util == null) {
            util = new TapUtil();
        }
        return util;
    }

    /**
     * getmWholeMonitorNum
     *
     * @return int
     * @date 2023/3/14 15:44
     */
    public int getmWholeMonitorNum() {
        return mWholeMonitorNum;
    }

    /**
     * setmWholeMonitorNum
     *
     * @param mWholeMonitorNum description
     * @return void
     * @date 2023/3/14 15:45
     */
    public void setmWholeMonitorNum(int mWholeMonitorNum) {
        this.mWholeMonitorNum = mWholeMonitorNum;
    }

    /**
     * setService
     *
     * @param service description
     * @return void
     * @date 2023/3/10 16:51
     */
    public void setService(MyAccessibilityService service) {
        this.service = service;
    }

    /**
     * tap
     *
     * @param locationX description
     * @param locationY description
     * @return void
     * @date 2023/3/10 16:51
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void tap(int locationX, int locationY) {
        if (service == null) {
            Log.e("TWT", "service is not initial yet");
        }
        AccessibilityUtil.tap(service, locationX, locationY,
                new AccessibilityCallback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure() {
                        Log.e("TWT", "tap failure");
                    }
                }
        );
    }

    /**
     * cloudPhoneTap
     *
     * @param locationX description
     * @param locationY description
     * @return void
     * @date 2023/3/10 16:53
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void cloudPhoneTap(int locationX, int locationY) {
        if (service == null) {
            Log.e("TWT", "service is not initial yet");
        }
        AccessibilityUtil.tap(service, locationX, locationY,
                new AccessibilityCallback() {
                    @Override
                    public void onSuccess() {
                        // 记录发送请求时的系统时间戳  Records the system timestamp when the request was sent
                        startTime = System.currentTimeMillis();
                        Request request = new Request.Builder()
                                .get()
                                .url(SettingData.getInstance().getServerAddress() + File.separator
                                        + "touch" + File.separator + "time")
                                .build();
                        ThreadPoolUtil.getPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                client.newCall(request)
                                        .enqueue(new Callback() {
                                            @Override
                                            public void onFailure(@NotNull Call call, @NotNull IOException exception) {
                                                Log.d("zzl", "onFailure: call===>" + exception.toString());
                                            }

                                            @Override
                                            public void onResponse(@NotNull Call call, @NotNull Response response)
                                                    throws IOException {
                                                // 获取成功响应的系统时间戳  Gets the system timestamp
                                                // of the successful response
                                                endTime = System.currentTimeMillis();
                                                responseTime = endTime - startTime;
                                                String result = response.body().string();
                                                JSONObject jsonObject = JSON.parseObject(result);
                                                Long timestamp = (Long) jsonObject.get("timestamp");
                                                Log.d("tapTimeOnLocal", "onResponse: timestamp" + timestamp);

                                                //String res = result.substring(81, 94);
                                                // 获取到的时间戳，应该减去响应时延  The timestamp obtained should
                                                // be subtracted from the response delay
                                                //mLastTapTime = Long.valueOf(res) - responseTime;
                                                mLastTapTime = timestamp - responseTime;
                                                mCurrentTapNum++;
                                                CacheUtil.put(("tapTimeOnLocal" + (mCurrentTapNum)), mLastTapTime);
                                            }
                                        });
                            }
                        });
                        if (mCurrentTapNum == TOTAL_TAP_NUM) {
                            mCurrentTapNum = 0;
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.e("TWT", "tap failure");
                    }
                }
        );
    }

    /**
     * gameTouchTap
     *
     * @param service description
     * @date 2023/3/9 14:55
     */
    public void gameTouchTap(GameTouchTestService service) {
        turn = 0;
        gameTouchUtil.clear();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Log.d("TWT", "turn = " + turn);
                turn++;
                if (turn % 2 == 1) {
                    tap(2165, 630); // 点击设置按钮  Click the "Set" button
                } else {
                    tap(1000, 830);  // 点击取消按钮  Click the Cancel button
                    gameTouchUtil.getTapTime(System.currentTimeMillis());
                }
                if (turn == 20) {
                    Log.d("TWT", "run: stop");
                    service.sendStopMsg();
                    cancel();
                }
            }
        };
        Timer timer = new Timer();
        gameTouchUtil.setReadyToTapTime(System.currentTimeMillis());
        timer.schedule(task, 1500, 750);
    }

    /**
     * phoneTouchTap
     *
     * @date 2023/3/9 14:56
     */
    public void phoneTouchTap() {
        phoneCurrentTapNum = 0;
        TimerTask task = new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                Log.d("zzl", "phoneCurrentTapNum = " + phoneCurrentTapNum);
                phoneCurrentTapNum++;
                cloudPhoneTap(screenWidth / 2, screenHeight / 2); // 点击设置按钮 Click the "Set" button
                if (phoneCurrentTapNum == TOTAL_TAP_NUM) {
                    phoneCurrentTapNum = 0;
                    Log.d("zzl", "phoneCurrentTapNum = " + phoneCurrentTapNum);
                    Log.d("zzl", "run: stop");
                    cancel();
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 1000, 1500);
    }
}
