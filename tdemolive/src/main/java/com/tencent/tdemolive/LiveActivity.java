package com.tencent.tdemolive;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.TIMMessage;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.business.livebusiness.ILVCustomCmd;
import com.tencent.ilivesdk.business.livebusiness.ILVLiveConfig;
import com.tencent.ilivesdk.business.livebusiness.ILVLiveConstants;
import com.tencent.ilivesdk.business.livebusiness.ILVLiveManager;
import com.tencent.ilivesdk.business.livebusiness.ILVText;
import com.tencent.ilivesdk.core.ILiveLog;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.core.ILiveRoomOption;
import com.tencent.ilivesdk.view.AVRootView;

import java.util.ArrayList;
import java.util.List;

public class LiveActivity extends Activity implements View.OnClickListener {
    Button createBtn, joinbtn, backBtn, sendBtn, inviteBtn, closeMemBtn;
    AVRootView avRootView;
    Button logoutBtn,loginLive,registLive;
    EditText roomNum, roomNumJoin, textInput, memId, hostIdInput,myId,myPwd;
    TextView myLoginId;
    FrameLayout loginView;
    private static final String TAG = LiveActivity.class.getSimpleName();
    private final int REQUEST_PHONE_PERMISSIONS = 0;

    private boolean bLogin = false, bEnterRoom = false;

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

        loginLive = (Button) findViewById(R.id.login_live);
        registLive = (Button) findViewById(R.id.register_live);
        loginView =  (FrameLayout)findViewById(R.id.login_fragment);

        avRootView = (AVRootView) findViewById(R.id.av_root_view);
        ILVLiveManager.getInstance().setAvVideoView(avRootView);

        logoutBtn = (Button) findViewById(R.id.btn_logout);
        roomNum = (EditText) findViewById(R.id.room_num);
        roomNumJoin = (EditText) findViewById(R.id.room_num_join);
        textInput = (EditText) findViewById(R.id.text_input);
        hostIdInput = (EditText) findViewById(R.id.host_id);
        memId = (EditText) findViewById(R.id.mem_id);
        myId= (EditText) findViewById(R.id.my_id);
        myPwd =(EditText) findViewById(R.id.my_pwd);
        myLoginId = (TextView) findViewById(R.id.my_login_id);

