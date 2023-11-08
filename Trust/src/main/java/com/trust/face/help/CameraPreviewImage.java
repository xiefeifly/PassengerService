package com.trust.face.help;



public class CameraPreviewImage {

    /**
     * 图像格式，目前只有{@link PreviewImageFormat#PIX_FMT_NV21}, {@link PreviewImageFormat#PIX_FMT_RGBA888}
     */
    private PreviewImageFormat format;
    /**
     * 图像数据
     * */
    private byte[] data;
    /**
     * 图像宽度
     * */
    private int width;
    /**
     * 图像高度
     * */
    private int height;

    public CameraPreviewImage() {
    }

    public CameraPreviewImage(PreviewImageFormat format, byte[] data, int width, int height) {
        this.format = format;
        this.data = data;
        this.width = width;
        this.height = height;
    }

    public PreviewImageFormat getFormat() {
        return format;
    }

    public void setFormat(PreviewImageFormat format) {
        this.format = format;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
