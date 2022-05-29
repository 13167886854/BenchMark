package com.example.benchmark.InitbenchMarkData;

import com.example.benchmark.Data.LiuChang;

import java.util.ArrayList;
import java.util.List;

public class InitShuomingItem {
    private List<LiuChang> list;
    public List<LiuChang> InitLiuchang(){
        list = new ArrayList<>();
        list.add(new LiuChang("平均帧率(fps)","测试方法:录屏总帧数/总时间(s)","评判标准:"));
        list.add(new LiuChang("抖动方差(方差)","测试方法:(统计每秒帧数，并以每秒帧数作为数据求方差)","评判标准:"));
        list.add(new LiuChang("低帧率(%)","测试方法:帧绘制时间超过电影帧耗时(1000ms/24)判定为低帧,低帧数/总帧数即为低帧率","评判标准"));
        list.add(new LiuChang("jank(卡顿次数/10min)","测试方法:","评判标准:"));
        list.add(new LiuChang("卡顿占比时长(%)","测试方法:jank帧发生时间(s)/总时间(s)","评判标准:"));
        list.add(new LiuChang("帧间隔(ms)","测试方法:总书记兼(s)/(总帧数-1)","评判标准:"));
        return  list;
    }

    public List<LiuChang> getList(){
        return list;
    }

}
