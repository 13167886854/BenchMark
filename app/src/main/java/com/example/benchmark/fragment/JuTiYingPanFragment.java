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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.benchmark.data.MobileCloud;
import com.example.benchmark.R;

/**
 * JuTiYingPanFragment
 *
 * @version 1.0
 * @since 2023/3/7 15:16
 */
public class JuTiYingPanFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.juti_yingpan_fragment, container, false);
        TextView juTiYingPanNum = view.findViewById(R.id.juti_yingpan_num);
        juTiYingPanNum.setText(MobileCloud.storage);
        return view;
    }
}
