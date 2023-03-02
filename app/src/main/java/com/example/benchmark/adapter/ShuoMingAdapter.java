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

public class ShuoMingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<ShuoMingData> data;

    private Context context;

    public ShuoMingAdapter(Context context, List<ShuoMingData> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shuoming_item, parent, false);

        return new RecyclerViewHolder(view);
    }

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
