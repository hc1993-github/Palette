package com.example.palette.adapter;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.palette.R;

import java.util.List;


public class StringAdapter extends RecyclerView.Adapter<StringAdapter.StringHolder>{
    List<String> data;
    ItemListener listener;
    public StringAdapter(List<String> data) {
        this.data = data;
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
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    listener.onFingerDown((String) tv.getTag(R.id.id1));
                    tv.setBackgroundResource(R.drawable.side_selected);
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    listener.onFingerUp((String) tv.getTag(R.id.id1));
                    tv.setBackgroundResource(R.drawable.side_unselected);
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
