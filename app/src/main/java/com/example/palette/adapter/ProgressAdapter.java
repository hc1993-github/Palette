package com.example.palette.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.palette.R;
import com.example.palette.module.ProgressInterceptor;
import com.example.palette.module.ProgressListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_SETTLING;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.holder1> {
    List<String> mData;
    Context context;
    public ProgressAdapter(Context context, List<String> mData) {
        this.mData = mData;
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                switch (newState){
                    case SCROLL_STATE_IDLE:
                        Glide.with(context).resumeRequests();
                        break;
                    case SCROLL_STATE_DRAGGING:
                    case SCROLL_STATE_SETTLING:
                        Glide.with(context).pauseRequests();
                        break;
                }
            }
        });
    }

    @Override
    public holder1 onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        holder1 holder1 = new holder1(inflate);
        holder1.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("adapter1", "onClick: "+holder1.getAdapterPosition());
            }
        });
        return holder1;
    }

    @Override
    public void onBindViewHolder(ProgressAdapter.holder1 holder, int position) {
        ProgressInterceptor.addListener(mData.get(position), new ProgressListener() {
            @Override
            public void onProgress(int progress) {
                Log.d("adapter1", mData.get(position)+" onProgress: "+progress);
            }
        });
        Glide.with(context).load(mData.get(position)).into(new DrawableImageViewTarget(holder.textView){
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                super.onResourceReady(resource, transition);
                ProgressInterceptor.removeListener(mData.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    protected class holder1 extends RecyclerView.ViewHolder{
        ImageView textView;
        public holder1(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_tv);
        }
    }
}
