/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.benchmark.data.Admin;
import com.example.benchmark.R;
import com.example.benchmark.data.SettingData;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.OkHttpUtils;

import java.io.File;
import java.util.Map;

import okhttp3.Call;

/**
 * HistoryFragment
 *
 * @version 1.0
 * @since 2023/3/7 15:14
 */
public class HistoryFragment extends Fragment {
    ImageButton historyBack;
    TextView testInfo;

    private String type = "";
    private TextView title;
    private String okHttpPara = "";
    private JSONArray rom;
    private JSONArray ram;
    private JSONArray cpu;
    private JSONArray gpu;
    private JSONArray fluency;
    private JSONArray stability;
    private JSONArray audioVideo;
    private JSONArray touch;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Object object;
            Map map;
            switch (type) {
                case "info_fluency":
                    textSet5();
                    break;
                case "info_stability":
                    textSet4();
                    break;
                case "info_touch":
                    textSet3();
                    break;
                case "info_audio_video":
                    textSet2();
                    break;
                case "info_hardware":
                    textSet1();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void textSet5() {
        Object object;
        Map map;
        if (fluency.size() == 0) {
            testInfo.setText(" 暂无测试数据。。。");
            return;
        }
        String text = "";
        for (int i = 0; i < fluency.size(); i++) {
            object = fluency.get(i);
            if (object instanceof Map) {
                map = (Map) object;
                text += "测试平台： " + (map.get("platformName") == null ? "null"
                        : map.get("platformName").toString()) + System.getProperty("line.separator");
                text += "    平均帧率: " + (map.get("averageFps") == null ? "null"
                        : map.get("averageFps").toString()) + System.getProperty("line.separator");
                text += "    低帧率: " + (map.get("lowFrameRate") == null ? "null"
                        : map.get("lowFrameRate").toString()) + System.getProperty("line.separator");
                text += "    抖动方差: " + (map.get("frameShakeRate") == null ? "null"
                        : map.get("frameShakeRate").toString()) + System.getProperty("line.separator");
                text += "    jank次数: " + (map.get("jankCount") == null ? "null"
                        : map.get("jankCount").toString()) + System.getProperty("line.separator");
                text += "    帧间隔: " + (map.get("frameInterval") == null ? "null"
                        : map.get("frameInterval").toString()) + System.getProperty("line.separator");
                text += "    卡顿占比时长: " + (map.get("stutterRate") == null ? "null"
                        : map.get("stutterRate").toString()) + System.getProperty("line.separator");
                text += "    评分: " + (map.get("fluencyScore") == null ? "null"
                        : map.get("fluencyScore").toString()) + System.getProperty("line.separator");
                text += "测试时间: " + (map.get("time") == null ? "null"
                        : map.get("time").toString()) + System.getProperty("line.separator");
            }
        }
        testInfo.setText(text);
    }

    private void textSet4() {
        Object object;
        Map map;
        if (stability.size() == 0) {
            testInfo.setText(" 暂无测试数据。。。");
            return;
        }
        String text = "";
        for (int i = 0; i < stability.size(); i++) {
            object = stability.get(i);
            if (object instanceof Map) {
                map = (Map) object;
                text += "测试平台： " + (map.get("platformName") == null ? "null"
                        : map.get("platformName").toString()) + System.getProperty("line.separator");
                text += "    启动成功率: " + (map.get("startSuccessRate") == null ? "null"
                        : map.get("startSuccessRate").toString()) + System.getProperty("line.separator");
                text += "    平均启动时间: " + (map.get("averageStartTime") == null ? "null"
                        : map.get("averageStartTime").toString()) + System.getProperty("line.separator");
                text += "    平均退出时间: " + (map.get("averageQuitTime") == null ? "null"
                        : map.get("averageQuitTime").toString()) + System.getProperty("line.separator");
                text += "    评分: " + (map.get("stabilityScore") == null ? "null"
                        : map.get("stabilityScore").toString()) + System.getProperty("line.separator");
                text += "测试时间: " + (map.get("time") == null ? "null"
                        : map.get("time").toString()) + System.getProperty("line.separator");
            }
        }
        testInfo.setText(text);
    }

    private void textSet3() {
        Object object;
        Map map;
        if (touch.size() == 0) {
            testInfo.setText(" 暂无测试数据。。。");
            return;
        }
        String text = "";
        for (int i = 0; i < touch.size(); i++) {
            object = touch.get(i);
            if (object instanceof Map) {
                map = (Map) object;
                text += "测试平台： " + (map.get("platformName") == null ? "null"
                        : map.get("platformName").toString()) + System.getProperty("line.separator");
                text += "    正确率: " + (map.get("touchAccuracy") == null ? "null"
                        : map.get("touchAccuracy").toString()) + System.getProperty("line.separator");
                text += "    点击时延: " + (map.get("touchTimeDelay") == null ? "null"
                        : map.get("touchTimeDelay").toString()) + System.getProperty("line.separator");
                text += "    评分: " + (map.get("touchScore") == null ? "null"
                        : map.get("touchScore").toString()) + System.getProperty("line.separator");
                text += "测试时间: " + (map.get("time") == null ? "null"
                        : map.get("time").toString()) + System.getProperty("line.separator");
            }
        }
        testInfo.setText(text);
    }

    private void textSet2() {
        Object object;
        Map map;
        if (audioVideo.size() == 0) {
            testInfo.setText(" 暂无测试数据。。。");
            return;
        }
        String text = "";
        for (int i = 0; i < audioVideo.size(); i++) {
            object = audioVideo.get(i);
            if (object instanceof Map) {
                map = (Map) object;
                text += "测试平台： " + (map.get("platformName") == null ? "null"
                        : map.get("platformName").toString()) + System.getProperty("line.separator");
                text += "    分辨率: " + (map.get("resolution") == null ? "null"
                        : map.get("resolution").toString()) + System.getProperty("line.separator");
                text += "    音画同步差: " + (map.get("maxDiffValue") == null ? "null"
                        : map.get("maxDiffValue").toString()) + System.getProperty("line.separator");
                text += "    PESQ: " + (map.get("pesq") == null ? "null"
                        : map.get("pesq").toString()) + System.getProperty("line.separator");
                text += "    SSIM: " + (map.get("ssim") == null ? "null"
                        : map.get("ssim").toString()) + System.getProperty("line.separator");
                text += "    PSNR: " + (map.get("psnr") == null ? "null"
                        : map.get("psnr").toString()) + System.getProperty("line.separator");
                text += "    评分: " + (map.get("qualityScore") == null ? "null"
                        : map.get("qualityScore").toString()) + System.getProperty("line.separator");
                text += "测试时间: " + (map.get("time") == null ? "null"
                        : map.get("time").toString()) + System.getProperty("line.separator");
            }
            testInfo.setText(text);
        }
    }

    private void textSet1() {
        Object object;
        Map map;
        if (rom.size() == 0) {
            testInfo.setText(" 暂无测试数据。。。");
            return;
        }
        String text = "";
        for (int i = 0; i < rom.size(); i++) {
            object = rom.get(i);
            if (object instanceof Map) {
                map = (Map) object;
                text += "测试平台： " + (map.get("platformName") == null ? "null"
                        : map.get("platformName").toString()) + System.getProperty("line.separator");
                text += "ROM" + System.getProperty("line.separator");
                text += "    可用内存: " + (map.get("availableRom") == null ? "null"
                        : map.get("availableRom").toString()) + System.getProperty("line.separator");
                text += "    总内存: " + (map.get("totalRom") == null ? "null"
                        : map.get("totalRom").toString()) + System.getProperty("line.separator");
            }
            object = ram.get(i);
            if (object instanceof Map) {
                map = (Map) object;
                text += "RAM" + System.getProperty("line.separator");
                text += "    可用内存: " + (map.get("availableRam") == null ? "null"
                        : map.get("availableRam").toString()) + System.getProperty("line.separator");
                text += "    总内存: " + (map.get("totalRam") == null ? "null"
                        : map.get("totalRam").toString()) + System.getProperty("line.separator");
            }
            object = cpu.get(i);
            if (object instanceof Map) {
                map = (Map) object;
                text += "CPU" + System.getProperty("line.separator");
                text += "    核数: " + (map.get("cores") == null ? "null"
                        : map.get("cores").toString()) + System.getProperty("line.separator");
            }
            object = gpu.get(i);
            if (object instanceof Map) {
                map = (Map) object;
                text += "GPU" + System.getProperty("line.separator");
                text += "    gpuVendor: " + (map.get("gpuRender") == null ? "null"
                        : map.get("gpuRender").toString()) + System.getProperty("line.separator");
                text += "    gpuVersion: " + (map.get("gpuVersion") == null ? "null"
                        : map.get("gpuVersion").toString()) + System.getProperty("line.separator");
                text += "    gpuVendor: " + (map.get("gpuVendor") == null ? "null"
                        : map.get("gpuVendor").toString()) + System.getProperty("line.separator");
                text += "测试时间: " + (map.get("time") == null ? "null"
                        : map.get("time").toString()) + System.getProperty("line.separator");
            }
        }
        testInfo.setText(text);
    }

    /**
     * onCreateView
     *
     * @param inflater           description
     * @param container          description
     * @param savedInstanceState description
     * @return android.view.View
     * @date 2023/3/10 14:10
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                                @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_fragment, container, false);

        historyBack = view.findViewById(R.id.history_back);
        title = view.findViewById(R.id.title);
        testInfo = view.findViewById(R.id.test_info);
        testInfo.setMovementMethod(ScrollingMovementMethod.getInstance());

        historyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                TishiFragment tishiFragment = new TishiFragment();
                FragmentManager fragmentManager = getFragmentManager();

                // 开启事务  Start transaction
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_fram, tishiFragment);
                fragmentTransaction.commit();
            }
        });

        Bundle arguments = getArguments();
        type = arguments.getString("type");

        Log.e("TWT", "onCreate: " + type);
        initTitle(type);

        queryForData(Admin.getInstance().getAdminName(), okHttpPara);

        return view;
    }

    private void initTitle(String type) {
        if (type.equals("")) {
            return;
        }
        Drawable drawable;
        switch (type) {
            case "info_fluency":
                title.setText("流畅性");
                drawable = getResources().getDrawable(R.drawable.blue_liuchang);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                title.setCompoundDrawablesRelative(drawable, null, null, null);
                okHttpPara = "Fluency";
                break;
            case "info_stability":
                title.setText("稳定性");
                drawable = getResources().getDrawable(R.drawable.blue_wending);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                title.setCompoundDrawablesRelative(drawable, null, null, null);
                okHttpPara = "Stability";
                break;
            case "info_touch":
                title.setText("触控测试");
                drawable = getResources().getDrawable(R.drawable.blue_chukong);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                title.setCompoundDrawablesRelative(drawable, null, null, null);
                okHttpPara = "Touch";
                break;
            case "info_audio_video":
                title.setText("音画质量");
                drawable = getResources().getDrawable(R.drawable.blue_yinhua);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                title.setCompoundDrawablesRelative(drawable, null, null, null);
                okHttpPara = "AudioVideo";
                break;
            case "info_hardware":
                title.setText("硬件信息");
                drawable = getResources().getDrawable(R.drawable.blue_cpu);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                title.setCompoundDrawablesRelative(drawable, null, null, null);
                okHttpPara = "Config";
                break;
            default:
                break;
        }
    }

    private void queryForData(String username, String type) {
        OkHttpUtils.builder().url(SettingData.getInstance().getServerAddress() + File.separator + "data"
                + File.separator + username + File.separator + type)
                .get()
                .async(new OkHttpUtils.ICallBack() {
                    @Override
                    public void onSuccessful(Call call, String data) {
                        Log.e("TWT", "data: " + data);
                        JSONObject jsonObject = JSON.parseObject(data);
                        rom = jsonObject.getJSONArray("ROM");
                        ram = jsonObject.getJSONArray("RAM");
                        cpu = jsonObject.getJSONArray("CPU");
                        gpu = jsonObject.getJSONArray("GPU");
                        fluency = jsonObject.getJSONArray("Fluency");
                        stability = jsonObject.getJSONArray("Stability");
                        audioVideo = jsonObject.getJSONArray("AudioVideo");
                        touch = jsonObject.getJSONArray("Touch");
                        handler.sendEmptyMessage(0);
                    }

                    @Override
                    public void onFailure(Call call, String errorMsg) {
                        Log.e("TWT", "onFailure: cpu---" + errorMsg);
                        Toast.makeText(getContext(), "网络连接异常", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
