package com.example.palette.opener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import com.example.palette.util.LogUtil;

import java.io.ByteArrayOutputStream;

public class CameraOpener implements SurfaceHolder.Callback, Camera.PreviewCallback{
    private Camera camera;
    private int picture_width = 400;
    private boolean pictureZoom = true;
    private Camera.Size size;
    private volatile boolean isCaptrue;
    private PictureDataCallback callback;

    private static CameraOpener instance = new CameraOpener();

    private CameraOpener(){

    }

    public static CameraOpener getInstance() {
        if(instance==null){
            synchronized (CameraOpener.class){
                if(instance==null){
                    instance = new CameraOpener();
                }
            }
        }
        return instance;
    }

    public void openCamera(SurfaceHolder holder){
        holder.addCallback(this);
    }

    public void takePicture(PictureDataCallback callback){
        this.callback = callback;
        isCaptrue = true;
    }

    public void closeCamera(){
        if(isCameraOpen()){
            try {
                camera.setPreviewCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isCameraOpen(){
        return camera != null;
    }

    private void open(SurfaceHolder holder) {
        try {
            camera = Camera.open(0);
            if(camera!=null){
                int angle = 0;
                camera.setDisplayOrientation(angle);
                camera.setPreviewDisplay(holder);
                Camera.Parameters parameters = camera.getParameters();
                size = parameters.getPreviewSize();
                if(size!=null){
                    parameters.setPictureSize(size.width,size.height);
                    camera.setParameters(parameters);
                    pictureZoom = false;
                }
                camera.setPreviewCallback(this);
                camera.startPreview();
            }
        }catch (Exception e){
            LogUtil.logi("-----相机打开失败-----");
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

    private void captrue(byte[] temp){
        try {
            YuvImage image = new YuvImage(temp, ImageFormat.NV21,size.width,size.height, null);
            if(image!=null){
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(0, 0, size.width,size.height),100, stream);
                if(callback!=null){
                    callback.getPicture(toRotate(toHorizontalMirror(BitmapFactory.decodeByteArray(stream.toByteArray(),0,stream.size())),caculateRotation(0,0)),pictureZoom,picture_width);
                }
            }
        }catch (Exception e){
            if(callback!=null){
                callback.getPicture(null,pictureZoom,picture_width);
            }
            LogUtil.logi("-----图像处理异常-----");
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
        matrix.postScale(-1f, 1f); // 水平镜像翻转
        return Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if(isCaptrue){
            isCaptrue = false;
            captrue(data);
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

    public interface PictureDataCallback{
        void getPicture(Bitmap bitmap, boolean zoom, int width);
    }
}
