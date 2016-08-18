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
import com.tencent.tilvbsdk.TILVBConstants;
import com.tencent.tilvbsdk.TILVBSDK;
import com.tencent.tilvbsdk.business.TILVBCallListener;
import com.tencent.tilvbsdk.business.TILVBCallManager;
import com.tencent.tilvbsdk.core.TILVBRoomManager;
import com.tencent.tilvbsdk.view.AVRootView;
import com.tencent.tilvbsdk.view.AVVideoView;

/**
 * 通话界面
 */
public class CallActivity extends Activity implements TILVBCallListener, View.OnClickListener {
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
            TILVBRoomManager.getInstance().enableCamera(TILVBRoomManager.getInstance().getCurCameraId(), false);
        } else {
            TILVBRoomManager.getInstance().enableCamera(TILVBConstants.FRONT_CAMERA, true);
        }
        bCameraEnable = !bCameraEnable;
        btnCamera.setText(bCameraEnable ? R.string.tip_close_camera : R.string.tip_open_camera);
    }

    private void changeMic() {
        TILVBRoomManager.getInstance().enableMic(!bMicEnalbe);

        bMicEnalbe = !bMicEnalbe;
        btnMic.setText(bMicEnalbe ? R.string.tip_close_mic : R.string.tip_open_mic);
    }

    private void changeSpeaker() {
        if (bSpeaker) {
            TILVBSDK.getInstance().getAvAudioCtrl().setAudioOutputMode(AVAudioCtrl.OUTPUT_MODE_HEADSET);
        } else {
            TILVBSDK.getInstance().getAvAudioCtrl().setAudioOutputMode(AVAudioCtrl.OUTPUT_MODE_SPEAKER);
        }
        bSpeaker = !bSpeaker;
        btnSpeaker.setText(bSpeaker ? R.string.tip_set_headset : R.string.tip_set_speaker);
    }

    private void switchCamera() {
        int mCurCameraId = TILVBRoomManager.getInstance().getCurCameraId();;
        if (TILVBConstants.FRONT_CAMERA == mCurCameraId) {
            TILVBRoomManager.getInstance().switchCamera(TILVBConstants.BACK_CAMERA);
        } else {
            TILVBRoomManager.getInstance().switchCamera(TILVBConstants.FRONT_CAMERA);
        }
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
                    TILVBSDK.getInstance().getAvVideoCtrl().inputBeautyParam(9.0f * progress / 100.0f);
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
        TILVBCallManager.getInstance().addCallListener(this);

        Intent intent = getIntent();
        mHostId = intent.getStringExtra("HostId");
        mCallId = intent.getIntExtra("CallId", 0);

        initView();

        tvTitle.setText("New Call From:\n" + mHostId);
        TILVBCallManager.getInstance().initAvView(avRootView);

        avRootView.setSubCreatedListener(new AVRootView.onSubViewCreatedListener() {
            @Override
            public void onSubViewCreated() {
                // 设置点击小屏切换及可拖动
                final AVVideoView minorView = avRootView.getViewByIndex(1);
                minorView.setDragable(true);
                minorView.setGestureListener(new GestureDetector.SimpleOnGestureListener(){
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        avRootView.swapVideoView(0, 1);
                        return false;
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        TILVBCallManager.getInstance().removeCallListener(this);
        TILVBRoomManager.getInstance().onDestory();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        // library中不能使用switch索引资源id
        if (v.getId() == R.id.btn_end){
            TILVBCallManager.getInstance().endCall(mCallId);
            finish();
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

    // 回调方法
    @Override
    public void onNewIncomingCall(int callId, int callType, String fromUserId, String strTips) {
    }

    /**
     * 会话建立回调
     * @param callId
     */
    @Override
    public void onCallEstablish(int callId) {
        btnEndCall.setVisibility(View.VISIBLE);
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
    public void onException(int i, int i1, String s) {

    }
}
