package com.example.palette.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeRecyclerView extends RecyclerView {
    private static final int INVALID_POSITION = -1;//触碰点不在item范围
    private static final int INVALID_ITEM_WIDTH = -1;//item只有一个子view
    private static final int MIN_VELOCITY_SPEED = 500;//最小滑动速度
    private int mScaledTouchSlop;//最小滑动距离
    private Scroller mScroller;//滑动器
    private VelocityTracker mVelocityTracker;//速度追踪器
    private float mFirstX;//首次按下时x位置
    private float mFirstY;//首次按下时y位置
    private float mLastX;//滑动时x位置
    private int mPosition;//第几个item被触碰
    private Rect mItemViewRect;//被触碰item矩形范围
    private ViewGroup mItemView;//被触碰item
    private int mInvisibleViewWidth;//item默认情况下不可见部分宽度
    private boolean mItemViewSwipe;//item是否滑动
    public SwipeRecyclerView(@NonNull Context context) {
        this(context,null);
    }

    public SwipeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public SwipeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = new Scroller(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int x = (int) e.getX();
        int y = (int) e.getY();
        obtainVelocity(e);
        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(!mScroller.isFinished()){
                    mScroller.abortAnimation();
                }
                mFirstX = mLastX = x;
                mFirstY = y;
                mPosition = pointToPosition(x,y);
                if(mPosition!=INVALID_POSITION){
                    View view = mItemView;
                    mItemView = (ViewGroup) getChildAt(mPosition-((LinearLayoutManager)getLayoutManager()).findFirstVisibleItemPosition());
                    //如果之前的item已经滑动 且当前触碰item和之前不同 则关闭之前item
                    if(view!=null && mItemView!=view && view.getScrollX()!=0){
                        view.scrollTo(0,0);
                    }
                    //不可见部分必须为具体值
                    if(mItemView.getChildCount()==2){
                        mInvisibleViewWidth = mItemView.getChildAt(1).getWidth();
                    }else{
                        mInvisibleViewWidth = INVALID_ITEM_WIDTH;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.computeCurrentVelocity(1000);
                //以下认为是侧滑
                //x方向速度大于y方向速度 且x方向速度大于最小速度
                //x方向滑动距离大于y方向滑动距离 且x方向滑动距离大于最小滑动距离
                float xVelocity = mVelocityTracker.getXVelocity();
                float yVelocity = mVelocityTracker.getYVelocity();
                if((Math.abs(xVelocity)> MIN_VELOCITY_SPEED && Math.abs(xVelocity) > Math.abs(yVelocity))
                        || (Math.abs(x-mFirstX)>=mScaledTouchSlop) && Math.abs(x-mFirstX) > Math.abs(y-mFirstY)){
                    mItemViewSwipe = true;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                releaseVelocity();
                break;
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if(mItemViewSwipe && mPosition!=INVALID_POSITION){
            float x = e.getX();
            //obtainVelocity(e);
            switch (e.getAction()){
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(mInvisibleViewWidth != INVALID_ITEM_WIDTH){
                        float dx = mLastX -x;
                        if(mItemView.getScrollX() + dx <= mInvisibleViewWidth && mItemView.getScrollX()+dx >0){
                            mItemView.scrollBy((int) dx,0);
                        }
                        mLastX = x;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if(mInvisibleViewWidth != INVALID_ITEM_WIDTH){
                        int scrollX = mItemView.getScrollX();
                        mVelocityTracker.computeCurrentVelocity(1000);
                        //以下不可见部分被滑出
                        //不可见部分滑动超过一半时
                        //x方向滑动速度大于最小速度
                        if(mVelocityTracker.getXVelocity()<-MIN_VELOCITY_SPEED){ //向左滑动大于最小速度 则打开
                            mScroller.startScroll(scrollX,0,mInvisibleViewWidth-scrollX,0,Math.abs(mInvisibleViewWidth-scrollX));
                        }else if(mVelocityTracker.getXVelocity() >= MIN_VELOCITY_SPEED){ //向右滑动大于最小速度 则关闭
                            mScroller.startScroll(scrollX,0,-scrollX,0,Math.abs(scrollX));
                        }else if(scrollX>=mInvisibleViewWidth/2){ //超过一半 则打开
                            mScroller.startScroll(scrollX,0,mInvisibleViewWidth-scrollX,0,Math.abs(mInvisibleViewWidth-scrollX));
                        }else { //其他情况 则关闭
                            mScroller.startScroll(scrollX,0,-scrollX,0,Math.abs(scrollX));
                        }
                        invalidate();
                    }
                    mInvisibleViewWidth = INVALID_ITEM_WIDTH;
                    mItemViewSwipe = false;
                    mPosition = INVALID_POSITION;
                    releaseVelocity();
                    break;
            }
            return true;
        }else {
            closeInvisibleView();
            releaseVelocity();
        }
        return super.onTouchEvent(e);
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()){
            mItemView.scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            invalidate();
        }
    }

    private int pointToPosition(int x, int y) {
        if(getLayoutManager()==null){
            return INVALID_POSITION;
        }
        int firstPosition = ((LinearLayoutManager)getLayoutManager()).findFirstVisibleItemPosition();
        Rect currentPointRect = mItemViewRect;
        if(currentPointRect==null){
            mItemViewRect = new Rect();
            currentPointRect = mItemViewRect;
        }
        for(int i = getChildCount()-1;i>=0;i--){
            View child = getChildAt(i);
            if(child.getVisibility() == VISIBLE){
                child.getHitRect(currentPointRect);
                if(currentPointRect.contains(x,y)){
                    return firstPosition+i;
                }
            }
        }
        return INVALID_POSITION;
    }

    private void obtainVelocity(MotionEvent e) {
        if(mVelocityTracker==null){
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(e);
    }

    private void releaseVelocity() {
        if(mVelocityTracker!=null){
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void closeInvisibleView() {
        if(mItemView!=null && mItemView.getScrollX()!=0){
            mItemView.scrollTo(0,0);
        }
    }
}
