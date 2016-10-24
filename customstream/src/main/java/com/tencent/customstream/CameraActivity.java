package com.tencent.customstream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.av.sdk.AVVideoCtrl;
import com.tencent.av.sdk.AVView;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.business.livebusiness.ILVLiveManager;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.core.ILiveRoomConfig;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.core.ILiveRoomOption;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.ilivesdk.view.AVVideoView;

import java.nio.ByteBuffer;

public class CameraActivity extends Activity implements View.OnClickListener {
    private final static int SCREEN_REQUEST_CODE = 0x1000;
    private final static int SCREEN_WIDTH = 480;
    private final static int SCREEN_HEIGHT = 640;
    private final static int YSIZE = SCREEN_WIDTH*SCREEN_HEIGHT;
    private final static int UVSIZE = YSIZE /4;

    private Camera mCamera;
    private CameraPreview mPreview;
    AVRootView avRootView;
    Button loginBtn;
    int iRoomNum;
    EditText inputId, roomNum, hostIdInput, hostRoom;
    private boolean bLogin = false;     // 记录登录状态
    private boolean bEnterRoom = false; // 进入房间
    private boolean bScreenShot = false;    // 屏幕录制状态

    private MediaProjectionManager mMediaPrjMgr;
    private MediaProjection mMediaPrj;
    private ImageReader mImageReader;

