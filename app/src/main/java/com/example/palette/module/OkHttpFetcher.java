package com.example.palette.module;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.util.ContentLengthInputStream;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttpFetcher implements DataFetcher<InputStream> {
    private static final String TAG = "OkHttpFetcher";
    GlideUrl mGlideUrl;
    volatile boolean isCancelled;
    OkHttpClient mOkHttpClient;
    ResponseBody mResponseBody;
    InputStream mInputStream;
    public OkHttpFetcher(OkHttpClient okHttpClient,GlideUrl mGlideUrl) {
        this.mOkHttpClient = okHttpClient;
        this.mGlideUrl = mGlideUrl;
    }

    @Override
    public void loadData(Priority priority,DataCallback<? super InputStream> callback) {
        Request.Builder builder = new Request.Builder().url(mGlideUrl.toStringUrl());
        for(Map.Entry<String,String> headerEntry:mGlideUrl.getHeaders().entrySet()){
            String key = headerEntry.getKey();
            builder.addHeader(key,headerEntry.getValue());
        }
        Request request = builder.build();
        if(isCancelled){
            return;
        }
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            mResponseBody = response.body();
            if(!response.isSuccessful() || mResponseBody==null){
                throw new IOException("Request failed with code: "+response.code());
            }
            mInputStream = ContentLengthInputStream.obtain(mResponseBody.byteStream(), mResponseBody.contentLength());
            callback.onDataReady(mInputStream);
        }catch (Exception e){
            callback.onLoadFailed(e);
        }
    }

    @Override
    public void cleanup() {
        try {
            if(mInputStream!=null){
                mInputStream.close();
            }
            if(mResponseBody!=null){
                mResponseBody.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void cancel() {
        isCancelled = true;
    }
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @NotNull
    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }
}
