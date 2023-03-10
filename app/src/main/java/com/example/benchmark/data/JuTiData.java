/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.data;

/**
 * JuTiData
 *
 * @version 1.0
 * @since 2023/3/7 15:12
 */
public class JuTiData {
    private String jutiItem;
    private String juTiItemGrade;

    /**
     * JuTiData
     *
     * @date 2023/3/10 11:24
     */
    public JuTiData() {
    }

    /**
     * JuTiData
     *
     * @param item description
     * @param itemGrade description
     * @return 
     * @date 2023/3/10 11:24
     */
    public JuTiData(String item, String itemGrade) {
        jutiItem = item;
        juTiItemGrade = itemGrade;
    }

    /**
     * getJutiItem
     *
     * @return java.lang.String
     * @date 2023/3/10 11:24
     */
    public String getJutiItem() {
        return jutiItem;
    }

    /**
     * setJutiItem
     *
     * @param item description
     * @return void
     * @date 2023/3/10 11:23
     */
    public void setJutiItem(String item) {
        jutiItem = item;
    }

    /**
     * getJuTiItemGrade
     *
     * @return java.lang.String
     * @date 2023/3/10 11:23
     */
    public String getJuTiItemGrade() {
        return juTiItemGrade;
    }

    /**
     * setJuTiItemGrade
     *
     * @param itemGrade description
     * @return void
     * @date 2023/3/10 11:23
     */
    public void setJuTiItemGrade(String itemGrade) {
        juTiItemGrade = itemGrade;
    }
}
