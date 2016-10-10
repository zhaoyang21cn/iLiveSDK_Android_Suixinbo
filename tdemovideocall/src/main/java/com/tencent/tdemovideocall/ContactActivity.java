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

import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.business.callbusiness.ILVCallConfig;
import com.tencent.ilivesdk.business.callbusiness.ILVCallConstants;
import com.tencent.ilivesdk.business.callbusiness.ILVCallListener;
import com.tencent.ilivesdk.business.callbusiness.ILVCallManager;
import com.tencent.ilivesdk.business.callbusiness.ILVCallOption;
import com.tencent.ilivesdk.business.callbusiness.ILVIncomingListener;
import com.tencent.ilivesdk.core.ILiveLoginManager;


import java.util.ArrayList;

/**
 * 联系人界面
 */
public class ContactActivity extends Activity implements View.OnClickListener, ILVIncomingListener, ILVCallListener {
    private static String TAG = "ContactActivity";
    private TextView tvMyAddr;
    private EditText etDstAddr, idInput, pwdInput;
    private ListView lvCallList;
    private Button confrim, regist;
    ArrayList<String> callList = new ArrayList<String>();
    private ArrayAdapter adapterCallList;
    private LinearLayout callView,loginView;
    private AlertDialog mIncomingDlg;
    private int mCurIncomingId;

    private boolean bLogin; // 记录登录状态

    // 内部方法
    private void initView() {
        tvMyAddr = (TextView) findViewById(R.id.tv_my_address);
        etDstAddr = (EditText) findViewById(R.id.et_dst_address);
        lvCallList = (ListView) findViewById(R.id.lv_call_list);
        idInput = (EditText) findViewById(R.id.id_account);
        pwdInput = (EditText)findViewById(R.id.id_password);
        confrim = (Button) findViewById(R.id.confirm);
        regist = (Button)findViewById(R.id.regist);
        callView = (LinearLayout)findViewById(R.id.call_view);
        loginView = (LinearLayout)findViewById(R.id.login_view);
        confrim.setOnClickListener(this);
        regist.setOnClickListener(this);
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
        ILVCallOption option = new ILVCallOption(ILiveSDK.getInstance().getMyUserId())
            .setCallType(ILVCallConstants.CALL_TYPE_VIDEO);
        int callId = ILVCallManager.getInstance().makeCall(remoteId, option);
        if (ILiveConstants.INVALID_INTETER_VALUE != callId) {
            // 成功处理
            Intent intent = new Intent();
            intent.setClass(this, CallActivity.class);
            intent.putExtra("HostId", ILiveSDK.getInstance().getMyUserId());
            intent.putExtra("CallId", callId);
            startActivity(intent);
        }
    }

    private void logout() {
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

    // 覆盖方法
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_main);
        //TODO 初始化随心播
        ILiveSDK.getInstance().initSdk(getApplicationContext(), 1400001533, 792);
        // 关闭IM群组
        ILVCallManager.getInstance().init(new ILVCallConfig());

        initView();

        // 设置通话回调
        ILVCallManager.getInstance().addIncomingListener(this);
        ILVCallManager.getInstance().addCallListener(this);
    }

    @Override
    protected void onDestroy() {
        if (bLogin){
            ILiveLoginManager.getInstance().tilvbLogout(null);
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (R.id.btn_logout == v.getId()){
            logout();
        }else if (R.id.btn_make_call == v.getId()){
            String remoteId = etDstAddr.getText().toString();
            if (!TextUtils.isEmpty(remoteId)) {
                addCallList(remoteId);
                makeCall(remoteId);
            } else {
                Toast.makeText(this, R.string.toast_phone_empty, Toast.LENGTH_SHORT).show();
            }
        }else if (R.id.regist == v.getId()){
            if (TextUtils.isEmpty(idInput.getText().toString()) || TextUtils.isEmpty(pwdInput.getText().toString())) {
                return;
            } else {
                regist(idInput.getText().toString(), pwdInput.getText().toString());
            }
        }else if (R.id.confirm == v.getId()){
            if (TextUtils.isEmpty(idInput.getText().toString()) || TextUtils.isEmpty(pwdInput.getText().toString())) {
                return;
            } else {
                login(idInput.getText().toString(), pwdInput.getText().toString());
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
    public void onNewIncomingCall(int callId, final int callType, final String fromUserId, String strTips) {
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
                        ILVCallManager.getInstance().acceptCall(mCurIncomingId, new ILVCallOption(fromUserId).setCallType(callType));
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
                        ILVCallManager.getInstance().rejectCall(mCurIncomingId);
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
    private void login(final String id, String password) {
        ILiveLoginManager.getInstance().tlsLogin(id, password, new ILiveCallBack<String>() {
            @Override
            public void onSuccess(String userSig) {
                ILiveLoginManager.getInstance().tilvbLogin(id, userSig, new ILiveCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        bLogin = true;
                        ILiveSDK.getInstance().setMyUserId(id);
                        tvMyAddr.setText(ILiveSDK.getInstance().getMyUserId());
                        callView.setVisibility(View.VISIBLE);
                        loginView.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {

                    }
                });
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Toast.makeText(getApplicationContext(), "login failed:"+module+"|"+errCode+"|"+errMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void regist(String account, String password){
        ILiveLoginManager.getInstance().tlsRegister(account, password, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                Toast.makeText(getApplicationContext(), "Regist success!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Toast.makeText(getApplicationContext(), "Regist failed:"+module+"|"+errCode+"|"+errMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onException(int iExceptionId, int errCode, String errMsg) {

    }
}
