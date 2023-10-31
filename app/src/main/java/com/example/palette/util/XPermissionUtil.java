package com.example.palette.util;

import android.Manifest;
import android.content.Context;

import androidx.fragment.app.Fragment;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XPermissionUtil {

    private static Map<String, String> map = new HashMap<>();

    static {
        map.put(Manifest.permission.CAMERA, "相机");
        map.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "写入外部存储");
        map.put(Manifest.permission.READ_EXTERNAL_STORAGE, "读取外部存储");
        map.put(Manifest.permission.ACCESS_NETWORK_STATE, "获取网络状态");
        map.put(Manifest.permission.CALL_PHONE, "拨打电话");
        map.put(Manifest.permission.READ_CONTACTS, "读取联系人");
        map.put(Manifest.permission.RECORD_AUDIO, "麦克风");
        map.put(Manifest.permission.SEND_SMS, "发送短信");
        map.put(Manifest.permission.READ_SMS, "读取短信");
    }

    public static void checkPermission(Context context, Fragment fragment, List<String> requestPermissions, XPermissionListener listener) {
        if (context == null && fragment == null) {
            throw new NullPointerException("context and fragment must at least one can not be null");
        }
        boolean granted = XXPermissions.isGranted(context == null ? fragment.getActivity() : context, requestPermissions);
        List<String> requestNoGrantedList;
        if (!granted) {
            requestNoGrantedList = XXPermissions.getDenied(context == null ? fragment.getActivity() : context, requestPermissions);
            List<String> requestBeforeGrantedList = new ArrayList<>();
            for (String request : requestPermissions) {
                if (!requestNoGrantedList.contains(request)) {
                    requestBeforeGrantedList.add(request);
                }
            }
            XXPermissions xxPermissions;
            if (context != null) {
                xxPermissions = XXPermissions.with(context);
            } else {
                xxPermissions = XXPermissions.with(fragment);
            }
            xxPermissions.permission(requestNoGrantedList).request(new OnPermissionCallback() {
                @Override
                public void onGranted(List<String> permissions, boolean allGranted) {
                    if (allGranted) {
                        listener.permissionGranted(true, requestPermissions, permissionConvert(map, requestPermissions));
                    }
                }

                @Override
                public void onDenied(List<String> permissions, boolean doNotAskAgain) {
                    List<String> requestDeniedList = XXPermissions.getDenied(context == null ? fragment.getActivity() : context, requestPermissions);
                    List<String> requestGrantedList = new ArrayList<>();
                    for (String request : requestPermissions) {
                        if (!requestDeniedList.contains(request)) {
                            requestGrantedList.add(request);
                        }
                    }
                    if (!requestGrantedList.isEmpty()) {
                        listener.permissionGranted(false, requestGrantedList, permissionConvert(map, requestGrantedList));
                    }
                    if (doNotAskAgain) {
                        listener.permissionForeverDenied(requestDeniedList, permissionConvert(map, requestDeniedList));
                    } else {
                        listener.permissionDenied(requestDeniedList, permissionConvert(map, requestDeniedList));
                    }
                }
            });
        } else {
            listener.permissionGranted(true, requestPermissions, permissionConvert(map, requestPermissions));
        }
    }

    private static List<String> permissionConvert(Map<String, String> dest, List<String> src) {
        List<String> result = new ArrayList<>();
        for (String s : src) {
            String d = dest.get(s);
            result.add(d);
        }
        return result;
    }

    public interface XPermissionListener {
        void permissionGranted(boolean allGranted, List<String> grantedList, List<String> grantedChineseList);

        void permissionDenied(List<String> deniedList, List<String> deniedChineseList);

        void permissionForeverDenied(List<String> deniedList, List<String> deniedChineseList);
    }
}
