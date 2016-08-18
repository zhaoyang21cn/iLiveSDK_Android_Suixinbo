package com.tencent.tilvbsdkdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tencent.tdemofm.channelActivity;
import com.tencent.tdemolive.LiveActivity;
import com.tencent.tdemovideocall.ContactActivity;
import com.tencent.tilvbsdk.TILVBCallBack;
import com.tencent.tilvbsdk.TILVBSDK;
import com.tencent.tilvbsdk.core.TILVBLoginManager;

import java.util.ArrayList;

/**
 * 示例菜单
 */
public class MainMenu extends Activity implements View.OnClickListener{
    private ListView lvMenu;

    private LinearLayout llLogin, llMain;
    private EditText etId;
    private TextView tvId;
    private ArrayAdapter adapterDemo;
    private ArrayList<String> listDemo = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TILVBSDK.getInstance().initSdk(getApplicationContext(), 1104620500, 107);

        setContentView(R.layout.activity_mainmenu);

        llLogin = (LinearLayout)findViewById(R.id.ll_login);
        llMain = (LinearLayout)findViewById(R.id.ll_main);
        etId = (EditText)findViewById(R.id.et_id);
        tvId = (TextView)findViewById(R.id.tv_id);
        lvMenu = (ListView)findViewById(R.id.lv_menu);

        listDemo.add("VideoCall: 双人视频");
        listDemo.add("FM: 广播电台");
        listDemo.add("Live: 直播");
        adapterDemo = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                listDemo);
        lvMenu.setAdapter(adapterDemo);
        lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();

                switch (position){
                case 0:
                    intent.setClass(MainMenu.this, ContactActivity.class);
                    break;
                case 1:
                    intent.setClass(MainMenu.this, channelActivity.class);
                    break;
                case 2:
                    intent.setClass(MainMenu.this, LiveActivity.class);
                    break;
                }

                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        logout();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        case R.id.btn_login:
            if (!TextUtils.isEmpty(etId.getText().toString())){
                login(etId.getText().toString());
            }
            break;
        case R.id.btn_logout:
            logout();
            break;
        }
    }

    /**
     *  SDK登陆
     */
    private void login(String id){
        TILVBLoginManager.getInstance().tilvbLogin(id, "123", new TILVBCallBack() {
            @Override
            public void onSuccess(Object o) {
                llMain.setVisibility(View.VISIBLE);
                llLogin.setVisibility(View.GONE);
                tvId.setText(TILVBSDK.getInstance().getMyUserId());
            }

            @Override
            public void onError(String s, int i, String s1) {

            }
        });
    }

    private void onLogout(){
        llMain.setVisibility(View.GONE);
        llLogin.setVisibility(View.VISIBLE);
    }

    private void logout(){
        TILVBLoginManager.getInstance().tilvbLogout(new TILVBCallBack() {
            @Override
            public void onSuccess(Object data) {
                onLogout();
            }

            @Override
            public void onError(String s, int i, String s1) {
                onLogout();
            }
        });
    }
}
