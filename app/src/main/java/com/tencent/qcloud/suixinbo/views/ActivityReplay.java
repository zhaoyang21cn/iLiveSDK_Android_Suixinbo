package com.tencent.qcloud.suixinbo.views;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.model.CurLiveInfo;
import com.tencent.qcloud.suixinbo.views.customviews.BaseActivity;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * Created by xkazerzhang on 2016/12/23.
 */
public class ActivityReplay extends BaseActivity implements ITXLivePlayListener {
    private final static String TAG = "ActivityReplay";
    private TXCloudVideoView txvvPlayerView;
    private TextView tvLog, tvCur, tvTotal;
    private SeekBar sbProgress;
    private String mStrLog = "";

    private TXLivePlayer mTxlpPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay);

        initView();

        if (null == CurLiveInfo.getRecordInfo()){
            finish();
        }

        mTxlpPlayer = new TXLivePlayer(this);

        mTxlpPlayer.setPlayerView(txvvPlayerView);
        mTxlpPlayer.setConfig(new TXLivePlayConfig());
        mTxlpPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTxlpPlayer.setPlayListener(this);
        mTxlpPlayer.startPlay(CurLiveInfo.getRecordInfo().getPlayUrl(), TXLivePlayer.PLAY_TYPE_VOD_MP4);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTxlpPlayer.stopPlay(false);
        txvvPlayerView.onDestroy();
    }

    @Override
    public void onPlayEvent(int event, Bundle param) {
        if (TXLiveConstants.PLAY_EVT_PLAY_PROGRESS == event){       // 忽略process事件
            int progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS);
            int duration = param.getInt(TXLiveConstants.EVT_PLAY_DURATION);
            tvCur.setText(String.format("%02d:%02d",progress/60,progress%60));
            tvTotal.setText(String.format("%02d:%02d",duration/60,duration%60));
            sbProgress.setMax(duration);
            sbProgress.setProgress(progress);
            return;
        }

        Log.v(TAG, "onPlayEvent->event: "+event+"|"+param.getString(TXLiveConstants.EVT_DESCRIPTION));
        addLog("event:"+event+"|"+param.getString(TXLiveConstants.EVT_DESCRIPTION));
        //错误还是要明确的报一下
        if (event < 0) {
            Toast.makeText(getApplicationContext(), param.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
            tvLog.setVisibility(View.VISIBLE);
        }

        if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {
            finish();
        }else if (TXLiveConstants.PLAY_EVT_PLAY_BEGIN == event){
            tvLog.setVisibility(View.GONE);
        }else if (TXLiveConstants.PLAY_EVT_PLAY_LOADING == event || TXLiveConstants.PLAY_EVT_PLAY_END == event){
            tvLog.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    private void addLog(String strInfo){
        mStrLog += strInfo + "\r\n";
        tvLog.setText(mStrLog);
    }

    private void initView(){
        txvvPlayerView = (TXCloudVideoView)findViewById(R.id.txvv_play_view);
        tvLog = (TextView)findViewById(R.id.tv_log);
        tvCur = (TextView)findViewById(R.id.tv_play_cur);
        tvTotal = (TextView)findViewById(R.id.tv_play_total);
        sbProgress = (SeekBar)findViewById(R.id.sb_progress);
        sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean bFromUser) {
                tvCur.setText(String.format("%02d:%02d",progress/60, progress%60));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if ( mTxlpPlayer != null) {
                    mTxlpPlayer.seek(seekBar.getProgress());
                }
            }
        });
    }
}
