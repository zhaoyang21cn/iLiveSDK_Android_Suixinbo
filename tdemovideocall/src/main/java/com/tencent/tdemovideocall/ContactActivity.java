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
import android.widget.ListView;
import android.widget.Toast;

import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.business.callbusiness.ILVCallConfig;
import com.tencent.ilivesdk.business.callbusiness.ILVCallConstants;
import com.tencent.ilivesdk.business.callbusiness.ILVCallListener;
import com.tencent.ilivesdk.business.callbusiness.ILVCallManager;
import com.tencent.ilivesdk.business.callbusiness.ILVCallOption;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 联系人界面
 */
public class ContactActivity extends Activity implements View.OnClickListener, ILVCallListener {
    private static String TAG = "ContactActivity";
//    private TextView tvMyAddr;
    private EditText etDstAddr,inputId;
    private Button loginBtn;
    private ListView lvCallList;
    ArrayList<String> callList = new ArrayList<String>();
    private ArrayAdapter adapterCallList;
    private AlertDialog mIncomingDlg;
    private int mCurIncomingId;

    // 内部方法
    private void initView() {
//        tvMyAddr = (TextView) findViewById(R.id.tv_my_address);
        etDstAddr = (EditText) findViewById(R.id.et_dst_address);
        lvCallList = (ListView) findViewById(R.id.lv_call_list);
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
        inputId =  (EditText) findViewById(R.id.id_input);
        loginBtn =  (Button) findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(this);
//        tvMyAddr.setText(ILiveSDK.getInstance().getMyUserId());
    }

    private void addCallList(String remoteId) {
        if (!callList.contains(remoteId)) {
            if (callList.add(remoteId)) {
                adapterCallList.notifyDataSetChanged();
            }
        }
    }

    /**
     * 发起呼叫
     *
     * @param remoteId
     */
    private void makeCall(String remoteId) {
        List<String> ids = new ArrayList<String>();
        ids.add(remoteId);
        int callId = ILVCallManager.getInstance().makeCall(ids, new ILVCallOption(ILiveSDK.getInstance().getMyUserId(), ILVCallConstants.CALL_TYPE_VIDEO));
        if (ILiveConstants.INVALID_INTETER_VALUE != callId) {
            // 成功处理
            Intent intent = new Intent();
            intent.setClass(this, CallActivity.class);
            intent.putExtra("HostId", ILiveSDK.getInstance().getMyUserId());
            intent.putExtra("CallId", callId);
            startActivity(intent);
        }
    }

    // 覆盖方法
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_main);
        ILiveSDK.getInstance().initSdk(getApplicationContext(), 1104620500, 107);
        ILVCallManager.getInstance().init(new ILVCallConfig());
        initView();

        // 设置通话回调
        ILVCallManager.getInstance().addCallListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_make_call) {
            String remoteId = etDstAddr.getText().toString();
            if (!TextUtils.isEmpty(remoteId)) {
                addCallList(remoteId);
                makeCall(remoteId);
            } else {
                Toast.makeText(this, R.string.toast_phone_empty, Toast.LENGTH_SHORT).show();
            }
        }
        if (v.getId() == R.id.btn_login) {
            ILiveSDK.getInstance().setMyUserId("" + inputId.getText());
            ILiveLoginManager.getInstance().tilvbLogin(ILiveSDK.getInstance().getMyUserId(), "123456", new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    Toast.makeText(ContactActivity.this, "login success !", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(ContactActivity.this, module + "|login fail " + errCode + " " + errMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    /**
     * 回调接口 来电
     *
     * @param callId     来电ID
     * @param callType   来电类型
     * @param fromUserId
     * @param strTips    提示消息
     */
    @Override
    public void onNewIncomingCall(int callId, final int callType, final String fromUserId, String strTips) {
        if (null != mIncomingDlg) {  // 关闭遗留来电对话框
            mIncomingDlg.dismiss();
        }
        mCurIncomingId = callId;
        mIncomingDlg = new AlertDialog.Builder(this)
                .setTitle("New Call From " + fromUserId)
                .setMessage(strTips)
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ILVCallManager.getInstance().acceptCall(mCurIncomingId, new ILVCallOption(fromUserId,ILVCallConstants.CALL_TYPE_VIDEO));
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
        if (mCurIncomingId == callId) {
            mIncomingDlg.dismiss();
        }
    }

    @Override
    public void onException(int i, int i1, String s) {

    }

    @Override
    public void onMembersUpdate() {

    }
}
