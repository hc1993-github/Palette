package com.example.palette.hilt;

import android.util.Log;


import javax.inject.Inject;

public class MyInterfaceImpl implements MyInterface {

    @Inject
    public MyInterfaceImpl() {
    }

    @Override
    public void function1() {
        Log.d("huachen", "function1:");
    }

    @Override
    public void fun1() {
        Log.d("huachen", "MyInterfaceImpl.fun1:");
    }

    @Override
    public void fun2() {
        Log.d("huachen", "MyInterfaceImpl.fun2:");
    }

}
