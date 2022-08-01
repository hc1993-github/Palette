package com.example.palette.hilt.module;

import com.example.palette.hilt.MyInterface;
import com.example.palette.hilt.MyInterfaceImpl;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;

@InstallIn(ActivityComponent.class)
@Module
public abstract class MyInterfaceImplModule {
    @Binds
    public abstract MyInterface bind(MyInterfaceImpl myInterface);
}
