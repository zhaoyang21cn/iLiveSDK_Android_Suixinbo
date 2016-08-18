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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.tilvbsdk.TILVBConstants;
import com.tencent.tilvbsdk.TILVBSDK;
import com.tencent.tilvbsdk.business.TILVBCallConfig;
import com.tencent.tilvbsdk.business.TILVBCallListener;
import com.tencent.tilvbsdk.business.TILVBCallManager;
import com.tencent.tilvbsdk.business.TILVBCallOption;
import com.tencent.tilvbsdk.core.TILVBRoomManager;

import java.util.ArrayList;

/**
 * 联系人界面
 */
public class ContactActivity extends Activity implements View.OnClickListener, TILVBCallListener {
    private static String TAG = "ContactActivity";
    private TextView tvMyAddr;
    private EditText etDstAddr;
    private ListView lvCallList;
    ArrayList<String> callList = new ArrayList<String>();
    private ArrayAdapter adapterCallList;
    private AlertDialog mIncomingDlg;
    private int mCurIncomingId;

    // 内部方法
    private void initView() {
        tvMyAddr = (TextView) findViewById(R.id.tv_my_address);
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

        tvMyAddr.setText(TILVBSDK.getInstance().getMyUserId());
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
        int callId = TILVBCallManager.getInstance().makeCall(remoteId, new TILVBCallOption(TILVBSDK.getInstance().getMyUserId(), TILVBConstants.CALL_TYPE_VIDEO));
        if (TILVBConstants.INVALID_INTETER_VALUE != callId) {
            // 成功处理
            Intent intent = new Intent();
            intent.setClass(this, CallActivity.class);
            intent.putExtra("HostId", TILVBSDK.getInstance().getMyUserId());
            intent.putExtra("CallId", callId);
            startActivity(intent);
        }
    }

    // 覆盖方法
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_main);

        TILVBCallManager.getInstance().init(this, new TILVBCallConfig());
        initView();

        // 设置通话回调
        TILVBCallManager.getInstance().addCallListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_make_call){
            String remoteId = etDstAddr.getText().toString();
            if (!TextUtils.isEmpty(remoteId)) {
                addCallList(remoteId);
                makeCall(remoteId);
            } else {
                Toast.makeText(this, R.string.toast_phone_empty, Toast.LENGTH_SHORT).show();
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
                        TILVBCallManager.getInstance().acceptCall(mCurIncomingId, new TILVBCallOption(fromUserId, callType));
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

    @Override
    public void onException(int i, int i1, String s) {

    }
}
