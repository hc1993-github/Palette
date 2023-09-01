package com.hc.autoupdater;

public interface AutoProgressListener {

    void start(int totalsize);

    void progress(int progress);

    void pause(int progress);

    void cancel();

    void finish(String fileAbsolutePath);

    void error(String message);

    void existed(String message);

}
