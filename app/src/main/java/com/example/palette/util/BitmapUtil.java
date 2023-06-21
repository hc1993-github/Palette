package com.example.palette.util;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class BitmapUtil {
    public static final int DEFAULT_MAX_WIDTH = 1920;
    public static final int DEFAULT_MAX_HEIGHT = 1080;


    private static final int MASK_A = 0xFF000000;
    private static final int MASK_R = 0x00FF0000;
    private static final int MASK_G = 0x0000FF00;
    private static final int MASK_B = 0x000000FF;

    private static int rgbToY(int r, int g, int b) {
        return (((66 * r + 129 * g + 25 * b + 128) >> 8) + 16);
    }

    private static int rgbToU(int r, int g, int b) {
        return (((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128);
    }

    private static int rgbToV(int r, int g, int b) {
        return (((112 * r - 94 * g - 18 * b + 128) >> 8) + 128);
    }

    public static void drawRectOnNv21(byte[] nv21, int width, int height, int color, int strokeWidth, Rect rect) {
        if (rect == null || rect.isEmpty()) {
            return;
        }
        drawRectOnNv21(nv21, width, height, color, strokeWidth, rect.left, rect.top, rect.right, rect.bottom);
    }

    private static void drawRectOnNv21(byte[] nv21, int width, int height, int color, int strokeWidth, int left, int top,
                                      int right, int bottom) {
        if ((strokeWidth & 1) == 1) {
            strokeWidth += 1;
        }
        // 确保边界是4的倍数
        left &= ~0b11;
        top &= ~0b11;
        right &= ~0b11;
        bottom &= ~0b11;
        // 对于溢出图像的边，不绘制
        boolean drawLeft = true, drawTop = true, drawRight = true, drawBottom = true;
        if (left <= 0) {
            left = 0;
            drawLeft = false;
        }
        if (top <= 0) {
            top = 0;
            drawTop = false;
        }
        if (right >= width) {
            right = width;
            drawRight = false;
        }
        if (bottom >= height) {
            bottom = height;
            drawBottom = false;
        }

        // 取出R G B的值，并转换为Y U V
        int r = (color & MASK_R) >> 16;
        int g = (color & MASK_G) >> 8;
        int b = color & MASK_B;
        int y = rgbToY(r, g, b);
        int u = rgbToU(r, g, b);
        int v = rgbToV(r, g, b);

        // 根据边框的strokeWidth确定内边界
        int innerTop = top + strokeWidth;
        int innerBottom = bottom - strokeWidth;
        int innerRight = right - strokeWidth;

        int horizontalPixels = right - left;
        int yStartIndex;
        int uvStartIndex;
        boolean drawUV;
        if (drawTop) {
            yStartIndex = top * width + left;
            uvStartIndex = width * height + ((top / 2 * width) + left);
            drawUV = false;
            for (int i = top; i < innerTop; i++) {
                for (int j = 0; j < horizontalPixels; j++) {
                    nv21[yStartIndex + j] = (byte) y;
                }
                yStartIndex += width;
                if (drawUV = !drawUV) {
                    for (int j = 0; j < horizontalPixels; j += 2) {
                        nv21[uvStartIndex + j] = (byte) v;
                        nv21[uvStartIndex + j + 1] = (byte) u;
                    }
                    uvStartIndex += width;
                }
            }
        }

        if (drawLeft) {
            //左边
            yStartIndex = innerTop * width + left;
            uvStartIndex = width * height + (innerTop / 2 * width + left);
            drawUV = false;
            for (int i = innerTop; i < innerBottom; i++) {
                for (int j = 0; j < strokeWidth; j++) {
                    nv21[yStartIndex + j] = (byte) y;
                }
                yStartIndex += width;
                if (drawUV = !drawUV) {
                    for (int j = 0; j < strokeWidth; j += 2) {
                        nv21[uvStartIndex + j] = (byte) v;
                        nv21[uvStartIndex + j + 1] = (byte) u;
                    }
                    uvStartIndex += width;
                }
            }
        }
        if (drawRight) {
            //右边
            yStartIndex = innerTop * width + innerRight;
            uvStartIndex = width * height + (innerTop / 2 * width + innerRight);
            drawUV = false;
            for (int i = innerTop; i < innerBottom; i++) {
                for (int j = 0; j < strokeWidth; j++) {
                    nv21[yStartIndex + j] = (byte) y;
                }
                yStartIndex += width;
                if (drawUV = !drawUV) {
                    for (int j = 0; j < strokeWidth; j += 2) {
                        nv21[uvStartIndex + j] = (byte) v;
                        nv21[uvStartIndex + j + 1] = (byte) u;
                    }
                    uvStartIndex += width;
                }
            }
        }

        if (drawBottom) {
            //下边
            yStartIndex = innerBottom * width + left;
            uvStartIndex = width * height + ((innerBottom / 2 * width) + left);
            drawUV = false;
            for (int i = innerBottom; i < bottom; i++) {
                for (int j = 0; j < horizontalPixels; j++) {
                    nv21[yStartIndex + j] = (byte) y;
                }
                yStartIndex += width;
                if (drawUV = !drawUV) {
                    for (int j = 0; j < horizontalPixels; j += 2) {
                        nv21[uvStartIndex + j] = (byte) v;
                        nv21[uvStartIndex + j + 1] = (byte) u;
                    }
                    uvStartIndex += width;
                }
            }
        }
    }

    /**
     * 按照期望宽高缩放bitmap
     * @param bitmap
     * @param destWidth
     * @param destHeight
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, int destWidth, int destHeight) {
//        float horizontalScale = ((float) bitmap.getWidth()) / destWidth;
//        float verticalScale = ((float) bitmap.getHeight()) / destHeight;
//        if (horizontalScale < 1 || verticalScale < 1) {
//            return bitmap;
//        }
//        float maxScale = Math.max(horizontalScale, verticalScale);
//        // 确保为4的倍数
//        int newWidth = (int) (bitmap.getWidth() / maxScale) & ~0b11;
//        int newHeight = (int) (bitmap.getHeight() / maxScale) & ~0b11;

        return Bitmap.createScaledBitmap(bitmap, destWidth, destHeight, true);
    }

    /**
     * 按照期望宽高将uri转为bitmap
     * @param context
     * @param uri
     * @param destWidth
     * @param destHeight
     * @return
     */
    public static Bitmap uriToScaledBitmap(Context context, Uri uri, int destWidth, int destHeight) {
        ContentResolver contentResolver = context.getContentResolver();
        byte[] data;
        try {
            InputStream input = null;
            input = contentResolver.openInputStream(uri);
            data = new byte[input.available()];
            input.read(data);
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return jpegToScaledBitmap(data, destWidth, destHeight);
    }

    /**
     * 根据资源id获取期望宽高bitmap
     * @param context
     * @param resId
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getBitmapFromResource(Context context,int resId,int width,int height){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(),resId,options);
        options.inSampleSize = calculateSampleSize(width,height,options);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(context.getResources(),resId,options);
    }

    /**
     * 根据文件路径获取期望宽高bitmap
     * @param filePath
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getBitmapFromFile(String filePath,int width,int height){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath,options);
        options.inSampleSize = calculateSampleSize(width,height,options);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath,options);
    }

    /**
     * 图片顺时针旋转
     * @param bitmap
     * @param degree 0-90-180-270
     * @return
     */
    public static Bitmap bitmapRotate(Bitmap bitmap,int degree){
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, w,h, matrix, true);
    }

    /**
     * 图片镜像
     * @param bmp
     * @param isHorizontal 是否水平方向
     * @return
     */
    public static Bitmap bitmapMirror(Bitmap bmp,boolean isHorizontal)  {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        Matrix matrix = new Matrix();
        if(isHorizontal){
            matrix.postScale(-1f, 1f); // 水平镜像翻转
        }else {
            matrix.postScale(1f, -1f);
        }
        return Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);
    }

    /**
     * bitmap转byte[]
     * @param bitmap
     * @return
     */
    public static byte[] bitmapToByte(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        return baos.toByteArray();
    }

    /**
     * 字符串转bitmap
     * @param base64data base64字符串数据
     * @return
     */
    public static Bitmap stringToBitmap(String base64data){
        byte[] bytes = Base64.decode(base64data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

    /**
     * bitmap转字符串
     * @param bitmap
     * @return
     */
    public static String bitmapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }

    /**
     * bitmap转文件
     * @param bitmap
     * @param file
     */
    public static void bitmapToFile(Bitmap bitmap, File file){
        if(file.exists()){
            file.delete();
        }
        FileOutputStream fos = null;
        try {
            file.createNewFile();
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(fos!=null){
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加水印图片到原图左上角
     * @param context
     * @param src
     * @param water
     * @param dp_paddingLeft
     * @param dp_paddingTop
     * @return
     */
    public static Bitmap addWaterMaskLeftTop(Context context,Bitmap src,Bitmap water,int dp_paddingLeft,int dp_paddingTop){
        return addWaterMaskToBitmap(src,water,ScreenUtil.dp2px(context,dp_paddingLeft),ScreenUtil.dp2px(context,dp_paddingTop));
    }

    /**
     * 添加水印图片到原图右下角
     * @param context
     * @param src
     * @param water
     * @param dp_paddingRight
     * @param dp_paddingBottom
     * @return
     */
    public static Bitmap addWaterMaskRightBottom(Context context,Bitmap src,Bitmap water,int dp_paddingRight,int dp_paddingBottom){
        return addWaterMaskToBitmap(src,water,src.getWidth()-water.getWidth()-ScreenUtil.dp2px(context,dp_paddingRight)
        ,src.getHeight()-water.getHeight()-ScreenUtil.dp2px(context,dp_paddingBottom));
    }

    /**
     * 添加水印图片到原图右上角
     * @param context
     * @param src
     * @param water
     * @param dp_paddingRight
     * @param dp_paddingTop
     * @return
     */
    public static Bitmap addWaterMaskRightTop(Context context,Bitmap src,Bitmap water,int dp_paddingRight,int dp_paddingTop){
        return addWaterMaskToBitmap(src,water,src.getWidth()-water.getWidth()-ScreenUtil.dp2px(context,dp_paddingRight)
                ,ScreenUtil.dp2px(context,dp_paddingTop));
    }

    /**
     * 添加水印图片到原图左下角
     * @param context
     * @param src
     * @param water
     * @param dp_paddingLeft
     * @param dp_paddingBottom
     * @return
     */
    public static Bitmap addWaterMaskLeftBottom(Context context,Bitmap src,Bitmap water,int dp_paddingLeft,int dp_paddingBottom){
        return addWaterMaskToBitmap(src,water,ScreenUtil.dp2px(context,dp_paddingLeft)
                ,src.getHeight()-water.getHeight()-ScreenUtil.dp2px(context,dp_paddingBottom));
    }

    /**
     * 添加水印图片到原图中间
     * @param src
     * @param water
     * @return
     */
    public static Bitmap addWaterMaskCenter(Bitmap src,Bitmap water){
        return addWaterMaskToBitmap(src,water,(src.getWidth()-water.getWidth())/2
                ,(src.getHeight()-water.getHeight())/2);
    }

    private static Bitmap addWaterMaskToBitmap(Bitmap src,Bitmap water,int paddingLeft,int paddingTop){
        if(src==null){
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(src,0,0,null);
        canvas.drawBitmap(water,paddingLeft,paddingTop,null);
        canvas.save();
        canvas.restore();
        return bitmap;
    }

    /**
     * 添加文字到图片左上角
     * @param context
     * @param src
     * @param text
     * @param dp_paddingLeft
     * @param dp_paddingTop
     * @param sp
     * @param color
     * @return
     */
    public static Bitmap addTextLeftTop(Context context,Bitmap src,String text,int dp_paddingLeft,int dp_paddingTop,float sp,int color){
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(ScreenUtil.sp2px(context,sp));
        Rect rect = new Rect();
        paint.getTextBounds(text,0,text.length(),rect);
        return addTextToBitmap(src,text,paint,ScreenUtil.dp2px(context,dp_paddingLeft),ScreenUtil.dp2px(context,dp_paddingTop)+rect.height());
    }

    /**
     * 添加文字到图片右下角
     * @param context
     * @param src
     * @param text
     * @param dp_paddingRight
     * @param dp_paddingBottom
     * @param sp
     * @param color
     * @return
     */
    public static Bitmap addTextRightBottom(Context context,Bitmap src,String text,int dp_paddingRight,int dp_paddingBottom,float sp,int color){
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(ScreenUtil.sp2px(context,sp));
        Rect rect = new Rect();
        paint.getTextBounds(text,0,text.length(),rect);
        return addTextToBitmap(src,text,paint,src.getWidth()-rect.width()-ScreenUtil.dp2px(context,dp_paddingRight),src.getHeight()-ScreenUtil.dp2px(context,dp_paddingBottom));
    }

    /**
     * 添加文字到图片右上角
     * @param context
     * @param src
     * @param text
     * @param dp_paddingRight
     * @param dp_paddingTop
     * @param sp
     * @param color
     * @return
     */
    public static Bitmap addTextRightTop(Context context,Bitmap src,String text,int dp_paddingRight,int dp_paddingTop,float sp,int color){
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(ScreenUtil.sp2px(context,sp));
        Rect rect = new Rect();
        paint.getTextBounds(text,0,text.length(),rect);
        return addTextToBitmap(src,text,paint,src.getWidth()-rect.width()-ScreenUtil.dp2px(context,dp_paddingRight),ScreenUtil.dp2px(context,dp_paddingTop)+rect.height());
    }

    /**
     * 添加文字到图片左下角
     * @param context
     * @param src
     * @param text
     * @param dp_paddingLeft
     * @param dp_paddingBottom
     * @param sp
     * @param color
     * @return
     */
    public static Bitmap addTextLeftBottom(Context context,Bitmap src,String text,int dp_paddingLeft,int dp_paddingBottom,float sp,int color){
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(ScreenUtil.sp2px(context,sp));
        Rect rect = new Rect();
        paint.getTextBounds(text,0,text.length(),rect);
        return addTextToBitmap(src,text,paint,ScreenUtil.dp2px(context,dp_paddingLeft),src.getHeight()-ScreenUtil.dp2px(context,dp_paddingBottom));
    }

    /**
     * 添加文字到图片中间
     * @param src
     * @param text
     * @param sp
     * @param color
     * @return
     */
    public static Bitmap addTextCenter(Context context,Bitmap src, String text,float sp,int color){
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(ScreenUtil.sp2px(context,sp));
        Rect rect = new Rect();
        paint.getTextBounds(text,0,text.length(),rect);
        return addTextToBitmap(src,text,paint,(src.getWidth()-rect.width())/2,(src.getHeight()+rect.height())/2);
    }

    /**
     * 创建二维码位图
     * @param text 二维码对应文本
     * @param imgWidth 位图宽
     * @param imgHeight 位图高
     * @param logo logo位图
     * @return
     */
    public static Bitmap createQRCode(String text,int imgWidth,int imgHeight,Bitmap logo){
        try {
            if(text==null || "".equals(text)){
                return null;
            }
            Map<EncodeHintType,Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET,"utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, imgWidth, imgHeight, hints);
            int[] pixels = new int[imgWidth*imgHeight];
            for(int y=0;y<imgHeight;y++){
                for(int x=0;x<imgHeight;x++){
                    if(bitMatrix.get(x,y)){
                        pixels[y*imgWidth+x] = 0xff000000;
                    }else {
                        pixels[y*imgWidth+x] = 0xffffffff;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(imgWidth, imgHeight, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels,0,imgWidth,0,0,imgWidth,imgHeight);
            if(logo!=null){
                bitmap = addLogo(bitmap,logo);
            }
            return bitmap;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static Bitmap addLogo(Bitmap src,Bitmap logo){
        if(src==null){
            return null;
        }
        int srcwidth = src.getWidth();
        int srcheight = src.getHeight();
        int logowidth = logo.getWidth();
        int logoheight = logo.getHeight();
        float scale = srcwidth*1.0f/5/logowidth;
        Bitmap bitmap = Bitmap.createBitmap(srcwidth,srcheight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src,0,0,null);
            canvas.scale(scale,scale,srcwidth/2,srcheight/2);
            canvas.drawBitmap(logo,(srcwidth-logowidth)/2,(srcheight-logoheight)/2,null);
            canvas.save();
            canvas.restore();
        }catch (Exception e){
            bitmap = null;
            e.printStackTrace();
        }
        return bitmap;
    }

    private static Bitmap addTextToBitmap(Bitmap src, String text, Paint paint,int paddingLeft,int paddingTop){
        Bitmap.Config config = src.getConfig();
        paint.setDither(true);
        paint.setFilterBitmap(true);
        if(config==null){
            config = Bitmap.Config.ARGB_8888;
        }
        src = src.copy(config,true);
        Canvas canvas = new Canvas(src);
        canvas.drawText(text,paddingLeft,paddingTop,paint);
        return src;
    }

    private static int calculateSampleSize(int width, int height, BitmapFactory.Options options) {
        int realWidth = options.outWidth;
        int realHeight = options.outHeight;
        int hScale = realHeight/height;
        int wScale = realWidth/width;
        if(wScale>hScale){
            return wScale;
        }else {
            return hScale;
        }
    }

    private static Bitmap jpegToScaledBitmap(byte[] jpeg, int destWidth, int destHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length, options);

        int inSampleSize = 1;
        while (options.outWidth / inSampleSize > destWidth || options.outHeight / inSampleSize > destHeight) {
            inSampleSize++;
        }
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length, options);
    }
}
