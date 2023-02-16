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

public class JuTiCpuFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.juti_cpu_fragment, container, false);
        TextView cpuModel = view.findViewById(R.id.juti_cpu_model);
        TextView cpuCore = view.findViewById(R.id.juti_cpu_core);
        TextView cpuMaxRate = view.findViewById(R.id.juti_cpu_maxrate);
        cpuCore.setText(MobileCloud.cpuCoreNum);
        Log.d("info", MobileCloud.spec);
        Log.d("info", MobileCloud.name);
        return view;
    }
}
