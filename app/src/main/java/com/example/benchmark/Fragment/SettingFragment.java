package com.example.benchmark.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.benchmark.Activity.SettingsActivity;
import com.example.benchmark.Activity.ShuoMingActivity;
import com.example.benchmark.R;

public class SettingFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment, container, false);
        RelativeLayout relativeLayout = view.findViewById(R.id.set_shiyongshuom);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ShuoMingActivity.class));
            }
        });
        RelativeLayout paraSet = view.findViewById(R.id.set_paraSet);
        paraSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });
//        RelativeLayout history = view.findViewById(R.id.history);
//        paraSet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), SettingsActivity.class));
//            }
//        });
        return view;
    }
}
