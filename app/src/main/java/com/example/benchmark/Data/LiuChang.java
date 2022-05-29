package com.example.benchmark.Data;

import java.util.List;

/**
 * 流畅类
 */
public class LiuChang {
    private String item_item;
    private String ceshifangfa;
    private String pingpanbiaozhun;

    public LiuChang() {
    }

    public LiuChang(String item_item, String ceshifangfa, String pingpanbiaozhun) {
        this.item_item = item_item;
        this.ceshifangfa = ceshifangfa;
        this.pingpanbiaozhun = pingpanbiaozhun;
    }

    public String getItem_item() {
        return item_item;
    }

    public void setItem_item(String item_item) {
        this.item_item = item_item;
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
