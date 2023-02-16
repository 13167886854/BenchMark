package com.example.benchmark.InitbenchMarkData;

import com.example.benchmark.data.ShuoMingData;
import com.example.benchmark.R;

import java.util.ArrayList;
import java.util.List;

public class InitShuoming {
    private List<ShuoMingData> list;
    private InitShuomingItem initLiuchang;
    public InitShuoming(){
        list=new ArrayList<>();
        initLiuchang=new InitShuomingItem();
        initLiuchang.InitLiuchang();
        ShuoMingData data = new ShuoMingData(R.drawable.blue_liuchang, "流畅性",initLiuchang.getList());

        list.add(data);
    }
    public List<ShuoMingData> getList(){
        return list;
    }

}
