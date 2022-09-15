package com.example.benchmark.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.benchmark.Activity.MainActivity;
import com.example.benchmark.Activity.TestInfoActivity;
import com.example.benchmark.R;

public class TishiFragment extends Fragment {

    private Button info_fluency;
    private Button info_stability;
    private Button info_touch;
    private Button info_audio_video;
    private Button info_hardware;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tishi_fragment, container, false);
        info_fluency = view.findViewById(R.id.info_fluency);
        info_stability = view.findViewById(R.id.info_stability);
        info_touch = view.findViewById(R.id.info_touch);
        info_audio_video = view.findViewById(R.id.info_audio_video);
        info_hardware = view.findViewById(R.id.info_hardware);

        info_fluency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TestInfoActivity.class);
                intent.putExtra("type","info_fluency");
                getContext().startActivity(intent);
            }
        });
        info_stability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TestInfoActivity.class);
                intent.putExtra("type","info_stability");
                getContext().startActivity(intent);
            }
        });
        info_touch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TestInfoActivity.class);
                intent.putExtra("type","info_touch");
                getContext().startActivity(intent);
            }
        });
        info_audio_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TestInfoActivity.class);
                intent.putExtra("type","info_audio_video");
                getContext().startActivity(intent);
            }
        });
        info_hardware.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TestInfoActivity.class);
                intent.putExtra("type","info_hardware");
                getContext().startActivity(intent);
            }
        });

        return view;



    }
}
