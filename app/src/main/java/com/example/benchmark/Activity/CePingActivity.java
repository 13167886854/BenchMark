package com.example.benchmark.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.benchmark.Adapter.CePingAdapter;
import com.example.benchmark.Data.CepingData;
import com.example.benchmark.R;
import com.example.benchmark.utils.ApkUtil;
import com.example.benchmark.utils.CacheUtil;
import com.example.benchmark.Service.StabilityMonitorService;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.ScoreUtil;
import com.example.benchmark.utils.ServiceUtil;

import java.util.ArrayList;
import java.util.List;

public class CePingActivity extends Activity implements View.OnClickListener {

    private final int REQUEST_STABILITY = 0, REQUEST_FX = 1;

    private ImageButton ceshi_fanhui;
    private RecyclerView recyclerView;
    private TextView cepingtv, ceping_phone_name;
    private List<CepingData> ceping_data = new ArrayList<>();
    private CePingAdapter adapter;
    private String checked_plat;

    private boolean isCheckStability, isCheckFluency, isCheckTouch, isCheckSoundFrame,
            isCheckCPU, isCheckGPU, isCheckROM, isCheckRAM, isHaveOtherPerformance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ceping);

        initView();
        initData();
        ceshi_fanhui.setOnClickListener(this::onClick);
        adapter = new CePingAdapter(ceping_data, (data) -> {
            Intent intent = new Intent(this, JutiZhibiaoActivity.class);
            intent.putExtra("select_plat", checked_plat);
            intent.putExtra("select_item", data.getCepingItem());
            intent.putExtra("select_img", data.getCepingImage());
            intent.putExtra("select_text", data.getCepingText());
            intent.putExtra("select_grade", data.getGrade());
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }

    void initView() {
        ceshi_fanhui = findViewById(R.id.ceshi_fanhui);
        recyclerView = findViewById(R.id.ceping_rv);
        cepingtv = findViewById(R.id.ceping_tv);
        ceping_phone_name = findViewById(R.id.ceping_phone_name);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ceshi_fanhui: {
                finish();
            }
        }
    }

    void initData() {
        Intent intent = getIntent();
        checked_plat = intent.getStringExtra(CacheConst.KEY_PLATFORM_NAME);
        ceping_phone_name.setText(checked_plat);

        isCheckStability = intent.getBooleanExtra(CacheConst.KEY_STABILITY_INFO, false);
        isCheckFluency = intent.getBooleanExtra(CacheConst.KEY_FLUENCY_INFO, false);
        isCheckTouch = intent.getBooleanExtra(CacheConst.KEY_TOUCH_INFO, false);
        isCheckSoundFrame = intent.getBooleanExtra(CacheConst.KEY_SOUND_FRAME_INFO, false);
        isCheckCPU = intent.getBooleanExtra(CacheConst.KEY_CPU_INFO, false);
        isCheckGPU = intent.getBooleanExtra(CacheConst.KEY_GPU_INFO, false);
        isCheckRAM = intent.getBooleanExtra(CacheConst.KEY_RAM_INFO, false);
        isCheckROM = intent.getBooleanExtra(CacheConst.KEY_ROM_INFO, false);
        isHaveOtherPerformance = isCheckFluency || isCheckTouch || isCheckSoundFrame;
        updateListData();

        if (isCheckStability && !CacheUtil.getBoolean(CacheConst.KEY_STABILITY_IS_MONITORED)) {
            startStabilityMonitorService();
        }
        if (!isCheckStability && isHaveOtherPerformance) {
            startFxService();
        }
    }

    @Override
    protected void onStart() {
        updateListData();
        super.onStart();
    }

    private void updateListData() {
        Intent intent = getIntent();
        boolean isCloudPhone = CacheConst.PLATFORM_KIND_CLOUD_PHONE.equals(
                intent.getStringExtra(CacheConst.KEY_PLATFORM_KIND));
        ceping_data.clear();
        if (isCheckStability) {
            ceping_data.add(new CepingData(
                    ScoreUtil.getStabilityScore(),
                    isCloudPhone ? R.drawable.blue_wending : R.drawable.red_wending,
                    CacheConst.KEY_STABILITY_INFO,
                    getString(R.string.stability_info_description)
            ));
        }
        if (isCheckFluency) {
            ceping_data.add(new CepingData(
                    ScoreUtil.getFluencyScore(),
                    isCloudPhone ? R.drawable.blue_liuchang : R.drawable.blue_liuchang,
                    CacheConst.KEY_FLUENCY_INFO,
                    getString(R.string.fluency_info_description)
            ));
        }
        if (isCheckTouch) {
            ceping_data.add(new CepingData(
                    ScoreUtil.getTouchScore(),
                    isCloudPhone ? R.drawable.blue_chukong : R.drawable.red_chukong,
                    CacheConst.KEY_TOUCH_INFO,
                    getString(R.string.touch_info_description)
            ));
        }
        if (isCheckSoundFrame) {
            ceping_data.add(new CepingData(
                    ScoreUtil.getSoundFrameScore(),
                    isCloudPhone ? R.drawable.blue_yinhua : R.drawable.red_yinhua,
                    CacheConst.KEY_SOUND_FRAME_INFO,
                    getString(R.string.sound_frame_info_description)
            ));
        }
        if (isCheckCPU) {
            ceping_data.add(new CepingData(
                    ScoreUtil.getCPUScore(),
                    isCloudPhone ? R.drawable.blue_cpu : R.drawable.red_cpu,
                    CacheConst.KEY_CPU_INFO,
                    getString(R.string.cpu_info_description)
            ));
        }
        if (isCheckGPU) {
            ceping_data.add(new CepingData(
                    ScoreUtil.getGPUScore(),
                    isCloudPhone ? R.drawable.blue_gpu : R.drawable.red_gpu,
                    CacheConst.KEY_GPU_INFO,
                    getString(R.string.gpu_info_description)
            ));

        }
        if (isCheckRAM) {
            ceping_data.add(new CepingData(
                    ScoreUtil.getRAMScore(),
                    isCloudPhone ? R.drawable.blue_ram : R.drawable.red_ram,
                    CacheConst.KEY_RAM_INFO,
                    getString(R.string.ram_info_description)
            ));

        }
        if (isCheckROM) {
            ceping_data.add(new CepingData(
                    ScoreUtil.getROMScore(),
                    isCloudPhone ? R.drawable.blue_rom : R.drawable.red_rom,
                    CacheConst.KEY_ROM_INFO,
                    getString(R.string.rom_info_description)
            ));
        }
        if (adapter != null) adapter.notifyItemRangeChanged(0, ceping_data.size());
    }

    private void startStabilityMonitorService() {
        if (
                CacheConst.PLATFORM_NAME_RED_FINGER_CLOUD_PHONE.equals(checked_plat)
                        || CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_GAME.equals(checked_plat)
                        || CacheConst.PLATFORM_NAME_E_CLOUD_PHONE.equals(checked_plat)
        ) {
            // 需要申请录屏权限
            MediaProjectionManager mMediaProjectionManager = (MediaProjectionManager)
                    this.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            if (mMediaProjectionManager != null) {
                this.startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_STABILITY);
            }
        } else {
            // 无需申请录屏权限，直接启动服务
            Intent service = new Intent(this, StabilityMonitorService.class)
                    .putExtra(CacheConst.KEY_PLATFORM_NAME, checked_plat)
                    .putExtra(CacheConst.KEY_IS_HAVING_OTHER_PERFORMANCE_MONITOR, isHaveOtherPerformance);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(service);
            } else {
                startService(service);
            }
        }
    }

    private void startFxService() {
        MediaProjectionManager mMediaProjectionManager = (MediaProjectionManager)
                this.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mMediaProjectionManager != null) {
            this.startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_FX);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_STABILITY && resultCode == RESULT_OK) {
            Intent service = new Intent(this, StabilityMonitorService.class)
                    .putExtra(CacheConst.KEY_PLATFORM_NAME, checked_plat)
                    .putExtra(CacheConst.KEY_IS_HAVING_OTHER_PERFORMANCE_MONITOR, isHaveOtherPerformance)
                    .putExtra("resultCode", resultCode)
                    .putExtra("data", data);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(service);
            } else {
                startService(service);
            }
        } else if (requestCode == REQUEST_FX && resultCode == RESULT_OK) {
            ServiceUtil.startFxService(this, resultCode, data);
            if (CacheConst.PLATFORM_NAME_RED_FINGER_CLOUD_PHONE.equals(checked_plat)) {
                ApkUtil.launchApp(this, getString(R.string.pkg_name_red_finger_game));
            } else if (CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_GAME.equals(checked_plat)) {
                ApkUtil.launchApp(this, getString(R.string.pkg_name_huawei_cloud_game));
            } else if (CacheConst.PLATFORM_NAME_E_CLOUD_PHONE.equals(checked_plat)) {
                ApkUtil.launchApp(this, getString(R.string.pkg_name_e_cloud_phone));
            } else if (CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_PHONE.equals(checked_plat)) {
                ApkUtil.launchApp(this, getString(R.string.pkg_name_huawei_cloud_phone));
            } else if (CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_PHONE.equals(checked_plat)) {
                ApkUtil.launchApp(this, getString(R.string.pkg_name_net_ease_cloud_phone));
            }
        }
    }

}
