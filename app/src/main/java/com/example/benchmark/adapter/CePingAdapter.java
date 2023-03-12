/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.benchmark.R;
import com.example.benchmark.data.CepingData;
import com.example.benchmark.utils.CacheConst;

import java.util.List;

/**
 * CePingAdapter
 *
 * @version 1.0
 * @since 2023/3/7 15:08
 */
public class CePingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CepingData> data;

    private OnBenchmarkResultItemClickListener onClickListener;

    /**
     * CePingAdapter
     *
     * @param data            description
     * @param onClickListener description
     * @return
     * @date 2023/3/10 11:13
     */
    public CePingAdapter(List<CepingData> data,
                            OnBenchmarkResultItemClickListener onClickListener) {
        this.data = data;
        this.onClickListener = onClickListener;
    }

    /**
     * onCreateViewHolder
     *
     * @param parent   description
     * @param viewType description
     * @return androidx.recyclerview.widget.RecyclerView.ViewHolder
     * @date 2023/3/10 11:13
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ceping_item,
                parent, false);
        return new RecyclerViewHolder(view);
    }

    /**
     * onBindViewHolder
     *
     * @param holder   description
     * @param position description
     * @return void
     * @date 2023/3/10 11:13
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RecyclerViewHolder) {
            final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
            CepingData cepingData = data.get(position);
            recyclerViewHolder.cePingImage.setImageResource(cepingData.getCepingImage());
            recyclerViewHolder.cePingItem.setText(cepingData.getCepingItem());
            recyclerViewHolder.cePingText.setText(cepingData.getCepingText());
            if (!CacheConst.KEY_CPU_INFO.equals(cepingData.getCepingItem())
                    && !CacheConst.KEY_GPU_INFO.equals(cepingData.getCepingItem())
                    && !CacheConst.KEY_RAM_INFO.equals(cepingData.getCepingItem())
                    && !CacheConst.KEY_ROM_INFO.equals(cepingData.getCepingItem())) {
                recyclerViewHolder.cePingGrade.setText(String.valueOf(cepingData.getGrade()));
            }

            recyclerViewHolder.itemView.setOnClickListener(v -> {
                int pos = recyclerViewHolder.getAdapterPosition();
                onClickListener.onClick(data.get(pos));
            });
        }
    }

    /**
     * getItemCount
     *
     * @return int
     * @date 2023/3/10 11:13
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private ImageView cePingImage;
        private TextView cePingItem;
        private TextView cePingGrade;
        private TextView cePingText;
        private RelativeLayout relativeLayout;

        /**
         * RecyclerViewHolder
         *
         * @param itemView description
         * @date 2023/3/10 11:17
         */
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            cePingImage = itemView.findViewById(R.id.ceping_image);
            cePingItem = itemView.findViewById(R.id.ceping_item);
            cePingGrade = itemView.findViewById(R.id.ceping_grade);
            cePingText = itemView.findViewById(R.id.ceping_text);
            relativeLayout = itemView.findViewById(R.id.ceping_lay);
        }
    }
}
