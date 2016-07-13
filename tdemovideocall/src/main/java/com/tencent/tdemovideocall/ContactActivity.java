package com.tencent.tdemovideocall;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.tilvbsdk.TILVBCallBack;
import com.tencent.tilvbsdk.TILVBConstants;
import com.tencent.tilvbsdk.TILVBSDK;
import com.tencent.tilvbsdk.business.TILVBCallListener;
import com.tencent.tilvbsdk.business.TILVBCallManager;
import com.tencent.tilvbsdk.core.TILVBLoginManager;
import com.tencent.tilvbsdk.core.TILVBRoom;
import com.tencent.tilvbsdk.core.TILVBRoomConfig;

import java.util.ArrayList;

/**
 * 联系人界面
 */
public class ContactActivity extends Activity implements View.OnClickListener, TILVBCallListener {
    private static String TAG = "ContactActivity";
    private TextView tvMyAddr;
    private EditText etDstAddr, idInput;
    private ListView lvCallList;
    private Button confrim;
    ArrayList<String> callList = new ArrayList<String>();
    private ArrayAdapter adapterCallList;
    private LinearLayout callView,loginView;
    private AlertDialog mIncomingDlg;
    private int mCurIncomingId;

    // 内部方法
    private void initView() {
        tvMyAddr = (TextView) findViewById(R.id.tv_my_address);
        etDstAddr = (EditText) findViewById(R.id.et_dst_address);
        lvCallList = (ListView) findViewById(R.id.lv_call_list);
        idInput = (EditText) findViewById(R.id.id_input);
        confrim = (Button) findViewById(R.id.confirm);
        callView = (LinearLayout)findViewById(R.id.call_view);
        loginView = (LinearLayout)findViewById(R.id.login_view);
        confrim.setOnClickListener(this);
        adapterCallList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                callList);
        lvCallList.setAdapter(adapterCallList);
        lvCallList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String remoteId = (String) adapterCallList.getItem(position);
                makeCall(remoteId);
            }
        });

    }

    private void addCallList(String remoteId) {
        if (!callList.contains(remoteId)) {
            if (callList.add(remoteId)) {
                adapterCallList.notifyDataSetChanged();
            }
        }
    }

    private void onLogout() {
        // 注销成功清除用户信息，并跳转到登陆界面
        //finish();
        callView.setVisibility(View.INVISIBLE);
        loginView.setVisibility(View.VISIBLE);
    }

    /**
     * 发起呼叫
     *
     * @param remoteId
     */
    private void makeCall(String remoteId) {
        int callId = TILVBCallManager.getInstance().makeCall(remoteId, TILVBConstants.CALL_TYPE_VIDEO);
        if (TILVBConstants.INVALID_INTETER_VALUE != callId) {
            // 成功处理
            Intent intent = new Intent();
            intent.setClass(this, CallActivity.class);
            intent.putExtra("HostId", TILVBSDK.getInstance().getMyUserId());
            intent.putExtra("CallId", callId);
            startActivity(intent);
        }
    }

    private void logout() {
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

    // 覆盖方法
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_main);
        //TODO 初始化随心播
        TILVBSDK.getInstance().initSdk(getApplicationContext(), 1104620500, 107);
        // 关闭IM群组
        TILVBRoom.getInstance().init(new TILVBRoomConfig().imSupport(false));

        initView();

        // 设置通话回调
        TILVBCallManager.getInstance().addCallListener(this);
    }

    @Override
    public void onBackPressed() {
        if (loginView.getVisibility() == View.GONE){
            logout();
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_logout){
            logout();
        }else if (v.getId() == R.id.btn_make_call){
            String remoteId = etDstAddr.getText().toString();
            if (!TextUtils.isEmpty(remoteId)) {
                addCallList(remoteId);
                makeCall(remoteId);
            } else {
                Toast.makeText(this, R.string.toast_phone_empty, Toast.LENGTH_SHORT).show();
            }
        }else if(v.getId() == R.id.confirm){
            if (idInput.getText().toString().equals("")) {
                return;
            } else {
                login(idInput.getText().toString());
            }
        }
    }


    /**
     * 回调接口 来电
     * @param callId  来电ID
     * @param callType 来电类型
     * @param fromUserId
     * @param strTips    提示消息
     */
    @Override
    public void onNewIncomingCall(int callId, int callType, final String fromUserId, String strTips) {
        if (null != mIncomingDlg){  // 关闭遗留来电对话框
            mIncomingDlg.dismiss();
        }
        mCurIncomingId = callId;
        mIncomingDlg = new AlertDialog.Builder(this)
                .setTitle("New Call From "+fromUserId)
                .setMessage(strTips)
                .setPositiveButton("Accept", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TILVBCallManager.getInstance().acceptCall(mCurIncomingId);
                        Intent intent = new Intent();
                        intent.setClass(ContactActivity.this, CallActivity.class);
                        intent.putExtra("HostId", fromUserId);
                        intent.putExtra("CallId", mCurIncomingId);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TILVBCallManager.getInstance().rejectCall(mCurIncomingId);
                    }
                })
                .create();
        mIncomingDlg.show();
        addCallList(fromUserId);
    }

    @Override
    public void onCallEstablish(int callId) {

    }

    @Override
    public void onCallEnd(int callId, int endResult, String endInfo) {
        if (mCurIncomingId == callId){
            mIncomingDlg.dismiss();
        }
    }

    /**
     * 调用SDK登陆
     */
    private void login(final String id) {
        TILVBLoginManager.getInstance().tilvbLogin(id, "123456", new TILVBCallBack() {
            @Override
            public void onSuccess(Object data) {
                TILVBSDK.getInstance().setMyUserId(id);
                tvMyAddr.setText(TILVBSDK.getInstance().getMyUserId());
                callView.setVisibility(View.VISIBLE);
                loginView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(int errCode, String errMsg) {

            }
        });
    }
}
