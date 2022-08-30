package com.example.benchmark.Data;

import java.io.Serializable;

public class CepingData implements Serializable {
    //测评的分数
    private int grade;
//    //测评模块得图片
    private int CepingImage;
    //测评的模块
    private String CepingItem;
    //测评得说明信息
    private String CepingText;

    public CepingData(int cepingImage, String cepingItem, String cepingText) {
        CepingImage = cepingImage;
        CepingItem = cepingItem;
        CepingText = cepingText;
    }


    public CepingData(int grade, int cepingImage, String cepingItem, String cepingText) {
        this.grade = grade;
        CepingImage = cepingImage;
        CepingItem = cepingItem;
        CepingText = cepingText;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getCepingImage() {
        return CepingImage;
    }

    public void setCepingImage(int cepingImage) {
        CepingImage = cepingImage;
    }

    public String getCepingItem() {
        return CepingItem;
    }

    public void setCepingItem(String cepingItem) {
        CepingItem = cepingItem;
    }

    public String getCepingText() {
        return CepingText;
    }

    public void setCepingText(String cepingText) {
        CepingText = cepingText;
    }

    public CepingData() {
    }

}
