package com.example.palette.layoutmanager.card;

import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import static com.example.palette.layoutmanager.card.CardLayoutManager.DEFAULT_ROTATE_DEGREE;
import static com.example.palette.layoutmanager.card.CardLayoutManager.DEFAULT_SCALE;
import static com.example.palette.layoutmanager.card.CardLayoutManager.DEFAULT_SHOW_ITEM;
import static com.example.palette.layoutmanager.card.CardLayoutManager.DEFAULT_TRANSLATE_Y;
import static com.example.palette.layoutmanager.card.CardLayoutManager.SWIPED_LEFT;
import static com.example.palette.layoutmanager.card.CardLayoutManager.SWIPED_RIGHT;
import static com.example.palette.layoutmanager.card.CardLayoutManager.SWIPING_LEFT;
import static com.example.palette.layoutmanager.card.CardLayoutManager.SWIPING_NONE;


public class ItemTouchHelperCallback<T> extends ItemTouchHelper.Callback{
    private RecyclerView.Adapter adapter;
    private List<T> dataList;
    private OnSwiperListener<T> mListener;

    public ItemTouchHelperCallback(RecyclerView.Adapter adapter, List<T> dataList) {
        this.adapter = adapter;
        this.dataList = dataList;
    }

    public ItemTouchHelperCallback(RecyclerView.Adapter adapter, List<T> dataList, OnSwiperListener<T> listener) {
        this.adapter = adapter;
        this.dataList = dataList;
        this.mListener = listener;
    }

    public void setOnSwipeListener(OnSwiperListener<T> mListener) {
        this.mListener = mListener;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView,RecyclerView.ViewHolder viewHolder) {
        int dragFlags = 0;
        int swipeFlags = 0;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager instanceof CardLayoutManager){
            swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        }
        return makeMovementFlags(dragFlags,swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView,RecyclerView.ViewHolder viewHolder,RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        viewHolder.itemView.setOnTouchListener(null);
        int layoutPosition = viewHolder.getAdapterPosition();
        T remove = dataList.remove(layoutPosition);
        adapter.notifyDataSetChanged();
        if(mListener!=null){
            mListener.onSwiped(viewHolder,remove,direction==ItemTouchHelper.LEFT?SWIPED_LEFT:SWIPED_RIGHT);
        }
        if(adapter.getItemCount()==0){
            if(mListener!=null){
                mListener.onSwipedClear();
            }
        }
    }

    @Override
    public void onChildDraw(Canvas c,RecyclerView recyclerView,RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView  = viewHolder.itemView;
        if(actionState==ItemTouchHelper.ACTION_STATE_SWIPE){
            float ratio = dX / getWidthThreshold(recyclerView,viewHolder);
            if(ratio>1){
                ratio = 1;
            }else if(ratio<-1){
                ratio = -1;
            }
            itemView.setRotation(ratio*DEFAULT_ROTATE_DEGREE);
            int childCount = recyclerView.getChildCount();
            if(childCount > DEFAULT_SHOW_ITEM){
                for(int position=1;position<childCount-1;position++){
                    int index = childCount-position-1;
                    View view = recyclerView.getChildAt(position);
                    view.setScaleX(1-index*DEFAULT_SCALE+Math.abs(ratio)*DEFAULT_SCALE);
                    view.setScaleY(1-index*DEFAULT_SCALE+Math.abs(ratio)*DEFAULT_SCALE);
                    view.setTranslationY((index-Math.abs(ratio))*itemView.getMeasuredHeight()/DEFAULT_TRANSLATE_Y);
                }
            }else {
                for (int position = 0; position < childCount - 1; position++) {
                    int index = childCount - position - 1;
                    View view = recyclerView.getChildAt(position);
                    view.setScaleX(1 - index * DEFAULT_SCALE + Math.abs(ratio) * DEFAULT_SCALE);
                    view.setScaleY(1 - index * DEFAULT_SCALE + Math.abs(ratio) * DEFAULT_SCALE);
                    view.setTranslationY((index - Math.abs(ratio)) * itemView.getMeasuredHeight() / DEFAULT_TRANSLATE_Y);
                }
            }
            if(mListener!=null){
                if(ratio!=0){
                    mListener.onSwiping(viewHolder,ratio,ratio<0?SWIPING_LEFT:SWIPED_RIGHT);
                }else {
                    mListener.onSwiping(viewHolder,ratio,SWIPING_NONE);
                }
            }
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView,RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setRotation(0f);
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        return 3000f;
    }

    private float getWidthThreshold(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder){
        return recyclerView.getWidth()*getSwipeThreshold(viewHolder);
    }

}
