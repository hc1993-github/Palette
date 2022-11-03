package com.example.palette.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseHolder>{
    Context context;
    int layoutId;
    List<T> datas;
    OnItemClickListener<T> onItemClickListen;
    public BaseAdapter(Context context,int layoutId,List<T> datas) {
        this.datas = datas;
        this.context = context;
        this.layoutId = layoutId;
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseHolder holder = new BaseHolder(LayoutInflater.from(context).inflate(layoutId, parent, false));
        bindListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {
        T t = datas.get(position);
        bindViews(holder,t,position);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public abstract void bindViews(BaseHolder holder, T t, int position);

    public abstract void bindListener(BaseHolder holder);

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListen) {
        this.onItemClickListen = onItemClickListen;
    }

    public interface OnItemClickListener<T>{
        void onItemClick(T t,int position);
    }
}
