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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.benchmark.Adapter.JutiAdapter;
import com.example.benchmark.Data.JuTiData;
import com.example.benchmark.R;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;
import com.example.benchmark.utils.ScoreUtil;

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

        switch (select_item) {
            case CacheConst.KEY_FLUENCY_INFO: {
                data = new ArrayList<>();
                data.add(new JuTiData("平均帧率", ScoreUtil.getAverageFPS() + "fps"));
                data.add(new JuTiData("抖动帧率(方差)", ScoreUtil.getFrameShakeRate() + "%"));
                data.add(new JuTiData("低帧率", ScoreUtil.getLowFrameRate() + "%"));
                data.add(new JuTiData("帧间隔", ScoreUtil.getFrameInterval() + "ms"));
                data.add(new JuTiData("jank", ScoreUtil.getJankCount() + "次"));
                data.add(new JuTiData("卡顿时长占比", ScoreUtil.getStutterRate() + "%"));
                break ;
            }
            case CacheConst.KEY_STABILITY_INFO: {
                data = new ArrayList<>();
                data.add(new JuTiData("启动成功率", ScoreUtil.getStartSuccessRate() + "%"));
                data.add(new JuTiData("平均启动时长", ScoreUtil.getAverageStartTime() + "ms"));
                data.add(new JuTiData("平均退出时长", ScoreUtil.getAverageQuitTime() + "ms"));
                break ;
            }
            case CacheConst.KEY_TOUCH_INFO: {
                data = new ArrayList<>();
                data.add(new JuTiData("平均正确率", ScoreUtil.getAverageAccuracy() + "%"));
                data.add(new JuTiData("平均响应时间", ScoreUtil.getResponseTime() + "ms"));
                data.add(new JuTiData("单点平均响应时间", ScoreUtil.getAverageResponseTime() + "ms"));
                break ;
            }
            case CacheConst.KEY_SOUND_FRAME_INFO: {
                data = new ArrayList<>();
                data.add(new JuTiData("分辨率", ScoreUtil.getResolution() + "px"));
                data.add(new JuTiData("音画同步差", ScoreUtil.getMaxDiffValue() + "ms"));
                break ;
            }
            case CacheConst.KEY_CPU_INFO: {
                data = new ArrayList<>();
                data.add(new JuTiData("cpu名称", CacheUtil.getString(CacheConst.KEY_CPU_NAME)));
                data.add(new JuTiData("cpu核数",CacheUtil.getInt(CacheConst.KEY_CPU_CORES) + "核"));
                break ;
            }
            case CacheConst.KEY_GPU_INFO: {
                data = new ArrayList<>();
                data.add(new JuTiData("gpu供应商", CacheUtil.getString(CacheConst.KEY_GPU_VENDOR)));
                data.add(new JuTiData("gpu渲染器", CacheUtil.getString(CacheConst.KEY_GPU_RENDER)));
                data.add(new JuTiData("gpu版本", CacheUtil.getString(CacheConst.KEY_GPU_VERSION)));
                break;
            }
            case CacheConst.KEY_ROM_INFO: {
                data = new ArrayList<>();
                data.add(new JuTiData("可用内存",CacheUtil.getFloat(CacheConst.KEY_AVAILABLE_STORAGE) + "GB"));
                data.add(new JuTiData("总内存",CacheUtil.getFloat(CacheConst.KEY_TOTAL_STORAGE) + "GB"));
                break ;

            }
            case CacheConst.KEY_RAM_INFO: {
                data = new ArrayList<>();
                data.add(new JuTiData("可用硬盘",CacheUtil.getFloat(CacheConst.KEY_AVAILABLE_RAM) + "GB"));
                data.add(new JuTiData("总硬盘",CacheUtil.getFloat(CacheConst.KEY_TOTAL_RAM) + "GB"));
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
