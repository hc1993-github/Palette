package com.example.palette.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palette.R;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.StringH> {
    public List<String> data;

    public RecyclerAdapter(List<String> data) {
        this.data = data;
    }

    @Override
    public StringH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview, parent, false);
        return new StringH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.StringH holder, int position) {
        holder.tv_item_recyclerview.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    protected class StringH extends RecyclerView.ViewHolder{
        TextView tv_item_recyclerview;
        public StringH(View itemView) {
            super(itemView);
            tv_item_recyclerview = itemView.findViewById(R.id.tv_item_recyclerview);
        }
    }
}
