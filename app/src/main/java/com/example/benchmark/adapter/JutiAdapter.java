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

public class JutiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<JuTiData> data;

    private Context context;



    public JutiAdapter(Context context, List<JuTiData> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.juti_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RecyclerViewHolder) {
            final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
            JuTiData info = this.data.get(position);
            recyclerViewHolder.juTiItemZhiBiao.setText(info.getJutiItem());
            recyclerViewHolder.juTiItemGrade.setText(info.getJuTiItemGrade());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public TextView juTiItemZhiBiao;
        public TextView juTiItemGrade;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            juTiItemZhiBiao = itemView.findViewById(R.id.juti_item_zhibiao);
            juTiItemGrade = itemView.findViewById(R.id.juti_item_grade);
        }
    }
}
