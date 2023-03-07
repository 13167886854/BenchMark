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

    public JuTiData() {
    }

    public JuTiData(String item, String itemGrade) {
        jutiItem = item;
        juTiItemGrade = itemGrade;
    }

    public String getJutiItem() {
        return jutiItem;
    }

    public void setJutiItem(String item) {
        jutiItem = item;
    }

    public String getJuTiItemGrade() {
        return juTiItemGrade;
    }

    public void setJuTiItemGrade(String itemGrade) {
        juTiItemGrade = itemGrade;
    }


}
