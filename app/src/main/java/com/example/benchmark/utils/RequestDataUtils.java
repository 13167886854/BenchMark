package com.example.benchmark.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.chinamobile.bcop.api.sdk.constant.HttpMethod;
import com.example.benchmark.data.MobileCloud;
import com.example.benchmark.data.RedFingure;

import java.io.IOException;
import java.util.HashMap;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RequestDataUtils {
    private static final String TAG = "RequestDataUtils";
    //主线程实例化Handler
    private static Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            // 主线程收到消息后就会执行这个函数

            // msg.what 可以区分从哪个线程发送过来的消息
            if (msg.what == 0) {
                // 将子线程传递过来的移动云手机配置信息（JSON）解析出来
                String num = JSONUtil.parseObj(msg.obj).getStr("Num");
                //Log.d(TAG, "handleMessage: Num==>" + num);
                String productID = JSONUtil.parseObj(msg.obj).getStr("ProductID");
                String name = JSONUtil.parseObj(msg.obj).getStr("Name");
                String storage = JSONUtil.parseObj(msg.obj).getStr("Storage");
                String runningMemory = JSONUtil.parseObj(msg.obj).getStr("RunningMemory");
                String spec = JSONUtil.parseObj(msg.obj).getStr("Spec");
                String cpuCoreNum = JSONUtil.parseObj(msg.obj).getStr("CPUCoreNum");

                // 得到的信息给全局变量
                MobileCloud.num = num;
                MobileCloud.productID = productID;
                MobileCloud.name = name;
                MobileCloud.storage = storage;
                MobileCloud.runningMemory = runningMemory;
                MobileCloud.spec = spec;
                MobileCloud.cpuCoreNum = cpuCoreNum;


                System.out.println(storage);
                System.out.println(runningMemory);
                System.out.println(spec);

            }
        }
    };


    /**
     * 获取移动云手机参数
     */
    public static void getMobileCloudInfo() {
        final String accessKey = "fc1affafe3024f5fbc082f8aff475157";
        final String secretKey = "80db1a8f09cc420fa7b7ed3f8f3b0270";
        // 测试时使用
        MobileCloud.accessKey = accessKey;
        MobileCloud.secretKey = secretKey;
        MobileCloudRequestUtils utils = new MobileCloudRequestUtils();
        String URL = utils.generateSignature(MobileCloud.PATH, HttpMethod.POST, new HashMap<>(), MobileCloud.accessKey, MobileCloud.secretKey);

//        System.out.println(URL);
        Object obj = new Object();
        final String jsonStr = JSONUtil.toJsonStr(obj);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String body = HttpRequest.post(URL).header("Content-Type", "application/json; charset=utf-8")
                        .body(jsonStr)
                        .timeout(60000)
                        .execute()
                        .body();
                JSONObject jsonObject = JSONUtil.parseObj(body);
                String body1 = jsonObject.getStr("body");
                JSONObject bodyJson = JSONUtil.parseObj(body1);
                String listStr = bodyJson.getStr("List");
                JSONArray listJSONArray = JSONUtil.parseArray(listStr);
                String specsStr = JSONUtil.parseObj(listJSONArray.get(0)).getStr("Specs");
                JSONObject mobileInfoJson = JSONUtil.parseObj(JSONUtil.parseArray(specsStr).get(0));

                // 向主线程的handler传消息
                // 1. 构建消息
                Message message = new Message();
                message.what = 0;
                message.obj = mobileInfoJson;

                // 2. 发送消息
                mHandler.sendMessage(message);
            }
        }).start();
    }


    //从数据库获取红手指云手的数据
    public static void  getRedFingureInfo(){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://114.116.243.31:8080/benchmark/get_cpuinfo?cloud_phone=redfigure")
                .build()
                ;
        try {
            Response response = okHttpClient.newCall(request).execute();
            ResponseBody body = response.body();
            JSONObject jsonObject = new JSONObject(body.toString());
            int code = (int) jsonObject.get("code");
            JSONObject data = jsonObject.getJSONObject("data");
            String cpu_core = (String) data.get("cpu_core");
            String cpu_maxrate= (String) data.get("cpu_maxrate");
            String cpu_zhilingji= (String) data.get("cpu_zhilingji");
            RedFingure.cpucore=cpu_core;
            RedFingure.cpumaxrate=cpu_maxrate;
            RedFingure.cpuname=cpu_zhilingji;
        } catch (IOException e) {
            Log.e(TAG, "getRedFingureInfo: ", e);
        }
    }


}
