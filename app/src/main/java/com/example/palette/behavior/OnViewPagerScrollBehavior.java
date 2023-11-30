package com.example.palette.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

public class OnViewPagerScrollBehavior extends CoordinatorLayout.Behavior {
    int lastPos;
    boolean downlimit;
    boolean uplimit;
    public OnViewPagerScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent,View child, MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                downlimit = false;
                uplimit = false;
                break;
        }
        return super.onInterceptTouchEvent(parent, child, ev);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int axes, int type) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL)!=0;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout,View child,View target, int dx, int dy, int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        if(target instanceof RecyclerView){
            RecyclerView recyclerView = (RecyclerView) target;
            int pos = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
            if(pos==0 && pos<lastPos){
                downlimit = true;
            }
            if(pos==0 && canScroll(child,dy)){
                float fY = child.getTranslationY()-dy;
                if(fY<-child.getHeight()){
                    fY = -child.getHeight();
                    uplimit = true;
                }else if(fY>0){
                    fY = 0;
                }
                child.setTranslationY(fY);
                consumed[1] = dy;
            }
            lastPos = pos;
        }
    }

    private boolean canScroll(View child,float scrollY){
        if(scrollY>0 && child.getTranslationY()==-child.getHeight() && !uplimit){
            return false;
        }
        if(downlimit){
            return false;
        }
        return true;
    }

}
