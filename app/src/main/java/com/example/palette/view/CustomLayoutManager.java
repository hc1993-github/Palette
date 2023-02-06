package com.example.palette.view;

import android.graphics.Rect;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class CustomLayoutManager extends RecyclerView.LayoutManager {
    private int mSumDy = 0;
    private int mTotalHeight = 0;
    private int mItemWidth;
    private int mItemHeight;
    private SparseArray<Rect> mItemRects = new SparseArray<>();
    private SparseBooleanArray mHasAttachedItems = new SparseBooleanArray();

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if(getItemCount()==0){
            detachAndScrapAttachedViews(recycler);
            return;
        }
        mHasAttachedItems.clear();
        mItemRects.clear();

        detachAndScrapAttachedViews(recycler);
        View childView = recycler.getViewForPosition(0);
        measureChildWithMargins(childView,0,0);
        mItemWidth = getDecoratedMeasuredWidth(childView);
        mItemHeight = getDecoratedMeasuredHeight(childView);
        int visibleCount = getVerticalSpace()/mItemHeight;
        int offsetY = 0;
        for (int i = 0; i < getItemCount(); i++) {
            Rect rect = new Rect(0,offsetY,mItemWidth,offsetY+mItemHeight);
            mItemRects.put(i,rect);
            mHasAttachedItems.put(i,false);
            offsetY+=mItemHeight;
        }
        for (int i = 0; i < visibleCount; i++) {
            Rect rect = mItemRects.get(i);
            View view = recycler.getViewForPosition(i);
            addView(view);
            measureChildWithMargins(view,0,0);
            layoutDecorated(view,rect.left,rect.top,rect.right,rect.bottom);
        }
        mTotalHeight = Math.max(offsetY,getVerticalSpace());
    }

    private int getVerticalSpace() {
        return getHeight()-getPaddingBottom()-getPaddingTop();
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if(getItemCount()<=0){
            return dy;
        }
        int travel = dy;
        if(mSumDy+dy<0){
            travel = -mSumDy;
        }else if(mSumDy+dy>mTotalHeight-getVerticalSpace()){
            travel = mTotalHeight-getVerticalSpace()-mSumDy;
        }
        mSumDy+=travel;
        Rect visibleRect = getVisibleArea();

        for (int i = getChildCount()-1; i >=0 ; i--) {
            View child = getChildAt(i);
            int position = getPosition(child);
            Rect rect = mItemRects.get(position);
            if(!Rect.intersects(rect,visibleRect)){
                removeAndRecycleView(child,recycler);
                mHasAttachedItems.put(position,false);
            }else {
                layoutDecoratedWithMargins(child,rect.left,rect.top-mSumDy,rect.right,rect.bottom-mSumDy);
                //child.setRotationY(child.getRotationY()+1);
                mHasAttachedItems.put(position,true);
            }
        }
        View lastView = getChildAt(getChildCount()-1);
        View firstView =getChildAt(0);
        if(travel>=0){
            int minPos = getPosition(firstView);
            for (int i = minPos; i < getItemCount(); i++) {
                insertView(i,visibleRect,recycler,false);
            }
        }else {
            int maxPos = getPosition(lastView);
            for (int i = maxPos; i >=0 ; i--) {
                insertView(i,visibleRect,recycler,true);
            }
        }
        return travel;
    }

    private void insertView(int pos, Rect visibleRect, RecyclerView.Recycler recycler,boolean firstPos){
        Rect rect = mItemRects.get(pos);
        if(Rect.intersects(visibleRect,rect) && !mHasAttachedItems.get(pos)){
            View child = recycler.getViewForPosition(pos);
            if(firstPos){
                addView(child,0);
            }else {
                addView(child);
            }
            measureChildWithMargins(child,0,0);
            layoutDecoratedWithMargins(child,rect.left,rect.top-mSumDy,rect.right,rect.bottom-mSumDy);

            //child.setRotationY(child.getRotationY()+1);
            mHasAttachedItems.put(pos,true);
        }
    }
    private Rect getVisibleArea(){
        Rect result = new Rect(getPaddingLeft(),getPaddingTop()+mSumDy,getWidth()+getPaddingRight(),getVerticalSpace()+mSumDy);
        return result;
    }

}
