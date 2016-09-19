package com.tencent.qcloud.suixinbo.views;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tencent.TIMCustomElem;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMMessage;
import com.tencent.TIMMessagePriority;
import com.tencent.TIMUserProfile;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.business.livebusiness.ILVLiveConfig;
import com.tencent.ilivesdk.business.livebusiness.ILVLiveConstants;
import com.tencent.ilivesdk.business.livebusiness.ILVLiveManager;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.core.ILiveRoomOption;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.qcloud.suixinbo.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;

public class TestSDK extends Activity implements View.OnClickListener {
    Button createBtn, joinbtn, backBtn, sendBtn, inviteBtn, closeMemBtn, thumbUpBtn, rotationBtn;
    AVRootView avRootView;
    Button loginBtn;
    EditText inputId, roomNum, textInput, memId, hostIdInput;
    private static final String TAG = TestSDK.class.getSimpleName();
    private int rotationStatus = ILiveConstants.ROTATION_AUTO;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);

        createBtn = (Button) findViewById(R.id.create);
        joinbtn = (Button) findViewById(R.id.join);
        backBtn = (Button) findViewById(R.id.back);
        sendBtn = (Button) findViewById(R.id.text_send);
        inviteBtn = (Button) findViewById(R.id.invite);
        closeMemBtn = (Button) findViewById(R.id.close_mem);
        thumbUpBtn = (Button) findViewById(R.id.thumbUp);
        rotationBtn = (Button) findViewById(R.id.changeRotation);

        avRootView = (AVRootView) findViewById(R.id.av_root_view);

        loginBtn = (Button) findViewById(R.id.btn_login);
        inputId = (EditText) findViewById(R.id.id_input);
        roomNum = (EditText) findViewById(R.id.room_num);
        textInput = (EditText) findViewById(R.id.text_input);
        hostIdInput = (EditText) findViewById(R.id.host_id);
        memId = (EditText) findViewById(R.id.mem_id);


        createBtn.setOnClickListener(this);
        joinbtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);
        inviteBtn.setOnClickListener(this);
        closeMemBtn.setOnClickListener(this);
        thumbUpBtn.setOnClickListener(this);
        rotationBtn.setOnClickListener(this);
        ILiveSDK.getInstance().initSdk(getApplicationContext(), 1104620500, 107);
        // 关闭IM群组
        ILVLiveConfig liveConfig = (ILVLiveConfig) new ILVLiveConfig()
                .autoRender(true)
                .highAudioQuality(true);

        liveConfig.setMsgListener(new ILVLiveConfig.TILVBLiveMsgListener() {
            @Override
            public void onNewGroupTextMsg(String text, String id, TIMUserProfile userProfile, TIMGroupMemberInfo groupMemberInfo) {
                Toast.makeText(TestSDK.this, "" + text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNewCustomMsg(TIMCustomElem elem, String id, TIMUserProfile userProfile, TIMGroupMemberInfo groupMemberInfo) {
                Log.i(TAG, "onNewCustomMsg ");
                handleCustomMsg(elem, id);
            }
        });
        ILVLiveManager.getInstance().init(liveConfig);
        ILVLiveManager.getInstance().setAvVideoView(avRootView);

        avRootView.setGravity(AVRootView.LAYOUT_GRAVITY_RIGHT);
        avRootView.setSubMarginX(12);
        avRootView.setSubMarginY(100);
        avRootView.setSubCreatedListener(new AVRootView.onSubViewCreatedListener() {
            @Override
            public void onSubViewCreated() {
                for (int i = 1; i < 3; i++) {
                    avRootView.getViewByIndex(i).setDragable(true);
                }
            }
        });


    }


    @Override
    protected void onPause() {
        super.onPause();
        ILVLiveManager.getInstance().onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ILVLiveManager.getInstance().onResume();
    }

    private void handleCustomMsg(TIMCustomElem elem, String id) { //解析Jason格式Custom消息
        try {
            Log.i(TAG, "handleCustomMsg ");
            String customText = new String(elem.getData(), "UTF-8");
            JSONTokener jsonParser = new JSONTokener(customText);
            JSONObject json = (JSONObject) jsonParser.nextValue();
            int action = json.getInt(ILVLiveConstants.CMD_KEY);
            switch (action) {
                case ILVLiveConstants.AVIMCMD_MUlTI_HOST_INVITE: //主播邀请上麦
                    Log.i(TAG, "handleCustomMsg ");
                    Toast.makeText(TestSDK.this, "receive a video invitation !!", Toast.LENGTH_SHORT).show();
                    ILVLiveManager.getInstance().upToVideoMember(ILVLiveConstants.VIDEO_MEMBER_AUTH, ILVLiveConstants.VIDEO_MEMBER_ROLE, new ILiveCallBack() {
                        @Override
                        public void onSuccess(Object data) {

                        }

                        @Override
                        public void onError(String module, int errCode, String errMsg) {

                        }
                    });
                    break;
                case ILVLiveConstants.AVIMCMD_MUlTI_JOIN:
                    break;
                case ILVLiveConstants.AVIMCMD_MUlTI_REFUSE:
                    break;
                case ILVLiveConstants.AVIMCMD_THUMBUP:
                    Toast.makeText(TestSDK.this, "thumb up !!!!!!!!!!!!!", Toast.LENGTH_SHORT).show();
                    break;
                case ILVLiveConstants.AVIMCMD_ENTERLIVE:
                    //mLiveView.refreshText("Step in live", sendId);
                    break;
                case ILVLiveConstants.AVIMCMD_EXITLIVE:
                    //mLiveView.refreshText("quite live", sendId);
                    break;
                case ILVLiveConstants.AVIMCMD_MULTI_CLOSE_INTERACT://主播关闭摄像头命令
                    ILVLiveManager.getInstance().downToNorMember(ILVLiveConstants.NORMAL_MEMBER_AUTH, ILVLiveConstants.NORMAL_MEMBER_ROLE, new ILiveCallBack() {
                        @Override
                        public void onSuccess(Object data) {

                        }

                        @Override
                        public void onError(String module, int errCode, String errMsg) {

                        }
                    });
                    break;
                case ILVLiveConstants.AVIMCMD_MULTI_HOST_CANCELINVITE:
                    break;
                case ILVLiveConstants.AVIMCMD_MULTI_HOST_CONTROLL_CAMERA:
                    break;
                case ILVLiveConstants.AVIMCMD_MULTI_HOST_CONTROLL_MIC:
                    break;
                case ILVLiveConstants.AVIMCMD_HOST_LEAVE:
                    break;
                case ILVLiveConstants.AVIMCMD_HOST_BACK:
                    break;
                default:
                    break;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        ILVLiveManager.getInstance().shutdown();

        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.create) {
            int room = Integer.parseInt("" + roomNum.getText());
            //创建房间配置项
            ILiveRoomOption hostOption = new ILiveRoomOption(null).
                    controlRole("Host")
                    .authBits(AVRoomMulti.AUTH_BITS_DEFAULT)
                    .cameraId(ILiveConstants.FRONT_CAMERA)
                    .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO);

            ILVLiveManager.getInstance().createRoom(room, hostOption, new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    Toast.makeText(TestSDK.this, "create room  ok ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(TestSDK.this, module + "|create fail " + errMsg + " " + errMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (view.getId() == R.id.join) {
            int room = Integer.parseInt("" + roomNum.getText());
            String hostId = "" + hostIdInput.getText();
            //加入房间配置项
            ILiveRoomOption memberOption = new ILiveRoomOption(hostId)
                    .autoCamera(false)
                    .controlRole("NormalMember")
                    .authBits(AVRoomMulti.AUTH_BITS_JOIN_ROOM | AVRoomMulti.AUTH_BITS_RECV_AUDIO | AVRoomMulti.AUTH_BITS_RECV_CAMERA_VIDEO | AVRoomMulti.AUTH_BITS_RECV_SCREEN_VIDEO)
                    .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO)
                    .autoMic(false);
            ILVLiveManager.getInstance().joinRoom(room, memberOption, new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    Toast.makeText(TestSDK.this, "join room  ok ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(TestSDK.this, module + "|join fail " + errMsg + " " + errMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (view.getId() == R.id.back) {
            ILVLiveManager.getInstance().quitRoom(new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    Toast.makeText(TestSDK.this, "quit room  ok ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(TestSDK.this, module + "|join fail " + errCode + " " + errMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }


        if (view.getId() == R.id.btn_login) {
            ILiveSDK.getInstance().setMyUserId("" + inputId.getText());
            ILiveLoginManager.getInstance().tilvbLogin(ILiveSDK.getInstance().getMyUserId(), "123456", new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    Toast.makeText(TestSDK.this, "login success !", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(TestSDK.this, module + "|login fail " + errCode + " " + errMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (view.getId() == R.id.text_send) {
            ILiveSDK.getInstance().setMyUserId("" + inputId.getText());
            ILVLiveManager.getInstance().sendGroupTextMsg("" + textInput.getText(), new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    Toast.makeText(TestSDK.this, "send succ!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {

                }

            });
        }


        if (view.getId() == R.id.invite) {
            String inviteVideo = jasonToString(ILVLiveConstants.AVIMCMD_MUlTI_HOST_INVITE, "");
            ILVLiveManager.getInstance().sendC2CCustomMessage(inviteVideo, "" + memId.getText(), TIMMessagePriority.High, new ILiveCallBack<TIMMessage>() {
                @Override
                public void onSuccess(TIMMessage data) {
                    Toast.makeText(TestSDK.this, "invite send succ!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {

                }

            });


        }

        if (view.getId() == R.id.close_mem) {
            String inviteVideo = jasonToString(ILVLiveConstants.AVIMCMD_MULTI_CLOSE_INTERACT, "");
            ILVLiveManager.getInstance().sendC2CCustomMessage(inviteVideo, "" + memId.getText(), TIMMessagePriority.High, new ILiveCallBack<TIMMessage>() {
                @Override
                public void onSuccess(TIMMessage data) {
                    Toast.makeText(TestSDK.this, "invite send succ!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {

                }

            });


        }
        if (view.getId() == R.id.thumbUp) {
            String inviteVideo = jasonToString(ILVLiveConstants.AVIMCMD_THUMBUP, "");
            ILVLiveManager.getInstance().sendGroupCustomMessage(inviteVideo, TIMMessagePriority.High, new ILiveCallBack<TIMMessage>() {
                @Override
                public void onSuccess(TIMMessage data) {
                    Toast.makeText(TestSDK.this, "invite send thumbup succ!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {

                }

            });

        }
        if (view.getId() == R.id.changeRotation){
            Button b = (Button) view;
            switch (rotationStatus){
                case ILiveConstants.ROTATION_AUTO:
                    b.setText("Full Screen");
                    break;
                case ILiveConstants.ROTATION_FULL_SCREEN:
                    b.setText("Crop");
                    break;
                case ILiveConstants.ROTATION_CROP:
                    b.setText("Auto");
                    break;
            }
            rotationStatus = (rotationStatus + 1)%3;
            for (int i = 0; i < 4; ++i){
                avRootView.getViewByIndex(i).setRotationMode(rotationStatus);
            }



        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    private String jasonToString(int cmd, String param) {
        JSONObject inviteCmd = new JSONObject();
        try {
            inviteCmd.put(ILVLiveConstants.CMD_KEY, cmd);
            inviteCmd.put(ILVLiveConstants.CMD_PARAM, param);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return inviteCmd.toString();
    }


}
