/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.data;

/**
 * MobileCloud
 *
 * @version 1.0
 * @since 2023/3/7 15:12
 */
public class MobileCloud {
    private static final MobileCloud mobileCloud = new MobileCloud();

    private MobileCloud() {
    }

    public static MobileCloud getInstance() {
        return mobileCloud;
    }

    /**
     * name
     */
    private String name = "";

    /**
     * storage
     */
    private String storage = "";

    /**
     * spec
     */
    private String spec = "";

    /**
     * cpuCoreNum
     */
    private String cpuCoreNum = "";

    /**
     * getName
     *
     * @return java.lang.String
     * @date 2023/3/13 16:20
     */
    public String getName() {
        return name;
    }

    /**
     * setName
     *
     * @param name description
     * @return void
     * @date 2023/3/13 16:20
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * getStorage
     *
     * @return java.lang.String
     * @date 2023/3/13 16:20
     */
    public String getStorage() {
        return storage;
    }

    /**
     * setStorage
     *
     * @param storage description
     * @return void
     * @date 2023/3/13 16:20
     */
    public void setStorage(String storage) {
        this.storage = storage;
    }

    /**
     * getSpec
     *
     * @return java.lang.String
     * @date 2023/3/13 16:20
     */
    public String getSpec() {
        return spec;
    }

    /**
     * setSpec
     *
     * @param spec description
     * @return void
     * @date 2023/3/13 16:20
     */
    public void setSpec(String spec) {
        this.spec = spec;
    }

    /**
     * getCpuCoreNum
     *
     * @return java.lang.String
     * @date 2023/3/13 16:20
     */
    public String getCpuCoreNum() {
        return cpuCoreNum;
    }

    /**
     * setCpuCoreNum
     *
     * @param cpuCoreNum description
     * @return void
     * @date 2023/3/13 16:25
     */
    public void setCpuCoreNum(String cpuCoreNum) {
        this.cpuCoreNum = cpuCoreNum;
    }
}
