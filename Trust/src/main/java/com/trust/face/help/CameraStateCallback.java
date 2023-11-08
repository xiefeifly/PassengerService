package com.trust.face.help;

public interface CameraStateCallback {


    /**
     * 相机打开回调
     * */
    void onCameraOpen();
    /**
     * 相机关闭回调
     * */
    void onCameraClose();
    /**
     * 相机打开错误回调
     * */
    void onCameraError(String message);
    /**
     * 相机预览时的画面数据回调
     * */
    void onCameraPreview(CameraPreviewImage image);

}
