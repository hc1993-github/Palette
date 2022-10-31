package com.example.palette.async;

import android.os.Process;

public class AppStartTaskRunnable implements Runnable{
    private AppStartTask task;
    private AppStartTaskDispatcher dispatcher;

    public AppStartTaskRunnable(AppStartTask task, AppStartTaskDispatcher dispatcher) {
        this.task = task;
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {
        Process.setThreadPriority(task.priority());
        task.waitToNotify();
        task.run();
        if(dispatcher!=null){
            dispatcher.setNotifyChildren(task);
            dispatcher.markAppStartTaskFinish(task);
        }
    }
}
