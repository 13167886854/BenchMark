package com.example.benchmark.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.benchmark.Fragment.JuTiChuKongFragment;
import com.example.benchmark.Fragment.JuTiCpuFragment;
import com.example.benchmark.Fragment.JuTiLiuChangFragment;
import com.example.benchmark.Fragment.JuTiWenDingFragment;
import com.example.benchmark.Fragment.JuTiYinHuaFragment;
import com.example.benchmark.R;

public class JutiZhibiaoActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton back;

    private TextView juti_phone_name,juti_grade;
    private Button back_ceping,next_zhibiao;

    private ImageView juti_img;
    private TextView juti_text,juti_item;

    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ceping_xiangxi);
        initview();
        back.setOnClickListener(this::onClick);
        back_ceping.setOnClickListener(this::onClick);
        Intent intent = getIntent();
        initdata(intent);
    }


    void initview(){
        back=findViewById(R.id.jutizhibiao_fanhui);
        juti_phone_name=findViewById(R.id.juti_phone_name);
        juti_grade=findViewById(R.id.juti_grade);

        back_ceping=findViewById(R.id.juti_back_ceping);
        next_zhibiao=findViewById(R.id.juti_next_ceping);


        juti_img=findViewById(R.id.juti_image);
        juti_item=findViewById(R.id.juti_item);
        juti_text=findViewById(R.id.juti_text);

    }

    @SuppressLint("SetTextI18n")
    void initdata(Intent intent){
        String select_plat = intent.getStringExtra("select_plat");
        String select_item = intent.getStringExtra("select_item");
        String select_text = intent.getStringExtra("select_text");
        Integer grade = intent.getIntExtra("select_grade",98);
        int select_img = intent.getIntExtra("select_img", R.drawable.blue_liuchang);

        juti_img.setImageResource(select_img);
        juti_text.setText(select_text);
        juti_item.setText(select_item);
        juti_phone_name.setText(select_plat+"·"+select_item);
        juti_grade.setText(String.valueOf(grade));

        switch (select_item){
            case "流畅性":{
                ChangeFragment(new JuTiLiuChangFragment(),true);

                break;
            }
            case "稳定性":{
                ChangeFragment(new JuTiWenDingFragment(),true);
                break;
            }
            case "触控体验":{
                ChangeFragment(new JuTiChuKongFragment(),true);
                break;
            }
            case "音画质量":{
                ChangeFragment(new JuTiYinHuaFragment(),true);
                break;
            }
            case "cpu":{
                ChangeFragment(new JuTiCpuFragment(),true);
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.jutizhibiao_fanhui:{
                finish();
                break;
            }
            case R.id.juti_back_ceping:{
                startActivity(new Intent(JutiZhibiaoActivity.this,CePingActivity.class));
                break;
            }

        }
    }
    public  void  ChangeFragment(Fragment fragment, boolean isFisrt){
        fragmentManager=getSupportFragmentManager();
        //开启事务
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.juti_fram,fragment);
        if (!isFisrt){
            fragmentTransaction.addToBackStack(null);;
        }
        fragmentTransaction.commit();
    }
}
