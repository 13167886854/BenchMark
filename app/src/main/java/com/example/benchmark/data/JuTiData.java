package com.example.benchmark.data;

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
