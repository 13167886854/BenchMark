/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.initbenchmarkdata;

import com.example.benchmark.data.ShuoMingData;
import com.example.benchmark.R;

import java.util.ArrayList;
import java.util.List;

/**
 * InitShuoming
 *
 * @version 1.0
 * @since 2023/3/7 15:04
 */
public class InitShuoming {
    private List<ShuoMingData> list;
    private InitShuomingItem initLiuchang;

    /**
     * InitShuoming
     *
     * @date 2023/3/7 17:33
     */
    public InitShuoming() {
        list = new ArrayList<>();
        initLiuchang = new InitShuomingItem();
        initLiuchang.initLiuchang();
        ShuoMingData data = new ShuoMingData(R.drawable.blue_liuchang, "流畅性",
                initLiuchang.getList());
        list.add(data);
    }

    /**
     * getList
     *
     * @return java.util.List<com.example.benchmark.data.ShuoMingData>
     * @date 2023/3/8 08:43
     */
    public List<ShuoMingData> getList() {
        return list;
    }
}
