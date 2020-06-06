package com.example.coronamaskmap.ui.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coronamaskmap.R;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private List<Responese.Store> stores;

    public Adapter(List<Responese.Store> stores) {
        this.stores = stores;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        Responese.Store store = stores.get(position);
        holder.name.setText(store.getName());
        holder.addr.setText("주소 : "+store.getAddr());
        holder.createdAt.setText("갱신시간 : " + store.getCreated_at());
        holder.stockAt.setText("입고시간 : " + store.getStock_at());
        if("plenty".equals(store.getRemain_stat())){
            holder.remainStat.setText("재고상태 : 100개 이상 보유");
        }else if ("some".equals(store.getRemain_stat())) {
            holder.remainStat.setText("재고상태 : 30개이상 100개미만");
        }else if ("few".equals(store.getRemain_stat())) {
            holder.remainStat.setText("재고상태 : 2개이상 30개미만");
        }else if("empty".equals(store.getRemain_stat())) {
            holder.remainStat.setText("재고상태 : 1개 이하");
        }else if("break".equals(store.getRemain_stat())){
            holder.remainStat.setText("재고상태 : 판매중지");
        }else{
            holder.remainStat.setText("알 수 없음");
        }
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, addr, createdAt, stockAt, remainStat;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            addr = itemView.findViewById(R.id.addr);
            createdAt = itemView.findViewById(R.id.created_at);
            stockAt = itemView.findViewById(R.id.stock_at);
            remainStat = itemView.findViewById(R.id.remainStat);
        }
    }
}