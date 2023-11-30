package com.example.palette.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager2.widget.ViewPager2;


public class OnViewPagerChangeBehavior extends CoordinatorLayout.Behavior {

    float initTop;
    int width;
    int height;
    public OnViewPagerChangeBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof ViewPager2;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        //child = textView  dependency=ViewPager2
        //-----下滑左侧进入,上滑左侧退出-----
        if(initTop==0){
            initTop = dependency.getTop();
            width = child.getMeasuredWidth();
        }
        float currentTop = dependency.getTop();
        int x;
        float per;
        if(currentTop>initTop){
            per=1;
        }else {
            per = currentTop / initTop;
        }
        x = (int) (width*per);
        child.scrollTo(x,0);
        //-----下滑展示上滑隐藏-----
//        if(initTop==0){
//            initTop = dependency.getTop();
//            height = child.getHeight();
//        }
//        float currentTop = dependency.getTop();
//        int y;
//        float per;
//        if(currentTop>initTop){
//            per = 1;
//        }else {
//            per = currentTop/initTop;
//        }
//        y = (int) (height*per);
//        child.scrollTo(0,y);
        return true;
    }
}
