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

}