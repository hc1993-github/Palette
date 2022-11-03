package com.example.palette.adapter;

import android.util.SparseArray;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class BaseHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViewSparseArray;
    public BaseHolder(View itemView) {
        super(itemView);
    }
    public <V extends View> V getView(int viewId) {
        if (mViewSparseArray == null) {
            mViewSparseArray = new SparseArray<>();
        }
        View view = mViewSparseArray.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViewSparseArray.put(viewId, view);
        }
        return (V) view;
    }
}
