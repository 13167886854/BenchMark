package com.example.benchmark.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.benchmark.Data.LiuChang;
import com.example.benchmark.Data.ShuoMingData;
import com.example.benchmark.R;

import java.util.List;

public class ShuoMingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    List<ShuoMingData> data;
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
        RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
        ShuoMingData data = this.data.get(position);

        recyclerViewHolder.shuoming_item_img.setImageResource(data.getShuoming_image());
        List<LiuChang> list = data.getList();

        ItemAdapter adapter = new ItemAdapter(context, list);
        recyclerViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
        recyclerViewHolder.recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder{

        public ImageView shuoming_item_img;
        public RecyclerView recyclerView;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            shuoming_item_img=itemView.findViewById(R.id.shuoming_img);
            recyclerView=itemView.findViewById(R.id.shuoming_item_rv);
        }
    }
}
