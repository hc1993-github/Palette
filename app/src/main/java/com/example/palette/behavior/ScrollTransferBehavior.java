package com.example.palette.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

public class ScrollTransferBehavior extends AppBarLayout.Behavior {
    private static final int INVAILD_POINTER = -1;
    private View mScrollTransferView;
    private int mTouchSlop = -1;
    private int mActivePointerId = INVAILD_POINTER;
    private int mLastMotionY;
    private MotionEvent mCurrentDownEvent;
    private boolean mNeedDispatchDown;
    private boolean mIsBeingDragged;

    public ScrollTransferBehavior() {

    }

    public ScrollTransferBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (mTouchSlop < 0) {
            mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        }
    }

    /**
     * 第一种情况:监听View的变化
     * 决定child是否根据dependency的变化而变化
     */
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, AppBarLayout child,View dependency) {
        return super.layoutDependsOn(parent, child, dependency);
    }

    /**
     * 当layoutDependsOn返回true时调用
     * child根据dependency的变化进行处理
     */
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, AppBarLayout child, View dependency) {
        return super.onDependentViewChanged(parent, child, dependency);
    }

    /**
     * 第二种情况:监听滑动
     * directTargetChild:parent的子滑动布局
     * target:parent的孙子布局
     * nestedScrollAxes:方向
     * type:引起滑动的事件
     */
    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent,AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes, int type) {
        return super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes, type);
    }

    /**
     * 滑动前的处理
     */
    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout,AppBarLayout child, View target, int dx, int dy, int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mIsBeingDragged = false;
                mNeedDispatchDown = true;
                int dx = (int) ev.getX();
                int dy = (int) ev.getY();
                if (parent.isPointInChildBounds(child, dx, dy)) {
                    mLastMotionY = dy;
                    mActivePointerId = ev.getPointerId(0);
                    if (mCurrentDownEvent != null) {
                        mCurrentDownEvent.recycle();
                    }
                    mCurrentDownEvent = MotionEvent.obtain(ev);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int activePointerId = mActivePointerId;
                if (activePointerId == INVAILD_POINTER) {
                    break;
                }
                int pointerIndex = ev.findPointerIndex(activePointerId);
                if (pointerIndex == -1) {
                    break;
                }
                int my = (int) ev.getY(pointerIndex);
                int diffy = Math.abs(my - mLastMotionY);
                if (diffy > mTouchSlop) {
                    mIsBeingDragged = true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                mNeedDispatchDown = true;
                mActivePointerId = INVAILD_POINTER;
                break;
        }
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                if (mIsBeingDragged) {
                    int offset = child.getHeight() - child.getBottom();
                    if (mNeedDispatchDown) {
                        mNeedDispatchDown = false;
                        mCurrentDownEvent.offsetLocation(0, offset);
                        mScrollTransferView.dispatchTouchEvent(mCurrentDownEvent);
                    }
                    ev.offsetLocation(0, offset);
                    mScrollTransferView.dispatchTouchEvent(ev);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsBeingDragged) {
                    ev.offsetLocation(0, child.getHeight() - child.getBottom());
                    mScrollTransferView.dispatchTouchEvent(ev);
                    return true;
                }
                break;
        }
        return false;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, AppBarLayout abl, int layoutDirection) {
        boolean handled = super.onLayoutChild(parent, abl, layoutDirection);
        for (int i = 0; i < parent.getChildCount(); i++) {
            View view = parent.getChildAt(i);
            CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) view.getLayoutParams()).getBehavior();
            if (behavior instanceof AppBarLayout.ScrollingViewBehavior) {
                mScrollTransferView = view;
            }
        }
        return handled;
    }
}
