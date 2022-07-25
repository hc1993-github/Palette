package com.example.palette.util;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
        float horizontalScale = ((float) bitmap.getWidth()) / destWidth;
        float verticalScale = ((float) bitmap.getHeight()) / destHeight;
        if (horizontalScale < 1 || verticalScale < 1) {
            return bitmap;
        }
        float maxScale = Math.max(horizontalScale, verticalScale);
        // 确保为4的倍数
        int newWidth = (int) (bitmap.getWidth() / maxScale) & ~0b11;
        int newHeight = (int) (bitmap.getHeight() / maxScale) & ~0b11;

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
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
     * 根据资源id获取bitmap
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
     * 根据文件路径获取bitmap
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
     * bitmap转byte[]
     * @param bitmap
     * @return
     */
    public static byte[] bitmapToByte(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        return baos.toByteArray();
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
