package com.example.benchmark.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import com.example.benchmark.DiaLog.PopDiaLog;
import com.example.benchmark.Fragment.SettingFragment;
import com.example.benchmark.Fragment.TishiFragment;
import com.example.benchmark.Fragment.ZhuyeFragment;
import com.example.benchmark.R;
import com.example.benchmark.utils.AccessUtils;

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
    }

    private   void init(){
        main_menu=findViewById(R.id.main_select_menu);
        menu=findViewById(R.id.menu);
        qr_code=findViewById(R.id.qr_code);


    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
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