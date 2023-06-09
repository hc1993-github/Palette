package com.hc.scopestore.file;

import android.content.Context;

import com.hc.scopestore.base.BaseRequest;
import com.hc.scopestore.base.BaseResponse;

public interface IFile {
    <T extends BaseRequest> BaseResponse create(Context context, T request);
}
