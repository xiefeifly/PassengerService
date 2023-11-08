package com.trust.face.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.FaceDetector;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.kaer.sdk.utils.LogUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;


public class BitmapUtil {

    public static synchronized int[] getFaceField(Bitmap b) {
        int[] res = new int[]{-1, 0, 0, 0};
        if (null == b) {
            return res;
        }
        int ori_w = b.getWidth();
        int ori_h = b.getHeight();
        int numberOfFace = 1;
        float scale = 1;
        if (ori_h > 192 || ori_w > 256) {
            scale = 640 / 256;
        }

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        b = BitmapFactory.decodeStream(is, null, opt);
        b = Bitmap.createScaledBitmap(b, 256, 192, false);
        PointF point = new PointF();
        float eyesDistance = 0;
        long time = System.currentTimeMillis();
        try {
            long time1 = System.currentTimeMillis();
            FaceDetector detector = new FaceDetector(b.getWidth(), b.getHeight(), numberOfFace);
            FaceDetector.Face[] face = new FaceDetector.Face[numberOfFace];
            int num = detector.findFaces(b, face);
            if (num == 0) {
                return res;
            }
            face[0].getMidPoint(point);
            eyesDistance = face[0].eyesDistance();
        } catch (Exception e) {
            return res;
        }
        point.x = point.x * scale;
        point.y = point.y * scale;
        eyesDistance *= scale;
        if (eyesDistance > 0 && eyesDistance < 60) {
            return new int[]{-2, 0, 0, 0};
        }
        int left = (int) (point.x - eyesDistance * 1.6);
        int top = (int) (point.y - eyesDistance * 1.0);
        int right = (int) (point.x + eyesDistance * 1.6);
        int bottom = (int) (point.y + eyesDistance * 2.4);

        if (left < 0) {
            left = 0;
        }
        if (top < 0) {
            top = 0;
        }
        if (right > ori_w) {
            right = ori_w;
        }
        if (bottom > ori_h) {
            bottom = ori_h;
        }
        res[0] = top;
        res[1] = bottom;
        res[2] = left;
        res[3] = right;

        return res;
    }

