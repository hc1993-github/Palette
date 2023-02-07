package com.example.palette.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.palette.R;

import java.util.List;

public class TestAdapter extends BaseAdapter<String> {
    public TestAdapter(Context context, int layoutId, List<String> datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void bindViews(BaseHolder holder, String s, int position) {
        TextView textView = holder.getView(R.id.tv_bann);
        textView.setText(s);
    }

    @Override
    public void bindListener(BaseHolder holder) {

    }
}
