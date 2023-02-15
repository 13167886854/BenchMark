package com.example.benchmark.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.example.benchmark.Adapter.CePingAdapter;
import com.example.benchmark.Data.Admin;
import com.example.benchmark.Data.CepingData;
import com.example.benchmark.Data.YinHuaData;
import com.example.benchmark.R;
import com.example.benchmark.Service.BothRecordService;
import com.example.benchmark.Service.FxService;
import com.example.benchmark.Service.GameSmoothTestService;
import com.example.benchmark.Service.GameTouchTestService;
import com.example.benchmark.Service.GameVATestService;
import com.example.benchmark.Service.VideoRecordService;
import com.example.benchmark.utils.ApkUtil;
import com.example.benchmark.utils.CacheUtil;
import com.example.benchmark.Service.MyAccessibilityService;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.ScoreUtil;
import com.example.benchmark.utils.ServiceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CePingActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "CePingActivity";
    private final int REQUEST_STABILITY = 0, REQUEST_FX = 1;
    private ImageButton ceshi_fanhui;
    private RecyclerView recyclerView;
    private TextView cepingtv, ceping_phone_name;
    private TextView mMonitorProgress;
    private List<CepingData> ceping_data = new ArrayList<>();
    private CePingAdapter adapter;
    private String checked_plat;
    private String platform_kind;
    private Boolean isCloudPhone;
    private boolean isCheckStability, isCheckFluency, isCheckTouch, isCheckSoundFrame,
            isCheckCPU, isCheckGPU, isCheckROM, isCheckRAM, isHaveOtherPerformance, isFluencyUntested,
            isGameTouchTested, isAudioVideoTested;


    private ServiceConnection sm_connection;
    private ServiceConnection touch_connection;
    private ServiceConnection record_connection;
    private ServiceConnection connection;
    private ServiceConnection va_connection;
    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private GameSmoothTestService gameSmoothService;
    private GameTouchTestService gameTouchTestService;
    private GameVATestService gameVATestService;
    private VideoRecordService videoRecordService;
    private BothRecordService bothRecordService;
    private FxService fxService;
    private static final int RECORD_SM_REQUEST_CODE = 111;
    private static final int RECORD_VA_REQUEST_CODE = 222;
    private static final int RECORD_TOUCH_REQUEST_CODE = 333;
    private static final int RECORD_SCREEN_REQUEST_CODE = 444;
    private static final int STORAGE_REQUEST_CODE = 555;
    private static final int AUDIO_REQUEST_CODE = 666;
    private static final int RECORD_REQUEST_CODE = 777;
    private HashMap mHashMapLocal = null;

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
            intent.putExtra("isCloudPhone", isCloudPhone);
            intent.putExtra("localMobileInfo", mHashMapLocal);

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
        mMonitorProgress = findViewById(R.id.ceping_jindu);
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
        Log.d(TAG, "initData: checked_plat===" + checked_plat);
        // 获取平台名称
        Admin.platformName = checked_plat;

        YinHuaData.platform_type = checked_plat;
        ceping_phone_name.setText(checked_plat);
        CacheUtil.put(CacheConst.KEY_PLATFORM_NAME, checked_plat);
        platform_kind = intent.getStringExtra(CacheConst.KEY_PLATFORM_KIND);
        isCheckStability = intent.getBooleanExtra(CacheConst.KEY_STABILITY_INFO, false);
        isCheckFluency = intent.getBooleanExtra(CacheConst.KEY_FLUENCY_INFO, false);
        isCheckTouch = intent.getBooleanExtra(CacheConst.KEY_TOUCH_INFO, false);
        isCheckSoundFrame = intent.getBooleanExtra(CacheConst.KEY_SOUND_FRAME_INFO, false);
        isCheckCPU = intent.getBooleanExtra(CacheConst.KEY_CPU_INFO, false);
        isCheckGPU = intent.getBooleanExtra(CacheConst.KEY_GPU_INFO, false);
        isCheckRAM = intent.getBooleanExtra(CacheConst.KEY_RAM_INFO, false);
        isCheckROM = intent.getBooleanExtra(CacheConst.KEY_ROM_INFO, false);

        if (checked_plat.equals(CacheConst.PLATFORM_NAME_MI_GU_GAME) || checked_plat.equals(CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_GAME) || checked_plat.equals(CacheConst.PLATFORM_NAME_Tencent_GAME)) {
            if (isCheckCPU || isCheckGPU || isCheckRAM || isCheckROM) {
                mHashMapLocal = (HashMap) intent.getSerializableExtra("localMobileInfo");
                Log.d(TAG, "initData: hashMap-------" + mHashMapLocal);
            }
        }

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

        Log.d("TWT", "platform_kind: " + platform_kind);
        if (isCheckStability && !CacheUtil.getBoolean(CacheConst.KEY_STABILITY_IS_MONITORED)) {
            startStabilityMonitorService();
        }
        if ( isHaveOtherPerformance && platform_kind.equals(CacheConst.PLATFORM_KIND_CLOUD_PHONE)) {
//        if (!isCheckStability && isHaveOtherPerformance) {
            Intent fxIntent = new Intent(this, FxService.class);
            startService(fxIntent);
            startFxService();
        }

        //云游戏流畅性测试
        if (platform_kind.equals(CacheConst.PLATFORM_KIND_CLOUD_GAME) && isCheckFluency
                && !isFluencyUntested
        ) {
            //Toast.makeText(this,"准备测试云游戏流畅性！",Toast.LENGTH_SHORT).show();
            //开启流畅性测试悬浮窗
            startGameSmoothService();
//                if (CacheConst.PLATFORM_NAME_Tencent_GAME.equals(checked_plat)) {
//                    ApkUtil.launchApp(this, getString(R.string.pkg_name_tencent_gamer));
//                } else if (CacheConst.PLATFORM_NAME_MI_GU_GAME.equals(checked_plat)) {
//                    ApkUtil.launchApp(this, getString(R.string.pkg_name_mi_gu_play));
//                } else if (CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_GAME.equals(checked_plat)) {
//                    ApkUtil.launchApp(this, getString(R.string.pkg_name_net_ease_cloud_phone));
//                }
        } else if (platform_kind.equals(CacheConst.PLATFORM_KIND_CLOUD_GAME) && isCheckSoundFrame && !isAudioVideoTested) {
            startGameVAService();
            //Log.d("TWT", "initData: 开始测试游戏音画质量");
        } else if (platform_kind.equals(CacheConst.PLATFORM_KIND_CLOUD_GAME) && isCheckTouch && !isGameTouchTested) {
            //开始云游戏触控体验测试。。。
            //Log.d("TWT", "开始云游戏触控体验测试");
            startGameTouchService();
        }
    }

    @Override
    protected void onStart() {
        updateListData();
        super.onStart();
    }

    private void updateListData() {
        Intent intent = getIntent();
        isCloudPhone = CacheConst.PLATFORM_KIND_CLOUD_PHONE.equals(
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
                    //ScoreUtil.getCPUScore(),
                    //0,
                    isCloudPhone ? R.drawable.blue_cpu : R.drawable.red_cpu,
                    CacheConst.KEY_CPU_INFO,
                    getString(R.string.cpu_info_description)
            ));
        }
        if (isCheckGPU) {
            ceping_data.add(new CepingData(
                    //ScoreUtil.getGPUScore(),
                    //0,
                    isCloudPhone ? R.drawable.blue_gpu : R.drawable.red_gpu,
                    CacheConst.KEY_GPU_INFO,
                    getString(R.string.gpu_info_description)
            ));
        }
        if (isCheckRAM) {
            ceping_data.add(new CepingData(
                    //ScoreUtil.getRAMScore(),
                    //0,
                    isCloudPhone ? R.drawable.blue_ram : R.drawable.red_ram,
                    CacheConst.KEY_RAM_INFO,
                    getString(R.string.ram_info_description)
            ));
        }
        if (isCheckROM) {
            ceping_data.add(new CepingData(
                    //ScoreUtil.getROMScore(),
                    //0,
                    isCloudPhone ? R.drawable.blue_rom : R.drawable.red_rom,
                    CacheConst.KEY_ROM_INFO,
                    getString(R.string.rom_info_description)
            ));
        }
        if (adapter != null) adapter.notifyItemRangeChanged(0, ceping_data.size());
    }


