package com.example.benchmark.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Messenger;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.benchmark.Adapter.CePingAdapter;
import com.example.benchmark.Data.AudioVideoData;
import com.example.benchmark.Data.BaseData;
import com.example.benchmark.Data.CepingData;
import com.example.benchmark.Data.SmoothData;
import com.example.benchmark.Data.TestMode;
import com.example.benchmark.Data.TouchData;
import com.example.benchmark.Data.WenDingData;
import com.example.benchmark.DiaLog.PopDiaLog;
import com.example.benchmark.InitbenchMarkData.InitData;
import com.example.benchmark.R;
import com.example.benchmark.Service.FxService;
import com.example.benchmark.utils.AccessUtils;
import com.example.benchmark.utils.ApkUtil;
import com.example.benchmark.utils.ConfigurationUtils;
import com.example.benchmark.utils.OkhttpUtil;
import com.example.benchmark.utils.RequestDataUtils;
import com.example.benchmark.Service.StabilityMonitorService;
import com.example.benchmark.utils.CacheConst;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CePingActivity extends Activity implements View.OnClickListener {

    private final  int REQUEST_CODE = 0;

    private ImageButton ceshi_fanhui;
    private RecyclerView recyclerView;
    private TextView cepingtv,ceping_phone_name;
    private List<CepingData> ceping_data;
    private CePingAdapter adapter;
    private Intent intent;
    private String select_plat;
    private Handler handler;


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //Log.d("TWT", "isGetSmoothData: "+isGetSmoothData);
            if(!SmoothData.haveData){
                OkhttpUtil.getSmoothTestData(CePingActivity.this);
            }
            if(!TouchData.haveData){
                OkhttpUtil.getTouchData(CePingActivity.this);
            }

            if(!BaseData.haveData){
                OkhttpUtil.getBaseData(CePingActivity.this);
            }

            if(!AudioVideoData.haveData){
                OkhttpUtil.getAudioVideoData(CePingActivity.this);
            }


            //所有数据获取后停止轮询
            if(!(SmoothData.haveData&&TouchData.haveData&&BaseData.haveData&&AudioVideoData.haveData)){
                handler.postDelayed(this,5000);
            }

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ceping);

        //定时尝试从服务器获取测试流畅性数据
        //handler = new Handler();
        //handler.postDelayed(runnable, 5000);//每5秒执行一次runnable.

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


        //云手机平台测试



        if (intent.getBooleanExtra("blue_liuchang_cheak",false)){
            CepingData liuchang = map.get("blue_liuchang");
            TestMode.TestMode = 1;
            ceping_data.add(liuchang);
        }
        if (intent.getBooleanExtra("blue_chukong_cheak",false)){
            CepingData liuchang = map.get("blue_chukong");
            TestMode.TestMode = 1;
            ceping_data.add(liuchang);

        }
        if (intent.getBooleanExtra("blue_yinhua_cheak",false)){
            CepingData liuchang = map.get("blue_yinhua");
            TestMode.TestMode = 1;
            ceping_data.add(liuchang);
        }

        if (intent.getBooleanExtra("blue_cpu_cheak",false)){
            CepingData liuchang = map.get("blue_cpu");
            TestMode.TestMode = 1;
            ceping_data.add(liuchang);

        }
        if (intent.getBooleanExtra("blue_gpu_cheak",false)){
            CepingData liuchang = map.get("blue_gpu");
            TestMode.TestMode = 1;
            ceping_data.add(liuchang);

        }
        if (intent.getBooleanExtra("blue_ram_cheak",false)){
            CepingData liuchang = map.get("blue_ram");
            TestMode.TestMode = 1;
            ceping_data.add(liuchang);

        }
        if (intent.getBooleanExtra("blue_rom_cheak",false)){
            CepingData liuchang = map.get("blue_rom");
            TestMode.TestMode = 1;
            ceping_data.add(liuchang);
        }

        if (intent.getBooleanExtra("blue_wending_cheak",false)){
            CepingData liuchang = map.get("blue_wending");
            ceping_data.add(liuchang);
            if(TestMode.TestMode==0){
                TestMode.TestMode=2;
            }else if(TestMode.TestMode==1){
                TestMode.TestMode=3;
            }
        }


        if(TestMode.TestMode==0){
            //啥也不测试
        }else if(TestMode.TestMode==1){
            //linkWithCloudPhone();
            //仅测试云端apk项目
            //select_plat
            if (CacheConst.PLATFORM_NAME_RED_FINGER_CLOUD_PHONE.equals(select_plat)) {
                ApkUtil.launchApp(this, getString(R.string.pkg_name_red_finger_game));
            } else if (CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_GAME.equals(select_plat)) {
                ApkUtil.launchApp(this, getString(R.string.pkg_name_huawei_cloud_game));
            } else if (CacheConst.PLATFORM_NAME_E_CLOUD_PHONE.equals(select_plat)) {
                ApkUtil.launchApp(this, getString(R.string.pkg_name_e_cloud_phone));
            } else if (CacheConst.PLATFORM_NAME_HUAWEI_CLOUD_PHONE.equals(select_plat)) {
                ApkUtil.launchApp(this, getString(R.string.pkg_name_huawei_cloud_phone));
            } else if (CacheConst.PLATFORM_NAME_NET_EASE_CLOUD_PHONE.equals(select_plat)) {
                ApkUtil.launchApp(this, getString(R.string.pkg_name_net_ease_cloud_phone));
            }
        }else if(TestMode.TestMode==2){
            //仅测试稳定性
            startMonitorStability();
        }else if(TestMode.TestMode==3){
            //linkWithCloudPhone();
            //两种都进行测试
            startMonitorStability();
        }

        //云游戏平台测试

        if (intent.getBooleanExtra("red_liuchang_cheak",false)){
            CepingData liuchang = map.get("red_liuchang");
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
        if (intent.getBooleanExtra("red_wending_cheak",false)){
            CepingData liuchang = map.get("red_wending");
            ceping_data.add(liuchang);

        }
    }

   /* private void linkWithCloudPhone(){

        //请求连接云手机app
        OkHttpClient client = new OkHttpClient();
        //post发生测试结果数据
        FormBody formBody = new FormBody.Builder()
                .build();
        final Request request = new Request.Builder()
                .url(ConfigurationUtils.URL+"/localRequireToMatch")
                .post(formBody)
                .build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TWT", "onFailure: "+e.toString() );
                Looper.prepare();
                Toast.makeText(CePingActivity.this,"网络连接异常！",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                //Toast.makeText(getContext(),res,Toast.LENGTH_SHORT).show();
                if(res.length()>50){ //返回了一个找不到地址的html网页
                    Looper.prepare();
                    Toast.makeText(CePingActivity.this,"网络连接异常！",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return;
                }
                ConfigurationUtils.Link_key = res;
                Intent push =new Intent(CePingActivity.this,MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(CePingActivity.this,0,push,0);

                NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

                if(Build.VERSION.SDK_INT >= 26)
                {
                    //当sdk版本大于26
                    String id = "channel_1";
                    String description = "143";
                    int importance = NotificationManager.IMPORTANCE_LOW;
                    NotificationChannel channel = new NotificationChannel(id, description, importance);
//                     channel.enableLights(true);
//                     channel.enableVibration(true);//
                    manager.createNotificationChannel(channel);
                    Notification notification = new Notification.Builder(CePingActivity.this, id)
                            .setCategory(Notification.CATEGORY_MESSAGE)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("链接码："+ ConfigurationUtils.Link_key)
                            .setContentText("请在云手机端进行匹配连接后继续测试")
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .build();
                    manager.notify(1, notification);
                }
                else
                {
                    //当sdk版本小于26
                    Notification notification = new NotificationCompat.Builder(CePingActivity.this)
                            .setContentTitle("链接码："+ConfigurationUtils.Link_key)
                            .setContentText("请在云手机端进行匹配连接后继续测试")
                            .setContentIntent(pendingIntent)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .build();
                    manager.notify(1,notification);
                }

                if(Looper.myLooper()==null){
                    Looper.prepare();
                }
                Toast.makeText(CePingActivity.this,"link_key="+ConfigurationUtils.Link_key+",请在云手机端进行匹配连接",Toast.LENGTH_LONG).show();
                Looper.loop();


            }
        });

        return ;
    }*/

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

}
