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

// 个体recycleview配置数据源
public class CePingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CepingData> data;

    private OnBenchmarkResultItemClickListener onClickListener;

    public CePingAdapter(List<CepingData> data, OnBenchmarkResultItemClickListener onClickListener) {
        this.data = data;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ceping_item, parent, false);
        return new RecyclerViewHolder(view);
    }

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

    @Override
    public int getItemCount() {
        return data.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public ImageView cePingImage;
        public TextView cePingItem;
        public TextView cePingGrade;
        public TextView cePingText;
        public RelativeLayout relativeLayout;

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
