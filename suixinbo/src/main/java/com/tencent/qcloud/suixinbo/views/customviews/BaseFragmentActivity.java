package com.tencent.qcloud.suixinbo.views.customviews;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.tencent.qcloud.suixinbo.utils.ConnectionChangeReceiver;
import com.tencent.qcloud.suixinbo.utils.Constants;

/**
 * Created by xkazerzhang on 2016/5/23.
 */
public class BaseFragmentActivity extends FragmentActivity {
    private BroadcastReceiver recv;
    private ConnectionChangeReceiver netWorkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recv = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.BD_EXIT_APP)){
                    finish();
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
}
