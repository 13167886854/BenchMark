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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.benchmark.activity.CePingActivity;
import com.example.benchmark.data.Admin;
import com.example.benchmark.dialog.PopDiaLog;
import com.example.benchmark.R;
import com.example.benchmark.dialog.IpPortDialog;
import com.example.benchmark.utils.AccessUtils;

import com.example.benchmark.BaseApp;
import com.example.benchmark.service.MyAccessibilityService;
import com.example.benchmark.utils.AccessibilityUtil;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;
import com.example.benchmark.utils.ServiceUtil;
import com.example.benchmark.utils.ThreadPoolUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * PhoneFragment
 *
 * @version 1.0
 * @since 2023/3/7 15:17
 */
public class PhoneFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "PhoneFragment";

    private final HashMap<String, String> checkPhoneMap = new HashMap<>();
    private final int allCheckCount = 8;

    private Button blueLiuChang;
    private Button blueWenDing;
    private Button blueChuKong;
    private Button blueYinHua;

    private CheckBox blueLiuChangCheck;
    private CheckBox blueWenDingCheck;
    private CheckBox blueChuKongCheck;
    private CheckBox blueYinHuaCheck;

    private IpPortDialog myDialog;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                myDialog.getYes().setEnabled(true);
            } else if (msg.what == 2) {
                myDialog.getYes().setEnabled(true);
                myDialog.dismiss();
            } else {
                Log.d(TAG, "handleMessage: ");
            }
        }
    };

    private CheckBox blueCpuCheck;
    private CheckBox blueGpuCheck;
    private CheckBox blueRamCheck;
    private CheckBox blueRomCheck;
    private CheckBox phoneSelectAll;

    private Button blueCpu;
    private Button blueGpu;
    private Button blueRam;
    private Button blueRom;

    private Button phoneStartCePing;

    private Button kunPengPhone;
    private Button huaWeiDataPhone;
    private Button redFingerPhone;
    private Button yiDongPhone;
    private Button wangYiYUnPhone;

    private AccessUtils accessUtils;
    private PopDiaLog popDiaLog;

    private int mCheckCounts = allCheckCount;

    /**
     * onCreateView
     *
     * @param inflater           description
     * @param container          description
     * @param savedInstanceState description
     * @return android.view.View
     * @date 2023/3/9 19:34
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.phone_fragment, container, false);
        initview(view);
        kunPengPhone.setOnClickListener(this);
        huaWeiDataPhone.setOnClickListener(this);
        redFingerPhone.setOnClickListener(this);
        yiDongPhone.setOnClickListener(this);
        wangYiYUnPhone.setOnClickListener(this);
        blueLiuChang.setOnClickListener(this::onClick);
        blueWenDing.setOnClickListener(this::onClick);
        blueChuKong.setOnClickListener(this::onClick);
        blueYinHua.setOnClickListener(this::onClick);

        blueCpu.setOnClickListener(this::onClick);
        blueGpu.setOnClickListener(this::onClick);
        blueRam.setOnClickListener(this::onClick);
        blueRom.setOnClickListener(this::onClick);
        phoneSelectAll.setOnClickListener(this::onClick);

        phoneStartCePing.setOnClickListener(v -> {
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = simpleDateFormat.format(date);
            Admin.getInstance().setTestTime(time);
            Log.d(TAG, "onCreateView: 开始测试时间====" + Admin.getInstance().getTestTime());
            if (checkPhoneMap.get(CacheConst.KEY_PLATFORM_NAME) == null) {
                Toast.makeText(getActivity(), "请选择需要测评的云手机平台", Toast.LENGTH_LONG).show();
                return;
            }
            afterCode();
        });
        return view;
    }


    private void afterCode() {
        if (blueWenDingCheck.isChecked()) {
            Log.e(TAG, "afterCode: 111111111111111111111111111");
            if (!AccessibilityUtil.isAccessibilityServiceEnabled(BaseApp.getContext())
                    || !ServiceUtil.isServiceRunning(BaseApp.getContext(), MyAccessibilityService.class.getName())) {
                popDiaLog.show();
                return;
            }
        }
        if (blueChuKongCheck.isChecked()) {
            if (!ServiceUtil.isServiceRunning(BaseApp.getContext(), MyAccessibilityService.class.getName())) {
                popDiaLog.show();
                return;
            }
        }
        if (blueYinHuaCheck.isChecked()) {
            if (!ServiceUtil.isServiceRunning(BaseApp.getContext(), MyAccessibilityService.class.getName())) {
                popDiaLog.show();
                return;
            }
        }
        if (!Settings.canDrawOverlays(getContext())) {
            Toast.makeText(getContext(), "请允许本应用显示悬浮窗！", Toast.LENGTH_SHORT).show();
            Intent intentToFloatPermission = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"
                    + getContext().getPackageName()));
            Log.d("TWT", "toFloatGetPermission: " + Uri.parse("package:" + getContext().getPackageName()));
            this.startActivity(intentToFloatPermission);
            return;
        }
        if (checkPhoneMap.get(CacheConst.KEY_PLATFORM_NAME) == CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_PHONE) {
            // 检测华为云手机测试 提示用户输入ip地址加端口
            Log.e(TAG, "onCreateView: hiahiasadsad");
            showDialog();
        }
        if (checkPhoneMap.get(CacheConst.KEY_PLATFORM_NAME) == CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_GAME) {
            Log.e(TAG, "onCreateView: hiahiasadsad");
            showDialog();
        }
    }


    private void initview(View view) {
        blueLiuChang = view.findViewById(R.id.bule_liuchangxing);
        blueWenDing = view.findViewById(R.id.bule_wendinxing);
        blueChuKong = view.findViewById(R.id.bule_chukong);
        blueYinHua = view.findViewById(R.id.bule_yinhua);

        blueCpu = view.findViewById(R.id.bule_cpu);
        blueGpu = view.findViewById(R.id.bule_gpu);
        blueRam = view.findViewById(R.id.bule_ram);
        blueRom = view.findViewById(R.id.bule_rom);

        blueLiuChangCheck = view.findViewById(R.id.blue_liuchang_cheak);
        blueWenDingCheck = view.findViewById(R.id.blue_wending_cheak);
        blueChuKongCheck = view.findViewById(R.id.blue_chukong_cheak);
        blueYinHuaCheck = view.findViewById(R.id.blue_yinhua_cheak);

        blueCpuCheck = view.findViewById(R.id.blue_cpu_cheak);
        blueGpuCheck = view.findViewById(R.id.blue_gpu_cheak);
        blueRamCheck = view.findViewById(R.id.blue_ram_cheak);
        blueRomCheck = view.findViewById(R.id.blue_rom_cheak);

        phoneSelectAll = view.findViewById(R.id.phone_select_all);

        phoneStartCePing = view.findViewById(R.id.blue_start_test);

        kunPengPhone = view.findViewById(R.id.kunpeng_phone);
        huaWeiDataPhone = view.findViewById(R.id.kunpeng_data_phone);
        redFingerPhone = view.findViewById(R.id.redfigure_phone);
        yiDongPhone = view.findViewById(R.id.yidong_phone);
        wangYiYUnPhone = view.findViewById(R.id.wangyiyun_phone);
        accessUtils = new AccessUtils(getContext());
        popDiaLog = new PopDiaLog(requireActivity());
    }

    private void initPhoneBtn() {
        // 移动云
        if (getActivity().findViewById(R.id.yidong_phone) instanceof Button) {
            Button btn = (Button) getActivity().findViewById(R.id.yidong_phone);
            if (getResources().getDrawable(R.drawable.yidong_phone_dark) instanceof Drawable) {
                Drawable drawable = getResources().getDrawable(R.drawable.yidong_phone_dark);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                btn.setCompoundDrawables(null, drawable, null, null); // 设置底图标
            }
        }
        // 网易
        if (getActivity().findViewById(R.id.wangyiyun_phone) instanceof Button) {
            Button btn = (Button) getActivity().findViewById(R.id.wangyiyun_phone);
            if (getResources().getDrawable(R.drawable.wangyi_phone_dark) instanceof Drawable) {
                Drawable drawable = getResources().getDrawable(R.drawable.yidong_phone_dark);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                btn.setCompoundDrawables(null, drawable, null, null); // 设置底图标
            }
        }
        // 华为
        if (getActivity().findViewById(R.id.kunpeng_phone) instanceof Button) {
            Button btn = (Button) getActivity().findViewById(R.id.kunpeng_phone);
            if (getResources().getDrawable(R.drawable.kunpeng_phone_dark) instanceof Drawable) {
                Drawable drawable = getResources().getDrawable(R.drawable.kunpeng_phone_dark);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                btn.setCompoundDrawables(null, drawable, null, null); // 设置底图标
            }
        }
        if (getActivity().findViewById(R.id.kunpeng_data_phone) instanceof Button) {
            Button btn = (Button) getActivity().findViewById(R.id.kunpeng_data_phone);
            if (getResources().getDrawable(R.drawable.kunpeng_phone_dark) instanceof Drawable) {
                Drawable drawable = getResources().getDrawable(R.drawable.kunpeng_phone_dark);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                btn.setCompoundDrawables(null, drawable, null, null); // 设置底图标
            }
        }
        // 红手指
        if (getActivity().findViewById(R.id.redfigure_phone) instanceof Button) {
            Button btn = (Button) getActivity().findViewById(R.id.redfigure_phone);
            if (getResources().getDrawable(R.drawable.redfingure_dark) instanceof Drawable) {
                Drawable drawable = getResources().getDrawable(R.drawable.redfingure_dark);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                btn.setCompoundDrawables(null, drawable, null, null); // 设置底图标
            }
        }
    }

    /**
     * onClick
     *
     * @param vi description
     * @date 2023/3/8 16:22
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View vi) {
        switch1(vi);
        switch2(vi);
    }

    private void switch2(View vi) {
        switch (vi.getId()) {
            case R.id.bule_chukong: {
                touch();
                break;
            }
            case R.id.bule_yinhua: {
                audioVideo();
                break;
            }
            case R.id.bule_cpu: {
                cpu();
                break;
            }
            case R.id.bule_gpu: {
                gpu();
                break;
            }
            case R.id.bule_ram: {
                ram();
                break;
            }
            case R.id.bule_rom: {
                rom();
                break;
            }
            case R.id.phone_select_all: {
                selectAll();
            }
        }
    }

    private void switch1(View vi) {
        switch (vi.getId()) {
            case R.id.kunpeng_phone: {
                kunPengPhone(vi);
                break;
            }
            case R.id.kunpeng_data_phone: {
                kunPengDataPhone(vi);
                break;
            }
            case R.id.redfigure_phone: {
                redFingerPhone(vi);
                break;
            }
            case R.id.yidong_phone: {
                yiDongPhone(vi);
                break;
            }
            case R.id.wangyiyun_phone: {
                wangYiYunPhone(vi);
                break;
            }
            case R.id.bule_liuchangxing: {
                flucency();
                break;
            }
            case R.id.bule_wendinxing: {
                stability();
                break;
            }
        }
    }

    private void selectAll() {
        boolean isCheckedAll = phoneSelectAll.isChecked();
        if (isCheckedAll) {
            blueLiuChangCheck.setChecked(true);
            blueLiuChangCheck.setVisibility(View.VISIBLE);
            blueWenDingCheck.setVisibility(View.VISIBLE);
            blueWenDingCheck.setChecked(true);
            blueChuKongCheck.setVisibility(View.VISIBLE);
            blueChuKongCheck.setChecked(true);
            blueYinHuaCheck.setVisibility(View.VISIBLE);
            blueYinHuaCheck.setChecked(true);
            blueCpuCheck.setVisibility(View.VISIBLE);
            blueCpuCheck.setChecked(true);
            blueGpuCheck.setVisibility(View.VISIBLE);
            blueGpuCheck.setChecked(true);
            blueRamCheck.setVisibility(View.VISIBLE);
            blueRamCheck.setChecked(true);
            blueRomCheck.setVisibility(View.VISIBLE);
            blueRomCheck.setChecked(true);
            mCheckCounts = 8;
        } else {
            blueLiuChangCheck.setChecked(false);
            blueLiuChangCheck.setVisibility(View.INVISIBLE);
            blueWenDingCheck.setChecked(false);
            blueWenDingCheck.setVisibility(View.INVISIBLE);
            blueChuKongCheck.setChecked(false);
            blueChuKongCheck.setVisibility(View.INVISIBLE);
            blueYinHuaCheck.setVisibility(View.INVISIBLE);
            blueYinHuaCheck.setChecked(false);
            blueCpuCheck.setVisibility(View.INVISIBLE);
            blueCpuCheck.setChecked(false);
            blueGpuCheck.setVisibility(View.INVISIBLE);
            blueGpuCheck.setChecked(false);
            blueRamCheck.setVisibility(View.INVISIBLE);
            blueRamCheck.setChecked(false);
            blueRomCheck.setVisibility(View.INVISIBLE);
            blueRomCheck.setChecked(false);
            mCheckCounts = 0;
        }
    }

    private void rom() {
        boolean isChecked = blueRomCheck.isChecked();
        if (isChecked) {
            blueRomCheck.setVisibility(View.INVISIBLE);
            blueRomCheck.setChecked(false);
            phoneSelectAll.setChecked(false);
            mCheckCounts--;
            blueCpuCheck.setVisibility(View.INVISIBLE);
            blueCpuCheck.setChecked(false);
            phoneSelectAll.setChecked(false);
            mCheckCounts--;
            blueGpuCheck.setVisibility(View.INVISIBLE);
            blueGpuCheck.setChecked(false);
            phoneSelectAll.setChecked(false);
            mCheckCounts--;
            blueRamCheck.setVisibility(View.INVISIBLE);
            blueRamCheck.setChecked(false);
            phoneSelectAll.setChecked(false);
            mCheckCounts--;
        } else {
            blueRomCheck.setVisibility(View.VISIBLE);
            blueRomCheck.setChecked(true);
            mCheckCounts++;
            if (mCheckCounts == allCheckCount) {
                phoneSelectAll.setChecked(true);
            }
            blueCpuCheck.setVisibility(View.VISIBLE);
            blueCpuCheck.setChecked(true);
            mCheckCounts++;
            if (mCheckCounts == allCheckCount) {
                phoneSelectAll.setChecked(true);
            }
            blueGpuCheck.setVisibility(View.VISIBLE);
            blueGpuCheck.setChecked(true);
            mCheckCounts++;
            if (mCheckCounts == allCheckCount) {
                phoneSelectAll.setChecked(true);
            }
            blueRamCheck.setVisibility(View.VISIBLE);
            blueRamCheck.setChecked(true);
            mCheckCounts++;
            if (mCheckCounts == allCheckCount) {
                phoneSelectAll.setChecked(true);
            }
        }
    }

    private void ram() {
        boolean isChecked = blueRamCheck.isChecked();
        if (isChecked) {
            blueRamCheck.setVisibility(View.INVISIBLE);
            blueRamCheck.setChecked(false);
            phoneSelectAll.setChecked(false);
            mCheckCounts--;
            blueCpuCheck.setVisibility(View.INVISIBLE);
            blueCpuCheck.setChecked(false);
            phoneSelectAll.setChecked(false);
            mCheckCounts--;
            blueGpuCheck.setVisibility(View.INVISIBLE);
            blueGpuCheck.setChecked(false);
            phoneSelectAll.setChecked(false);
            mCheckCounts--;
            blueRomCheck.setVisibility(View.INVISIBLE);
            blueRomCheck.setChecked(false);
            phoneSelectAll.setChecked(false);
            mCheckCounts--;
        } else {
            blueRamCheck.setVisibility(View.VISIBLE);
            blueRamCheck.setChecked(true);
            mCheckCounts++;
            if (mCheckCounts == allCheckCount) {
                phoneSelectAll.setChecked(true);
            }
            blueCpuCheck.setVisibility(View.VISIBLE);
            blueCpuCheck.setChecked(true);
            mCheckCounts++;
            if (mCheckCounts == allCheckCount) {
                phoneSelectAll.setChecked(true);
            }
            blueGpuCheck.setVisibility(View.VISIBLE);
            blueGpuCheck.setChecked(true);
            mCheckCounts++;
            if (mCheckCounts == allCheckCount) {
                phoneSelectAll.setChecked(true);
            }
            blueRomCheck.setVisibility(View.VISIBLE);
            blueRomCheck.setChecked(true);
            mCheckCounts++;
            if (mCheckCounts == allCheckCount) {
                phoneSelectAll.setChecked(true);
            }
        }
    }

    private void gpu() {
        boolean isChecked = blueGpuCheck.isChecked();
        if (isChecked) {
            blueGpuCheck.setVisibility(View.INVISIBLE);
            blueGpuCheck.setChecked(false);
            phoneSelectAll.setChecked(false);
            mCheckCounts--;
            blueCpuCheck.setVisibility(View.INVISIBLE);
            blueCpuCheck.setChecked(false);
            phoneSelectAll.setChecked(false);
            mCheckCounts--;
            blueRamCheck.setVisibility(View.INVISIBLE);
            blueRamCheck.setChecked(false);
            phoneSelectAll.setChecked(false);
            mCheckCounts--;
            blueRomCheck.setVisibility(View.INVISIBLE);
            blueRomCheck.setChecked(false);
            phoneSelectAll.setChecked(false);
            mCheckCounts--;
        } else {
            blueGpuCheck.setVisibility(View.VISIBLE);
            blueGpuCheck.setChecked(true);
            mCheckCounts++;
            if (mCheckCounts == allCheckCount) {
                phoneSelectAll.setChecked(true);
            }
            blueCpuCheck.setVisibility(View.VISIBLE);
            blueCpuCheck.setChecked(true);
            mCheckCounts++;
            if (mCheckCounts == allCheckCount) {
                phoneSelectAll.setChecked(true);
            }
            blueRamCheck.setVisibility(View.VISIBLE);
            blueRamCheck.setChecked(true);
            mCheckCounts++;
            if (mCheckCounts == allCheckCount) {
                phoneSelectAll.setChecked(true);
            }
            blueRomCheck.setVisibility(View.VISIBLE);
            blueRomCheck.setChecked(true);
            mCheckCounts++;
            if (mCheckCounts == allCheckCount) {
                phoneSelectAll.setChecked(true);
            }
        }
    }

    private void cpu() {
        boolean isChecked = blueCpuCheck.isChecked();
        if (isChecked) {
            blueCpuCheck.setVisibility(View.INVISIBLE);
            blueCpuCheck.setChecked(false);
            phoneSelectAll.setChecked(false);
            mCheckCounts--;

            blueGpuCheck.setVisibility(View.INVISIBLE);
            blueGpuCheck.setChecked(false);
            phoneSelectAll.setChecked(false);
            mCheckCounts--;

            blueRamCheck.setVisibility(View.INVISIBLE);
            blueRamCheck.setChecked(false);
            phoneSelectAll.setChecked(false);
            mCheckCounts--;

            blueRomCheck.setVisibility(View.INVISIBLE);
            blueRomCheck.setChecked(false);
            phoneSelectAll.setChecked(false);
            mCheckCounts--;
        } else {
            blueCpuCheck.setVisibility(View.VISIBLE);
            blueCpuCheck.setChecked(true);
            mCheckCounts++;
            if (mCheckCounts == allCheckCount) {
                phoneSelectAll.setChecked(true);
            }
            blueGpuCheck.setVisibility(View.VISIBLE);
            blueGpuCheck.setChecked(true);
            mCheckCounts++;
            if (mCheckCounts == allCheckCount) {
                phoneSelectAll.setChecked(true);
            }
            blueRamCheck.setVisibility(View.VISIBLE);
            blueRamCheck.setChecked(true);
            mCheckCounts++;
            if (mCheckCounts == allCheckCount) {
                phoneSelectAll.setChecked(true);
            }
            blueRomCheck.setVisibility(View.VISIBLE);
            blueRomCheck.setChecked(true);
            mCheckCounts++;
            if (mCheckCounts == allCheckCount) {
                phoneSelectAll.setChecked(true);
            }
        }
    }

    private void audioVideo() {
        boolean isChecked = blueYinHuaCheck.isChecked();
        if (isChecked) {
            blueYinHuaCheck.setVisibility(View.INVISIBLE);
            blueYinHuaCheck.setChecked(false);
            phoneSelectAll.setChecked(false);
            mCheckCounts--;
        } else {
            blueYinHuaCheck.setVisibility(View.VISIBLE);
            blueYinHuaCheck.setChecked(true);
            mCheckCounts++;
            if (mCheckCounts == allCheckCount) {
                phoneSelectAll.setChecked(true);
            }
        }
    }

    private void touch() {
        boolean isChecked = blueChuKongCheck.isChecked();
        if (isChecked) {
            blueChuKongCheck.setVisibility(View.INVISIBLE);
            blueChuKongCheck.setChecked(false);
            phoneSelectAll.setChecked(false);
            mCheckCounts--;
        } else {
            blueChuKongCheck.setVisibility(View.VISIBLE);
            blueChuKongCheck.setChecked(true);
            mCheckCounts++;
            if (mCheckCounts == allCheckCount) {
                phoneSelectAll.setChecked(true);
            }
        }
    }

    private void stability() {
        boolean isChecked = blueWenDingCheck.isChecked();
        if (isChecked) {
            blueWenDingCheck.setVisibility(View.INVISIBLE);
            blueWenDingCheck.setChecked(false);
            phoneSelectAll.setChecked(false);
            mCheckCounts--;
        } else {
            blueWenDingCheck.setVisibility(View.VISIBLE);
            blueWenDingCheck.setChecked(true);
            mCheckCounts++;
            if (mCheckCounts == allCheckCount) {
                phoneSelectAll.setChecked(true);
            }
        }
    }

    private void flucency() {
        boolean isChecked = blueLiuChangCheck.isChecked();
        if (isChecked) {
            blueLiuChangCheck.setVisibility(View.INVISIBLE);
            blueLiuChangCheck.setChecked(false);
            phoneSelectAll.setChecked(false);
            mCheckCounts--;
        } else {
            blueLiuChangCheck.setChecked(true);
            blueLiuChangCheck.setVisibility(View.VISIBLE);
            mCheckCounts++;
            if (mCheckCounts == allCheckCount) {
                phoneSelectAll.setChecked(true);
            }
        }
    }

    private void wangYiYunPhone(View vi) {
        checkPhoneMap.put(CacheConst.KEY_PLATFORM_NAME, CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_PHONE);
        initPhoneBtn();
        if (vi instanceof Button) {
            Button btn = (Button) vi;
            Drawable drawable = getResources().getDrawable(R.drawable.wangyi_phone);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            btn.setCompoundDrawables(null, drawable, null, null); // 设置底图标
        }
    }

    private void yiDongPhone(View vi) {
        checkPhoneMap.put(CacheConst.KEY_PLATFORM_NAME, CacheConst.PLATFORM_NAME_E_CLOUD_PHONE);
        initPhoneBtn();
        if (vi instanceof Button) {
            Button btn = (Button) vi;
            Drawable drawable = getResources().getDrawable(R.drawable.yidong_phone);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            btn.setCompoundDrawables(null, drawable, null, null); // 设置底图标
        }
    }

    private void redFingerPhone(View vi) {
        checkPhoneMap.put(CacheConst.KEY_PLATFORM_NAME, CacheConst.PLATFORM_NAME_RED_FINGER_CLOUD_PHONE);
        initPhoneBtn();
        if (vi instanceof Button) {
            Button btn = (Button) vi;
            Drawable drawable = getResources().getDrawable(R.drawable.redfingure);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            btn.setCompoundDrawables(null, drawable, null, null); // 设置底图标
        }
    }

    private void kunPengDataPhone(View vi) {
        checkPhoneMap.put(CacheConst.KEY_PLATFORM_NAME, CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_GAME);
        initPhoneBtn();
        if (vi instanceof Button) {
            Button btn = (Button) vi;
            Drawable drawable = getResources().getDrawable(R.drawable.kunpeng_phone);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            btn.setCompoundDrawables(null, drawable, null, null); // 设置底图标
        }
    }

    private void kunPengPhone(View vi) {
        checkPhoneMap.put(CacheConst.KEY_PLATFORM_NAME, CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_PHONE);
        initPhoneBtn();
        if (vi instanceof Button) {
            Button btn = (Button) vi;
            Drawable drawable = getResources().getDrawable(R.drawable.kunpeng_phone);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            btn.setCompoundDrawables(null, drawable, null, null); // 设置底图标
        }
    }

    /**
     * showDialog
     *
     * @date 2023/3/8 16:22
     */
    public void showDialog() {
        Log.e(TAG, "PhoneFragement-showDialog: ");
        myDialog = new IpPortDialog(getContext());
        myDialog.setNoOnclickListener("取消", new IpPortDialog.OnNoOnclickListener() {
            @Override
            public void onNoClick() {
                myDialog.dismiss();
            }
        });
        ThreadPoolUtil.getPool().execute(new Runnable() {
            @Override
            public void run() {
                myDialog.setYesOnclickListener("确定", new IpPortDialog.OnYesOnclickListener() {
                    @Override
                    public void onYesClick() {
                        myDialog.getYes().setEnabled(false);
                        myDialog.dismiss();
                        Log.d(TAG, "输入IP地址");
                        CacheUtil.put(CacheConst.KEY_STABILITY_IS_MONITORED, false);
                        CacheUtil.put(CacheConst.KEY_PERFORMANCE_IS_MONITORED, false);
                        Intent intent = new Intent(getActivity(), CePingActivity.class);

                        // 传入checkbox是否被选中
                        intent.putExtra(CacheConst.KEY_PLATFORM_KIND, CacheConst.PLATFORM_KIND_CLOUD_PHONE);
                        intent.putExtra(CacheConst.KEY_FLUENCY_INFO, blueLiuChangCheck.isChecked());
                        intent.putExtra(CacheConst.KEY_STABILITY_INFO, blueWenDingCheck.isChecked());
                        intent.putExtra(CacheConst.KEY_TOUCH_INFO, blueChuKongCheck.isChecked());
                        intent.putExtra(CacheConst.KEY_SOUND_FRAME_INFO, blueYinHuaCheck.isChecked());
                        intent.putExtra(CacheConst.KEY_CPU_INFO, blueCpuCheck.isChecked());
                        intent.putExtra(CacheConst.KEY_GPU_INFO, blueGpuCheck.isChecked());
                        intent.putExtra(CacheConst.KEY_ROM_INFO, blueRomCheck.isChecked());
                        intent.putExtra(CacheConst.KEY_RAM_INFO, blueRamCheck.isChecked());
                        intent.putExtra(CacheConst.KEY_PLATFORM_NAME,
                                checkPhoneMap.get(CacheConst.KEY_PLATFORM_NAME));
                        startActivity(intent);
                    }
                });
            }
        });
        myDialog.show();
        Window dialogWindow = myDialog.getWindow();
        WindowManager manager = getActivity().getWindowManager();
        Display display = manager.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        params.height = (int) (display.getHeight() * 0.9); // 高度设置为屏幕的0.6，根据实际情况调整
        params.width = (int) (display.getWidth() * 0.9); // 宽度设置为屏幕的0.65，根据实际情况调整
        dialogWindow.setAttributes(params);
    }
}
