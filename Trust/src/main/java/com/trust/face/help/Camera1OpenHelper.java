package com.trust.face.help;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import androidx.core.app.ActivityCompat;


import com.kaer.sdk.utils.LogUtils;
import com.trust.face.utils.NV21ToBitmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * ZH: 基于Camera1接口的工具类，可方便的打开并预览摄像头，获取预览画面数据
 * A helper class based on the Camera1 interface, which can easily open and preview the camera,
 * and obtain the preview screen data
 */
public class Camera1OpenHelper implements CameraOpenInterface {


    private String TAG = "Camera1OpenHelper";

    private Context mContext;
    private String mCameraId;
    private Size mPreviewSize;
    private View mPreviewView;
    private PreviewImageFormat mPreviewImageFormat = PreviewImageFormat.PIX_FMT_NV21;

    private Camera mCamera;
    private CameraStateCallback mCameraStateCallback;
    private Handler mCameraHandler;
    private NV21ToBitmap mNv21ToBitmap;

    public Camera1OpenHelper(Context context) {
        this.mContext = context;
        HandlerThread handlerThread = new HandlerThread("cameraHandlerThread");
        handlerThread.start();
        mCameraHandler = new Handler(handlerThread.getLooper());
        mNv21ToBitmap = NV21ToBitmap.Companion.getInstance(context);
    }

