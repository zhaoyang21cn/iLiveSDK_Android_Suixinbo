package com.tencent.tdemovideocall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.av.sdk.AVAudioCtrl;
import com.tencent.av.sdk.AVVideoCtrl;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.business.callbusiness.ILVCallListener;
import com.tencent.ilivesdk.business.callbusiness.ILVCallManager;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.ilivesdk.view.AVVideoView;

/**
 * 通话界面
 */
public class CallActivity extends Activity implements ILVCallListener, View.OnClickListener {
    private Button btnEndCall, btnCamera, btnMic, btnSpeaker;
    private AVRootView avRootView;
    private TextView tvTitle;
    private RelativeLayout rlControl;
    private LinearLayout llBeauty;
    private SeekBar sbBeauty;

    private String mHostId;
    private int mCallId;
    private int mBeautyRate;

    private boolean bCameraEnable = true;
    private boolean bMicEnalbe = true;
    private boolean bSpeaker = true;
    private int mCurCameraId = ILiveConstants.FRONT_CAMERA;
    AVVideoCtrl.EnableCameraCompleteCallback mCameraCallback = new AVVideoCtrl.EnableCameraCompleteCallback(){
        @Override
        protected void onComplete(boolean b, int i) {
            super.onComplete(b, i);
        }
    };
    AVVideoCtrl.SwitchCameraCompleteCallback mSwitchCameraCompleteCallback = new AVVideoCtrl.SwitchCameraCompleteCallback(){
        @Override
        protected void onComplete(int i, int i1) {
            super.onComplete(i, i1);
        }
    };

    private void initView() {
        avRootView = (AVRootView) findViewById(R.id.av_root_view);
        btnEndCall = (Button) findViewById(R.id.btn_end);
        btnSpeaker = (Button) findViewById(R.id.btn_speaker);
        tvTitle = (TextView) findViewById(R.id.tv_call_title);

        btnCamera = (Button) findViewById(R.id.btn_camera);
        btnMic = (Button) findViewById(R.id.btn_mic);

        llBeauty = (LinearLayout) findViewById(R.id.ll_beauty_setting);
        rlControl = (RelativeLayout) findViewById(R.id.rl_control);

        btnEndCall.setVisibility(View.VISIBLE);
    }

    private void changeCamera() {
        if (bCameraEnable) {
            ILiveSDK.getInstance().getAvVideoCtrl().enableCamera(mCurCameraId, false, mCameraCallback);
            avRootView.closeUserView(ILiveSDK.getInstance().getMyUserId(), true);
        } else {
            ILiveSDK.getInstance().getAvVideoCtrl().enableCamera(mCurCameraId, true, mCameraCallback);
        }
        bCameraEnable = !bCameraEnable;
        btnCamera.setText(bCameraEnable ? R.string.tip_close_camera : R.string.tip_open_camera);
    }

    private void changeMic() {
        if (bMicEnalbe) {
            ILiveSDK.getInstance().getAvAudioCtrl().enableMic(false);
        } else {
            ILiveSDK.getInstance().getAvAudioCtrl().enableMic(true);
        }

        bMicEnalbe = !bMicEnalbe;
        btnMic.setText(bMicEnalbe ? R.string.tip_close_mic : R.string.tip_open_mic);
    }

    private void changeSpeaker() {
        if (bSpeaker) {
            ILiveSDK.getInstance().getAvAudioCtrl().setAudioOutputMode(AVAudioCtrl.OUTPUT_MODE_HEADSET);
        } else {
            ILiveSDK.getInstance().getAvAudioCtrl().setAudioOutputMode(AVAudioCtrl.OUTPUT_MODE_SPEAKER);
        }
        bSpeaker = !bSpeaker;
        btnSpeaker.setText(bSpeaker ? R.string.tip_set_headset : R.string.tip_set_speaker);
    }

    private void switchCamera() {
        mCurCameraId = (ILiveConstants.FRONT_CAMERA==mCurCameraId) ? ILiveConstants.BACK_CAMERA : ILiveConstants.FRONT_CAMERA;
        ILiveSDK.getInstance().getAvVideoCtrl().switchCamera(mCurCameraId, mSwitchCameraCompleteCallback);
    }

    private void setBeauty() {
        if (null == sbBeauty) {
            sbBeauty = (SeekBar) findViewById(R.id.sb_beauty_progress);
            sbBeauty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub
                    Toast.makeText(CallActivity.this, "beauty " + mBeautyRate + "%", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    // TODO Auto-generated method stub
                    mBeautyRate = progress;
                    ILiveSDK.getInstance().getAvVideoCtrl().inputBeautyParam(9.0f * progress / 100.0f);
                }
            });
        }
        llBeauty.setVisibility(View.VISIBLE);
        rlControl.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_call);

        // 添加通话回调
        ILVCallManager.getInstance().addCallListener(this);

        Intent intent = getIntent();
        mHostId = intent.getStringExtra("HostId");
        mCallId = intent.getIntExtra("CallId", 0);

        initView();

        tvTitle.setText("New Call From:\n" + mHostId);
        ILVCallManager.getInstance().initAvView(avRootView);
    }

    @Override
    protected void onResume() {
        ILVCallManager.getInstance().onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        ILVCallManager.getInstance().onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        ILVCallManager.getInstance().removeCallListener(this);
        ILVCallManager.getInstance().onDestory();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        ILVCallManager.getInstance().endCall(mCallId);
    }

    @Override
    public void onClick(View v) {
        // library中不能使用switch索引资源id
        if (v.getId() == R.id.btn_end){
            ILVCallManager.getInstance().endCall(mCallId);
        }else if (v.getId() == R.id.btn_camera){
            changeCamera();
        }else if(v.getId() == R.id.btn_mic){
            changeMic();
        }else if(v.getId() == R.id.btn_switch_camera){
            switchCamera();
        }else if(v.getId() == R.id.btn_speaker){
            changeSpeaker();
        }else if(v.getId() == R.id.btn_beauty){
            setBeauty();
        }else if(v.getId() == R.id.btn_beauty_setting_finish){
            llBeauty.setVisibility(View.GONE);
            rlControl.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 会话建立回调
     * @param callId
     */
    @Override
    public void onCallEstablish(int callId) {
        btnEndCall.setVisibility(View.VISIBLE);

        // 设置点击小屏切换及可拖动
        final AVVideoView minorView = avRootView.getViewByIndex(1);
        minorView.setDragable(true);
        minorView.setGestureListener(new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                avRootView.swapVideoView(0, 1);
                //roomMgr.
                return false;
            }
        });
    }

    /**
     *  会话结束回调
     * @param callId
     * @param endResult 结束原因
     * @param endInfo   结束描述
     */
    @Override
    public void onCallEnd(int callId, int endResult, String endInfo) {
        finish();
    }

    @Override
    public void onException(int iExceptionId, int errCode, String errMsg) {

    }
}