        createBtn.setOnClickListener(this);
        joinbtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);
        inviteBtn.setOnClickListener(this);
        closeMemBtn.setOnClickListener(this);
        loginLive.setOnClickListener(this);
        registLive.setOnClickListener(this);
        checkPermission();




        //初始化SDK
        ILiveSDK.getInstance().initSdk(getApplicationContext(), 1400013700, 7285);
        // 关闭IM群组
        ILVLiveConfig liveConfig = new ILVLiveConfig();

        liveConfig.setLiveMsgListener(new ILVLiveConfig.ILVLiveMsgListener() {
            @Override
            public void onNewTextMsg(String text, String id) {
                Toast.makeText(LiveActivity.this, "onNewTextMsg : " + text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNewCmdMsg(int cmd, String param, String id) {
                switch (cmd) {
                    case ILVLiveConstants.ILVLIVE_CMD_INVITE:
                        Toast.makeText(LiveActivity.this, "onNewCmdMsg : received a invitation! ", Toast.LENGTH_SHORT).show();
                        ILiveLog.d(TAG, "ILVB-LiveApp|received ");
                        ILVLiveManager.getInstance().upToVideoMember(ILVLiveConstants.VIDEO_MEMBER_AUTH, ILVLiveConstants.VIDEO_MEMBER_ROLE, new ILiveCallBack() {
                            @Override
                            public void onSuccess(Object data) {

                            }

                            @Override
                            public void onError(String module, int errCode, String errMsg) {

                            }
                        });
                        break;
                    case  ILVLiveConstants.ILVLIVE_CMD_INVITE_CANCEL:

                        break;
                    case ILVLiveConstants.ILVLIVE_CMD_INVITE_CLOSE:
                        ILVLiveManager.getInstance().downToNorMember(ILVLiveConstants.NORMAL_MEMBER_AUTH, ILVLiveConstants.NORMAL_MEMBER_ROLE, new ILiveCallBack() {
                            @Override
                            public void onSuccess(Object data) {

                            }

                            @Override
                            public void onError(String module, int errCode, String errMsg) {

                            }
                        });
                        break;
                    case ILVLiveConstants.ILVLIVE_CMD_INTERACT_AGREE:
                        break;
                    case  ILVLiveConstants.ILVLIVE_CMD_INTERACT_REJECT:
                        break;
                }

            }

            @Override
            public void onNewCustomMsg(int cmd, String param, String id) {
                Toast.makeText(LiveActivity.this, "cmd "+ cmd, Toast.LENGTH_SHORT).show();

            }
        });
        //初始化直播场景
        ILVLiveManager.getInstance().init(liveConfig);
        //设置渲染界面


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


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void logout(boolean quit) {
        if (bLogin) {
            ILiveLoginManager.getInstance().iLiveLogout(null);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        if (bEnterRoom) {
            ILiveRoomManager.getInstance().quitRoom(new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    logout(true);
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    logout(true);
                }
            });
        } else {
            logout(true);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_logout) { //登陆房间
            ILiveLoginManager.getInstance().iLiveLogout( new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    bLogin = false;
                    loginView.setVisibility(View.VISIBLE);
                    Toast.makeText(LiveActivity.this, "logout success !", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {

                    Toast.makeText(LiveActivity.this, module + "|logout fail " + errCode + " " + errMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }


        if (view.getId() == R.id.create) { //创建房间
            int room = Integer.parseInt("" + roomNum.getText());
            //创建房间配置项
            ILiveRoomOption hostOption = new ILiveRoomOption(null).
                    controlRole("Host")//角色设置
                    .autoFocus(true)
                    .authBits(AVRoomMulti.AUTH_BITS_DEFAULT)//权限设置
                    .cameraId(ILiveConstants.FRONT_CAMERA)//摄像头前置后置
                    .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO);//是否开始半自动接收
            //创建房间
            ILVLiveManager.getInstance().createRoom(room, hostOption, new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    Toast.makeText(LiveActivity.this, "create room  ok", Toast.LENGTH_SHORT).show();
                    logoutBtn.setVisibility(View.INVISIBLE);
                    backBtn.setVisibility(View.VISIBLE);
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
                    bEnterRoom = true;
                    Toast.makeText(LiveActivity.this, "join room  ok ", Toast.LENGTH_SHORT).show();
                    logoutBtn.setVisibility(View.INVISIBLE);
                    backBtn.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(LiveActivity.this, module + "|join fail " + errMsg + " " + errMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (view.getId() == R.id.back) {
            if (avRootView != null)
                avRootView.clearUserView();
            //退出房间
            ILVLiveManager.getInstance().quitRoom(new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    bEnterRoom = false;
                    Toast.makeText(LiveActivity.this, "quit room  ok ", Toast.LENGTH_SHORT).show();
                    logoutBtn.setVisibility(View.VISIBLE);
                    backBtn.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(LiveActivity.this, module + "|join fail " + errCode + " " + errMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }


        if (view.getId() == R.id.text_send) {
            //发送消息
            ILVText iliveText = new ILVText("ss", "", ILVLiveConstants.GROUP_TYPE);
            iliveText.setText("" + textInput.getText());
            //发送消息
            ILVLiveManager.getInstance().sendText(iliveText, new ILiveCallBack() {
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
            ILVCustomCmd cmd = new ILVCustomCmd();
            cmd.setCmd(ILVLiveConstants.ILVLIVE_CMD_INVITE);
            cmd.setType(ILVLiveConstants.C2C_TYPE);
            cmd.setDestid("" + memId.getText());
            cmd.setParam("");
            ILVLiveManager.getInstance().sendCustomCmd(cmd, new ILiveCallBack<TIMMessage>() {
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
            ILVCustomCmd cmd = new ILVCustomCmd();
            cmd.setCmd(ILVLiveConstants.ILVLIVE_CMD_INVITE_CLOSE);
            cmd.setType(ILVLiveConstants.C2C_TYPE);
            cmd.setDestid("" + memId.getText());
            cmd.setParam("");
            ILVLiveManager.getInstance().sendCustomCmd(cmd, new ILiveCallBack<TIMMessage>() {
                @Override
                public void onSuccess(TIMMessage data) {
                    Toast.makeText(LiveActivity.this, "invite send succ!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {

                }

            });

        }

        if(view.getId()==R.id.register_live){
            Log.i(TAG, "onClick: register ");
            ILiveLoginManager.getInstance().tlsRegister(""+myId.getText(), ""+myPwd.getText(), new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    Toast.makeText(LiveActivity.this, "register suc !!!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(LiveActivity.this, "register failed : "+errMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }

        if(view.getId()==R.id.login_live){
            ILiveLoginManager.getInstance().tlsLogin("" + myId.getText(), "" + myPwd.getText(), new ILiveCallBack<String>() {
                @Override
                public void onSuccess(String data) {
                    ILiveLoginManager.getInstance().iLiveLogin("" + myId.getText(), data, new ILiveCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            bLogin = true;
                            Toast.makeText(LiveActivity.this, "login success !", Toast.LENGTH_SHORT).show();
                            myLoginId.setText(""+ILiveLoginManager.getInstance().getMyUserId());
                            loginView.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError(String module, int errCode, String errMsg) {
                            Toast.makeText(LiveActivity.this, module + "|login fail " + errCode + " " + errMsg, Toast.LENGTH_SHORT).show();
                        }
                    });
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


    void checkPermission() {
        final List<String> permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.CAMERA);
            if ((checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.RECORD_AUDIO);
            if ((checkSelfPermission(Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.WAKE_LOCK);
            if ((checkSelfPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.MODIFY_AUDIO_SETTINGS);
            if ((checkSelfPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.MODIFY_AUDIO_SETTINGS);
            if (permissionsList.size() != 0) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_PHONE_PERMISSIONS);
            }
        }
    }

}
