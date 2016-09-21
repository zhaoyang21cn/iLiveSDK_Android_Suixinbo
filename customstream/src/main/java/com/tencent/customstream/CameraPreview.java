package com.tencent.customstream;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import com.tencent.av.sdk.AVVideoCtrl;
import com.tencent.av.sdk.AVView;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveRoomManager;

import java.io.IOException;


/** 摄像头预览界面控件 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private boolean bPreviewInit;
    private Camera.Size mCameraSize;
    private static final String TAG = "CameraPreview";


    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (mCamera == null) return;
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mCamera == null||mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setDisplayOrientation(90);
            ILiveSDK.getInstance().getAvVideoCtrl().enableExternalCapture(true, new AVVideoCtrl.EnableExternalCaptureCompleteCallback() {
                @Override
                protected void onComplete(boolean enable, int result) {
                    Log.v("XDBG", "enableExternalCapture->result: " + enable + "|" + result);
                    super.onComplete(enable, result);
                }
            });
            ILiveRoomManager.getInstance().enableMic(true);
            bPreviewInit = false;
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }


    /**
     * Called as preview frames are displayed.  This callback is invoked
     * on the event thread  was called from.
     * <p>
     * <p>If using the {@link ImageFormat#YV12} format,
     * refer to the equations in {@link Camera.Parameters#setPreviewFormat}
     * for the arrangement of the pixel data in the preview callback
     * buffers.
     *
     * @param data   the contents of the preview frame in the format defined
     *               by {@link ImageFormat}, which can be queried
     *               with {@link Camera.Parameters#getPreviewFormat()}.
     *               If {@link Camera.Parameters#setPreviewFormat(int)}
     *               is never called, the default will be the YCbCr_420_SP
     *               (NV21) format.
     * @param camera the Camera service object.
     */
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (null != camera && !bPreviewInit) {
            Camera.Parameters parameters = camera.getParameters();
            mCameraSize = parameters.getPreviewSize();
            bPreviewInit = true;
        }
        if (null != ILiveSDK.getInstance().getAvVideoCtrl()) {
            ILiveSDK.getInstance().getAvVideoCtrl().fillExternalCaptureFrame(data, data.length,
                    mCameraSize.width, mCameraSize.height, 1, AVVideoCtrl.COLOR_FORMAT_I420, AVView.VIDEO_SRC_TYPE_CAMERA);

        }
    }

    public void release(){
        if (mCamera != null){
            mCamera.release();
        }

    }


}
