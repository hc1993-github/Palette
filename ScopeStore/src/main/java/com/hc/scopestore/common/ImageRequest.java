package com.hc.scopestore.common;

import android.provider.MediaStore;


import com.hc.scopestore.annotation.FileField;
import com.hc.scopestore.base.BaseRequest;

import java.io.File;

public class ImageRequest extends BaseRequest {

    public ImageRequest(File file) {
        super(file);
    }

    @FileField(MediaStore.Downloads.DISPLAY_NAME)
    private String displayName;
    @FileField(MediaStore.Downloads.RELATIVE_PATH)
    private String path;
    @FileField(MediaStore.Downloads.TITLE)
    private String title;
    @FileField(MediaStore.Downloads.MIME_TYPE)
    private String mimeType;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
