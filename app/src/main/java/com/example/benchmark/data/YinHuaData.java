/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.data;

/**
 * YinHuaData
 *
 * @version 1.0
 * @since 2023/3/7 15:13
 */
public class YinHuaData {
    private static final YinHuaData YIN_HUA_DATA = new YinHuaData();

    /**
     * 平台类型
     */
    private String platformType;

    /**
     * 音频得分
     */
    private String pesq;

    /**
     * 视频得分
     */
    private String ssim;

    /**
     * psnr
     */
    private String psnr;

    /**
     * resolution
     */
    private String resolution;

    private boolean isTestOver = false;

    private YinHuaData() {

    }

    /**
     * getInstance
     *
     * @return com.example.benchmark.data.YinHuaData
     * @date 2023/3/14 14:51
     */
    public static YinHuaData getInstance() {
        return YIN_HUA_DATA;
    }

    /**
     * getPlatformType
     *
     * @return java.lang.String
     * @date 2023/3/13 16:32
     */
    public String getPlatformType() {
        return platformType;
    }

    /**
     * setPlatformType
     *
     * @param platformType description
     * @return void
     * @date 2023/3/13 16:32
     */
    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    /**
     * getPesq
     *
     * @return java.lang.String
     * @date 2023/3/13 16:32
     */
    public String getPesq() {
        return pesq;
    }

    /**
     * setPesq
     *
     * @param pesq description
     * @return void
     * @date 2023/3/13 16:32
     */
    public void setPesq(String pesq) {
        this.pesq = pesq;
    }

    /**
     * getSsim
     *
     * @return java.lang.String
     * @date 2023/3/13 16:32
     */
    public String getSsim() {
        return ssim;
    }

    /**
     * setSsim
     *
     * @param ssim description
     * @return void
     * @date 2023/3/13 16:32
     */
    public void setSsim(String ssim) {
        this.ssim = ssim;
    }

    /**
     * getPsnr
     *
     * @return java.lang.String
     * @date 2023/3/13 16:32
     */
    public String getPsnr() {
        return psnr;
    }

    /**
     * setPsnr
     *
     * @param psnr description
     * @return void
     * @date 2023/3/13 16:32
     */
    public void setPsnr(String psnr) {
        this.psnr = psnr;
    }

    /**
     * getResolution
     *
     * @return java.lang.String
     * @date 2023/3/13 16:32
     */
    public String getResolution() {
        return resolution;
    }

    /**
     * setResolution
     *
     * @param resolution description
     * @return void
     * @date 2023/3/13 16:32
     */
    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public boolean isTestOver() {
        return isTestOver;
    }

    public void setTestOver(boolean testOver) {
        isTestOver = testOver;
    }
}
