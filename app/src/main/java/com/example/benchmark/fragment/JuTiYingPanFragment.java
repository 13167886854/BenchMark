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
    private View mView;
    private TextView mJuTiYingPanNum;

    /**
     * onCreateView
     *
     * @param inflater           description
     * @param container          description
     * @param savedInstanceState description
     * @return android.view.View
     * @date 2023/3/9 19:41
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.juti_yingpan_fragment, container, false);
        mJuTiYingPanNum = mView.findViewById(R.id.juti_yingpan_num);
        mJuTiYingPanNum.setText(MobileCloud.getInstance().getStorage());
        return mView;
    }
}
