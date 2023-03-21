/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.benchmark.activity.CePingActivity;
import com.example.benchmark.BaseApp;
import com.example.benchmark.data.Admin;
import com.example.benchmark.dialog.PopDiaLog;
import com.example.benchmark.R;
import com.example.benchmark.service.MyAccessibilityService;
import com.example.benchmark.render.GPURenderer;
import com.example.benchmark.utils.AccessUtils;
import com.example.benchmark.utils.AccessibilityUtil;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;
import com.example.benchmark.utils.DeviceInfoUtils;
import com.example.benchmark.utils.MemInfoUtil;
import com.example.benchmark.utils.SDCardUtils;
import com.example.benchmark.utils.ServiceUtil;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GameFragment
 *
 * @version 1.0
 * @since 2023/3/7 15:14
 */
public class GameFragment extends Fragment implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener, CheckBox.OnCheckedChangeListener {
    private static final String TAG = "GameFragment";
    private static final HashMap<String, String> cheak_game_map;
    private static List<Boolean> list;

    static {
        cheak_game_map = new HashMap<>();
        list = new ArrayList<>();
    }

    private Button redLiuChang;
    private Button redWenDing;
    private Button redChuKong;
    private Button redYinHua;
    private GLSurfaceView mSurfaceView;
    private CheckBox redLiuChangCheck;
    private CheckBox redWenDingCheck;
    private CheckBox redChuKongCheck;
    private CheckBox redYinHuaCheck;
    private CheckBox redCpuCheck;
    private CheckBox redGpuCheck;
    private CheckBox redRamCheck;
    private CheckBox redRomCheck;
    private CheckBox gameSelectAll;
    private Button redCpu;
    private Button redGpu;
    private Button redRam;
    private Button redRom;
    private Button redStartTest;
    private RadioGroup selectGame;
    private AccessUtils accessUtils;
    private PopDiaLog popDiaLog;

