package com.hc.autoupdater;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AutoUpdater {
    protected Context mContext;
    protected AutoDownLoader mAutoDownLoader;
    protected List<String> mUpdateInfo;
    protected String mApkUrl;
    protected String mUnZipPwd;
    protected int mRemoteVersionCode = -1;
    protected AutoCommonDialog mUpdateDialog;
    private int mUpdateDialogLayoutId = -1;
    private int mTv_positiveId = -1;
    private int mTv_negativeId = -1;
    private int mTv_normalId = -1;
    private int mTv_progressId = -1;
    private int mTv_messageId = -1;
    private int mProgressBarId = -1;
    private int mRecyclerViewId = -1;
    private int mRecyclerView_ItemLayoutId = -1;
    private View mTv_positive;
    private View mTv_negative;
    private View mTv_normal;
    private TextView mTv_progress;
    private TextView mTv_message;
    private ProgressBar mProgressBar;
    protected Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    showUpdateDialog();
                    break;
                case 2:
                    Toast.makeText(mContext, (String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    int progress = msg.arg1;
                    if (mUpdateDialog != null && mUpdateDialog.isShowing()) {
                        if (mProgressBar != null) {
                            mProgressBar.setProgress(progress);
                        }
                        if (mTv_progress != null) {
                            float ple = (float) progress / (float) mProgressBar.getMax();
                            mTv_progress.setText(String.format("%.0f", ple * 100) + "%");
                        }
                    }
                    break;
            }
        }
    };

    public AutoUpdater(Context context) {
        mContext = context;
        mAutoDownLoader = new AutoDownLoader(mContext);
        mUpdateInfo = new ArrayList<>();
    }

    public void preProcess(Object...params){
        checkVersion(params);
    }

    public void checkVersion(Object...params){
        try {
            PackageManager packageManager = mContext.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            final int currentVersionCode = packageInfo.versionCode;
            Request request = new Request.Builder().url((String) params[0]).build();
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    sendMessage(2,"网络异常,检查更新失败");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response.isSuccessful()){
                        mRemoteVersionCode = analayisResponse(response.body().string());
                        if (mRemoteVersionCode > currentVersionCode) {
                            mHandler.sendEmptyMessage(1);
                        }
                    }else {
                        sendMessage(2,"服务器发生异常,检查更新失败");
                    }
                }
            });
        }catch (Exception e){
            sendMessage(2,"发生未知错误,检查更新失败");
        }
    }

    public int analayisResponse(@NonNull String jsonString) {
        Gson gson = new Gson();
        AutoVersionInfo versionInfoBean = gson.fromJson(jsonString, AutoVersionInfo.class);
        mApkUrl = versionInfoBean.getApkUrl();
        mUnZipPwd = versionInfoBean.getUnzipPwd();
        String[] strings = versionInfoBean.getUpdateInfo().split("-");
        mUpdateInfo.clear();
        for (String s : strings) {
            mUpdateInfo.add(s);
        }
        return Integer.parseInt(versionInfoBean.getVersionCode());
    }

    private void downLoad(String apkUrl){
        mAutoDownLoader.downLoad(apkUrl, new AutoProgressListener() {
            @Override
            public void start(int totalsize) {
                if (mUpdateDialog != null && mUpdateDialog.isShowing()) {
                    if (mProgressBar != null) {
                        mProgressBar.setMax(100);
                    }
                }
            }

            @Override
            public void progress(int progress) {
                Message message = Message.obtain();
                message.what = 3;
                message.arg1 = progress;
                mHandler.sendMessage(message);
            }

            @Override
            public void end(String destFilePath) {
                if (mUpdateDialog != null && mUpdateDialog.isShowing()) {
                    mUpdateDialog.dismiss();
                }
                installApkCommon(destFilePath,mUnZipPwd);
            }

            @Override
            public void error(String message) {
                if (mUpdateDialog != null && mUpdateDialog.isShowing()) {
                    mUpdateDialog.dismiss();
                }
                Toast.makeText(mContext,message,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean installApkSlience(String apkPath,String unZipPwd){
        String cachePath = apkPath;
        File file = new File(apkPath);
        if (file.getPath().contains("zip")) {
            int result = AutoFileUtil.unZipFile(file.getAbsolutePath(), unZipPwd);
            if(result!=0){
                sendMessage(2,"文件解压失败");
                return false;
            }else {
                file = AutoFileUtil.getApkFile(file);
                cachePath = file.getAbsolutePath();
            }
        }
        boolean result = false;
        BufferedReader es = null;
        DataOutputStream os = null;
        try {
            Process process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            String command = "pm install -r " + cachePath + "\n";
            os.write(command.getBytes(Charset.forName("utf-8")));
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            String line;
            StringBuilder builder = new StringBuilder();
            es = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = es.readLine()) != null) {
                builder.append(line);
            }
            if (builder.toString().equalsIgnoreCase("success")) {
                result = true;
                Toast.makeText(mContext,"静默安装完成",Toast.LENGTH_SHORT).show();
            }else {
                result = false;
                Toast.makeText(mContext,"静默安装失败",Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(mContext,"静默安装失败",Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (es != null) {
                    es.close();
                }
            } catch (IOException e) {
            }
        }
        return result;
    }

    private void installApkCommon(String apkPath,String unZipPwd){
        File file = new File(apkPath);
        if (file.getPath().contains("zip")) {
            int result = AutoFileUtil.unZipFile(file.getAbsolutePath(),unZipPwd);
            if(result!=0){
                sendMessage(2,"文件解压失败");
                return;
            }else {
                file = AutoFileUtil.getApkFile(file);
            }
        }
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".FileProvider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

    private boolean isRoot(){
        String[] rootDirs = new String[]{"/su","/su/bin/su","/sbin/su",
                                         "/data/local/xbin/su","/data/local/bin/su","/data/local/su",
                                         "/system/xbin/su","/system/bin/su","/system/sd/xbin/su",
                                         "/system/bin/failsafe/su","/system/bin/cufsdosck","/system/xbin/cufsdosck",
                                         "/system/bin/cufsmgr","/system/xbin/cufsmgr","/system/bin/cufaevdd",
                                         "/system/xbin/cufaevdd","/system/bin/conbb","/system/xbin/conbb"};
        boolean isRoot = false;
        for (int i = 0; i < rootDirs.length; i++) {
            String dir = rootDirs[i];
            if(new File(dir).exists()){
                isRoot = true;
                break;
            }
        }
        return Build.TAGS!=null && Build.TAGS.contains("test-keys") || isRoot;
    }

    private void showUpdateDialog() {
        String[] splits = mContext.getPackageName().split("\\.");
        mAutoDownLoader.deleteFile(Environment.DIRECTORY_DOWNLOADS+File.separator+splits[splits.length-1]+File.separator+"update",mApkUrl.substring(mApkUrl.lastIndexOf(File.separator) + 1));
        final AutoCommonDialog.Builder builder = new AutoCommonDialog.Builder()
                .setContext(mContext)
                .setCancelable(false)
                .setDefaultListener(new AutoCommonDialog.CommonDialogDefaultOnClickListener() {
                    @Override
                    public void onLeftViewClick(Dialog dialog) {
                        doNegative(dialog);
                    }

                    @Override
                    public void onCommonViewClick(Dialog dialog) {
                        doCommon(dialog);
                    }

                    @Override
                    public void onRightViewClick(Dialog dialog) {
                        doPositive(dialog);
                    }
                });
        if (mUpdateDialogLayoutId != -1) {
            builder.setLayoutId(mUpdateDialogLayoutId);
            builder.setLeftViewId(mTv_negativeId);
            builder.setRightViewId(mTv_positiveId);
            builder.setCommonViewId(mTv_normalId);
        } else {
            builder.setLayoutId(R.layout.default_dialog_update);
            builder.setLeftViewId(R.id.default_negative);
            builder.setRightViewId(R.id.default_positive);
            builder.setCommonViewId(R.id.default_normal);
        }
        mUpdateDialog = builder.build();
        mUpdateDialog.show();
        if (mUpdateDialogLayoutId == -1) {
            RecyclerView recyclerView = mUpdateDialog.findViewById(R.id.default_rv_info);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            AutoTextAdapter textAdapter = new AutoTextAdapter(mUpdateInfo, R.layout.default_dialog_update_recyclerview_item);
            recyclerView.setAdapter(textAdapter);
            mTv_positive = mUpdateDialog.findViewById(R.id.default_positive);
            mTv_negative = mUpdateDialog.findViewById(R.id.default_negative);
            mTv_normal = mUpdateDialog.findViewById(R.id.default_normal);
            mTv_progress = mUpdateDialog.findViewById(R.id.default_tv_progress);
            mTv_message = mUpdateDialog.findViewById(R.id.default_tv_message);
            mProgressBar = mUpdateDialog.findViewById(R.id.default_progressbar);
        } else {
            mTv_positive = mUpdateDialog.findViewById(mTv_positiveId);
            mTv_negative = mUpdateDialog.findViewById(mTv_negativeId);
            mTv_normal = mUpdateDialog.findViewById(mTv_normalId);
            mTv_progress = mUpdateDialog.findViewById(mTv_progressId);
            mTv_message = mUpdateDialog.findViewById(mTv_messageId);
            mProgressBar = mUpdateDialog.findViewById(mProgressBarId);
            RecyclerView recyclerView = mUpdateDialog.findViewById(mRecyclerViewId);
            if(recyclerView!=null){
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                AutoTextAdapter textAdapter = new AutoTextAdapter(mUpdateInfo,mRecyclerView_ItemLayoutId);
                recyclerView.setAdapter(textAdapter);
            }
        }

    }

    public void doPositive(Dialog dialog){
        showViews();
        downLoad(mApkUrl);
    }

    public void doNegative(Dialog dialog){
        dialog.dismiss();
    }

    public void doCommon(Dialog dialog) {
        dialog.dismiss();
    }

    public void showViews() {
        if(mTv_positive!=null){
            mTv_positive.setVisibility(View.INVISIBLE);
        }
        if(mTv_negative!=null){
            mTv_negative.setVisibility(View.INVISIBLE);
        }
        if(mTv_normal!=null){
            mTv_normal.setVisibility(View.INVISIBLE);
        }
        if (mTv_progress != null) {
            mTv_progress.setVisibility(View.VISIBLE);
        }
        if (mTv_message != null) {
            mTv_message.setVisibility(View.VISIBLE);
        }
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    public void setUpdateDialogLayout(int layoutId,int positiveId,int negativeId,int normalId,int tv_progressId,int tv_messageId,int progressBarId,int recyclerViewId,int itemLayoutId) {
        mUpdateDialogLayoutId = layoutId;
        mTv_positiveId = positiveId;
        mTv_negativeId = negativeId;
        mTv_normalId = normalId;
        mTv_progressId = tv_progressId;
        mTv_messageId = tv_messageId;
        mProgressBarId = progressBarId;
        mRecyclerViewId = recyclerViewId;
        mRecyclerView_ItemLayoutId = itemLayoutId;
    }

    private void sendMessage(int type, String str,int... arg1) {
        Message message = Message.obtain();
        message.what = type;
        if (arg1.length != 0) {
            message.arg1 = arg1[0];
        }
        if(str!=null){
            message.obj = str;
        }
        mHandler.sendMessage(message);
    }
}
