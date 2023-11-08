package com.trust.face.utils;



public class NV21RotateUtils {
    /**
     * 后置旋转90度，前置270度
     **/
    public static byte[] rotateDegree90(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        // Rotate the Y luma
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }
        // Rotate the U and V color components
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i--;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                i--;
            }
        }
        return yuv;
    }

    /**
     * 旋转180度
     **/
    public static byte[] rotateDegree180(byte[] nv21_data, int width, int height) {
        byte[] nv21_rotated = new byte[width * height * 3 / 2];
        int y_size = width * height;
        int buffser_size = y_size * 3 / 2;
        int i = 0;
        int count = 0;

        for (i = y_size - 1; i >= 0; i--)
        {
            nv21_rotated[count] = nv21_data[i];
            count++;
        }

        for (i = buffser_size - 1; i >= y_size; i -= 2)
        {
            nv21_rotated[count++] = nv21_data[i - 1];
            nv21_rotated[count++] = nv21_data[i];
        }
        return nv21_rotated;
    }

    /**
     * 后置旋转270度，前置90度
     **/
    public static byte[] rotateDegree270(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        // Rotate the Y luma
        int i = 0;
        for (int x = imageWidth - 1; x >= 0; x--) {
            for (int y = 0; y < imageHeight; y++) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }// Rotate the U and V color components
        i = imageWidth * imageHeight;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                i++;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i++;
            }
        }
        return yuv;
    }

    /**
     * 镜像旋转
     **/
    public  byte[] frameMirror(byte[] data, int width, int height) {
        byte tempData;
        for (int i = 0; i < height * 3 / 2; i++) {
            for (int j = 0; j < width / 2; j++) {
                tempData = data[i * width + j];
                data[i * width + j] = data[(i + 1) * width - 1 - j];
                data[(i + 1) * width - 1 - j] = tempData;
            }

        }
        return data;
    }

    public static byte[] rotateDegree(byte[] data, int imageWidth, int imageHeight, int degree) {
        if (degree == 90){
            return rotateDegree90(data,imageWidth,imageHeight);
        } else if (degree == 180){
            return rotateDegree180(data,imageWidth,imageHeight);
        }  else if (degree == 270){
            return rotateDegree270(data,imageWidth,imageHeight);
        } else {
            return data;
        }
    }






}


