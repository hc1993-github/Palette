package com.example.palette.download;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palette.R;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.palette.download.AutoFileUtil.mDefaultDir;

public class AutoUpdater {
    private static final int NET_ERROR_CODE = -1;
    private static final String NET_ERROR_INFO = "网络连接异常,检查更新失败";
    private static final int SERVER_ERROR_CODE = -2;
    private static final String SERVER_ERROR_INFO = "服务器响应异常,检查更新失败";
    private static final int UNKNOWN_ERROR_CODE = -3;
    private static final String UNKNOWN_ERROR_INFO = "未知异常,检查更新失败";
    private static final int NEWEST_CODE = 0;
    private static final String NEWEST_INFO = "已是最新版本";
    private static final int mConnectTimeout = 5;
    private static final int mReadTimeout = 5;
    private static final int mWriteTimeout = 5;
    protected Activity mContext;
    protected List<String> mUpdateInfo;
    protected String mUrl;
    protected String mUnZipPwd;
    protected String mRemoteMD5;
    protected int mRemoteVersionCode = -1;
    private int mUpdateDialogLayoutId = -1;
    private int mTvPositiveId = -1;
    private int mTvNegativeId = -1;
    private int mTvTitleId = -1;
    private int mTvProgressId = -1;
    private int mProgressBarId = -1;
    private int mRecyclerViewId = -1;
    private int mRecyclerViewItemLayoutId = -1;
    private int mRecyclerViewItemTvId = -1;
    private View tv_negative;
    private View tv_positive;
    private TextView tv_title;
    private TextView tv_progress;
    private ProgressBar progressBar;
    private CheckUpdateListener mCheckUpdateListener;
    private AutoCommonDialog mUpdateDialog;
    protected Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case -1:
                    if(mCheckUpdateListener!=null){
                        mCheckUpdateListener.onCheckUpdateError(msg.arg1, (String) msg.obj);
                    }
                    break;
                case 0:
                    showUpdateDialog();
                    break;
            }
        }
    };

    public AutoUpdater(Activity activity) {
        mContext = activity;
        mUpdateInfo = new ArrayList<>();
    }

    public void beforeCheckUpdate(Object...params){
        checkUpdate(params);
    }

    public void checkUpdate(Object...params){
        try {
            PackageManager packageManager = mContext.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            int currentVersionCode = packageInfo.versionCode;
            Request request = new Request.Builder().url((String) params[0]).build();
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(mConnectTimeout, TimeUnit.SECONDS)
                    .readTimeout(mReadTimeout,TimeUnit.SECONDS)
                    .writeTimeout(mWriteTimeout,TimeUnit.SECONDS);
            OkHttpClient client = builder.build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    sendMessage(-1,NET_ERROR_CODE,NET_ERROR_INFO);
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        if(response.isSuccessful()){
                            mRemoteVersionCode = dealResponse(response.body().string());
                            if(mRemoteVersionCode>currentVersionCode){
                                mHandler.sendEmptyMessage(0);
                            }else {
                                sendMessage(-1,NEWEST_CODE,NEWEST_INFO);
                            }
                        }else {
                            sendMessage(-1,SERVER_ERROR_CODE,SERVER_ERROR_INFO);
                        }
                    }catch (Exception e){
                        sendMessage(-1,NET_ERROR_CODE,NET_ERROR_INFO);
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception e){
            sendMessage(-1,UNKNOWN_ERROR_CODE,UNKNOWN_ERROR_INFO);
            e.printStackTrace();
        }
    }

    public void setCustomUpdateDialogLayout(int updateDialogLayoutId,int vPositiveId,int vNegativeId,int tvTitleId,int tvProgressId,int progressBarId,int recyclerViewId,int recyclerViewItemLayoutId,int recyclerViewItemTvId){
        mUpdateDialogLayoutId = updateDialogLayoutId;
        mTvPositiveId = vPositiveId;
        mTvNegativeId = vNegativeId;
        mTvTitleId = tvTitleId;
        mTvProgressId = tvProgressId;
        mProgressBarId = progressBarId;
        mRecyclerViewId = recyclerViewId;
        mRecyclerViewItemLayoutId = recyclerViewItemLayoutId;
        mRecyclerViewItemTvId = recyclerViewItemTvId;
    }

    public void setCheckUpdateListener(CheckUpdateListener checkUpdateListener) {
        mCheckUpdateListener = checkUpdateListener;
    }

    public void pause(){
        AutoDownLoader.getInstance(mContext).pause();
    }

    public void cancel(){
        AutoDownLoader.getInstance(mContext).cancel();
    }

    public void resume(){
        AutoDownLoader.getInstance(mContext).resume();
    }

    private int dealResponse(String string) {
        AutoVersionInfo autoVersionInfo = new Gson().fromJson(string,AutoVersionInfo.class);
        mUrl = autoVersionInfo.getUrl();
        mUnZipPwd = autoVersionInfo.getUnZipPwd();
        mRemoteMD5 = autoVersionInfo.getMd5();
        String[] infos = autoVersionInfo.getUpdateInfo().split("_");
        mUpdateInfo.clear();
        for (String s:infos){
            mUpdateInfo.add(s);
        }
        return Integer.parseInt(TextUtils.isEmpty(autoVersionInfo.getVersionCode())?"-1":autoVersionInfo.getVersionCode());
    }

    private void showUpdateDialog() {
        AutoCommonDialog.Builder builder = new AutoCommonDialog.Builder()
                .setContext(mContext)
                .setCancelable(false)
                .setDefaultListener(new AutoCommonDialog.CommonDialogDefaultOnClickListener() {
                    @Override
                    public void onLeftViewClick(Dialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onCommonViewClick(Dialog dialog) {

                    }

                    @Override
                    public void onRightViewClick(Dialog dialog) {
                        changeViews();
                        download();
                    }
                });
        if(mUpdateDialogLayoutId != -1){
            builder.setLayoutId(mUpdateDialogLayoutId);
            builder.setLeftViewId(mTvNegativeId);
            builder.setRightViewId(mTvPositiveId);
        }else {
            builder.setLayoutId(R.layout.default_dialog_update);
            builder.setLeftViewId(R.id.default_negative);
            builder.setRightViewId(R.id.default_positive);
        }
        if(mUpdateDialog == null){
            mUpdateDialog = builder.build();
        }
        mUpdateDialog.show();
        if(mUpdateDialogLayoutId != -1){
            tv_negative = mUpdateDialog.findViewById(mTvNegativeId);
            tv_positive = mUpdateDialog.findViewById(mTvPositiveId);
            tv_progress = mUpdateDialog.findViewById(mTvProgressId);
            tv_title = mUpdateDialog.findViewById(mTvTitleId);
            progressBar = mUpdateDialog.findViewById(mProgressBarId);
            RecyclerView recyclerView = mUpdateDialog.findViewById(mRecyclerViewId);
            if(recyclerView != null){
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                AutoTextAdapter textAdapter = new AutoTextAdapter(mUpdateInfo,mRecyclerViewItemLayoutId,mRecyclerViewItemTvId);
                recyclerView.setAdapter(textAdapter);
            }
        }else {
            tv_negative = mUpdateDialog.findViewById(R.id.default_negative);
            tv_positive = mUpdateDialog.findViewById(R.id.default_positive);
            tv_progress = mUpdateDialog.findViewById(R.id.default_tv_progress);
            tv_title = mUpdateDialog.findViewById(R.id.default_tv_title);
            progressBar = mUpdateDialog.findViewById(R.id.default_progressbar);
            RecyclerView recyclerView = mUpdateDialog.findViewById(R.id.default_rv_info);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            AutoTextAdapter textAdapter = new AutoTextAdapter(mUpdateInfo,R.layout.default_dialog_update_recyclerview_item,R.id.default_item_tv);
            recyclerView.setAdapter(textAdapter);
        }
    }

    private void changeViews(){
        if(tv_positive!=null){
            tv_positive.setVisibility(View.INVISIBLE);
        }
        if(tv_negative!=null){
            tv_negative.setVisibility(View.INVISIBLE);
        }
        if(tv_progress!=null){
            tv_progress.setVisibility(View.VISIBLE);
        }
        if(tv_title!=null){
            tv_title.setVisibility(View.VISIBLE);
        }
        if(progressBar != null){
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void download() {
        String apkFilePath = AutoFileUtil.createDefaultDir(mContext, mDefaultDir) + File.separator + mUrl.substring(mUrl.lastIndexOf("/") + 1).trim();
        AutoDownLoader.getInstance(mContext).download(mUrl, new AutoProgressListener() {
            @Override
            public void start(int totalSize) {
                tv_title.setText("下载新版本中,请稍等");
            }

            @Override
            public void progress(int progress) {
                progressBar.setProgress(progress);
                tv_progress.setText(progress+"%");
            }

            @Override
            public void pause(int progress) {

            }

            @Override
            public void cancel(int progress) {

            }

            @Override
            public void finish(String fileAbsolutePath) {
                AutoFileUtil.checkIsNeedUnzip(mContext, fileAbsolutePath, mRemoteMD5, mUnZipPwd, new AutoFileUtil.InstallListener() {
                    @Override
                    public void onCheckedFail(int code, String message) {

                    }

                    @Override
                    public void onUnzipFail(int code, String message) {

                    }

                    @Override
                    public void onUnzipSuccess(Activity context, File file, AutoFileUtil.InstallListener listener) {
                        AutoFileUtil.realInstall(context,file,listener);
                    }

                    @Override
                    public void onUnzipIng(int code, String message) {

                    }

                    @Override
                    public void onInstallFail(int code, String message) {

                    }

                    @Override
                    public void onExecuteInstall() {

                    }
                });
            }

            @Override
            public void error(int code, String info) {
                mUpdateDialog.dismiss();
            }
        },apkFilePath,true);
    }

    private void sendMessage(int type,int arg1,String info){
        Message message = Message.obtain();
        message.what = type;
        message.arg1 = arg1;
        message.obj = info;
        mHandler.sendMessage(message);
    }

    public interface CheckUpdateListener{
        void onCheckUpdateError(int errorCode,String errorMessage);
    }
}
