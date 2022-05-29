package com.example.benchmark.Data;

import java.util.List;

public class ShuoMingData {
    private Integer shuoming_image;
    private String item_shuoming;
    private List<LiuChang> list;

    public String getItem_shuoming() {
        return item_shuoming;
    }

    public void setItem_shuoming(String item_shuoming) {
        this.item_shuoming = item_shuoming;
    }

    public ShuoMingData(Integer shuoming_image, String item_shuoming, List<LiuChang> list) {
        this.shuoming_image = shuoming_image;
        this.item_shuoming =item_shuoming;
        this.list = list;
    }

    public ShuoMingData() {
    }

    public Integer getShuoming_image() {
        return shuoming_image;
    }

    public void setShuoming_image(Integer shuoming_image) {
        this.shuoming_image = shuoming_image;
    }

    public List<LiuChang> getList() {
        return list;
    }

    public void setList(List<LiuChang> list) {
        this.list = list;
    }
}
