package com.example.palette.module;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;

import okhttp3.OkHttpClient;

/**
 * 自定义Glide模块
 */
public class CustomGlideModule implements GlideModule {


    private static final String TAG = "CustomGlideModule";

    /**
     * 更改配置
     * @param context
     * @param builder
     */
    @Override
    public void applyOptions(Context context,GlideBuilder builder) {
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context));
    }

    /**
     * 更改组件
     * @param context
     * @param glide
     * @param registry
     */
    @Override
    public void registerComponents(Context context,Glide glide,Registry registry) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new ProgressInterceptor());
        OkHttpClient client = builder.build();
        registry.replace(GlideUrl.class, InputStream.class,new OkHttpGlideUrlLoader.Factory(client));
    }
}
