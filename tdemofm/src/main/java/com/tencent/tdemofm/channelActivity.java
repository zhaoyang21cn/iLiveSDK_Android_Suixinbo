package com.tencent.tdemofm;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.smp.soundtouchandroid.SoundTouch;
import com.tencent.av.sdk.AVAudioCtrl;
import com.tencent.av.sdk.AVError;
import com.tencent.tilvbsdk.TILVBCallBack;
import com.tencent.tilvbsdk.TILVBConstants;
import com.tencent.tilvbsdk.TILVBSDK;
import com.tencent.tilvbsdk.core.TILVBAudioManager;
import com.tencent.tilvbsdk.core.TILVBLoginManager;
import com.tencent.tilvbsdk.core.TILVBRoom;
import com.tencent.tilvbsdk.core.TILVBRoomConfig;
import com.tencent.tilvbsdk.core.TILVBRoomOption;

/**
 * 变声Activity
 */
public class channelActivity extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener{
    private static final String TAG = "channelActivity";
    private LinearLayout llMain, llLogin, llChannel, llControl, llChange;
    private EditText etId, etChannel, etWavFile;
    private TextView tvId, tvBgVolume, tvPitchSemi, tvSpeed;
    private Button btnMic;
    private CheckBox cbBgMusic, cbChange;
    private SeekBar sbBkVolume, sbPitchSemi, sbSpeed;

    private boolean bMicEnable = false;
    private WaveFileReader mReader = null;
    private int position = 0;
    private Object obj = new Object();

    private SoundTouch mSoundTouch;
    private boolean bEnterRoom = false, bLogin = false;

    private AVAudioCtrl.RegistAudioDataCompleteCallback mAudioDataCompleteCallback = new AVAudioCtrl.RegistAudioDataCompleteCallback(){
        @Override
        protected int onComplete(AVAudioCtrl.AudioFrame audioFrame, int srcType) {
            if (audioFrame==null)
                return AVError.AV_ERR_FAILED;

            if (AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_MIXTOSEND ==  srcType) {  // 语音发送混入背景音乐
                synchronized (obj) {
                    if (null != mReader) {
                        audioFrame.sampleRate = (int) mReader.getSampleRate();
                        audioFrame.channelNum = mReader.getNumChannels();
                        audioFrame.bits = mReader.getBitPerSample();
                        audioFrame.dataLen = audioFrame.sampleRate * audioFrame.channelNum * 2 / 50;
                        if (position + audioFrame.dataLen > mReader.getDataLen()) {
                            position = 0;
                        }
                        int len = audioFrame.data.length;
                        System.arraycopy(mReader.getData(), position, audioFrame.data, 0, len);
                        position += len;
                    }
                }
            }else if (AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_VOICEDISPOSE == srcType){   // 变声
                if (cbChange.isChecked()){
                    mSoundTouch.putBytes(audioFrame.data);
                    mSoundTouch.getBytes(audioFrame.data);
                }
            }
            return AVError.AV_OK;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_activity);

        TILVBSDK.getInstance().initSdk(getApplicationContext(), 1104620500, 107);

        TILVBRoom.getInstance().init(new TILVBRoomConfig()
                .imSupport(false)
                .highAudioQuality(true)
                .autoRender(false));

        mSoundTouch = new SoundTouch(0, 2, 1, 2, 1, 1);

        initView();
    }

    @Override
    public void onBackPressed() {
        if (bEnterRoom){
            quit(bLogin);
        }else if (bLogin){
            logout();
        }
        super.onBackPressed();
    }

    private void initView(){
        llMain = (LinearLayout)findViewById(R.id.ll_main);
        llLogin = (LinearLayout)findViewById(R.id.ll_login);
        llChannel = (LinearLayout)findViewById(R.id.ll_channel);
        llControl = (LinearLayout)findViewById(R.id.ll_channel_ctrl);
        llChange = (LinearLayout)findViewById(R.id.ll_change);

        etId = (EditText)findViewById(R.id.et_id);
        etChannel = (EditText)findViewById(R.id.et_channel);
        etWavFile = (EditText)findViewById(R.id.et_wav_file);

        btnMic = (Button)findViewById(R.id.btn_mic);

        tvId = (TextView)findViewById(R.id.tv_id);
        tvBgVolume = (TextView)findViewById(R.id.tv_bg_volume);
        tvPitchSemi = (TextView)findViewById(R.id.tv_pitchsemi);
        tvSpeed = (TextView)findViewById(R.id.tv_speed);

        cbBgMusic = (CheckBox)findViewById(R.id.cb_bg_music);
        cbChange = (CheckBox)findViewById(R.id.cb_change_voice);

        sbBkVolume = (SeekBar)findViewById(R.id.sb_bg_volume);
        sbPitchSemi = (SeekBar)findViewById(R.id.sb_pitchsemi);
        sbSpeed = (SeekBar)findViewById(R.id.sb_speed);

        sbPitchSemi.setOnSeekBarChangeListener(this);
        sbSpeed.setOnSeekBarChangeListener(this);
        sbBkVolume.setOnSeekBarChangeListener(this);


        cbBgMusic.setOnCheckedChangeListener(this);
        cbChange.setOnCheckedChangeListener(this);

        etWavFile.setText("/sdcard/oneMin.wav");
    }

