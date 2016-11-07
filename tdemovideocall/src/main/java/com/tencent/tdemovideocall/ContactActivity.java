package com.tencent.tdemovideocall;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import com.tencent.ilivesdk.ILiveFunc;
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
    private LinearLayout callView,loginView, llDstNums;
    private AlertDialog mIncomingDlg;
    private int mCurIncomingId;

    private boolean bTLSAccount = true;

    // 多人视频控件列表
    private ArrayList<EditText> mEtNums = new ArrayList<>();

    private AccountMgr mAccountMgr = new AccountMgr();

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
        llDstNums = (LinearLayout)findViewById(R.id.ll_dst_numbers);
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
        bLogin = false;
        callView.setVisibility(View.INVISIBLE);
        loginView.setVisibility(View.VISIBLE);
    }

    /**
     * 发起多人呼叫
     * @param remoteId 远程主id
     */
    private boolean makeMutiCall(String remoteId){
        ArrayList<String> nums = new ArrayList<>();
        String tmpNum;
        String calllist = "";
        for (EditText etNum : mEtNums){
            tmpNum = etNum.getText().toString();
            if (!TextUtils.isEmpty(tmpNum)){
                nums.add(tmpNum);
                calllist = calllist + tmpNum + ",";
            }
        }
        if (!nums.isEmpty()){
            if (null != remoteId){
                nums.add(remoteId);
                calllist = calllist + remoteId + ",";
            }

            // 添加通话记录
            addCallList(calllist);
            // 多人在界面中发起呼叫
            Intent intent = new Intent();
            intent.setClass(this, MutiCallActivity.class);
            intent.putExtra("HostId", ILiveLoginManager.getInstance().getMyUserId());
            intent.putExtra("CallType", ILVCallConstants.CALL_TYPE_VIDEO);
            intent.putStringArrayListExtra("CallNumbers", nums);
            startActivity(intent);
            return true;
        }

        return false;
    }

    /**
     * 发起呼叫
     *
     * @param remoteId
     */
    private void makeCall(String remoteId) {
        ILVCallOption option = new ILVCallOption(ILiveLoginManager.getInstance().getMyUserId())
            .setCallType(ILVCallConstants.CALL_TYPE_VIDEO);
        int callId = ILVCallManager.getInstance().makeCall(remoteId, option);
        if (ILiveConstants.INVALID_INTETER_VALUE != callId) {
            // 成功处理
            Intent intent = new Intent();
            intent.setClass(this, CallActivity.class);
            intent.putExtra("HostId", ILiveLoginManager.getInstance().getMyUserId());
            intent.putExtra("CallId", callId);
            startActivity(intent);
        }
    }

    private void logout() {
        ILiveLoginManager.getInstance().iLiveLogout(new ILiveCallBack() {
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
        if (bTLSAccount){
            ILiveSDK.getInstance().initSdk(getApplicationContext(), 1400013552, 6926);
        }else {
            ILiveSDK.getInstance().initSdk(getApplicationContext(), 1400016949, 8002);
        }
        // 关闭IM群组
        ILVCallManager.getInstance().init(new ILVCallConfig());

        initView();

        // 设置通话回调
        ILVCallManager.getInstance().addIncomingListener(this);
        ILVCallManager.getInstance().addCallListener(this);
    }

    @Override
    public void onBackPressed() {
        if (bLogin){
            ILiveLoginManager.getInstance().iLiveLogout(new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    finish();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    finish();
                }
            });
        }else{
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        ILVCallManager.getInstance().removeIncomingListener(this);
        ILVCallManager.getInstance().removeCallListener(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (R.id.btn_logout == v.getId()){
            logout();
        }else if (R.id.btn_make_call == v.getId()){
            String remoteId = etDstAddr.getText().toString();
            if (makeMutiCall(remoteId)){
                Log.d(TAG, "make MutiCall enter");
            }else if (!TextUtils.isEmpty(remoteId)) {
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
        }else if (R.id.btn_add == v.getId()){
            addNewInputNumbers();
        }
    }

    /**
     * 使用userSig登录iLiveSDK(独立模式下获有userSig直接调用登录)
     */
    private void loginSDK(final String id, String userSig){
        ILiveLoginManager.getInstance().iLiveLogin(id, userSig, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                bLogin = true;
                tvMyAddr.setText(ILiveLoginManager.getInstance().getMyUserId());
                callView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Toast.makeText(ContactActivity.this, "Login failed:"+module+"|"+errCode+"|"+errMsg, Toast.LENGTH_SHORT).show();
                loginView.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 登录并获取userSig(*托管模式，独立模式下直接用userSig调用loginSDK登录)
     */
    private void login(final String id, String password) {
        loginView.setVisibility(View.INVISIBLE);

        if (bTLSAccount){
            ILiveLoginManager.getInstance().tlsLogin(id, password, new ILiveCallBack<String>() {
                @Override
                public void onSuccess(String data) {
                    loginSDK(id, data);
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(getApplicationContext(), "login failed:" + module+"|"+errCode+"|"+errMsg, Toast.LENGTH_SHORT).show();
                    loginView.setVisibility(View.VISIBLE);
                }
            });
        }else {
            mAccountMgr.login(id, password, new AccountMgr.RequestCallBack() {
                @Override
                public void onResult(int error, String response) {
                    if (0 == error) {
                        loginSDK(id, response);
                    } else {
                        Toast.makeText(getApplicationContext(), "login failed:" + response, Toast.LENGTH_SHORT).show();
                        loginView.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    /**
     *  注册用户名(*托管模式，独立模式下请向自己私有服务器注册)
     */
    private void regist(String account, String password){
        if (bTLSAccount){
            ILiveLoginManager.getInstance().tlsRegister(account, password, new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    Toast.makeText(getApplicationContext(), "regist success!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(getApplicationContext(), "regist failed:" + module+"|"+errCode+"|"+errMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            mAccountMgr.regist(account, password, new AccountMgr.RequestCallBack() {
                @Override
                public void onResult(int error, String response) {
                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 添加新的用户号码输入
     */
    private void addNewInputNumbers(){
        if (mEtNums.size() >= 3){
            return;
        }
        final LinearLayout linearLayout = new LinearLayout(this);
        final EditText etNum = new EditText(this);
        mEtNums.add(etNum);
        Button btnDel = new Button(this);
        btnDel.setText("-");
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtNums.remove(etNum);
                llDstNums.removeView(linearLayout);
            }
        });
        linearLayout.addView(btnDel, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(etNum, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        llDstNums.addView(linearLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 回调接口 来电
     * @param callId  来电ID
     * @param callType 来电类型
     * @param fromUserId
     * @param strTips    提示消息
     * @param strCustom 用户自定义字段
     */
    @Override

    public void onNewIncomingCall(int callId, final int callType, final String fromUserId, String strTips, String strCustom, long timeStamp) {
        if (null != mIncomingDlg){  // 关闭遗留来电对话框
            mIncomingDlg.dismiss();
        }
        long curSec = ILiveFunc.getCurrentSec();
        if (curSec < timeStamp || (curSec-timeStamp)>30){    //忽略超过30秒的消息
            Log.v(TAG, "onNewIncomingCall->ignore expired incoming:"+(curSec-timeStamp)+"s ago");
            return;
        }
        mCurIncomingId = callId;
        mIncomingDlg = new AlertDialog.Builder(this)
                .setTitle("New Call From "+fromUserId)
                .setMessage(strTips)
                .setPositiveButton("Accept", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (0 == ILVCallManager.getInstance().acceptCall(mCurIncomingId, new ILVCallOption(fromUserId).setCallType(callType))) {
                            Intent intent = new Intent();
                            intent.setClass(ContactActivity.this, CallActivity.class);
                            intent.putExtra("HostId", fromUserId);
                            intent.putExtra("CallId", mCurIncomingId);
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ILVCallManager.getInstance().rejectCall(mCurIncomingId);
                    }
                })
                .create();
        mIncomingDlg.setCanceledOnTouchOutside(false);
        mIncomingDlg.show();
        addCallList(fromUserId);
    }

    @Override
    public void onNewMutiIncomingCall(final int callId, final int callType, final String sponser, String strTips, String strCustom, long timeStamp) {
        if (null != mIncomingDlg){  // 关闭遗留来电对话框
            mIncomingDlg.dismiss();
        }
        long curSec = ILiveFunc.getCurrentSec();
        if (curSec < timeStamp || (curSec-timeStamp)>30){    //忽略超过30秒的消息
            Log.v(TAG, "onNewMutiIncomingCall->ignore expired incoming:"+(curSec-timeStamp)+"s ago");
            return;
        }
        mCurIncomingId = callId;
        mIncomingDlg = new AlertDialog.Builder(this)
                .setTitle("New Muti Call From "+sponser)
                .setMessage(strTips)
                .setPositiveButton("Accept", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setClass(ContactActivity.this, MutiCallActivity.class);
                        intent.putExtra("HostId", sponser);
                        intent.putExtra("CallId", callId);
                        intent.putExtra("CallType", callType);
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
        mIncomingDlg.setCanceledOnTouchOutside(false);
        mIncomingDlg.show();
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

    @Override
    public void onException(int iExceptionId, int errCode, String errMsg) {

    }
}
