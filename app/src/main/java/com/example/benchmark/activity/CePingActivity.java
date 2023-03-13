/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.benchmark.adapter.CePingAdapter;
import com.example.benchmark.data.Admin;
import com.example.benchmark.data.CepingData;
import com.example.benchmark.data.YinHuaData;
import com.example.benchmark.R;
import com.example.benchmark.service.BothRecordService;
import com.example.benchmark.service.FxService;
import com.example.benchmark.service.GameSmoothTestService;
import com.example.benchmark.service.GameTouchTestService;
import com.example.benchmark.service.GameVATestService;
import com.example.benchmark.service.VideoRecordService;
import com.example.benchmark.utils.ApkUtil;
import com.example.benchmark.utils.CacheUtil;
import com.example.benchmark.service.MyAccessibilityService;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.ScoreUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * CePingActivity
 *
 * @version 1.0
 * @since 2023/3/7 15:05
 */
public class CePingActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "CePingActivity";
    private static final int RECORD_SM_REQUEST_CODE = 111;
    private static final int RECORD_VA_REQUEST_CODE = 222;
    private static final int RECORD_TOUCH_REQUEST_CODE = 333;
    private static final int RECORD_SCREEN_REQUEST_CODE = 444;
    private static final int STORAGE_REQUEST_CODE = 555;
    private static final int AUDIO_REQUEST_CODE = 666;
    private static final int RECORD_REQUEST_CODE = 777;

    private final int requestStability = 0;
    private final int requestFX = 1;

    private ImageButton testBack;
    private RecyclerView recyclerView;
    private TextView phoneName;
    private TextView testTextView;
    private TextView mMonitorProgress;
    private List<CepingData> testData = new ArrayList<>();
    private CePingAdapter adapter;
    private String checkedPlat;
    private String platformKind;
    private Boolean isCloudPhone;
    private boolean isCheckStability;
    private boolean isCheckFluency;
    private boolean isCheckTouch;
    private boolean isCheckSoundFrame;
    private boolean isCheckCPU;
    private boolean isCheckGPU;
    private boolean isCheckROM;
    private boolean isCheckRAM;
    private boolean isHaveOtherPerformance;
    private boolean isFluencyUntested;
    private boolean isGameTouchTested;
    private boolean isAudioVideoTested;

    private ServiceConnection smoothConnection;
    private ServiceConnection touchConnection;
    private ServiceConnection recordConnection;
    private ServiceConnection connection;
    private ServiceConnection vaConnection;
    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private GameSmoothTestService gameSmoothService;
    private GameTouchTestService gameTouchTestService;
    private GameVATestService gameVATestService;
    private VideoRecordService videoRecordService;
    private BothRecordService bothRecordService;
    private FxService fxService;

    private HashMap mHashMapLocal = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ceping);

        initView();
        initData();

        testBack.setOnClickListener(this::onClick);
        adapter = new CePingAdapter(testData, (data) -> {
            Intent intent = new Intent(this, JutiZhibiaoActivity.class);
            intent.putExtra("select_plat", checkedPlat);
            intent.putExtra("select_item", data.getCepingItem());
            intent.putExtra("select_img", data.getCepingImage());
            intent.putExtra("select_text", data.getCepingText());
            intent.putExtra("select_grade", data.getGrade());
            intent.putExtra("isCloudPhone", isCloudPhone);
            intent.putExtra("localMobileInfo", mHashMapLocal);

            startActivity(intent);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }

    void initView() {
        testBack = findViewById(R.id.ceshi_fanhui);
        recyclerView = findViewById(R.id.ceping_rv);
        testTextView = findViewById(R.id.ceping_tv);
        phoneName = findViewById(R.id.ceping_phone_name);
        mMonitorProgress = findViewById(R.id.ceping_jindu);
    }

    /**
     * onClick
     *
     * @param view description
     * @return void
     * @date 2023/3/9 19:54
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ceshi_fanhui: {
                finish();
            }
        }
    }

    /**
     * initData
     *
     * @return void
     * @date 2023/3/9 19:55
     */
    void initData() {
        Intent intent = getIntent();
        checkedPlat = intent.getStringExtra(CacheConst.KEY_PLATFORM_NAME);
        Log.d(TAG, "initData: checkedPlat===" + checkedPlat);
        init1(intent);

        init2(intent);
    }

    private void init2(Intent intent) {
        if (isCheckFluency || isCheckTouch || isCheckSoundFrame) {
            isHaveOtherPerformance = true;
        } else if (isCheckCPU || isCheckGPU || isCheckRAM || isCheckROM) {
            isHaveOtherPerformance = true;
        } else {
            isHaveOtherPerformance = false;
        }

        isFluencyUntested = intent.getBooleanExtra("isFluencyUntested", false);
        isGameTouchTested = intent.getBooleanExtra("isGameTouchTested", false);
        isAudioVideoTested = intent.getBooleanExtra("isAudioVideoTested", false);
        updateListData();

        Log.d("TWT", "platformKind: " + platformKind);
        if (isCheckStability && !CacheUtil.getBoolean(CacheConst.KEY_STABILITY_IS_MONITORED)) {
            startStabilityMonitorService();
        }
        if (isHaveOtherPerformance && platformKind.equals(CacheConst.PLATFORM_KIND_CLOUD_PHONE)) {
            Intent fxIntent = new Intent(this, FxService.class);
            startService(fxIntent);
            startFxService();
        }
        // 云游戏流畅性测试
        if (platformKind.equals(CacheConst.PLATFORM_KIND_CLOUD_GAME) && isCheckFluency
                && !isFluencyUntested
        ) {
            // 开启流畅性测试悬浮窗
            startGameSmoothService();
        } else if (platformKind.equals(CacheConst.PLATFORM_KIND_CLOUD_GAME)
                && isCheckSoundFrame && !isAudioVideoTested) {
            startGameVAService();
        } else if (platformKind.equals(CacheConst.PLATFORM_KIND_CLOUD_GAME)
                && isCheckTouch && !isGameTouchTested) {
            // 开始云游戏触控体验测试。。。
            startGameTouchService();
        } else {
            Log.e(TAG, "非正常启动 ");
        }
    }

    private void init1(Intent intent) {
        // 获取平台名称
        Admin.getInstance().setPlatformName(checkedPlat);

        YinHuaData.getInstance().setPlatformType(checkedPlat);
        phoneName.setText(checkedPlat);
        CacheUtil.put(CacheConst.KEY_PLATFORM_NAME, checkedPlat);
        platformKind = intent.getStringExtra(CacheConst.KEY_PLATFORM_KIND);
        isCheckStability = intent.getBooleanExtra(CacheConst.KEY_STABILITY_INFO, false);
        isCheckFluency = intent.getBooleanExtra(CacheConst.KEY_FLUENCY_INFO, false);
        isCheckTouch = intent.getBooleanExtra(CacheConst.KEY_TOUCH_INFO, false);
        isCheckSoundFrame = intent.getBooleanExtra(CacheConst.KEY_SOUND_FRAME_INFO, false);
        isCheckCPU = intent.getBooleanExtra(CacheConst.KEY_CPU_INFO, false);
        isCheckGPU = intent.getBooleanExtra(CacheConst.KEY_GPU_INFO, false);
        isCheckRAM = intent.getBooleanExtra(CacheConst.KEY_RAM_INFO, false);
        isCheckROM = intent.getBooleanExtra(CacheConst.KEY_ROM_INFO, false);

        if (checkedPlat.equals(CacheConst.PLATFORM_NAME_MI_GU_GAME)
                || checkedPlat.equals(CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_GAME)
                || checkedPlat.equals(CacheConst.PLATFORM_NAME_TENCENT_GAME)) {
            if (isCheckCPU || isCheckGPU || isCheckRAM || isCheckROM) {
                if (intent.getSerializableExtra("localMobileInfo") instanceof HashMap) {
                    mHashMapLocal = (HashMap) intent.getSerializableExtra("localMobileInfo");
                    Log.d(TAG, "initData: hashMap-------" + mHashMapLocal);
                }
            }
        }
    }

    /**
     * onStart
     *
     * @return void
     * @date 2023/3/9 19:55
     */
    @Override
    protected void onStart() {
        updateListData();
        super.onStart();
    }

    /**
     * updateListData
     *
     * @return void
     * @date 2023/3/9 19:55
     */
    private void updateListData() {
        Intent intent = getIntent();
        isCloudPhone = CacheConst.PLATFORM_KIND_CLOUD_PHONE.equals(
                intent.getStringExtra(CacheConst.KEY_PLATFORM_KIND));
        testData.clear();
        checkState();
        checkBaseState();
    }

    private void checkBaseState() {
        if (isCheckCPU) {
            testData.add(new CepingData(
                    isCloudPhone ? R.drawable.blue_cpu : R.drawable.red_cpu,
                    CacheConst.KEY_CPU_INFO,
                    getString(R.string.cpu_info_description)
            ));
        }
        if (isCheckGPU) {
            testData.add(new CepingData(
                    isCloudPhone ? R.drawable.blue_gpu : R.drawable.red_gpu,
                    CacheConst.KEY_GPU_INFO,
                    getString(R.string.gpu_info_description)
            ));
        }
        if (isCheckRAM) {
            testData.add(new CepingData(
                    isCloudPhone ? R.drawable.blue_ram : R.drawable.red_ram,
                    CacheConst.KEY_RAM_INFO,
                    getString(R.string.ram_info_description)
            ));
        }
        if (isCheckROM) {
            testData.add(new CepingData(
                    isCloudPhone ? R.drawable.blue_rom : R.drawable.red_rom,
                    CacheConst.KEY_ROM_INFO,
                    getString(R.string.rom_info_description)
            ));
        }
        if (adapter != null) {
            adapter.notifyItemRangeChanged(0, testData.size());
        }
    }

    private void checkState() {
        if (isCheckStability) {
            testData.add(new CepingData(
                    ScoreUtil.getStabilityScore(),
                    isCloudPhone ? R.drawable.blue_wending : R.drawable.red_wending,
                    CacheConst.KEY_STABILITY_INFO,
                    getString(R.string.stability_info_description)
            ));
        }
        if (isCheckFluency) {
            testData.add(new CepingData(
                    ScoreUtil.getFluencyScore(),
                    isCloudPhone ? R.drawable.blue_liuchang : R.drawable.blue_liuchang,
                    CacheConst.KEY_FLUENCY_INFO,
                    getString(R.string.fluency_info_description)
            ));
        }
        if (isCheckTouch) {
            testData.add(new CepingData(
                    ScoreUtil.getTouchScore(),
                    isCloudPhone ? R.drawable.blue_chukong : R.drawable.red_chukong,
                    CacheConst.KEY_TOUCH_INFO,
                    getString(R.string.touch_info_description)
            ));
        }
        if (isCheckSoundFrame) {
            testData.add(new CepingData(
                    ScoreUtil.getSoundFrameScore(),
                    isCloudPhone ? R.drawable.blue_yinhua : R.drawable.red_yinhua,
                    CacheConst.KEY_SOUND_FRAME_INFO,
                    getString(R.string.sound_frame_info_description)
            ));
        }
    }

    private void startGameTouchService() {
        touchConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                if (service instanceof GameTouchTestService.RecordBinder) {
                    GameTouchTestService.RecordBinder binder = (GameTouchTestService.RecordBinder) service;
                    gameTouchTestService = binder.getRecordService();
                    gameTouchTestService.setConfig(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
            }
        };
        // 录屏权限申请
        if (ContextCompat.checkSelfPermission(CePingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
        }
        if (ContextCompat.checkSelfPermission(CePingActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_REQUEST_CODE);
        }
        Intent intent = new Intent(this, GameTouchTestService.class);
        bindService(intent, touchConnection, BIND_AUTO_CREATE);
        if (getSystemService(MEDIA_PROJECTION_SERVICE) instanceof MediaProjectionManager) {
            projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            Intent captureIntent = projectionManager.createScreenCaptureIntent();
            startActivityForResult(captureIntent, RECORD_TOUCH_REQUEST_CODE);
        }
    }


    private void startGameVAService() {
        vaConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                if (service instanceof GameVATestService.RecordBinder) {
                    GameVATestService.RecordBinder binder = (GameVATestService.RecordBinder) service;
                    gameVATestService = binder.getRecordService();
                    gameVATestService.setConfig(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
            }
        };
        // 录屏权限申请
        if (ContextCompat.checkSelfPermission(CePingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
        }
        if (ContextCompat.checkSelfPermission(CePingActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_REQUEST_CODE);
        }
        Intent intent = new Intent(this, GameVATestService.class);
        bindService(intent, vaConnection, BIND_AUTO_CREATE);
        if (getSystemService(MEDIA_PROJECTION_SERVICE) instanceof MediaProjectionManager) {
            projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            Intent captureIntent = projectionManager.createScreenCaptureIntent();
            startActivityForResult(captureIntent, RECORD_VA_REQUEST_CODE);
        }
    }


    private void startGameSmoothService() {
        smoothConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                if (service instanceof GameSmoothTestService.GameSmoothBinder) {
                    GameSmoothTestService.GameSmoothBinder binder = (GameSmoothTestService.GameSmoothBinder) service;
                    gameSmoothService = binder.getGameSmoothService();
                    gameSmoothService.setConfig(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
            }
        };
        // 录屏权限申请
        if (ContextCompat.checkSelfPermission(CePingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
        }
        if (ContextCompat.checkSelfPermission(CePingActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_REQUEST_CODE);
        }
        Intent intent = new Intent(this, GameSmoothTestService.class);
        bindService(intent, smoothConnection, BIND_AUTO_CREATE);
        if (getSystemService(MEDIA_PROJECTION_SERVICE) instanceof MediaProjectionManager) {
            projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            Intent captureIntent = projectionManager.createScreenCaptureIntent();
            startActivityForResult(captureIntent, RECORD_SM_REQUEST_CODE);
        }
    }


    private void startStabilityMonitorService() {
        if (getSystemService(MEDIA_PROJECTION_SERVICE) instanceof MediaProjectionManager) {
            projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            if (projectionManager != null) {
                this.startActivityForResult(projectionManager.createScreenCaptureIntent(), requestStability);
            }
        }
    }

    private void startFxService() {
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                if (service instanceof FxService.RecordBinder) {
                    FxService.RecordBinder binder = (FxService.RecordBinder) service;
                    fxService = binder.getRecordService();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
            }
        };

        // 录屏权限申请
        if (ContextCompat.checkSelfPermission(CePingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
        }
        if (ContextCompat.checkSelfPermission(CePingActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_REQUEST_CODE);
        }
        Intent intent = new Intent(this, FxService.class);
        intent.putExtra("isCheckSoundFrame", isCheckSoundFrame);
        intent.putExtra("isCheckTouch", isCheckTouch);
        bindService(intent, connection, BIND_AUTO_CREATE);
        if (!isCheckStability) {
            if (getSystemService(MEDIA_PROJECTION_SERVICE) instanceof MediaProjectionManager) {
                projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
                Intent captureIntent = projectionManager.createScreenCaptureIntent();
                startActivityForResult(captureIntent, requestFX);
            }
        }
    }

    /**
     * onActivityResult
     *
     * @param requestCode description
     * @param resultCode  description
     * @param data        description
     * @return void
     * @date 2023/3/9 19:55
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestStability && resultCode == RESULT_OK) {
            stability(resultCode, data);
        } else if (requestCode == requestFX && resultCode == RESULT_OK) {
            fx(resultCode, data);
        } else if (requestCode == RECORD_SM_REQUEST_CODE && resultCode == RESULT_OK) {
            recordsmooth(resultCode, data);
        } else if (requestCode == RECORD_VA_REQUEST_CODE && resultCode == RESULT_OK) {
            recordVideoAudio(resultCode, data);
        } else if (requestCode == RECORD_TOUCH_REQUEST_CODE && resultCode == RESULT_OK) {
            touch(resultCode, data);
        } else {
            Log.e(TAG, "非正常启动");
        }
    }

    private void touch(int resultCode, @Nullable Intent data) {
        mediaProjection = projectionManager.getMediaProjection(resultCode, data);
        gameTouchTestService.setMediaProject(mediaProjection);

        if (CacheConst.PLATFORM_NAME_TENCENT_GAME.equals(checkedPlat)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_tencent_gamer));
        } else if (CacheConst.PLATFORM_NAME_MI_GU_GAME.equals(checkedPlat)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_mi_gu_play));
            ApkUtil.launchApp(this, getString(R.string.pkg_name_mi_gu_play2));
        } else if (CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_GAME.equals(checkedPlat)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_net_ease_cloud_phone));
        } else {
            Log.e(TAG, "非正常启动");
        }
    }

    private void recordVideoAudio(int resultCode, @Nullable Intent data) {
        mediaProjection = projectionManager.getMediaProjection(resultCode, data);
        gameVATestService.setMediaProject(mediaProjection);
        if (CacheConst.PLATFORM_NAME_TENCENT_GAME.equals(checkedPlat)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_tencent_gamer));
        } else if (CacheConst.PLATFORM_NAME_MI_GU_GAME.equals(checkedPlat)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_mi_gu_play));
            ApkUtil.launchApp(this, getString(R.string.pkg_name_mi_gu_play2));
        } else if (CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_GAME.equals(checkedPlat)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_net_ease_cloud_phone));
        } else {
            Log.e(TAG, "非正常启动");
        }
    }

    private void recordsmooth(int resultCode, @Nullable Intent data) {
        mediaProjection = projectionManager.getMediaProjection(resultCode, data);
        gameSmoothService.setMediaProject(mediaProjection);
        if (CacheConst.PLATFORM_NAME_TENCENT_GAME.equals(checkedPlat)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_tencent_gamer));
        } else if (CacheConst.PLATFORM_NAME_MI_GU_GAME.equals(checkedPlat)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_mi_gu_play));
            ApkUtil.launchApp(this, getString(R.string.pkg_name_mi_gu_play2));
        } else if (CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_GAME.equals(checkedPlat)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_net_ease_cloud_phone));
        } else {
            Log.e(TAG, "非正常启动");
        }
    }

    private void fx(int resultCode, @Nullable Intent data) {
        mediaProjection = projectionManager.getMediaProjection(resultCode, data);
        fxService.setMediaProject(mediaProjection);
        fxService.setPara(isCheckTouch, isCheckSoundFrame);
        if (CacheConst.PLATFORM_NAME_RED_FINGER_CLOUD_PHONE.equals(checkedPlat)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_red_finger_game));
        } else if (CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_GAME.equals(checkedPlat)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_huawei_cloud_game));
        } else if (CacheConst.PLATFORM_NAME_E_CLOUD_PHONE.equals(checkedPlat)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_e_cloud_phone));
        } else if (CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_PHONE.equals(checkedPlat)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_huawei_cloud_phone));
        } else if (CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_PHONE.equals(checkedPlat)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_net_ease_cloud_phone));
        } else if (CacheConst.PLATFORM_NAME_TENCENT_GAME.equals(checkedPlat)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_tencent_gamer));
        } else if (CacheConst.PLATFORM_NAME_MI_GU_GAME.equals(checkedPlat)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_mi_gu_play));
            ApkUtil.launchApp(this, getString(R.string.pkg_name_mi_gu_play2));
        } else if (CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_GAME.equals(checkedPlat)) {
            ApkUtil.launchApp(this, getString(R.string.pkg_name_net_ease_cloud_phone));
        } else {
            Log.e(TAG, "非正常启动");
        }
    }

    private void stability(int resultCode, @Nullable Intent data) {
        Intent service = new Intent(this, MyAccessibilityService.class)
                .putExtra(CacheConst.KEY_PLATFORM_NAME, checkedPlat)
                .putExtra(CacheConst.KEY_IS_HAVING_OTHER_PERFORMANCE_MONITOR, isHaveOtherPerformance)
                .putExtra("resultCode", resultCode)
                .putExtra("data", data)
                .putExtra("isCheckTouch", isCheckTouch);

        if (isHaveOtherPerformance) {
            mediaProjection = projectionManager.getMediaProjection(resultCode, data);
            fxService.setMediaProject(mediaProjection);
            fxService.setPara(isCheckTouch, isCheckSoundFrame);
        }
        Log.e("TWT", "onActivityResult: " + isCheckSoundFrame);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(service);
        } else {
            startService(service);
        }
    }

    /**
     * onRestart
     *
     * @return void
     * @date 2023/3/9 19:55
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        updateListData();
    }

    /**
     * onResume
     *
     * @return void
     * @date 2023/3/9 19:55
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateListData();
    }

    /**
     * onDestroy
     *
     * @return void
     * @date 2023/3/9 19:56
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
