/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.benchmark.R;

/**
 * JuTiNeiCunFragment
 *
 * @version 1.0
 * @since 2023/3/7 15:16
 */
public class JuTiNeiCunFragment extends Fragment {
    private View mView;

    /**
     * onCreateView
     *
     * @param inflater           inflater
     * @param container          container
     * @param savedInstanceState savedInstanceState
     * @return android.view.View
     * @throws null
     * @date 2023/3/8 10:15
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.juti_neicun_fragment, container, false);
        return mView;
    }
}
