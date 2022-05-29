package com.example.benchmark.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.benchmark.Adapter.CePingAdapter;
import com.example.benchmark.Data.CepingData;
import com.example.benchmark.InitbenchMarkData.InitData;
import com.example.benchmark.DiaLog.PopDiaLog;
import com.example.benchmark.R;
import com.example.benchmark.utils.AccessUtils;
import com.example.benchmark.utils.RequestDataUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CePingActivity extends Activity implements View.OnClickListener {
    private ImageButton ceshi_fanhui;
    private RecyclerView recyclerView;
    private TextView cepingtv,ceping_phone_name;
    private List<CepingData> ceping_data;
    private CePingAdapter adapter;
    private  Intent intent;
    private static String select_plat;
    private AccessUtils accessUtils;
    private PopDiaLog popDiaLog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ceping);
        initview();

        intent = getIntent();
        initdata(intent);
        ceshi_fanhui.setOnClickListener(this::onClick);

        switch (select_plat){
            case "移动云手机":{
                RequestDataUtils.getMobileCloudInfo();
            }
            case "红手指云手机":{
                RequestDataUtils.getRedFingureInfo();
            }
        }


        adapter=new CePingAdapter(CePingActivity.this,ceping_data);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }



    void initview(){

        ceshi_fanhui=findViewById(R.id.ceshi_fanhui);
        recyclerView=findViewById(R.id.ceping_rv);
        cepingtv=findViewById(R.id.ceping_tv);
        ceping_phone_name=findViewById(R.id.ceping_phone_name);
        accessUtils=new AccessUtils(CePingActivity.this);
        popDiaLog=new PopDiaLog(CePingActivity.this);

    }
    public  String getSelect_plat(){
        return select_plat;
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ceshi_fanhui:{
                finish();
            }
        }
    }


    public static InitData data;

    void initdata(Intent intent){
        String cheaked_plat = intent.getStringExtra("cheaked_plat");
        select_plat=cheaked_plat;
        ceping_phone_name.setText(cheaked_plat);
        data = new InitData();
        ceping_data=new ArrayList<>();
        Map<String, CepingData> map = data.getMap();
        if (intent.getBooleanExtra("blue_liuchang_cheak",false)){
            CepingData liuchang = map.get("blue_liuchang");
            ceping_data.add(liuchang);

        }
        if (intent.getBooleanExtra("blue_wending_cheak",false)){
            CepingData liuchang = map.get("blue_wending");
            ceping_data.add(liuchang);

        }
        if (intent.getBooleanExtra("blue_chukong_cheak",false)){
            CepingData liuchang = map.get("blue_chukong");
            ceping_data.add(liuchang);

        }
        if (intent.getBooleanExtra("blue_yinhua_cheak",false)){
            CepingData liuchang = map.get("blue_yinhua");
            ceping_data.add(liuchang);
        }

        if (intent.getBooleanExtra("blue_cpu_cheak",false)){
            CepingData liuchang = map.get("blue_cpu");
            ceping_data.add(liuchang);

        }
        if (intent.getBooleanExtra("blue_gpu_cheak",false)){
            CepingData liuchang = map.get("blue_gpu");
            ceping_data.add(liuchang);

        }
        if (intent.getBooleanExtra("blue_ram_cheak",false)){
            CepingData liuchang = map.get("blue_ram");
            ceping_data.add(liuchang);

        }
        if (intent.getBooleanExtra("blue_rom_cheak",false)){
            CepingData liuchang = map.get("blue_rom");
            ceping_data.add(liuchang);
        }



        if (intent.getBooleanExtra("red_liuchang_cheak",false)){
            CepingData liuchang = map.get("red_liuchang");
            ceping_data.add(liuchang);

        }
        if (intent.getBooleanExtra("red_wending_cheak",false)){
            CepingData liuchang = map.get("red_wending");
            ceping_data.add(liuchang);

        }
        if (intent.getBooleanExtra("red_chukong_cheak",false)){
            CepingData liuchang = map.get("red_chukong");
            ceping_data.add(liuchang);

        }
        if (intent.getBooleanExtra("red_yinhua_cheak",false)){
            CepingData liuchang = map.get("red_yinhua");
            ceping_data.add(liuchang);
        }

        if (intent.getBooleanExtra("red_cpu_cheak",false)){
            CepingData liuchang = map.get("red_cpu");
            ceping_data.add(liuchang);

        }
        if (intent.getBooleanExtra("red_gpu_cheak",false)){
            CepingData liuchang = map.get("red_gpu");
            ceping_data.add(liuchang);

        }
        if (intent.getBooleanExtra("red_ram_cheak",false)){
            CepingData liuchang = map.get("red_ram");
            ceping_data.add(liuchang);

        }
        if (intent.getBooleanExtra("red_rom_cheak",false)){
            CepingData liuchang = map.get("red_rom");
            ceping_data.add(liuchang);
        }
    }


}
