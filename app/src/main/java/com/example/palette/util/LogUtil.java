package com.example.palette.util;


import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class LogUtil {
    private static final String TAG = "LogUtil";
    private static final int SEC = 1000;
    private static final int MIN = 60000;
    private static final int HOUR = 3600000;
    private static final int DAY = 86400000;
    private static final int DEFAULT_SIZE = 7;
    private static final int DEFAULT_PART_SIZE = 1024;
    public static final int LEVEL_ALL = 0;
    public static final int LEVEL_VERBOSE = 1;
    public static final int LEVEL_DEBUG = 2;
    public static final int LEVEL_INFO = 3;
    public static final int LEVEL_WARN = 4;
    public static final int LEVEL_ERROR = 5;
    private static final String VERBOSE = " 追踪:";
    private static final String DEBUG = " 调试:";
    private static final String INFO = " 信息:";
    private static final String WARN = " 警告:";
    private static final String ERROR = " 异常:";
    private static LogUtil INSTANCE;
    private static String LOG_PATH;
    private volatile static LogReader mLogReader;
    private static int mPid;
    private static int mSize;
    private static int mUnit;
    private static int mLevel = -1;
    private static int mSuffix = 0;
    private static Context mContext;
    private static boolean mDeleteFile;
    private static SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat fullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static Date simpleDate = new Date();
    private static Date fullDate = new Date();

    private LogUtil() {

    }

    public static LogUtil getInstance() {
        if (INSTANCE == null) {
            synchronized (LogUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LogUtil();
                }
            }
        }
        return INSTANCE;
    }

    public void startRecord(Context context, int... params) {
        if (context == null) {
            mContext = null;
        } else {
            mContext = context.getApplicationContext();
        }
        if (params != null) {
            if (params.length == 1) {
                if (params[0] < 1) {
                    mSize = DEFAULT_SIZE;
                } else {
                    mSize = params[0];
                }
                mUnit = DAY;
                mLevel = LEVEL_ALL;
            } else if (params.length == 2) {
                if (params[0] < 1) {
                    mSize = DEFAULT_SIZE;
                } else {
                    mSize = params[0];
                }
                if (params[1] < SEC) {
                    mUnit = DAY;
                } else {
                    mUnit = params[1];
                }
                mLevel = LEVEL_ALL;
            } else if (params.length >= 3) {
                if (params[0] < 1) {
                    mSize = DEFAULT_SIZE;
                } else {
                    mSize = params[0];
                }
                if (params[1] < SEC) {
                    mUnit = DAY;
                } else {
                    mUnit = params[1];
                }
                if (params[2] < LEVEL_ALL) {
                    mLevel = LEVEL_ALL;
                } else {
                    mLevel = params[2];
                }
            }
        } else {
            mSize = DEFAULT_SIZE;
            mUnit = DAY;
            mLevel = LEVEL_ALL;
        }
        createLogPath();
        mPid = android.os.Process.myPid();
        mSuffix = 0;
        mLogReader = new LogReader(String.valueOf(mPid), LOG_PATH, mLevel);
        mLogReader.start();
    }

    public void endRecord() {
        if (mLogReader != null) {
            mLogReader.stoplog();
            mLogReader = null;
        }
    }

    private void createLogPath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String[] split = mContext.getPackageName().split("\\.");
            LOG_PATH = split[split.length - 1] + File.separator + "log";
            //自动清除日志
            File file;
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                file = new File(Environment.getExternalStorageDirectory(), LOG_PATH);
            } else {
                file = new File(mContext.getExternalCacheDir(), LOG_PATH);
            }
            autoClearLog(file);
        } else {
            LOG_PATH = null;
            Log.e(TAG, getFullDate() + ERROR + " your device may not have sdcard");
        }
    }

    private void autoClearLog(File file) {
        if (!file.exists()) {
            file.mkdirs();
            return;
        }
        if (file.isDirectory()) {
            try {
                File[] files = file.listFiles();
                String days = date2String(getDate(new Date(), -mSize, mUnit), "yyyy-MM-dd");
                if (files != null) {
                    for (File f : files) {
                        String name = f.getName();
                        if (name.length() > 10) {
                            name = name.substring(0, 10);
                        }
                        if (name.compareTo(days) < 0) {
                            f.delete();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String date2String(Date date, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    private static Date getDate(Date date, long timeSpan, int unit) {
        return millis2Date(date2Millis(date) + timeSpan2Millis(timeSpan, unit));
    }

    private static long date2Millis(Date date) {
        return date.getTime();
    }

    private static Date millis2Date(long millis) {
        return new Date(millis);
    }

    private static long timeSpan2Millis(long timeSpan, int unit) {
        return timeSpan * unit;
    }

    private static void checkLogReader() {
        if (LOG_PATH == null) {
            Log.e(TAG, getFullDate() + ERROR + " you may not excute method startRecord , that cause log file not record");
            return;
        }
        if (mLogReader == null) {
            Log.e(TAG, getFullDate() + ERROR + " you may not excute method startRecord or you may excute method endRecord, that cause log file not record anymore");
            return;
        }
        if (mDeleteFile) {
            Log.e(TAG, getFullDate() + ERROR + " you may delete log file , that cause log file not record anymore");
        }
    }

    public static void logvRecord(String msg) {
        checkLogReader();
        if (mContext == null) {
            Log.v(TAG, getFullDate() + VERBOSE + msg);
        } else {
            Log.v(mContext.getPackageName(), getFullDate() + VERBOSE + msg);
        }
    }

    public static void logv(String msg){
        Log.v(TAG, getFullDate() + VERBOSE + msg);
    }

    public static void logdRecord(String msg) {
        checkLogReader();
        if (mContext == null) {
            Log.d(TAG, getFullDate() + DEBUG + msg);
        } else {
            Log.d(mContext.getPackageName(), getFullDate() + DEBUG + msg);
        }
    }

    public static void logd(String msg){
        Log.d(TAG, getFullDate() + DEBUG + msg);
    }

    public static void logiRecord(String msg) {
        checkLogReader();
        if (mContext == null) {
            Log.i(TAG, getFullDate() + INFO + msg);
        } else {
            Log.i(mContext.getPackageName(), getFullDate() + INFO + msg);
        }
    }

    public static void logi(String msg){
        Log.i(TAG, getFullDate() + INFO + msg);
    }

    public static void logwRecord(String msg) {
        checkLogReader();
        if (mContext == null) {
            Log.w(TAG, getFullDate() + WARN + msg);
        } else {
            Log.w(mContext.getPackageName(), getFullDate() + WARN + msg);
        }
    }

    public static void logw(String msg){
        Log.w(TAG, getFullDate() + WARN + msg);
    }

    public static void logeRecord(String msg) {
        checkLogReader();
        if (mContext == null) {
            Log.e(TAG, getFullDate() + ERROR + msg);
        } else {
            Log.e(mContext.getPackageName(), getFullDate() + ERROR + msg);
        }
    }

    public static void loge(String msg){
        Log.e(TAG, getFullDate() + ERROR + msg);
    }

    public static void logStackTraceRecord(Throwable e) {
        if (e != null) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            Throwable[] throwables = e.getSuppressed();
            Throwable cause = e.getCause();
            for (StackTraceElement traceElement : stackTrace) {
                logeRecord("\tat " + traceElement);
            }
            for (Throwable se : throwables) {
                logeRecord("\tat " + se.toString());
            }
            if (cause != null) {
                logeRecord("\tat " + cause.toString());
            }
        }
    }

    public static void logStackTrace(Throwable e){
        if (e != null) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            Throwable[] throwables = e.getSuppressed();
            Throwable cause = e.getCause();
            for (StackTraceElement traceElement : stackTrace) {
                loge("\tat " + traceElement);
            }
            for (Throwable se : throwables) {
                loge("\tat " + se.toString());
            }
            if (cause != null) {
                loge("\tat " + cause.toString());
            }
        }
    }

    private static String getSimpleDate() {
        simpleDate.setTime(System.currentTimeMillis());
        return simpleFormat.format(simpleDate);
    }

    private static String getFullDate() {
        fullDate.setTime(System.currentTimeMillis());
        return fullFormat.format(fullDate);
    }

    private static class LogReader extends Thread {
        private Process process;
        private BufferedReader bufferedReader;
        private boolean isStop;
        private String cmds;
        private String mPID;
        private OutputStream fos;
        private InputStream fis;
        private Pattern mPattern = Pattern.compile("(^.*([0-9]{4})-([0-9]{2})-([0-9]{2}) 00:00:00)");
        private File file;
        private List<String> tagList = new ArrayList<>();
        public LogReader(String pid, String dir, int level) {
            mPID = pid;
            try {
                while (true) {
                    String name;
                    if (mSuffix != 0) {
                        name = getSimpleDate() + "-" + mSuffix + ".log";
                    } else {
                        name = getSimpleDate() + ".log";
                    }
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                        file = new File(Environment.getExternalStorageDirectory() + File.separator + dir, name);
                    } else {
                        file = new File(mContext.getExternalCacheDir() + File.separator + dir, name);
                    }
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    fis = new FileInputStream(file);
                    int originFileSizeB = fis.available();
                    if (originFileSizeB > 1024) {
                        int originFileSizeMB = originFileSizeB / 1024 / 1024;
                        if (originFileSizeMB >= DEFAULT_PART_SIZE) { //每超过多少兆,生成后缀如2023-01-01-1.log
                            mSuffix++;
                            continue;
                        }
                    }
                    fos = new FileOutputStream(file, true);
                    break;
                }
            } catch (Exception e) {
                fos = null;
                e.printStackTrace();
            }
            if (level == LEVEL_VERBOSE) {
                cmds = "logcat *:v | logcat *:d | logcat *:i | logcat *:w | logcat *:e | grep \"(" + mPID + ")\"";
            } else if (level == LEVEL_DEBUG) {
                cmds = "logcat *:d | logcat *:i | logcat *:w | logcat *:e | grep \"(" + mPID + ")\"";
            } else if (level == LEVEL_INFO) {
                cmds = "logcat *:i | logcat *:w | logcat *:e | grep \"(" + mPID + ")\"";
            } else if (level == LEVEL_WARN) {
                cmds = "logcat *:w | logcat *:e | grep \"(" + mPID + ")\"";
            } else if (level == LEVEL_ERROR) {
                cmds = "logcat *:e | grep \"(" + mPID + ")\"";
            } else {
                cmds = "logcat | grep \"(" + mPID + ")\"";
            }
            addFilterates();
        }

        private void addFilterates() {
            tagList.add(".java:");
            tagList.add("Error");
            tagList.add("error");
            tagList.add("Exception");
            tagList.add("exception");
            tagList.add("Google");
            tagList.add("google");
            //native信号
//            tagList.add("SIGHUP");//1挂起
//            tagList.add("SIGINT");//2中断
//            tagList.add("SIGQUIT");//3退出
            tagList.add("SIGILL");//4非法指令
            tagList.add("SIGTRAP");//5跟踪中断
            tagList.add("SIGABRT");//6放弃
//            tagList.add("SIGIOT");//6IOT trap
            tagList.add("SIGBUS");//7非法地址
            tagList.add("SIGFPE");//8浮点异常
//            tagList.add("SIGKILL");//9删除
//            tagList.add("SIGUSR1");//10用户信号1
            tagList.add("SIGSEGV");//11访问非法地址
//            tagList.add("SIGUSR2");//12用户信号2
//            tagList.add("SIGPIPE");//13管道错误
//            tagList.add("SIGALRM");//14时钟定时
//            tagList.add("SIGTERM");//15程序结束
//            tagList.add("SIGSTKFLT");//16Stack fault.
//            tagList.add("SIGCLD");//SIGCHLD Same as SIGCHLD (System V)..
//            tagList.add("SIGCHLD");//17Child status has changed (POSIX).
//            tagList.add("SIGCONT");//18Stop, unblockable (POSIX).
//            tagList.add("SIGSTOP");//19Stop, unblockable (POSIX).
//            tagList.add("SIGTSTP");//20Keyboard stop (POSIX).
//            tagList.add("SIGTTIN");//21Background read from tty (POSIX).
//            tagList.add("SIGTTOU");//22Background write to tty (POSIX).
//            tagList.add("SIGURG");//23Urgent condition on socket (4.2 BSD).
//            tagList.add("SIGXCPU");//24CPU limit exceeded (4.2 BSD).
//            tagList.add("SIGXFSZ");//25File size limit exceeded (4.2 BSD).
//            tagList.add("SIGVTALRM");//26Virtual alarm clock (4.2 BSD).
//            tagList.add("SIGPROF");//27Profiling alarm clock (4.2 BSD).
//            tagList.add("SIGWINCH");//28Window size change (4.3 BSD, Sun).
//            tagList.add("SIGPOLL");//SIGIO Pollable event occurred (System V).
//            tagList.add("SIGIO");//29Same as SIGCHLD (System V).
//            tagList.add("SIGPWR");//30Power failure restart (System V).
//            tagList.add("SIGSYS");//31Bad system call.
//            tagList.add("SIGUNUSED");//31
        }

        private boolean hitFilterate(String text){
            for (int i = 0; i < tagList.size(); i++) {
                if(text.contains(tagList.get(i))){
                    return true;
                }
            }
            return false;
        }

        private void stoplog() {
            isStop = true;
        }

        private boolean isZero(String text) {
            try {
                return mPattern.matcher(text).find();
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public void run() {
            try {
                process = Runtime.getRuntime().exec(cmds);
                bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()), 1024);
                String line;
                while (!isStop) {
                    line = bufferedReader.readLine();
                    if (line == null || line.length() == 0) {
                        continue;
                    }
                    if (file == null || !file.exists()) {
                        mDeleteFile = true;
                        break;
                    }
                    mDeleteFile = false;
                    long originFileSizeB = fis.available();
                    if (originFileSizeB > 1024) {
                        long originFileSizeMB = originFileSizeB / 1024 / 1024;
                        if (originFileSizeMB >= DEFAULT_PART_SIZE) {
                            break;
                        }
                    }
                    if (fos != null) {
                        if ((line.contains(mPID) && (line.contains(TAG) || (mContext != null && line.contains(mContext.getPackageName()))))
                                || (mContext != null && line.contains(mContext.getPackageName()))
                                || hitFilterate(line)) {
                            fos.write((getSimpleDate() + " " + line + "\n").getBytes());
                            if (isZero(line)) {
                                break;
                            }
                        }
                    } else {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (process != null) {
                    process.destroy();
                    process = null;
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                        bufferedReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                        fos = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                        fis = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
