package com.example.palette.adapter;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palette.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;


public class StringAdapter extends RecyclerView.Adapter<StringAdapter.StringHolder>{
    List<String> data;
    ItemListener listener;
    boolean isEnable = false;
    public StringAdapter(List<String> data) {
        this.data = data;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState==SCROLL_STATE_IDLE){
                    isEnable = true;
                }else {
                    isEnable = false;
                }
            }
        });
    }

    @Override
    public StringHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_string, parent, false);
        return new StringHolder(view);
    }

    @Override
    public void onBindViewHolder(StringHolder holder, int position) {
        holder.tv.setText(data.get(position));
        holder.tv.setTag(R.id.id1,data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setListener(ItemListener listener) {
        this.listener = listener;
    }

    protected class StringHolder extends RecyclerView.ViewHolder{
        private TextView tv;
        public StringHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_item);
            itemView.setOnTouchListener((v, event) -> {
                if(isEnable){
                    if(event.getAction()==MotionEvent.ACTION_DOWN){
                        listener.onFingerDown((String) tv.getTag(R.id.id1));
                        tv.setBackgroundResource(R.drawable.side_selected);
                    }else if(event.getAction()==MotionEvent.ACTION_UP){
                        listener.onFingerUp((String) tv.getTag(R.id.id1));
                        tv.setBackgroundResource(R.drawable.side_unselected);
                    }
                }else {
                    tv.setBackgroundResource(R.drawable.side_unselected);
                    return true;
                }
                return true;
            });
        }
    }

    public interface ItemListener{
        void onFingerDown(String string);
        void onFingerUp(String string);
    }
}
