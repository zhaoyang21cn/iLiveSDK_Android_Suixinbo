package com.tencent.ilivedemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tencent.tdemolive.LiveActivity;

import java.util.ArrayList;

/**
 * 示例菜单
 */
public class MainMenu extends Activity {
    private ListView lvMenu;

    private LinearLayout llLogin, llMain;
    private EditText etId;
    private TextView tvId;
    private ArrayAdapter adapterDemo;
    private ArrayList<String> listDemo = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mainmenu);
        llLogin = (LinearLayout) findViewById(R.id.ll_login);
        llMain = (LinearLayout) findViewById(R.id.ll_main);
        etId = (EditText) findViewById(R.id.et_id);
        tvId = (TextView) findViewById(R.id.tv_id);
        lvMenu = (ListView) findViewById(R.id.lv_menu);

        listDemo.add("Live: 简单直播");
        adapterDemo = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                listDemo);
        lvMenu.setAdapter(adapterDemo);
        lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();

                switch (position) {
                    case 0:
                        intent.setClass(MainMenu.this, LiveActivity.class);
                        break;
                }

                startActivity(intent);
            }
        });
    }

}
