package com.hc;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SerialPortHelper {
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;
    private HandlerThread mSendThread;
    private ReadThread mReadThread;
    private Handler mSendHandler;
    private boolean mStart;
    private SerialPortListener mListener;
    private static final int OPEN_NO_PERMISSION = -1;
    private static final int OPEN_FAIL = -2;

    static {
        System.loadLibrary("serial_port");
    }

    public SerialPortHelper(File device, int baudrate, SerialPortListener listener) {
        mListener = listener;
        if (!device.canRead() || !device.canWrite()) {
            try {
                Process su;
                su = Runtime.getRuntime().exec("/system/bin/su");
                String cmd = "chmod 777 " + device.getAbsolutePath() + "\n"
                        + "exit\n";
                su.getOutputStream().write(cmd.getBytes());
                if ((su.waitFor() != 0) || !device.canRead()
                        || !device.canWrite()) {
                    mListener.onFail(device, OPEN_NO_PERMISSION);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                mListener.onFail(device, OPEN_FAIL);
            }
        }
        mFd = open(device.getAbsolutePath(), baudrate, 0);
        if (mFd == null) {
            mListener.onFail(device, OPEN_FAIL);
            return;
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
        mListener.onSuccess(device);
        mStart = true;
        openSendThread();
        openReadThread();
    }

    private native static FileDescriptor open(String path, int baudrate, int flags);

    private native void close();

    public void closeAll() {
        if (mFd != null) {
            close();
            mFd = null;
        }
        closeSendThread();
        closeReadThread();
        if (mFileInputStream != null) {
            try {
                mFileInputStream.close();
                mFileInputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mFileOutputStream != null) {
            try {
                mFileOutputStream.close();
                mFileOutputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean sendData(byte[] data) {
        if (mFileOutputStream != null && mSendHandler != null && mFd != null) {
            Message message = Message.obtain();
            message.obj = data;
            return mSendHandler.sendMessage(message);
        }
        return false;
    }

    private void openSendThread() {
        mSendThread = new HandlerThread("mSendThread");
        mSendThread.start();
        mSendHandler = new Handler(mSendThread.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                byte[] sendBytes = (byte[]) msg.obj;
                if (null != mFileOutputStream && null != sendBytes && 0 < sendBytes.length) {
                    try {
                        mFileOutputStream.write(sendBytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private void closeSendThread() {
        mSendHandler.removeCallbacksAndMessages(null);
        mSendHandler = null;
        if (null != mSendThread) {
            mSendThread.interrupt();
            mSendThread.quit();
            mSendThread = null;
        }
    }

    private void openReadThread() {
        mReadThread = new ReadThread("mReadThread");
        mReadThread.start();
    }

    private void closeReadThread() {
        if (mReadThread != null) {
            mReadThread.interrupt();
        }
    }

    private class ReadThread extends HandlerThread {
        public ReadThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            try {
                while (mStart) {
                    if (mFileInputStream == null) {
                        return;
                    }
                    byte[] buffer = new byte[1024];
                    int size = mFileInputStream.read(buffer);
                    if (size > 0 && mListener != null) {
                        mListener.onReadData(buffer);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface SerialPortListener {
        void onSuccess(File device);

        void onFail(File device, int failFlag);

        void onReadData(byte[] data);
    }
}
