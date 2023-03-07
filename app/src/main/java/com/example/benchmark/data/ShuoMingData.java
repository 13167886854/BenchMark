/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.data;

import java.util.List;

/**
 * ShuoMingData
 *
 * @version 1.0
 * @since 2023/3/7 15:13
 */
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
