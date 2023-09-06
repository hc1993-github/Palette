package com.example.palette.download;

public interface AutoProgressListener {

    void start(int totalSize);

    void progress(int progress);

    void pause(int progress);

    void cancel(int progress);

    void finish(String fileAbsolutePath);

    void error(int code,String info);

}
