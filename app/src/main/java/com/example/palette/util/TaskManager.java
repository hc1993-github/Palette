package com.example.palette.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskManager {
    private static final String TAG = "TaskManager";
    private volatile static TaskManager mInstance;
    private static final int MAIN = 10000;
    private static final int THREAD_TO_MAIN = 20000;
    private Map<String,ThreadTaskerRunnable> mThreadTaskerRunnableMap = new ConcurrentHashMap<>();
    private Map<String,ThreadMainTaskerRunnable> mThreadMainTaskerRunnableMap = new ConcurrentHashMap<>();
    private Map<String,ThreadLoopTaskerRunnable> mThreadLoopTaskerRunnableMap = new ConcurrentHashMap<>();
    private ExecutorService mExecutor;
    private static Handler mMainHandler;

    public static TaskManager getInstance() {
        if (mInstance == null) {
            synchronized (TaskManager.class) {
                if (mInstance == null) {
                    mInstance = new TaskManager();
                }
            }
        }
        if(mMainHandler==null){
            init();
        }
        return mInstance;
    }

    private static void init() {
        mMainHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MAIN:
                        MainTasker tasker = (MainTasker) msg.obj;
                        try {
                            tasker.taskExecute();
                            tasker.taskFinish(false);
                        }catch (Throwable e){
                            tasker.taskFinish(true);
                            e.printStackTrace();
                        }
                        break;
                    case THREAD_TO_MAIN:
                        ThreadMainTaskerData data = (ThreadMainTaskerData) msg.obj;
                        ThreadMainTasker threadMainTasker = data.tasker;
                        try {
                            threadMainTasker.taskMainExecute(data.object);
                            threadMainTasker.taskMainFinish(false);
                        }catch (Throwable e){
                            threadMainTasker.taskMainFinish(true);
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };
    }

    public void doOnceInThread(String name,ThreadTasker tasker){
        if(TextUtils.isEmpty(name)){
            throw new NullPointerException("name must be not empty");
        }
        if(tasker!=null){
            ThreadTaskerRunnable runnable = mThreadTaskerRunnableMap.get(name);
            if(runnable==null){
                runnable = new ThreadTaskerRunnable(name, tasker);
                mThreadTaskerRunnableMap.put(name,runnable);
                if(mExecutor==null){
                    mExecutor = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
                }
                mExecutor.execute(runnable);
            }else {
                Log.i(TAG, "the" + name + " ThreadTasker is running");
            }
        }
    }

    private void cancelOnceInThread(String name) {
        try {
            Iterator<Map.Entry<String, ThreadTaskerRunnable>> iterator = mThreadTaskerRunnableMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ThreadTaskerRunnable> next = iterator.next();
                if (name.equals(next.getKey())) {
                    iterator.remove();
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void doLoopInThread(String name,long period,ThreadLoopTasker tasker){
        if(TextUtils.isEmpty(name)){
            throw new NullPointerException("name must be not empty");
        }
        if(period<0){
            throw new NullPointerException("period must be bigger than zero");
        }
        if(tasker!=null){
            ThreadLoopTaskerRunnable runnable = mThreadLoopTaskerRunnableMap.get(name);
            if(runnable==null){
                runnable = new ThreadLoopTaskerRunnable(name, period, tasker);
                mThreadLoopTaskerRunnableMap.put(name,runnable);
                if(mExecutor==null){
                    mExecutor = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
                }
                mExecutor.execute(runnable);
            }else {
                Log.i(TAG, "the" + name + " ThreadLoopTasker is running");
            }
        }
    }

    public void cancelLoopInThread(String name){
        if(!TextUtils.isEmpty(name)){
            try {
                ThreadLoopTaskerRunnable runnable = mThreadLoopTaskerRunnableMap.get(name);
                if(runnable!=null){
                    runnable.stop = true;
                    mThreadLoopTaskerRunnableMap.remove(name);
                }
            }catch (Throwable e){
                e.printStackTrace();
            }
        }
    }

    public void doOnceInMain(MainTasker tasker){
        doOnceInMainDelay(0,tasker);
    }

    public void doOnceInMainDelay(long delay,MainTasker tasker){
        if(delay<0){
            delay = 0;
        }
        if(tasker!=null && mMainHandler!=null){
            Message message = Message.obtain();
            message.what = MAIN;
            message.obj = tasker;
            mMainHandler.sendMessageDelayed(message,delay);
        }
    }

    public void doOnceInThreadToMain(String name,ThreadMainTasker tasker){
        doOnceInThreadToMainDelay(name,0,tasker);
    }

    public void doOnceInThreadToMainDelay(String name,long delay,ThreadMainTasker tasker){
        if(TextUtils.isEmpty(name)){
            throw new NullPointerException("name must be not empty");
        }
        if(delay<0){
            delay=0;
        }
        if(tasker!=null){
            ThreadMainTaskerRunnable runnable = mThreadMainTaskerRunnableMap.get(name);
            if(runnable==null){
                runnable = new ThreadMainTaskerRunnable(name, delay, tasker);
                mThreadMainTaskerRunnableMap.put(name,runnable);
                if(mExecutor==null){
                    mExecutor = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
                }
                mExecutor.execute(runnable);
            }else {
                Log.i(TAG, "the" + name + " ThreadMainTasker is running");
            }
        }
    }

    private void cancelOnceInThreadToMain(String name) {
        try {
            Iterator<Map.Entry<String, ThreadMainTaskerRunnable>> iterator = mThreadMainTaskerRunnableMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ThreadMainTaskerRunnable> next = iterator.next();
                if (name.equals(next.getKey())) {
                    iterator.remove();
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void cancelAll(){
        if (mMainHandler != null) {
            mMainHandler.removeCallbacksAndMessages(null);
            mMainHandler = null;
        }
        if(mExecutor!=null){
            mExecutor.shutdownNow();
            mExecutor = null;
        }
        try {
            Iterator<Map.Entry<String, ThreadMainTaskerRunnable>> iterator = mThreadMainTaskerRunnableMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ThreadMainTaskerRunnable> next = iterator.next();
                iterator.remove();
            }
            Iterator<Map.Entry<String, ThreadTaskerRunnable>> iterator1 = mThreadTaskerRunnableMap.entrySet().iterator();
            while (iterator1.hasNext()) {
                Map.Entry<String, ThreadTaskerRunnable> next = iterator1.next();
                iterator1.remove();
            }
            Iterator<Map.Entry<String, ThreadLoopTaskerRunnable>> iterator2 = mThreadLoopTaskerRunnableMap.entrySet().iterator();
            while (iterator2.hasNext()) {
                Map.Entry<String, ThreadLoopTaskerRunnable> next = iterator2.next();
                ThreadLoopTaskerRunnable runnable = next.getValue();
                runnable.stop = true;
                iterator2.remove();
            }
        }catch (Throwable e){
            e.printStackTrace();
        }
    }

    public interface MainTasker{
        void taskExecute();
        void taskFinish(boolean exception);
    }

    public interface ThreadTasker{
        void taskExecute();
        void taskFinish(boolean exception);
    }

    public interface ThreadMainTasker<T>{
        T taskThreadExecute();
        void taskThreadFinish(boolean exception);
        void taskMainExecute(T t);
        void taskMainFinish(boolean exception);
    }

    public interface ThreadLoopTasker{
        void taskExecute();
        void taskFinish(boolean exception,int number);
    }

    public static class ThreadLoopTaskerRunnable implements Runnable{
        private String name;
        private long period;
        private ThreadLoopTasker tasker;
        private int number;
        private boolean stop;

        public ThreadLoopTaskerRunnable(String name, long period, ThreadLoopTasker tasker) {
            this.name = name;
            this.period = period;
            this.tasker = tasker;
        }

        @Override
        public void run() {
            try {
                while (true){
                    number++;
                    tasker.taskExecute();
                    tasker.taskFinish(false,number);
                    Thread.sleep(period);
                    if(stop){
                        break;
                    }
                }
            }catch (Throwable e){
                tasker.taskFinish(true,number);
                e.printStackTrace();
            }finally {
                TaskManager.getInstance().cancelLoopInThread(name);
            }
        }
    }

    public static class ThreadTaskerRunnable implements Runnable{
        private String name;
        private ThreadTasker tasker;

        public ThreadTaskerRunnable(String name, ThreadTasker tasker) {
            this.name = name;
            this.tasker = tasker;
        }

        @Override
        public void run() {
            try {
                tasker.taskExecute();
                tasker.taskFinish(false);
            }catch (Throwable e){
                tasker.taskFinish(true);
                e.printStackTrace();
            }finally {
                TaskManager.getInstance().cancelOnceInThread(name);
            }
        }
    }

    public static class ThreadMainTaskerRunnable implements Runnable{
        private String name;
        private long delay;
        private ThreadMainTasker tasker;

        public ThreadMainTaskerRunnable(String name,long delay,ThreadMainTasker tasker) {
            this.name = name;
            this.delay = delay;
            this.tasker = tasker;
        }

        @Override
        public void run() {
            Object o = null;
            try {
                o = tasker.taskThreadExecute();
                tasker.taskThreadFinish(false);
                if(mMainHandler!=null){
                    Message message = Message.obtain();
                    message.what = THREAD_TO_MAIN;
                    message.obj = new ThreadMainTaskerData(tasker,o);
                    mMainHandler.sendMessageDelayed(message,delay);
                }

            }catch (Throwable e){
                tasker.taskThreadFinish(true);
                if(mMainHandler!=null){
                    Message message = Message.obtain();
                    message.what = THREAD_TO_MAIN;
                    message.obj = new ThreadMainTaskerData(tasker,o);
                    mMainHandler.sendMessageDelayed(message,delay);
                }
                e.printStackTrace();
            }finally {
                TaskManager.getInstance().cancelOnceInThreadToMain(name);
            }
        }
    }

    private static class ThreadMainTaskerData{

        public ThreadMainTasker tasker;
        public Object object;

        public ThreadMainTaskerData(ThreadMainTasker tasker, Object object) {
            this.tasker = tasker;
            this.object = object;
        }
    }
}
