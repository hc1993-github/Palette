package com.hc;

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
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
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

public class AutoUpdater implements LifecycleObserver {
    protected Context context;
    protected AutoDownLoader downLoader;
    protected List<String> updateInfo;
    protected String apkUrl;
    protected String unzipPwd;
    private String destFileAbsolutePath;
    private String logFileAbsolutePath;
    private int updateDialogLayoutId = -1;
    private int tv_positiveId = -1;
    private int tv_negativeId = -1;
    private int tv_normalId = -1;
    private int tv_progressId = -1;
    private int tv_messageId = -1;
    private int progressBarId = -1;
    private int rvId = -1;
    private int itemResId = -1;
    protected int remoteVersionCode = -1;
    private View tv_positive;
    private View tv_negative;
    private View tv_normal;
    private TextView tv_progress;
    private TextView tv_message;
    private ProgressBar progressBar;
    protected AutoCommonDialog updateDialog;
    public Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    showUpdateDialog();
                    break;
                case 2:
                    Toast.makeText(context, (String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    int progress = msg.arg1;
                    if (updateDialog != null && updateDialog.isShowing()) {
                        if (progressBar != null) {
                            progressBar.setProgress(progress);
                        }
                        if (tv_progress != null) {
                            float ple = (float) progress / (float) progressBar.getMax();
                            tv_progress.setText(String.format("%.0f", ple * 100) + "%");
                        }
                    }
                    break;
            }
        }
    };

    public AutoUpdater(@NonNull Context context) {
        this.context = context;
        this.downLoader = new AutoDownLoader(context);
        this.updateInfo = new ArrayList<>();
    }

    /**
     * 预处理
     * @param params
     */
    public void preDoSomeThing(Object...params){
        checkVersion((String) params[0]);
    }

    /**
     * 默认为url
     * @param params
     */
    public void checkVersion(@NonNull Object... params) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
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
                    if (response.isSuccessful() && response != null) {
                        remoteVersionCode = AnalayisResponse(response.body().string());
                        if (remoteVersionCode > currentVersionCode) {
                            handler.obtainMessage(1).sendToTarget();
                        }
                    }else {
                        sendMessage(2,"服务器发生异常,检查更新失败");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            sendMessage(2,"发生未知错误,检查更新失败");
        }
    }

    /**
     * 自动更新弹框
     */
    private void showUpdateDialog() {
        final AutoCommonDialog.Builder builder = new AutoCommonDialog.Builder()
                .setContext(context)
                .setCancelable(false)
                .setDefaultListener(new AutoCommonDialog.CommonDialogDefaultOnClickListener() {
                    @Override
                    public void onLeftViewClick(Dialog dialog) {
                        doNegative(dialog);
                    }

                    @Override
                    public void onCommonViewClick(Dialog dialog) {
                        doIgnore(dialog);
                    }

                    @Override
                    public void onRightViewClick(Dialog dialog) {
                        doPositive(dialog);
                    }
                });

        if (updateDialogLayoutId != -1) {
            builder.setLayoutId(updateDialogLayoutId);
            builder.setLeftViewId(tv_negativeId);
            builder.setRightViewId(tv_positiveId);
            builder.setCommonViewId(tv_normalId);
        } else {
            builder.setLayoutId(R.layout.default_dialog_update);
            builder.setLeftViewId(R.id.negative);
            builder.setRightViewId(R.id.positive);
            builder.setCommonViewId(R.id.normal);
        }
        updateDialog = builder.build();
        updateDialog.show();
        if (updateDialogLayoutId == -1) {
            RecyclerView recyclerView = updateDialog.findViewById(R.id.rv_info);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            AutoTextAdapter textAdapter = new AutoTextAdapter(this.updateInfo,R.layout.default_dialog_update_recyclerview_item);
            recyclerView.setAdapter(textAdapter);
            tv_positive = updateDialog.findViewById(R.id.positive);
            tv_negative = updateDialog.findViewById(R.id.negative);
            tv_normal = updateDialog.findViewById(R.id.normal);
            tv_progress = updateDialog.findViewById(R.id.tv_progress);
            tv_message = updateDialog.findViewById(R.id.tv_message);
            progressBar = updateDialog.findViewById(R.id.progressbar);
        } else {
            tv_positive = updateDialog.findViewById(tv_positiveId);
            tv_negative = updateDialog.findViewById(tv_negativeId);
            tv_normal = updateDialog.findViewById(tv_normalId);
            tv_progress = updateDialog.findViewById(tv_progressId);
            tv_message = updateDialog.findViewById(tv_messageId);
            progressBar = updateDialog.findViewById(progressBarId);
            RecyclerView recyclerView = updateDialog.findViewById(rvId);
            if(recyclerView!=null){
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                AutoTextAdapter textAdapter = new AutoTextAdapter(this.updateInfo,itemResId);
                recyclerView.setAdapter(textAdapter);
            }
        }
    }

    /**
     * 自定义确定更新操作
     * @param dialog
     */
    public void doPositive(Dialog dialog){
        showViews();
        downLoad(apkUrl);
    }

    /**
     * 自定义取消更新操作
     * @param dialog
     */
    public void doNegative(Dialog dialog){
        dialog.dismiss();
    }

    /**
     * 自定义忽略更新操作
     * @param dialog
     */
    public void doIgnore(Dialog dialog) {
        dialog.dismiss();
    }

    /**
     * 自定义升级操作UI布局变化
     */
    public void showViews() {
        if(tv_positive!=null){
            tv_positive.setVisibility(View.INVISIBLE);
        }
        if(tv_negative!=null){
            tv_negative.setVisibility(View.INVISIBLE);
        }
        if(tv_normal!=null){
            tv_normal.setVisibility(View.INVISIBLE);
        }
        if (tv_progress != null) {
            tv_progress.setVisibility(View.VISIBLE);
        }
        if (tv_message != null) {
            tv_message.setVisibility(View.VISIBLE);
        }
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * apk下载
     * @param apkUrl
     */
    private void downLoad(String apkUrl) {
        downLoader.download2(apkUrl, new AutoProgressListener() {
            @Override
            public void start(int totalsize) {
                if (updateDialog != null && updateDialog.isShowing()) {
                    if (progressBar != null) {
                        progressBar.setMax(totalsize);
                    }
                }
            }

            @Override
            public void progress(int progress) {
                Message message = Message.obtain();
                message.what = 3;
                message.arg1 = progress;
                handler.sendMessage(message);
            }

            @Override
            public void pause(int progress) {

            }

            @Override
            public void cancel() {

            }

            @Override
            public void finish(String fileAbsolutePath) {
                if (updateDialog != null && updateDialog.isShowing()) {
                    updateDialog.dismiss();
                }
                if(isRoot()){
                    InstallApkSlient(fileAbsolutePath,unzipPwd);
                }else {
                    InstallApk(fileAbsolutePath,unzipPwd);
                }
            }

            @Override
            public void error(String message) {
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
                downLoader.cancel();
                if (updateDialog != null && updateDialog.isShowing()) {
                    updateDialog.dismiss();
                }
            }
        },destFileAbsolutePath == null ? null : destFileAbsolutePath);
//        downLoader.download(apkUrl, new AutoDownLoader.Listener() {
//            @Override
//            public void start(int totalsize) {
//                if (updateDialog != null && updateDialog.isShowing()) {
//                    if (progressBar != null) {
//                        progressBar.setMax(totalsize);
//                    }
//                }
//            }
//
//            @Override
//            public void progress(int progress) {
//                if (updateDialog != null && updateDialog.isShowing()) {
//                    if (progressBar != null) {
//                        progressBar.setProgress(progress);
//                    }
//                    if (tv_progress != null) {
//                        float ple = (float) progress / (float) progressBar.getMax();
//                        tv_progress.setText(String.format("%.0f", ple * 100) + "%");
//                    }
//                }
//            }
//
//            @Override
//            public void pause(int progress) {
//
//            }
//
//            @Override
//            public void cancel() {
//
//            }
//
//            @Override
//            public void finish(String fileAbsolutePath) {
//                if (updateDialog != null && updateDialog.isShowing()) {
//                    updateDialog.dismiss();
//                }
//                InstallApk(fileAbsolutePath,unzipPwd);
//            }
//
//            @Override
//            public void error(String message) {
//                Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
//                downLoader.cancel();
//                if (updateDialog != null && updateDialog.isShowing()) {
//                    updateDialog.dismiss();
//                }
//            }
//        }, destFileAbsolutePath == null ? null : destFileAbsolutePath, logFileAbsolutePath == null ? null : logFileAbsolutePath);
    }

    /**
     * 暂停下载
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void pause(){
        downLoader.pause();
    }

    /**
     * 恢复下载
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void resume(){
        downLoader.resume();
    }

    /**
     * apk安装
     * @param destFileAbsolutePath
     */
    private void InstallApk(String destFileAbsolutePath,String unzipPwd) {
        File file;
        if (destFileAbsolutePath == null) {
            String[] split = context.getPackageName().split("\\.");
            file = new File(Environment.getExternalStorageDirectory(), split[split.length-1]+"/"+apkUrl.substring(apkUrl.lastIndexOf("/") + 1));
        } else {
            file = new File(destFileAbsolutePath);
        }
        if (file.getPath().contains("zip")) {
            int result = AutoFileUtil.unZipFile(file.getAbsolutePath(), unzipPwd);
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
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".FileProvider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    private boolean InstallApkSlient(String destFileAbsolutePath,String unzipPwd){
        String cachePath = destFileAbsolutePath;
        File file;
        if (destFileAbsolutePath == null) {
            String[] split = context.getPackageName().split("\\.");
            file = new File(Environment.getExternalStorageDirectory(), split[split.length-1]+"/"+apkUrl.substring(apkUrl.lastIndexOf("/") + 1));
        } else {
            file = new File(destFileAbsolutePath);
        }
        if (file.getPath().contains("zip")) {
            int result = AutoFileUtil.unZipFile(file.getAbsolutePath(), unzipPwd);
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
            es = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = es.readLine()) != null) {
                builder.append(line);
            }

            if (!builder.toString().contains("Failure")) {
                result = true;
                Toast.makeText(context,"静默安装完成",Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context,"静默安装失败",Toast.LENGTH_SHORT).show();
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

    private boolean isRoot(){
        boolean isRoot = false;
        try {
            isRoot = new File("/system/bin/su").exists() || new File("/system/xbin/su").exists();
        }catch (Exception e){
            e.printStackTrace();
        }
        return isRoot;
    }
    /**
     * 自定义版本号解析规则
     * 若重写需赋值apkUrl和updateInfo
     *
     * @param jsonString 校验版本号后返回信息
     * @return
     */
    public int AnalayisResponse(@NonNull String jsonString) {
        Gson gson = new Gson();
        AutoVersionInfo versionInfoBean = gson.fromJson(jsonString, AutoVersionInfo.class);
        apkUrl = versionInfoBean.getApkUrl();
        unzipPwd = versionInfoBean.getUnzipPwd();
        String[] strings = versionInfoBean.getUpdateInfo().split("-");
        for (String s : strings) {
            updateInfo.add(s);
        }
        return Integer.valueOf(versionInfoBean.getVersionCode());
    }

    /**
     * 自定义升级dialog
     *
     * @param layoutId      布局 id
     * @param positiveId    升级textView id
     * @param negativeId    不升级textView id
     * @param tv_progressId 进度textView id
     * @param tv_messageId  提示信息textView id
     * @param progressBarId 进度progressBar id
     * @param rvId          更新内容recyclerView id
     */
    public void setUpdateDialogLayout(@NonNull int layoutId, @NonNull int positiveId, @NonNull int negativeId,@NonNull int normalId, @NonNull int tv_progressId, @NonNull int tv_messageId, @NonNull int progressBarId, @NonNull int rvId,@NonNull int itemResId) {
        this.updateDialogLayoutId = layoutId;
        this.tv_positiveId = positiveId;
        this.tv_negativeId = negativeId;
        this.tv_normalId = normalId;
        this.tv_progressId = tv_progressId;
        this.tv_messageId = tv_messageId;
        this.progressBarId = progressBarId;
        this.rvId = rvId;
        this.itemResId = itemResId;
    }

    /**
     * 设置下载的文件绝对路径 不设置则默认sdcard
     *
     * @param destFileAbsolutePath
     */
    public void setDestFileAbsolutePath(@NonNull String destFileAbsolutePath) {
        this.destFileAbsolutePath = destFileAbsolutePath;
    }

    /**
     * 设置断点记录文件绝对路径 不设置则默认sdcard
     *
     * @param logFileAbsolutePath
     */
    public void setLogFileAbsolutePath(@NonNull String logFileAbsolutePath) {
        this.logFileAbsolutePath = logFileAbsolutePath;
    }

    /**
     * 往handler中发消息
     * @param type
     * @param arg1
     */
    private void sendMessage(int type, String str,int... arg1) {
        Message message = Message.obtain();
        message.what = type;
        if (arg1.length != 0) {
            message.arg1 = arg1[0];
        }
        if(str!=null){
            message.obj = str;
        }
        handler.sendMessage(message);
    }
}
