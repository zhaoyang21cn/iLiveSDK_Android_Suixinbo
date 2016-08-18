package com.tencent.tdemolive;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tencent.tilvbsdk.TILVBCallBack;
import com.tencent.tilvbsdk.TILVBConstants;
import com.tencent.tilvbsdk.core.TILVBRoomManager;
import com.tencent.tilvbsdk.core.TILVBRoomOption;
import com.tencent.tilvbsdk.view.AVRootView;

/**
 * Created by xkazerzhang on 2016/8/18.
 */

public class LiveActivity extends Activity implements View.OnClickListener{
    private Button btnRoom, btnCamera, btnMic;
    private EditText etRoomNum;
    private AVRootView avRootView;


    private boolean bInRoom = false;
    private boolean bCameraEnable = true;
    private boolean bMicEnable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_live);
        initView();
    }

    @Override
    public void onBackPressed() {
        if (bInRoom){
            quitRoom();
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        TILVBRoomManager.getInstance().onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        TILVBRoomManager.getInstance().onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        TILVBRoomManager.getInstance().onDestory();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_room){
            if (bInRoom){
                quitRoom();
            }else{
                joinRoom();
            }
        }else if (v.getId() == R.id.btn_camera){
            if (bCameraEnable){
                TILVBRoomManager.getInstance().enableCamera(TILVBRoomManager.getInstance().getCurCameraId(), false);
            }else{
                TILVBRoomManager.getInstance().enableCamera(TILVBConstants.FRONT_CAMERA, true);
            }
            bCameraEnable = !bCameraEnable;
            btnCamera.setText(bCameraEnable ? R.string.btn_camera_close : R.string.btn_camera_open);
        }else if (v.getId() == R.id.btn_mic){
            if (bMicEnable){
                TILVBRoomManager.getInstance().enableMic(false);
            }else{
                TILVBRoomManager.getInstance().enableMic(false);
            }
            bMicEnable = !bMicEnable;
            btnMic.setText(bMicEnable ? R.string.btn_mic_close : R.string.btn_mic_open);
        }

    }

    private void initView(){
        etRoomNum = (EditText)findViewById(R.id.et_roomnum);
        btnRoom = (Button)findViewById(R.id.btn_room);
        btnCamera = (Button)findViewById(R.id.btn_camera);
        btnMic = (Button)findViewById(R.id.btn_mic);

        avRootView = (AVRootView)findViewById(R.id.avrv_video);

        TILVBRoomManager.getInstance().initAvRootView(avRootView);
    }

    private void joinRoom(){
        TILVBRoomManager.getInstance().joinRoom(Integer.valueOf(etRoomNum.getText().toString()),
                new TILVBRoomOption(""),
                new TILVBCallBack() {
            @Override
            public void onSuccess(Object data) {
                bInRoom = true;
                btnRoom.setText(R.string.btn_exit);
                etRoomNum.setEnabled(false);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });
    }

    private void quitRoom(){
        TILVBRoomManager.getInstance().quitRoom(new TILVBCallBack() {
            @Override
            public void onSuccess(Object data) {
                bInRoom = false;
                btnRoom.setText(R.string.btn_join);
                etRoomNum.setEnabled(true);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                bInRoom = false;
                btnRoom.setText(R.string.btn_join);
                etRoomNum.setEnabled(true);
            }
        });
    }
}
