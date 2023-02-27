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
