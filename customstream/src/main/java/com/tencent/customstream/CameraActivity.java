package com.tencent.customstream;

import android.app.Activity;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.business.livebusiness.ILVLiveManager;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.core.ILiveRoomConfig;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.core.ILiveRoomOption;
import com.tencent.ilivesdk.view.AVRootView;

public class CameraActivity extends Activity implements View.OnClickListener {

    private Camera mCamera;
    private CameraPreview mPreview;
    AVRootView avRootView;
    Button loginBtn;
    int iRoomNum;
    EditText inputId, roomNum, hostIdInput, hostRoom;
    boolean bEnterRoom;
    private boolean bLogin;     // 记录登录状态

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
        if (view.getId() == R.id.btn_join) {//加入房间
            int room = Integer.parseInt("" + hostRoom.getText());
            String hostId = "" + hostIdInput.getText();
            ILVLiveManager.getInstance().setAvVideoView(avRootView);
            avRootView.setVisibility(View.VISIBLE);
            //加入房间配置项
            ILiveRoomOption memberOption = new ILiveRoomOption(hostId)
                    .imsupport(false)
                    .autoCamera(false) //是否自动打开摄像头
                    .controlRole("NormalMember") //角色设置
                    .authBits(AVRoomMulti.AUTH_BITS_JOIN_ROOM | AVRoomMulti.AUTH_BITS_RECV_AUDIO | AVRoomMulti.AUTH_BITS_RECV_CAMERA_VIDEO | AVRoomMulti.AUTH_BITS_RECV_SCREEN_VIDEO) //权限设置
                    .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO) //是否开始半自动接收
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
        ILVLiveManager.getInstance().onPause();
        if (mPreview != null)
            mPreview.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ILVLiveManager.getInstance().onResume();
    }


    @Override
    protected void onDestroy() {
        if (bLogin){
            ILiveLoginManager.getInstance().tilvbLogout(null);
        }
        ILVLiveManager.getInstance().shutdown();
        super.onDestroy();
    }


}
