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
     * @return
     * @date 2023/3/9 19:45
     */
    public JuTiData() {
    }

    /**
     * JuTiData
     *
     * @param item      description
     * @param itemGrade description
     * @date 2023/3/9 19:45
     */
    public JuTiData(String item, String itemGrade) {
        jutiItem = item;
        juTiItemGrade = itemGrade;
    }

    /**
     * getJutiItem
     *
     * @return java.lang.String
     * @description: getJutiItem
     * @date 2023/3/5 16:47
     */
    public String getJutiItem() {
        return jutiItem;
    }

    /**
     * setJutiItem
     *
     * @description: setJutiItem
     * @param: * @param item 具体字符串
     * @date 2023/3/5 16:52
     */
    public void setJutiItem(String item) {
        jutiItem = item;
    }

    /**
     * getJuTiItemGrade
     *
     * @return java.lang.String
     * @description: getJuTiItemGrade
     * @param:
     * @date 2023/3/5 16:53
     */
    public String getJuTiItemGrade() {
        return juTiItemGrade;
    }

    /**
     * setJuTiItemGrade
     *
     * @description: setJuTiItemGrade
     * @param: * @param itemGrade description
     * @date 2023/3/5 16:53
     */
    public void setJuTiItemGrade(String itemGrade) {
        juTiItemGrade = itemGrade;
    }
}
