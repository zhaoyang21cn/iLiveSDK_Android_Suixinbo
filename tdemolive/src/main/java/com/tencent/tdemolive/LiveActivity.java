package com.tencent.tdemolive;

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

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;

public class LiveActivity extends Activity implements View.OnClickListener {
    Button createBtn, joinbtn, backBtn, sendBtn, inviteBtn, closeMemBtn, thumbUpBtn;
    AVRootView avRootView;
    Button loginBtn;
    EditText inputId, roomNum, roomNumJoin, textInput, memId, hostIdInput;
    private static final String TAG = LiveActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_layout);

        createBtn = (Button) findViewById(R.id.create);
        joinbtn = (Button) findViewById(R.id.join);
        backBtn = (Button) findViewById(R.id.back);
        sendBtn = (Button) findViewById(R.id.text_send);
        inviteBtn = (Button) findViewById(R.id.invite);
        closeMemBtn = (Button) findViewById(R.id.close_mem);
        thumbUpBtn = (Button) findViewById(R.id.thumbUp);

        avRootView = (AVRootView) findViewById(R.id.av_root_view);

        loginBtn = (Button) findViewById(R.id.btn_login);
        inputId = (EditText) findViewById(R.id.id_input);
        roomNum = (EditText) findViewById(R.id.room_num);
        roomNumJoin = (EditText) findViewById(R.id.room_num_join);
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

        //初始化SDK
        ILiveSDK.getInstance().initSdk(getApplicationContext(), 1104620500, 107);
        // 关闭IM群组
        ILVLiveConfig liveConfig = (ILVLiveConfig) new ILVLiveConfig()
                .autoRender(true)
                .highAudioQuality(true);

        //设置消息回调接口
        liveConfig.setMsgListener(new ILVLiveConfig.TILVBLiveMsgListener() {
            @Override
            //群文本消息
            public void onNewGroupTextMsg(String text, String id, TIMUserProfile userProfile, TIMGroupMemberInfo groupMemberInfo) {
                Toast.makeText(LiveActivity.this, "" + text, Toast.LENGTH_SHORT).show();
            }

            @Override
            //自定消息 包括C2C和群
            public void onNewCustomMsg(TIMCustomElem elem, String id, TIMUserProfile userProfile, TIMGroupMemberInfo groupMemberInfo) {
                Log.i(TAG, "onNewCustomMsg ");
                handleCustomMsg(elem, id);
            }
        });
        //初始化直播场景
        ILVLiveManager.getInstance().init(liveConfig);
        //设置渲染界面
        ILVLiveManager.getInstance().setAvVideoView(avRootView);

        //设置小窗口初始位置
        avRootView.setGravity(AVRootView.LAYOUT_GRAVITY_RIGHT);
        avRootView.setSubMarginX(12);
        avRootView.setSubMarginY(100);
        //配置拖拽
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

    /**
     * 处理自定义消息  更多自定义消息可以查看类ILVLiveConstants,也支持用户自定义自己的消息
     * @param elem 自定义消息类型
     * @param id 发送者
     */
    private void handleCustomMsg(TIMCustomElem elem, String id) { //解析Jason格式Custom消息
        try {
            Log.i(TAG, "handleCustomMsg ");
            String customText = new String(elem.getData(), "UTF-8");
            JSONTokener jsonParser = new JSONTokener(customText);
            JSONObject json = (JSONObject) jsonParser.nextValue();
            int action = json.getInt(ILVLiveConstants.CMD_KEY);
            switch (action) {
                case ILVLiveConstants.AVIMCMD_MUlTI_HOST_INVITE: //以定义的上麦消息
                    Log.i(TAG, "handleCustomMsg ");
                    ILVLiveManager.getInstance().upToVideoMember(ILVLiveConstants.VIDEO_MEMBER_AUTH, ILVLiveConstants.VIDEO_MEMBER_ROLE, new ILiveCallBack() {
                        @Override
                        public void onSuccess(Object data) {

                        }

                        @Override
                        public void onError(String module, int errCode, String errMsg) {

                        }
                    });
                    break;
                case ILVLiveConstants.AVIMCMD_THUMBUP://处理点赞
                    Toast.makeText(LiveActivity.this, "thumb up !!!!!!!!!!!!!", Toast.LENGTH_SHORT).show();
                    break;
                case ILVLiveConstants.AVIMCMD_MULTI_CLOSE_INTERACT://关闭消息
                    ILVLiveManager.getInstance().downToNorMember(ILVLiveConstants.NORMAL_MEMBER_AUTH, ILVLiveConstants.NORMAL_MEMBER_ROLE, new ILiveCallBack() {
                        @Override
                        public void onSuccess(Object data) {

                        }

                        @Override
                        public void onError(String module, int errCode, String errMsg) {

                        }
                    });
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

        if (view.getId() == R.id.btn_login) { //登陆房间
            ILiveSDK.getInstance().setMyUserId("" + inputId.getText());
            ILiveLoginManager.getInstance().tilvbLogin(ILiveSDK.getInstance().getMyUserId(), "123456", new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    Toast.makeText(LiveActivity.this, "login success !", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(LiveActivity.this, module + "|login fail " + errCode + " " + errMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }


        if (view.getId() == R.id.create) { //创建房间
            int room = Integer.parseInt("" + roomNum.getText());
            //创建房间配置项
            ILiveRoomOption hostOption = new ILiveRoomOption(null).
                    controlRole("Host")//角色设置
                    .authBits(AVRoomMulti.AUTH_BITS_DEFAULT)//权限设置
                    .cameraId(ILiveConstants.FRONT_CAMERA)//摄像头前置后置
                    .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO);//是否开始半自动接收
            //创建房间
            ILVLiveManager.getInstance().createRoom(room, hostOption, new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    Toast.makeText(LiveActivity.this, "create room  ok", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(LiveActivity.this, module + "|create fail " + errMsg + " " + errMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (view.getId() == R.id.join) {//加入房间
            int room = Integer.parseInt("" + roomNumJoin.getText());
            String hostId = "" + hostIdInput.getText();
            //加入房间配置项
            ILiveRoomOption memberOption = new ILiveRoomOption(hostId)
                    .autoCamera(false) //是否自动打开摄像头
                    .controlRole("NormalMember") //角色设置
                    .authBits(AVRoomMulti.AUTH_BITS_JOIN_ROOM | AVRoomMulti.AUTH_BITS_RECV_AUDIO | AVRoomMulti.AUTH_BITS_RECV_CAMERA_VIDEO | AVRoomMulti.AUTH_BITS_RECV_SCREEN_VIDEO) //权限设置
                    .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO) //是否开始半自动接收
                    .autoMic(false);//是否自动打开mic
            //加入房间
            ILVLiveManager.getInstance().joinRoom(room, memberOption, new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    Toast.makeText(LiveActivity.this, "join room  ok ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(LiveActivity.this, module + "|join fail " + errMsg + " " + errMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (view.getId() == R.id.back) {
            //退出房间
            ILVLiveManager.getInstance().quitRoom(new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    Toast.makeText(LiveActivity.this, "quit room  ok ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(LiveActivity.this, module + "|join fail " + errCode + " " + errMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }


        if (view.getId() == R.id.text_send) {
            //发送消息
            ILiveSDK.getInstance().setMyUserId("" + inputId.getText());
            ILVLiveManager.getInstance().sendGroupTextMsg("" + textInput.getText(), new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    Toast.makeText(LiveActivity.this, "send succ!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {

                }

            });
        }


        if (view.getId() == R.id.invite) {
            //邀请上麦
            String inviteVideo = jasonToString(ILVLiveConstants.AVIMCMD_MUlTI_HOST_INVITE, "");
            ILVLiveManager.getInstance().sendC2CCustomMessage(inviteVideo, "" + memId.getText(), TIMMessagePriority.High, new ILiveCallBack<TIMMessage>() {
                @Override
                public void onSuccess(TIMMessage data) {
                    Toast.makeText(LiveActivity.this, "invite send succ!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {

                }

            });


        }

        if (view.getId() == R.id.close_mem) {
            //关闭上麦
            String inviteVideo = jasonToString(ILVLiveConstants.AVIMCMD_MULTI_CLOSE_INTERACT, "");
            ILVLiveManager.getInstance().sendC2CCustomMessage(inviteVideo, "" + memId.getText(), TIMMessagePriority.High, new ILiveCallBack<TIMMessage>() {
                @Override
                public void onSuccess(TIMMessage data) {
                    Toast.makeText(LiveActivity.this, "invite send succ!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {

                }

            });


        }
        if (view.getId() == R.id.thumbUp) {
            //发送点赞
            String inviteVideo = jasonToString(ILVLiveConstants.AVIMCMD_THUMBUP, "");
            ILVLiveManager.getInstance().sendGroupCustomMessage(inviteVideo, TIMMessagePriority.High, new ILiveCallBack<TIMMessage>() {
                @Override
                public void onSuccess(TIMMessage data) {
                    Toast.makeText(LiveActivity.this, "invite send thumbup succ!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {

                }

            });

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
