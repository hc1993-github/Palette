package com.example.palette.async;

import android.os.Process;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

public abstract class AppStartTask {
    //定时器 数量=父任务数量
    private CountDownLatch countDownLatch = new CountDownLatch(getParentTask().size());

    /**
     * 获取父任务
     * @return
     */
    public abstract List<Class<? extends AppStartTask>> getParentTask();

    /**
     * 当前任务等待
     */
    public void waitToNotify(){
        try {
            countDownLatch.await();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 减少定时数
     */
    public void notifyParentTaskFinish(){
        countDownLatch.countDown();
    }

    /**
     * 执行线程
     * @return
     */
    public Executor runOnExecutor(){
        return AppStartTaskExecutor.getInstance().ioExecutor;
    }

    /**
     * 优先级
     * @return
     */
    public int priority(){
        return Process.THREAD_PRIORITY_BACKGROUND;
    }

    /**
     * 是否等待
     * @return
     */
    public boolean needWait(){
        return true;
    }

    /**
     * 是否主线程执行
     * @return
     */
    public abstract boolean isRunOnMainThread();

    /**
     * 执行任务
     */
    public abstract void run();
}
