package com.tencent.tdemofm;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Toast;

import com.smp.soundtouchandroid.SoundTouch;
import com.tencent.av.sdk.AVAudioCtrl;
import com.tencent.av.sdk.AVError;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.core.ILiveRoomConfig;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.core.ILiveRoomOption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * 变声Activity
 */
public class channelActivity extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener{
    private static final String TAG = "channelActivity";
    private LinearLayout llMain, llLogin, llChannel, llControl, llChange;
    private EditText etId, etChannel, etWavFile;
    private TextView tvId, tvBgVolume, tvPitchSemi, tvSpeed, tvLog;
    private Button btnMic;
    private CheckBox cbBgMusic, cbChange;
    private SeekBar sbBkVolume, sbPitchSemi, sbSpeed;

    private boolean bMicEnable = false;
    private String mStrLog="", mResPath;
    private WaveFileReader mReader = null;
    private int position = 0;
    private Object obj = new Object();

    private SoundTouch mSoundTouch;
    private boolean bEnterRoom = false, bLogin = false;

    private DiscussSer mDisSer;

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
            case 0:
                addLog("拷贝资源文件成功");
                break;
            case 1:
                addLog("拷贝资源文件失败:"+msg.obj);
                break;
            }
        }
    };

    private AVAudioCtrl.RegistAudioDataCompleteCallback mAudioDataCompleteCallback = new AVAudioCtrl.RegistAudioDataCompleteCallback(){
        @Override
        protected int onComplete(AVAudioCtrl.AudioFrame audioFrame, int srcType) {
            if (audioFrame==null)
                return AVError.AV_ERR_FAILED;

            if (AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_MIXTOSEND ==  srcType) {  // 语音发送混入背景音乐
                synchronized (obj) {
                    if (null != mReader && mReader.getDataLen() > audioFrame.data.length) {
                        int len = audioFrame.data.length;
                        if (position + len > mReader.getDataLen()) {
                            position = 0;
                        }
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

        mSoundTouch = new SoundTouch(0, 2, 1, 2, 1, 1);
        ILiveSDK.getInstance().initSdk(getApplicationContext(), 1104620500, 107);
        ILiveRoomManager.getInstance().init(new ILiveRoomConfig());
        initView();
        addLog("初始化ILiveSDK...");
        initResource();
    }

    @Override
    public void onBackPressed() {
        if (bEnterRoom){
            quit(bLogin);
        }
        if (View.VISIBLE == llMain.getVisibility()){
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
        tvLog = (TextView)findViewById(R.id.tv_log);

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
    }

    private void initResource(){
        addLog("初始化资源文件...");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream is = getResources().openRawResource(R.raw.onemin);
                    File tempfile = File.createTempFile("tempfile", ".wav", getDir("filez", 0));

                    FileOutputStream os=new FileOutputStream(tempfile);
                    byte[] buffer=new byte[16000];
                    int length=0;
                    while((length=is.read(buffer))!=-1){
                        os.write(buffer,0,length);
                    }
                    os.close();
                    is.close();
                    mResPath = tempfile.getPath();
                    mHandler.sendEmptyMessage(0);
                }catch (Exception e){
                    Log.e("ILVB-DBG", "open resource failed:"+e.toString());
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = e.toString();
                    mHandler.sendMessage(msg);
                }
            }
        });
        thread.start();
    }

    private void addLog(String strInfo){
        mStrLog += strInfo + "\r\n";
        tvLog.setText(mStrLog);
    }

    /**
     *  SDK登陆
     */
    private void login(String id){
        addLog("正在登录用户"+id+"...");
        ILiveLoginManager.getInstance().tilvbLogin(id, "123", new ILiveCallBack() {
            @Override
            public void onSuccess(Object o) {
                llMain.setVisibility(View.VISIBLE);
                llLogin.setVisibility(View.GONE);
                llControl.setVisibility(View.GONE);
                llChannel.setVisibility(View.VISIBLE);
                tvId.setText(ILiveSDK.getInstance().getMyUserId());
                mDisSer = new DiscussSer("FM", new DiscussSer.onInitListener() {
                    @Override
                    public void onInitComplete(int result) {
                        mDisSer.quaryStatus(new DiscussSer.onGetStatusList() {
                            @Override
                            public void onQueryComplete(List<String> list) {
                                if (null != list){
                                    for (String id : list){
                                        addLog("频道: "+id);
                                    }
                                }
                            }
                        });
                    }
                });
                addLog("登录成功，欢迎用户"+ILiveSDK.getInstance().getMyUserId());
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Toast.makeText(channelActivity.this, "login failed:"+module+"|"+errCode+"|"+errMsg, Toast.LENGTH_SHORT).show();
                addLog("登录失败:"+module+"|"+errCode+"|"+errMsg);
            }
        });
    }

    private void onLogout(){
        llMain.setVisibility(View.GONE);
        llLogin.setVisibility(View.VISIBLE);
        llControl.setVisibility(View.GONE);
        llChannel.setVisibility(View.GONE);
    }

    private void innerLogout(){
        ILiveLoginManager.getInstance().tilvbLogout(new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                onLogout();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                onLogout();
            }
        });
    }

    private void logout(){
        if (bEnterRoom){    // 先退出房间
            quit(true);
        }else {
            innerLogout();
        }
    }
    private void onRoomQuit(final boolean bNeedLogout){
        llControl.setVisibility(View.GONE);
        llChannel.setVisibility(View.VISIBLE);
        mDisSer.modifyStatus("");
        bEnterRoom = false;
        if (bNeedLogout){
            innerLogout();
        }
    }

    /**
     *  加入频道
     */
    private void join(final String strChannel, boolean bCreated){
        int channel = Integer.valueOf(strChannel);
        addLog((bCreated ? "尝试创建广播频道:" : "尝试加入广播频道:")+strChannel+"...");
        ILiveRoomOption option = new ILiveRoomOption(ILiveSDK.getInstance().getMyUserId())
                .imsupport(false)
                .autoCamera(false)
                .autoMic(false);
        ILiveCallBack callBack = new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                llControl.setVisibility(View.VISIBLE);
                llChannel.setVisibility(View.GONE);
                bEnterRoom = true;
                mDisSer.modifyStatus(strChannel);
                addLog("进入广播频道"+strChannel+"成功");
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Log.v(TAG, "create room failed: errCode:" + errCode + "|" + errMsg);
                Toast.makeText(channelActivity.this, "join room failed:"+module+"|"+errCode+"|"+errMsg, Toast.LENGTH_SHORT).show();
                if (10021 == errCode){
                    addLog("创建广播频道失败:频道已存在，尝试加入频道"+strChannel);
                    join(strChannel, false);
                }else{
                    addLog("进入广播频道失败:"+module+"|"+errCode+"|"+errMsg);
                }
            }
        };

        if (bCreated){
            ILiveRoomManager.getInstance().createRoom(channel, option, callBack);
        }else{
            ILiveRoomManager.getInstance().joinRoom(channel, option, callBack);
        }
    }

    /**
     * 修改Mic
     */
    private int changeMic(){
        int ret = ILiveConstants.NO_ERR;
        if (bMicEnable){
            ret = ILiveRoomManager.getInstance().enableMic(false);
            cbBgMusic.setChecked(false);
            cbChange.setChecked(false);
            ILiveSDK.getInstance().getAvAudioCtrl().unregistAudioDataCallbackAll();
        }else{
            ret = ILiveRoomManager.getInstance().enableMic(true);

            ILiveSDK.getInstance().getAvAudioCtrl().registAudioDataCallback(AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_MIXTOSEND, mAudioDataCompleteCallback);
            ILiveSDK.getInstance().getAvAudioCtrl().registAudioDataCallback(AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_VOICEDISPOSE, mAudioDataCompleteCallback);
        }

        if (ILiveConstants.NO_ERR == ret){
            bMicEnable = !bMicEnable;
            btnMic.setText(bMicEnable ? R.string.tip_close_mic : R.string.tip_open_mic);
        }

        return ret;
    }

    /**
     * 退出频道
     */
    private void quit(final boolean bNeedLogout){
        addLog("正在退出广播频道...");
        ILiveRoomManager.getInstance().quitRoom(new ILiveCallBack() {
            @Override
            public void onSuccess(Object o) {
                addLog("退出广播频道成功");
                onRoomQuit(bNeedLogout);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                addLog("退出广播频道失败:"+module+"|"+errCode+"|"+errMsg);
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
        }else if(v.getId() == R.id.btn_logout){
            logout();
        }else if (v.getId() == R.id.btn_join_channel){
            if (!TextUtils.isEmpty(etChannel.getText().toString())){
                join(etChannel.getText().toString(), false);
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
            ILiveSDK.getInstance().getAvAudioCtrl().setAudioDataVolume(AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_MIXTOSEND, volume);
        }else if (seekBar.getId() == R.id.sb_pitchsemi){     // 语调
            float pitchSemi = progress - 12;
            tvPitchSemi.setText(""+pitchSemi);
            mSoundTouch.setPitchSemi(pitchSemi);
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

                if (!TextUtils.isEmpty(filePath) && filePath.endsWith("wav")){
                    mReader = new WaveFileReader(filePath);
                    addLog("混入背景音乐:"+filePath);
                }else if (!TextUtils.isEmpty(mResPath)){  // 加载资源
                    mReader = new WaveFileReader(mResPath);
                    addLog("混入默认背景音乐");
                }else{
                    mReader = null;
                    return;
                }

                // 根据背景音乐调整设备参数
                AVAudioCtrl.AudioFrameDesc audioFrameDesc = new AVAudioCtrl.AudioFrameDesc();
                audioFrameDesc.sampleRate = (int) mReader.getSampleRate();
                audioFrameDesc.channelNum = mReader.getNumChannels();
                audioFrameDesc.bits = mReader.getBitPerSample();

                Log.v(TAG, "read wav file rate:"+mReader.getSampleRate()+", channel:"+mReader.getNumChannels()+", bits:"+mReader.getBitPerSample());
                audioFrameDesc.srcTye = AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_MIXTOSEND;
                ILiveSDK.getInstance().getAvAudioCtrl().setAudioDataFormat(AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_MIXTOSEND, audioFrameDesc);

                mSoundTouch.setChannels(mReader.getNumChannels());
                mSoundTouch.setSamplingRate((int)mReader.getSampleRate());
            } else {
                mReader = null;
                addLog("关闭背景音乐");
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
