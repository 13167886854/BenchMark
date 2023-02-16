package com.example.benchmark.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.benchmark.activity.CePingActivity;
import com.example.benchmark.data.Admin;
import com.example.benchmark.data.CepingData;
import com.example.benchmark.diaLog.PopDiaLog;
import com.example.benchmark.R;
import com.example.benchmark.utils.AccessUtils;


import com.example.benchmark.BaseApp;
import com.example.benchmark.service.MyAccessibilityService;
import com.example.benchmark.utils.AccessibilityUtil;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;
import com.example.benchmark.utils.ServiceUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PhoneFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "PhoneFragment";
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

    private Button kunpeng_phone, huawei_data_phone, redfingure_phone, yiodng_phone, wangyiyun_phone;

    private AccessUtils accessUtils;
    private PopDiaLog popDiaLog;

    private final HashMap<String, String> check_phone_map = new HashMap<>();

    private final int ALL_CHECK_COUNTS = 8;
    private int mCheckCounts = ALL_CHECK_COUNTS;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.phone_fragment, container, false);
        initview(view);
        kunpeng_phone.setOnClickListener(this);
        huawei_data_phone.setOnClickListener(this);
        redfingure_phone.setOnClickListener(this);
        yiodng_phone.setOnClickListener(this);
        wangyiyun_phone.setOnClickListener(this);
        blue_liuchang.setOnClickListener(this::onClick);
        blue_wending.setOnClickListener(this::onClick);
        blue_chukong.setOnClickListener(this::onClick);
        blue_yinhua.setOnClickListener(this::onClick);

        blue_cpu.setOnClickListener(this::onClick);
        blue_gpu.setOnClickListener(this::onClick);
        blue_ram.setOnClickListener(this::onClick);
        blue_rom.setOnClickListener(this::onClick);
        phone_select_all.setOnClickListener(this::onClick);
