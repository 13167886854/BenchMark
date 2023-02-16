package com.example.benchmark.InitbenchMarkData;

import com.example.benchmark.data.JuTiData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InitJuTiData {
    private List<JuTiData> data;
    private HashMap<String ,List<JuTiData>> map;
    public InitJuTiData(){
        map=new HashMap<>();
        data=new ArrayList<>();
    }

    private List<JuTiData> LiuChangData(){
        List<JuTiData> list = new ArrayList<>();
        list.add(new JuTiData("平均帧率(fps)","29.7"));
        list.add(new JuTiData("抖动帧率(方差)","1.06"));
        list.add(new JuTiData("低帧率(%)","6.63%"));
        list.add(new JuTiData("帧间隔(ms)","22.93"));
        list.add(new JuTiData("jank(卡顿次数/10min)","0.18"));
        list.add(new JuTiData("卡顿市场占比(%)","0.76"));
        return list;
    }
    private List<JuTiData> WenDingData(){
        List<JuTiData> list = new ArrayList<>();
        list.add(new JuTiData("启动成功率(%)","96%"));
        list.add(new JuTiData("启动时长(ms)","34ms"));
        list.add(new JuTiData("退出时长","21ms"));
        return list;
    }

}
