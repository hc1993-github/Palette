package com.example.palette.hilt.module;

import com.example.palette.hilt.MyObserver;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.internal.managers.ApplicationComponentManager;

@InstallIn(ActivityComponent.class)
@Module
public class MyObserverModule {

    @Provides
    public MyObserver getMyObserver(){
        return new MyObserver();
    }
}
