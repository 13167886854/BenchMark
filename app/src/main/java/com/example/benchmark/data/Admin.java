/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.data;

/**
 * Admin.java
 *
 * @Author benchmark
 * @Version 1.0
 * @since 2023/3/10 11:21
 */
public class Admin {
    private static final Admin ADMIN = new Admin();

    /**
     * 全局变量记录管理员  Global variable record manager
     */
    private String adminName;

    /**
     * 全局变量记录用户名  Global variables record user names
     */
    private String username;

    /**
     * 全局变量记录登录密码  Global variables record the login password
     */
    private String password;

    /**
     * 全局变量记录测试平台  Global variable record test platform
     */
    private String platformName;

    /**
     * 全局变量记录测试时间  Global variables record test times
     */
    private String testTime;

    /**
     * 登录状态  Logging status
     */
    private String status = "Failure";

    private Admin() {
    }

    /**
     * getInstance
     *
     * @return com.example.benchmark.data.Admin
     * @date 2023/3/14 14:47
     */
    public static Admin getInstance() {
        return ADMIN;
    }

    /**
     * getAdminName
     *
     * @return java.lang.String
     * @date 2023/3/13 15:59
     */
    public String getAdminName() {
        return adminName;
    }

    /**
     * setAdminName
     *
     * @param adminName description
     * @return void
     * @date 2023/3/13 15:59
     */
    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    /**
     * getUsername
     *
     * @return java.lang.String
     * @date 2023/3/13 15:59
     */
    public String getUsername() {
        return username;
    }

    /**
     * setUsername
     *
     * @param username description
     * @return void
     * @date 2023/3/13 15:59
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * getPassword
     *
     * @return java.lang.String
     * @date 2023/3/13 15:59
     */
    public String getPassword() {
        return password;
    }

    /**
     * setPassword
     *
     * @param password description
     * @return void
     * @date 2023/3/13 15:59
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * getPlatformName
     *
     * @return java.lang.String
     * @date 2023/3/13 15:59
     */
    public String getPlatformName() {
        return platformName;
    }

    /**
     * setPlatformName
     *
     * @param platformName description
     * @return void
     * @date 2023/3/13 15:59
     */
    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    /**
     * getTestTime
     *
     * @return java.lang.String
     * @date 2023/3/13 16:00
     */
    public String getTestTime() {
        return testTime;
    }

    /**
     * setTestTime
     *
     * @param testTime description
     * @return void
     * @date 2023/3/13 16:00
     */
    public void setTestTime(String testTime) {
        this.testTime = testTime;
    }

    /**
     * getStatus
     *
     * @return java.lang.String
     * @date 2023/3/13 16:00
     */
    public String getStatus() {
        return status;
    }

    /**
     * setStatus
     *
     * @param status description
     * @return void
     * @date 2023/3/13 16:00
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
