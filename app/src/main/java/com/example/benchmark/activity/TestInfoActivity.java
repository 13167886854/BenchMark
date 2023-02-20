package com.example.benchmark.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.benchmark.data.Admin;
import com.example.benchmark.R;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.OkHttpUtils;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import okhttp3.Call;

public class TestInfoActivity extends AppCompatActivity {

    private String type = "";
    private TextView title;
    private String okHttpPara = "";

    private JSONArray rom;
    private JSONArray ram;
    private JSONArray cpu;
    private JSONArray gpu;
    private JSONArray fluency;
    private JSONArray stability;
    private JSONArray audioVideo;
    private JSONArray touch;



    TextView testInfo;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            String text = "";
            Object object;
            Map map;
            switch (type){
                case "info_fluency":
                    if(fluency.size()==0){
                        text = " 暂无测试数据。。。";
                        break;
                    }
                    for(int i=0;i<fluency.size();i++){
                        object = fluency.get(i);
                        if (object instanceof Map) {
                            map = (Map) object;
                            text += "测试平台： "+(map.get("platformName")==null?"null":map.get("platformName").toString())+"\n";
                            text += "    平均帧率: "+(map.get("averageFps")==null?"null":map.get("averageFps").toString())+"\n";
                            text += "    低帧率: "+(map.get("lowFrameRate")==null?"null":map.get("lowFrameRate").toString())+"\n";
                            text += "    抖动方差: "+ (map.get("frameShakeRate")==null?"null":map.get("frameShakeRate").toString())+"\n";
                            text += "    jank次数: "+(map.get("jankCount")==null?"null":map.get("jankCount").toString())+"\n";
                            text += "    帧间隔: "+(map.get("frameInterval")==null?"null":map.get("frameInterval").toString())+"\n";
                            text += "    卡顿占比时长: "+(map.get("stutterRate")==null?"null":map.get("stutterRate").toString())+"\n";
                            text += "    评分: "+(map.get("fluencyScore")==null?"null":map.get("fluencyScore").toString())+"\n";
                            text += "测试时间: "+(map.get("time")==null?"null":map.get("time").toString())+"\n\n\n";
                        }
                    }
                    break;
                case "info_stability":
                    if(stability.size()==0){
                        text = " 暂无测试数据。。。";
                        break;
                    }
                    for(int i=0;i<stability.size();i++){
                        object = stability.get(i);
                        if (object instanceof Map) {
                            map = (Map) object;
                            text += "测试平台： " + (map.get("platformName") == null ? "null" : map.get("platformName").toString()) + "\n";
                            text += "    启动成功率: " + (map.get("startSuccessRate") == null ? "null" : map.get("startSuccessRate").toString()) + "\n";
                            text += "    平均启动时间: " + (map.get("averageStartTime") == null ? "null" : map.get("averageStartTime").toString()) + "\n";
                            text += "    平均退出时间: " + (map.get("averageQuitTime") == null ? "null" : map.get("averageQuitTime").toString()) + "\n";
                            text += "    评分: " + (map.get("stabilityScore") == null ? "null" : map.get("stabilityScore").toString()) + "\n";
                            text += "测试时间: " + (map.get("time") == null ? "null" : map.get("time").toString()) + "\n\n\n";
                        }
                    }
                    break;
                case "info_touch":
                    if(touch.size()==0){
                        text = " 暂无测试数据。。。";
                        break;
                    }
                    for(int i=0;i<touch.size();i++){
                        object = touch.get(i);
                        if (object instanceof Map) {
                            map = (Map) object;
                            text += "测试平台： " + (map.get("platformName") == null ? "null" : map.get("platformName").toString()) + "\n";
                            text += "    正确率: " + (map.get("touchAccuracy") == null ? "null" : map.get("touchAccuracy").toString()) + "\n";
                            text += "    点击时延: " + (map.get("touchTimeDelay") == null ? "null" : map.get("touchTimeDelay").toString()) + "\n";
                            text += "    评分: " + (map.get("touchScore") == null ? "null" : map.get("touchScore").toString()) + "\n";
                            text += "测试时间: " + (map.get("time") == null ? "null" : map.get("time").toString()) + "\n\n\n";
                        }
                    }
                    break;
                case "info_audio_video":
                    if(audioVideo.size()==0){
                        text = " 暂无测试数据。。。";
                        break;
                    }
                    for(int i=0;i<audioVideo.size();i++){
                        object = audioVideo.get(i);
                        if (object instanceof Map) {
                            map = (Map) object;
                            text += "测试平台： " + (map.get("platformName") == null ? "null" : map.get("platformName").toString()) + "\n";
                            text += "    分辨率: " + (map.get("resolution") == null ? "null" : map.get("resolution").toString()) + "\n";
                            text += "    音画同步差: " + (map.get("maxDiffValue") == null ? "null" : map.get("maxDiffValue").toString()) + "\n";
                            text += "    PESQ: " + (map.get("pesq") == null ? "null" : map.get("pesq").toString()) + "\n";
                            text += "    SSIM: " + (map.get("ssim") == null ? "null" : map.get("ssim").toString()) + "\n";
                            text += "    PSNR: " + (map.get("psnr") == null ? "null" : map.get("psnr").toString()) + "\n";
                            text += "    评分: " + (map.get("qualityScore") == null ? "null" : map.get("qualityScore").toString()) + "\n";
                            text += "测试时间: " + (map.get("time") == null ? "null" : map.get("time").toString()) + "\n\n\n";
                        }
                    }
                    break;
                case "info_hardware":
                    if(rom.size()==0){
                        text = " 暂无测试数据。。。";
                        break;
                    }
                    for(int i=0;i<rom.size();i++){

                        object = rom.get(i);
                        if (object instanceof Map) {
                            map = (Map) object;
                            text += "测试平台： " + (map.get("platformName") == null ? "null" : map.get("platformName").toString()) + "\n";
                            text += "ROM\n";
                            text += "    可用内存: " + (map.get("availableRom") == null ? "null" : map.get("availableRom").toString()) + "\n";
                            text += "    总内存: " + (map.get("totalRom") == null ? "null" : map.get("totalRom").toString()) + "\n";
                        }

                        object = ram.get(i);
                        if (object instanceof Map) {
                            map = (Map) object;
                            text += "RAM\n";
                            text += "    可用内存: " + (map.get("availableRam") == null ? "null" : map.get("availableRam").toString()) + "\n";
                            text += "    总内存: " + (map.get("totalRam") == null ? "null" : map.get("totalRam").toString()) + "\n";
                        }


                        object = cpu.get(i);
                        if (object instanceof Map) {
                            map = (Map) object;
                            text += "CPU\n";
                            text += "    核数: " + (map.get("cores") == null ? "null" : map.get("cores").toString()) + "\n";
                            object = gpu.get(i);
                            map = (Map) object;
                            text += "GPU\n";
                            text += "    gpuVendor: " + (map.get("gpuRender") == null ? "null" : map.get("gpuRender").toString()) + "\n";
                            text += "    gpuVersion: " + (map.get("gpuVersion") == null ? "null" : map.get("gpuVersion").toString()) + "\n";
                            text += "    gpuVendor: " + (map.get("gpuVendor") == null ? "null" : map.get("gpuVendor").toString()) + "\n";
                            text += "测试时间: " + (map.get("time") == null ? "null" : map.get("time").toString()) + "\n\n\n";
                        }
                    }
                    break;

            }
            testInfo.setText(text);
            super.handleMessage(msg);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_info);

        title = findViewById(R.id.title);
        testInfo = findViewById(R.id.test_info);
        testInfo.setMovementMethod(ScrollingMovementMethod.getInstance());
        Intent intent = getIntent();
        type = intent.getStringExtra("type");

        Log.e("TWT", "onCreate: "+type );
        initTitle(type);

        queryForData(Admin.adminName,okHttpPara);



    }

    private void initTitle(String type){
        if(type.equals("")){
            return;
        }
        Drawable drawable;
        switch (type){
            case "info_fluency":
                title.setText("流畅性");
                drawable = getResources().getDrawable(R.drawable.blue_liuchang);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                title.setCompoundDrawablesRelative(drawable,null,null,null);
                okHttpPara = "Fluency";
                break;
            case "info_stability":
                title.setText("稳定性");
                drawable = getResources().getDrawable(R.drawable.blue_wending);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                title.setCompoundDrawablesRelative(drawable,null,null,null);
                okHttpPara = "Stability";
                break;
            case "info_touch":
                title.setText("触控测试");
                drawable = getResources().getDrawable(R.drawable.blue_chukong);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                title.setCompoundDrawablesRelative(drawable,null,null,null);
                okHttpPara = "Touch";
                break;
            case "info_audio_video":
                title.setText("音画质量");
                drawable = getResources().getDrawable(R.drawable.blue_yinhua);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                title.setCompoundDrawablesRelative(drawable,null,null,null);
                okHttpPara = "AudioVideo";
                break;
            case "info_hardware":
                title.setText("硬件信息");
                drawable = getResources().getDrawable(R.drawable.blue_cpu);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                title.setCompoundDrawablesRelative(drawable,null,null,null);
                okHttpPara = "Config";
                break;
        }
    }

    private void queryForData(String username,String type){
        OkHttpUtils.builder().url(CacheConst.GLOBAL_IP + "/data/"+username+"/"+type)
                .get()
                .async(new OkHttpUtils.ICallBack() {
                    @Override
                    public void onSuccessful(Call call, String data) {
                        Log.e("TWT", "data: " + data);
                        JSONObject jsonObject = JSON.parseObject(data);
                        rom = jsonObject.getJSONArray("ROM");
                        ram = jsonObject.getJSONArray("RAM");
                        cpu = jsonObject.getJSONArray("CPU");
                        gpu = jsonObject.getJSONArray("GPU");
                        fluency =  jsonObject.getJSONArray("Fluency");
                        stability =  jsonObject.getJSONArray("Stability");
                        audioVideo =  jsonObject.getJSONArray("AudioVideo");
                        touch =  jsonObject.getJSONArray("Touch");
                        handler.sendEmptyMessage(0);
                    }

                    @Override
                    public void onFailure(Call call, String errorMsg) {
                        Log.e("TWT", "onFailure: cpu---" + errorMsg);
                        Toast.makeText(TestInfoActivity.this,"网络连接异常",Toast.LENGTH_SHORT).show();

                    }
                });
    }
}