    public static String bitmap2Base64(Bitmap bitmap) {
        if (null == bitmap) {
            return "";
        }
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] bitmapBytes = baos.toByteArray();
            result = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
        } catch (Exception e) {
            Log.w("test", e.getMessage());
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                Log.w("test", e.getMessage());
            }
        }
        if (null == result) {
            result = "";
        }
        return result;
    }

    public static Bitmap base64ToBitmap(String base64Data) {
        Bitmap bitmap = null;
        if (base64Data != null && !"".equals(base64Data)) {
            byte[] bytes = Base64.decode(base64Data, Base64.NO_WRAP);
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return bitmap;
    }


    public static String saveBase64ToFile(String base64, String dir, String fileName) {
        if (TextUtils.isEmpty(base64) || TextUtils.isEmpty(dir) || TextUtils.isEmpty(fileName)) {
            return "";
        }
        File dirFile = new File(dir);
        if(!dirFile.exists()){
            dirFile.mkdirs();
        }
        File file = new File(dir + "/" + fileName);
        byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);
            FileDescriptor fd = fos.getFD();
            fd.sync();
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.e(e.getMessage());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file.getPath();
    }

    public static String saveBase64ToDat(String base64, String dir, String name) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        File f = new File(dir + name + ".dat");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            fos.write(base64.getBytes(Charset.forName("utf-8")));
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return f.getPath();

    }

    public static String saveBitmap2File(Bitmap b, String dir, String fileName) {
        if (null == b) {
            return null;
        }
        File dirFile = new File(dir);
        if(!dirFile.exists()){
            dirFile.mkdirs();
        }
        File file = new File(dir + "/" + fileName+".png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            FileDescriptor fd = fos.getFD();
            fd.sync();
        } catch (IOException e) {
            LogUtils.e(e.getMessage());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (b != null && !b.isRecycled()) {
                b.recycle();
                b = null;
            }
        }

        return file.getPath();
    }
    public static Bitmap deepCopyBitmap(Bitmap srcBitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    public static Bitmap cutBitmapByRect(Bitmap bitmap, Rect rect) {
        if (null == bitmap) {
            return null;
        }
        int x = rect.left;
        int y = rect.top;
        int w = rect.width();
        int h = rect.height();
        if (x < 0 || x > bitmap.getWidth()){
            x = 0;
        }
        if (y < 0 || y > bitmap.getHeight()){
            y = 0;
        }
        if (x + w > bitmap.getWidth()){
            w = bitmap.getWidth() - x;
        }
        if (y + h > bitmap.getHeight()){
            h = bitmap.getHeight() - y;
        }
        try {
            bitmap = Bitmap.createBitmap(bitmap, x, y, w, h);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }


    public static Bitmap cutBmp(Bitmap src_bmp, float[] facePos, int previewWidth, int previewHeight) {
        int left = (int) (facePos[0] * 1.0);
        int top = (int) (facePos[1] * 1.0);
        int right = (int) (facePos[2] * 1.0);
        int bottom = (int) (facePos[3] * 1.0);
        if (top < 0) {
            top = 0;
        }
        if (bottom > previewHeight) {
            bottom = previewHeight;
        }
        if (left < 0) {
            left = 0;
        }
        if (right > previewWidth) {
            right = previewWidth;
        }
        return Bitmap.createBitmap(src_bmp, left, top, right - left, bottom - top);
    }

    @SuppressWarnings("NumericOverflow")
    public static Bitmap convertGreyImg(Bitmap img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int[] pixels = new int[width * height];
        img.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];
                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);
                grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        return result;
    }







    /**
     *  * 根据分辨率压缩
     *  *
     *  * @param srcPath 图片路径
     *  * @param ImageSize 图片大小 单位kb
     *  * @return
     *  
     */
    public static boolean compressBitmap(String srcPath, int ImageSize, String savePath) {
        int subtract;
//        AraLogger.i("图片处理开始..");
        Bitmap bitmap = compressByResolution(srcPath, 640, 480); //分辨率压缩
        if (bitmap == null) {
//            AraLogger.i("bitmap 为空");
            return false;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
//        AraLogger.i("图片分辨率压缩后：" + baos.toByteArray().length / 1024 + "KB");


        while (baos.toByteArray().length > ImageSize * 1024) { //循环判断如果压缩后图片是否大于ImageSize kb,大于继续压缩
            subtract = setSubstractSize(baos.toByteArray().length / 1024);
            baos.reset();//重置baos即清空baos
            options -= subtract;//每次都减少10
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
//            AraLogger.i("图片压缩后：" + baos.toByteArray().length / 1024 + "KB");
        }
//        AraLogger.i("图片处理完成!" + baos.toByteArray().length / 1024 + "KB");
        try {
            FileOutputStream fos = new FileOutputStream(new File(savePath));//将压缩后的图片保存的本地上指定路径中
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        bitmap.recycle();

        return true; //压缩成功返回ture
    }


    /**
     * 根据分辨率压缩图片比例
     *
     * @param imgPath
     * @param w
     * @param h
     * @return
     */
    private static Bitmap compressByResolution(String imgPath, int w, int h) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, opts);

        int width = opts.outWidth;
        int height = opts.outHeight;
        int widthScale = width / w;
        int heightScale = height / h;

        int scale;
        if (widthScale < heightScale) { //保留压缩比例小的
            scale = widthScale;
        } else {
            scale = heightScale;
        }

        if (scale < 1) {
            scale = 1;
        }

        opts.inSampleSize = scale;

        opts.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, opts);

        return bitmap;
    }

    /**
     * 根据图片的大小设置压缩的比例，提高速度
     *
     * @param imageKB
     * @return
     */
    private static int setSubstractSize(int imageKB) {

        if (imageKB > 1000) {
            return 60;
        } else if (imageKB > 750) {
            return 40;
        } else if (imageKB > 500) {
            return 20;
        } else {
            return 10;
        }

    }

    public static Bitmap getSmallBitmap(String path, int imageSize) {
        if (TextUtils.isEmpty(path)) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = computeSampleSize(options, -1, imageSize);

        //使用获取到的inSampleSize再次解析图片
        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    /**
     * 计算原始大小
     *
     * @param options        解析图片所需的BitmapFactory.Options
     * @param minSideLength  调整后图片最小的宽或高值,一般赋值为 -1
     * @param maxNumOfPixels 调整后图片的内存占用量上限
     * @return
     */
    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 计算压缩的比例
     *
     * @param options        解析图片所需的BitmapFactory.Options
     * @param minSideLength  调整后图片最小的宽或高值,一般赋值为 -1
     * @param maxNumOfPixels 调整后图片的内存占用量上限
     * @return
     */
    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    public static Bitmap getScaleBitmap(Bitmap bitmap, float[] scaleSize) {

        int bmpWidth = bitmap.getWidth();
        int bmpHeight = bitmap.getHeight();

        scaleSize[0] = 1;
        int maxSize = 260;
//        int maxSize = 200;
        while (true) {
            if (bmpWidth * scaleSize[0] < maxSize || bmpHeight * scaleSize[0] < maxSize) {
                break;
            }
            scaleSize[0] /= 2;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scaleSize[0], scaleSize[0]);
        return Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);
    }


    public static Bitmap getCutFaceBitmap(Bitmap bitmap, float scale, int x, int y, int width, int height) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, x, y, width, height, matrix, true);
    }

    /**
     * 通过图片base64流判断图片等于多少字节
     * image 图片流
     */
    public static long imageSize(String image) {
        String str = image.substring(22); // 1.需要计算文件流大小，首先把头部的data:image/png;base64,（注意有逗号）去掉。
        Integer equalIndex = str.indexOf("=");//2.找到等号，把等号也去掉
        if (str.indexOf("=") > 0) {
            str = str.substring(0, equalIndex);
        }
        long strLength = str.length();//3.原来的字符流大小，单位为字节
        long size = strLength - (strLength / 8) * 2;//4.计算后得到的文件流大小，单位为字节
        return size;
    }

    public static byte[] rotateYUV420Degree180(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        int i = 0;
        int count = 0;

        for (i = imageWidth * imageHeight - 1; i >= 0; i--) {
            yuv[count] = data[i];
            count++;
        }

        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (i = imageWidth * imageHeight * 3 / 2 - 1; i >= imageWidth
                * imageHeight; i -= 2) {
            yuv[count++] = data[i - 1];
            yuv[count++] = data[i];
        }
        return yuv;
    }

    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        if (bitmap == null) return "";

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                int options = 100;
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
                while (baos.size() / 1024 > 30) {
                    baos.reset();
                    options -= 10;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
                }
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        return result.replaceAll("[\\s*\t\n\r]", "");
        return result;
    }

    public static Bitmap url2bitmap(String url) {
        Bitmap bm = null;
        try {
            URL iconUrl = new URL(url);
            URLConnection conn = iconUrl.openConnection();
            HttpURLConnection http = (HttpURLConnection) conn;
            int length = http.getContentLength();
            conn.connect();
            // 获得图像的字符流
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is, length);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
        return bm;
    }

    /**
     * 将图片转换成Base64编码的字符串
     */
    public static String imageToBase64(String path){
        if(TextUtils.isEmpty(path)){
            return null;
        }
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try{
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data,Base64.NO_WRAP);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null !=is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    /**
     * 选择变换
     *
     * @param origin 原图
     * @param degree  旋转角度，可正可负
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmap(Bitmap origin, float degree) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(degree);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    /**
     * 选择变换
     *
     * @param origin 原图
     * @param x  左右旋转角度，可正可负
     * @param y  上下旋转角度，可正可负
     * @return 旋转后的图片
     */
    public static Bitmap mirrorBitmap(Bitmap origin, float x, float y) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setScale(x,y);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }


    public static String bitmapToBase64Limited(Bitmap bitmap, int size) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                int options = 100;
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
                while (baos.size() > size && options > 10) {
                    //Log.d("fuwanan","bitmapToBase64Limited： "+options+" size="+baos.size()+" " +size);
                    baos.reset();
                    options -= 10;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
                }
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result != null ? result.replaceAll("[\\s*\t\n\r]", "") : null;
    }

}
