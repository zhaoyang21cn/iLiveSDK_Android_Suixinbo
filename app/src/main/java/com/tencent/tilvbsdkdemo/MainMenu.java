package com.tencent.tilvbsdkdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.tencent.tdemofm.channelActivity;
import com.tencent.tdemovideocall.ContactActivity;

import java.util.ArrayList;

/**
 * 示例菜单
 */
public class MainMenu extends Activity {
    private ListView lvMenu;

    private ArrayAdapter adapterDemo;
    private ArrayList<String> listDemo = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mainmenu);

        lvMenu = (ListView)findViewById(R.id.lv_menu);

        listDemo.add("VideoCall: 双人视频");
        listDemo.add("FM: 广播电台");
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
                }

                startActivity(intent);
            }
        });
    }


}
