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

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<LiuChang> data;

    private Context context;

    public ItemAdapter(Context context, List<LiuChang> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
        LiuChang liuChang = data.get(position);
        recyclerViewHolder.itemOfItem.setText(liuChang.getItemOfItem());
        recyclerViewHolder.item_ceshifangfa.setText(liuChang.getCeshifangfa());
        recyclerViewHolder.item_pingjiazhibiao.setText(liuChang.getPingpanbiaozhun());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public TextView itemOfItem, item_ceshifangfa, item_pingjiazhibiao;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            itemOfItem = itemView.findViewById(R.id.item_item);
            item_ceshifangfa = itemView.findViewById(R.id.item_ceshifangfa);
            item_pingjiazhibiao = itemView.findViewById(R.id.item_pingpanbiaozhun);
        }
    }
}
