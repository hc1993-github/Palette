package com.hc.scopestore.base;

import java.io.File;

public class BaseRequest {
    private File file;
    private String type;

    public BaseRequest(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
