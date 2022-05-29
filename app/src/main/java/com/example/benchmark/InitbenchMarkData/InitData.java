package com.example.benchmark.InitbenchMarkData;

import com.example.benchmark.Data.CepingData;
import com.example.benchmark.R;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class InitData implements Serializable {
    private Map<String, CepingData> map;
    public InitData(){
        map=new HashMap<>();
        CepingData blue_liuchang = new CepingData(98,R.drawable.blue_liuchang,"流畅性", "通过多个不同分辨率的视频，测试出平均帧率，帧抖动率，Jank等参数评估流畅性性能", true);
        CepingData blue_wending = new CepingData(97,R.drawable.blue_wending,"稳定性", "通过后台持续启动、关闭云手机，分析启动成功率、启动时长、退出时长，测评稳定性性能", true);
        CepingData blue_chukong = new CepingData(87,R.drawable.blue_chukong,"触控体验", "通过后台程序对屏幕进行点击灵敏度和触屏响应时延进行测试，评估触控体验", true);
        CepingData blue_yinhua = new CepingData(79,R.drawable.blue_yinhua,"音画质量", "通过显示分辨率、音画同步差、画面质量PSNR、音频质量PESQ等指标对音画质量做出客观评价", true);

        CepingData red_liuchang = new CepingData(56,R.drawable.blue_liuchang,"流畅性", "通过多个不同分辨率的视频，测试出平均帧率，帧抖动率，Jank等参数评估流畅性性能", true);
        CepingData red_wending = new CepingData(80,R.drawable.red_wending,"稳定性", "通过后台持续启动、关闭云手机，分析启动成功率、启动时长、退出时长，测评稳定性性能", true);
        CepingData red_chukong = new CepingData(90,R.drawable.red_chukong,"触控体验", "通过后台程序对屏幕进行点击灵敏度和触屏响应时延进行测试，评估触控体验", true);
        CepingData red_yinhua = new CepingData(78,R.drawable.red_yinhua,"音画质量", "通过显示分辨率、音画同步差、画面质量PSNR、音频质量PESQ等指标对音画质量做出客观评价", true);


        CepingData blue_cpu = new CepingData(76,R.drawable.blue_cpu,"cpu", "通过后台对云手机数据信息的读取，测试出CPU型号及性能，得出测评结果", true);
        CepingData blue_gpu = new CepingData(89,R.drawable.blue_gpu,"gpu", "通过后台对云手机数据信息的读取，测试出GPU型号及性能，得出测评结果", true);
        CepingData blue_ram = new CepingData(89,R.drawable.blue_ram,"内存", "通过读写速度，随机/顺序读写等方式，评价设备的RAM性能", true);
        CepingData blue_rom = new CepingData(90,R.drawable.blue_rom,"硬盘", "通过后台对云手机数据信息的读取，测试出硬盘大小及性能，得出测评结果", true);


        CepingData red_cpu = new CepingData(78,R.drawable.red_cpu,"cpu", "通过后台对云手机数据信息的读取，测试出CPU型号及性能，得出测评结果", true);
        CepingData red_gpu = new CepingData(90,R.drawable.red_gpu,"gpu", "通过后台对云手机数据信息的读取，测试出GPU型号及性能，得出测评结果", true);
        CepingData red_ram = new CepingData(87,R.drawable.red_ram,"内存", "通过读写速度，随机/顺序读写等方式，评价设备的RAM性能", true);
        CepingData red_rom = new CepingData(87,R.drawable.red_rom,"硬盘", "通过后台对云手机数据信息的读取，测试出硬盘大小及性能，得出测评结果", true);

        map.put("blue_liuchang",blue_liuchang);
        map.put("blue_wending",blue_wending);
        map.put("blue_chukong",blue_chukong);
        map.put("blue_yinhua",blue_yinhua);


        map.put("red_liuchang",red_liuchang);
        map.put("red_wending",red_wending);
        map.put("red_chukong",red_chukong);
        map.put("red_yinhua",red_yinhua);

        map.put("red_cpu",red_cpu);
        map.put("red_gpu",red_gpu);
        map.put("red_ram",red_ram);
        map.put("red_rom",red_rom);

        map.put("blue_cpu",blue_cpu);
        map.put("blue_gpu",blue_gpu);
        map.put("blue_ram",blue_ram);
        map.put("blue_rom",blue_rom);
    }
    public InitData(Map<String,CepingData> map){
        this.map=map;
    }

    public Map<String,CepingData> getMap(){
        return map;
    }

}
