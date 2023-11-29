package com.example.palette.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

/**
 * <com.example.palette.behavior.NestedTransferScrollView
 *     xmlns:android="http://schemas.android.com/apk/res/android"
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent"
 *     android:id="@+id/nsv"
 *     xmlns:app="http://schemas.android.com/apk/res-auto">
 *     <androidx.appcompat.widget.LinearLayoutCompat
 *         android:orientation="vertical"
 *         android:layout_width="match_parent"
 *         android:layout_height="match_parent">
 *         <androidx.appcompat.widget.AppCompatImageView
 *             android:layout_width="match_parent"
 *             android:layout_height="300dp"
 *             android:clickable="true"
 *             android:src="@drawable/one"
 *             android:scaleType="centerCrop"/>
 *         <LinearLayout
 *             android:id="@+id/ll_tlvp"
 *             android:orientation="vertical"
 *             android:layout_width="match_parent"
 *             android:layout_height="wrap_content">
 *             <com.google.android.material.tabs.TabLayout
 *                 android:id="@+id/tl"
 *                 android:layout_width="match_parent"
 *                 android:layout_height="wrap_content"
 *                 app:tabMaxWidth="0dp"
 *                 app:tabGravity="fill"
 *                 app:tabMode="fixed"/>
 *             <androidx.viewpager2.widget.ViewPager2
 *                 android:id="@+id/vp"
 *                 android:layout_width="match_parent"
 *                 android:layout_height="match_parent"/>
 *         </LinearLayout>
 *     </androidx.appcompat.widget.LinearLayoutCompat>
 * </com.example.palette.behavior.NestedTransferScrollView>
 */
public class NestedTransferScrollView extends NestedScrollView {
    ViewPager2 viewPager2;
    RecyclerView recyclerView;
    public NestedTransferScrollView(Context context,AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 向上滑动优先滑动父亲
     * @param target
     * @param dx
     * @param dy
     * @param consumed
     * @param type
     */
    @Override
    public void onNestedPreScroll(View target, int dx, int dy,int[] consumed, int type) {
        int appCompatImageViewHeight = ((LinearLayoutCompat) getChildAt(0)).getChildAt(0).getMeasuredHeight();
        boolean needHide = dy > 0 && getScrollY() < appCompatImageViewHeight; //向上滑动且已滑动的距离小于设置的大小
        if(needHide){
            scrollBy(0,dy);//向上滑动
            consumed[1] = dy;//告诉ViewPager2已消耗掉该距离
        }
    }

    /**
     * 传递滑动
     * @param velocityY
     */
    @Override
    public void fling(int velocityY) {
        super.fling(velocityY);
        if(velocityY>0){
            ViewPager2 viewPager2 = getChildView(this,ViewPager2.class);
            if(viewPager2!=null){
                RecyclerView recyclerView = getChildView((ViewGroup)viewPager2.getChildAt(0), RecyclerView.class);
                if(recyclerView!=null){
                    recyclerView.fling(0,velocityY);
                }
            }
        }
    }

    private <T> T getChildView(View viewGroup, Class<T> clazz) {
        if(viewGroup!=null && viewGroup.getClass()==clazz){
            return (T) viewGroup;
        }
        if(viewGroup instanceof ViewGroup){
            ViewGroup vg = (ViewGroup) viewGroup;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View view = vg.getChildAt(i);
                if(view instanceof ViewGroup){
                    T t = getChildView(view, clazz);
                    if(t!=null){
                        return t;
                    }
                }
            }
        }
        return null;
    }
}
