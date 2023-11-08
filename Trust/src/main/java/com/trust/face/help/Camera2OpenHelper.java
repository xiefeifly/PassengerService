package com.trust.face.help;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;


import com.trust.face.utils.NV21ToBitmap;
import com.trust.face.utils.YUVConvertUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ZH: 基于Camera2接口的工具类，可方便的打开并预览摄像头，获取预览画面数据
 * A helper class based on the Camera 2 interface, which can easily open and preview the camera,
 * and obtain the preview screen data
 * */
public class Camera2OpenHelper implements CameraOpenInterface{

    private static String TAG = "Camera2OpenHelper";
    private Context mContext;
    private CameraManager mCameraManager;
    private Handler mCameraHandler;
    private NV21ToBitmap mNv21ToBitmap;
    private String mCurrentCameraId;
    private Size mPreviewSize;
    private View mPreviewView;
    private ImageReader mImageReader;
    private CameraStateCallback mCameraStateCallback;
    private CameraDevice mCameraDevice;
    private PreviewImageFormat mPreviewImageFormat = PreviewImageFormat.PIX_FMT_NV21;

    public Camera2OpenHelper(Context context) {
        this.mContext = context;
        mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        HandlerThread handlerThread = new HandlerThread("cameraHandlerThread");
        handlerThread.start();
        mCameraHandler = new Handler(handlerThread.getLooper());
        mNv21ToBitmap = new NV21ToBitmap(context);

    }

