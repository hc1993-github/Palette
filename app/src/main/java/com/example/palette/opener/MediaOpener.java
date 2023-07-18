package com.example.palette.opener;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class MediaOpener {
    private MediaPlayer mMediaPlayer;
    private MediaPlayer.OnPreparedListener mOnPreparedListener;
    private MediaPlayer.OnCompletionListener mOnCompletionListener;
    private MediaMetadataRetriever mRetriever;
    private boolean mPlaying = false;
    public void initView(MediaPlayer.OnPreparedListener onPreparedListener,MediaPlayer.OnCompletionListener onCompletionListener){
        mMediaPlayer = new MediaPlayer();
        mRetriever = new MediaMetadataRetriever();
        mOnPreparedListener = onPreparedListener;
        mOnCompletionListener = onCompletionListener;
    }

    public void initData(String url){
        try {
            mMediaPlayer.setDataSource(url);
            mRetriever.setDataSource(url);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void prepare(){
        mMediaPlayer.prepareAsync();
    }

    public void play(){
        if(mMediaPlayer!=null && !mMediaPlayer.isPlaying()){
            mPlaying = true;
            mMediaPlayer.start();
        }
    }

    public void pause(){
        if(mMediaPlayer!=null &&  mMediaPlayer.isPlaying()){
            mPlaying = false;
            mMediaPlayer.pause();
        }
    }

    public void reset(String url){
        if(mMediaPlayer!=null){
            mPlaying = false;
            mMediaPlayer.reset();
            initData(url);
            prepare();
        }
    }

    public void onSurfaceCreated(SurfaceHolder holder){
        mMediaPlayer.setDisplay(holder);
    }

    public void destroy(){
        if(mMediaPlayer!=null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
    }

    public void onPrepared(Context context,SurfaceView surfaceView){
        adjustSize(context, surfaceView);
    }

    public void onCompletion(){
        reset(null);
    }

    public void adjustSize(Context context,SurfaceView surfaceView){
        int videoWidth = mMediaPlayer.getVideoWidth();
        int videoHeight = mMediaPlayer.getVideoHeight();
        int deviceWidth = context.getResources().getDisplayMetrics().widthPixels;
        int deviceHeight = context.getResources().getDisplayMetrics().heightPixels;
        float scale;
        if (videoWidth > videoHeight) { //视频是横屏
            scale = (float) deviceWidth / (float) videoWidth;
        } else { //视频是竖屏
            scale = (float) deviceHeight / (float) videoHeight;
        }
        videoWidth = (int) (videoWidth * scale);
        videoHeight = (int) (videoHeight * scale);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) surfaceView.getLayoutParams();
        layoutParams.width = videoWidth;
        layoutParams.height = videoHeight;
        layoutParams.verticalBias = 0.5f;
        layoutParams.horizontalBias = 0.5f;
        surfaceView.setLayoutParams(layoutParams);
//        ConstraintLayout.LayoutParams ivlayoutParams = (ConstraintLayout.LayoutParams) iv.getLayoutParams();
//        ivlayoutParams.width = videoWidth;
//        ivlayoutParams.height = videoHeight;
//        ivlayoutParams.verticalBias = 0.5f;
//        ivlayoutParams.horizontalBias = 0.5f;
//        iv.setLayoutParams(ivlayoutParams);
//        iv.setVisibility(View.VISIBLE);
//        iv.setImageBitmap(retriever.getFrameAtTime());
    }
}
