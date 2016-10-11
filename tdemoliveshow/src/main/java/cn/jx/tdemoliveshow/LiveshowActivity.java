package cn.jx.tdemoliveshow;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Toast;

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



public class LiveshowActivity extends Activity implements View.OnClickListener {
    Button createBtn, joinbtn, backBtn;
    AVRootView avRootView;
    Button loginBtn;
    EditText inputId, roomNum, roomNumJoin, textInput, memId, hostIdInput;
    private static final String TAG = LiveshowActivity.class.getSimpleName();
    private boolean bLogin = false; // 记录登陆状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liveshow);
        Looper.getMainLooper().setMessageLogging(new LooperMonitor(new LooperMonitor.BlockListener() {
            @Override
            public void onBlockEvent(long realStartTime, long realTimeEnd, long threadTimeStart, long threadTimeEnd) {

            }
        }, 200));
        createBtn = (Button) findViewById(R.id.create);
        joinbtn = (Button) findViewById(R.id.join);
        backBtn = (Button) findViewById(R.id.back);

        avRootView = (AVRootView) findViewById(R.id.av_root_view);

        loginBtn = (Button) findViewById(R.id.btn_login);
        inputId = (EditText) findViewById(R.id.id_input);
        roomNum = (EditText) findViewById(R.id.room_num);
        roomNumJoin = (EditText) findViewById(R.id.room_num_join);
        hostIdInput = (EditText) findViewById(R.id.host_id);


        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        SeekBar seekW = (SeekBar) findViewById(R.id.seek_width);
        seekW.setProgress(100);
        seekW.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ViewGroup.LayoutParams params = avRootView.getLayoutParams();
                params.width = displayMetrics.widthPixels * progress/100;
                avRootView.setLayoutParams(params);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar seekH = (SeekBar) findViewById(R.id.seek_height);
        seekH.setProgress(100);
        seekH.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ViewGroup.LayoutParams params = avRootView.getLayoutParams();
                params.height = displayMetrics.heightPixels * progress/100;
                avRootView.setLayoutParams(params);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        createBtn.setOnClickListener(this);
        joinbtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);


        //初始化SDK
        ILiveSDK.getInstance().initSdk(getApplicationContext(), 1104620500, 107);
        // 关闭IM群组
        ILVLiveConfig liveConfig = (ILVLiveConfig) new ILVLiveConfig()
                .autoRender(true)
                .highAudioQuality(true);

        //初始化直播场景
        ILVLiveManager.getInstance().init(liveConfig);
        //设置渲染界面
        ILVLiveManager.getInstance().setAvVideoView(avRootView);




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
        if (bLogin){    // 关闭界面时注销用户
            ILiveLoginManager.getInstance().tilvbLogout(null);
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_login) { //登陆房间
            ILiveSDK.getInstance().setMyUserId("" + inputId.getText());
            ILiveLoginManager.getInstance().tilvbLogin(ILiveSDK.getInstance().getMyUserId(), "123456", new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    bLogin = true;
                    Toast.makeText(LiveshowActivity.this, "login success !", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(LiveshowActivity.this, module + "|login fail " + errCode + " " + errMsg, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(LiveshowActivity.this, "create room  ok", Toast.LENGTH_SHORT).show();
                    avRootView.getViewByIndex(0).setRotationMode(ILiveConstants.ROTATION_FULL_SCREEN);
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(LiveshowActivity.this, module + "|create fail " + errMsg + " " + errMsg, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(LiveshowActivity.this, "join room  ok ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(LiveshowActivity.this, module + "|join fail " + errMsg + " " + errMsg, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(LiveshowActivity.this, "quit room  ok ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(LiveshowActivity.this, module + "|join fail " + errCode + " " + errMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }




    }

    @Override
    protected void onStart() {
        super.onStart();
    }




}

