/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.benchmark.R;
import com.example.benchmark.data.LiuChang;
import com.example.benchmark.data.ShuoMingData;

import java.util.List;

/**
 * ShuoMingAdapter
 *
 * @version 1.0
 * @since 2023/3/7 15:09
 */
public class ShuoMingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<ShuoMingData> data;

    private Context context;

    /**
     * ShuoMingAdapter
     *
     * @param context description
     * @param data    description
     * @return
     * @date 2023/3/9 19:46
     */
    public ShuoMingAdapter(Context context, List<ShuoMingData> data) {
        this.context = context;
        this.data = data;
    }

    /**
     * onCreateViewHolder
     *
     * @param parent   description
     * @param viewType description
     * @return androidx.recyclerview.widget.RecyclerView.ViewHolder
     * @date 2023/3/9 19:46
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shuoming_item, parent, false);

        return new RecyclerViewHolder(view);
    }

    /**
     * onBindViewHolder
     *
     * @param holder   description
     * @param position description
     * @return void
     * @date 2023/3/9 19:47
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RecyclerViewHolder) {
            RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
            ShuoMingData smData = this.data.get(position);

            recyclerViewHolder.introductionImg.setImageResource(smData.getInfoImage());
            List<LiuChang> list = smData.getList();

            ItemAdapter adapter = new ItemAdapter(context, list);
            recyclerViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
            recyclerViewHolder.recyclerView.setAdapter(adapter);
        }
    }

    /**
     * getItemCount
     *
     * @return int
     * @date 2023/3/9 19:47
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public ImageView introductionImg;
        public RecyclerView recyclerView;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            introductionImg = itemView.findViewById(R.id.shuoming_img);
            recyclerView = itemView.findViewById(R.id.shuoming_item_rv);
        }
    }
}
