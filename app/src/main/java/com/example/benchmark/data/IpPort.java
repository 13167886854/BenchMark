/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.data;

/**
 * IpPort
 *
 * @version 1.0
 * @since 2023/3/7 15:12
 */
public class IpPort {
    private static final IpPort ipPort = new IpPort();

    private IpPort() {
    }

    public static IpPort getInstance() {
        return ipPort;
    }

    /**
     * ip地址
     */
    private String ip = "0.0.0.0";

    /**
     * 端口号
     */
    private String port = "0000";

    /**
     * getIp
     *
     * @return java.lang.String
     * @date 2023/3/13 16:13
     */
    public String getIp() {
        return ip;
    }

    /**
     * setIp
     *
     * @param ip description
     * @return void
     * @date 2023/3/13 16:13
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * getPort
     *
     * @return java.lang.String
     * @date 2023/3/13 16:13
     */
    public String getPort() {
        return port;
    }

    /**
     * setPort
     *
     * @param port description
     * @return void
     * @date 2023/3/13 16:13
     */
    public void setPort(String port) {
        this.port = port;
    }
}
