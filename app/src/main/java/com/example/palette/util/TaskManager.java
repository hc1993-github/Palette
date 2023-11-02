package com.example.palette.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TaskManager {
    private static final String TAG = "TaskManager";
    private volatile static TaskManager mInstance;
    private static Handler mMainHandler;
    private static final int MAIN = 10000;
    private static final int THREAD_TO_MAIN = 10001;
    private Map<String, ThreadTask> mThreadTaskMap = new ConcurrentHashMap<>();
    private Map<String, ThreadMainTask> mThreadMainTaskMap = new ConcurrentHashMap<>();
    private Map<String, ThreadLoopThread> mThreadLoopThreadMap = new ConcurrentHashMap<>();

    public static TaskManager getInstance() {
        if (mInstance == null) {
            synchronized (TaskManager.class) {
                if (mInstance == null) {
                    mInstance = new TaskManager();
                }
            }
        }
        if (mMainHandler == null) {
            init();
        }
        return mInstance;
    }

    private static void init() {
        mMainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MAIN:
                        if (mMainHandler != null) {
                            MainTasker mainTasker = (MainTasker) msg.obj;
                            try {
                                mainTasker.taskInMain();
                                mainTasker.taskInMainFinish(false);
                            } catch (Throwable e) {
                                mainTasker.taskInMainFinish(true);
                                e.printStackTrace();
                            }
                        }
                        break;
                    case THREAD_TO_MAIN:
                        if (mMainHandler != null) {
                            ThreadMainTaskerData data = (ThreadMainTaskerData) msg.obj;
                            ThreadMainTasker threadMainTasker = data.getListener();
                            try {
                                threadMainTasker.taskInMain(data.getObject());
                                threadMainTasker.taskInMainFinish(false);
                            } catch (Throwable e) {
                                threadMainTasker.taskInMainFinish(true);
                                e.printStackTrace();
                            }
                            data.clear();
                        }
                        break;
                }
            }
        };
    }

    /**
     * 子线程执行一次
     */
    public void doOnceInThread(String name, ThreadTasker threadTasker) {
        if (TextUtils.isEmpty(name)) {
            throw new NullPointerException("name must be not empty");
        }
        if (threadTasker != null) {
            ThreadTask threadTask = mThreadTaskMap.get(name);
            if (threadTask == null) {
                threadTask = new ThreadTask(name, threadTasker);
                mThreadTaskMap.put(name, threadTask);
                threadTask.start();
            } else {
                Log.i(TAG, "the" + name + " thread is running");
            }
        }
    }

    /**
     * 主线程执行一次
     */
    public void doOnceInMain(MainTasker mainTasker) {
        doOnceInMainDelay(0, mainTasker);
    }

    /**
     * 主线程延时时间后执行一次
     */
    public void doOnceInMainDelay(long delay, MainTasker mainTasker) {
        if (delay < 0) {
            throw new NullPointerException("delay must be bigger than zero");
        }
        if (mainTasker != null && mMainHandler != null) {
            Message message = Message.obtain();
            message.what = MAIN;
            message.obj = mainTasker;
            if (mMainHandler != null) {
                mMainHandler.sendMessageDelayed(message, delay);
            }
        }
    }

    /**
     * 先在子线程执行一次再在主线程执行一次
     */
    public void doOnceInThreadInMain(String name, ThreadMainTasker threadMainListener) {
        doOnceInThreadDelayOnceInMain(name, 0, threadMainListener);
    }

    /**
     * 先在子线程执行一次,延时时间后,再在主线程执行一次
     */
    public void doOnceInThreadDelayOnceInMain(String name, long delay, ThreadMainTasker threadMainTasker) {
        if (delay < 0) {
            throw new NullPointerException("delay must be bigger than zero");
        }
        if (threadMainTasker != null && mMainHandler != null) {
            ThreadMainTask threadMainTask = mThreadMainTaskMap.get(name);
            if (threadMainTask == null) {
                threadMainTask = new ThreadMainTask(name, delay, threadMainTasker);
                mThreadMainTaskMap.put(name, threadMainTask);
                threadMainTask.start();
            } else {
                Log.i(TAG, "the" + name + " thread is running");
            }
        }
    }

    /**
     * 子线程循环任务
     */
    public void doLoopInThread(String name, long delay, ThreadLoopTasker threadLoopTasker) {
        if (delay < 0) {
            throw new NullPointerException("delay must be bigger than zero");
        }
        if (TextUtils.isEmpty(name)) {
            throw new NullPointerException("name must be not empty");
        }
        if (threadLoopTasker != null) {
            ThreadLoopThread threadLoopThread = mThreadLoopThreadMap.get(name);
            if (threadLoopThread == null) {
                threadLoopThread = new ThreadLoopThread(name, delay, threadLoopTasker);
                mThreadLoopThreadMap.put(name, threadLoopThread);
                threadLoopThread.start();
            } else {
                Log.i(TAG, "the" + name + " loop thread is running");
            }
        }
    }

    /**
     * 取消循环任务
     */
    public void cancelWhichLoopInThread(String name) {
        if (TextUtils.isEmpty(name)) {
            throw new NullPointerException("name must be not empty");
        }
        try {
            ThreadLoopThread threadLoopThread = mThreadLoopThreadMap.get(name);
            if (threadLoopThread != null) {
                threadLoopThread.interrupt();
                mThreadLoopThreadMap.remove(name);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 销毁(Activity或APP退出时调用)
     */
    public void cancelAllLoopInThread() {
        try {
            Iterator<Map.Entry<String, ThreadLoopThread>> iterator = mThreadLoopThreadMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ThreadLoopThread> next = iterator.next();
                ThreadLoopThread threadLoopThread = next.getValue();
                threadLoopThread.interrupt();
                iterator.remove();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void cancelOnceInThread(String name) {
        try {
            Iterator<Map.Entry<String, ThreadTask>> iterator = mThreadTaskMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ThreadTask> next = iterator.next();
                if (name.equals(next.getKey())) {
                    ThreadTask threadLoopThread = next.getValue();
                    threadLoopThread.interrupt();
                    iterator.remove();
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void cancelOnceInThreadMain(String name) {
        try {
            Iterator<Map.Entry<String, ThreadMainTask>> iterator = mThreadMainTaskMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ThreadMainTask> next = iterator.next();
                if (name.equals(next.getKey())) {
                    ThreadMainTask threadLoopThread = next.getValue();
                    threadLoopThread.interrupt();
                    iterator.remove();
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 销毁(Activity或APP退出时调用)
     */
    public void cancelAllOnceInThread() {
        if (mMainHandler != null) {
            mMainHandler.removeCallbacksAndMessages(null);
            mMainHandler = null;
        }
        try {
            Iterator<Map.Entry<String, ThreadTask>> iterator = mThreadTaskMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ThreadTask> next = iterator.next();
                ThreadTask threadLoopThread = next.getValue();
                threadLoopThread.interrupt();
                iterator.remove();
            }

            Iterator<Map.Entry<String, ThreadMainTask>> iterator1 = mThreadMainTaskMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ThreadMainTask> next = iterator1.next();
                ThreadMainTask threadLoopThread = next.getValue();
                threadLoopThread.interrupt();
                iterator.remove();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public interface ThreadTasker {
        /**
         * 子线程执行
         */
        void taskInThread();

        /**
         * 是否执行发生异常(回调在子线程)
         *
         * @param isHappenException true 是 false 否
         */
        void taskInThreadFinish(boolean isHappenException);
    }

    public interface MainTasker {
        /**
         * 主线程执行
         */
        void taskInMain();

        /**
         * 是否执行发生异常(回调在主线程)
         *
         * @param isHappenException true 是 false 否
         */
        void taskInMainFinish(boolean isHappenException);
    }

    public interface ThreadMainTasker {
        /**
         * 子线程执行
         *
         * @return 传给主线程的结果
         */
        Object taskInThread();

        /**
         * 是否执行发生异常(回调在子线程)
         *
         * @param isHappenException true 是 false 否
         */
        void taskInThreadFinish(boolean isHappenException);

        /**
         * 主线程执行
         *
         * @param object 接收子线程的结果
         */
        void taskInMain(Object object);

        /**
         * 是否执行发生异常(回调在主线程)
         *
         * @param isHappenException true 是 false 否
         */
        void taskInMainFinish(boolean isHappenException);
    }

    public interface ThreadLoopTasker {
        /**
         * 子线程循环执行
         */
        void taskInThreadLoop();

        /**
         * 循环执行是否发生异常
         *
         * @param happenException 是否异常
         * @param loopNumber      循环到第几次
         */
        void taskInThreadStatus(boolean happenException, int loopNumber);
    }

    private static class ThreadTask extends Thread {
        private ThreadTasker mThreadTasker;
        private String mName;

        public ThreadTask(String name, ThreadTasker listener) {
            mName = name;
            mThreadTasker = listener;
        }

        @Override
        public void run() {
            try {
                mThreadTasker.taskInThread();
                mThreadTasker.taskInThreadFinish(false);
            } catch (Throwable e) {
                mThreadTasker.taskInThreadFinish(true);
                e.printStackTrace();
            } finally {
                mThreadTasker = null;
                TaskManager.getInstance().cancelOnceInThread(mName);
            }
        }
    }

    private static class ThreadMainTask extends Thread {
        private ThreadMainTasker mThreadMainTasker;
        private long mDelay;
        private String mName;

        public ThreadMainTask(String name, long delay, ThreadMainTasker listener) {
            mName = name;
            mDelay = delay;
            mThreadMainTasker = listener;
        }

        @Override
        public void run() {
            try {
                Object o = mThreadMainTasker.taskInThread();
                Message message = Message.obtain();
                message.what = THREAD_TO_MAIN;
                message.obj = new ThreadMainTaskerData(mThreadMainTasker, o);
                if (mMainHandler != null) {
                    mMainHandler.sendMessageDelayed(message, mDelay);
                }
                mThreadMainTasker.taskInThreadFinish(false);
            } catch (Throwable e) {
                mThreadMainTasker.taskInThreadFinish(true);
                Message message = Message.obtain();
                message.what = THREAD_TO_MAIN;
                message.obj = new ThreadMainTaskerData(mThreadMainTasker, null);
                if (mMainHandler != null) {
                    mMainHandler.sendMessageDelayed(message, mDelay);
                }
                e.printStackTrace();
            } finally {
                mThreadMainTasker = null;
                TaskManager.getInstance().cancelOnceInThreadMain(mName);
            }
        }
    }

    private static class ThreadLoopThread extends Thread {
        private ThreadLoopTasker mThreadLoopTasker;
        private long mDelay;
        private int mNumber;
        private String mName;

        public ThreadLoopThread(String name, long delay, ThreadLoopTasker threadLoopTasker) {
            mName = name;
            mDelay = delay;
            mThreadLoopTasker = threadLoopTasker;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    mNumber++;
                    mThreadLoopTasker.taskInThreadLoop();
                    mThreadLoopTasker.taskInThreadStatus(false, mNumber);
                    if (isInterrupted()) {
                        break;
                    }
                    sleep(mDelay);
                }
            } catch (Throwable e) {
                mThreadLoopTasker.taskInThreadStatus(true, mNumber);
                e.printStackTrace();
            } finally {
                mThreadLoopTasker = null;
                TaskManager.getInstance().cancelWhichLoopInThread(mName);
            }
        }
    }

    private static class ThreadMainTaskerData {
        private ThreadMainTasker mThreadMainTasker;
        private Object mObject;

        public ThreadMainTaskerData(ThreadMainTasker threadMainTasker, Object object) {
            mThreadMainTasker = threadMainTasker;
            mObject = object;
        }

        public ThreadMainTasker getListener() {
            return mThreadMainTasker;
        }

        public Object getObject() {
            return mObject;
        }

        public void clear() {
            mThreadMainTasker = null;
            mObject = null;
        }
    }
}
