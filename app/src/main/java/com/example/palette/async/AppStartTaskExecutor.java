package com.example.palette.async;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AppStartTaskExecutor {
    //CPU核数
    private int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    //核心线程数
    private int CORE_POOL_SIZE = Math.max(2,Math.min(CPU_COUNT-1,4));
    //最大线程数
    private int MAX_POOL_SIZE = CORE_POOL_SIZE;
    //线程回收时间
    private final long KEEP_ALIVE_SECONDS = 5L;
    //线程池队列
    private BlockingDeque<Runnable> mPoolWorkQueue = new LinkedBlockingDeque<>();
    //CPU线程池
    public ThreadPoolExecutor cpuExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS, mPoolWorkQueue, Executors.defaultThreadFactory(), new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    Executors.newCachedThreadPool().execute(r);
                }
            });
    //IO线程池
    public ExecutorService ioExecutor = Executors.newCachedThreadPool(Executors.defaultThreadFactory());

    public static AppStartTaskExecutor mAppStartTaskExecutor;

    public static AppStartTaskExecutor getInstance(){
        if(mAppStartTaskExecutor==null){
            synchronized (AppStartTaskExecutor.class){
                if(mAppStartTaskExecutor==null){
                    mAppStartTaskExecutor = new AppStartTaskExecutor();
                }
            }
        }
        return mAppStartTaskExecutor;
    }

    private AppStartTaskExecutor() {
    }
}
