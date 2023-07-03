package com.example.palette.util;

import android.view.View;

public abstract class QuickClickListener implements View.OnClickListener{

    private long default_delay_time = 1000;
    private long last_time = 0;

    public QuickClickListener() {
    }

    public QuickClickListener(long delayTime) {
        this.default_delay_time = delayTime;
    }

    @Override
    public void onClick(View v) {
        long current = System.currentTimeMillis();
        if(current-last_time>default_delay_time){
            last_time = current;
            onQuickClickPass(v);
        }else {
            last_time = current;
            onQuickClickNoPass(v);
        }
    }

    public abstract void onQuickClickPass(View v);

    public void onQuickClickNoPass(View v){

    }
}
