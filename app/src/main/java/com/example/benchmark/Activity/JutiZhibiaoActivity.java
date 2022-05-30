<<<<<<< HEAD
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.benchmark.Adapter.JutiAdapter;
import com.example.benchmark.Data.JuTiData;
//import com.example.benchmark.Fragment.JuTiChuKongFragment;
//import com.example.benchmark.Fragment.JuTiCpuFragment;
//import com.example.benchmark.Fragment.JuTiLiuChangFragment;
//import com.example.benchmark.Fragment.JuTiWenDingFragment;
//import com.example.benchmark.Fragment.JuTiYinHuaFragment;
import com.example.benchmark.Data.MobileCloud;
import com.example.benchmark.R;

import java.util.ArrayList;
import java.util.List;

public class JutiZhibiaoActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton back;

    private TextView juti_phone_name,juti_grade;
    private Button back_ceping,next_zhibiao;

    private ImageView juti_img;
    private TextView juti_text,juti_item;

    private FragmentManager fragmentManager;

    private RecyclerView recyclerView;
    private List<JuTiData> data;

    private JutiAdapter jutiAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ceping_xiangxi);
        initview();
        back.setOnClickListener(this::onClick);
        back_ceping.setOnClickListener(this::onClick);
        Intent intent = getIntent();
        initdata(intent);
        jutiAdapter = new JutiAdapter(JutiZhibiaoActivity.this,data);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(jutiAdapter);
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

        recyclerView=findViewById(R.id.juti_rv);

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


        out:switch (select_plat) {
            /**
             * 红手指云手机
             */
            case "红手指云手机": {
                in:switch (select_item) {
                    case "流畅性": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("平均帧率(fps)", "29.7"));
                        data.add(new JuTiData("抖动帧率(方差)", "1.06"));
                        data.add(new JuTiData("低帧率(%)", "6.63%"));
                        data.add(new JuTiData("帧间隔(ms)", "22.93"));
                        data.add(new JuTiData("jank(卡顿次数/10min)", "0.18"));
                        data.add(new JuTiData("卡顿市场占比(%)", "0.76"));
                        break ;
                    }
                    case "稳定性": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("启动成功率(%)", "96%"));
                        data.add(new JuTiData("启动时长(ms)", "34ms"));
                        data.add(new JuTiData("退出时长", "21ms"));
                        break ;
                    }
                    case "触控体验": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("点击灵敏度", "98%"));
                        data.add(new JuTiData("具体响应时延", "21ms"));
                        break ;
                    }
                    case "音画质量": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("分辨率(px)", "1080x2440"));
                        data.add(new JuTiData("音画同步差(ms)", "100ms"));
                        data.add(new JuTiData("画面质量PSNR/SSIM(%)", "39%"));
                        data.add(new JuTiData("音频质量PESQ", "1ms"));
                        break ;
                    }
                    case "cpu": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("cpuName", "麒麟9000"));
                        data.add(new JuTiData("cpu型号", "麒麟9000"));
                        data.add(new JuTiData("cpu最大利用率", "3.2GHz"));
                        data.add(new JuTiData("cpu核心数", MobileCloud.cpuCoreNum));
                        break ;
                    }
                    case "gpu": {
                        data = new ArrayList<>();
                        break;
                    }
                    case "内存": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("内存", "12GG"));
                        data.add(new JuTiData("读写速率", "500MB/S"));
                        break ;

                    }
                    case "硬盘": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("硬盘", "256G"));
                        break ;
                    }
                }
                break ;

            }




            case "移动云手机": {
               in: switch (select_item) {
                    case "流畅性": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("平均帧率(fps)", "29.7"));
                        data.add(new JuTiData("抖动帧率(方差)", "1.06"));
                        data.add(new JuTiData("低帧率(%)", "6.63%"));
                        data.add(new JuTiData("帧间隔(ms)", "22.93"));
                        data.add(new JuTiData("jank(卡顿次数/10min)", "0.18"));
                        data.add(new JuTiData("卡顿市场占比(%)", "0.76"));
                        break ;
                    }
                    case "稳定性": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("启动成功率(%)", "96%"));
                        data.add(new JuTiData("启动时长(ms)", "34ms"));
                        data.add(new JuTiData("退出时长", "21ms"));
                        break ;
                    }
                    case "触控体验": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("点击灵敏度", "98%"));
                        data.add(new JuTiData("具体响应时延", "21ms"));
                        break ;
                    }
                    case "音画质量": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("分辨率(px)", "1080x2440"));
                        data.add(new JuTiData("音画同步差(ms)", "100ms"));
                        data.add(new JuTiData("画面质量PSNR/SSIM(%)", "39%"));
                        data.add(new JuTiData("音频质量PESQ", "1ms"));
                        break ;
                    }
                    case "cpu": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("cpuName", "麒麟9000"));
                        data.add(new JuTiData("cpu型号", "麒麟9000"));
                        data.add(new JuTiData("cpu最大利用率", "3.2GHz"));
                        data.add(new JuTiData("cpu核心数", MobileCloud.cpuCoreNum));
                        break ;
                    }
                    case "gpu": {
                        data = new ArrayList<>();
                        break;
                    }
                    case "内存": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("内存", "12GG"));
                        data.add(new JuTiData("读写速率", "500MB/S"));
                        break ;

                    }
                    case "硬盘": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("硬盘", "128G"));
                        break ;
                    }
                }
                break ;

            }


            /**
             * 鲲鹏云手机
             */
            case "鲲鹏云手机": {
                in:switch (select_item) {
                    case "流畅性": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("平均帧率(fps)", "29.7"));
                        data.add(new JuTiData("抖动帧率(方差)", "1.06"));
                        data.add(new JuTiData("低帧率(%)", "6.63%"));
                        data.add(new JuTiData("帧间隔(ms)", "22.93"));
                        data.add(new JuTiData("jank(卡顿次数/10min)", "0.18"));
                        data.add(new JuTiData("卡顿市场占比(%)", "0.76"));
                        break ;
                    }
                    case "稳定性": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("启动成功率(%)", "96%"));
                        data.add(new JuTiData("启动时长(ms)", "34ms"));
                        data.add(new JuTiData("退出时长", "21ms"));
                        break ;
                    }
                    case "触控体验": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("点击灵敏度", "98%"));
                        data.add(new JuTiData("具体响应时延", "21ms"));
                        break ;
                    }
                    case "音画质量": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("分辨率(px)", "1080x2440"));
                        data.add(new JuTiData("音画同步差(ms)", "100ms"));
                        data.add(new JuTiData("画面质量PSNR/SSIM(%)", "39%"));
                        data.add(new JuTiData("音频质量PESQ", "1ms"));
                        break ;
                    }
                    case "cpu": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("cpuName", "麒麟9000"));
                        data.add(new JuTiData("cpu型号", "麒麟9000"));
                        data.add(new JuTiData("cpu最大利用率", "3.2GHz"));
                        data.add(new JuTiData("cpu核心数", MobileCloud.cpuCoreNum));
                        break ;
                    }
                    case "gpu": {
                        data = new ArrayList<>();
                        break;
                    }
                    case "内存": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("内存", "12GG"));
                        data.add(new JuTiData("读写速率", "500MB/S"));
                        break ;

                    }
                    case "硬盘": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("硬盘", "64G"));
                        break ;
                    }
                }
                break ;



            }

            /**
             * 网易云手机
             */
            case "网易云手机": {
                in:switch (select_item) {
                    case "流畅性": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("平均帧率(fps)", "29.7"));
                        data.add(new JuTiData("抖动帧率(方差)", "1.06"));
                        data.add(new JuTiData("低帧率(%)", "6.63%"));
                        data.add(new JuTiData("帧间隔(ms)", "22.93"));
                        data.add(new JuTiData("jank(卡顿次数/10min)", "0.18"));
                        data.add(new JuTiData("卡顿市场占比(%)", "0.76"));
                        break ;
                    }
                    case "稳定性": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("启动成功率(%)", "96%"));
                        data.add(new JuTiData("启动时长(ms)", "34ms"));
                        data.add(new JuTiData("退出时长", "21ms"));
                        break ;
                    }
                    case "触控体验": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("点击灵敏度", "98%"));
                        data.add(new JuTiData("具体响应时延", "21ms"));
                        break ;
                    }
                    case "音画质量": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("分辨率(px)", "1080x2440"));
                        data.add(new JuTiData("音画同步差(ms)", "100ms"));
                        data.add(new JuTiData("画面质量PSNR/SSIM(%)", "39%"));
                        data.add(new JuTiData("音频质量PESQ", "1ms"));
                        break ;
                    }
                    case "cpu": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("cpuName", "麒麟9000"));
                        data.add(new JuTiData("cpu型号", "麒麟9000"));
                        data.add(new JuTiData("cpu最大利用率", "3.2GHz"));
                        data.add(new JuTiData("cpu核心数", MobileCloud.cpuCoreNum));
                        break ;
                    }
                    case "gpu": {
                        data = new ArrayList<>();
                        break;
                    }
                    case "内存": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("内存", "12GG"));
                        data.add(new JuTiData("读写速率", "500MB/S"));
                        break ;

                    }
                    case "硬盘": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("硬盘", "32G"));
                        break ;
                    }
                }
                break ;


            }


            case "腾讯先锋": {
               in:switch (select_item) {
                    case "流畅性": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("平均帧率(fps)", "29.7"));
                        data.add(new JuTiData("抖动帧率(方差)", "1.06"));
                        data.add(new JuTiData("低帧率(%)", "6.63%"));
                        data.add(new JuTiData("帧间隔(ms)", "22.93"));
                        data.add(new JuTiData("jank(卡顿次数/10min)", "0.18"));
                        data.add(new JuTiData("卡顿市场占比(%)", "0.76"));
                        break ;
                    }
                    case "稳定性": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("启动成功率(%)", "96%"));
                        data.add(new JuTiData("启动时长(ms)", "34ms"));
                        data.add(new JuTiData("退出时长", "21ms"));
                        break;
                    }
                    case "触控体验": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("点击灵敏度", "98%"));
                        data.add(new JuTiData("具体响应时延", "21ms"));
                        break ;
                    }
                    case "音画质量": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("分辨率(px)", "1080x2440"));
                        data.add(new JuTiData("音画同步差(ms)", "100ms"));
                        data.add(new JuTiData("画面质量PSNR/SSIM(%)", "39%"));
                        data.add(new JuTiData("音频质量PESQ", "1ms"));
                        break ;
                    }
                    case "cpu": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("cpuName", "麒麟9000"));
                        data.add(new JuTiData("cpu型号", "麒麟9000"));
                        data.add(new JuTiData("cpu最大利用率", "3.2GHz"));
                        data.add(new JuTiData("cpu核心数", MobileCloud.cpuCoreNum));
                        break ;
                    }
                    case "gpu": {
                        data = new ArrayList<>();
                        break ;
                    }
                    case "内存": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("内存", "12GG"));
                        data.add(new JuTiData("读写速率", "500MB/S"));
                        break ;

                    }
                    case "硬盘": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("硬盘", "33G"));
                        break ;
                    }
                }
                break ;



            }

            case "咪咕快游": {
                in:switch (select_item) {
                    case "流畅性": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("平均帧率(fps)", "29.7"));
                        data.add(new JuTiData("抖动帧率(方差)", "1.06"));
                        data.add(new JuTiData("低帧率(%)", "6.63%"));
                        data.add(new JuTiData("帧间隔(ms)", "22.93"));
                        data.add(new JuTiData("jank(卡顿次数/10min)", "0.18"));
                        data.add(new JuTiData("卡顿市场占比(%)", "0.76"));
                        break ;
                    }
                    case "稳定性": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("启动成功率(%)", "96%"));
                        data.add(new JuTiData("启动时长(ms)", "34ms"));
                        data.add(new JuTiData("退出时长", "21ms"));
                        break ;
                    }
                    case "触控体验": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("点击灵敏度", "98%"));
                        data.add(new JuTiData("具体响应时延", "21ms"));
                        break ;
                    }
                    case "音画质量": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("分辨率(px)", "1080x2440"));
                        data.add(new JuTiData("音画同步差(ms)", "100ms"));
                        data.add(new JuTiData("画面质量PSNR/SSIM(%)", "39%"));
                        data.add(new JuTiData("音频质量PESQ", "1ms"));
                        break ;
                    }
                    case "cpu": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("cpuName", "麒麟9000"));
                        data.add(new JuTiData("cpu型号", "麒麟9000"));
                        data.add(new JuTiData("cpu最大利用率", "3.2GHz"));
                        data.add(new JuTiData("cpu核心数", MobileCloud.cpuCoreNum));
                        break;
                    }
                    case "gpu": {
                        data = new ArrayList<>();
                        break ;
                    }
                    case "内存": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("内存", "12GG"));
                        data.add(new JuTiData("读写速率", "500MB/S"));
                        break ;

                    }
                    case "硬盘": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("硬盘", "16G"));
                        break ;
                    }
                }
                break ;


            }


            case "网易云游戏": {
                switch (select_item) {
                    case "流畅性": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("平均帧率(fps)", "29.7"));
                        data.add(new JuTiData("抖动帧率(方差)", "1.06"));
                        data.add(new JuTiData("低帧率(%)", "6.63%"));
                        data.add(new JuTiData("帧间隔(ms)", "22.93"));
                        data.add(new JuTiData("jank(卡顿次数/10min)", "0.18"));
                        data.add(new JuTiData("卡顿市场占比(%)", "0.76"));
                        break;
                    }
                    case "稳定性": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("启动成功率(%)", "96%"));
                        data.add(new JuTiData("启动时长(ms)", "34ms"));
                        data.add(new JuTiData("退出时长", "21ms"));
                        break;
                    }
                    case "触控体验": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("点击灵敏度", "98%"));
                        data.add(new JuTiData("具体响应时延", "21ms"));
                        break;
                    }
                    case "音画质量": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("分辨率(px)", "1080x2440"));
                        data.add(new JuTiData("音画同步差(ms)", "100ms"));
                        data.add(new JuTiData("画面质量PSNR/SSIM(%)", "39%"));
                        data.add(new JuTiData("音频质量PESQ", "1ms"));
                        break;
                    }
                    case "cpu": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("cpuName", "麒麟9000"));
                        data.add(new JuTiData("cpu型号", "麒麟9000"));
                        data.add(new JuTiData("cpu最大利用率", "3.2GHz"));
                        data.add(new JuTiData("cpu核心数", MobileCloud.cpuCoreNum));
                        break;
                    }
                    case "gpu": {
                        data = new ArrayList<>();
                        break;
                    }
                    case "内存": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("内存", "12GG"));
                        data.add(new JuTiData("读写速率", "500MB/S"));
                        break;

                    }
                    case "硬盘": {
                        data = new ArrayList<>();
                        data.add(new JuTiData("硬盘", "8G"));
                        break ;
                    }
                }
                break ;

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
//    public  void  ChangeFragment(Fragment fragment, boolean isFisrt){
//        fragmentManager=getSupportFragmentManager();
//        //开启事务
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.juti_fram,fragment);
//        if (!isFisrt){
//            fragmentTransaction.addToBackStack(null);;
//        }
//        fragmentTransaction.commit();
//    }
}
=======
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.benchmark.Adapter.JutiAdapter;
import com.example.benchmark.Data.JuTiData;
import com.example.benchmark.R;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;

