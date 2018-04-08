package com.tencent.qcloud.suixinbo.views.customviews;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.qcloud.suixinbo.R;
import com.tencent.qcloud.suixinbo.model.MySelfInfo;
import com.tencent.qcloud.suixinbo.utils.ConnectionChangeReceiver;
import com.tencent.qcloud.suixinbo.utils.Constants;
import com.tencent.qcloud.suixinbo.utils.LogConstants;
import com.tencent.qcloud.suixinbo.utils.SxbLog;

/**
 * Created by xkazerzhang on 2016/5/23.
 */
public class BaseFragmentActivity extends FragmentActivity {
    private String TAG = "BaseFragmentActivity";
    private BroadcastReceiver recv;
    private ConnectionChangeReceiver netWorkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ILiveLoginManager.getInstance().setUserStatusListener(new ILiveLoginManager.TILVBStatusListener() {
            @Override
            public void onForceOffline(int error, String message) {
                switch (error){
                    case ILiveConstants.ERR_KICK_OUT:
                        SxbLog.w(TAG, "onForceOffline->entered!");
                        SxbLog.d(TAG, LogConstants.ACTION_HOST_KICK + LogConstants.DIV + MySelfInfo.getInstance().getId() + LogConstants.DIV + "on force off line");
                        processOffline(getString(R.string.str_offline_msg));
                        break;
                    case ILiveConstants.ERR_EXPIRE:
                        SxbLog.w(TAG, "onUserSigExpired->entered!");
                        processOffline("onUserSigExpired|"+message);
                        break;
                }
            }
        });

        recv = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.BD_EXIT_APP)){
                    onRequireLogin();
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BD_EXIT_APP);

        registerReceiver(recv, filter);

        //监听网络变化
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new ConnectionChangeReceiver();
        }
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(netWorkStateReceiver, filter2);
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(recv);
            unregisterReceiver(netWorkStateReceiver);
        }catch (Exception e){
        }
        super.onDestroy();
    }

    private void processOffline(String message){
        if (isDestroyed() || isFinishing()) {
            return;
        }
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.str_tips_title)
                .setMessage(message)
                .setPositiveButton(R.string.btn_sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create();
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                requiredLogin();
            }
        });
        alertDialog.show();
    }

    public void requiredLogin(){
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putBoolean("living", false);
        editor.apply();
        MySelfInfo.getInstance().clearCache(getBaseContext());
        getBaseContext().sendBroadcast(new Intent(Constants.BD_EXIT_APP));
    }

    protected void onRequireLogin(){
        finish();
    }
}
