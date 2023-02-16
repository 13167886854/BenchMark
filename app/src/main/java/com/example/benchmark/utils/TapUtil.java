package com.example.benchmark.utils;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.benchmark.service.MyAccessibilityService;
import com.example.benchmark.service.GameTouchTestService;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class TapUtil {
    public static int mWholeMonitorNum;
    private final int screenHeight = CacheUtil.getInt(CacheConst.KEY_SCREEN_HEIGHT);
    private final int screenWidth = CacheUtil.getInt(CacheConst.KEY_SCREEN_WIDTH);
    //单例模式
    private MyAccessibilityService service;
    private GameTouchUtil gameTouchUtil = GameTouchUtil.getGameTouchUtil();
    private static TapUtil util = new TapUtil();


    private int phoneCurrentTapNum = 0;
    // 触控测试模拟点击次数
    public static final int TOTAL_TAP_NUM = 12;

    private final OkHttpClient client = new OkHttpClient();

    private int mCurrentTapNum = 0;

    private long mLastTapTime = 0L;
    //private long mCurrentTime = 0;

    private long startTime = 0L;
    private long endTime = 0L;
    private long responseTime = 0L;

    private Thread mThread;

    //    public  Handler handler = new Handler(){
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            switch (msg.what){
//                case 0:
//                    timer.cancel();
//                    break;
//            }
//            super.handleMessage(msg);
//        }
//    };
    private TapUtil() {
    }

    public static TapUtil getUtil() {
        if (util == null) {
            util = new TapUtil();
            //util.tap();
        }
        return util;
    }

    private int turn = 0;


    public void setService(MyAccessibilityService service) {
        this.service = service;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void tap(int x, int y) {
        if (service == null) {
            Log.e("TWT", "service is not initial yet");
        }
        AccessibilityUtil.tap(service, x, y,
                new AccessibilityCallback() {
                    @Override
                    public void onSuccess() {
                        //Log.d("TWT", "do tap when time is " + System.currentTimeMillis());
                    }

                    @Override
                    public void onFailure() {
                        Log.e("TWT", "tap failure");
                    }
                }
        );

    }

    // 云手机触控体验
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void cloudPhoneTap(int x, int y) {
        if (service == null) {
            Log.e("TWT", "service is not initial yet");
        }
        AccessibilityUtil.tap(service, x, y,
                new AccessibilityCallback() {
                    @Override
                    public void onSuccess() {
                        /**
                         * 模拟点击成功，记录每次点击的时间戳
                         * 使用okhttp3发送 GET 请求，获取一个标准的时间戳
                         */
                        // 记录发送请求时的系统时间戳
                        startTime = System.currentTimeMillis();

                        Request request = new Request.Builder()
                                .get()
                                .url(CacheConst.WEB_TIME_URL)
                                .build();
                        mThread =  new Thread(new Runnable() {
                            @Override
                            public void run() {
                                client.newCall(request)
                                        .enqueue(new Callback() {
                                            @Override
                                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                                Log.d("zzl", "onFailure: call===>" + e.toString());
                                            }

                                            @Override
                                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                                // 获取成功响应的系统时间戳
                                                endTime = System.currentTimeMillis();
                                                responseTime = endTime - startTime;

                                                String result = response.body().string();
                                                String res = result.substring(81, 94);
                                                Log.d("zzl", "onResponse: result===>" + result);
                                                Log.d("zzl", "onResponse: res===>" + res);
                                                //Log.d("zzl", "onResponse: response===>" + response);
                                                // 获取到的时间戳，应该减去响应时延
                                                mLastTapTime = Long.valueOf(res) - responseTime;
                                                mCurrentTapNum++;

                                                Log.e("TWT zzl", "Tap Time mCurrentTapNum-" + mCurrentTapNum + ": " + mLastTapTime);
                                                Log.e("TWT zzl", "Tap Time mCurrentTapNum-" + mCurrentTapNum + "System.currentTimeMillis(): " + System.currentTimeMillis());
                                                //Log.e("TWT", "Tap Time mCurrentTapNum-" + mCurrentTapNum + ": " +new Date().getTime());
                                                //service.mTapStartTimes.add(String.valueOf(mLastTapTime));
                                                CacheUtil.put(("tapTimeOnLocal" + (mCurrentTapNum)), mLastTapTime);
                                                //Log.e("Auto Tap", "Tap Time:" + System.currentTimeMillis());
                                                //Message msg = Message.obtain();
                                                //msg.what = 1;
                                                //msg.obj = result;
                                                //mHandler.sendMessage(msg);
                                            }
                                        });
                            }
                        });
                        mThread.start();
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

    public void GameTouchTap(GameTouchTestService service) {
        turn = 0;
        gameTouchUtil.clear();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Log.d("TWT", "turn = " + turn);
                turn++;
                if (turn % 2 == 1) {
//                    tap(2165, 860); //点击设置按钮
                    tap(2165, 630); //点击设置按钮
                } else {
                    tap(1000, 830);  //点击取消按钮
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
        gameTouchUtil.readyToTapTime = System.currentTimeMillis();
        timer.schedule(task, 1500, 750);
    }

    public void PhoneTouchTap() {
        phoneCurrentTapNum = 0;
        TimerTask task = new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                Log.d("zzl", "phoneCurrentTapNum = " + phoneCurrentTapNum);
                phoneCurrentTapNum++;
                cloudPhoneTap(screenWidth / 2, screenHeight / 2); //点击设置按钮
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