import java.util.ArrayList;
import java.util.List;

public class JutiZhibiaoActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton back;

    private TextView juti_phone_name,juti_grade;
    private Button back_ceping,next_zhibiao;

    private ImageView juti_img;
    private TextView juti_text,juti_item;

    private FragmentManager fragmentManager;

    private RecyclerView recyclerView;
    private List<JuTiData> data;

    private JutiAdapter jutiAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ceping_xiangxi);
        initview();
        back.setOnClickListener(this::onClick);
        back_ceping.setOnClickListener(this::onClick);
        Intent intent = getIntent();
        initdata(intent);
        jutiAdapter = new JutiAdapter(JutiZhibiaoActivity.this,data);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(jutiAdapter);
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

        recyclerView=findViewById(R.id.juti_rv);

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
                data=new ArrayList<>();
                data.add(new JuTiData("平均帧率(fps)","29.7"));
                data.add(new JuTiData("抖动帧率(方差)","1.06"));
                data.add(new JuTiData("低帧率(%)","6.63%"));
                data.add(new JuTiData("帧间隔(ms)","22.93"));
                data.add(new JuTiData("jank(卡顿次数/10min)","0.18"));
                data.add(new JuTiData("卡顿市场占比(%)","0.76"));
                break;
            }
            case "稳定性":{
                data=new ArrayList<>();
                data.add(new JuTiData("启动成功率(%)", CacheUtil.getFloat(CacheConst.KEY_START_SUCCESS_RATE) + "%"));
                data.add(new JuTiData("启动时长(ms)",CacheUtil.getFloat(CacheConst.KEY_AVERAGE_START_TIME) + "ms"));
                data.add(new JuTiData("退出时长",CacheUtil.getFloat(CacheConst.KEY_AVERAGE_QUIT_TIME) + "ms"));
                break;
            }
            case "触控体验":{
                data=new ArrayList<>();
                data.add(new JuTiData("点击灵敏度","98%"));
                data.add(new JuTiData("具体响应时延","21ms"));
                break;
            }
            case "音画质量":{
                data=new ArrayList<>();
               data.add(new JuTiData("分辨率(px)","1080x2440"));
               data.add(new JuTiData("音画同步差(ms)","100ms"));
               data.add(new JuTiData("画面质量PSNR/SSIM(%)","39%"));
               data.add(new JuTiData("音频质量PESQ","1ms"));
                break;
            }
            case "cpu":{
                data=new ArrayList<>();
                data.add(new JuTiData("cpuName","麒麟9000"));
                data.add(new JuTiData("cpu型号","麒麟9000"));
                data.add(new JuTiData("cpu最大利用率","3.2GHz"));
                break;
            }
            case "gpu":{
                data=new ArrayList<>();
                break;
            }
            case "内存":{
                data=new ArrayList<>();
                data.add(new JuTiData("内存","12GG"));
                data.add(new JuTiData("读写速率","500MB/S"));
                break;

            }
            case "硬盘":{
                data=new ArrayList<>();
                data.add(new JuTiData("硬盘","256G"));
                break;
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
//    public  void  ChangeFragment(Fragment fragment, boolean isFisrt){
//        fragmentManager=getSupportFragmentManager();
//        //开启事务
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.juti_fram,fragment);
//        if (!isFisrt){
//            fragmentTransaction.addToBackStack(null);;
//        }
//        fragmentTransaction.commit();
//    }
}
>>>>>>> 211fdf0 ([feat]华为云手机、云手游，红手指、移动云手机稳定性测评)
