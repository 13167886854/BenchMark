package com.example.benchmark.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.benchmark.Adapter.CePingAdapter;
import com.example.benchmark.Data.CepingData;
import com.example.benchmark.DiaLog.PopDiaLog;
import com.example.benchmark.InitbenchMarkData.InitData;
import com.example.benchmark.R;
import com.example.benchmark.Service.OCRService;
import com.example.benchmark.utils.AccessUtils;
import com.example.benchmark.utils.RequestDataUtils;
import com.example.benchmark.Service.StabilityMonitorService;
import com.example.benchmark.utils.CacheConst;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CePingActivity extends Activity implements View.OnClickListener {

    private final  int REQUEST_CODE = 0;

    private ImageButton ceshi_fanhui;
    private RecyclerView recyclerView;
    private TextView cepingtv,ceping_phone_name;
    private List<CepingData> ceping_data;
    private CePingAdapter adapter;
    private Intent intent;

    private String select_plat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ceping);
        initview();
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        new Thread(() -> deepFile("tessdata")).start();
    }

    void initview(){
        ceshi_fanhui=findViewById(R.id.ceshi_fanhui);
        recyclerView=findViewById(R.id.ceping_rv);
        cepingtv=findViewById(R.id.ceping_tv);
        ceping_phone_name=findViewById(R.id.ceping_phone_name);
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

    void initdata(Intent intent){
        String cheaked_plat = intent.getStringExtra("cheaked_plat");
        select_plat=cheaked_plat;
        ceping_phone_name.setText(cheaked_plat);
        InitData data = new InitData();
        ceping_data=new ArrayList<>();
        Map<String, CepingData> map = data.getMap();
        if (intent.getBooleanExtra("blue_liuchang_cheak",false)){
            CepingData liuchang = map.get("blue_liuchang");
            ceping_data.add(liuchang);
        }
        if (intent.getBooleanExtra("blue_wending_cheak",false)){
            CepingData liuchang = map.get("blue_wending");
            ceping_data.add(liuchang);
            startMonitorStability();
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

    /**
     * 将assets中的文件复制出
     *
     * @param path
     */
    public void deepFile(String path) {
        String newPath = getExternalFilesDir(null) + "/";
        try {
            String str[] = getAssets().list(path);
            if (str.length > 0) {//如果是目录
                File file = new File(newPath + path);
                file.mkdirs();
                for (String string : str) {
                    path = path + "/" + string;
                    deepFile(path);
                    path = path.substring(0, path.lastIndexOf('/'));//回到原来的path
                }
            } else {//如果是文件
                InputStream is = getAssets().open(path);
                FileOutputStream fos = new FileOutputStream(new File(newPath + path));
                byte[] buffer = new byte[1024];
                while (true) {
                    int len = is.read(buffer);
                    if (len == -1) {
                        break;
                    }
                    fos.write(buffer, 0, len);
                }
                is.close();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
