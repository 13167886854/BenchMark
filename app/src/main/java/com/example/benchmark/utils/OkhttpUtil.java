package com.example.benchmark.utils;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.benchmark.data.AudioVideoData;
import com.example.benchmark.data.BaseData;
import com.example.benchmark.data.SmoothData;
import com.example.benchmark.data.TouchData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkhttpUtil {

    public static void getBaseData(Context context){
        // 请求连接云手机app
        OkHttpClient client = new OkHttpClient();
        // post发生测试结果数据
        FormBody formBody = new FormBody.Builder()
                .add("link_key", ConfigurationUtils.linkKey)
                .build();
        final Request request = new Request.Builder()
                .url(ConfigurationUtils.url+"/getBaseDataByKey")
                .post(formBody)
                .build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException exception) {
                Looper.prepare();
                Log.e("TWT", exception.toString());
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                Log.d("TWT", "result: "+res);
                if(res.equals("null")){
                    Log.e("TAG", "onResponse: empty info");
                }else{
                    // 获取数据后显示到前端页面
                    try {
                        JSONObject jsonObject = new JSONObject(res);
                        BaseData.availRam = jsonObject.getString("availRam");
                        BaseData.totalRam = jsonObject.getString("totalRam");
                        BaseData.availStorage = jsonObject.getString("availStorage");
                        BaseData.totalStorage = jsonObject.getString("totalStorage");
                        BaseData.cpuName = jsonObject.getString("cpuName");
                        BaseData.cpuCores = jsonObject.getString("cpuCores");
                        BaseData.gpuVendor = jsonObject.getString("gpuVendor");
                        BaseData.gpuRenderer = jsonObject.getString("gpuRenderer");
                        BaseData.gpuVersion = jsonObject.getString("gpuVersion");
                        BaseData.date = jsonObject.getString("date");
                        BaseData.hasData = true;
                        Looper.prepare();
                        Toast.makeText(context,"获取基础硬件数据成功",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } catch (JSONException exception) {
                        Looper.prepare();
                        Toast.makeText(context,"未接受到数据",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        exception.printStackTrace();
                    }
                }
            }
        });
    }

    public static void getSmoothTestData(Context context){
        // 请求连接云手机app
        OkHttpClient client = new OkHttpClient();
        // post发生测试结果数据
        FormBody formBody = new FormBody.Builder()
                .add("link_key", ConfigurationUtils.linkKey)
                .build();
        final Request request = new Request.Builder()
                .url(ConfigurationUtils.url+"/getSmoothDataByKey")
                .post(formBody)
                .build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException exception) {
                Looper.prepare();
                Log.e("TWT", exception.toString());
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                Log.d("TWT", "result: "+res);
                if(res.equals("null")){
                    Log.e("TAG", "onResponse: empty info");
                }else{
                    // 获取数据后显示到前端页面
                    try {
                        JSONObject jsonObject = new JSONObject(res);
                        Log.d("TWT", "avergeFPS:"+jsonObject.getString("avergeFPS"));
                        SmoothData.avergeFPS = jsonObject.getString("avergeFPS");
                        SmoothData.frameShakingRate = jsonObject.getString("frameShakingRate");
                        SmoothData.lowFrameRate = jsonObject.getString("lowFrameRate");
                        SmoothData.frameInterval = jsonObject.getString("frameInterval");
                        SmoothData.jankCount = jsonObject.getString("jankCount");
                        SmoothData.stutterRate = jsonObject.getString("stutterRate");
                        SmoothData.date = jsonObject.getString("date");
                        SmoothData.haveData = true;
                        Looper.prepare();
                        Toast.makeText(context,"获取流畅性测试数据成功",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } catch (JSONException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });
    }


    public static void getTouchData(Context context){
        // 请求连接云手机app
        OkHttpClient client = new OkHttpClient();
        // post发生测试结果数据
        FormBody formBody = new FormBody.Builder()
                .add("link_key", ConfigurationUtils.linkKey)
                .build();
        final Request request = new Request.Builder()
                .url(ConfigurationUtils.url+"/getTouchDataByKey")
                .post(formBody)
                .build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException exception) {
                Looper.prepare();
                Log.e("TWT", exception.toString());
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                Log.d("TWT", "result: "+res);
                if(res.equals("null")){
                    Log.e("TAG", "onResponse: empty info");
                }else{
                    // 获取数据后显示到前端页面
                    try {
                        JSONObject jsonObject = new JSONObject(res);
                        TouchData.averageAccuracy = jsonObject.getString("averageAccuracy");
                        TouchData.responseTime = jsonObject.getString("responseTime");
                        TouchData.averageResponseTime = jsonObject.getString("averageResponseTime");
                        TouchData.date = jsonObject.getString("date");
                        TouchData.hasData = true;
                        Looper.prepare();
                        Toast.makeText(context,"获取触控测试数据成功",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } catch (JSONException exception) {
                        Looper.prepare();
                        Toast.makeText(context,"未接受到数据",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        exception.printStackTrace();
                    }
                }
            }
        });
    }

    public static void getAudioVideoData(Context context){
        // 请求连接云手机app
        OkHttpClient client = new OkHttpClient();
        // post发生测试结果数据
        FormBody formBody = new FormBody.Builder()
                .add("link_key", ConfigurationUtils.linkKey)
                .build();
        final Request request = new Request.Builder()
                .url(ConfigurationUtils.url+"/getAudioVideoDataByKey")
                .post(formBody)
                .build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException exception) {
                Looper.prepare();
                Log.e("TWT", exception.toString());
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                Log.d("TWT", "result: "+res);
                if(res.equals("null")){
                    Log.e("TAG", "onResponse: empty info");
                }else{
                    // 获取数据后显示到前端页面
                    try {
                        JSONObject jsonObject = new JSONObject(res);
                        AudioVideoData.resolution = jsonObject.getString("resolution");
                        AudioVideoData.maxdifferencevalue = jsonObject.getString("maxdifferencevalue");
                        AudioVideoData.date = jsonObject.getString("date");
                        AudioVideoData.haveData = true;
                        Looper.prepare();
                        Toast.makeText(context,"获取音画测试数据成功",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } catch (JSONException exception) {
                        Looper.prepare();
                        Toast.makeText(context,"未接受到数据",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        exception.printStackTrace();
                    }
                }
            }
        });
    }
}
