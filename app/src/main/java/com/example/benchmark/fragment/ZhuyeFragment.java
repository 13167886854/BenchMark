package com.example.benchmark.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.benchmark.R;

import java.util.Objects;

public class ZhuyeFragment extends Fragment {
    private FragmentManager fragmentManager;
    private RadioGroup main_select_plat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.zhuye_fragment, container, false);
        fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.zhuye_fram, new PhoneFragment());
        fragmentTransaction.commit();

        main_select_plat = view.findViewById(R.id.main_select_plat);
        main_select_plat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.cloud_phone: {
                        changeFragment(new PhoneFragment(), true);
                        break;
                    }
                    case R.id.cloud_game: {
                        changeFragment(new GameFragment(), true);
                        break;
                    }
                }
            }
        });
        return view;
    }

    public void changeFragment(Fragment fragment, boolean isFisrt) {
        fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();

        // 开启事务
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.zhuye_fram, fragment);
        if (!isFisrt) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
