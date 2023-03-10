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
import com.example.benchmark.data.LiuChang;

import java.util.List;

/**
 * ItemAdapter
 *
 * @version 1.0
 * @since 2023/3/7 15:08
 */
public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<LiuChang> data;

    private Context context;

    public ItemAdapter(Context context, List<LiuChang> data) {
        this.context = context;
        this.data = data;
    }

    /**
     * onCreateViewHolder
     *
     * @param parent description
     * @param viewType description
     * @return androidx.recyclerview.widget.RecyclerView.ViewHolder
     * @date 2023/3/8 15:15
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    /**
     * onBindViewHolder
     *
     * @param holder description
     * @param position description
     * @date 2023/3/8 15:15
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RecyclerViewHolder) {
            RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
            LiuChang liuChang = data.get(position);
            recyclerViewHolder.itemOfItem.setText(liuChang.getItemOfItem());
            recyclerViewHolder.itemTestMethod.setText(liuChang.getCeshifangfa());
            recyclerViewHolder.itemEvaluationIndex.setText(liuChang.getPingpanbiaozhun());
        }
    }

    /**
     * getItemCount
     *
     * @return int
     * @date 2023/3/8 15:15
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        /** itemOfItem */
        private TextView itemOfItem;

        /** itemTestMethod */
        private TextView itemTestMethod;

        /** itemEvaluationIndex */
        private TextView itemEvaluationIndex;

        /**
         * RecyclerViewHolder
         *
         * @param itemView description
         * @date 2023/3/8 15:16
         */
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            itemOfItem = itemView.findViewById(R.id.item_item);
            itemTestMethod = itemView.findViewById(R.id.item_ceshifangfa);
            itemEvaluationIndex = itemView.findViewById(R.id.item_pingpanbiaozhun);
        }
    }
}
