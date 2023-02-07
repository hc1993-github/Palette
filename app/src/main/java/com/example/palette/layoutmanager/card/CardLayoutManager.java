package com.example.palette.layoutmanager.card;

import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;



public class CardLayoutManager extends RecyclerView.LayoutManager {
    public static final int DEFAULT_SHOW_ITEM = 3;//可见数量
    public static final float DEFAULT_SCALE = 0.1f;//缩放比例
    public static final int DEFAULT_TRANSLATE_Y = 14;//y轴偏移量14等分
    public static final float DEFAULT_ROTATE_DEGREE = 15f;//滑动倾斜角度
    public static final int SWIPING_NONE = 1;//滑动不偏离
    public static final int SWIPING_LEFT = 1<<2;//左滑动
    public static final int SWIPING_RIGHT = 1<<3;//右滑动
    public static final int SWIPED_LEFT = 1;//左滑出
    public static final int SWIPED_RIGHT = 1<<2;//右滑出
    private RecyclerView mRecyclerView;
    private ItemTouchHelper mItemTouchHelper;
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            RecyclerView.ViewHolder viewHolder = mRecyclerView.getChildViewHolder(v);
            if(MotionEventCompat.getActionMasked(event)==MotionEvent.ACTION_DOWN){
                mItemTouchHelper.startSwipe(viewHolder);
            }
            return false;
        }
    };

    public CardLayoutManager(RecyclerView recyclerView, ItemTouchHelper itemTouchHelper) {
        this.mRecyclerView = checkIsNull(recyclerView);
        this.mItemTouchHelper = checkIsNull(itemTouchHelper);
    }

    public  <T> T checkIsNull(T t){
        if(t==null){
            throw new NullPointerException();
        }
        return t;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        int itemCount = getItemCount();
        if(itemCount==0){
            detachAndScrapAttachedViews(recycler);
            return;
        }
        removeAllViews();
        detachAndScrapAttachedViews(recycler);//移除子View 放入Scrap缓存中
        if(itemCount>DEFAULT_SHOW_ITEM){
            //倒序排放子View来符合视觉逻辑
            for (int position = DEFAULT_SHOW_ITEM; position >=0 ; position--) {
                View childView = recycler.getViewForPosition(position);//获取子View
                addView(childView);//添加至RecyclerView
                measureChildWithMargins(childView,0,0);//测量子View
                int widthSpace = getWidth()-getDecoratedMeasuredWidth(childView);//除去子View 剩下的空间
                int heightSpace = getHeight()-getDecoratedMeasuredHeight(childView);
                layoutDecoratedWithMargins(childView,widthSpace/2,heightSpace/2,widthSpace/2+getDecoratedMeasuredWidth(childView),heightSpace/2+getDecoratedMeasuredHeight(childView));//子View布局在RecyclerView中心
                if(position==DEFAULT_SHOW_ITEM){ //最后1个子View放至最后第2个子View处 保持连贯性
                    childView.setScaleX(1-(position-1)*DEFAULT_SCALE);
                    childView.setScaleY(1-(position-1)*DEFAULT_SCALE);
                    childView.setTranslationY((position-1)*childView.getMeasuredHeight()/DEFAULT_TRANSLATE_Y);
                }else if(position>0){
                    childView.setScaleX(1-position*DEFAULT_SCALE);
                    childView.setScaleY(1-position*DEFAULT_SCALE);
                    childView.setTranslationY(position*childView.getMeasuredHeight()/DEFAULT_TRANSLATE_Y);
                }else {
                    childView.setOnTouchListener(mOnTouchListener);
                }
            }
        }else {
            for (int position = itemCount-1; position >=0 ; position--) {
                View childView = recycler.getViewForPosition(position);
                addView(childView);
                measureChildWithMargins(childView,0,0);
                int widthSpace = getWidth()-getDecoratedMeasuredWidth(childView);
                int heightSpace = getHeight()-getDecoratedMeasuredHeight(childView);
                layoutDecoratedWithMargins(childView,widthSpace/2,heightSpace/2,widthSpace/2+getDecoratedMeasuredWidth(childView),heightSpace/2+getDecoratedMeasuredHeight(childView));//子View布局在RecyclerView中心
                if(position>0){
                    childView.setScaleX(1-position*DEFAULT_SCALE);
                    childView.setScaleY(1-position*DEFAULT_SCALE);
                    childView.setTranslationY(position*childView.getMeasuredHeight()/DEFAULT_TRANSLATE_Y);
                }else {
                    childView.setOnTouchListener(mOnTouchListener);
                }
            }
        }
    }

}
