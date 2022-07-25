package com.example.palette.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

public class ScreenUtil {
    /**
     * 获取density
     * @param context
     * @return
     */
    public static float getDensity(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.density;
    }

    /**
     * 获取屏幕宽度
     * @param context
     * @return
     */
    public static int getWidthPx(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /**
     * 获取屏幕高度
     * @param context
     * @return
     */
    public static int getDpi(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.densityDpi;
    }

    /**
     * dp转px
     * @param context
     * @param dp
     * @return
     */
    public static int dp2px(Context context,float dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     * @param context
     * @param sp
     * @return
     */
    public static int sp2px(Context context,float sp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,sp,context.getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     * @param context
     * @param px
     * @return
     */
    public static float px2dp(Context context,float px){
        float scale = context.getResources().getDisplayMetrics().density;
        return px/scale;
    }

    /**
     * px转sp
     * @param context
     * @param px
     * @return
     */
    public static float px2sp(Context context,float px){
        return px/context.getResources().getDisplayMetrics().scaledDensity;
    }

    /**
     * dip转px
     * @param context
     * @param dip
     * @return
     */
    public static int dip2px(Context context,float dip){
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dip*density+0.5f);
    }

    /**
     * px转dip
     * @param context
     * @param px
     * @return
     */
    public static int px2dip(Context context,float px){
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (px/density+0.5f);
    }
}
