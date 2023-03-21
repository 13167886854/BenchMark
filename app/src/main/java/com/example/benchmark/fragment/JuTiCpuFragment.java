/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.fragment;

import android.os.Bundle;
import android.util.Log;
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
 * JuTiCpuFragment
 *
 * @version 1.0
 * @since 2023/3/7 15:14
 */
public class JuTiCpuFragment extends Fragment {
    /**
     * onCreateView
     *
     * @param inflater           description
     * @param container          description
     * @param savedInstanceState description
     * @return android.view.View
     * @date 2023/3/9 19:45
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                                @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.juti_cpu_fragment, container, false);
        TextView cpuModel = view.findViewById(R.id.juti_cpu_model);
        TextView cpuCore = view.findViewById(R.id.juti_cpu_core);
        TextView cpuMaxRate = view.findViewById(R.id.juti_cpu_maxrate);
        cpuCore.setText(MobileCloud.getInstance().getCpuCoreNum());
        Log.d("info", MobileCloud.getInstance().getSpec());
        Log.d("info", MobileCloud.getInstance().getName());
        return view;
    }
}
