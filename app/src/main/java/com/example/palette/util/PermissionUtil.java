package com.example.palette.util;

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palette.R;
import com.example.palette.adapter.BaseAdapter;
import com.example.palette.adapter.BaseHolder;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.ExplainReasonCallbackWithBeforeParam;
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
        map.put(Manifest.permission.CAMERA, "照相机");
        map.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "SD卡写入");
        map.put(Manifest.permission.READ_EXTERNAL_STORAGE, "SD卡读取");
        map.put(Manifest.permission.ACCESS_NETWORK_STATE, "获取网络状态");
    }

    public static void checkPermissionsWithDefaultDialog(@NonNull FragmentActivity fragmentActivity, @NonNull List<String> requestList, @Nullable List<String> necessaryList, boolean explainReasonBefore, boolean forwardToSetting, @NonNull PermissionListener listener) {
        PermissionBuilder permissionBuilder = PermissionX.init(fragmentActivity).permissions(requestList);
        if (explainReasonBefore) {
            permissionBuilder.explainReasonBeforeRequest();
            permissionBuilder.onExplainRequestReason(new ExplainReasonCallbackWithBeforeParam() {
                @Override
                public void onExplainReason(ExplainScope scope, List<String> deniedList, boolean beforeRequest) {
                    if (beforeRequest) {
                        scope.showRequestReasonDialog(permissionConvert(map, deniedList), "为保证应用正常运行,即将申请以下权限", "接受", "拒绝");
                    } else {
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
                        scope.showRequestReasonDialog(permissionConvert(map, current), "为保证应用正常运行,必须申请以下权限", "接受", "拒绝");
                    }
                }
            });
        } else {
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
        }
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
                    listener.permissionSomeDenied(deniedList);
                }
            }
        });
    }

    public static void checkPermissionsWithDefaultDialog(@NonNull Fragment fragment, @NonNull List<String> requestList, @Nullable List<String> necessaryList, boolean explainReasonBefore, boolean forwardToSetting, @NonNull PermissionListener listener) {
        PermissionBuilder permissionBuilder = PermissionX.init(fragment).permissions(requestList);
        if (explainReasonBefore) {
            permissionBuilder.explainReasonBeforeRequest();
            permissionBuilder.onExplainRequestReason(new ExplainReasonCallbackWithBeforeParam() {
                @Override
                public void onExplainReason(ExplainScope scope, List<String> deniedList, boolean beforeRequest) {
                    if (beforeRequest) {
                        scope.showRequestReasonDialog(permissionConvert(map, deniedList), "为保证应用正常运行,即将申请以下权限", "接受", "拒绝");
                    } else {
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
                        scope.showRequestReasonDialog(permissionConvert(map, current), "为保证应用正常运行,必须申请以下权限", "接受", "拒绝");
                    }
                }
            });
        } else {
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
        }
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
                    listener.permissionSomeDenied(deniedList);
                }
            }
        });
    }

    public static void checkPermissionsWithReasonDialog(@NonNull Fragment fragment, @NonNull List<String> requestList, @Nullable List<String> necessaryList, boolean explainReasonBefore, boolean forwardToSetting, @Nullable ReasonDialog reasonDialogBefore, @Nullable ReasonDialog reasonDialogAfter, @Nullable ReasonDialog reasonDialogForward, @NonNull PermissionListener listener) {
        PermissionBuilder permissionBuilder = PermissionX.init(fragment).permissions(requestList);
        if (explainReasonBefore) {
            permissionBuilder.explainReasonBeforeRequest();
            permissionBuilder.onExplainRequestReason(new ExplainReasonCallbackWithBeforeParam() {
                @Override
                public void onExplainReason(ExplainScope scope, List<String> deniedList, boolean beforeRequest) {
                    if (beforeRequest) {
                        if (reasonDialogBefore == null) {
                            scope.showRequestReasonDialog(new ReasonDialog.Builder()
                                    .setContext(fragment.getContext())
                                    .setLayoutId(R.layout.default_dialog_request_before)
                                    .setRightViewId(R.id.default_dialog_request_before_positive)
                                    .setLeftViewId(R.id.default_dialog_request_before_negative)
                                    .setReasonsRvId(R.id.default_dialog_request_before_rv)
                                    .setReasonItemLayoutId(R.layout.default_reason_before_item)
                                    .setReasonItemTvId(R.id.default_reason_before_item_tv)
                                    .setReasons(permissionConvert(map, deniedList))
                                    .build());
                        } else {
                            scope.showRequestReasonDialog(reasonDialogBefore);
                        }
                    } else {
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
                        if (reasonDialogAfter == null) {
                            scope.showRequestReasonDialog(new ReasonDialog.Builder()
                                    .setContext(fragment.getContext())
                                    .setLayoutId(R.layout.default_dialog_request_after)
                                    .setRightViewId(R.id.default_dialog_request_after_positive)
                                    .setLeftViewId(R.id.default_dialog_request_after_negative)
                                    .setReasonsRvId(R.id.default_dialog_request_after_rv)
                                    .setReasonItemLayoutId(R.layout.default_reason_after_item)
                                    .setReasonItemTvId(R.id.default_reason_after_item_tv)
                                    .setReasons(permissionConvert(map, current))
                                    .build());
                        } else {
                            scope.showRequestReasonDialog(reasonDialogAfter);
                        }
                    }
                }
            });
        } else {
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
                    if (reasonDialogAfter == null) {
                        scope.showRequestReasonDialog(new ReasonDialog.Builder()
                                .setContext(fragment.getContext())
                                .setLayoutId(R.layout.default_dialog_request_after)
                                .setRightViewId(R.id.default_dialog_request_after_positive)
                                .setLeftViewId(R.id.default_dialog_request_after_negative)
                                .setReasonsRvId(R.id.default_dialog_request_after_rv)
                                .setReasonItemLayoutId(R.layout.default_reason_after_item)
                                .setReasonItemTvId(R.id.default_reason_after_item_tv)
                                .setReasons(permissionConvert(map, current))
                                .build());
                    } else {
                        scope.showRequestReasonDialog(reasonDialogAfter);
                    }
                }
            });
        }
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
                                .setReasons(permissionConvert(map, deniedList))
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
                    listener.permissionSomeDenied(deniedList);
                }
            }
        });
    }

    public static void checkPermissionsWithReasonDialog(@NonNull FragmentActivity fragmentActivity, @NonNull List<String> requestList, @Nullable List<String> necessaryList, boolean explainReasonBefore, boolean forwardToSetting, @Nullable ReasonDialog reasonDialogBefore, @Nullable ReasonDialog reasonDialogAfter, @Nullable ReasonDialog reasonDialogForward, @NonNull PermissionListener listener) {
        PermissionBuilder permissionBuilder = PermissionX.init(fragmentActivity).permissions(requestList);
        if (explainReasonBefore) {
            permissionBuilder.explainReasonBeforeRequest();
            permissionBuilder.onExplainRequestReason(new ExplainReasonCallbackWithBeforeParam() {
                @Override
                public void onExplainReason(ExplainScope scope, List<String> deniedList, boolean beforeRequest) {
                    if (beforeRequest) {
                        if (reasonDialogBefore == null) {
                            scope.showRequestReasonDialog(new ReasonDialog.Builder()
                                    .setContext(fragmentActivity)
                                    .setLayoutId(R.layout.default_dialog_request_before)
                                    .setRightViewId(R.id.default_dialog_request_before_positive)
                                    .setLeftViewId(R.id.default_dialog_request_before_negative)
                                    .setReasonsRvId(R.id.default_dialog_request_before_rv)
                                    .setReasonItemLayoutId(R.layout.default_reason_before_item)
                                    .setReasonItemTvId(R.id.default_reason_before_item_tv)
                                    .setReasons(permissionConvert(map, deniedList))
                                    .build());
                        } else {
                            scope.showRequestReasonDialog(reasonDialogBefore);
                        }
                    } else {
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
                        if (reasonDialogAfter == null) {
                            scope.showRequestReasonDialog(new ReasonDialog.Builder()
                                    .setContext(fragmentActivity)
                                    .setLayoutId(R.layout.default_dialog_request_after)
                                    .setRightViewId(R.id.default_dialog_request_after_positive)
                                    .setLeftViewId(R.id.default_dialog_request_after_negative)
                                    .setReasonsRvId(R.id.default_dialog_request_after_rv)
                                    .setReasonItemLayoutId(R.layout.default_reason_after_item)
                                    .setReasonItemTvId(R.id.default_reason_after_item_tv)
                                    .setReasons(permissionConvert(map, current))
                                    .build());
                        } else {
                            scope.showRequestReasonDialog(reasonDialogAfter);
                        }
                    }
                }
            });
        } else {
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
                    if (reasonDialogAfter == null) {
                        scope.showRequestReasonDialog(new ReasonDialog.Builder()
                                .setContext(fragmentActivity)
                                .setLayoutId(R.layout.default_dialog_request_after)
                                .setRightViewId(R.id.default_dialog_request_after_positive)
                                .setLeftViewId(R.id.default_dialog_request_after_negative)
                                .setReasonsRvId(R.id.default_dialog_request_after_rv)
                                .setReasonItemLayoutId(R.layout.default_reason_after_item)
                                .setReasonItemTvId(R.id.default_reason_after_item_tv)
                                .setReasons(permissionConvert(map, current))
                                .build());
                    } else {
                        scope.showRequestReasonDialog(reasonDialogAfter);
                    }
                }
            });
        }
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
                                .setReasons(permissionConvert(map, deniedList))
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
                    listener.permissionSomeDenied(deniedList);
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
            return builder.reasons;
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

            public Builder setReasons(List<String> reasons) {
                if (reasons == null || reasons.isEmpty()) {
                    throw new RuntimeException("the reasons must have at least one");
                }
                this.reasons = reasons;
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
