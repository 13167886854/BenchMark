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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.benchmark.R;
import com.example.benchmark.data.JuTiData;

import java.util.List;

/**
 * JutiAdapter
 *
 * @version 1.0
 * @since 2023/3/7 15:08
 */
public class JutiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<JuTiData> data;

    private Context context;

    /**
     * JutiAdapter
     *
     * @param context description
     * @param data    description
     * @return
     * @date 2023/3/9 19:47
     */
    public JutiAdapter(Context context, List<JuTiData> data) {
        this.context = context;
        this.data = data;
    }

    /**
     * onCreateViewHolder
     *
     * @param parent   description
     * @param viewType description
     * @return androidx.recyclerview.widget.RecyclerView.ViewHolder
     * @date 2023/3/9 19:47
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.juti_item, parent, false);
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
            final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
            JuTiData info = this.data.get(position);
            recyclerViewHolder.juTiItemZhiBiao.setText(info.getJutiItem());
            recyclerViewHolder.juTiItemGrade.setText(info.getJuTiItemGrade());
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
        private TextView juTiItemZhiBiao;
        private TextView juTiItemGrade;

        /**
         * RecyclerViewHolder
         *
         * @param itemView description
         * @date 2023/3/10 11:17
         */
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            juTiItemZhiBiao = itemView.findViewById(R.id.juti_item_zhibiao);
            juTiItemGrade = itemView.findViewById(R.id.juti_item_grade);
        }
    }
}
