package com.hc.scopestore.common;

import android.os.Environment;

import com.hc.scopestore.base.BaseRequest;
import com.hc.scopestore.file.CommonIFile;
import com.hc.scopestore.file.IFile;
import com.hc.scopestore.file.MediaStoreIFile;

public class FileOperater {

    public static IFile getIFile(BaseRequest request){
        if(Environment.isExternalStorageLegacy()){
            return new CommonIFile();
        }else {
            dealRequest(request);
            return MediaStoreIFile.getInstance();
        }
    }

    private static void dealRequest(BaseRequest request){
        String path = request.getFile().getAbsolutePath();
        if(path.endsWith(MediaStoreIFile.MP3) || path.endsWith(MediaStoreIFile.WAV)){
            request.setType(MediaStoreIFile.AUDIO);
        }else if(path.endsWith(MediaStoreIFile.MP4) || path.endsWith(MediaStoreIFile.AVI) || path.endsWith(MediaStoreIFile.RMVB)){
            request.setType(MediaStoreIFile.VIDEO);
        }else if(path.endsWith(MediaStoreIFile.JPG) || path.endsWith(MediaStoreIFile.PNG)){
            request.setType(MediaStoreIFile.PICTURE);
        }else {
            request.setType(MediaStoreIFile.DOWNLOAD);
        }
    }
}