    /**
     * 获取摄像头id列表
     * */
    public List<String> getCameraIdList(){
        List<String> cameraIdList = new ArrayList<>();
        try {
            String[] list = mCameraManager.getCameraIdList();
            for (String cameraId : list) {
                Log.e(TAG, "getCameraIdList:============ "+cameraId );
                if (!cameraIdList.contains(cameraId)) cameraIdList.add(cameraId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cameraIdList;
    }


    /**
     * 获取输出分辨率
     * */
    public List<Size> getOutputSizes(){
        List<Size> outputSizeList = new ArrayList<>();
        try {
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCurrentCameraId);
            Size[] outputSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.YUV_420_888);
            /*int orientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            Log.d(TAG, "orientation=" + orientation);*/
            if (outputSizes != null) {
                //Collections.addAll(outputSizeList, outputSizes);
                for (Size size : outputSizes) {
                    Log.d(TAG, "size=" + size.toString());
                    if (!outputSizeList.toString().contains(size.toString())){
                        outputSizeList.add(size);
                    }
                }
                Collections.sort(outputSizeList,(o1, o2) -> {
                    return o1.getWidth() - o2.getWidth();
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputSizeList;
    }

    /**
     * 获取摄像头支持的格式，详见{@link ImageFormat} {@link android.graphics.PixelFormat}
     * */
    public List<Integer> getOutputFormats(){
        List<Integer> outputFormatList = new ArrayList<>();
        try {
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCurrentCameraId);
            int[] outputFormats = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputFormats();
            if (outputFormats != null) {
                for (int format : outputFormats) {
                    Log.d(TAG, "format:" + format);
                    outputFormatList.add(format);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputFormatList;
    }

    /**
     * 设置当前要打开的摄像头ID，一般为0或者1，最好先通过{@link #getCameraIdList}确认
     * */
    public Camera2OpenHelper setCameraId(String cameraId){
        mCurrentCameraId = cameraId;
        return this;
    }

    public String getCameraId() {
        return mCurrentCameraId;
    }

    /**
     * 设置相机状态相关回调，可获取预览帧数据
     * */
    public Camera2OpenHelper setCameraStateCallback(CameraStateCallback cameraStateCallback) {
        this.mCameraStateCallback = cameraStateCallback;
        return this;
    }

    /**
     * 设置相机预览分辨率大小
     * */
    public Camera2OpenHelper setPreviewSize(Size previewSize) {
        mPreviewSize = previewSize;
        mImageReader = ImageReader.newInstance(previewSize.getWidth(),previewSize.getHeight(), ImageFormat.YUV_420_888, 2);
        mImageReader.setOnImageAvailableListener(mImageReaderListener, mCameraHandler);
        return this;
    }

    /**
     * 获取相机预览分辨率大小
     * */
    public Size getPreviewSize() {
        return mPreviewSize;
    }

    /**
     * 设置预览回调onCameraPreview()中输出的图片格式，目前只支持{@link PreviewImageFormat#PIX_FMT_NV21}和{@link PreviewImageFormat#PIX_FMT_RGBA888}
     * */
    public Camera2OpenHelper setPreviewImageFormat(PreviewImageFormat previewImageFormat) {
        this.mPreviewImageFormat = previewImageFormat;
        return this;
    }

    /**
     * 设置相机预览画面显示视图，也可以获取返回的预览帧数据进行显示
     * */
    @Override
    public Camera2OpenHelper setPreviewView(View previewView) {
        if (previewView instanceof TextureView
                || previewView instanceof SurfaceView){
            this.mPreviewView = previewView;
        } else {
            Log.e(TAG, "previewView must be TextureView or SurfaceView!");
        }
        return this;
    }
    /**
     * 打开相机并开始预览，要先确认是否有打开相机的权限，并设置相机id和预览分辨率
     * */
    public void openCamera() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "need to request Manifest.permission.CAMERA permission");
            if (mCameraStateCallback != null){
                mCameraStateCallback.onCameraError("need to request Manifest.permission.CAMERA permission");
            }
            return;
        }
        if (TextUtils.isEmpty(mCurrentCameraId)){
            Log.e(TAG, "please call setCameraId");
            if (mCameraStateCallback != null){
                mCameraStateCallback.onCameraError("please call setCameraId");
            }
            return;
        }
        if (mPreviewSize == null){
            Log.e(TAG, "please call setPreviewSize");
            if (mCameraStateCallback != null){
                mCameraStateCallback.onCameraError("please call setPreviewSize");
            }
            return;
        }
        waitSurfaceAvailable(() -> {
            try {
                mCameraManager.openCamera(mCurrentCameraId, mCameraDeviceStateCallback, mCameraHandler);
            } catch (Exception e) {
                e.printStackTrace();
                if (mCameraStateCallback != null){
                    mCameraStateCallback.onCameraError("openCamera: "+e.getMessage());
                }
            }
        });
    }

    /**
     * 关闭相机并停止预览
     * */
    public void closeCamera(){
        if (mCameraDevice != null){
            mCameraDevice.close();
        }
        if (mCameraStateCallback != null){
            mCameraStateCallback.onCameraClose();
        }
        if (mCameraHandler != null){
            mCameraHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public boolean isFacingFront() {
        try {
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCurrentCameraId);
            int facing = characteristics.get(CameraCharacteristics.LENS_FACING);
            return facing == CameraCharacteristics.LENS_FACING_FRONT;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 如果设置了预览视图，则等待surface可用时才开始预览
     * */
    private void waitSurfaceAvailable(Runnable runnable){
        if (mPreviewView == null){
            runnable.run();
        } else {
            if ((mPreviewView instanceof TextureView && ((TextureView) mPreviewView).isAvailable())
                    || (mPreviewView instanceof SurfaceView && ((SurfaceView) mPreviewView).getHolder() != null)){
                runnable.run();
            } else {
                Log.w(TAG, "mPreviewView is not available, retry after 100ms");
                mCameraHandler.postDelayed(() -> {
                    waitSurfaceAvailable(runnable);
                },100);
            }
        }
    }

    /**
     * 图片格式yuv转换nv21,这个方法还有点问题，用 方法代替
     */
    private byte[] yuv420ToNV21(Image image) {
        if (image.getFormat() != ImageFormat.YUV_420_888){
            throw new IllegalArgumentException("only support the image format: ImageFormat.YUV_420_888");
        }
        // Y分量
        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
        int yRowStride = image.getPlanes()[0].getRowStride();
        // U分量，实际UV分量交错存储，即：UV-UV
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer();
        int uRowStride = image.getPlanes()[1].getRowStride();
        // V分量，实际VU分量交错存储,即：VU-VU, 和uBuffer的区别时UV顺序不同
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer();
        int vRowStride = image.getPlanes()[2].getRowStride();
        Log.d(TAG,"yBuffer getPixelStride="+image.getPlanes()[2].getPixelStride() + " getRowStride="+image.getPlanes()[2].getRowStride());
        Log.d(TAG,"image width="+image.getWidth() + " height="+image.getHeight());
        Log.d(TAG,"yBuffer="+yBuffer.capacity() + " uBuffer="+uBuffer.capacity()+" vBuffer="+vBuffer.capacity());
        // 初始化nv21数据缓冲区
        int totalSize = image.getWidth() * image.getHeight() * 3 / 2;
        byte[] nv21Buffer = new byte[totalSize];
        // 复制Y分量到nv21Buffer的前面
        if (yRowStride == image.getWidth()){
            yBuffer.get(nv21Buffer, 0, yBuffer.capacity());
        } else {
            for (int i = 0; i < image.getHeight(); i++) {
                yBuffer.get(nv21Buffer, i*yRowStride, image.getWidth());
            }
        }
        // 复制VU分量到nv21Buffer的后面
        if (vRowStride == image.getWidth()){
            vBuffer.get(nv21Buffer, yBuffer.capacity(), vBuffer.capacity());
        } else {
            for (int i = 0; i < image.getHeight()/2; i++) {
                vBuffer.get(nv21Buffer, i*vRowStride, image.getWidth());
            }
        }
        // 因为vBuffer最后一个是V分量，少了一个U分量配对，所以取uBuffer的最后一个U分量进行补充
        byte lastValue = uBuffer.get(uBuffer.capacity() - 1);
        nv21Buffer[totalSize - 1] = lastValue;
        return nv21Buffer;
    }

    /**
     * 系统打开相机时的状态回调
     * */
    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            Log.d(TAG, "mCameraDeviceStateCallback: onOpened");
            if (mCameraStateCallback != null){
                mCameraStateCallback.onCameraOpen();
            }
            mCameraDevice = cameraDevice;
            try {
                List<Surface> targetSurfaces = new ArrayList<>();
                CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                captureRequestBuilder.addTarget(mImageReader.getSurface());
                targetSurfaces.add(mImageReader.getSurface());

                if (mPreviewView != null){
                    Surface previewSurface;
                    if (mPreviewView instanceof TextureView){
                        ((TextureView)mPreviewView).getSurfaceTexture().setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                        previewSurface = new Surface(((TextureView)mPreviewView).getSurfaceTexture());
                    } else {
                        ((SurfaceView)mPreviewView).getHolder().setFixedSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                        previewSurface = ((SurfaceView)mPreviewView).getHolder().getSurface();
                    }
                    captureRequestBuilder.addTarget(previewSurface);
                    targetSurfaces.add(previewSurface);
                }

                cameraDevice.createCaptureSession(targetSurfaces, new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        Log.d(TAG, "createCaptureSession: onConfigured");
                        try {
                            session.setRepeatingRequest(captureRequestBuilder.build(), null, mCameraHandler);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                            if (mCameraStateCallback != null){
                                mCameraStateCallback.onCameraError("setRepeatingRequest: "+e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        Log.d(TAG, "createCaptureSession: onConfigureFailed");
                        if (mCameraStateCallback != null){
                            mCameraStateCallback.onCameraError("createCaptureSession: onConfigureFailed");
                        }
                    }
                }, mCameraHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
                if (mCameraStateCallback != null){
                    mCameraStateCallback.onCameraError("createCaptureSession: "+e.getMessage());
                }
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            Log.d(TAG, "mCameraDeviceStateCallback: onDisconnected");
            if (mCameraStateCallback != null){
                mCameraStateCallback.onCameraClose();
            }
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            Log.d(TAG, "mCameraDeviceStateCallback: onError");
            if (mCameraStateCallback != null){
                mCameraStateCallback.onCameraError("openCamera "+i+" failed");
            }
        }
    };

    /**
     * 预览时，ImageReader读取到的预览帧数据，会转换成Bitmap后，通过{@link #mCameraStateCallback}回传
     * */
    private ImageReader.OnImageAvailableListener mImageReaderListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            //需要调用 acquireLatestImage() 和 close(),不然会卡顿
            //Log.i(TAG,"onImageAvailable");
            Image image = reader.acquireLatestImage();
            if (image != null){
                CameraPreviewImage previewImage = null;
                long startTime = System.currentTimeMillis();
                //byte[] nv21dData =  yuv420ToNV21(image);
                byte[] nv21dData = YUVConvertUtil.getDataFromImage(image,YUVConvertUtil.COLOR_FormatNV21);
                Log.d(TAG, "YUV_420_888 to NV21:" + (System.currentTimeMillis() - startTime) + "ms");
                if (mPreviewImageFormat == PreviewImageFormat.PIX_FMT_NV21){
                    previewImage = new CameraPreviewImage(PreviewImageFormat.PIX_FMT_NV21,nv21dData,image.getWidth(),image.getHeight());
                } else if (mPreviewImageFormat == PreviewImageFormat.PIX_FMT_RGBA888){
                    byte[] rgbaData = mNv21ToBitmap.nv21ToRGBA(nv21dData,image.getWidth(),image.getHeight());
                    previewImage = new CameraPreviewImage(PreviewImageFormat.PIX_FMT_RGBA888,rgbaData,image.getWidth(),image.getHeight());
                    Log.i(TAG, "NV21 to RGBA888:" + (System.currentTimeMillis() - startTime) + "ms");
                }
                if (mCameraStateCallback != null && previewImage != null){
                    mCameraStateCallback.onCameraPreview(previewImage);
                }
                image.close();
            }

        }
    };

    /**
     * 获取摄像头硬件登记，一般返回：
     * <br>
     * {@link CameraMetadata#INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY} : 向后兼容模式, 只有基本功能, 等同于Camera1
     * <br>
     * {@link CameraMetadata#INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL} : 外接的摄像头, 功能和 LIMITED 相似
     * <br>
     * {@link CameraMetadata#INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED} : 有最基本的功能, 还支持一些额外的高级功能, 这些高级功能是LEVEL_FULL的子集
     * <br>
     * {@link CameraMetadata#INFO_SUPPORTED_HARDWARE_LEVEL_FULL} : 全功能高清摄像头，支持对每一帧数据进行控制,还支持高速率的图片拍摄
     * <br>
     * {@link CameraMetadata#INFO_SUPPORTED_HARDWARE_LEVEL_3} : 支持YUV再处理和原始数据采集功能，并且具备先进的功能。
     * <br>
     * **/
    public static int getHardwareLevel(Context context, String cameraId){
        int deviceLevel = -1;
        try {
            CameraManager mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(cameraId);
            deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceLevel;
    }

    /**
     * 系统是否支持Camera2调用
     * **/
    public static boolean isSupportCamera2(Context context, String cameraId){
        boolean isSupport = false;
        int deviceLevel = Camera2OpenHelper.getHardwareLevel(context,cameraId);
        switch (deviceLevel) {
            case CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY:
                Log.d(TAG, "Camera has LEGACY Camera2 support");
//                isSupport = true;
                break;
            case CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL:
                Log.d(TAG, "Camera has EXTERNAL Camera2 support");
                isSupport = true;
                break;
            case CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED:
                Log.d(TAG, "Camera has LIMITED Camera2 support");
                isSupport = true;
                break;
            case CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_FULL:
                Log.d(TAG, "Camera has FULL Camera2 support");
                isSupport = true;
                break;
            case CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_3:
                Log.d(TAG, "Camera has Level 3 Camera2 support");
                isSupport = true;
                break;
            default:
                Log.d(TAG, "Camera has unknown Camera2 support: " + deviceLevel);
                break;
        }
        return isSupport && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }


}
