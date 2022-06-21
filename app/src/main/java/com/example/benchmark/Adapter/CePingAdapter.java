package com.example.benchmark.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.benchmark.Activity.CePingActivity;
import com.example.benchmark.Activity.JutiZhibiaoActivity;
import com.example.benchmark.Data.CepingData;
import com.example.benchmark.R;


import java.util.List;

//个题recycleview配置数据源
public class CePingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<CepingData> data;

    private OnBenchmarkResultItemClickListener onClickListener;

    public CePingAdapter(Context context,List<CepingData> data, OnBenchmarkResultItemClickListener onClickListener){
        this.context=context;
        this.data=data;
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
        final RecyclerViewHolder recyclerViewHolder= (RecyclerViewHolder) holder;
        CepingData cepingData = data.get(position);
        if (!cepingData.getCheaked()){
            recyclerViewHolder.relativeLayout.setVisibility(View.GONE);
        }else {

            recyclerViewHolder.ceping_image.setImageResource(cepingData.getCepingImage());
            recyclerViewHolder.ceping_item.setText(cepingData.getCepingItem());
            recyclerViewHolder.ceping_text.setText(cepingData.getCepingText());
            recyclerViewHolder.ceping_grade.setText(String.valueOf(cepingData.getGrade()));
        }

        //点击事件
        CePingActivity cePingActivity = new CePingActivity();

        recyclerViewHolder.itemView.setOnClickListener(v -> {
            int p= recyclerViewHolder.getAdapterPosition();
            onClickListener.onClick(data.get(p));
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    class RecyclerViewHolder extends RecyclerView.ViewHolder{
        public ImageView ceping_image;
        public TextView ceping_item;
        public TextView ceping_grade;
        public TextView ceping_text;
        public RelativeLayout relativeLayout;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            ceping_image=itemView.findViewById(R.id.ceping_image);
            ceping_item=itemView.findViewById(R.id.ceping_item);
            ceping_grade=itemView.findViewById(R.id.ceping_grade);
            ceping_text=itemView.findViewById(R.id.ceping_text);
            relativeLayout=itemView.findViewById(R.id.ceping_lay);
        }
    }
}
