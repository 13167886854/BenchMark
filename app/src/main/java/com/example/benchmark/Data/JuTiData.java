package com.example.benchmark.Data;

public class JuTiData {
    private String JutiItem;
    private String JuTiItemGrade;

    public String getJutiItem() {
        return JutiItem;
    }

    public void setJutiItem(String jutiItem) {
        JutiItem = jutiItem;
    }

    public String getJuTiItemGrade() {
        return JuTiItemGrade;
    }

    public void setJuTiItemGrade(String juTiItemGrade) {
        JuTiItemGrade = juTiItemGrade;
    }

    public JuTiData() {
    }

    public JuTiData(String jutiItem, String juTiItemGrade) {
        JutiItem = jutiItem;
        JuTiItemGrade = juTiItemGrade;
    }
}
