/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.InitbenchMarkData;

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

    public InitShuoming() {
        list = new ArrayList<>();
        initLiuchang = new InitShuomingItem();
        initLiuchang.InitLiuchang();
        ShuoMingData data = new ShuoMingData(R.drawable.blue_liuchang, "流畅性", initLiuchang.getList());
        list.add(data);
    }

    public List<ShuoMingData> getList() {
        return list;
    }
}
