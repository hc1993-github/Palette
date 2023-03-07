package com.example.palette.util;

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palette.R;
import com.example.palette.adapter.BaseAdapter;
import com.example.palette.adapter.BaseHolder;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.dialog.RationaleDialog;
import com.permissionx.guolindev.request.ExplainScope;
import com.permissionx.guolindev.request.ForwardScope;
import com.permissionx.guolindev.request.PermissionBuilder;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionUtil {
    static Map<String, String> map = new HashMap<>();

    static {
        map.put(Manifest.permission.REQUEST_INSTALL_PACKAGES, "允许安装应用");
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

    public static void checkPermissionsWithDefaultDialog(FragmentActivity fragmentActivity,List<String> requestList,List<String> necessaryList, boolean forwardToSetting,PermissionListener listener) {
        PermissionBuilder permissionBuilder = PermissionX.init(fragmentActivity).permissions(requestList);
        permissionBuilder.onExplainRequestReason(new ExplainReasonCallback() {
            @Override
            public void onExplainReason(ExplainScope scope, List<String> deniedList) {
                List<String> current = null;
                for (String s : deniedList) {
                    if (necessaryList == null) {
                        current = deniedList;
                    } else {
                        if (necessaryList.contains(s)) {
                            if (current == null) {
                                current = new ArrayList<>();
                            }
                            current.add(s);
                        }
                    }
                }
                scope.showRequestReasonDialog(permissionConvert(map, current), "为保证应用正常运行,即将申请以下权限", "接受", "拒绝");
            }
        });
        if (forwardToSetting) {
            permissionBuilder.onForwardToSettings(new ForwardToSettingsCallback() {
                @Override
                public void onForwardToSettings(ForwardScope scope, List<String> deniedList) {
                    scope.showForwardToSettingsDialog(permissionConvert(map, deniedList), "您需要应用权限设置中手动开启权限!", "好的", "取消");
                }
            });
        }
        permissionBuilder.request(new RequestCallback() {
            @Override
            public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
                if (allGranted) {
                    listener.permissionAllGranted();
                } else {
                    listener.permissionSomeDenied(permissionConvert(map, deniedList));
                }
            }
        });
    }

    public static void checkPermissionsWithDefaultDialog(Fragment fragment,List<String> requestList,List<String> necessaryList, boolean forwardToSetting,PermissionListener listener) {
        PermissionBuilder permissionBuilder = PermissionX.init(fragment).permissions(requestList);
        permissionBuilder.onExplainRequestReason(new ExplainReasonCallback() {
            @Override
            public void onExplainReason(ExplainScope scope, List<String> deniedList) {
                List<String> current = null;
                for (String s : deniedList) {
                    if (necessaryList == null) {
                        current = deniedList;
                    } else {
                        if (necessaryList.contains(s)) {
                            if (current == null) {
                                current = new ArrayList<>();
                            }
                            current.add(s);
                        }
                    }
                }
                scope.showRequestReasonDialog(permissionConvert(map, current), "为保证应用正常运行,即将申请以下权限", "接受", "拒绝");
            }
        });
        if (forwardToSetting) {
            permissionBuilder.onForwardToSettings(new ForwardToSettingsCallback() {
                @Override
                public void onForwardToSettings(ForwardScope scope, List<String> deniedList) {
                    scope.showForwardToSettingsDialog(permissionConvert(map, deniedList), "您需要应用权限设置中手动开启权限!", "好的", "取消");
                }
            });
        }
        permissionBuilder.request(new RequestCallback() {
            @Override
            public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
                if (allGranted) {
                    listener.permissionAllGranted();
                } else {
                    listener.permissionSomeDenied(permissionConvert(map, deniedList));
                }
            }
        });
    }

    public static void checkPermissionsWithReasonDialog(Fragment fragment,List<String> permissions, boolean forwardToSetting,ReasonDialog reasonDialogExplain,ReasonDialog reasonDialogForward,PermissionListener listener) {
        PermissionBuilder permissionBuilder = PermissionX.init(fragment).permissions(permissions);
        permissionBuilder.onExplainRequestReason(new ExplainReasonCallback() {
            @Override
            public void onExplainReason(ExplainScope scope, List<String> deniedList) {
                if (reasonDialogExplain == null) {
                    scope.showRequestReasonDialog(new ReasonDialog.Builder()
                            .setContext(fragment.getContext())
                            .setLayoutId(R.layout.default_dialog_request_explain)
                            .setRightViewId(R.id.default_dialog_request_explain_positive)
                            .setLeftViewId(R.id.default_dialog_request_explain_negative)
                            .setReasonsRvId(R.id.default_dialog_request_explain_rv)
                            .setReasonItemLayoutId(R.layout.default_reason_explain_item)
                            .setReasonItemTvId(R.id.default_reason_explain_item_tv)
                            .setDeniedPermissionsChinese(permissionConvert(map, deniedList))
                            .setDeniedPermissions(deniedList)
                            .build());
                } else {
                    scope.showRequestReasonDialog(reasonDialogExplain);
                }
            }
        });
        if (forwardToSetting) {
            permissionBuilder.onForwardToSettings(new ForwardToSettingsCallback() {
                @Override
                public void onForwardToSettings(ForwardScope scope, List<String> deniedList) {
                    if (reasonDialogForward == null) {
                        scope.showForwardToSettingsDialog(new ReasonDialog.Builder()
                                .setContext(fragment.getContext())
                                .setLayoutId(R.layout.default_dialog_request_forward)
                                .setRightViewId(R.id.default_dialog_request_forward_positive)
                                .setLeftViewId(R.id.default_dialog_request_forward_negative)
                                .setReasonsRvId(R.id.default_dialog_request_forward_rv)
                                .setReasonItemLayoutId(R.layout.default_reason_forward_item)
                                .setReasonItemTvId(R.id.default_reason_forward_item_tv)
                                .setDeniedPermissionsChinese(permissionConvert(map, deniedList))
                                .setDeniedPermissions(deniedList)
                                .build());
                    } else {
                        scope.showForwardToSettingsDialog(reasonDialogForward);
                    }
                }
            });
        }
        permissionBuilder.request(new RequestCallback() {
            @Override
            public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
                if (allGranted) {
                    listener.permissionAllGranted();
                } else {
                    listener.permissionSomeDenied(permissionConvert(map, deniedList));
                }
            }
        });
    }

    /**
     * @param fragmentActivity
     * @param permissions  必须的权限
     * @param forwardToSetting  永久拒绝时是否转到设置页面
     * @param reasonDialogExplain  申请未同意时解释弹框
     * @param reasonDialogForward  永久拒绝时手动设置的页面弹框
     * @param listener
     */
    public static void checkPermissionsWithReasonDialog(FragmentActivity fragmentActivity,List<String> permissions, boolean forwardToSetting,ReasonDialog reasonDialogExplain,ReasonDialog reasonDialogForward,PermissionListener listener) {
        PermissionBuilder permissionBuilder = PermissionX.init(fragmentActivity).permissions(permissions);
        permissionBuilder.onExplainRequestReason(new ExplainReasonCallback() {
            @Override
            public void onExplainReason(ExplainScope scope, List<String> deniedList) {
                if (reasonDialogExplain == null) {
                    scope.showRequestReasonDialog(new ReasonDialog.Builder()
                            .setContext(fragmentActivity)
                            .setLayoutId(R.layout.default_dialog_request_explain)
                            .setRightViewId(R.id.default_dialog_request_explain_positive)
                            .setLeftViewId(R.id.default_dialog_request_explain_negative)
                            .setReasonsRvId(R.id.default_dialog_request_explain_rv)
                            .setReasonItemLayoutId(R.layout.default_reason_explain_item)
                            .setReasonItemTvId(R.id.default_reason_explain_item_tv)
                            .setDeniedPermissionsChinese(permissionConvert(map, deniedList))
                            .setDeniedPermissions(deniedList)
                            .build());
                } else {
                    scope.showRequestReasonDialog(reasonDialogExplain);
                }
            }
        });
        if (forwardToSetting) {
            permissionBuilder.onForwardToSettings(new ForwardToSettingsCallback() {
                @Override
                public void onForwardToSettings(ForwardScope scope, List<String> deniedList) {
                    if (reasonDialogForward == null) {
                        scope.showForwardToSettingsDialog(new ReasonDialog.Builder()
                                .setContext(fragmentActivity)
                                .setLayoutId(R.layout.default_dialog_request_forward)
                                .setRightViewId(R.id.default_dialog_request_forward_positive)
                                .setLeftViewId(R.id.default_dialog_request_forward_negative)
                                .setReasonsRvId(R.id.default_dialog_request_forward_rv)
                                .setReasonItemLayoutId(R.layout.default_reason_forward_item)
                                .setReasonItemTvId(R.id.default_reason_forward_item_tv)
                                .setDeniedPermissionsChinese(permissionConvert(map, deniedList))
                                .setDeniedPermissions(deniedList)
                                .build());
                    } else {
                        scope.showForwardToSettingsDialog(reasonDialogForward);
                    }
                }
            });
        }
        permissionBuilder.request(new RequestCallback() {
            @Override
            public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
                if (allGranted) {
                    listener.permissionAllGranted();
                } else {
                    listener.permissionSomeDenied(permissionConvert(map, deniedList));
                }
            }
        });
    }

    public static List<String> permissionConvert(Map<String, String> dest, List<String> src) {
        List<String> result = new ArrayList<>();
        for (String s : src) {
            String d = dest.get(s);
            result.add(d);
        }
        return result;
    }

    public interface PermissionListener {
        void permissionAllGranted();

        void permissionSomeDenied(List<String> deniedList);
    }

    public static class ReasonDialog extends RationaleDialog {
        private Builder builder;
        private View leftView;
        private View rightView;

        private ReasonDialog(Builder builder) {
            super(builder.context);
            this.builder = builder;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(builder.layoutId);
            Window window = getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setDimAmount(0.5f);
            window.setTitle(null);
            initViews();
        }

        protected void initViews() {
            leftView = findViewById(builder.leftViewId);
            rightView = findViewById(builder.rightViewId);
            RecyclerView recyclerView = findViewById(builder.reasonsRvId);
            if (recyclerView != null) {
                recyclerView.setLayoutManager(new LinearLayoutManager(builder.context));
                ReasonAdapter adapter = new ReasonAdapter(builder.context, builder.reasonItemLayoutId, builder.reasons, builder.reasonItemTextViewId);
                recyclerView.setAdapter(adapter);
            }
        }

        @Override
        public View getPositiveButton() {
            return rightView;
        }

        @Override
        public View getNegativeButton() {
            return leftView;
        }

        @Override
        public List<String> getPermissionsToRequest() {
            return builder.deniedList;
        }

        public static class Builder {
            private Context context;
            private int layoutId;
            private int leftViewId;
            private int rightViewId;
            private int reasonsRvId;
            private int reasonItemLayoutId;
            private int reasonItemTextViewId;
            private List<String> reasons;
            private List<String> deniedList;
            public Builder setContext(Context context) {
                this.context = context;
                return this;
            }

            public Builder setLeftViewId(int leftViewId) {
                this.leftViewId = leftViewId;
                return this;
            }

            public Builder setRightViewId(int rightViewId) {
                this.rightViewId = rightViewId;
                return this;
            }


            public Builder setLayoutId(int layoutId) {
                this.layoutId = layoutId;
                return this;
            }

            public Builder setReasonsRvId(int reasonsRvId) {
                this.reasonsRvId = reasonsRvId;
                return this;
            }

            public Builder setReasonItemLayoutId(int reasonItemLayoutId) {
                this.reasonItemLayoutId = reasonItemLayoutId;
                return this;
            }

            public Builder setReasonItemTvId(int reasonItemTextViewId) {
                this.reasonItemTextViewId = reasonItemTextViewId;
                return this;
            }

            public Builder setDeniedPermissionsChinese(List<String> reasons) {
                this.reasons = reasons;
                return this;
            }

            public Builder setDeniedPermissions(List<String> deniedPermissions) {
                this.deniedList = deniedPermissions;
                return this;
            }

            public ReasonDialog build() {
                if (context == null) {
                    throw new RuntimeException("you must setContext before build");
                }
                return new ReasonDialog(this);
            }
        }

        private class ReasonAdapter extends BaseAdapter<String> {
            private int mTvId;

            public ReasonAdapter(Context context, int layoutId, List<String> datas, int tvId) {
                super(context, layoutId, datas);
                this.mTvId = tvId;
            }

            @Override
            public void bindViews(BaseHolder holder, String s, int position) {
                TextView textView = holder.getView(mTvId);
                textView.setText(s);
            }

            @Override
            public void bindListener(BaseHolder holder) {

            }
        }
    }

}