    @Override
    public List<Size> getOutputSizes() {
        List<Size> outputSizeList = new ArrayList<>();
        if (mCamera != null) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPreviewFormat(ImageFormat.NV21);
                List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
                if (previewSizes != null) {
                    for (Camera.Size size : previewSizes) {
                        Log.d(TAG, "size=" + size.width + "*" + size.height);
                        Size tempSize = new Size(size.width, size.height);
                        if (!outputSizeList.toString().contains(tempSize.toString())) {
                            outputSizeList.add(tempSize);
                        }
                    }
                    Collections.sort(outputSizeList, (o1, o2) -> {
                        return o1.getWidth() - o2.getWidth();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "getOutputSizes: call setCameraId() first!");
        }
        return outputSizeList;
    }

    @Override
    public List<Integer> getOutputFormats() {
        List<Integer> outputFormats = new ArrayList<>();
        if (mCamera != null) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                List<Integer> formats = parameters.getSupportedPreviewFormats();
                outputFormats.addAll(formats);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "getOutputFormats: call setCameraId() first!");
        }
        return outputFormats;
    }

    @Override
    public List<String> getCameraIdList() {
        List<String> cameraIdList = new ArrayList<>();
        int num = Camera.getNumberOfCameras();
        for (int i = 0; i < num; i++) {
            cameraIdList.add(i + "");
        }
        return cameraIdList;
    }

    @Override
    public Camera1OpenHelper setCameraId(String cameraId) {
        mCameraId = cameraId;
        try {
            if (mCamera != null) {
                mCamera.release();
            }
            mCamera = Camera.open(Integer.parseInt(mCameraId));
            boolean smoothZoomSupported = mCamera.getParameters().isSmoothZoomSupported();
            Log.e(TAG, "setCameraId: ======================${smoothZoomSupported}" + smoothZoomSupported);
        } catch (Exception exception) {
            exception.printStackTrace();
            Log.e(TAG, "open camera failed: " + exception);
            if (mCameraStateCallback != null) {
                mCameraStateCallback.onCameraError("open camera failed: " + exception);
            }
        }

        return this;
    }

    @Override
    public String getCameraId() {
        return mCameraId;
    }

    @Override
    public Camera1OpenHelper setPreviewSize(Size previewSize) {
        mPreviewSize = previewSize;
        return this;
    }

    @Override
    public Size getPreviewSize() {
        return mPreviewSize;
    }

    @Override
    public Camera1OpenHelper setPreviewImageFormat(PreviewImageFormat previewImageFormat) {
        mPreviewImageFormat = previewImageFormat;
        return this;
    }

    @Override
    public Camera1OpenHelper setPreviewView(View previewView) {
        if (previewView instanceof TextureView
                || previewView instanceof SurfaceView) {
            this.mPreviewView = previewView;
        } else {
            Log.e(TAG, "previewView must be TextureView or SurfaceView!");
        }
        return this;
    }

    @Override
    public Camera1OpenHelper setCameraStateCallback(CameraStateCallback cameraStateCallback) {
        this.mCameraStateCallback = cameraStateCallback;
        return this;
    }

    @Override
    public void openCamera() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "need to request Manifest.permission.CAMERA permission");
            if (mCameraStateCallback != null) {
                mCameraStateCallback.onCameraError("need to request Manifest.permission.CAMERA permission");
            }
            return;
        }
        if (TextUtils.isEmpty(mCameraId)) {
            Log.e(TAG, "please call setCameraId");
            if (mCameraStateCallback != null) {
                mCameraStateCallback.onCameraError("please call setCameraId");
            }
            return;
        }
        if (mPreviewSize == null) {
            Log.e(TAG, "please call setPreviewSize");
            if (mCameraStateCallback != null) {
                mCameraStateCallback.onCameraError("please call setPreviewSize");
            }
            return;
        }
        waitSurfaceAvailable(() -> {
            try {
                if (mCamera == null) {
                    mCamera = Camera.open(Integer.parseInt(mCameraId));
                }
                //设置预览视图
                if (mPreviewView != null) {
                    if (mPreviewView instanceof TextureView) {
                        mCamera.setPreviewTexture(((TextureView) mPreviewView).getSurfaceTexture());
                    } else {
                        mCamera.setPreviewDisplay(((SurfaceView) mPreviewView).getHolder());
                    }
                }
                Camera.Parameters parameters = mCamera.getParameters();
                //设置预览格式
                parameters.setPreviewFormat(ImageFormat.NV21);
                //设置预览大小
                parameters.setPreviewSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                //设置对焦模式
                List<String> focusModes = parameters.getSupportedFocusModes();
                LogUtils.d("focusModes=" + focusModes);
                if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//                } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
//                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }
                mCamera.setParameters(parameters);
                //设置预览数据回调
                mCamera.setPreviewCallback(mPreviewCallback);
                //开始预览
                mCamera.startPreview();
                if (mCameraStateCallback != null) {
                    mCameraStateCallback.onCameraOpen();
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (mCameraStateCallback != null) {
                    mCameraStateCallback.onCameraError("openCamera: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void closeCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if (mCameraStateCallback != null) {
            mCameraStateCallback.onCameraClose();
        }
        if (mCameraHandler != null) {
            mCameraHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public boolean isFacingFront() {
        try {
            CameraManager mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCameraId);
            int facing = characteristics.get(CameraCharacteristics.LENS_FACING);
            return facing == CameraCharacteristics.LENS_FACING_FRONT;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void waitSurfaceAvailable(Runnable runnable) {
        if (mPreviewView == null) {
            runnable.run();
        } else {
            if ((mPreviewView instanceof TextureView && ((TextureView) mPreviewView).isAvailable())
                    || (mPreviewView instanceof SurfaceView && ((SurfaceView) mPreviewView).getHolder() != null)) {
                runnable.run();
            } else {
                Log.w(TAG, "mPreviewView is not available, retry after 100ms");
                mCameraHandler.postDelayed(() -> {
                    waitSurfaceAvailable(runnable);
                }, 100);
            }
        }
    }

    private final Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            CameraPreviewImage previewImage = null;
            long startTime = System.currentTimeMillis();
            if (mPreviewImageFormat == PreviewImageFormat.PIX_FMT_NV21) {
                previewImage = new CameraPreviewImage(PreviewImageFormat.PIX_FMT_NV21, data, mPreviewSize.getWidth(), mPreviewSize.getHeight());
            } else if (mPreviewImageFormat == PreviewImageFormat.PIX_FMT_RGBA888) {
                byte[] rgbaData = mNv21ToBitmap.nv21ToRGBA(data, mPreviewSize.getWidth(), mPreviewSize.getHeight());
                previewImage = new CameraPreviewImage(PreviewImageFormat.PIX_FMT_RGBA888, rgbaData, mPreviewSize.getWidth(), mPreviewSize.getHeight());
                Log.i(TAG, "NV21 to RGBA888:" + (System.currentTimeMillis() - startTime) + "ms");
            }
            if (mCameraStateCallback != null && previewImage != null) {
                mCameraStateCallback.onCameraPreview(previewImage);
            }
        }
    };


}
