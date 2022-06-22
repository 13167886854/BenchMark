package com.example.benchmark.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.benchmark.Activity.CePingActivity;
import com.example.benchmark.Data.CepingData;
import com.example.benchmark.DiaLog.PopDiaLog;
import com.example.benchmark.R;
import com.example.benchmark.utils.AccessUtils;


import com.example.benchmark.BaseApp;
import com.example.benchmark.Service.StabilityMonitorService;
import com.example.benchmark.utils.AccessibilityUtil;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.ConfigurationUtils;
import com.example.benchmark.utils.ServiceUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PhoneFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, CheckBox.OnCheckedChangeListener {
    private Button blue_liuchang, blue_wending, blue_chukong, blue_yinhua;

    private List<CepingData> data;

    private CheckBox blue_liuchang_cheak;
    private CheckBox blue_wending_cheak;
    private CheckBox blue_chukong_cheak;
    private CheckBox blue_yinhua_cheak;

    private CheckBox blue_cpu_cheak;
    private CheckBox blue_gpu_cheak;
    private CheckBox blue_ram_cheak;
    private CheckBox blue_rom_cheak;
    private CheckBox phone_select_all;

    private Button blue_cpu, blue_gpu, blue_ram, blue_rom;

    private Button phone_start_ceping;

    private RadioGroup radioGroup;
    private AccessUtils accessUtils;
    private PopDiaLog popDiaLog;

    private static HashMap<String, String> cheak_phone_map;

    static {
        cheak_phone_map = new HashMap<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.phone_fragment, container, false);
        initview(view);
        blue_liuchang.setOnClickListener(this::onClick);
        blue_wending.setOnClickListener(this::onClick);
        blue_chukong.setOnClickListener(this::onClick);
        blue_yinhua.setOnClickListener(this::onClick);

        blue_cpu.setOnClickListener(this::onClick);
        blue_gpu.setOnClickListener(this::onClick);
        blue_ram.setOnClickListener(this::onClick);
        blue_rom.setOnClickListener(this::onClick);
        phone_select_all.setOnCheckedChangeListener(this::onCheckedChanged);

        radioGroup.setOnCheckedChangeListener(this::onCheckedChanged);
        phone_start_ceping.setOnClickListener(v -> {
            if (cheak_phone_map.get("cheaked_phone") == null) {
                Toast.makeText(getActivity(), "请选择需要测评的云手机平台", Toast.LENGTH_LONG).show();
                return;
            }
            if(blue_wending_cheak.isChecked()){
                if (!AccessibilityUtil.isAccessibilityServiceEnabled(BaseApp.context)
//                    || !accessUtils.isIgnoringBatteryOptimizations()
                        || !ServiceUtil.isServiceRunning(BaseApp.context, StabilityMonitorService.class.getName())) {
                    popDiaLog.show();
                    return;
                }
            }


                    Intent intent = new Intent(getActivity(), CePingActivity.class);
                    //传入cheakbox是否被选中
                    intent.putExtra("blue_liuchang_cheak", blue_liuchang_cheak.isChecked());
                    intent.putExtra("blue_wending_cheak", blue_wending_cheak.isChecked());
                    intent.putExtra("blue_chukong_cheak", blue_chukong_cheak.isChecked());
                    intent.putExtra("blue_yinhua_cheak", blue_yinhua_cheak.isChecked());
                    intent.putExtra("blue_cpu_cheak", blue_cpu_cheak.isChecked());
                    intent.putExtra("blue_gpu_cheak", blue_gpu_cheak.isChecked());
                    intent.putExtra("blue_ram_cheak", blue_ram_cheak.isChecked());
                    intent.putExtra("blue_rom_cheak", blue_rom_cheak.isChecked());
                    intent.putExtra("cheaked_plat", cheak_phone_map.get("cheaked_phone"));
                    startActivity(intent);


        });
        return view;
    }

    private void initview(View view) {
        blue_liuchang = view.findViewById(R.id.bule_liuchangxing);
        blue_wending = view.findViewById(R.id.bule_wendinxing);
        blue_chukong = view.findViewById(R.id.bule_chukong);
        blue_yinhua = view.findViewById(R.id.bule_yinhua);


        blue_cpu = view.findViewById(R.id.bule_cpu);
        blue_gpu = view.findViewById(R.id.bule_gpu);
        blue_ram = view.findViewById(R.id.bule_ram);
        blue_rom = view.findViewById(R.id.bule_rom);

        blue_liuchang_cheak = view.findViewById(R.id.blue_liuchang_cheak);
        blue_wending_cheak = view.findViewById(R.id.blue_wending_cheak);
        blue_chukong_cheak = view.findViewById(R.id.blue_chukong_cheak);
        blue_yinhua_cheak = view.findViewById(R.id.blue_yinhua_cheak);

        blue_cpu_cheak = view.findViewById(R.id.blue_cpu_cheak);
        blue_gpu_cheak = view.findViewById(R.id.blue_gpu_cheak);
        blue_ram_cheak = view.findViewById(R.id.blue_ram_cheak);
        blue_rom_cheak = view.findViewById(R.id.blue_rom_cheak);

        phone_select_all = view.findViewById(R.id.phone_select_all);

        phone_start_ceping = view.findViewById(R.id.blue_start_test);

        radioGroup = view.findViewById(R.id.select_phone);

        kunpeng_phone = view.findViewById(R.id.kunpeng_phone);
        redfingure_phone = view.findViewById(R.id.redfigure_phone);
        yiodng_phone = view.findViewById(R.id.yidong_phone);
        wangyiyun_phone = view.findViewById(R.id.wangyiyun_phone);
        accessUtils = new AccessUtils(getContext());
        popDiaLog = new PopDiaLog(Objects.requireNonNull(getActivity()));

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bule_liuchangxing: {

                boolean checked = blue_liuchang_cheak.isChecked();
                if (checked) {
                    blue_liuchang_cheak.setVisibility(View.INVISIBLE);
                    blue_liuchang_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                } else {
                    blue_liuchang_cheak.setChecked(true);
                    blue_liuchang_cheak.setVisibility(View.VISIBLE);
                }
                break;

            }
            case R.id.bule_wendinxing: {

                boolean checked = blue_wending_cheak.isChecked();
                if (checked) {
                    blue_wending_cheak.setVisibility(View.INVISIBLE);
                    blue_wending_cheak.setChecked(false);
                    phone_select_all.setChecked(false
                    );
                } else {
                    blue_wending_cheak.setVisibility(View.VISIBLE);
                    blue_wending_cheak.setChecked(true);
                }
                break;
            }
            case R.id.bule_chukong: {

                boolean checked = blue_chukong_cheak.isChecked();
                if (checked) {
                    blue_chukong_cheak.setVisibility(View.INVISIBLE);
                    blue_chukong_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                } else {
                    blue_chukong_cheak.setVisibility(View.VISIBLE);
                    blue_chukong_cheak.setChecked(true);
                }
                break;
            }
            case R.id.bule_yinhua: {

                boolean checked = blue_yinhua_cheak.isChecked();
                if (checked) {
                    blue_yinhua_cheak.setVisibility(View.INVISIBLE);
                    blue_yinhua_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                } else {
                    blue_yinhua_cheak.setVisibility(View.VISIBLE);
                    blue_yinhua_cheak.setChecked(true);

                }
                break;
            }
            case R.id.bule_cpu: {

                boolean checked = blue_cpu_cheak.isChecked();
                if (checked) {
                    blue_cpu_cheak.setVisibility(View.INVISIBLE);
                    blue_cpu_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                } else {
                    blue_cpu_cheak.setVisibility(View.VISIBLE);
                    blue_cpu_cheak.setChecked(true);
                }
                break;
            }
            case R.id.bule_gpu: {

                boolean checked = blue_gpu_cheak.isChecked();
                if (checked) {
                    blue_gpu_cheak.setVisibility(View.INVISIBLE);
                    blue_gpu_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                } else {
                    blue_gpu_cheak.setVisibility(View.VISIBLE);
                    blue_gpu_cheak.setChecked(true);
                }
                break;
            }
            case R.id.bule_ram: {

                boolean checked = blue_ram_cheak.isChecked();
                if (checked) {
                    blue_ram_cheak.setVisibility(View.INVISIBLE);
                    blue_ram_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                } else {
                    blue_ram_cheak.setVisibility(View.VISIBLE);
                    blue_ram_cheak.setChecked(true);
                }
                break;
            }
            case R.id.bule_rom: {

                boolean checked = blue_rom_cheak.isChecked();
                if (checked) {
                    blue_rom_cheak.setVisibility(View.INVISIBLE);
                    blue_rom_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                } else {
                    blue_rom_cheak.setVisibility(View.VISIBLE);
                    blue_rom_cheak.setChecked(true);
                }
                break;
            }


        }
    }

    private RadioButton kunpeng_phone, redfingure_phone, yiodng_phone, wangyiyun_phone;

    @SuppressLint({"NonConstantResourceId", "ResourceAsColor"})
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.kunpeng_phone: {
                if (!kunpeng_phone.isChecked()) {
                    kunpeng_phone.setTextColor(R.color.select);
                }
                cheak_phone_map.put("cheaked_phone", CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_PHONE);
                break;
            }
            case R.id.redfigure_phone: {
                if (!redfingure_phone.isChecked()) {
                    redfingure_phone.setTextColor(R.color.select);
                }

                cheak_phone_map.put("cheaked_phone", CacheConst.PLATFORM_NAME_RED_FINGER_CLOUD_PHONE);
                break;
            }
            case R.id.yidong_phone: {
                if (!yiodng_phone.isChecked()) {
                    yiodng_phone.setTextColor(R.color.select);
                }
                cheak_phone_map.put("cheaked_phone", CacheConst.PLATFORM_NAME_E_CLOUD_PHONE);
                break;
            }
            case R.id.wangyiyun_phone: {
                if (!wangyiyun_phone.isChecked()) {
                    wangyiyun_phone.setTextColor(R.color.select);
                }
                cheak_phone_map.put("cheaked_phone", CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_PHONE);
                break;
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (isChecked) {
            blue_liuchang_cheak.setChecked(true);
            blue_liuchang_cheak.setVisibility(View.VISIBLE);


            blue_wending_cheak.setVisibility(View.VISIBLE);
            blue_wending_cheak.setChecked(true);

            blue_chukong_cheak.setVisibility(View.VISIBLE);
            blue_chukong_cheak.setChecked(true);

            blue_yinhua_cheak.setVisibility(View.VISIBLE);
            blue_yinhua_cheak.setChecked(true);

            blue_cpu_cheak.setVisibility(View.VISIBLE);
            blue_cpu_cheak.setChecked(true);


            blue_gpu_cheak.setVisibility(View.VISIBLE);
            blue_gpu_cheak.setChecked(true);

            blue_ram_cheak.setVisibility(View.VISIBLE);
            blue_ram_cheak.setChecked(true);

            blue_rom_cheak.setVisibility(View.VISIBLE);
            blue_rom_cheak.setChecked(true);
        } else {
            blue_liuchang_cheak.setChecked(false);
            blue_liuchang_cheak.setVisibility(View.INVISIBLE);

            blue_wending_cheak.setChecked(false);
            blue_wending_cheak.setVisibility(View.INVISIBLE);


            blue_chukong_cheak.setChecked(false);
            blue_chukong_cheak.setVisibility(View.INVISIBLE);


            blue_yinhua_cheak.setVisibility(View.INVISIBLE);
            blue_yinhua_cheak.setChecked(false);

            blue_cpu_cheak.setVisibility(View.INVISIBLE);
            blue_cpu_cheak.setChecked(false);

            blue_gpu_cheak.setVisibility(View.INVISIBLE);
            blue_gpu_cheak.setChecked(false);

            blue_ram_cheak.setVisibility(View.INVISIBLE);
            blue_ram_cheak.setChecked(false);

            blue_rom_cheak.setVisibility(View.INVISIBLE);
            blue_rom_cheak.setChecked(false);
        }


    }
}
