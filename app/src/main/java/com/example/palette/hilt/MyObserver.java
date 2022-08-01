package com.example.palette.hilt;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class MyObserver implements LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void fun1(){
        Log.d("huachen", "fun1:");
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void fun2(){
        Log.d("huachen", "fun2:");
    }
}
