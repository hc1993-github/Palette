package com.example.palette.util;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
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
import java.util.Date;

public class LogUtil {
    private static final String TAG = "LogUtil";
    private static final int MSEC = 1;
    private static final int SEC = 1000;
    private static final int MIN = 60000;
    private static final int HOUR = 3600000;
    private static final int DAY = 86400000;
    private static final int DEFAULT_SIZE = 3;
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
    private volatile static String LOG_PATH;
    private volatile LogReader mLogReader;
    private int mPid;
    private int mSize;
    private int mUnit;
    private int mLevel;
    private volatile int mSuffix = 0;
    private static Context mContext;
    private static SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat fullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static Date simpleDate = new Date();
    private static Date fullDate = new Date();

    private LogUtil(Context context, int... size) {
        mContext = context.getApplicationContext();
        if (size != null && size.length > 1) {
            mSize = size[0];
            mUnit = size[1];
        } else {
            mSize = DEFAULT_SIZE;
            mUnit = DAY;
        }
        init();
        mPid = android.os.Process.myPid();
    }

    private void init() {
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    String[] split = mContext.getPackageName().split("\\.");
                    LOG_PATH = split[split.length - 1] + File.separator + "log";
                }
                //自动清除3天前日志
                File file = new File(Environment.getExternalStorageDirectory(), LOG_PATH);
                if (!file.exists()) {
                    file.mkdirs();
                }
                if (file.exists() && file.isDirectory()) {
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
                }
            } else {
                String[] splits = mContext.getPackageName().split("\\.");
                LOG_PATH = splits[splits.length - 1] + File.separator + "log";
                //自动清除3天前日志
                Uri uri = MediaStore.Files.getContentUri("external");
                ContentResolver contentResolver = mContext.getContentResolver();
                for (int i = 1; i <= mSize; i++) {
                    String preLog = date2String(getDate(new Date(), -i, mUnit), "yyyy-MM-dd");
                    Cursor cursor = contentResolver.query(uri, null, MediaStore.Downloads.DISPLAY_NAME + " like?", new String[]{preLog + "%" + ".log"}, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        Uri queryUri = ContentUris.withAppendedId(uri, cursor.getLong(25));
                        contentResolver.delete(queryUri, null, null);
                    } else {
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            LOG_PATH = null;
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

    public static LogUtil getInstance(Context context, int... size) {
        if (INSTANCE == null) {
            synchronized (LogUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LogUtil(context, size);
                }
            }
        }
        return INSTANCE;
    }

    public void start(int level) {
        mLevel = level;
        if (mLogReader == null) {
            mSuffix = 0;
            mLogReader = new LogReader(String.valueOf(mPid), LOG_PATH, level);
            mLogReader.start();
        }
    }

    public void stop() {
        if (mLogReader != null) {
            mSuffix = 0;
            mLogReader.stoplog();
            mLogReader = null;
        }
    }

    public static void logv(String msg) {
        if (mContext == null) {
            Log.v(TAG, getFullDate() + VERBOSE + msg);
        } else {
            Log.v(mContext.getPackageName(), getFullDate() + VERBOSE + msg);
        }
    }

    public static void logd(String msg) {
        if (mContext == null) {
            Log.d(TAG, getFullDate() + DEBUG + msg);
        } else {
            Log.d(mContext.getPackageName(), getFullDate() + DEBUG + msg);
        }
    }

    public static void logi(String msg) {
        if (mContext == null) {
            Log.i(TAG, getFullDate() + INFO + msg);
        } else {
            Log.i(mContext.getPackageName(), getFullDate() + INFO + msg);
        }
    }

    public static void logw(String msg) {
        if (mContext == null) {
            Log.w(TAG, getFullDate() + WARN + msg);
        } else {
            Log.w(mContext.getPackageName(), getFullDate() + WARN + msg);
        }
    }

    public static void loge(String msg) {
        if (mContext == null) {
            Log.e(TAG, getFullDate() + ERROR + msg);
        } else {
            Log.e(mContext.getPackageName(), getFullDate() + ERROR + msg);
        }
    }

    public static void logStackTrace(Throwable e) {
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

    private String getSimpleDate() {
        simpleDate.setTime(System.currentTimeMillis());
        return simpleFormat.format(simpleDate);
    }

    private static String getFullDate() {
        fullDate.setTime(System.currentTimeMillis());
        return fullFormat.format(fullDate);
    }

    private class LogReader extends Thread {
        private Process process;
        private BufferedReader bufferedReader;
        private boolean isRunning = true;
        private String cmds;
        private String mPID;
        private OutputStream fos = null;
        private InputStream fis = null;

        public LogReader(String pid, String dir, int level) {
            mPID = pid;
            if (dir != null) {
                initFos(dir);
                if (level == 1) {
                    cmds = "logcat *:v | logcat *:d | logcat *:i | logcat *:w | logcat *:e | grep \"(" + mPID + ")\"";
                } else if (level == 2) {
                    cmds = "logcat *:d | logcat *:i | logcat *:w | logcat *:e | grep \"(" + mPID + ")\"";
                } else if (level == 3) {
                    cmds = "logcat *:i | logcat *:w | logcat *:e | grep \"(" + mPID + ")\"";
                } else if (level == 4) {
                    cmds = "logcat *:w | logcat *:e | grep \"(" + mPID + ")\"";
                } else if (level == 5) {
                    cmds = "logcat *:e | grep \"(" + mPID + ")\"";
                } else {
                    cmds = "logcat | grep \"(" + mPID + ")\"";
                }
            }
        }

        private void initFos(String dir) {
            try {
                while (true) {
                    if (fos != null) {
                        fos.close();
                        fos = null;
                    }
                    if (fis != null) {
                        fis.close();
                        fis = null;
                    }
                    String name;
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                        if (mSuffix != 0) {
                            name = getSimpleDate() + "-" + mSuffix + ".log";
                        } else {
                            name = getSimpleDate() + ".log";
                        }
                        File file = new File(Environment.getExternalStorageDirectory() + File.separator + dir, name);
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        fis = new FileInputStream(file);
                        int originFileSizeB = fis.available();
                        int originFileSizeMB;
                        if (originFileSizeB > 1024) {
                            originFileSizeMB = originFileSizeB / 1024 / 1024;
                            if (originFileSizeMB >= 1) {
                                mSuffix++;
                                continue;
                            }
                        }
                        fos = new FileOutputStream(file, true);
                        break;
                    } else {
                        if (fos != null) {
                            fos.close();
                            fos = null;
                        }
                        if (fis != null) {
                            fis.close();
                            fis = null;
                        }
                        Uri uri = MediaStore.Files.getContentUri("external");
                        ContentResolver contentResolver = mContext.getContentResolver();
                        ContentValues contentValues = new ContentValues();
                        if (mSuffix != 0) {
                            name = getSimpleDate() + "-" + mSuffix + ".log";
                        } else {
                            name = getSimpleDate() + ".log";
                        }
                        contentValues.put(MediaStore.Downloads.RELATIVE_PATH, dir);
                        contentValues.put(MediaStore.Downloads.DISPLAY_NAME, name);
                        contentValues.put(MediaStore.Downloads.TITLE, name);
                        Cursor cursor = contentResolver.query(uri, null, MediaStore.Downloads.DISPLAY_NAME + "=?", new String[]{name}, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            Uri queryUri = ContentUris.withAppendedId(uri, cursor.getLong(25));
                            fis = contentResolver.openInputStream(queryUri);
                            int originFileSizeB = fis.available();
                            int originFileSizeMB;
                            if (originFileSizeB > 1024) {
                                originFileSizeMB = originFileSizeB / 1024 / 1024;
                                if (originFileSizeMB >= 1) {
                                    mSuffix++;
                                    cursor.close();
                                    continue;
                                }
                            }
                            fos = contentResolver.openOutputStream(queryUri);
                            cursor.close();
                            break;
                        } else {
                            Uri insert = contentResolver.insert(uri, contentValues);
                            if (insert != null) {
                                fos = contentResolver.openOutputStream(insert);
                                fis = contentResolver.openInputStream(insert);
                            }
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void stoplog() {
            isRunning = false;
        }

        @Override
        public void run() {
            try {
                if (TextUtils.isEmpty(cmds)) {
                    return;
                }
                process = Runtime.getRuntime().exec(cmds);
                bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()), 1024);
                String line;
                while (isRunning) {
                    line = bufferedReader.readLine();
                    if (line == null || line.length() == 0) {
                        continue;
                    }
                    int originFileSizeB = fis.available();
                    int originFileSizeMB;
                    if (originFileSizeB > 1024) {
                        originFileSizeMB = originFileSizeB / 1024 / 1024;
                        if (originFileSizeMB >= 1) {
                            LogUtil.this.stop();
                            LogUtil.this.start(mLevel);
                            return;
                        }
                    }
                    if (fos != null) {
                        if ((line.contains(mPID) && (line.contains(TAG) || (mContext != null && line.contains(mContext.getPackageName()))))
                                || (mContext != null && line.contains(mContext.getPackageName()))
                                || line.contains(".java:")
                                || line.contains("Error")
                                || line.contains("error")
                                || line.contains("Exception")
                                || line.contains("exception")
                                || line.contains("Android")
                                || line.contains("android")
                                || line.contains("Google")
                                || line.contains("google")) {
                            fos.write((getSimpleDate() + " " + line + "\n").getBytes());
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
