package com.example.palette.opener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.io.ByteArrayOutputStream;

public class CameraOpener implements SurfaceHolder.Callback, Camera.PreviewCallback, Camera.ErrorCallback{
    private Camera mCamera;
    private Camera.Size mSize;
    private volatile boolean mIsCaptrue;
    private PreviewFrameCallback mCallback;
    private int mExposureSize;
    private int mCameraId;
    private int mPreviewDirect;
    private int mPictureDirect;
    private SurfaceHolder mHolder;
    private static CameraOpener mInstance = new CameraOpener();

    private CameraOpener(){

    }

    public static CameraOpener getInstance() {
        if(mInstance==null){
            synchronized (CameraOpener.class){
                if(mInstance==null){
                    mInstance = new CameraOpener();
                }
            }
        }
        return mInstance;
    }

    public void openCamera(SurfaceHolder holder,int exposureSize,int cameraId,int previewDirect,int pictureDirect){
        mExposureSize = exposureSize;
        mCameraId = cameraId;
        mPreviewDirect = previewDirect;
        mPictureDirect = pictureDirect;
        mHolder = holder;
        mHolder.addCallback(this);
    }

    public void takePicture(PreviewFrameCallback callback){
        mCallback = callback;
        mIsCaptrue = true;
    }

    public void closeCamera(){
        mHolder = null;
        if(isCameraOpen()){
            try {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isCameraOpen(){
        return mCamera != null;
    }

    private void open(SurfaceHolder holder) {
        try {
            mCamera = Camera.open(mCameraId);
            if(mCamera!=null){
                mCamera.setDisplayOrientation(mPreviewDirect);
                mCamera.setPreviewDisplay(holder);
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setExposureCompensation(mExposureSize);
                mSize = parameters.getPreviewSize();
                if(mSize!=null){
                    parameters.setPictureSize(mSize.width,mSize.height);
                    mCamera.setParameters(parameters);
                }
                mCamera.setPreviewCallback(this);
                mCamera.startPreview();
//                mCamera.setErrorCallback(this);
            }
        }catch (Exception e){
            mCamera = null;
            e.printStackTrace();
        }
    }

    private int caculateRotation(int angle, int rotation) {
        if(angle==0){
            return rotation;
        }else if(angle==90){
            if(rotation==270){
                return 0;
            }else {
                return angle+rotation;
            }
        }else if(angle==180){
            if(rotation==180){
                return 0;
            }else if(rotation==270){
                return 90;
            }else {
                return angle+rotation;
            }
        }else if(angle==270){
            if(rotation==0){
                return 270;
            }else if(rotation==90){
                return 0;
            }else if(rotation==180){
                return 90;
            }else if(rotation==270){
                return 180;
            }
        }
        return 0;
    }

    private void previewFrame(byte[] temp){
        try {
            if(mCallback!=null){
                YuvImage image = new YuvImage(temp, ImageFormat.NV21,mSize.width,mSize.height, null);
                if(image!=null){
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compressToJpeg(new Rect(0, 0, mSize.width,mSize.height),100, stream);
                    mCallback.onPreviewFrame(toRotate(toHorizontalMirror(BitmapFactory.decodeByteArray(stream.toByteArray(),0,stream.size())),caculateRotation(mPreviewDirect,mPictureDirect)));
                }else {
                    mCallback.onPreviewFrame(null);
                }
            }
        }catch (Exception e){
            if(mCallback!=null){
                mCallback.onPreviewFrame(null);
            }
            e.printStackTrace();
        }
    }

    private Bitmap toRotate(Bitmap bitmap,float degree){
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    private Bitmap toHorizontalMirror(Bitmap bmp)  {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(-1f, 1f);
        return Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if(mIsCaptrue){
            mIsCaptrue = false;
            previewFrame(data);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        open(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onError(int error, Camera camera) {
//        open(mHolder);
    }

    public interface PreviewFrameCallback{
        void onPreviewFrame(Bitmap bitmap);
    }
}
