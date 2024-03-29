package com.example.palette.download;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palette.R;

import java.util.List;

public class AutoTextAdapter extends RecyclerView.Adapter<AutoTextAdapter.TextHolder> {
    List<String> data;
    int layoutId;
    int itemTvId;
    public AutoTextAdapter(List<String> data, int layoutId,int item_tvId) {
        this.data = data;
        this.layoutId = layoutId;
        this.itemTvId = item_tvId;
    }

    @NonNull
    @Override
    public TextHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new TextHolder(view);
    }

    @Override
    public void onBindViewHolder(TextHolder holder, int position) {
        holder.tv.setText((position + 1) + "." + data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    protected class TextHolder extends RecyclerView.ViewHolder {
        private TextView tv;

        public TextHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(itemTvId);
        }
    }
}
