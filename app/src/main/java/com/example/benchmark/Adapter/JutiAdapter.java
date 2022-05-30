<<<<<<< HEAD
package com.example.benchmark.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.benchmark.Data.JuTiData;
import com.example.benchmark.R;

import java.util.List;

public class JutiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    List<JuTiData> data;
    public JutiAdapter(Context context, List<JuTiData> data) {
        this.context = context;
        this.data = data;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.juti_item, parent,false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final RecyclerViewHolder recyclerViewHolder= (RecyclerViewHolder) holder;
        JuTiData data = this.data.get(position);
        recyclerViewHolder.JutiItemZhibiao.setText(data.getJutiItem());
        recyclerViewHolder.JuTiItemGrade.setText(data.getJuTiItemGrade());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder{
        public TextView JutiItemZhibiao,JuTiItemGrade;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            JutiItemZhibiao=itemView.findViewById(R.id.juti_item_zhibiao);
            JuTiItemGrade = itemView.findViewById(R.id.juti_item_grade);
        }
    }
}
=======
package com.example.benchmark.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.benchmark.Data.JuTiData;
import com.example.benchmark.R;

import java.util.List;

public class JutiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    List<JuTiData> data;
    public JutiAdapter(Context context, List<JuTiData> data) {
        this.context = context;
        this.data = data;
    }




    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.juti_item, parent,false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final RecyclerViewHolder recyclerViewHolder= (RecyclerViewHolder) holder;
        JuTiData data = this.data.get(position);
        recyclerViewHolder.JutiItemZhibiao.setText(data.getJutiItem());
        recyclerViewHolder.JuTiItemGrade.setText(data.getJuTiItemGrade());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder{
        public TextView JutiItemZhibiao,JuTiItemGrade;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            JutiItemZhibiao=itemView.findViewById(R.id.juti_item_zhibiao);
            JuTiItemGrade = itemView.findViewById(R.id.juti_item_grade);
        }
    }
}
>>>>>>> 211fdf0 ([feat]华为云手机、云手游，红手指、移动云手机稳定性测评)
