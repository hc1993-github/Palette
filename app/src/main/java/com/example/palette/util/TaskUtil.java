package com.example.palette.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TaskUtil {
    private volatile static TaskUtil mInstance;
    private static Handler mMainHandler;
    private static final int MAIN = 10000;
    private static final int THREAD_TO_MAIN = 10001;
    private static final int DEFAULT_THREAD_TO_MAIN_FAIL = -1;
    private List<ThreadTask> mThreadTaskList = new ArrayList<>();
    private List<ThreadMainTask> mThreadMainTaskList = new ArrayList<>();
    private Map<String, ThreadLoopThread> mThreadLoopThreadMap = new ConcurrentHashMap<>();

    public static TaskUtil getInstance() {
        if (mInstance == null) {
            synchronized (TaskUtil.class) {
                if (mInstance == null) {
                    mInstance = new TaskUtil();
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
                            mainTasker.taskInMain();
                        }
                        break;
                    case THREAD_TO_MAIN:
                        if (mMainHandler != null) {
                            Data data = (Data) msg.obj;
                            ThreadMainTasker threadMainTasker = data.getListener();
                            threadMainTasker.taskInMain(data.getSuccess(), data.getObject());
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
    public void doOnceInThread(ThreadTasker threadTasker) {
        if (threadTasker != null) {
            ThreadTask threadTask = new ThreadTask(threadTasker);
            mThreadTaskList.add(threadTask);
            threadTask.start();
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
    public void doOnceInThreadInMain(ThreadMainTasker threadMainListener) {
        doOnceInThreadDelayOnceInMain(0, threadMainListener);
    }

    /**
     * 先在子线程执行一次,延时时间后,再在主线程执行一次
     */
    public void doOnceInThreadDelayOnceInMain(long delay, ThreadMainTasker threadMainTasker) {
        if (threadMainTasker != null && mMainHandler != null) {
            ThreadMainTask threadMainTask = new ThreadMainTask(delay, threadMainTasker);
            mThreadMainTaskList.add(threadMainTask);
            threadMainTask.start();
        }
    }

    /**
     * 子线程循环任务
     */
    public void doLoopInThread(String name, long delay, ThreadLoopTasker threadLoopTasker) {
        if (threadLoopTasker != null) {
            ThreadLoopThread threadLoopThread = mThreadLoopThreadMap.get(name);
            if (threadLoopThread == null) {
                threadLoopThread = new ThreadLoopThread(delay, threadLoopTasker);
                mThreadLoopThreadMap.put(name, threadLoopThread);
            }
            threadLoopThread.start();
        }
    }

    /**
     * 取消循环任务
     */
    public void cancelWhichLoopInThread(String name) {
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

    /**
     * 销毁(Activity或APP退出时调用)
     */
    public void destroy() {
        if (mMainHandler != null) {
            mMainHandler.removeCallbacksAndMessages(null);
            mMainHandler = null;
        }
        try {
            Iterator<ThreadTask> taskIterator = mThreadTaskList.iterator();
            while (taskIterator.hasNext()) {
                ThreadTask threadTask = taskIterator.next();
                threadTask.interrupt();
                taskIterator.remove();
            }
            Iterator<ThreadMainTask> mainTaskIterator = mThreadMainTaskList.iterator();
            while (mainTaskIterator.hasNext()) {
                ThreadMainTask threadMainTask = mainTaskIterator.next();
                threadMainTask.interrupt();
                mainTaskIterator.remove();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public interface ThreadTasker {
        void taskInThread();
    }

    public interface MainTasker {
        void taskInMain();
    }

    public interface ThreadMainTasker {
        Object taskInThread();

        void taskInMain(boolean threadNoException, Object object);
    }

    public interface ThreadLoopTasker {
        void taskInThreadLoop(int loopNumber);
    }

    private static class ThreadTask extends Thread {
        private ThreadTasker mThreadTasker;

        public ThreadTask(ThreadTasker listener) {
            mThreadTasker = listener;
        }

        @Override
        public void run() {
            try {
                mThreadTasker.taskInThread();
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                mThreadTasker = null;
            }
        }
    }

    private static class ThreadMainTask extends Thread {
        private ThreadMainTasker mThreadMainTasker;
        private long mDelay;

        public ThreadMainTask(long delay, ThreadMainTasker listener) {
            mDelay = delay;
            mThreadMainTasker = listener;
        }

        @Override
        public void run() {
            try {
                Object o = mThreadMainTasker.taskInThread();
                Message message = Message.obtain();
                message.what = THREAD_TO_MAIN;
                message.obj = new Data(mThreadMainTasker, true, o);
                if (mMainHandler != null) {
                    mMainHandler.sendMessageDelayed(message, mDelay);
                }
            } catch (Throwable e) {
                Message message = Message.obtain();
                message.what = THREAD_TO_MAIN;
                message.obj = new Data(mThreadMainTasker, false, DEFAULT_THREAD_TO_MAIN_FAIL);
                if (mMainHandler != null) {
                    mMainHandler.sendMessageDelayed(message, mDelay);
                }
                e.printStackTrace();
            } finally {
                mThreadMainTasker = null;
            }
        }
    }

    private static class ThreadLoopThread extends Thread {
        private ThreadLoopTasker mThreadLoopTasker;
        private long mDelay;
        private int mNumber;
        public ThreadLoopThread(long delay, ThreadLoopTasker threadLoopTasker) {
            mDelay = delay;
            mThreadLoopTasker = threadLoopTasker;
        }

        @Override
        public void run() {
            try {
                while (!isInterrupted()) {
                    mNumber++;
                    mThreadLoopTasker.taskInThreadLoop(mNumber);
                    sleep(mDelay);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private static class Data {
        private ThreadMainTasker mThreadMainTasker;
        private boolean mThreadNoException;
        private Object mObject;

        public Data(ThreadMainTasker threadMainTasker, boolean threadNoException, Object object) {
            mThreadMainTasker = threadMainTasker;
            mThreadNoException = threadNoException;
            mObject = object;
        }

        public ThreadMainTasker getListener() {
            return mThreadMainTasker;
        }

        public boolean getSuccess() {
            return mThreadNoException;
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
