package com.example.benchmark.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.benchmark.Fragment.SettingFragment;
import com.example.benchmark.Fragment.TishiFragment;
import com.example.benchmark.Fragment.ZhuyeFragment;
import com.example.benchmark.R;
import com.example.benchmark.Service.FxService;
import com.example.benchmark.Service.StabilityMonitorService;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    private FragmentManager fragmentManager;
    private RadioGroup main_menu;
    private ImageButton menu,qr_code;

    private Intent fxService;

    private final int REQUEST_FX = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fram,new ZhuyeFragment());
        fragmentTransaction.commit();
        main_menu.setOnCheckedChangeListener(this::onCheckedChanged);
        // 测量屏幕长宽
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        CacheUtil.put(CacheConst.KEY_SCREEN_WIDTH, dm.widthPixels);
        CacheUtil.put(CacheConst.KEY_SCREEN_HEIGHT, dm.heightPixels);
        CacheUtil.put(CacheConst.KEY_SCREEN_DPI, dm.densityDpi);

    }

    private   void init(){
        main_menu=findViewById(R.id.main_select_menu);
        menu=findViewById(R.id.menu);
        qr_code=findViewById(R.id.qr_code);

        qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Settings.canDrawOverlays(MainActivity.this)) {
                    toFloatGetPermission();
                }else{
                    if(!FxService.isFxServiceWorking){
                        startFxService();
                    }else{
                        stopService(fxService);
                    }

                }
            }
        });


    }


    @Override
    protected void onRestart() {
        super.onRestart();
//        Intent intent = getIntent();
//        overridePendingTransition(0, 0);
//        finish();
//        overridePendingTransition(0, 0);
//        startActivity(intent);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.main_zhuye:{
                ChangeFragment(new ZhuyeFragment(),true);
                break;
            }
            case R.id.main_tishi:{
                ChangeFragment(new TishiFragment(),true);
                break;
            }
            case R.id.main_setting:{
                ChangeFragment(new SettingFragment(),true);
                break;
            }

        }
    }

    public  void  ChangeFragment(Fragment fragment, boolean isFisrt){
        fragmentManager=getSupportFragmentManager();
        //开启事务
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fram,fragment);
        if (!isFisrt){
            fragmentTransaction.addToBackStack(null);;
        }
        fragmentTransaction.commit();
    }

    public void toFloatGetPermission() {
        Toast.makeText(MainActivity.this, "请允许本应用显示悬浮窗！", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        Log.d("TWT", "toFloatGetPermission: " + Uri.parse("package:" + getPackageName()));
        //intent.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        this.startActivity(intent);
        //startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")), 0);

    }


    private void startFxService(){
        MediaProjectionManager mMediaProjectionManager = (MediaProjectionManager)
                this.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mMediaProjectionManager != null) {
            this.startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_FX);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_FX && resultCode == RESULT_OK){
            fxService = new Intent(this, FxService.class)
                    .putExtra("resultCode", resultCode)
                    .putExtra("data", data);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //startForegroundService(service);
                startService(fxService);
            } else {
                startService(fxService);
            }

        }
    }

}