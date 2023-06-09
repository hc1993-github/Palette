package com.hc.scopestore.file;

import android.content.Context;

import com.hc.scopestore.base.BaseRequest;
import com.hc.scopestore.base.BaseResponse;

public class CommonIFile implements IFile {
    @Override
    public <T extends BaseRequest> BaseResponse create(Context context, T request) {
        return null;
    }
}
