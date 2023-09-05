package com.example.palette.download;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class AutoProgressRequestBody extends RequestBody {
    RequestBody mRequestBody;
    AutoProgressListener mListener;

    public AutoProgressRequestBody(RequestBody mRequestBody, AutoProgressListener mListener) {
        this.mRequestBody = mRequestBody;
        this.mListener = mListener;
    }

    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink bufferedSink) throws IOException {
        long totalLength = contentLength();
        BufferedSink buffer = Okio.buffer(new ProgressSink(bufferedSink, totalLength,mListener));
        mRequestBody.writeTo(buffer);
        buffer.flush();
    }
    private class ProgressSink extends ForwardingSink{
        long mTotalLength;
        long mCurrentLength;
        AutoProgressListener listener;
        public ProgressSink(Sink delegate, long totalLength, AutoProgressListener ls) {
            super(delegate);
            mTotalLength = totalLength;
            listener = ls;
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            mCurrentLength+=byteCount;
            if(listener!=null){
                listener.progress((int)(100f*mCurrentLength/mTotalLength));
            }
            super.write(source, byteCount);
        }
    }
}
