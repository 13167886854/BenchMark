/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.benchmark.adapter.JutiAdapter;
import com.example.benchmark.data.JuTiData;
import com.example.benchmark.data.YinHuaData;
import com.example.benchmark.R;
import com.example.benchmark.render.GPURenderer;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;
import com.example.benchmark.utils.DeviceInfoUtils;
import com.example.benchmark.utils.MemInfoUtil;
import com.example.benchmark.utils.SDCardUtils;
import com.example.benchmark.utils.ScoreUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JutiZhibiaoActivity
 *
 * @version 1.0
 * @since 2023/3/7 15:05
 */
public class JutiZhibiaoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "JutiZhibiaoActivity";

    private Boolean isCloudPhone;
    private HashMap mHashMapLocal;
    private ImageButton back;
    private TextView juTiPhoneName;
    private TextView juTiGrade;
    private Button backCePing;
    private Button nextZhiBiao;
    private ImageView juTiImg;
    private TextView juTiText;
    private TextView juTiItem;
    private LinearLayout mHeadScore;
    private FragmentManager fragmentManager;
    private RecyclerView recyclerView;
    private List<JuTiData> data;
    private JutiAdapter jutiAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ceping_xiangxi);
        initview();
        back.setOnClickListener(this::onClick);
        backCePing.setOnClickListener(this::onClick);
        Intent intent = getIntent();
        initdata(intent);
        jutiAdapter = new JutiAdapter(JutiZhibiaoActivity.this, data);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(jutiAdapter);
    }

    private void initview() {
        back = findViewById(R.id.jutizhibiao_fanhui);
        juTiPhoneName = findViewById(R.id.juti_phone_name);
        juTiGrade = findViewById(R.id.juti_grade);
        backCePing = findViewById(R.id.juti_back_ceping);
        nextZhiBiao = findViewById(R.id.juti_next_ceping);
        mHeadScore = findViewById(R.id.detail_monitor_head_score);
        juTiImg = findViewById(R.id.juti_image);
        juTiItem = findViewById(R.id.juti_item);
        juTiText = findViewById(R.id.juti_text);
        recyclerView = findViewById(R.id.juti_rv);
    }

    @SuppressLint("SetTextI18n")
    private void initdata(Intent intent) {
        String selectPlat = intent.getStringExtra("selectPlat");
        Log.d(TAG, "initdata: selectPlat-------" + selectPlat);
        String selectItem = intent.getStringExtra("selectItem");
        String selectText = intent.getStringExtra("selectText");
        Integer grade = intent.getIntExtra("select_grade", 98);
        int selectImg = intent.getIntExtra("selectImg", R.drawable.blue_liuchang);
        isCloudPhone = intent.getBooleanExtra("isCloudPhone", false);
        Log.d(TAG, "onCreate: isCloudPhone-----------" + isCloudPhone);
        if (mHashMapLocal instanceof HashMap) {
            mHashMapLocal = (HashMap) intent.getSerializableExtra("localMobileInfo");
        }
        Log.d(TAG, "initdata: localMobileInfo--------" + mHashMapLocal);
        juTiImg.setImageResource(selectImg);
        juTiText.setText(selectText);
        juTiItem.setText(selectItem);
        juTiPhoneName.setText(selectPlat + "·" + selectItem);
        juTiGrade.setText(String.valueOf(grade));
        Log.e("TWT", "selectItem: " + selectItem);
        switch (selectItem) {
            case CacheConst.KEY_FLUENCY_INFO: {
                data = new ArrayList<>();
                data.add(new JuTiData("平均帧率", ScoreUtil.getAverageFPS() + "fps"));
                data.add(new JuTiData("抖动帧率(方差)", ScoreUtil.getFrameShakeRate() + ""));
                data.add(new JuTiData("低帧率", ScoreUtil.getLowFrameRate() + "%"));
                data.add(new JuTiData("帧间隔", ScoreUtil.getFrameInterval() + "ms"));
                data.add(new JuTiData("jank", ScoreUtil.getJankCount() + "次"));
                data.add(new JuTiData("卡顿时长占比", ScoreUtil.getStutterRate() + "%"));
                break;
            }
            case CacheConst.KEY_STABILITY_INFO: {
                data = new ArrayList<>();
                data.add(new JuTiData("启动成功率", ScoreUtil.getStartSuccessRate() + "%"));
                data.add(new JuTiData("平均启动时长", ScoreUtil.getAverageStartTime() + "ms"));
                data.add(new JuTiData("平均退出时长", ScoreUtil.getAverageQuitTime() + "ms"));
                break;
            }
            case CacheConst.KEY_TOUCH_INFO: {
                data = new ArrayList<>();
                data.add(new JuTiData("平均正确率", ScoreUtil.getAverageAccuracy() + "%"));
                data.add(new JuTiData("触屏响应时延", ScoreUtil.getResponseTime() + "ms"));
                break;
            }
            case CacheConst.KEY_SOUND_FRAME_INFO: {
                data = new ArrayList<>();
                data.add(new JuTiData("分辨率", ScoreUtil.getResolution() + "px"));
                data.add(new JuTiData("音画同步差", ScoreUtil.getMaxDiffValue() + "帧"));
                data.add(new JuTiData("PSNR", YinHuaData.psnr));
                data.add(new JuTiData("SSIM", YinHuaData.ssim));
                data.add(new JuTiData("PESQ", YinHuaData.pesq));
                break;
            }
            case CacheConst.KEY_CPU_INFO: {
                mHeadScore.setVisibility(View.GONE);
                data = new ArrayList<>();
                if (!isCloudPhone) {
                    data.add(new JuTiData("CPU核数", mHashMapLocal.get("CPUCores") + "核"));
                } else {
                    data.add(new JuTiData("CPU核数", CacheUtil.getInt(CacheConst.KEY_CPU_CORES) + "核"));
                }
                break;
            }
            case CacheConst.KEY_GPU_INFO: {
                mHeadScore.setVisibility(View.GONE);
                data = new ArrayList<>();
                if (!isCloudPhone) {
                    data.add(new JuTiData("GPU供应商", mHashMapLocal.get("GPUVendor") + ""));
                    data.add(new JuTiData("GPU渲染器", mHashMapLocal.get("GPURenderer") + ""));
                    data.add(new JuTiData("GPU版本", mHashMapLocal.get("GPUVersion") + ""));
                } else {
                    data.add(new JuTiData("GPU供应商", CacheUtil.getString(CacheConst.KEY_GPU_VENDOR)));
                    data.add(new JuTiData("GPU渲染器", CacheUtil.getString(CacheConst.KEY_GPU_RENDER)));
                    data.add(new JuTiData("GPU版本", CacheUtil.getString(CacheConst.KEY_GPU_VERSION)));
                }
                break;
            }
            case CacheConst.KEY_ROM_INFO: {
                mHeadScore.setVisibility(View.GONE);
                data = new ArrayList<>();
                if (!isCloudPhone) {
                    if (mHashMapLocal instanceof HashMap) {
                        HashMap rom = (HashMap) mHashMapLocal.get("ROM");
                        data.add(new JuTiData("可用ROM", rom.get("可用") + ""));
                        data.add(new JuTiData("总共ROM", rom.get("总共") + ""));
                    }
                } else {
                    data.add(new JuTiData("可用ROM", CacheUtil.getString(CacheConst.KEY_AVAILABLE_STORAGE)));
                    data.add(new JuTiData("总共ROM", CacheUtil.getString(CacheConst.KEY_TOTAL_STORAGE)));
                }
                break;
            }
            case CacheConst.KEY_RAM_INFO: {
                mHeadScore.setVisibility(View.GONE);
                data = new ArrayList<>();
                if (!isCloudPhone) {
                    if (mHashMapLocal instanceof HashMap) {
                        HashMap ram = (HashMap) mHashMapLocal.get("RAM");
                        data.add(new JuTiData("可用RAM", ram.get("可用") + ""));
                        data.add(new JuTiData("总共RAM", ram.get("总共") + ""));
                    }
                } else {
                    data.add(new JuTiData("可用RAM", CacheUtil.getString(CacheConst.KEY_AVAILABLE_RAM)));
                    data.add(new JuTiData("总共RAM", CacheUtil.getString(CacheConst.KEY_TOTAL_RAM)));
                }
                break;
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.jutizhibiao_fanhui: {
                finish();
                break;
            }
            case R.id.juti_back_ceping: {
                startActivity(new Intent(JutiZhibiaoActivity.this, CePingActivity.class));
                break;
            }
        }
    }

    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @throws null
     * @description: 查询本机的规格数据
     * @date 2023/2/23 10:35
     */
    public Map<String, Object> getInfo() {
        // {ROM={可用=4.68 GB, 总共=6.24 GB}, CPUCores=4, RAM={可用=801 MB, 总共=2.05 GB}}
        Map<String, String> ramInfo = SDCardUtils.getRAMInfo(this);
        if (ramInfo.get("可用").equals(ramInfo.get("总共"))) {
            ramInfo.put("可用", MemInfoUtil.getMemAvailable());
        }
        for (Map.Entry<String, String> entry : ramInfo.entrySet()) {
            String value = entry.getValue().endsWith("吉字节") ? (entry.getValue().split("吉字节")[0]
                    + "GB") : entry.getValue();
            entry.setValue(value);
        }
        Log.d(TAG, "getInfo: " + ramInfo);
        Map<String, String> storageInfo = SDCardUtils.getStorageInfo(this, 0);
        for (Map.Entry<String, String> entry : storageInfo.entrySet()) {
            String value = entry.getValue().endsWith("吉字节") ? (entry.getValue().split("吉字节")[0]
                    + "GB") : entry.getValue();
            entry.setValue(value);
        }
        Log.d(TAG, "getInfo: " + storageInfo);
        int cpuNumCores = DeviceInfoUtils.getCpuNumCores();
        Map<String, Object> res = new HashMap<>();
        res.put("RAM", ramInfo);
        res.put("ROM", storageInfo);
        res.put("CPUCores", cpuNumCores);
        res.put("GPURenderer", GPURenderer.glRenderer);
        res.put("GPUVendor", GPURenderer.glVendor);
        res.put("GPUVersion", GPURenderer.glVersion);
        Log.d(TAG, "getInfo: res-----------+\n" + res);
        return res;
    }
}
