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

    /**
     * LiuChang
     *
     * @return
     * @throws null
     * @date 2023/3/8 10:59
     */
    public LiuChang() {
    }

    /**
     * LiuChang
     *
     * @param itemOfItem      description
     * @param ceshifangfa     description
     * @param pingpanbiaozhun description
     * @return
     * @throws null
     * @date 2023/3/8 10:59
     */
    public LiuChang(String itemOfItem, String ceshifangfa, String pingpanbiaozhun) {
        this.itemOfItem = itemOfItem;
        this.ceshifangfa = ceshifangfa;
        this.pingpanbiaozhun = pingpanbiaozhun;
    }

    /**
     * getItemOfItem
     *
     * @return java.lang.String
     * @throws null
     * @date 2023/3/8 10:59
     */
    public String getItemOfItem() {
        return itemOfItem;
    }

    /**
     * setItemOfItem
     *
     * @param itemOfItem description
     * @return void
     * @throws null
     * @date 2023/3/8 10:59
     */
    public void setItemOfItem(String itemOfItem) {
        this.itemOfItem = itemOfItem;
    }

    /**
     * getCeshifangfa
     *
     * @return java.lang.String
     * @throws null
     * @date 2023/3/8 10:59
     */
    public String getCeshifangfa() {
        return ceshifangfa;
    }

    /**
     * setCeshifangfa
     *
     * @param ceshifangfa description
     * @return void
     * @throws null
     * @date 2023/3/8 10:59
     */
    public void setCeshifangfa(String ceshifangfa) {
        this.ceshifangfa = ceshifangfa;
    }

    /**
     * getPingpanbiaozhun
     *
     * @return java.lang.String
     * @throws null
     * @date 2023/3/8 10:59
     */
    public String getPingpanbiaozhun() {
        return pingpanbiaozhun;
    }

    /**
     * setPingpanbiaozhun
     *
     * @param pingpanbiaozhun description
     * @return void
     * @throws null
     * @date 2023/3/8 11:00
     */
    public void setPingpanbiaozhun(String pingpanbiaozhun) {
        this.pingpanbiaozhun = pingpanbiaozhun;
    }
}
