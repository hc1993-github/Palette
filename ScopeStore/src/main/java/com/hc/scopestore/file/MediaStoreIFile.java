package com.hc.scopestore.file;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.hc.scopestore.annotation.FileField;
import com.hc.scopestore.base.BaseRequest;
import com.hc.scopestore.base.BaseResponse;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MediaStoreIFile implements IFile {
    static MediaStoreIFile instance;
    public static final String VIDEO = "video";
    public static final String AUDIO = "audio";
    public static final String PICTURE = "picture";
    public static final String DOWNLOAD = "download";
    public static final String MP3 = ".mp3";
    public static final String WAV = ".wav";
    public static final String JPG = ".jpg";
    public static final String PNG = ".png";
    public static final String MP4 = ".mp4";
    public static final String AVI = ".avi";
    public static final String RMVB = ".rmvb";
    Map<String, Uri> map = new HashMap<>();
    public static MediaStoreIFile getInstance() {
        if(instance==null){
            synchronized (MediaStoreIFile.class){
                if(instance==null){
                    instance = new MediaStoreIFile();
                }
            }
        }
        return instance;
    }

    private MediaStoreIFile() {
        map.put(VIDEO, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        map.put(AUDIO, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        map.put(PICTURE, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        map.put(DOWNLOAD, MediaStore.Downloads.EXTERNAL_CONTENT_URI);
    }

    @Override
    public <T extends BaseRequest> BaseResponse create(Context context, T request) {
        Uri uri = map.get(request.getType());
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = request2Values(request);
        Uri insertUri = contentResolver.insert(uri, contentValues);
        BaseResponse baseResponse = new BaseResponse();
        if(insertUri!=null){
            baseResponse.setSuccess(true);
            baseResponse.setUri(insertUri);
        }else {
            baseResponse.setSuccess(false);
            baseResponse.setUri(null);
        }
        return baseResponse;
    }

    private <T extends BaseRequest> ContentValues request2Values(T request) {
        ContentValues contentValues = new ContentValues();
        Class<? extends BaseRequest> clazz = request.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field:fields){
            FileField fileField = field.getAnnotation(FileField.class);
            if(fileField==null){
                continue;
            }
            String annotationValue = fileField.value();
            String fieldName = field.getName();
            char firstChar = Character.toUpperCase(fieldName.charAt(0));
            String methodName = "get"+firstChar+fieldName.substring(1);
            String fieldValue = null;
            try {
                Method method = clazz.getMethod(methodName);
                fieldValue = (String) method.invoke(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(!TextUtils.isEmpty(annotationValue) && !TextUtils.isEmpty(fieldValue)){
                contentValues.put(annotationValue,fieldValue);
            }
        }
        return contentValues;
    }
}