    /**
     * onCreateView
     *
     * @param inflater description
     * @param container description
     * @param savedInstanceState description
     * @return android.view.View
     * @date 2023/3/9 19:44
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_fragment, container, false);
        initview(view);
        redLiuChang.setOnClickListener(this::onClick);
        redWenDing.setOnClickListener(this::onClick);
        redChuKong.setOnClickListener(this::onClick);
        redYinHua.setOnClickListener(this::onClick);
        redCpu.setOnClickListener(this::onClick);
        redGpu.setOnClickListener(this::onClick);
        redRam.setOnClickListener(this::onClick);
        redRom.setOnClickListener(this::onClick);
        gameSelectAll.setOnCheckedChangeListener(this::onCheckedChanged);
        selectGame.setOnCheckedChangeListener(this::onCheckedChanged);
        clickInit();
        return view;
    }

    private void clickInit() {
        redStartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = simpleDateFormat.format(date);
                Admin.testTime = time;
                Log.d(TAG, "onCreateView: 开始测试时间====" + Admin.testTime);
                check();
                Intent intent = new Intent(getActivity(), CePingActivity.class);
                intent.putExtra(CacheConst.KEY_PLATFORM_KIND, CacheConst.PLATFORM_KIND_CLOUD_GAME);
                intent.putExtra(CacheConst.KEY_FLUENCY_INFO, redLiuChangCheck.isChecked());
                intent.putExtra(CacheConst.KEY_STABILITY_INFO, redWenDingCheck.isChecked());
                intent.putExtra(CacheConst.KEY_TOUCH_INFO, redChuKongCheck.isChecked());
                intent.putExtra(CacheConst.KEY_SOUND_FRAME_INFO, redYinHuaCheck.isChecked());
                intent.putExtra(CacheConst.KEY_CPU_INFO, redCpuCheck.isChecked());
                intent.putExtra(CacheConst.KEY_GPU_INFO, redGpuCheck.isChecked());
                intent.putExtra(CacheConst.KEY_ROM_INFO, redRamCheck.isChecked());
                intent.putExtra(CacheConst.KEY_RAM_INFO, redRomCheck.isChecked());

                Map<String, Object> localMobileInfo = getInfo(); // 本地手机的规格数据
                if (redRomCheck.isChecked() || redRamCheck.isChecked() || redGpuCheck.isChecked()
                        || redCpuCheck.isChecked()) {
                    if (localMobileInfo instanceof Serializable) {
                        intent.putExtra("localMobileInfo", (Serializable) localMobileInfo);
                    }
                }
                intent.putExtra(CacheConst.KEY_PLATFORM_NAME, cheak_game_map.get("cheaked_game"));
                startActivity(intent);
            }
        });
    }

    private void check() {
        if (cheak_game_map.get("cheaked_game") == null) {
            Toast.makeText(getActivity(), "请选择需要测评的云游戏平台", Toast.LENGTH_LONG).show();
            return;
        }
        if (redWenDingCheck.isChecked() || redYinHuaCheck.isChecked()) {
            if (!AccessibilityUtil.isAccessibilityServiceEnabled(BaseApp.context)
                    || !ServiceUtil.isServiceRunning(BaseApp.context, MyAccessibilityService.class.getName())) {
                popDiaLog.show();
                return;
            }
        } else if (redChuKongCheck.isChecked()) {
            // 检查是否开启无障碍服务。。。。  Check whether barrier-free service is enabled...
            if (!ServiceUtil.isServiceRunning(BaseApp.context, MyAccessibilityService.class.getName())) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return;
            }
        } else {
            Log.d(TAG, "onCreateView: 正常启动");
        }
        if (!Settings.canDrawOverlays(getContext())) {
            Toast.makeText(getContext(), "请允许本应用显示悬浮窗！", Toast.LENGTH_SHORT).show();
            Intent intentToFloatPermission = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getContext().getPackageName()));
            Log.d("TWT", "toFloatGetPermission: " + Uri.parse("package:"
                    + getContext().getPackageName()));
            startActivity(intentToFloatPermission);
            return;
        } else {
            Toast.makeText(getContext(), "can draw floatWindow", Toast.LENGTH_SHORT);
        }
        CacheUtil.put(CacheConst.KEY_STABILITY_IS_MONITORED, false);
        CacheUtil.put(CacheConst.KEY_PERFORMANCE_IS_MONITORED, false);
    }

    private void initview(View view) {
        redLiuChang = view.findViewById(R.id.red_liuchangxing);
        redWenDing = view.findViewById(R.id.red_wendinxing);
        redChuKong = view.findViewById(R.id.red_chukong);
        redYinHua = view.findViewById(R.id.red_yinhua);

        mSurfaceView = view.findViewById(R.id.surfaceView);
        mSurfaceView.setEGLContextClientVersion(1);
        mSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        mSurfaceView.setRenderer(new GPURenderer());

        redCpu = view.findViewById(R.id.red_cpu);
        redGpu = view.findViewById(R.id.red_gpu);
        redRam = view.findViewById(R.id.red_ram);
        redRom = view.findViewById(R.id.red_rom);

        redLiuChangCheck = view.findViewById(R.id.red_liuchang_cheak);
        redWenDingCheck = view.findViewById(R.id.red_wending_cheak);
        redChuKongCheck = view.findViewById(R.id.red_chukong_cheak);
        redYinHuaCheck = view.findViewById(R.id.red_yinhua_cheak);

        redCpuCheck = view.findViewById(R.id.red_cpu_cheak);
        redGpuCheck = view.findViewById(R.id.red_gpu_cheak);
        redRamCheck = view.findViewById(R.id.red_ram_cheak);
        redRomCheck = view.findViewById(R.id.red_rom_cheak);

        redStartTest = view.findViewById(R.id.red_start_test);

        selectGame = view.findViewById(R.id.select_game);
        gameSelectAll = view.findViewById(R.id.game_select_all);

        accessUtils = new AccessUtils(getContext());
        popDiaLog = new PopDiaLog(getActivity());
    }

    private void initGameBtn() {
        // 腾讯  Tencent
        if (getActivity().findViewById(R.id.tengxun_game) instanceof Button) {
            Button btn = (Button) getActivity().findViewById(R.id.tengxun_game);
            Drawable drawable = getResources().getDrawable(R.drawable.tengxunxianfeng_dark);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            btn.setCompoundDrawables(null, drawable, null, null); // 设置底图标  Set bottom icon
        }

        // 咪咕  MiGu
        if (getActivity().findViewById(R.id.migu_game) instanceof Button) {
            Button btn = (Button) getActivity().findViewById(R.id.migu_game);
            Drawable drawable = getResources().getDrawable(R.drawable.migukuaiyou_dark);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            btn.setCompoundDrawables(null, drawable, null, null); // 设置底图标  Set bottom icon
        }

        // 网易  Netease
        if (getActivity().findViewById(R.id.wangyi_game) instanceof Button) {
            Button btn = (Button) getActivity().findViewById(R.id.wangyi_game);
            Drawable drawable = getResources().getDrawable(R.drawable.wangyiyunyouxi_dark);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            btn.setCompoundDrawables(null, drawable, null, null); // 设置底图标  Set bottom icon
        }
    }

    /**
     * onClick
     *
     * @param vi description
     * @date 2023/3/8 15:23
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View vi) {
        switch (vi.getId()) {
            case R.id.red_liuchangxing: {
                liuChangClick();
                break;
            }
            case R.id.red_wendinxing: {
                wenDingClick();
                break;
            }
            case R.id.red_chukong: {
                chuKongClick();
                break;
            }
            case R.id.red_yinhua: {
                yinHuaClick();
                break;
            }
            case R.id.red_cpu: {
                cpuClick();
                break;
            }
            case R.id.red_gpu: {
                gpuClick();
                break;
            }
            case R.id.red_ram: {
                ramClick();
                break;
            }
            case R.id.red_rom: {
                romClick();
                break;
            }
        }
    }

    private void romClick() {
        boolean isChecked = redRomCheck.isChecked();
        if (isChecked) {
            redRomCheck.setVisibility(View.INVISIBLE);
            redRomCheck.setChecked(false);
            gameSelectAll.setChecked(false);
        } else {
            redRomCheck.setVisibility(View.VISIBLE);
            redRomCheck.setChecked(true);
        }
    }

    private void ramClick() {
        boolean isChecked = redRamCheck.isChecked();
        if (isChecked) {
            redRamCheck.setVisibility(View.INVISIBLE);
            redRamCheck.setChecked(false);
            gameSelectAll.setChecked(false);
        } else {
            redRamCheck.setVisibility(View.VISIBLE);
            redRamCheck.setChecked(true);
        }
    }

    private void gpuClick() {
        boolean isChecked = redGpuCheck.isChecked();
        if (isChecked) {
            redGpuCheck.setVisibility(View.INVISIBLE);
            redGpuCheck.setChecked(false);
            gameSelectAll.setChecked(false);
        } else {
            redGpuCheck.setVisibility(View.VISIBLE);
            redGpuCheck.setChecked(true);
        }
    }

    private void cpuClick() {
        boolean isChecked = redCpuCheck.isChecked();
        if (isChecked) {
            redCpuCheck.setVisibility(View.INVISIBLE);
            redCpuCheck.setChecked(false);
            gameSelectAll.setChecked(false);
        } else {
            redCpuCheck.setVisibility(View.VISIBLE);
            redCpuCheck.setChecked(true);
        }
    }

    private void yinHuaClick() {
        boolean isChecked = redYinHuaCheck.isChecked();
        if (isChecked) {
            redYinHuaCheck.setVisibility(View.INVISIBLE);
            redYinHuaCheck.setChecked(false);
            gameSelectAll.setChecked(false);
        } else {
            redYinHuaCheck.setVisibility(View.VISIBLE);
            redYinHuaCheck.setChecked(true);
        }
    }

    private void chuKongClick() {
        boolean isChecked = redChuKongCheck.isChecked();
        if (isChecked) {
            redChuKongCheck.setChecked(false);
            redChuKongCheck.setVisibility(View.INVISIBLE);
            gameSelectAll.setChecked(false);
        } else {
            redChuKongCheck.setVisibility(View.VISIBLE);
            redChuKongCheck.setChecked(true);
        }
    }

    private void wenDingClick() {
        boolean isChecked = redWenDingCheck.isChecked();
        if (isChecked) {
            redWenDingCheck.setChecked(false);
            redWenDingCheck.setVisibility(View.INVISIBLE);
            gameSelectAll.setChecked(false);
        } else {
            redWenDingCheck.setVisibility(View.VISIBLE);
            redWenDingCheck.setChecked(true);
        }
    }

    private void liuChangClick() {
        boolean isChecked = redLiuChangCheck.isChecked();
        if (isChecked) {
            redLiuChangCheck.setChecked(false);
            redLiuChangCheck.setVisibility(View.INVISIBLE);
            gameSelectAll.setChecked(false);
        } else {
            redLiuChangCheck.setChecked(true);
            redLiuChangCheck.setVisibility(View.VISIBLE);
        }
    }

    /**
     * onCheckedChanged
     *
     * @param group description
     * @param checkedId description
     * @date 2023/3/8 15:23
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.tengxun_game: {
                cheak_game_map.put("cheaked_game", CacheConst.PLATFORM_NAME_TENCENT_GAME);
                initGameBtn();
                if (getActivity().findViewById(R.id.tengxun_game) instanceof Button) {
                    Button btn = (Button) getActivity().findViewById(R.id.tengxun_game);
                    Drawable drawable = getResources().getDrawable(R.drawable.tengxunxianfeng);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    btn.setCompoundDrawables(null, drawable, null, null); // 设置底图标  Set bottom icon
                }
                break;
            }
            case R.id.migu_game: {
                cheak_game_map.put("cheaked_game", CacheConst.PLATFORM_NAME_MI_GU_GAME);
                initGameBtn();
                if (getActivity().findViewById(R.id.migu_game) instanceof Button) {
                    Button btn = (Button) getActivity().findViewById(R.id.migu_game);
                    Drawable drawable = getResources().getDrawable(R.drawable.migukuaiyou);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    btn.setCompoundDrawables(null, drawable, null, null); // 设置底图标  Set bottom icon
                }
                break;
            }
            case R.id.wangyi_game: {
                cheak_game_map.put("cheaked_game", CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_GAME);
                initGameBtn();
                if (getActivity().findViewById(R.id.wangyi_game) instanceof Button) {
                    Button btn = (Button) getActivity().findViewById(R.id.wangyi_game);
                    Drawable drawable = getResources().getDrawable(R.drawable.wangyiyunyouxi);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    btn.setCompoundDrawables(null, drawable, null, null); // 设置底图标  Set bottom icon
                }
                break;
            }
        }
    }

    /**
     * onCheckedChanged
     *
     * @param buttonView description
     * @param isChecked description
     * @date 2023/3/8 15:23
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            redLiuChangCheck.setChecked(true);
            redLiuChangCheck.setVisibility(View.VISIBLE);

            redWenDingCheck.setVisibility(View.VISIBLE);
            redWenDingCheck.setChecked(true);

            redChuKongCheck.setVisibility(View.VISIBLE);
            redChuKongCheck.setChecked(true);

            redYinHuaCheck.setVisibility(View.VISIBLE);
            redYinHuaCheck.setChecked(true);

            redCpuCheck.setVisibility(View.VISIBLE);
            redCpuCheck.setChecked(true);

            redGpuCheck.setVisibility(View.VISIBLE);
            redGpuCheck.setChecked(true);

            redRamCheck.setVisibility(View.VISIBLE);
            redRamCheck.setChecked(true);

            redRomCheck.setVisibility(View.VISIBLE);
            redRomCheck.setChecked(true);
        } else {
            redLiuChangCheck.setChecked(false);
            redLiuChangCheck.setVisibility(View.INVISIBLE);

            redWenDingCheck.setChecked(false);
            redWenDingCheck.setVisibility(View.INVISIBLE);

            redChuKongCheck.setChecked(false);
            redChuKongCheck.setVisibility(View.INVISIBLE);

            redYinHuaCheck.setVisibility(View.INVISIBLE);
            redYinHuaCheck.setChecked(false);

            redCpuCheck.setVisibility(View.INVISIBLE);
            redCpuCheck.setChecked(false);

            redGpuCheck.setVisibility(View.INVISIBLE);
            redGpuCheck.setChecked(false);

            redRamCheck.setVisibility(View.INVISIBLE);
            redRamCheck.setChecked(false);

            redRomCheck.setVisibility(View.INVISIBLE);
            redRomCheck.setChecked(false);
        }
    }

    /**
     * getInfo
     *
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @date 2023/3/8 15:23
     */
    public Map<String, Object> getInfo() {
        // {ROM={可用=4.68 GB, 总共=6.24 GB}, CPUCores=4, RAM={可用=801 MB, 总共=2.05 GB}}
        // {ROM={available = 4.68GB, total = 6.24GB}, CPUCores=4, RAM={available =801 MB, total = 2.05GB}}
        Map<String, String> ramInfo = SDCardUtils.getRAMInfo(getContext());
        if (ramInfo.get("可用").equals(ramInfo.get("总共"))) {
            ramInfo.put("可用", MemInfoUtil.getMemAvailable());
        }
        for (Map.Entry<String, String> entry : ramInfo.entrySet()) {
            String value = entry.getValue().endsWith("吉字节")
                    ? (entry.getValue().split("吉字节")[0] + "GB") : entry.getValue();
            entry.setValue(value);
        }
        Log.d(TAG, "getInfo: " + ramInfo);

        Map<String, String> storageInfo = SDCardUtils.getStorageInfo(getContext(), 0);
        for (Map.Entry<String, String> entry : storageInfo.entrySet()) {
            String value = entry.getValue().endsWith("吉字节") ? (entry.getValue().split("吉字节")[0] + "GB")
                    : entry.getValue();
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
        Log.d(TAG, "getInfo: res:" + res);
        return res;
    }
}
