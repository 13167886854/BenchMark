package com.example.benchmark.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.benchmark.Data.MobileCloud;
import com.example.benchmark.R;

public class JuTiCpuFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.juti_cpu_fragment, container, false);
        TextView juti_cpu_model = view.findViewById(R.id.juti_cpu_model);
        //TextView juti_cpu_name = view.findViewById(R.id.juti_cpu_name);
        TextView juti_cpu_core = view.findViewById(R.id.juti_cpu_core);
        TextView juti_cpu_maxrate = view.findViewById(R.id.juti_cpu_maxrate);
        juti_cpu_core.setText(MobileCloud.cpuCoreNum);
        System.out.println(MobileCloud.spec);
        System.out.println(MobileCloud.name);
        return  view;
    }

}
