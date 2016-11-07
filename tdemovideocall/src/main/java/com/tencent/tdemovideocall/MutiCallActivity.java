package com.tencent.tdemovideocall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.business.callbusiness.ILVBCallMemberListener;
import com.tencent.ilivesdk.business.callbusiness.ILVCallConstants;
import com.tencent.ilivesdk.business.callbusiness.ILVCallListener;
import com.tencent.ilivesdk.business.callbusiness.ILVCallManager;
import com.tencent.ilivesdk.business.callbusiness.ILVCallOption;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.view.AVRootView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tencent on 2016/11/1.
 */
public class MutiCallActivity extends Activity implements ILVBCallMemberListener, ILVCallListener, View.OnClickListener {
    private AVRootView avRootView;
    private List<String> videos = new ArrayList<>();
    private int callId = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_muti_call);
        initView();

        Intent intent = getIntent();
        String hostId = intent.getStringExtra("HostId");
        int callType = intent.getIntExtra("CallType", ILVCallConstants.CALL_TYPE_VIDEO);
        callId = intent.getIntExtra("CallId", 0);

        if (ILiveLoginManager.getInstance().getMyUserId().equals(hostId)){  // 主叫
            List<String> numbers = intent.getStringArrayListExtra("CallNumbers");
            callId = ILVCallManager.getInstance().makeMutiCall(numbers, new ILVCallOption(ILiveLoginManager.getInstance().getMyUserId())
                .setCallType(callType)
                .setMemberListener(this));
        }else{
            if (0 != ILVCallManager.getInstance().acceptCall(callId, new ILVCallOption(hostId)
                    .setCallType(callType)
                    .setMemberListener(this))){
                // 接听失败
                finish();
            }
        }
        if (ILiveConstants.INVALID_INTETER_VALUE == callId){
            // 呼叫失败
            finish();
        }
        ILVCallManager.getInstance().addCallListener(this);
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
    public void onClick(View v) {
        if (R.id.btn_endcall == v.getId()){
            ILVCallManager.getInstance().endCall(callId);
        }
    }

    @Override
    public void onCameraEvent(String id, boolean bEnable) {
        Log.v("ILVB-DBG", "onCameraEvent->id:"+id+"/"+bEnable);
        if (bEnable){
            if (!videos.contains(id))
                videos.add(id);
        }else{
            videos.remove(id);
        }
        resetLayout(videos.size());
    }

    @Override
    public void onMicEvent(String id, boolean bEnable) {

    }

    @Override
    public void onCallEstablish(int callId) {
        resetLayout(videos.size());
    }

    @Override
    public void onCallEnd(int callId, int endResult, String endInfo) {
        finish();
    }

    @Override
    public void onException(int iExceptionId, int errCode, String errMsg) {

    }

    private void initView(){
        avRootView = (AVRootView)findViewById(R.id.av_root_view);
    }

    private void resetLayout(int count){
        switch (count){
        case 0:
        case 1:
            // 全屏
            avRootView.getViewByIndex(0).setPosTop(0);
            avRootView.getViewByIndex(0).setPosLeft(0);
            avRootView.getViewByIndex(0).setPosWidth(avRootView.getWidth());
            avRootView.getViewByIndex(0).setPosHeight(avRootView.getHeight());
            avRootView.getViewByIndex(0).autoLayout();
            break;
        case 2:
            // 左右半屏
            avRootView.getViewByIndex(0).setPosTop(0);
            avRootView.getViewByIndex(0).setPosLeft(0);
            avRootView.getViewByIndex(0).setPosWidth(avRootView.getWidth());
            avRootView.getViewByIndex(0).setPosHeight(avRootView.getHeight()/2);
            avRootView.getViewByIndex(0).autoLayout();

            avRootView.getViewByIndex(1).setPosTop(avRootView.getHeight()/2);
            avRootView.getViewByIndex(1).setPosLeft(0);
            avRootView.getViewByIndex(1).setPosWidth(avRootView.getWidth());
            avRootView.getViewByIndex(1).setPosHeight(avRootView.getHeight()/2);
            avRootView.getViewByIndex(1).autoLayout();
            break;
        case 3:
            // 三角布局
            avRootView.getViewByIndex(0).setPosTop(0);
            avRootView.getViewByIndex(0).setPosLeft(avRootView.getWidth()/2);
            avRootView.getViewByIndex(0).setPosWidth(avRootView.getWidth()/2);
            avRootView.getViewByIndex(0).setPosHeight(avRootView.getHeight()/2);
            avRootView.getViewByIndex(0).autoLayout();

            avRootView.getViewByIndex(1).setPosTop(avRootView.getHeight()/2);
            avRootView.getViewByIndex(1).setPosLeft(avRootView.getWidth()/2);
            avRootView.getViewByIndex(1).setPosWidth(avRootView.getWidth()/2);
            avRootView.getViewByIndex(1).setPosHeight(avRootView.getHeight()/2);
            avRootView.getViewByIndex(1).autoLayout();

            avRootView.getViewByIndex(2).setPosTop(avRootView.getHeight()/4);
            avRootView.getViewByIndex(2).setPosLeft(0);
            avRootView.getViewByIndex(2).setPosWidth(avRootView.getWidth()/2);
            avRootView.getViewByIndex(2).setPosHeight(avRootView.getHeight()/2);
            avRootView.getViewByIndex(2).autoLayout();
            break;
        case 4:
            avRootView.getViewByIndex(0).setPosTop(0);
            avRootView.getViewByIndex(0).setPosLeft(avRootView.getWidth()/2);
            avRootView.getViewByIndex(0).setPosWidth(avRootView.getWidth()/2);
            avRootView.getViewByIndex(0).setPosHeight(avRootView.getHeight()/2);
            avRootView.getViewByIndex(0).autoLayout();

            avRootView.getViewByIndex(1).setPosTop(avRootView.getHeight()/2);
            avRootView.getViewByIndex(1).setPosLeft(avRootView.getWidth()/2);
            avRootView.getViewByIndex(1).setPosWidth(avRootView.getWidth()/2);
            avRootView.getViewByIndex(1).setPosHeight(avRootView.getHeight()/2);
            avRootView.getViewByIndex(1).autoLayout();

            avRootView.getViewByIndex(2).setPosTop(0);
            avRootView.getViewByIndex(2).setPosLeft(0);
            avRootView.getViewByIndex(2).setPosWidth(avRootView.getWidth()/2);
            avRootView.getViewByIndex(2).setPosHeight(avRootView.getHeight()/2);
            avRootView.getViewByIndex(2).autoLayout();

            avRootView.getViewByIndex(3).setPosTop(avRootView.getHeight()/2);
            avRootView.getViewByIndex(3).setPosLeft(0);
            avRootView.getViewByIndex(3).setPosWidth(avRootView.getWidth()/2);
            avRootView.getViewByIndex(3).setPosHeight(avRootView.getHeight()/2);
            avRootView.getViewByIndex(3).autoLayout();
            break;
        }
    }
}
