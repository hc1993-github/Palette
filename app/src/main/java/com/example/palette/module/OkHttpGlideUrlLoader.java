package com.example.palette.module;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

import okhttp3.OkHttpClient;

public class OkHttpGlideUrlLoader implements ModelLoader<GlideUrl, InputStream> {
    private static final String TAG = "OkHttpGlideUrlLoader";
    private OkHttpClient mOkHttpClient;
    public OkHttpGlideUrlLoader(OkHttpClient okHttpClient) {
        this.mOkHttpClient = okHttpClient;
    }

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(GlideUrl glideUrl, int width, int height,Options options) {
        return new LoadData<>(glideUrl,new OkHttpFetcher(mOkHttpClient,glideUrl));
    }

    @Override
    public boolean handles(@NonNull @NotNull GlideUrl glideUrl) {
        return true;
    }
    public static class Factory implements ModelLoaderFactory<GlideUrl, InputStream>{
        private OkHttpClient client;
        public Factory() {
        }

        public Factory(OkHttpClient mOkHttpClient) {
            this.client = mOkHttpClient;
        }

        public OkHttpClient getmOkHttpClient() {
            if(client==null){
                client = new OkHttpClient();
            }
            return client;
        }
        
        @Override
        public ModelLoader<GlideUrl, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new OkHttpGlideUrlLoader(getmOkHttpClient());
        }

        @Override
        public void teardown() {

        }
    }
}
