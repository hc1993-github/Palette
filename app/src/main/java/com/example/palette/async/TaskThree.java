package com.example.palette.async;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class TaskThree extends AppStartTask {
    @Override
    public List<Class<? extends AppStartTask>> getParentTask() {
        List<Class<?extends AppStartTask>> tasks = new ArrayList<>();
        //tasks.add(TaskTwo.class);
        return tasks;
    }

    @Override
    public boolean isRunOnMainThread() {
        return false;
    }

    @Override
    public void run() {
        Log.e("huachen", "任务三");
    }

}
