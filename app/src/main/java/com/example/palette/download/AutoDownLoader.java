package com.example.palette.download;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AutoDownLoader {
    private static AutoDownLoader mInstance;
    private static final int mConnectTimeout = 5;
    private static final int mReadTimeout = 5;
    private static final int mWriteTimeout = 5;
    private static final String NEW_ERROR_INFO = "网络连接异常,下载失败";
    private static final String SERVER_ERROR_INFO = "服务器响应异常,下载失败";
    private static final String UNKNOWN_ERROR_INFO = "未知错误,下载失败";
    private static final String EXISTED_ERROR_INFO = "文件已存在,重新下载";
    private static final String FILE_LENGTH_ERROR_INFO = "文件大小异常,下载失败";
    private volatile boolean mIsPause = true;
    private volatile boolean mIsCancel = false;
    private volatile boolean mIsEmptyed = false;
    private volatile boolean mIsBreakPoint = true;
    private AutoProgressListener mListener;
    private File mDestFile;
    private String mUrl;
    private String mDestFileAbsolutePath;
    private final Context mContext;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -1:
                case 0:
                    resume();
                    break;
                case 1:
                    mListener.start(msg.arg1);
                    break;
                case 2:
                    mListener.progress(msg.arg1);
                    break;
                case 3:
                    mListener.pause(msg.arg1);
                    break;
                case 4:
                    mListener.cancel();
                    break;
                case 5:
                    mListener.finish(mDestFile.getAbsolutePath());
                    break;
                case 6:
                    mListener.error((String) msg.obj);
                    break;
            }
        }
    };

    public static AutoDownLoader getInstance(Context context){
        if (mInstance == null) {
            synchronized (AutoDownLoader.class) {
                if (mInstance == null) {
                    mInstance = new AutoDownLoader(context);
                }
            }
        }
        return mInstance;
    }

    private AutoDownLoader(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * 下载
     * @param url 地址
     * @param listener 监听器
     * @param destFileAbsolutePath 为空则默认/sdcard/applicationId后缀名/
     * @param isBreakPoint 是否断点下载
     */
    public void download(String url, AutoProgressListener listener, String destFileAbsolutePath, boolean isBreakPoint){
        mIsBreakPoint = isBreakPoint;
        if(mIsBreakPoint){
            breakPointDownLoad(url, listener, destFileAbsolutePath);
        }else {
            commonDownLoad(url, listener, destFileAbsolutePath);
        }
    }

    /**
     * 断点下载
     * @param url 地址
     * @param listener 监听器
     * @param destFileAbsolutePath 文件绝对路径
     */
    private void breakPointDownLoad(String url, AutoProgressListener listener, String destFileAbsolutePath){
        try {
            mUrl = url;
            mIsPause = false;
            mIsCancel = false;
            mListener = listener;
            mDestFileAbsolutePath = destFileAbsolutePath;
            if (destFileAbsolutePath == null) {
                String[] split = mContext.getPackageName().split("\\.");
                File file = new File(Environment.getExternalStorageDirectory(),split[split.length-1]+"");
                if(!file.exists()){
                    file.mkdirs();
                }
                mDestFile = new File(file.getAbsolutePath(),url.substring(url.lastIndexOf("/") + 1));
            } else {
                mDestFile = new File(destFileAbsolutePath);
            }
            final int currentlength = (int) mDestFile.length();
            if(currentlength==0){
                if(!mIsEmptyed){
                    new Thread(){
                        @Override
                        public void run() {
                            AutoFileUtil.deleteZipAndApk(mContext);
                            mIsEmptyed = true;
                            sendMessage(0,null);
                        }
                    }.start();
                    return;
                }
            }else {
                mIsEmptyed = false;
            }
            final RandomAccessFile randomAccessFile = new RandomAccessFile(mDestFile, "rwd");
            Request request = new Request.Builder().header("RANGE", "bytes=" + currentlength + "-").url(url).build();
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(mConnectTimeout, TimeUnit.SECONDS)
                    .readTimeout(mReadTimeout,TimeUnit.SECONDS)
                    .writeTimeout(mWriteTimeout,TimeUnit.SECONDS);
            OkHttpClient client = builder.build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    sendMessage(6,NEW_ERROR_INFO);
                }

                @Override
                public void onResponse(Call call, Response response){
                    try {
                        if(response.isSuccessful()){
                            long filetotalsize = response.body().contentLength() + currentlength;
                            sendMessage(1, null,(int) filetotalsize);
                            sendMessage(2, null,(int)(((float)currentlength/filetotalsize)*100));
                            randomAccessFile.seek(currentlength);
                            InputStream is = response.body().byteStream();
                            byte[] buf = new byte[1024*4];
                            int readlength;
                            int currentreadlength = currentlength;
                            while ((readlength = is.read(buf)) != -1) {
                                if (mIsCancel) {
                                    sendMessage(4,null);
                                    closeResources(randomAccessFile, is, response.body());
                                    deleteFile(mDestFile);
                                    return;
                                }
                                if (mIsPause) {
                                    sendMessage(3, null,currentreadlength);
                                    closeResources(randomAccessFile, is, response.body());
                                    return;
                                }
                                randomAccessFile.write(buf, 0, readlength);
                                currentreadlength += readlength;
                                sendMessage(2, null,(int)(((float)currentreadlength/filetotalsize)*100));
                                randomAccessFile.seek(currentreadlength);
                            }
                            closeResources(randomAccessFile, is, response.body());
                            sendDelayMessage(5,null,1000);
                        }else {
                            int code = response.code();
                            if(code==416){
                                String fileFullName = mDestFile.getAbsolutePath().substring(mDestFile.getAbsolutePath().lastIndexOf("/")+1);
                                String fileSubName = fileFullName.substring(0, fileFullName.lastIndexOf("."));
                                AutoFileUtil.deleteSimilarFile(mContext,fileSubName);
                                sendMessage(-1,EXISTED_ERROR_INFO);
                            }else {
                                sendMessage(6,SERVER_ERROR_INFO);
                            }
                        }
                    }catch (Exception e){
                        sendMessage(6,NEW_ERROR_INFO);
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            sendMessage(6,UNKNOWN_ERROR_INFO);
            e.printStackTrace();
        }
    }

    /**
     * 普通下载
     * @param url
     * @param listener
     * @param destFileAbsolutePath
     */
    private void commonDownLoad(String url, AutoProgressListener listener, String destFileAbsolutePath){
        try {
            mUrl = url;
            mIsPause = false;
            mIsCancel = false;
            mListener = listener;
            mDestFileAbsolutePath = destFileAbsolutePath;
            if (destFileAbsolutePath == null) {
                String[] split = mContext.getPackageName().split("\\.");
                File file = new File(Environment.getExternalStorageDirectory(),split[split.length-1]+"");
                if(!file.exists()){
                    file.mkdirs();
                }
                mDestFile = new File(file.getAbsolutePath(),url.substring(url.lastIndexOf("/") + 1));
            } else {
                mDestFile = new File(destFileAbsolutePath);
            }
            if(!mIsEmptyed){
                new Thread(){
                    @Override
                    public void run() {
                        AutoFileUtil.deleteZipAndApk(mContext);
                        mIsEmptyed = true;
                        sendMessage(0,null);
                    }
                }.start();
                return;
            }
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(mConnectTimeout, TimeUnit.SECONDS)
                    .readTimeout(mReadTimeout,TimeUnit.SECONDS)
                    .writeTimeout(mWriteTimeout,TimeUnit.SECONDS);
            builder.addInterceptor(new AutoProgressInterceptor(mListener));
            OkHttpClient client = builder.build();
            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call,IOException e) {
                    sendMessage(6,NEW_ERROR_INFO);
                }

                @Override
                public void onResponse( Call call,Response response) throws IOException {
                    FileOutputStream fileOutputStream = null;
                    InputStream stream = null;
                    try {
                        if(response.isSuccessful()){
                            long contentLength = response.body().contentLength();
                            sendMessage(1, null, (int) contentLength);
                            stream = response.body().byteStream();
                            File file = new File(mDestFile.getAbsolutePath());
                            fileOutputStream = new FileOutputStream(file);
                            byte[] buffer = new byte[1024 * 8];
                            int length;
                            while ((length = stream.read(buffer)) != -1) {
                                if (mIsCancel || mIsPause) {
                                    sendMessage(4,null);
                                    closeResources(fileOutputStream);
                                    deleteFile(mDestFile);
                                    return;
                                }
                                fileOutputStream.write(buffer, 0, length);
                                fileOutputStream.flush();
                            }
                            if (mDestFile.length() == contentLength) {
                                sendDelayMessage(5,null,1000);
                            }else {
                                sendMessage(6,FILE_LENGTH_ERROR_INFO);
                            }
                        }else {
                            sendMessage(6,SERVER_ERROR_INFO);
                        }
                    }catch (Exception e){
                        sendMessage(6,NEW_ERROR_INFO);
                        e.printStackTrace();
                    }finally {
                        try {
                            if (fileOutputStream != null) {
                                fileOutputStream.close();
                            }
                            if (stream != null) {
                                stream.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }catch (Exception e){
            sendMessage(6,UNKNOWN_ERROR_INFO);
            e.printStackTrace();
        }
    }

    /**
     * 流关闭
     *
     * @param closeables
     */
    private void closeResources(Closeable... closeables) {
        try {
            int length = closeables.length;
            for (int i = 0; i < length; i++) {
                closeables[i].close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件删除
     *
     * @param files
     */
    private void deleteFile(File... files) {
        try {
            int length = files.length;
            for (int i = 0; i < length; i++) {
                if (files[i].isDirectory()) {
                    for (File f : files[i].listFiles()) {
                        f.delete();
                    }
                    files[i].delete();
                } else {
                    files[i].delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停下载(断点不删除文件,普通删除文件)
     */
    public void pause() {
        mIsPause = true;
    }

    /**
     * 恢复下载
     */
    public void resume() {
        if(!TextUtils.isEmpty(mUrl)){
            download(mUrl,mListener,mDestFileAbsolutePath,mIsBreakPoint);
        }
    }

    /**
     * 取消下载(删除文件)
     */
    public void cancel() {
        mIsCancel = true;
        mIsPause = true;
    }

    /**
     * 断点下载时是否已暂停或已取消
     *
     * @return
     */
    public boolean isPause() {
        return mIsPause;
    }

    /**
     * 往handler中发消息
     * @param type
     * @param arg1
     */
    private void sendMessage(int type, String str,int... arg1) {
        Message message = Message.obtain();
        message.what = type;
        if (arg1.length != 0) {
            message.arg1 = arg1[0];
        }
        if(str!=null){
            message.obj = str;
        }
        mHandler.sendMessage(message);
    }

    /**
     * 往handler中发延迟消息
     * @param type
     * @param str
     * @param delayTime
     * @param arg1
     */
    private void sendDelayMessage(int type, String str,long delayTime,int... arg1) {
        Message message = Message.obtain();
        message.what = type;
        if (arg1.length != 0) {
            message.arg1 = arg1[0];
        }
        if(str!=null){
            message.obj = str;
        }
        mHandler.sendMessageDelayed(message,delayTime);
    }
}
