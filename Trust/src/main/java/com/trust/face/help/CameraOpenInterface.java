package com.trust.face.help;

import android.graphics.ImageFormat;
import android.util.Size;
import android.view.TextureView;
import android.view.View;

import java.util.List;

public interface CameraOpenInterface {


    /**
     * 获取输出分辨率
     * */
    List<Size> getOutputSizes();

    /**
     * 获取摄像头支持的格式，详见{@link ImageFormat} {@link android.graphics.PixelFormat}
     *
     * @return*/
    Object getOutputFormats();

    /**
     * 获取摄像头id列表
     * */
    List<String> getCameraIdList();

    /**
     * 设置当前要打开的摄像头ID，一般为0或者1，最好先通过{@link #getCameraIdList}确认
     * */
    CameraOpenInterface setCameraId(String cameraId);

    /**
     * 获取当前设置的摄像头ID
     * */
    String getCameraId();

    /**
     * 设置相机预览分辨率大小
     * */
    CameraOpenInterface setPreviewSize(Size previewSize);

    /**
     * 获取相机预览分辨率大小
     * */
    Size getPreviewSize();

    /**
     * 设置预览回调onCameraPreview()中输出的图片格式，
     * 目前只支持{@link PreviewImageFormat#PIX_FMT_NV21}和{@link PreviewImageFormat#PIX_FMT_RGBA888}
     * */
    CameraOpenInterface setPreviewImageFormat(PreviewImageFormat previewImageFormat);

    /**
     * 设置相机预览画面显示视图，支持{@link TextureView} 和 {@link android.view.SurfaceView}，
     * 也可以获取返回的预览帧数据进行显示
     * */
    CameraOpenInterface setPreviewView(View previewView);

    /**
     * 设置相机状态相关回调，可获取预览帧数据
     * */
    CameraOpenInterface setCameraStateCallback(CameraStateCallback cameraStateCallback);

    /**
     * 打开相机并开始预览，要先确认是否有打开相机的权限，并设置相机id和预览分辨率
     * */
    void openCamera();

    /**
     * 关闭相机并停止预览
     * */
    void closeCamera();

    /**
     * 是否前置摄像头
     * */
    boolean isFacingFront();




}