//        phone_select_all.setOnCheckedChangeListener(this::onCheckedChanged);

        phone_start_ceping.setOnClickListener(v -> {
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = simpleDateFormat.format(date);
            Admin.testTime = time;
            Log.d(TAG, "onCreateView: 开始测试时间====" +Admin.testTime);
            if (check_phone_map.get(CacheConst.KEY_PLATFORM_NAME) == null) {
                Toast.makeText(getActivity(), "请选择需要测评的云手机平台", Toast.LENGTH_LONG).show();
                return;
            }
            if (blue_wending_cheak.isChecked()) {
                if (!AccessibilityUtil.isAccessibilityServiceEnabled(BaseApp.context)
//                    || !accessUtils.isIgnoringBatteryOptimizations()
                        || !ServiceUtil.isServiceRunning(BaseApp.context, MyAccessibilityService.class.getName())) {
                    popDiaLog.show();
                    return;
                }
            }
            if (blue_chukong_cheak.isChecked()) {
                if (!ServiceUtil.isServiceRunning(BaseApp.context, MyAccessibilityService.class.getName())) {
                    popDiaLog.show();
                    return;
                }
            }
            if (blue_yinhua_cheak.isChecked()) {
                if (!ServiceUtil.isServiceRunning(BaseApp.context, MyAccessibilityService.class.getName())) {
                    popDiaLog.show();
                    return;
                }
            }
            if (!Settings.canDrawOverlays(getContext())) {
                Toast.makeText(getContext(), "请允许本应用显示悬浮窗！", Toast.LENGTH_SHORT).show();
                Intent intentToFloatPermission = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getContext().getPackageName()));
                Log.d("TWT", "toFloatGetPermission: " + Uri.parse("package:" + getContext().getPackageName()));
                //intent.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                this.startActivity(intentToFloatPermission);
                //startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")), 0);
                return;
            }
            CacheUtil.put(CacheConst.KEY_STABILITY_IS_MONITORED, false);
            CacheUtil.put(CacheConst.KEY_PERFORMANCE_IS_MONITORED, false);
            Intent intent = new Intent(getActivity(), CePingActivity.class);
            //传入cheakbox是否被选中
            intent.putExtra(CacheConst.KEY_PLATFORM_KIND, CacheConst.PLATFORM_KIND_CLOUD_PHONE);
            intent.putExtra(CacheConst.KEY_FLUENCY_INFO, blue_liuchang_cheak.isChecked());
            intent.putExtra(CacheConst.KEY_STABILITY_INFO, blue_wending_cheak.isChecked());
            intent.putExtra(CacheConst.KEY_TOUCH_INFO, blue_chukong_cheak.isChecked());
            intent.putExtra(CacheConst.KEY_SOUND_FRAME_INFO, blue_yinhua_cheak.isChecked());
            intent.putExtra(CacheConst.KEY_CPU_INFO, blue_cpu_cheak.isChecked());
            intent.putExtra(CacheConst.KEY_GPU_INFO, blue_gpu_cheak.isChecked());
            intent.putExtra(CacheConst.KEY_ROM_INFO, blue_rom_cheak.isChecked());
            intent.putExtra(CacheConst.KEY_RAM_INFO, blue_ram_cheak.isChecked());
            intent.putExtra(CacheConst.KEY_PLATFORM_NAME, check_phone_map.get(CacheConst.KEY_PLATFORM_NAME));
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


        kunpeng_phone = view.findViewById(R.id.kunpeng_phone);
        huawei_data_phone = view.findViewById(R.id.kunpeng_data_phone);
        redfingure_phone = view.findViewById(R.id.redfigure_phone);
        yiodng_phone = view.findViewById(R.id.yidong_phone);
        wangyiyun_phone = view.findViewById(R.id.wangyiyun_phone);
        accessUtils = new AccessUtils(getContext());
        //popDiaLog = new PopDiaLog(Objects.requireNonNull(getActivity()));
        popDiaLog = new PopDiaLog(requireActivity());

    }

    public void initPhoneBtn() {
        Drawable drawable;
        Button btn;
        //移动云
        btn = (Button) getActivity().findViewById(R.id.yidong_phone);
        drawable = getResources().getDrawable(R.drawable.yidong_phone_dark);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        btn.setCompoundDrawables(null, drawable, null, null); //设置底图标
        //网易
        btn = (Button) getActivity().findViewById(R.id.wangyiyun_phone);
        drawable = getResources().getDrawable(R.drawable.wangyi_phone_dark);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        btn.setCompoundDrawables(null, drawable, null, null); //设置底图标
        //华为
        btn = (Button) getActivity().findViewById(R.id.kunpeng_phone);
        drawable = getResources().getDrawable(R.drawable.kunpeng_phone_dark);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        btn.setCompoundDrawables(null, drawable, null, null); //设置底图标
        btn = (Button) getActivity().findViewById(R.id.kunpeng_data_phone);
        drawable = getResources().getDrawable(R.drawable.kunpeng_phone_dark);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        btn.setCompoundDrawables(null, drawable, null, null); //设置底图标
        //红手指
        btn = (Button) getActivity().findViewById(R.id.redfigure_phone);
        drawable = getResources().getDrawable(R.drawable.redfingure_dark);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        btn.setCompoundDrawables(null, drawable, null, null); //设置底图标
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        Log.d("mCheckCounts", "onClick: mCheckCounts==>  " + mCheckCounts);
        switch (v.getId()) {
            case R.id.kunpeng_phone: {
                check_phone_map.put(CacheConst.KEY_PLATFORM_NAME, CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_PHONE);
                initPhoneBtn();
                Button btn = (Button) v;
                Drawable drawable = getResources().getDrawable(R.drawable.kunpeng_phone);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                btn.setCompoundDrawables(null, drawable, null, null); //设置底图标
                break;
            }
            case R.id.kunpeng_data_phone: {
                check_phone_map.put(CacheConst.KEY_PLATFORM_NAME, CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_GAME);
                initPhoneBtn();
                Button btn = (Button) v;
                Drawable drawable = getResources().getDrawable(R.drawable.kunpeng_phone);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                btn.setCompoundDrawables(null, drawable, null, null); //设置底图标
                break;
            }
            case R.id.redfigure_phone: {
                check_phone_map.put(CacheConst.KEY_PLATFORM_NAME, CacheConst.PLATFORM_NAME_RED_FINGER_CLOUD_PHONE);
                initPhoneBtn();
                Button btn = (Button) v;
                Drawable drawable = getResources().getDrawable(R.drawable.redfingure);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                btn.setCompoundDrawables(null, drawable, null, null); //设置底图标
                break;
            }
            case R.id.yidong_phone: {
                check_phone_map.put(CacheConst.KEY_PLATFORM_NAME, CacheConst.PLATFORM_NAME_E_CLOUD_PHONE);
                initPhoneBtn();
                Button btn = (Button) v;
                Drawable drawable = getResources().getDrawable(R.drawable.yidong_phone);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                btn.setCompoundDrawables(null, drawable, null, null); //设置底图标
                break;
            }
            case R.id.wangyiyun_phone: {
                check_phone_map.put(CacheConst.KEY_PLATFORM_NAME, CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_PHONE);
                initPhoneBtn();
                Button btn = (Button) v;
                Drawable drawable = getResources().getDrawable(R.drawable.wangyi_phone);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                btn.setCompoundDrawables(null, drawable, null, null); //设置底图标
                break;
            }
            case R.id.bule_liuchangxing: {

                boolean checked = blue_liuchang_cheak.isChecked();
                if (checked) {
                    blue_liuchang_cheak.setVisibility(View.INVISIBLE);
                    blue_liuchang_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                    mCheckCounts--;
                } else {
                    blue_liuchang_cheak.setChecked(true);
                    blue_liuchang_cheak.setVisibility(View.VISIBLE);
                    mCheckCounts++;
                    if (mCheckCounts == ALL_CHECK_COUNTS) {
                        phone_select_all.setChecked(true);
                    }
                }
                break;

            }
            case R.id.bule_wendinxing: {

                boolean checked = blue_wending_cheak.isChecked();
                if (checked) {
                    blue_wending_cheak.setVisibility(View.INVISIBLE);
                    blue_wending_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                    mCheckCounts--;
                } else {
                    blue_wending_cheak.setVisibility(View.VISIBLE);
                    blue_wending_cheak.setChecked(true);
                    mCheckCounts++;
                    if (mCheckCounts == ALL_CHECK_COUNTS) {
                        phone_select_all.setChecked(true);
                    }
                }
                break;
            }
            case R.id.bule_chukong: {

                boolean checked = blue_chukong_cheak.isChecked();
                if (checked) {
                    blue_chukong_cheak.setVisibility(View.INVISIBLE);
                    blue_chukong_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                    mCheckCounts--;
                } else {
                    blue_chukong_cheak.setVisibility(View.VISIBLE);
                    blue_chukong_cheak.setChecked(true);
                    mCheckCounts++;
                    if (mCheckCounts == ALL_CHECK_COUNTS) {
                        phone_select_all.setChecked(true);
                    }
                }
                break;
            }
            case R.id.bule_yinhua: {

                boolean checked = blue_yinhua_cheak.isChecked();
                if (checked) {
                    blue_yinhua_cheak.setVisibility(View.INVISIBLE);
                    blue_yinhua_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                    mCheckCounts--;
                } else {
                    blue_yinhua_cheak.setVisibility(View.VISIBLE);
                    blue_yinhua_cheak.setChecked(true);
                    mCheckCounts++;
                    if (mCheckCounts == ALL_CHECK_COUNTS) {
                        phone_select_all.setChecked(true);
                    }
                }
                break;
            }


            case R.id.bule_cpu: {

                boolean checked = blue_cpu_cheak.isChecked();
                if (checked) {
                    blue_cpu_cheak.setVisibility(View.INVISIBLE);
                    blue_cpu_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                    mCheckCounts--;

                    blue_gpu_cheak.setVisibility(View.INVISIBLE);
                    blue_gpu_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                    mCheckCounts--;

                    blue_ram_cheak.setVisibility(View.INVISIBLE);
                    blue_ram_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                    mCheckCounts--;

                    blue_rom_cheak.setVisibility(View.INVISIBLE);
                    blue_rom_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                    mCheckCounts--;


                } else {
                    blue_cpu_cheak.setVisibility(View.VISIBLE);
                    blue_cpu_cheak.setChecked(true);
                    mCheckCounts++;
                    if (mCheckCounts == ALL_CHECK_COUNTS) {
                        phone_select_all.setChecked(true);
                    }

                    blue_gpu_cheak.setVisibility(View.VISIBLE);
                    blue_gpu_cheak.setChecked(true);
                    mCheckCounts++;
                    if (mCheckCounts == ALL_CHECK_COUNTS) {
                        phone_select_all.setChecked(true);
                    }

                    blue_ram_cheak.setVisibility(View.VISIBLE);
                    blue_ram_cheak.setChecked(true);
                    mCheckCounts++;
                    if (mCheckCounts == ALL_CHECK_COUNTS) {
                        phone_select_all.setChecked(true);
                    }

                    blue_rom_cheak.setVisibility(View.VISIBLE);
                    blue_rom_cheak.setChecked(true);
                    mCheckCounts++;
                    if (mCheckCounts == ALL_CHECK_COUNTS) {
                        phone_select_all.setChecked(true);
                    }

                }
                break;
            }
            case R.id.bule_gpu: {

                boolean checked = blue_gpu_cheak.isChecked();
                if (checked) {
                    blue_gpu_cheak.setVisibility(View.INVISIBLE);
                    blue_gpu_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                    mCheckCounts--;

                    blue_cpu_cheak.setVisibility(View.INVISIBLE);
                    blue_cpu_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                    mCheckCounts--;

                    blue_ram_cheak.setVisibility(View.INVISIBLE);
                    blue_ram_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                    mCheckCounts--;

                    blue_rom_cheak.setVisibility(View.INVISIBLE);
                    blue_rom_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                    mCheckCounts--;

                } else {
                    blue_gpu_cheak.setVisibility(View.VISIBLE);
                    blue_gpu_cheak.setChecked(true);
                    mCheckCounts++;
                    if (mCheckCounts == ALL_CHECK_COUNTS) {
                        phone_select_all.setChecked(true);
                    }

                    blue_cpu_cheak.setVisibility(View.VISIBLE);
                    blue_cpu_cheak.setChecked(true);
                    mCheckCounts++;
                    if (mCheckCounts == ALL_CHECK_COUNTS) {
                        phone_select_all.setChecked(true);
                    }

                    blue_ram_cheak.setVisibility(View.VISIBLE);
                    blue_ram_cheak.setChecked(true);
                    mCheckCounts++;
                    if (mCheckCounts == ALL_CHECK_COUNTS) {
                        phone_select_all.setChecked(true);
                    }

                    blue_rom_cheak.setVisibility(View.VISIBLE);
                    blue_rom_cheak.setChecked(true);
                    mCheckCounts++;
                    if (mCheckCounts == ALL_CHECK_COUNTS) {
                        phone_select_all.setChecked(true);
                    }

                }
                break;
            }
            case R.id.bule_ram: {

                boolean checked = blue_ram_cheak.isChecked();
                if (checked) {
                    blue_ram_cheak.setVisibility(View.INVISIBLE);
                    blue_ram_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                    mCheckCounts--;

                    blue_cpu_cheak.setVisibility(View.INVISIBLE);
                    blue_cpu_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                    mCheckCounts--;

                    blue_gpu_cheak.setVisibility(View.INVISIBLE);
                    blue_gpu_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                    mCheckCounts--;


                    blue_rom_cheak.setVisibility(View.INVISIBLE);
                    blue_rom_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                    mCheckCounts--;

                } else {
                    blue_ram_cheak.setVisibility(View.VISIBLE);
                    blue_ram_cheak.setChecked(true);
                    mCheckCounts++;
                    if (mCheckCounts == ALL_CHECK_COUNTS) {
                        phone_select_all.setChecked(true);
                    }

                    blue_cpu_cheak.setVisibility(View.VISIBLE);
                    blue_cpu_cheak.setChecked(true);
                    mCheckCounts++;
                    if (mCheckCounts == ALL_CHECK_COUNTS) {
                        phone_select_all.setChecked(true);
                    }

                    blue_gpu_cheak.setVisibility(View.VISIBLE);
                    blue_gpu_cheak.setChecked(true);
                    mCheckCounts++;
                    if (mCheckCounts == ALL_CHECK_COUNTS) {
                        phone_select_all.setChecked(true);
                    }


                    blue_rom_cheak.setVisibility(View.VISIBLE);
                    blue_rom_cheak.setChecked(true);
                    mCheckCounts++;
                    if (mCheckCounts == ALL_CHECK_COUNTS) {
                        phone_select_all.setChecked(true);
                    }

                }
                break;
            }
            case R.id.bule_rom: {

                boolean checked = blue_rom_cheak.isChecked();
                if (checked) {
                    blue_rom_cheak.setVisibility(View.INVISIBLE);
                    blue_rom_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                    mCheckCounts--;

                    blue_cpu_cheak.setVisibility(View.INVISIBLE);
                    blue_cpu_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                    mCheckCounts--;

                    blue_gpu_cheak.setVisibility(View.INVISIBLE);
                    blue_gpu_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                    mCheckCounts--;

                    blue_ram_cheak.setVisibility(View.INVISIBLE);
                    blue_ram_cheak.setChecked(false);
                    phone_select_all.setChecked(false);
                    mCheckCounts--;

                } else {
                    blue_rom_cheak.setVisibility(View.VISIBLE);
                    blue_rom_cheak.setChecked(true);
                    mCheckCounts++;
                    if (mCheckCounts == ALL_CHECK_COUNTS) {
                        phone_select_all.setChecked(true);
                    }

                    blue_cpu_cheak.setVisibility(View.VISIBLE);
                    blue_cpu_cheak.setChecked(true);
                    mCheckCounts++;
                    if (mCheckCounts == ALL_CHECK_COUNTS) {
                        phone_select_all.setChecked(true);
                    }

                    blue_gpu_cheak.setVisibility(View.VISIBLE);
                    blue_gpu_cheak.setChecked(true);
                    mCheckCounts++;
                    if (mCheckCounts == ALL_CHECK_COUNTS) {
                        phone_select_all.setChecked(true);
                    }

                    blue_ram_cheak.setVisibility(View.VISIBLE);
                    blue_ram_cheak.setChecked(true);
                    mCheckCounts++;
                    if (mCheckCounts == ALL_CHECK_COUNTS) {
                        phone_select_all.setChecked(true);
                    }


                }
                break;
            }
            case R.id.phone_select_all: {
                boolean isCheckedAll = phone_select_all.isChecked();
                if (isCheckedAll) {
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
                    mCheckCounts = 8;
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
                    mCheckCounts = 0;
                }
            }
        }
    }


}
