package com.hc.autoupdater;

public interface AutoProgressListener {

    void start(int totalsize);

    void progress(int progress);

    void end(String destFilePath);

    void error(String message);

}
