package com.example.benchmark.Data;

import java.util.List;

public class ShuoMingData {
    private Integer infoImage;
    private String itemInfo;
    private List<LiuChang> list;

    public ShuoMingData(Integer infoImage, String itemInfo, List<LiuChang> list) {
        this.infoImage = infoImage;
        this.itemInfo = itemInfo;
        this.list = list;
    }

    public ShuoMingData() {
    }

    public String getItemInfo() {
        return itemInfo;
    }

    public void setItemInfo(String itemInfo) {
        this.itemInfo = itemInfo;
    }

    public Integer getInfoImage() {
        return infoImage;
    }

    public void setInfoImage(Integer infoImage) {
        this.infoImage = infoImage;
    }

    public List<LiuChang> getList() {
        return list;
    }

    public void setList(List<LiuChang> list) {
        this.list = list;
    }
}
