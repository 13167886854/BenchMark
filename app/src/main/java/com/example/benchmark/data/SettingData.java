/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.data;

/**
 * ShuoMingData
 *
 * @version 1.0
 * @since 2023/4/15 12:59
 */
public class SettingData {
    private static final SettingData SETTING_DATA = new SettingData();

    private int stabilityTestNum = 5;
    private String serverIp1 = "175";
    private String serverIp2 = "38";
    private String serverIp3 = "1";
    private String serverIp4 = "81";
    private String serverPort = "8080";

    private SettingData() {}

    /**
     * getInstance
     *
     * @return com.example.benchmark.data.SettingData
     * @date 2023/4/15 14:47
     */
    public static SettingData getInstance() {
        return SETTING_DATA;
    }

    /**
     * getStabilityTestNum
     *
     * @return int
     * @date 2023/4/15 13:02
     */
    public int getStabilityTestNum() {
        return stabilityTestNum;
    }

    /**
     * setStabilityTestNum
     *
     * @param stabilityTestNum description
     * @return void
     * @date 2023/4/15 13:02
     */
    public void setStabilityTestNum(int stabilityTestNum) {
        this.stabilityTestNum = stabilityTestNum;
    }

    /**
     * getServerIp
     *
     * @return java.lang.String
     * @date 2023/4/15 13:02
     */
    public String getServerIp() {
        return serverIp1 + "." + serverIp2 + "." + serverIp3 + "." + serverIp4;
    }

    /**
     * getServerIp1
     *
     * @return java.lang.String
     * @date 2023/4/16 13:55
     */
    public String getServerIp1() {
        return serverIp1;
    }

    /**
     * setServerIp1
     *
     * @param serverIp1 description
     * @return void
     * @date 2023/4/16 13:55
     */
    public void setServerIp1(String serverIp1) {
        this.serverIp1 = serverIp1;
    }

    /**
     * getServerIp2
     *
     * @return java.lang.String
     * @date 2023/4/16 13:55
     */
    public String getServerIp2() {
        return serverIp2;
    }

    /**
     * setServerIp2
     *
     * @param serverIp2 description
     * @return void
     * @date 2023/4/16 13:55
     */
    public void setServerIp2(String serverIp2) {
        this.serverIp2 = serverIp2;
    }

    /**
     * getServerIp3
     *
     * @return java.lang.String
     * @date 2023/4/16 13:56
     */
    public String getServerIp3() {
        return serverIp3;
    }

    /**
     * setServerIp3
     *
     * @param serverIp3 description
     * @return void
     * @date 2023/4/16 13:56
     */
    public void setServerIp3(String serverIp3) {
        this.serverIp3 = serverIp3;
    }

    /**
     * getServerIp4
     *
     * @return java.lang.String
     * @date 2023/4/16 13:56
     */
    public String getServerIp4() {
        return serverIp4;
    }

    /**
     * setServerIp4
     *
     * @param serverIp4 description
     * @return void
     * @date 2023/4/16 13:56
     */
    public void setServerIp4(String serverIp4) {
        this.serverIp4 = serverIp4;
    }

    /**
     * getServerPort
     *
     * @return java.lang.String
     * @date 2023/4/15 13:02
     */
    public String getServerPort() {
        return serverPort;
    }

    /**
     * setServerPort
     *
     * @param serverPort description
     * @return void
     * @date 2023/4/15 13:03
     */
    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * getServerAddress
     *
     * @return java.lang.String
     * @date 2023/4/15 13:27
     */
    public String getServerAddress() {
        return "http://" + this.getServerIp() + ":" + this.serverPort;
    }
}
