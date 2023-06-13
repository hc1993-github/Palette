package com.hc.scopestore.file;

import android.content.Context;

import com.hc.scopestore.base.BaseRequest;
import com.hc.scopestore.base.BaseResponse;

public interface IFile {
    <T extends BaseRequest> BaseResponse add(Context context, T request);
    <T extends BaseRequest> BaseResponse delete(Context context, T request);
    <T extends BaseRequest> BaseResponse query(Context context, T request);
}
