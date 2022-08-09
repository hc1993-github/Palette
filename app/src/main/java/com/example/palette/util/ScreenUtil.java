package com.example.palette.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

    public static int getHeightPx(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
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

    /**
     * 显示软键盘
     * @param context
     * @param view
     */
    public static void showSoftInput(final Context context, final View view) {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            }
        }, 998);
    }

    /**
     * 隐藏软键盘
     * @param context
     * @param view
     */
    public static void hideSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
    }

    /**
     * 获取软键盘状态
     * @param context
     * @return
     */
    public static boolean isShowSoftInput(Context context,View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        //获取状态信息
        return imm.isActive(view);//true 打开
    }

    /**
     * 获取显示在最顶端的activity名称
     * @param context
     * @return
     */
    public static String getTopActivityName(Context context) {
        String topActivityClassName = null;
        ActivityManager manager = (ActivityManager) (context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningTaskInfo> taskInfo = manager.getRunningTasks(1);
        if (taskInfo != null) {
            ComponentName f = taskInfo.get(0).topActivity;
            topActivityClassName = f.getClassName();
        }
        return topActivityClassName;
    }

    /**
     * 判断Activity是否运行在前台
     * @param context
     * @return
     */
    public static boolean isRunningForeground(Context context) {
        String packageName = context.getPackageName();
        String topActivityClassName = getTopActivityName(context);
        if (packageName != null && topActivityClassName != null && topActivityClassName.startsWith(packageName)) {
            return true;
        } else {
            return false;
        }
    }
}
