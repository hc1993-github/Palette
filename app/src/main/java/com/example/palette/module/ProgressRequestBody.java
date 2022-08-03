package com.example.palette.module;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * MultipartBody.Builder builder = new MultipartBody.Builder();
 * builder.addFormDataPart("","", RequestBody.create(new File(""),MediaType.parse("")));
 * ProgressRequestBody progressRequestBody = new ProgressRequestBody(builder.build(), progress -> {
 *
 * });
 * Request request = new Request.Builder().post(progressRequestBody).build();
 */
public class ProgressRequestBody extends RequestBody {
    RequestBody mRequestBody;
    ProgressListener mListener;

    public ProgressRequestBody(RequestBody mRequestBody, ProgressListener mListener) {
        this.mRequestBody = mRequestBody;
        this.mListener = mListener;
    }

    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
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
        ProgressListener listener;
        public ProgressSink(Sink delegate,long totalLength,ProgressListener ls) {
            super(delegate);
            mTotalLength = totalLength;
            listener = ls;
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            mCurrentLength+=byteCount;
            if(listener!=null){
                listener.onProgress((int)(100f*mCurrentLength/mTotalLength));
            }
            super.write(source, byteCount);
        }
    }
}
