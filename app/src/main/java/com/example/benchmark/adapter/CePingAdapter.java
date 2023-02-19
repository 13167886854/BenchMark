package com.example.benchmark.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.benchmark.data.CepingData;
import com.example.benchmark.R;
import com.example.benchmark.utils.CacheConst;

import java.util.List;

//个题recycleview配置数据源
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
        final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
        CepingData cepingData = data.get(position);
        recyclerViewHolder.ceping_image.setImageResource(cepingData.getCepingImage());
        recyclerViewHolder.ceping_item.setText(cepingData.getCepingItem());
        recyclerViewHolder.ceping_text.setText(cepingData.getCepingText());
        if (!CacheConst.KEY_CPU_INFO.equals(cepingData.getCepingItem())
                && !CacheConst.KEY_GPU_INFO.equals(cepingData.getCepingItem())
                && !CacheConst.KEY_RAM_INFO.equals(cepingData.getCepingItem())
                && !CacheConst.KEY_ROM_INFO.equals(cepingData.getCepingItem()))
            recyclerViewHolder.ceping_grade.setText(String.valueOf(cepingData.getGrade()));

        recyclerViewHolder.itemView.setOnClickListener(v -> {
            int p = recyclerViewHolder.getAdapterPosition();
            onClickListener.onClick(data.get(p));
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public ImageView ceping_image;
        public TextView ceping_item;
        public TextView ceping_grade;
        public TextView ceping_text;
        public RelativeLayout relativeLayout;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            ceping_image = itemView.findViewById(R.id.ceping_image);
            ceping_item = itemView.findViewById(R.id.ceping_item);
            ceping_grade = itemView.findViewById(R.id.ceping_grade);
            ceping_text = itemView.findViewById(R.id.ceping_text);
            relativeLayout = itemView.findViewById(R.id.ceping_lay);
        }
    }
}
