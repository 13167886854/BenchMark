package com.example.benchmark.Data;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class CepingData implements Serializable {
    //测评的分数
    private Integer grade;
//    //测评模块得图片
    private Integer CepingImage;
    //测评的模块
    private String CepingItem;
    //测评得说明信息
    private String CepingText;
    //是否选中
    private Boolean isCheaked;

    public CepingData(Integer grade, Integer cepingImage, String cepingItem, String cepingText, Boolean isCheaked) {
        this.grade = grade;
        CepingImage = cepingImage;
        CepingItem = cepingItem;
        CepingText = cepingText;
        this.isCheaked = isCheaked;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public Integer getCepingImage() {
        return CepingImage;
    }

    public void setCepingImage(Integer cepingImage) {
        CepingImage = cepingImage;
    }

    public CepingData(Integer cepingImage, String cepingItem, String cepingText, Boolean isCheaked) {
        CepingImage = cepingImage;
        CepingItem = cepingItem;
        CepingText = cepingText;
        this.isCheaked = isCheaked;
    }

    public CepingData(String cepingItem, String cepingText, Boolean isCheaked) {
        CepingItem = cepingItem;
        CepingText = cepingText;
        this.isCheaked = isCheaked;
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

    public Boolean getCheaked() {
        return isCheaked;
    }

    public void setCheaked(Boolean cheaked) {
        isCheaked = cheaked;
    }

    public CepingData() {
    }

}