    private ImageReader.OnImageAvailableListener mImageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);


        loginBtn = (Button) findViewById(R.id.btn_login);
        inputId = (EditText) findViewById(R.id.id_input);
        roomNum = (EditText) findViewById(R.id.id_room);
        hostIdInput = (EditText) findViewById(R.id.id_host);
        hostRoom = (EditText) findViewById(R.id.id_host_room);
        avRootView = (AVRootView) findViewById(R.id.av_root_view);
        ILiveSDK.getInstance().initSdk(getApplicationContext(), 1104620500, 107);
        ILiveRoomManager.getInstance().init(new ILiveRoomConfig().autoRender(true));


        avRootView.setSubCreatedListener(new AVRootView.onSubViewCreatedListener() {
            @Override
            public void onSubViewCreated() {
                for (int i=0; i<ILiveConstants.MAX_AV_VIDEO_NUM; i++){
                    avRootView.getViewByIndex(i).setRotationMode(ILiveConstants.ROTATION_FULL_SCREEN);
                }
            }
        });
    }


    /**
     * A safe way to get an instance of the Camera object.
     */
    private static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private void switchScreenShot(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            Log.e("XDBG", "system not support!");
            Toast.makeText(this, getString(R.string.system_not_support), Toast.LENGTH_SHORT).show();
            return;
        }else {
            if (null == mImageListener){
                mImageListener = new ImageReader.OnImageAvailableListener() {
                    @Override
                    public void onImageAvailable(ImageReader imageReader) {
                        Image image = imageReader.acquireNextImage();
                        Image.Plane[] planes = image.getPlanes();

                        byte[] outFrame = new byte[YSIZE + UVSIZE *2];
                        int outFrameNextIndex = 0;

                        Log.v("XDBG", "onImageAvailable->enter: " + outFrame.length);

                        // YVU(YV12) =>YUV(I420)
                        ByteBuffer sourceBuffer = planes[0].getBuffer();
                        sourceBuffer.get(outFrame, 0, YSIZE);

                        ByteBuffer vByteBuf = planes[2].getBuffer();
                        vByteBuf.get(outFrame, YSIZE, UVSIZE);

                        ByteBuffer yByteBuf = planes[1].getBuffer();
                        yByteBuf.get(outFrame, YSIZE+UVSIZE, UVSIZE);

                        if (null != ILiveSDK.getInstance().getAvVideoCtrl()){
                            ILiveSDK.getInstance().getAvVideoCtrl().fillExternalCaptureFrame(outFrame, outFrame.length,
                                    SCREEN_WIDTH, SCREEN_HEIGHT, 0, AVVideoCtrl.COLOR_FORMAT_I420, AVView.VIDEO_SRC_TYPE_CAMERA);
                        }

                        //free the Image
                        image.close();
                    }
                };
            }
            if (!bScreenShot) {
                mMediaPrjMgr = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
                Intent captureIntent = mMediaPrjMgr.createScreenCaptureIntent();
                startActivityForResult(captureIntent, SCREEN_REQUEST_CODE);
                ILiveSDK.getInstance().getAvVideoCtrl().enableExternalCapture(true, new AVVideoCtrl.EnableExternalCaptureCompleteCallback() {
                    @Override
                    protected void onComplete(boolean enable, int result) {
                        Log.v("XDBG", "enableExternalCapture->result: " + enable + "|" + result);
                        super.onComplete(enable, result);
                    }
                });
            }else{
                ILiveSDK.getInstance().getAvVideoCtrl().enableExternalCapture(false, new AVVideoCtrl.EnableExternalCaptureCompleteCallback() {
                    @Override
                    protected void onComplete(boolean enable, int result) {
                        Log.v("XDBG", "enableExternalCapture->result: " + enable + "|" + result);
                        super.onComplete(enable, result);
                    }
                });
                if (null != mMediaPrj) {
                    mMediaPrj.stop();
                }
                bScreenShot = false;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("XDBG", "onActivityResult->enter code:"+requestCode+", result:"+resultCode);
        if (requestCode == SCREEN_REQUEST_CODE && resultCode == RESULT_OK){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mMediaPrj = mMediaPrjMgr.getMediaProjection(resultCode, data);
                //mImageReader = ImageReader.newInstance(SCREEN_WIDTH, SCREEN_HEIGHT, ImageFormat.YV12, 30);
                mImageReader = ImageReader.newInstance(SCREEN_WIDTH, SCREEN_HEIGHT, ImageFormat.YV12, 30);
                mImageReader.setOnImageAvailableListener(mImageListener, null);
                mMediaPrj.createVirtualDisplay("Screen", SCREEN_WIDTH, SCREEN_HEIGHT, 1,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.getSurface(), null, null);
                Log.e("XDBG", "onActivityResult->create virtual display enter");
                bScreenShot = true;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_login) { //登陆房间
            ILiveSDK.getInstance().setMyUserId("" + inputId.getText());
            ILiveLoginManager.getInstance().tilvbLogin(ILiveSDK.getInstance().getMyUserId(), "123456", new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    bLogin = true;
                    Toast.makeText(CameraActivity.this, "login success !", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(CameraActivity.this, module + "|login fail " + errCode + " " + errMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (view.getId() == R.id.btn_create) {
            avRootView.setVisibility(View.GONE);
            join();
        }
        if (view.getId() == R.id.btn_camera) {
            mCamera = getCameraInstance();
            mPreview = new CameraPreview(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);
        }
        if (view.getId() == R.id.btn_screen){
            switchScreenShot();
        }
        if (view.getId() == R.id.btn_join) {//加入房间
            int room = Integer.parseInt("" + hostRoom.getText());
            String hostId = "" + hostIdInput.getText();
            ILVLiveManager.getInstance().setAvVideoView(avRootView);
            avRootView.setVisibility(View.VISIBLE);
            //加入房间配置项
            ILiveRoomOption memberOption = new ILiveRoomOption(hostId)
                    .imsupport(false)
                    .autoCamera(false) //是否自动打开摄像头
                    //.controlRole("NormalMember") //角色设置
                    //.authBits(AVRoomMulti.AUTH_BITS_JOIN_ROOM | AVRoomMulti.AUTH_BITS_RECV_AUDIO | AVRoomMulti.AUTH_BITS_RECV_CAMERA_VIDEO | AVRoomMulti.AUTH_BITS_RECV_SCREEN_VIDEO) //权限设置
                    //.videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO) //是否开始半自动接收
                    .autoMic(false);//是否自动打开mic
            //加入房间
            ILVLiveManager.getInstance().joinRoom(room, memberOption, new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    Toast.makeText(CameraActivity.this, "join room  ok ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(CameraActivity.this, module + "|join fail " + errMsg + " " + errMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }


    // 加入房间
    private void join() {
        if (!TextUtils.isEmpty(roomNum.getText().toString())) {
            ILiveRoomOption option = new ILiveRoomOption("")
                    .imsupport(false)
                    .autoCamera(false)
                    .autoMic(false);
            iRoomNum = Integer.valueOf(roomNum.getText().toString());
            ILiveRoomManager.getInstance().createRoom(iRoomNum, option, new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    bEnterRoom = true;
                    Toast.makeText(CameraActivity.this, "create success !", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(CameraActivity.this, "create room failed->" + module + "|" + errCode + "|" + errMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //ILVLiveManager.getInstance().onPause();
        //if (mPreview != null)
        //    mPreview.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //ILVLiveManager.getInstance().onResume();
    }


    @Override
    protected void onDestroy() {
        if (bEnterRoom){
            ILVLiveManager.getInstance().quitRoom(new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    if (bLogin){
                        ILiveLoginManager.getInstance().tilvbLogout(null);
                    }
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    if (bLogin){
                        ILiveLoginManager.getInstance().tilvbLogout(null);
                    }
                }
            });
        }
        super.onDestroy();
    }


}
