package com.example.palette.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;


public class OnTextViewScrollBehavior extends CoordinatorLayout.Behavior {
    public OnTextViewScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof TextView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child,View dependency) {
        float bottom = dependency.getHeight()+dependency.getTranslationY();
        if(bottom<0){
            bottom = 0;
        }
        child.setY(bottom);
        return true;
    }
}
