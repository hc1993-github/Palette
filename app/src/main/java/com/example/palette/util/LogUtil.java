package com.example.palette.util;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {
    private static LogUtil INSTANCE = null;
    private static String LOG_PATH;
    private LogReader mLogReader = null;
    private int mPid;
    public static final int MSEC = 1;
    public static final int SEC = 1000;
    public static final int MIN = 60000;
    public static final int HOUR = 3600000;
    public static final int DAY = 86400000;
    private static Context mContext;
    private LogUtil(Context context) {
        this.mContext = context;
        init();
        mPid = android.os.Process.myPid();
    }

    private void init() {
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String[] split = mContext.getPackageName().split("\\.");
                LOG_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+split[split.length-1]+File.separator+"log";
            } else {
                LOG_PATH = mContext.getFilesDir().getAbsolutePath() + File.separator + "log";
            }
            File file = new File(LOG_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
            //删除30天日志
            if (file.exists() && file.isDirectory()) {
                File[] files = file.listFiles();
                String days = date2String(getDate(new Date(), -30, DAY), "yyyy-MM-dd");
                if (files != null) {
                    for (File f : files) {
                        if (f.getName().compareTo(days) < 0) {
                            f.delete();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public static LogUtil getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LogUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LogUtil(context);
                }
            }
        }
        return INSTANCE;
    }

    //开始输出日志
    public void start() {
        if (mLogReader == null) {
            mLogReader = new LogReader(String.valueOf(mPid), LOG_PATH);
            mLogReader.start();
        }
    }

    //停止输出日志
    public void stop() {
        if (mLogReader != null) {
            mLogReader.stoplog();
            mLogReader = null;
        }
    }

    public static void log(String msg) {
        Log.i(mContext.getPackageName(), getFullDate()+" "+msg);
    }

    private static String getSimpleDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(new Date(System.currentTimeMillis()));
    }

    private static String getFullDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date(System.currentTimeMillis()));
    }

    private class LogReader extends Thread {
        private Process process;
        private BufferedReader bufferedReader;
        private boolean isRunning = true;
        String cmds = null;
        private String mPID;
        private FileOutputStream fos = null;

        public LogReader(String pid, String dir) {
            mPID = pid;
            try {
                fos = new FileOutputStream(new File(dir, getSimpleDate() + ".log"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            cmds = "logcat | grep \"(" + mPID + ")\"";
        }

        public void stoplog() {
            isRunning = false;
        }

        @Override
        public void run() {
            try {
                process = Runtime.getRuntime().exec(cmds);
                bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()), 1024);
                String line = null;
                while (isRunning && (line = bufferedReader.readLine()) != null) {
                    if (line.length() == 0) {
                        continue;
                    }
                    if (fos != null && line.contains(mPID)) {
                        fos.write((getSimpleDate() + " " + line + "\n").getBytes());
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
            }
        }
    }
}
