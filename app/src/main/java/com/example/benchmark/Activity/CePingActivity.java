package com.example.benchmark.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
<<<<<<< HEAD
import android.content.Intent;
=======
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
>>>>>>> 211fdf0 ([feat]华为云手机、云手游，红手指、移动云手机稳定性测评)
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.benchmark.Adapter.CePingAdapter;
import com.example.benchmark.Data.CepingData;
<<<<<<< HEAD
import com.example.benchmark.InitbenchMarkData.InitData;
import com.example.benchmark.DiaLog.PopDiaLog;
import com.example.benchmark.R;
import com.example.benchmark.utils.AccessUtils;
import com.example.benchmark.utils.RequestDataUtils;
=======
import com.example.benchmark.Data.InitData;
import com.example.benchmark.R;
import com.example.benchmark.Service.StabilityMonitorService;
import com.example.benchmark.utils.CacheConst;
>>>>>>> 211fdf0 ([feat]华为云手机、云手游，红手指、移动云手机稳定性测评)

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CePingActivity extends Activity implements View.OnClickListener {
<<<<<<< HEAD
=======
    private final int REQUEST_CODE = 0;

>>>>>>> 211fdf0 ([feat]华为云手机、云手游，红手指、移动云手机稳定性测评)
    private ImageButton ceshi_fanhui;
    private RecyclerView recyclerView;
    private TextView cepingtv,ceping_phone_name;
    private List<CepingData> ceping_data;
    private CePingAdapter adapter;
    private  Intent intent;
<<<<<<< HEAD
    private static String select_plat;
    private AccessUtils accessUtils;
    private PopDiaLog popDiaLog;

=======
    private String select_plat;
>>>>>>> 211fdf0 ([feat]华为云手机、云手游，红手指、移动云手机稳定性测评)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ceping);
        initview();
<<<<<<< HEAD

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
=======
        intent = getIntent();
        initdata(intent);
        ceshi_fanhui.setOnClickListener(this::onClick);
        adapter=new CePingAdapter(CePingActivity.this, ceping_data, (data) -> {
            Intent intent = new Intent(this, JutiZhibiaoActivity.class);
            intent.putExtra("select_plat",select_plat);
            intent.putExtra("select_item",data.getCepingItem());
            intent.putExtra("select_img",data.getCepingImage());
            intent.putExtra("select_text",data.getCepingText());
            intent.putExtra("select_grade",data.getGrade());
            startActivity(intent);
        });
>>>>>>> 211fdf0 ([feat]华为云手机、云手游，红手指、移动云手机稳定性测评)
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }

<<<<<<< HEAD


    void initview(){

=======
    void initview(){
>>>>>>> 211fdf0 ([feat]华为云手机、云手游，红手指、移动云手机稳定性测评)
        ceshi_fanhui=findViewById(R.id.ceshi_fanhui);
        recyclerView=findViewById(R.id.ceping_rv);
        cepingtv=findViewById(R.id.ceping_tv);
        ceping_phone_name=findViewById(R.id.ceping_phone_name);
<<<<<<< HEAD
        accessUtils=new AccessUtils(CePingActivity.this);
        popDiaLog=new PopDiaLog(CePingActivity.this);

    }
    public  String getSelect_plat(){
        return select_plat;
    }
=======
    }

>>>>>>> 211fdf0 ([feat]华为云手机、云手游，红手指、移动云手机稳定性测评)
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ceshi_fanhui:{
                finish();
            }
        }
    }

<<<<<<< HEAD

    public static InitData data;

=======
>>>>>>> 211fdf0 ([feat]华为云手机、云手游，红手指、移动云手机稳定性测评)
    void initdata(Intent intent){
        String cheaked_plat = intent.getStringExtra("cheaked_plat");
        select_plat=cheaked_plat;
        ceping_phone_name.setText(cheaked_plat);
<<<<<<< HEAD
        data = new InitData();
=======
        InitData data = new InitData();
>>>>>>> 211fdf0 ([feat]华为云手机、云手游，红手指、移动云手机稳定性测评)
        ceping_data=new ArrayList<>();
        Map<String, CepingData> map = data.getMap();
        if (intent.getBooleanExtra("blue_liuchang_cheak",false)){
            CepingData liuchang = map.get("blue_liuchang");
            ceping_data.add(liuchang);
<<<<<<< HEAD

=======
>>>>>>> 211fdf0 ([feat]华为云手机、云手游，红手指、移动云手机稳定性测评)
        }
        if (intent.getBooleanExtra("blue_wending_cheak",false)){
            CepingData liuchang = map.get("blue_wending");
            ceping_data.add(liuchang);
<<<<<<< HEAD

=======
            startMonitorStability();
>>>>>>> 211fdf0 ([feat]华为云手机、云手游，红手指、移动云手机稳定性测评)
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
<<<<<<< HEAD



=======
>>>>>>> 211fdf0 ([feat]华为云手机、云手游，红手指、移动云手机稳定性测评)
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

<<<<<<< HEAD
=======
    private void startMonitorStability() {
        MediaProjectionManager mMediaProjectionManager = (MediaProjectionManager)
                this.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mMediaProjectionManager != null) {
            // 关键代码
            this.startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Intent service = new Intent(this, StabilityMonitorService.class)
                    .putExtra(CacheConst.KEY_PLATFORM_NAME, select_plat)
                    .putExtra("resultCode", resultCode)
                    .putExtra("data", data);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(service);
            } else {
                startService(service);
            }
        }
    }
>>>>>>> 211fdf0 ([feat]华为云手机、云手游，红手指、移动云手机稳定性测评)

}
