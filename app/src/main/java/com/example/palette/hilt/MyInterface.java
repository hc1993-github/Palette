package com.example.palette.hilt;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public interface MyInterface extends LifecycleObserver {
    void function1();

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void fun1();

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void fun2();
}
