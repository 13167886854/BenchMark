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

    /**
     * ShuoMingData
     *
     * @param infoImage description
     * @param itemInfo  description
     * @param list      description
     * @return
     * @date 2023/3/9 19:44
     */
    public ShuoMingData(Integer infoImage, String itemInfo, List<LiuChang> list) {
        this.infoImage = infoImage;
        this.itemInfo = itemInfo;
        this.list = list;
    }

    /**
     * ShuoMingData
     *
     * @date 2023/3/8 16:04
     */
    public ShuoMingData() {
    }

    /**
     * getItemInfo
     *
     * @return java.lang.String
     * @date 2023/3/8 16:04
     */
    public String getItemInfo() {
        return itemInfo;
    }

    /**
     * setItemInfo
     *
     * @param itemInfo description
     * @date 2023/3/8 16:05
     */
    public void setItemInfo(String itemInfo) {
        this.itemInfo = itemInfo;
    }

    /**
     * getInfoImage
     *
     * @return java.lang.Integer
     * @date 2023/3/8 16:05
     */
    public Integer getInfoImage() {
        return infoImage;
    }

    /**
     * setInfoImage
     *
     * @param infoImage description
     * @date 2023/3/8 16:05
     */
    public void setInfoImage(Integer infoImage) {
        this.infoImage = infoImage;
    }

    /**
     * getList
     *
     * @return java.util.List<com.example.benchmark.data.LiuChang>
     * @date 2023/3/8 16:05
     */
    public List<LiuChang> getList() {
        return list;
    }

    /**
     * setList
     *
     * @param list description
     * @date 2023/3/8 16:05
     */
    public void setList(List<LiuChang> list) {
        this.list = list;
    }
}
