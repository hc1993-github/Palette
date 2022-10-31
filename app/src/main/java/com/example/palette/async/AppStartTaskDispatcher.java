package com.example.palette.async;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AppStartTaskDispatcher {
    public boolean callContext = false;
    public boolean showLog = false;
    //等待总时长
    public long allTaskWaitTimeOut = 1000L;
    //当前是否为主线程
    private boolean isInMainThread = false;
    private CountDownLatch countDownLatch;
    //需要等待的任务数量
    private AtomicInteger needWaitCount = new AtomicInteger();
    //所有任务
    private ArrayList<AppStartTask> startTaskList = new ArrayList<>();
    //存放每个任务
    private HashMap<Class<? extends AppStartTask>,AppStartTask> taskMap = new HashMap<>();
    //存放每个任务的子任务
    private HashMap<Class<? extends AppStartTask>, HashSet<Class<? extends AppStartTask>>> taskChildMap = new HashMap<>();
    //排序后主线程的任务
    private ArrayList<AppStartTask> sortMainThreadTaskList = new ArrayList<>();
    //排序后子线程的任务
    private ArrayList<AppStartTask> sortChildThreadTaskList = new ArrayList<>();
    private long startTime = 0L;

    public static AppStartTaskDispatcher appStartTaskDispatcher;
    public static AppStartTaskDispatcher getInstance(){
        if(appStartTaskDispatcher==null){
            synchronized (AppStartTaskDispatcher.class){
                if(appStartTaskDispatcher==null){
                    appStartTaskDispatcher = new AppStartTaskDispatcher();
                }
            }
        }
        return appStartTaskDispatcher;
    }

    private AppStartTaskDispatcher() {
    }

    public AppStartTaskDispatcher setContext(Context context){
        callContext = true;
        isInMainThread = AppStartTaskUtil.isMainProcess(context);
        return this;
    }

    public AppStartTaskDispatcher setShowLog(boolean show){
        showLog = show;
        return this;
    }

    public AppStartTaskDispatcher setAllTaskWaitTimeOut(long timeOut){
        allTaskWaitTimeOut = timeOut;
        return this;
    }

    /**
     * 添加任务
     * @param task
     * @return
     */
    public AppStartTaskDispatcher addAppStartTask(AppStartTask task){
        if(task==null){
            throw new RuntimeException("the task your gived is null");
        }
        startTaskList.add(task);
        if(task.needWait()){
            needWaitCount.getAndIncrement();
        }
        return this;
    }

    /**
     * 开始所有任务
     * @return
     */
    public AppStartTaskDispatcher start(){
        if(!callContext){
            throw new RuntimeException("you must setContext before do this operation");
        }
        if(Looper.getMainLooper()!=Looper.myLooper()){
            throw new RuntimeException("you must do this operation in mainThread");
        }
        if(!isInMainThread){
            if(showLog){
                Log.e("AppStartTask","current process is not main thread");
            }
            return this;
        }
        startTime = System.currentTimeMillis();
        ArrayList<AppStartTask> sortAppStartTask = AppStartTaskUtil.sortAppStartTask(startTaskList, taskMap, taskChildMap);
        printSortTask(sortAppStartTask);
        initRealSortTask(sortAppStartTask);
        countDownLatch = new CountDownLatch(needWaitCount.get());
        dispatchAppStartTask();
        return this;
    }

    /**
     * 等待 阻塞主线程
     */
    public void await(){
        try {
            if(countDownLatch==null){
                throw new RuntimeException("you must start before do this operation");
            }
            countDownLatch.await(allTaskWaitTimeOut, TimeUnit.MILLISECONDS);
            if(showLog){
                Log.e("AppStartTask","all task start consume time: "+(System.currentTimeMillis()-startTime));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 日志打印
     * @param sortAppStartTask
     */
    private void printSortTask(ArrayList<AppStartTask> sortAppStartTask){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("current all task sort is: ");
        for(int i=0;i<sortAppStartTask.size();i++){
            String simpleName = sortAppStartTask.get(i).getClass().getSimpleName();
            if(i==0){
                stringBuilder.append(simpleName);
            }else {
                stringBuilder.append("-->");
                stringBuilder.append(simpleName);
            }
        }
        if(showLog){
            Log.e("AppStartTask",stringBuilder.toString());
        }
    }

    /**
     * 初始化主线程和子线程任务
     * @param sortAppStartTask
     */
    private void initRealSortTask(ArrayList<AppStartTask> sortAppStartTask){
        sortMainThreadTaskList.clear();
        sortChildThreadTaskList.clear();
        for(AppStartTask task:sortAppStartTask){
            if(task.isRunOnMainThread()){
                sortMainThreadTaskList.add(task);
            }else {
                sortChildThreadTaskList.add(task);
            }
        }
    }

    /**
     * 分线程执行任务
     */
    private void dispatchAppStartTask(){
        for(AppStartTask task:sortChildThreadTaskList){
            task.runOnExecutor().execute(new AppStartTaskRunnable(task,this));
        }
        for(AppStartTask task:sortMainThreadTaskList){
            new AppStartTaskRunnable(task,this).run();
        }
    }

    /**
     * 通知子任务,父任务已完成
     * @param appStartTask
     */
    public void setNotifyChildren(AppStartTask appStartTask){
        HashSet<Class<? extends AppStartTask>> hashSet = taskChildMap.get(appStartTask.getClass());
        if(hashSet!=null){
            for(Class<? extends AppStartTask> clz:hashSet){
                AppStartTask task = taskMap.get(clz);
                if(task!=null){
                    task.notifyParentTaskFinish();
                }
            }
        }
    }

    /**
     * 标识需要等待的任务
     * @param appStartTask
     */
    public void markAppStartTaskFinish(AppStartTask appStartTask){
        if(showLog){
            Log.e("AppStartTask",appStartTask.getClass().getSimpleName() +" is finished");
        }
        if(ifNeedWait(appStartTask)){
            if(countDownLatch!=null){
                countDownLatch.countDown();
            }
            needWaitCount.getAndDecrement();
        }
    }

    /**
     * 任务是否需要等待
     * @param appStartTask
     * @return
     */
    private boolean ifNeedWait(AppStartTask appStartTask){
        return !appStartTask.isRunOnMainThread() && appStartTask.needWait();
    }
}