//    private void startRecordService(){
//        record_connection = new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName className, IBinder service) {
//                DisplayMetrics metrics = new DisplayMetrics();
//                getWindowManager().getDefaultDisplay().getMetrics(metrics);
//                VideoRecordService.RecordBinder binder = (VideoRecordService.RecordBinder) service;
//                videoRecordService = binder.getRecordService();
//                videoRecordService.setConfig(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi);
//                //start.setText(recordService.isRunning() ? "停止录制" : "开始录制");
//            }
//            @Override
//            public void onServiceDisconnected(ComponentName arg0) {
//            }
//        };
//        //录屏权限申请
//        if (ContextCompat.checkSelfPermission(CePingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
//        }
//        if (ContextCompat.checkSelfPermission(CePingActivity.this, Manifest.permission.RECORD_AUDIO)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_REQUEST_CODE);
//        }
//        Intent intent = new Intent(this, VideoRecordService.class);
//        bindService(intent, record_connection, BIND_AUTO_CREATE);
//        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
//        Intent captureIntent = projectionManager.createScreenCaptureIntent();
//        startActivityForResult(captureIntent, RECORD_SCREEN_REQUEST_CODE);
//    }


    private void startGameTouchService() {
        touch_connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                GameTouchTestService.RecordBinder binder = (GameTouchTestService.RecordBinder) service;
                gameTouchTestService = binder.getRecordService();
                gameTouchTestService.setConfig(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi);
                //start.setText(recordService.isRunning() ? "停止录制" : "开始录制");
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
            }
        };
        //录屏权限申请
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
        bindService(intent, touch_connection, BIND_AUTO_CREATE);
        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        Intent captureIntent = projectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, RECORD_TOUCH_REQUEST_CODE);
    }


    private void startGameVAService() {
        va_connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                GameVATestService.RecordBinder binder = (GameVATestService.RecordBinder) service;
                gameVATestService = binder.getRecordService();
                gameVATestService.setConfig(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi);
                //start.setText(recordService.isRunning() ? "停止录制" : "开始录制");
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
            }
        };
        //录屏权限申请
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
        bindService(intent, va_connection, BIND_AUTO_CREATE);
        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        Intent captureIntent = projectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, RECORD_VA_REQUEST_CODE);
    }


    private void startGameSmoothService() {
        sm_connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                GameSmoothTestService.GameSmoothBinder binder = (GameSmoothTestService.GameSmoothBinder) service;
                gameSmoothService = binder.getGameSmoothService();
                gameSmoothService.setConfig(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi);
                //start.setText(recordService.isRunning() ? "停止录制" : "开始录制");
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
            }
        };
        //录屏权限申请
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
        bindService(intent, sm_connection, BIND_AUTO_CREATE);
        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        Intent captureIntent = projectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, RECORD_SM_REQUEST_CODE);
    }


    private void startStabilityMonitorService() {
        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        if (projectionManager != null) {
            this.startActivityForResult(projectionManager.createScreenCaptureIntent(), REQUEST_STABILITY);
        }
    }

    private void startFxService() {
//        MediaProjectionManager mMediaProjectionManager = (MediaProjectionManager)
//                this.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//        if (mMediaProjectionManager != null) {
//            this.startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_FX);
//        }
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                FxService.RecordBinder binder = (FxService.RecordBinder) service;
                fxService = binder.getRecordService();
                //start.setText(recordService.isRunning() ? "停止录制" : "开始录制");
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
            }
        };

        //录屏权限申请
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
        if(!isCheckStability){
            projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            Intent captureIntent = projectionManager.createScreenCaptureIntent();
            startActivityForResult(captureIntent, REQUEST_FX);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_STABILITY && resultCode == RESULT_OK) {
            Intent service = new Intent(this, MyAccessibilityService.class)
                    .putExtra(CacheConst.KEY_PLATFORM_NAME, checked_plat)
                    .putExtra(CacheConst.KEY_IS_HAVING_OTHER_PERFORMANCE_MONITOR, isHaveOtherPerformance)
                    .putExtra("resultCode", resultCode)
                    .putExtra("data", data)
                    .putExtra("isCheckTouch", isCheckTouch);

            if(isHaveOtherPerformance){
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
        } else if (requestCode == REQUEST_FX && resultCode == RESULT_OK) {
            mediaProjection = projectionManager.getMediaProjection(resultCode, data);
            fxService.setMediaProject(mediaProjection);
            fxService.setPara(isCheckTouch, isCheckSoundFrame);
            try {
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
                } else if (CacheConst.PLATFORM_NAME_Tencent_GAME.equals(checked_plat)) {
                    ApkUtil.launchApp(this, getString(R.string.pkg_name_tencent_gamer));
                } else if (CacheConst.PLATFORM_NAME_MI_GU_GAME.equals(checked_plat)) {
                    ApkUtil.launchApp(this, getString(R.string.pkg_name_mi_gu_play));
                    ApkUtil.launchApp(this, getString(R.string.pkg_name_mi_gu_play2));
                } else if (CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_GAME.equals(checked_plat)) {
                    ApkUtil.launchApp(this, getString(R.string.pkg_name_net_ease_cloud_phone));
                }
            } catch (Exception e) {
                Log.e("TWT", "ERROR:" + e.toString());
            }
        } else if (requestCode == RECORD_SM_REQUEST_CODE && resultCode == RESULT_OK) {
            mediaProjection = projectionManager.getMediaProjection(resultCode, data);
            gameSmoothService.setMediaProject(mediaProjection);
            try {
                if (CacheConst.PLATFORM_NAME_Tencent_GAME.equals(checked_plat)) {
                    ApkUtil.launchApp(this, getString(R.string.pkg_name_tencent_gamer));
                } else if (CacheConst.PLATFORM_NAME_MI_GU_GAME.equals(checked_plat)) {
                    ApkUtil.launchApp(this, getString(R.string.pkg_name_mi_gu_play));
                    ApkUtil.launchApp(this, getString(R.string.pkg_name_mi_gu_play2));
                } else if (CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_GAME.equals(checked_plat)) {
                    ApkUtil.launchApp(this, getString(R.string.pkg_name_net_ease_cloud_phone));
                }
            } catch (Exception e) {
                Log.e("TWT", "ERROR:" + e.toString());
            }
        } else if (requestCode == RECORD_VA_REQUEST_CODE && resultCode == RESULT_OK) {
            mediaProjection = projectionManager.getMediaProjection(resultCode, data);
            gameVATestService.setMediaProject(mediaProjection);
            try {
                if (CacheConst.PLATFORM_NAME_Tencent_GAME.equals(checked_plat)) {
                    ApkUtil.launchApp(this, getString(R.string.pkg_name_tencent_gamer));
                } else if (CacheConst.PLATFORM_NAME_MI_GU_GAME.equals(checked_plat)) {
                    ApkUtil.launchApp(this, getString(R.string.pkg_name_mi_gu_play));
                    ApkUtil.launchApp(this, getString(R.string.pkg_name_mi_gu_play2));
                } else if (CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_GAME.equals(checked_plat)) {
                    ApkUtil.launchApp(this, getString(R.string.pkg_name_net_ease_cloud_phone));
                }
            } catch (Exception e) {
                Log.e("TWT", "ERROR:" + e.toString());
            }
        } else if (requestCode == RECORD_TOUCH_REQUEST_CODE && resultCode == RESULT_OK) {
            //    Log.e("TWT", "onActivityResult: 123111111111111111111" );
            mediaProjection = projectionManager.getMediaProjection(resultCode, data);
            gameTouchTestService.setMediaProject(mediaProjection);
            try {
                if (CacheConst.PLATFORM_NAME_Tencent_GAME.equals(checked_plat)) {
                    ApkUtil.launchApp(this, getString(R.string.pkg_name_tencent_gamer));
                } else if (CacheConst.PLATFORM_NAME_MI_GU_GAME.equals(checked_plat)) {
                    ApkUtil.launchApp(this, getString(R.string.pkg_name_mi_gu_play));
                    ApkUtil.launchApp(this, getString(R.string.pkg_name_mi_gu_play2));
                } else if (CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_GAME.equals(checked_plat)) {
                    ApkUtil.launchApp(this, getString(R.string.pkg_name_net_ease_cloud_phone));
                }
            } catch (Exception e) {
                Log.e("TWT", "ERROR:" + e.toString());
            }
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        updateListData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateListData();
    }

    @Override
    protected void onDestroy() {
        //unbindService(sm_connection);
        //unbindService(touch_connection);
        //unbindService(connection);
        super.onDestroy();
    }
}
