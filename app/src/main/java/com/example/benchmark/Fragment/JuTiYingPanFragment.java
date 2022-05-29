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

public class JuTiYingPanFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.juti_yingpan_fragment, container, false);

        TextView juti_yingpan_num = view.findViewById(R.id.juti_yingpan_num);
        juti_yingpan_num.setText(MobileCloud.storage);

        return  view;
    }

}
