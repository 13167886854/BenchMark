package com.example.benchmark.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.benchmark.Activity.CePingActivity;
import com.example.benchmark.DiaLog.PopDiaLog;
import com.example.benchmark.R;
import com.example.benchmark.utils.AccessUtils;
import com.example.benchmark.utils.CacheConst;
import com.example.benchmark.utils.CacheUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, CheckBox.OnCheckedChangeListener {
    private Button red_liuchang,red_wending,red_chukong,red_yinhua;

    private CheckBox red_liuchang_cheak;
    private CheckBox red_wending_cheak;
    private CheckBox red_chukong_cheak;
    private CheckBox red_yinhua_cheak;

    private CheckBox red_cpu_cheak;
    private CheckBox red_gpu_cheak;
    private CheckBox red_ram_cheak;
    private CheckBox red_rom_cheak;

    private CheckBox game_select_all;

    private Button red_cpu,red_gpu,red_ram,red_rom;

    private Button red_start_test;

    private RadioGroup select_game;

    private static List<Boolean> list;

    private AccessUtils accessUtils;
    private PopDiaLog popDiaLog;


    private static final HashMap<String,String> cheak_game_map;
    static {
        cheak_game_map=new HashMap<>();
        list = new ArrayList<>();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_fragment, container, false);
        initview(view);
        red_liuchang.setOnClickListener(this::onClick);
        red_wending.setOnClickListener(this::onClick);
        red_chukong.setOnClickListener(this::onClick);
        red_yinhua.setOnClickListener(this::onClick);

        red_cpu.setOnClickListener(this::onClick);
        red_gpu.setOnClickListener(this::onClick);
        red_ram.setOnClickListener(this::onClick);
        red_rom.setOnClickListener(this::onClick);





        game_select_all.setOnCheckedChangeListener(this::onCheckedChanged);
        select_game.setOnCheckedChangeListener(this::onCheckedChanged);

            red_start_test.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!(accessUtils.isIgnoringBatteryOptimizations() && accessUtils.isAccessibilityServiceOpen())){
                        popDiaLog.show();
                    }else
                    if (cheak_game_map.get("cheaked_game") == null) {
                        Toast.makeText(getActivity(), "请选择需要测评的云游戏平台", Toast.LENGTH_LONG).show();
                    } else {
                        CacheUtil.put(CacheConst.KEY_STABILITY_IS_MONITORED, false);
                        CacheUtil.put(CacheConst.KEY_PERFORMANCE_IS_MONITORED, false);
                        Intent intent = new Intent(getActivity(), CePingActivity.class);
                        intent.putExtra(CacheConst.KEY_PLATFORM_KIND, CacheConst.PLATFORM_KIND_CLOUD_GAME);
                        intent.putExtra(CacheConst.KEY_FLUENCY_INFO, red_liuchang_cheak.isChecked());
                        intent.putExtra(CacheConst.KEY_STABILITY_INFO, red_wending_cheak.isChecked());
                        intent.putExtra(CacheConst.KEY_TOUCH_INFO, red_chukong_cheak.isChecked());
                        intent.putExtra(CacheConst.KEY_SOUND_FRAME_INFO, red_yinhua_cheak.isChecked());
                        intent.putExtra(CacheConst.KEY_CPU_INFO, red_cpu_cheak.isChecked());
                        intent.putExtra(CacheConst.KEY_GPU_INFO, red_gpu_cheak.isChecked());
                        intent.putExtra(CacheConst.KEY_ROM_INFO, red_ram_cheak.isChecked());
                        intent.putExtra(CacheConst.KEY_RAM_INFO, red_rom_cheak.isChecked());
                        intent.putExtra(CacheConst.KEY_PLATFORM_NAME, cheak_game_map.get(CacheConst.KEY_PLATFORM_NAME));
//                        intent.putExtra("red_liuchang_cheak", red_liuchang_cheak.isChecked());
//                        intent.putExtra("red_wending_cheak", red_wending_cheak.isChecked());
//                        intent.putExtra("red_chukong_cheak", red_chukong_cheak.isChecked());
//                        intent.putExtra("red_yinhua_cheak", red_yinhua_cheak.isChecked());
//                        intent.putExtra("red_cpu_cheak", red_cpu_cheak.isChecked());
//                        intent.putExtra("red_gpu_cheak", red_gpu_cheak.isChecked());
//                        intent.putExtra("red_ram_cheak", red_ram_cheak.isChecked());
//                        intent.putExtra("red_rom_cheak", red_rom_cheak.isChecked());
//                        intent.putExtra("cheaked_plat",cheak_game_map.get("cheaked_game"));
                        startActivity(intent);
                    }
                }
            });



        return view;
    }



    private void initview(View view){
        red_liuchang=view.findViewById(R.id.red_liuchangxing);
        red_wending=view.findViewById(R.id.red_wendinxing);
        red_chukong=view.findViewById(R.id.red_chukong);
        red_yinhua = view.findViewById(R.id.red_yinhua);


        red_cpu = view.findViewById(R.id.red_cpu);
        red_gpu=view.findViewById(R.id.red_gpu);
        red_ram = view.findViewById(R.id.red_ram);
        red_rom = view.findViewById(R.id.red_rom);

        red_liuchang_cheak = view.findViewById(R.id.red_liuchang_cheak);
        red_wending_cheak=view.findViewById(R.id.red_wending_cheak);
        red_chukong_cheak = view.findViewById(R.id.red_chukong_cheak);
        red_yinhua_cheak = view.findViewById(R.id.red_yinhua_cheak);

        red_cpu_cheak = view.findViewById(R.id.red_cpu_cheak);
        red_gpu_cheak = view.findViewById(R.id.red_gpu_cheak);
        red_ram_cheak = view.findViewById(R.id.red_ram_cheak);
        red_rom_cheak = view.findViewById(R.id.red_rom_cheak);


        red_start_test=view.findViewById(R.id.red_start_test);

        select_game=view.findViewById(R.id.select_game);
        game_select_all=view.findViewById(R.id.game_select_all);

        accessUtils=new AccessUtils(getContext());
        popDiaLog = new PopDiaLog(getActivity());

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.red_liuchangxing:{
                boolean checked = red_liuchang_cheak.isChecked();
                if (checked){

                    red_liuchang_cheak.setChecked(false);
                    red_liuchang_cheak.setVisibility(View.INVISIBLE);
                    game_select_all.setChecked(false);
                }else {
                    red_liuchang_cheak.setChecked(true);
                    red_liuchang_cheak.setVisibility(View.VISIBLE);
                }
                break;

            }
            case R.id.red_wendinxing:{

                boolean checked = red_wending_cheak.isChecked();
                if (checked){

                    red_wending_cheak.setChecked(false);
                    red_wending_cheak.setVisibility(View.INVISIBLE);
                    game_select_all.setChecked(false);

                }else {
                    red_wending_cheak.setVisibility(View.VISIBLE);
                    red_wending_cheak.setChecked(true);
                }
                break;
            }
            case R.id.red_chukong:{

                boolean checked = red_chukong_cheak.isChecked();
                if (checked){

                    red_chukong_cheak.setChecked(false);
                    red_chukong_cheak.setVisibility(View.INVISIBLE);
                    game_select_all.setChecked(false);
                }else{
                    red_chukong_cheak.setVisibility(View.VISIBLE);
                    red_chukong_cheak.setChecked(true);
                }
                break;
            }
            case R.id.red_yinhua:{

                boolean checked = red_yinhua_cheak.isChecked();
                if (checked){
                    red_yinhua_cheak.setVisibility(View.INVISIBLE);
                    red_yinhua_cheak.setChecked(false);
                    game_select_all.setChecked(false);
                }else{
                    red_yinhua_cheak.setVisibility(View.VISIBLE);
                    red_yinhua_cheak.setChecked(true);
                }
                break;
            }
            case R.id.red_cpu:{

                boolean checked = red_cpu_cheak.isChecked();

                if (checked){
                    red_cpu_cheak.setVisibility(View.INVISIBLE);
                    red_cpu_cheak.setChecked(false);
                    game_select_all.setChecked(false);
                }else{
                    red_cpu_cheak.setVisibility(View.VISIBLE);
                    red_cpu_cheak.setChecked(true);
                }
                break;
            }
            case R.id.red_gpu:{

                boolean checked = red_gpu_cheak.isChecked();

                if (checked){
                    red_gpu_cheak.setVisibility(View.INVISIBLE);
                    red_gpu_cheak.setChecked(false);
                    game_select_all.setChecked(false);
                }else{
                    red_gpu_cheak.setVisibility(View.VISIBLE);
                    red_gpu_cheak.setChecked(true);
                }
                break;
            }
            case R.id.red_ram:{

                boolean checked = red_ram_cheak.isChecked();

                if (checked){
                    red_ram_cheak.setVisibility(View.INVISIBLE);
                    red_ram_cheak.setChecked(false);
                    game_select_all.setChecked(false);
                }else{
                    red_ram_cheak.setVisibility(View.VISIBLE);
                    red_ram_cheak.setChecked(true);
                }
                break;
            }
            case R.id.red_rom:{

                boolean checked = red_rom_cheak.isChecked();

                if (checked){
                    red_rom_cheak.setVisibility(View.INVISIBLE);
                    red_rom_cheak.setChecked(false);
                    game_select_all.setChecked(false);
                }else{
                    red_rom_cheak.setVisibility(View.VISIBLE);
                    red_rom_cheak.setChecked(true);
                }
                break;
            }


        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.tengxun_game:{
                cheak_game_map.put("cheaked_game","腾讯先锋");
                break;
            }
            case R.id.migu_game:{
                cheak_game_map.put("cheaked_game","咪咕快游");
                break;
            }
            case R.id.wangyi_game:{
                cheak_game_map.put("cheaked_game","网易云游戏");
                break;
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked){
                red_liuchang_cheak.setChecked(true);
                red_liuchang_cheak.setVisibility(View.VISIBLE);


                red_wending_cheak.setVisibility(View.VISIBLE);
                red_wending_cheak.setChecked(true);

                red_chukong_cheak.setVisibility(View.VISIBLE);
                red_chukong_cheak.setChecked(true);

                red_yinhua_cheak.setVisibility(View.VISIBLE);
                red_yinhua_cheak.setChecked(true);

                red_cpu_cheak.setVisibility(View.VISIBLE);
                red_cpu_cheak.setChecked(true);


                red_gpu_cheak.setVisibility(View.VISIBLE);
                red_gpu_cheak.setChecked(true);

                red_ram_cheak.setVisibility(View.VISIBLE);
                red_ram_cheak.setChecked(true);

                red_rom_cheak.setVisibility(View.VISIBLE);
                red_rom_cheak.setChecked(true);
            }
            else{
                red_liuchang_cheak.setChecked(false);
                red_liuchang_cheak.setVisibility(View.INVISIBLE);

                red_wending_cheak.setChecked(false);
                red_wending_cheak.setVisibility(View.INVISIBLE);


                red_chukong_cheak.setChecked(false);
                red_chukong_cheak.setVisibility(View.INVISIBLE);


                red_yinhua_cheak.setVisibility(View.INVISIBLE);
                red_yinhua_cheak.setChecked(false);

                red_cpu_cheak.setVisibility(View.INVISIBLE);
                red_cpu_cheak.setChecked(false);

                red_gpu_cheak.setVisibility(View.INVISIBLE);
                red_gpu_cheak.setChecked(false);

                red_ram_cheak.setVisibility(View.INVISIBLE);
                red_ram_cheak.setChecked(false);

                red_rom_cheak.setVisibility(View.INVISIBLE);
                red_rom_cheak.setChecked(false);

        }
    }
}