    /**
     *  SDK登陆
     */
    private void login(String id){
        TILVBLoginManager.getInstance().tilvbLogin(id, "123", new TILVBCallBack() {
            @Override
            public void onSuccess(Object o) {
                llMain.setVisibility(View.VISIBLE);
                llLogin.setVisibility(View.GONE);
                llControl.setVisibility(View.GONE);
                llChannel.setVisibility(View.VISIBLE);
                tvId.setText(TILVBSDK.getInstance().getMyUserId());
                bLogin = true;
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void onLogout(){
        llMain.setVisibility(View.GONE);
        llLogin.setVisibility(View.VISIBLE);
        llControl.setVisibility(View.GONE);
        llChannel.setVisibility(View.GONE);
        bLogin = false;
    }

    private void onRoomQuit(final boolean bNeedLogout){
        llControl.setVisibility(View.GONE);
        llChannel.setVisibility(View.VISIBLE);
        bEnterRoom = false;
        if (bNeedLogout){
            logout();
        }
    }

    private void logout(){
        TILVBLoginManager.getInstance().tilvbLogout(new TILVBCallBack() {
            @Override
            public void onSuccess(Object data) {
                onLogout();
            }

            @Override
            public void onError(int errCode, String errMsg) {
                onLogout();
            }
        });
    }

    /**
     *  加入频道
     */
    private void join(String strChannel){
        int channel = Integer.valueOf(strChannel);
        TILVBRoomOption option = new TILVBRoomOption(channel, TILVBSDK.getInstance().getMyUserId())
                .autoCamera(false)
                .autoMic(false);
        TILVBRoom.getInstance().createRoom(option, new TILVBCallBack() {
            @Override
            public void onSuccess(Object o) {
                llControl.setVisibility(View.VISIBLE);
                llChannel.setVisibility(View.GONE);
                bEnterRoom = true;
            }

            @Override
            public void onError(int i, String s) {
                Log.v(TAG, "create room failed: i:" + i + "|" + s);
            }
        });
    }

    /**
     * 修改Mic
     */
    private int changeMic(){
        int ret = TILVBConstants.NO_ERR;
        if (bMicEnable){
            ret = TILVBAudioManager.getInstance().closeMic();
        }else{
            ret = TILVBAudioManager.getInstance().openMic();

            TILVBAudioManager.getInstance().registAudioDataCallback(AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_MIXTOSEND, mAudioDataCompleteCallback);
            TILVBAudioManager.getInstance().registAudioDataCallback(AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_VOICEDISPOSE, mAudioDataCompleteCallback);
        }

        if (TILVBConstants.NO_ERR == ret){
            bMicEnable = !bMicEnable;
            btnMic.setText(bMicEnable ? R.string.tip_close_mic : R.string.tip_open_mic);
        }

        return ret;
    }

    /**
     * 退出频道
     */
    private void quit(final boolean bNeedLogout){
        TILVBRoom.getInstance().quitRoom(new TILVBCallBack() {
            @Override
            public void onSuccess(Object o) {
                onRoomQuit(bNeedLogout);
            }

            @Override
            public void onError(int i, String s) {
                onRoomQuit(bNeedLogout);
            }
        });
    }

    /**
     * 按钮事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login){
            if (!TextUtils.isEmpty(etId.getText().toString())){
                login(etId.getText().toString());
            }
        }else if (v.getId() == R.id.btn_logout){
            logout();
        }else if (v.getId() == R.id.btn_join_channel){
            if (!TextUtils.isEmpty(etChannel.getText().toString())){
                join(etChannel.getText().toString());
            }
        }else if (v.getId() == R.id.btn_quit){
            quit(false);
        }else if (v.getId() == R.id.btn_mic){
            changeMic();
        }
    }

    /**
     * 进度条更新事件
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId() == R.id.sb_bg_volume){  // 背景音量
            float volume = (float)progress / 100;
            tvBgVolume.setText(progress+"%");
            TILVBAudioManager.getInstance().setAudioDataVolume(AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_MIXTOSEND, volume);
        }else if (seekBar.getId() == R.id.sb_pitchsemi){     // 语调
            float pitchSemi = progress - 12;
            tvPitchSemi.setText(""+pitchSemi);
            mSoundTouch.setTempoChange(pitchSemi);
        }else if (seekBar.getId() == R.id.sb_speed){         // 语速
            float speed = progress - 50;
            tvSpeed.setText(""+speed);
            mSoundTouch.setTempoChange(speed);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    /**
     * 复选框更新事件
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.cb_bg_music){
            if (isChecked) {
                sbBkVolume.setProgress(100);
                String filePath = etWavFile.getText().toString();
                if (null != filePath) {
                    if(filePath.endsWith("wav")) {
                        mReader = new WaveFileReader(filePath);
                        // 根据背景音乐调整设备参数
                        AVAudioCtrl.AudioFrameDesc audioFrameDesc = new AVAudioCtrl.AudioFrameDesc();
                        audioFrameDesc.sampleRate = (int) mReader.getSampleRate();
                        audioFrameDesc.channelNum = mReader.getNumChannels();
                        audioFrameDesc.bits = mReader.getBitPerSample();

                        audioFrameDesc.srcTye = AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_MIXTOSEND;
                        TILVBAudioManager.getInstance().setAudioDataFormat(AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_MIXTOSEND, audioFrameDesc);

                        mSoundTouch.setChannels(mReader.getNumChannels());
                        mSoundTouch.setSamplingRate((int)mReader.getSampleRate());
                    }
                }
            } else {
                mReader = null;
            }
        }else if(buttonView.getId() == R.id.cb_change_voice){
            if (isChecked) {
                mSoundTouch.clearBuffer();
                sbPitchSemi.setProgress(12);
                sbSpeed.setProgress(50);
                tvPitchSemi.setText("0");
                tvSpeed.setText("0");
            }
            llChange.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        }
    }
}
