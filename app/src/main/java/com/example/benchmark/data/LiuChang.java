/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.data;

/**
 * LiuChang
 *
 * @version 1.0
 * @since 2023/3/7 15:12
 */
public class LiuChang {
    private String itemOfItem;
    private String ceshifangfa;
    private String pingpanbiaozhun;

    public LiuChang() {
    }

    public LiuChang(String itemOfItem, String ceshifangfa, String pingpanbiaozhun) {
        this.itemOfItem = itemOfItem;
        this.ceshifangfa = ceshifangfa;
        this.pingpanbiaozhun = pingpanbiaozhun;
    }

    public String getItemOfItem() {
        return itemOfItem;
    }

    public void setItemOfItem(String itemOfItem) {
        this.itemOfItem = itemOfItem;
    }

    public String getCeshifangfa() {
        return ceshifangfa;
    }

    public void setCeshifangfa(String ceshifangfa) {
        this.ceshifangfa = ceshifangfa;
    }

    public String getPingpanbiaozhun() {
        return pingpanbiaozhun;
    }

    public void setPingpanbiaozhun(String pingpanbiaozhun) {
        this.pingpanbiaozhun = pingpanbiaozhun;
    }
}
