/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.data;

/**
 * CepingData
 *
 * @version 1.0
 * @since 2023/3/7 15:11
 */
public class CepingData {
    // 测评的分数  Test score
    private int grade;

    // 测评模块得图片  Evaluation module to get the picture
    private int cepingImage;

    // 测评的模块  Evaluation module
    private String cepingItem;

    // 测评得说明信息  The evaluation results in explanatory information
    private String cepingText;

    /**
     * CepingData
     *
     * @return
     * @throws null
     * @date 2023/3/8 11:06
     */
    public CepingData() {
    }

    /**
     * CepingData
     *
     * @param cepingImage description
     * @param cepingItem  description
     * @param cepingText  description
     * @return
     * @throws null
     * @date 2023/3/8 11:05
     */
    public CepingData(int cepingImage, String cepingItem, String cepingText) {
        this.cepingImage = cepingImage;
        this.cepingItem = cepingItem;
        this.cepingText = cepingText;
    }

    /**
     * CepingData
     *
     * @param grade       description
     * @param cepingImage description
     * @param cepingItem  description
     * @param cepingText  description
     * @return
     * @throws null
     * @date 2023/3/8 11:05
     */
    public CepingData(int grade, int cepingImage, String cepingItem, String cepingText) {
        this(cepingImage,cepingItem,cepingText);
        this.grade = grade;
    }

    /**
     * getGrade
     *
     * @return int
     * @throws null
     * @date 2023/3/8 11:05
     */
    public int getGrade() {
        return grade;
    }

    /**
     * setGrade
     *
     * @param grade description
     * @return void
     * @throws null
     * @date 2023/3/8 11:05
     */
    public void setGrade(int grade) {
        this.grade = grade;
    }

    /**
     * getCepingImage
     *
     * @return int
     * @throws null
     * @date 2023/3/8 11:05
     */
    public int getCepingImage() {
        return cepingImage;
    }

    /**
     * setCepingImage
     *
     * @param cepingImage description
     * @return void
     * @throws null
     * @date 2023/3/8 11:05
     */
    public void setCepingImage(int cepingImage) {
        this.cepingImage = cepingImage;
    }

    /**
     * getCepingItem
     *
     * @return java.lang.String
     * @throws null
     * @date 2023/3/8 11:06
     */
    public String getCepingItem() {
        return cepingItem;
    }

    /**
     * setCepingItem
     *
     * @param cepingItem description
     * @return void
     * @throws null
     * @date 2023/3/8 11:06
     */
    public void setCepingItem(String cepingItem) {
        this.cepingItem = cepingItem;
    }

    /**
     * getCepingText
     *
     * @return java.lang.String
     * @throws null
     * @date 2023/3/8 11:06
     */
    public String getCepingText() {
        return cepingText;
    }

    /**
     * setCepingText
     *
     * @param cepingText description
     * @return void
     * @throws null
     * @date 2023/3/8 11:06
     */
    public void setCepingText(String cepingText) {
        this.cepingText = cepingText;
    }
}
