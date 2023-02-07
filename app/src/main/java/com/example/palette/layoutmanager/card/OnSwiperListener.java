package com.example.palette.layoutmanager.card;

import androidx.recyclerview.widget.RecyclerView;

public interface OnSwiperListener<T> {
    /**
     * 正在滑动
     * @param viewHolder
     * @param ratio 滑动比例
     * @param direction
     */
    void onSwiping(RecyclerView.ViewHolder viewHolder, float ratio, int direction);

    /**
     * 当前子View完全滑出
     * @param viewHolder
     * @param t
     * @param direction
     */
    void onSwiped(RecyclerView.ViewHolder viewHolder,T t,int direction);

    /**
     * 全部子View滑出
     */
    void onSwipedClear();
}
