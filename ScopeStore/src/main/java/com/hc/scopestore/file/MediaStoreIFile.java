package com.hc.scopestore.file;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.hc.scopestore.annotation.FileField;
import com.hc.scopestore.base.BaseRequest;
import com.hc.scopestore.base.BaseResponse;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    public <T extends BaseRequest> BaseResponse add(Context context, T request) {
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

    @Override
    public <T extends BaseRequest> BaseResponse delete(Context context, T request) {
        Uri uri = query(context, request).getUri();
        ContentResolver contentResolver = context.getContentResolver();
        int i = contentResolver.delete(uri, null, null);
        BaseResponse baseResponse = new BaseResponse();
        if(i>=1){
            baseResponse.setSuccess(true);
        }else {
            baseResponse.setSuccess(false);
        }
        return baseResponse;
    }

    @Override
    public <T extends BaseRequest> BaseResponse query(Context context, T request) {
        Uri uri = map.get(request.getType());
        ContentValues contentValues = request2Values(request);
        Condition condition = new Condition(contentValues);
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, new String[]{"_id"}, condition.whereCasue, condition.whereArgs, null);
        Uri queryUri=null;
        if(cursor!=null && cursor.moveToFirst()){
            queryUri = ContentUris.withAppendedId(uri,cursor.getLong(25));
            cursor.close();
        }
        BaseResponse baseResponse = new BaseResponse();
        if(queryUri!=null){
            baseResponse.setSuccess(true);
            baseResponse.setUri(queryUri);
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

    private class Condition{
        private String whereCasue;
        private String[] whereArgs;
        public Condition( ContentValues contentValues) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("1=1");
            ArrayList list = new ArrayList();
            Iterator<Map.Entry<String, Object>> set= contentValues.valueSet().iterator() ;
            while (set.hasNext()) {
                Map.Entry<String, Object> entry=set.next();
                String key = entry.getKey();
                String value = (String) entry.getValue();
                if (value != null) {
                    stringBuilder.append(" and " + key + " =? ");
                    list.add(value);
                }
            }
            whereCasue = stringBuilder.toString();
            whereArgs = (String[]) list.toArray(new String[list.size()]);
        }
    }
}
