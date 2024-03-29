package com.example.palette.download;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class AutoProgressResponseBody extends ResponseBody {
    AutoProgressListener progressListener;
    ResponseBody responseBody;
    BufferedSource bufferedSource;

    public AutoProgressResponseBody(ResponseBody responseBody, AutoProgressListener progressListener) {
        this.progressListener = progressListener;
        this.responseBody = responseBody;
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public BufferedSource source() {
        if(bufferedSource==null){
            bufferedSource= Okio.buffer(new ProgressSource(responseBody.source()));
        }
        return bufferedSource;
    }
    private class ProgressSource extends ForwardingSource{
        long totalBytesRead =0;
        int currentProgress;
        public ProgressSource(Source source) {
            super(source);
        }

        @Override
        public long read( Buffer sink, long byteCount) throws IOException {
            long bytesRead = super.read(sink, byteCount);
            long totalLength = responseBody.contentLength();
            if(bytesRead==-1){
                totalBytesRead = totalLength;
            }else {
                totalBytesRead+=bytesRead;
            }
            int progress = (int)(100f*totalBytesRead/totalLength);
            if(progressListener!=null && progress!=currentProgress){
                progressListener.progress(progress);
            }
            if(progressListener!=null && totalBytesRead==totalLength){
                progressListener = null;
            }
            currentProgress = progress;
            return bytesRead;
        }
    }
}
