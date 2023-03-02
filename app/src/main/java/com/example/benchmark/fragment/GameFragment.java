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
import com.example.benchmark.diaLog.PopDiaLog;
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
 * @description 云游戏Fragment
 * @date 2023/2/15 16:14
 * @version 1.0
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

    private Button red_liuchang;
    private Button red_wending;
    private Button red_chukong;
    private Button red_yinhua;
    private GLSurfaceView mSurfaceView;
    private CheckBox red_liuchang_cheak;
    private CheckBox red_wending_cheak;
    private CheckBox red_chukong_cheak;
    private CheckBox red_yinhua_cheak;
    private CheckBox red_cpu_cheak;
    private CheckBox red_gpu_cheak;
    private CheckBox red_ram_cheak;
    private CheckBox red_rom_cheak;
    private CheckBox game_select_all;
    private Button red_cpu;
    private Button red_gpu;
    private Button red_ram;
    private Button red_rom;
    private Button red_start_test;
    private RadioGroup select_game;
    private AccessUtils accessUtils;
    private PopDiaLog popDiaLog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_fragment, container, false);
        initview(view);

        red_liuchang.setOnClickListener(this::onClick);
        red_wending.setOnClickListener(this::onClick);
        red_chukong.setOnClickListener(this::onClick);
        red_yinhua.setOnClickListener(this::onClick);

        red_cpu.setOnClickListener(this::onClick);
        red_gpu.setOnClickListener(this::onClick);
        red_ram.setOnClickListener(this::onClick);
        red_rom.setOnClickListener(this::onClick);


        game_select_all.setOnCheckedChangeListener(this::onCheckedChanged);
        select_game.setOnCheckedChangeListener(this::onCheckedChanged);

        red_start_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = simpleDateFormat.format(date);
                Admin.testTime = time;
                Log.d(TAG, "onCreateView: 开始测试时间====" + Admin.testTime);
                if (cheak_game_map.get("cheaked_game") == null) {
                    Toast.makeText(getActivity(), "请选择需要测评的云游戏平台", Toast.LENGTH_LONG).show();
                    return;
                }
                if (red_wending_cheak.isChecked() || red_yinhua_cheak.isChecked()) {
                    if (!AccessibilityUtil.isAccessibilityServiceEnabled(BaseApp.context)
                            || !ServiceUtil.isServiceRunning(BaseApp.context, MyAccessibilityService.class.getName())) {
                        popDiaLog.show();
                        return;
                    }
                } else if (red_chukong_cheak.isChecked()) {
                    //检查是否开启无障碍服务。。。。
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
                    Toast.makeText(getContext(),"can draw floatWindow", Toast.LENGTH_SHORT);
                }
                CacheUtil.put(CacheConst.KEY_STABILITY_IS_MONITORED, false);
                CacheUtil.put(CacheConst.KEY_PERFORMANCE_IS_MONITORED, false);
                Intent intent = new Intent(getActivity(), CePingActivity.class);
                intent.putExtra(CacheConst.KEY_PLATFORM_KIND, CacheConst.PLATFORM_KIND_CLOUD_GAME);
                intent.putExtra(CacheConst.KEY_FLUENCY_INFO, red_liuchang_cheak.isChecked());
                intent.putExtra(CacheConst.KEY_STABILITY_INFO, red_wending_cheak.isChecked());
                intent.putExtra(CacheConst.KEY_TOUCH_INFO, red_chukong_cheak.isChecked());
                intent.putExtra(CacheConst.KEY_SOUND_FRAME_INFO, red_yinhua_cheak.isChecked());

                intent.putExtra(CacheConst.KEY_CPU_INFO, red_cpu_cheak.isChecked());
                intent.putExtra(CacheConst.KEY_GPU_INFO, red_gpu_cheak.isChecked());
                intent.putExtra(CacheConst.KEY_ROM_INFO, red_ram_cheak.isChecked());
                intent.putExtra(CacheConst.KEY_RAM_INFO, red_rom_cheak.isChecked());

                Log.d(TAG, "onClick: -----------" + red_cpu_cheak.isChecked());
                Log.d(TAG, "onClick: -----------" + red_gpu_cheak.isChecked());
                Log.d(TAG, "onClick: -----------" + red_ram_cheak.isChecked());
                Log.d(TAG, "onClick: -----------" + red_rom_cheak.isChecked());

                Map<String, Object> localMobileInfo = getInfo(); // 本地手机的规格数据
                if (red_rom_cheak.isChecked() || red_ram_cheak.isChecked() || red_gpu_cheak.isChecked()
                        || red_cpu_cheak.isChecked()) {
                    if (localMobileInfo instanceof Serializable) {
                        intent.putExtra("localMobileInfo", (Serializable) localMobileInfo);
                    }
                }

                intent.putExtra(CacheConst.KEY_PLATFORM_NAME, cheak_game_map.get("cheaked_game"));

                startActivity(intent);
            }
        });

        return view;
    }



    private void initview(View view) {
        red_liuchang = view.findViewById(R.id.red_liuchangxing);
        red_wending = view.findViewById(R.id.red_wendinxing);
        red_chukong = view.findViewById(R.id.red_chukong);
        red_yinhua = view.findViewById(R.id.red_yinhua);

        mSurfaceView = view.findViewById(R.id.surfaceView);
        mSurfaceView.setEGLContextClientVersion(1);
        mSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        mSurfaceView.setRenderer(new GPURenderer());

        red_cpu = view.findViewById(R.id.red_cpu);
        red_gpu = view.findViewById(R.id.red_gpu);
        red_ram = view.findViewById(R.id.red_ram);
        red_rom = view.findViewById(R.id.red_rom);

        red_liuchang_cheak = view.findViewById(R.id.red_liuchang_cheak);
        red_wending_cheak = view.findViewById(R.id.red_wending_cheak);
        red_chukong_cheak = view.findViewById(R.id.red_chukong_cheak);
        red_yinhua_cheak = view.findViewById(R.id.red_yinhua_cheak);

        red_cpu_cheak = view.findViewById(R.id.red_cpu_cheak);
        red_gpu_cheak = view.findViewById(R.id.red_gpu_cheak);
        red_ram_cheak = view.findViewById(R.id.red_ram_cheak);
        red_rom_cheak = view.findViewById(R.id.red_rom_cheak);


        red_start_test = view.findViewById(R.id.red_start_test);

        select_game = view.findViewById(R.id.select_game);
        game_select_all = view.findViewById(R.id.game_select_all);

        accessUtils = new AccessUtils(getContext());
        popDiaLog = new PopDiaLog(getActivity());

    }


    public void initGameBtn() {
        //腾讯
        if (getActivity().findViewById(R.id.tengxun_game) instanceof  Button){
            Button btn = (Button) getActivity().findViewById(R.id.tengxun_game);
            Drawable drawable = getResources().getDrawable(R.drawable.tengxunxianfeng_dark);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            btn.setCompoundDrawables(null, drawable, null, null); //设置底图标

        }
        //咪咕
        if (getActivity().findViewById(R.id.migu_game) instanceof  Button){
            Button btn = (Button) getActivity().findViewById(R.id.migu_game);
            Drawable drawable = getResources().getDrawable(R.drawable.migukuaiyou_dark);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            btn.setCompoundDrawables(null, drawable, null, null); //设置底图标
        }
        //网易
        if (getActivity().findViewById(R.id.wangyi_game) instanceof  Button){
            Button btn = (Button) getActivity().findViewById(R.id.wangyi_game);
            Drawable drawable = getResources().getDrawable(R.drawable.wangyiyunyouxi_dark);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            btn.setCompoundDrawables(null, drawable, null, null); //设置底图标
        }

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.red_liuchangxing: {
                boolean isChecked = red_liuchang_cheak.isChecked();
                if (isChecked) {
                    red_liuchang_cheak.setChecked(false);
                    red_liuchang_cheak.setVisibility(View.INVISIBLE);
                    game_select_all.setChecked(false);
                } else {
                    red_liuchang_cheak.setChecked(true);
                    red_liuchang_cheak.setVisibility(View.VISIBLE);
                }
                break;
            }
            case R.id.red_wendinxing: {
                boolean isChecked = red_wending_cheak.isChecked();
                if (isChecked) {
                    red_wending_cheak.setChecked(false);
                    red_wending_cheak.setVisibility(View.INVISIBLE);
                    game_select_all.setChecked(false);
                } else {
                    red_wending_cheak.setVisibility(View.VISIBLE);
                    red_wending_cheak.setChecked(true);
                }
                break;
            }
            case R.id.red_chukong: {
                boolean isChecked = red_chukong_cheak.isChecked();
                if (isChecked) {
                    red_chukong_cheak.setChecked(false);
                    red_chukong_cheak.setVisibility(View.INVISIBLE);
                    game_select_all.setChecked(false);
                } else {
                    red_chukong_cheak.setVisibility(View.VISIBLE);
                    red_chukong_cheak.setChecked(true);
                }
                break;
            }
            case R.id.red_yinhua: {
                boolean isChecked = red_yinhua_cheak.isChecked();
                if (isChecked) {
                    red_yinhua_cheak.setVisibility(View.INVISIBLE);
                    red_yinhua_cheak.setChecked(false);
                    game_select_all.setChecked(false);
                } else {
                    red_yinhua_cheak.setVisibility(View.VISIBLE);
                    red_yinhua_cheak.setChecked(true);
                }
                break;
            }
            case R.id.red_cpu: {

                boolean isChecked = red_cpu_cheak.isChecked();

                if (isChecked) {
                    red_cpu_cheak.setVisibility(View.INVISIBLE);
                    red_cpu_cheak.setChecked(false);
                    game_select_all.setChecked(false);
                } else {
                    red_cpu_cheak.setVisibility(View.VISIBLE);
                    red_cpu_cheak.setChecked(true);
                }
                break;
            }
            case R.id.red_gpu: {

                boolean isChecked = red_gpu_cheak.isChecked();

                if (isChecked) {
                    red_gpu_cheak.setVisibility(View.INVISIBLE);
                    red_gpu_cheak.setChecked(false);
                    game_select_all.setChecked(false);
                } else {
                    red_gpu_cheak.setVisibility(View.VISIBLE);
                    red_gpu_cheak.setChecked(true);
                }
                break;
            }
            case R.id.red_ram: {

                boolean isChecked = red_ram_cheak.isChecked();

                if (isChecked) {
                    red_ram_cheak.setVisibility(View.INVISIBLE);
                    red_ram_cheak.setChecked(false);
                    game_select_all.setChecked(false);
                } else {
                    red_ram_cheak.setVisibility(View.VISIBLE);
                    red_ram_cheak.setChecked(true);
                }
                break;
            }
            case R.id.red_rom: {

                boolean isChecked = red_rom_cheak.isChecked();

                if (isChecked) {
                    red_rom_cheak.setVisibility(View.INVISIBLE);
                    red_rom_cheak.setChecked(false);
                    game_select_all.setChecked(false);
                } else {
                    red_rom_cheak.setVisibility(View.VISIBLE);
                    red_rom_cheak.setChecked(true);
                }
                break;
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.tengxun_game: {
                cheak_game_map.put("cheaked_game", CacheConst.PLATFORM_NAME_TENCENT_GAME);
                initGameBtn();
                if (getActivity().findViewById(R.id.tengxun_game) instanceof  Button) {
                    Button btn = (Button) getActivity().findViewById(R.id.tengxun_game);
                    Drawable drawable = getResources().getDrawable(R.drawable.tengxunxianfeng);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    btn.setCompoundDrawables(null, drawable, null, null); //设置底图标
                }
                break;
            }
            case R.id.migu_game: {
                cheak_game_map.put("cheaked_game", CacheConst.PLATFORM_NAME_MI_GU_GAME);
                initGameBtn();
                if (getActivity().findViewById(R.id.migu_game) instanceof  Button) {
                    Button btn = (Button) getActivity().findViewById(R.id.migu_game);
                    Drawable drawable = getResources().getDrawable(R.drawable.migukuaiyou);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    btn.setCompoundDrawables(null, drawable, null, null); //设置底图标
                }
                break;
            }
            case R.id.wangyi_game: {
                cheak_game_map.put("cheaked_game", CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_GAME);
                initGameBtn();
                if (getActivity().findViewById(R.id.wangyi_game) instanceof  Button) {
                    Button btn = (Button) getActivity().findViewById(R.id.wangyi_game);
                    Drawable drawable = getResources().getDrawable(R.drawable.wangyiyunyouxi);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    btn.setCompoundDrawables(null, drawable, null, null); //设置底图标
                }
                break;
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
                red_liuchang_cheak.setChecked(true);
                red_liuchang_cheak.setVisibility(View.VISIBLE);

                red_wending_cheak.setVisibility(View.VISIBLE);
                red_wending_cheak.setChecked(true);

                red_chukong_cheak.setVisibility(View.VISIBLE);
                red_chukong_cheak.setChecked(true);

                red_yinhua_cheak.setVisibility(View.VISIBLE);
                red_yinhua_cheak.setChecked(true);

                red_cpu_cheak.setVisibility(View.VISIBLE);
                red_cpu_cheak.setChecked(true);


                red_gpu_cheak.setVisibility(View.VISIBLE);
                red_gpu_cheak.setChecked(true);

                red_ram_cheak.setVisibility(View.VISIBLE);
                red_ram_cheak.setChecked(true);

                red_rom_cheak.setVisibility(View.VISIBLE);
                red_rom_cheak.setChecked(true);
        }else {
                red_liuchang_cheak.setChecked(false);
                red_liuchang_cheak.setVisibility(View.INVISIBLE);

                red_wending_cheak.setChecked(false);
                red_wending_cheak.setVisibility(View.INVISIBLE);


                red_chukong_cheak.setChecked(false);
                red_chukong_cheak.setVisibility(View.INVISIBLE);


                red_yinhua_cheak.setVisibility(View.INVISIBLE);
                red_yinhua_cheak.setChecked(false);

                red_cpu_cheak.setVisibility(View.INVISIBLE);
                red_cpu_cheak.setChecked(false);

                red_gpu_cheak.setVisibility(View.INVISIBLE);
                red_gpu_cheak.setChecked(false);

                red_ram_cheak.setVisibility(View.INVISIBLE);
                red_ram_cheak.setChecked(false);

                red_rom_cheak.setVisibility(View.INVISIBLE);
                red_rom_cheak.setChecked(false);
        }
    }


    public Map<String, Object> getInfo() {


        // {ROM={可用=4.68 GB, 总共=6.24 GB}, CPUCores=4, RAM={可用=801 MB, 总共=2.05 GB}}
        Map<String, String> ramInfo = SDCardUtils.getRAMInfo(getContext());
        if (ramInfo.get("可用").equals(ramInfo.get("总共"))) {
            ramInfo.put("可用", MemInfoUtil.getMemAvailable());
        }
        for (Map.Entry<String, String> entry : ramInfo.entrySet()) {
            String value = entry.getValue().endsWith("吉字节") ?
                    (entry.getValue().split("吉字节")[0] + "GB") : entry.getValue();
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
        Log.d(TAG, "getInfo: res-----------+\n" + res);
        return res;
    }

}
