package com.example.palette.module;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * ProgressInterceptor.addListener(mData.get(position), new ProgressListener() {
 *  @Override
 *  public void onProgress(int progress) {
 *     Log.d("adapter1", mData.get(position)+" onProgress: "+progress);
 *  }
 *  });
 *  Glide.with(context).load(mData.get(position)).into(new DrawableImageViewTarget(holder.textView){
 *   @Override
 *   public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
 *      super.onResourceReady(resource, transition);
 *      ProgressInterceptor.removeListener(mData.get(position));
 *   }
 *  });
 */
public class ProgressResponseBody extends ResponseBody {
    ProgressListener progressListener;
    ResponseBody responseBody;
    BufferedSource bufferedSource;
    public ProgressResponseBody(String url,ResponseBody responseBody) {
        this.responseBody = responseBody;
        progressListener = ProgressInterceptor.listenerMap.get(url);
    }

    public ProgressResponseBody(ResponseBody responseBody,ProgressListener progressListener) {
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
                progressListener.onProgress(progress);
            }
            if(progressListener!=null && totalBytesRead==totalLength){
                progressListener = null;
            }
            currentProgress = progress;
            return bytesRead;
        }
    }
}
